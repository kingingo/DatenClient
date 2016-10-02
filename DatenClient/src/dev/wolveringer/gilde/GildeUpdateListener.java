package dev.wolveringer.gilde;

import java.util.ArrayList;

import dev.wolveringer.client.debug.Debugger;
import dev.wolveringer.event.EventListener;
import dev.wolveringer.events.Event;
import dev.wolveringer.events.gilde.GildeMoneyChangeEvent;
import dev.wolveringer.events.gilde.GildePermissionEvent;
import dev.wolveringer.events.gilde.GildePlayerEvent;
import dev.wolveringer.events.gilde.GildePermissionEvent.Action;
import dev.wolveringer.events.gilde.GildePropertiesUpdate;
import dev.wolveringer.events.gilde.GildePropertiesUpdate.Property;
import dev.wolveringer.gilde.GildeType;
import lombok.RequiredArgsConstructor;
import static dev.wolveringer.gilde.GildeVariables.INVITE_GROUP;

@RequiredArgsConstructor
public class GildeUpdateListener implements EventListener {
	private final GildManager manager;

	@Override
	public void fireEvent(Event e) {
		if (e instanceof GildePropertiesUpdate) {
			Gilde g = manager.getGilde(((GildePropertiesUpdate) e).getGilde());
			if (g == null)
				return;
			if (((GildePropertiesUpdate) e).getGildenType() == GildeType.ALL) {
				if (((GildePropertiesUpdate) e).getProperty() == Property.NAME || ((GildePropertiesUpdate) e).getProperty() == Property.SHORT_NAME)
					g.reloadNameAndShortname();
				if (((GildePropertiesUpdate) e).getProperty() == Property.DELETE){
					manager.deleteGilde(g, false);
				}
			}
			if (((GildePropertiesUpdate) e).getProperty() == Property.COSTUM_DATA) {
				if (g.getSelection(((GildePropertiesUpdate) e).getGildenType()).isActive())
					g.getSelection(((GildePropertiesUpdate) e).getGildenType()).reloadDataSync();
			}
			if (((GildePropertiesUpdate) e).getProperty() == Property.ACTIVE_GILD_SECTION) {
				boolean old = g.getSelection(((GildePropertiesUpdate) e).getGildenType()).isActive();
				Debugger.debug("Updating section status for gilde "+g.getUuid()+"["+((GildePropertiesUpdate) e).getGildenType()+"] from "+old+" to "+true);
				if (!old){
					g.getSelection(((GildePropertiesUpdate) e).getGildenType()).active = true;
					g.getSelection(((GildePropertiesUpdate) e).getGildenType()).getMoney().init();
				}
			}
			if (((GildePropertiesUpdate) e).getProperty() == Property.DEACTIVE_GILD_SECTION) {
				boolean old = g.getSelection(((GildePropertiesUpdate) e).getGildenType()).isActive();
				Debugger.debug("Updating section status for gilde "+g.getUuid()+"["+((GildePropertiesUpdate) e).getGildenType()+"] from "+old+" to "+false);
				if (old)
					g.getSelection(((GildePropertiesUpdate) e).getGildenType()).active = false;
			}
		} 
		else if(e instanceof GildeMoneyChangeEvent){
			Gilde g = manager.getGilde(((GildeMoneyChangeEvent) e).getGilde());
			if (g == null)
				return;
			if(!g.getSelection(((GildeMoneyChangeEvent) e).getGildenType()).isActive())
				return;
			GildSectionMoney money = g.getSelection(((GildeMoneyChangeEvent) e).getGildenType()).getMoney();
			switch (((GildeMoneyChangeEvent) e).getAction()) {
			case ADD:
				money.currentMoney+=((GildeMoneyChangeEvent) e).getMoney();
				break;
			case REMOVE:
				money.currentMoney-=((GildeMoneyChangeEvent) e).getMoney();
				break;
			case SET:
				money.currentMoney=((GildeMoneyChangeEvent) e).getMoney();
				break;
			case HISTORY_ADD:
				money.history.add(((GildeMoneyChangeEvent) e).getRecord());
				break;
			default:
				break;
			}
		} else if (e instanceof GildePermissionEvent) {
			GildePermissionEvent event = (GildePermissionEvent) e;
			Gilde g = manager.getGilde(event.getGilde());
			if (g == null)
				return;
			if(!g.getSelection(event.getGildenType()).isActive())
				return;
			if(event.getAction() == Action.GROUP_ADD){
				g.getSelection(event.getGildenType()).getPermission().loadGroup(event.getGroup());
			}
			else if(event.getAction() == Action.GROUP_REMOVE){
				g.getSelection(event.getGildenType()).getPermission().unloadGroup(event.getGroup());
			}else if(event.getAction() == Action.ADD){
				GildPermissionGroup group = g.getSelection(event.getGildenType()).getPermission().getGroup(event.getGroup());
				if(group == null)
					return;
				group.permissions.add(event.getPermission());
			}else if(event.getAction() == Action.REMOVE){
				GildPermissionGroup group = g.getSelection(event.getGildenType()).getPermission().getGroup(event.getGroup());
				if(group == null)
					return;
				group.permissions.remove(event.getPermission());
			}
		} else if(e instanceof GildePlayerEvent){
			GildePlayerEvent event = (GildePlayerEvent) e;
			Gilde g = manager.getGilde(event.getGilde());
			if (g == null)
				return;
			if(!g.getSelection(event.getGildenType()).isActive())
				return;

			if(event.getRank() != null && event.getRank().equalsIgnoreCase(INVITE_GROUP)){
				if(((GildePlayerEvent) e).getAction() == GildePlayerEvent.Action.ADD){
					if(!g.getSelection(event.getGildenType()).requestedPlayer.contains(event.getPlayer())){
						g.getSelection(event.getGildenType()).requestedPlayer.add(event.getPlayer());
					}
				} else if(((GildePlayerEvent) e).getAction() == GildePlayerEvent.Action.REMOVE){
					if(g.getSelection(event.getGildenType()).requestedPlayer.contains(event.getPlayer())){
						g.getSelection(event.getGildenType()).requestedPlayer.remove(event.getPlayer());
					}
				}
				return;
			}
			if(event.getAction() == GildePlayerEvent.Action.ADD){
				ArrayList<Integer> players = g.getSelection(event.getGildenType()).players;
				if(!players.contains(new Integer(event.getPlayer()))){
					players.add(new Integer(event.getPlayer()));
					g.getSelection(event.getGildenType()).getPermission().players.put(new Integer(event.getPlayer()),event.getRank());
				}
			}else if(event.getAction() == dev.wolveringer.events.gilde.GildePlayerEvent.Action.REMOVE){
				ArrayList<Integer> players = g.getSelection(event.getGildenType()).players;
				if(players.contains(new Integer(event.getPlayer()))){
					players.remove(new Integer(event.getPlayer()));
					g.getSelection(event.getGildenType()).getPermission().players.remove(new Integer(event.getPlayer()));
				}
			}
			else if(event.getAction() == dev.wolveringer.events.gilde.GildePlayerEvent.Action.CHANGE){
				ArrayList<Integer> players = g.getSelection(event.getGildenType()).players;
				if(players.contains(new Integer(event.getPlayer()))){
					g.getSelection(event.getGildenType()).getPermission().players.put(new Integer(event.getPlayer()),event.getRank());
				}
			}
		}
	}
}
