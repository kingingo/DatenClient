package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildInformationResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemberResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemberResponse.MemberInformation;
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
	
	private HashMap<GildeType, GildSection> selections;
	private boolean exist;
	
	public Gilde(ClientWrapper connection,UUID uuid) {
		this.uuid = uuid;
		this.connection = connection;
	}
	
	public void load(){
		try{
			PacketGildInformationResponse infos = connection.getGildeInformations(uuid).getSync();
			name = infos.getName();
			shortName = infos.getShortName();
			for(GildeType t : infos.getActiveSections())
				selections.put(t, new GildSection(this, t, true));
			for(GildeType t : GildeType.values())
				selections.putIfAbsent(t, new GildSection(this, t, false));
			PacketGildMemberResponse member = connection.getGildeMembers(uuid).getSync();
			for(MemberInformation i : member.getMember()){
				for(int j = 0;j<i.getMember().length;j++) {
					selections.get(i.getMember()[j]).players.add(i.getPlayerId());
					selections.get(i.getMember()[j]).getPermission().players.put(i.getPlayerId(), i.getGroups()[j]);
				}
			}
			exist = true;
		}catch(PacketHandleErrorException e){
			if(e.getErrors()[0].getId() == -2)
				exist = false;
		}
			
	}
	public List<GildSection> getActiveSections(){
		ArrayList<GildSection> out = new ArrayList<>();
		for(GildSection s : selections.values())
			if(s.isActive())
				out.add(s);
		return out;
	}
}
