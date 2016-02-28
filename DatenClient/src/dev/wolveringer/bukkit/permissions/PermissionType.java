package dev.wolveringer.bukkit.permissions;

public enum PermissionType {
	ALL_PERMISSION("epicpvp.*"),
	KBAN("epicpvp.bg.kban"),
	ZEITBAN("epicpvp.bg.zeitban"),
	ZEITBAN_MAX_14TAGE("epicpvp.bg.zeitban.14tage"),
	ZEITBAN_MAX_7TAGE("epicpvp.bg.zeitban.7tage"),
	KICKEN("epicpvp.bg.kicken"),
	CLEAR_CHAT("epicpvp.bg.cc"),
	SERVER("epicpvp.bg.server"),
	SERVER_SEND("epicpvp.bg.server.send"),
	DEL_SERVER("epicpvp.bg.delserver"),
	ADD_SERVER("epicpvp.bg.addserver"),
	TEAM_MESSAGE("epicpvp.bg.team_message"),
	WHERE_IS("epicpvp.bg.whereis"),
	KINFO("epicpvp.bg.kinfo"),
	RESTART("epicpvp.bg.restart"),
	PROTECTION("epicpvp.bg.protection"),
	BROADCAST("epicpvp.bg.broadcast"),
	MOTD("epicpvp.bg.motd"),
	DATEN_SERVER_CHECK("epicpvp.bg.datenservercheck"),
	PREMIUM_TOGGLE("epicpvp.bg.premium"),
	UNBAN("epicpvp.bg.unkban"),
	LAG("epicpvp.bg.lag"),
	WHO("epicpvp.bg.who"),
	CLIENT("epicpvp.bg.client"),
	TICKET_ADMIN("epicpvp.bg.ticket"),
	SETGROUP("epicpvp.bg.setgroup"),
	PERMISSION("epicpvp.bg.permission"),
	TBUILD_SERVER("epicpvp.bg.tbuild.server"),
	BUILD_SERVER("epicpvp.bg.build.server"),
	NICK_SEE("epicpvp.bg.nick.see"),
	NICK_RANDOM("epicpvp.bg.nick.random"),
	NICK_SET("epicpvp.bg.nick.set"),
	PREMIUM_LOBBY("epicpvp.bg.hub.premium"),
	HUB_MUTE_BYPASS("epicpvp.bg.hub.mute.bypass"),

	BAN_LVL_1("epicpvp.bg.kban.lvl.one"),
	BAN_LVL_2("epicpvp.bg.kban.lvl.two"),
	BAN_LVL_3("epicpvp.bg.kban.lvl.three"),
	BAN_LVL_4("epicpvp.bg.kban.lvl.four"),
	BAN_LVL_5("epicpvp.bg.kban.lvl.five"),

	TEAMSPEAK_QUERY("epicpvp.ts.query"),
	TEAMSPEAK_OWNER("epicpvp.ts.owner"),
	TEAMSPEAK_DEV_OWNER("epicpvp.ts.dev_owner"),
	TEAMSPEAK_ADMIN("epicpvp.ts.admin"),
	TEAMSPEAK_TEAM_LEITER("epicpvp.ts.team_leiter"),
	TEAMSPEAK_SMOD("epicpvp.ts.smod"),
	TEAMSPEAK_FOURM_MOD("epicpvp.ts.forummod"),
	TEAMSPEAK_PVP_ADMIN("epicpvp.ts.pvpadmin"),
	TEAMSPEAK_MOD("epicpvp.ts.mod"),
	TEAMSPEAK_ARCHITEKT_LEITER("epicpvp.ts.architekt-leiter"),
	TEAMSPEAK_ARCHITEKT("epicpvp.ts.architekt"),
	TEAMSPEAK_HELFER("epicpvp.ts.helfer"),
	TEAMSPEAK_PROBE_ARCHITEKT("epicpvp.ts.probe_architekt"),
	TEAMSPEAK_MITGLIED("epicpvp.ts.mitglied"),
	TEAMSPEAK_PREMIUM("epicpvp.ts.premium"),
	TEAMSPEAK_YOUTUBER("epicpvp.ts.youtuber"),
	TEAMSPEAK_TEAM_FREUND("epicpvp.ts.team_freund"),
	TEAMSPEAK_GEBURTSTAG("epicpvp.ts.geburtstag"),
	TEAMSPEAK_VERIFIZIERT("epicpvp.ts.verifiziert"),
	TEAMSPEAK_BOT("epicpvp.ts.bot"),
	TEAMSPEAK_NICHT_ANSTUPSBAR("epicpvp.ts.nicht_anstupsbar"),
	TEAMSPEAK_GARNICHT_ANSTUPSBAR("epicpvp.ts.garnicht_anstupsbar"),
	TEAMSPEAK_NICHT_REDEN("epicpvp.ts.nicht_reden"),
	TEAMSPEAK_NICHT_BEWEGEN("epicpvp.ts.nicht_bewegen"),
	TEAMSPEAK_NICHT_ANSTUPSBART("epicpvp.ts.nicht_anstupsbart"),
	TEAMSPEAK_NICHT_ANSCHREIBBAR("epicpvp.ts.nicht_anschreibbar"),

	NONE("FAIL");

	public static PermissionType isPerm(String perm) {
		PermissionType per = PermissionType.NONE;
		for (PermissionType permission : PermissionType.values()) {
			if (permission.getPermissionToString().equalsIgnoreCase(perm)) {
				per = permission;
				break;
			}
		}
		return per;
	}

	private String perm;

	private PermissionType(String perm) {
		this.perm = perm;
	}

	public String getPermissionToString() {
		return this.perm;
	}

}
