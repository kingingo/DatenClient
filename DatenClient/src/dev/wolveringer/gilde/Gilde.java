package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildInformationResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemberResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemberResponse.MemberInformation;
import dev.wolveringer.gilde.GildeType;
import lombok.Getter;

public class Gilde {
	@Getter
	private ClientWrapper connection;
	
	@Getter
	private UUID uuid;
	@Getter
	private String name;
	@Getter
	private String shortName;
	@Getter
	private int ownerId;
	
	private HashMap<GildeType, GildSection> selections = new HashMap();
	private boolean exist;
	private boolean loaded;
	
	public Gilde(ClientWrapper connection,UUID uuid) {
		this.uuid = uuid;
		this.connection = connection;
	}
	
	public synchronized void reload(){
		loaded = false;
		load();
	}
	
	public synchronized void reloadNameAndShortname(){
		PacketGildInformationResponse infos = connection.getGildeInformations(uuid).getSync();
		name = infos.getName();
		shortName = infos.getShortName();
		ownerId = infos.getOwnerId();
	}
	
	public void load(){
		if(loaded)
			return;
		try{
			PacketGildInformationResponse infos = connection.getGildeInformations(uuid).getSync();
			name = infos.getName();
			shortName = infos.getShortName();
			ownerId = infos.getOwnerId();
			for(GildeType t : infos.getActiveSections())
				selections.put(t, new GildSection(this, t, true));
			for(GildeType t : GildeType.values())
				if(t != GildeType.ALL)
					selections.putIfAbsent(t, new GildSection(this, t, false));
			PacketGildMemberResponse member = connection.getGildeMembers(uuid).getSync();
			for(MemberInformation i : member.getMember()){
				System.out.println(i.getPlayerId()+" - "+Arrays.asList(i.getGroups())+" - "+Arrays.asList(i.getMember()));
				for(int j = 0;j<i.getMember().length;j++) {
					selections.get(i.getMember()[j]).players.add(i.getPlayerId());
					selections.get(i.getMember()[j]).getPermission().players.put(i.getPlayerId(), i.getGroups()[j]);
				}
			}
			exist = true;
			loaded = true;
		}catch(PacketHandleErrorException e){
			if(e.getErrors().length >= 1 && e.getErrors()[0].getId() == -2){
				loaded = true;
				exist = false;
			}
			else
				e.printStackTrace();
		}
			
	}
	
	public GildSection getSelection(GildeType type){
		return selections.get(type);
	}
	
	public List<GildSection> getActiveSections(){
		ArrayList<GildSection> out = new ArrayList<>();
		for(GildSection s : selections.values())
			if(s.isActive())
				out.add(s);
		return out;
	}
}
