/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.move.ggtapi;

import java.util.Locale;

/**
 * Mebis specific identifier of groups for sharing.
 */
public class GroupIdentifier {
	public final String name;
	private final GroupCategory category;

	/**
	 * Group category.
	 */
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
