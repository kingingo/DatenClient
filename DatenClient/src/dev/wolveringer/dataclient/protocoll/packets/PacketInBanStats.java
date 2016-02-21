package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PacketInBanStats extends Packet{
	private UUID request;
	private BanEntity e;
	
	public static class BanEntity {
		@Getter
		private String ip;
		@Getter
		private String username =  null;
		@Getter
		private UUID uuid = null;
		@Getter
		private String reson;
		
		@Getter
		private String banner;
		@Getter
		private String bannerIp;
		@Getter
		private UUID bannerUUID;
		
		@Getter
		private int level;
		@Getter
		private long end;
		
		protected BanEntity(String ip,String username,String uuid,String banner,String bannerUUID,String bannerIp,String reson,int level,long end) {
			this.end = end;
			this.ip = ip;
			this.bannerIp = bannerIp;
			if(username != null && username.length() != 0 && !username.equalsIgnoreCase("null"))
				this.username = username.toLowerCase();
			if(uuid != null && uuid.length() != 0 && !uuid.equalsIgnoreCase("null"))
				this.uuid = UUID.fromString(uuid);
			this.banner = banner;
			if(bannerUUID != null && bannerUUID.length() != 0 && !bannerUUID.equalsIgnoreCase("null") && !bannerUUID.equalsIgnoreCase("AACHack"))
				this.bannerUUID = UUID.fromString(bannerUUID);
			this.level = level;
			this.reson = reson;
		}
		
		public boolean isTempBanned(){
			return end != -1;
		}
		
		public boolean isActive(){
			return end == -1 || System.currentTimeMillis()<end;
		}

		@Override
		public String toString() {
			return "BanEntity [ip=" + ip + ", username=" + username + ", uuid=" + uuid + ", reson=" + reson + ", banner=" + banner + ", bannerIp=" + bannerIp + ", bannerUUID=" + bannerUUID + ", level=" + level + ", end=" + end + "]";
		}
		private static class NotBannedBanEntity extends BanEntity {
			public NotBannedBanEntity() {
				super(null,null,null,null,null,null,null,0,0L);
			}
			@Override
			public boolean isTempBanned() {
				return false;
			}
			@Override
			public boolean isActive() {
				return false;
			}
			@Override
			public String toString() {
				return "BanEntity [Not banned]";
			}
		}
	}

	@Override
	public void read(DataBuffer buffer) {
		request = buffer.readUUID();
		if(buffer.readBoolean()){
			String username = buffer.readString();
			String uuid = buffer.readString();
			String ip = buffer.readString();
			String banner = buffer.readString();
			String bannerIp = buffer.readString();
			String bannerUUID = buffer.readString();
			long end = buffer.readLong();
			Integer level = buffer.readInt();
			String reson = buffer.readString();
			e = new BanEntity(ip, username, uuid, banner, bannerUUID, bannerIp, reson, level, end);
		}
		else
			e = new BanEntity.NotBannedBanEntity();
	}
}
