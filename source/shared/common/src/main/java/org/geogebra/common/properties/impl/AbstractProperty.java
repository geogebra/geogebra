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

package org.geogebra.common.properties.impl;

import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;

/**
 * Helper class for implementing the localized name of a property.
 */
public abstract class AbstractProperty implements Property {

	private Localization localization;
	private String name;
	private boolean frozen = false;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public AbstractProperty(Localization localization, String name) {
		this.localization = localization;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return localization.getMenu(name);
	}

	@Override
	public @Nonnull String getRawName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	/**
	 * Returns the localization of the class.
	 * @return localization used
	 */
	protected Localization getLocalization() {
		return localization;
	}

	@Override
	public boolean isFrozen() {
		return frozen;
	}

	@Override
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
