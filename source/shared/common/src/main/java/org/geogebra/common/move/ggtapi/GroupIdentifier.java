package org.geogebra.common.move.ggtapi;

import java.util.Locale;

public class GroupIdentifier {
	public final String name;
	private final GroupCategory category;

	public enum GroupCategory {
		CLASS, COURSE
	}

	/**
	 * @param name display name
	 * @param category group category
	 */
	public GroupIdentifier(String name, GroupCategory category) {
		this.name = name;
		this.category = category == null ? GroupCategory.CLASS : category;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof GroupIdentifier) {
			return name.equals(((GroupIdentifier) other).name)
					&& category == ((GroupIdentifier) other).category;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode() | category.hashCode();
	}

	public String getCategory() {
		return category.name().toLowerCase(Locale.ROOT);
	}

}
