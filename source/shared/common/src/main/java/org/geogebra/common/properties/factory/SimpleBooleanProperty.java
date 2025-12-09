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

package org.geogebra.common.properties.factory;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 *
 */
public class SimpleBooleanProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private final Supplier<Boolean> getter;
	private final Consumer<Boolean> setter;

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 * @param setter property setter
	 * @param getter property getter
	 */
	public SimpleBooleanProperty(Localization localization, String name,
			Supplier<Boolean> getter, Consumer<Boolean> setter) {
		super(localization, name);
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	protected void doSetValue(Boolean value) {
		setter.accept(value);
	}

	@Override
	public Boolean getValue() {
		return getter.get();
	}
}
