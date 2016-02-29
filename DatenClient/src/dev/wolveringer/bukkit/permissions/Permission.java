package dev.wolveringer.bukkit.permissions;

import lombok.Getter;

@Getter
public class Permission {
	private String permission;
	private GroupTyp group;
	private int starIndex = -1;
	
	
	public boolean acceptPermission(String perm){
		if(starIndex != -1){
			return permission.substring(0,Math.min(starIndex, perm.length())).equalsIgnoreCase(perm.substring(0,starIndex));
		}
		return permission.equalsIgnoreCase(perm);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		if (group != other.group)
			return false;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		return true;
	}

	public Permission(String permission, GroupTyp group) {
		this.permission = permission;
		this.group = group;
		starIndex = permission.indexOf("*");
	}
}