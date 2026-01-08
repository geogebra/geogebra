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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Abstract base class for button size properties (width and height).
 */
public abstract class ButtonSizeProperty extends AbstractNumericProperty
		implements GeoElementDependentProperty {

	protected final GeoButton button;

	private ButtonSizeProperty(AlgebraProcessor algebraProcessor, Localization localization,
			GeoElement element, String name) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, name);
		if (!(element instanceof GeoButton) || element instanceof GeoInputBox) {
			throw new NotApplicablePropertyException(element);
		}
		this.button = (GeoButton) element;
	}

	@Override
	public boolean isEnabled() {
		return button.isFixedSize();
	}

	@Override
	public GeoElement getGeoElement() {
		return button;
	}

	/**
	 * Property for button width.
	 */
	static class Width extends ButtonSizeProperty {

		/**
		 * Creates a Width property.
		 * @param algebraProcessor processor
		 * @param localization localization
		 * @param element construction element
		 */
		Width(AlgebraProcessor algebraProcessor, Localization localization, GeoElement element)
				throws NotApplicablePropertyException {
			super(algebraProcessor, localization, element, "Width");
		}

		@Override
		protected void setNumberValue(GeoNumberValue value) {
			button.setWidth(value.evaluateDouble());
			button.getKernel().notifyRepaint();
		}

		@Override
		protected NumberValue getNumberValue() {
			return new MyDouble(button.getKernel(), button.getWidth());
		}
	}

	/**
	 * Property for button height.
	 */
	static class Height extends ButtonSizeProperty {

		/**
		 * Creates a Height property.
		 * @param algebraProcessor processor
		 * @param localization localization
		 * @param element construction element
		 */
		Height(AlgebraProcessor algebraProcessor, Localization localization, GeoElement element)
				throws NotApplicablePropertyException {
			super(algebraProcessor, localization, element, "Height");
		}

		@Override
		protected void setNumberValue(GeoNumberValue value) {
			button.setHeight(value.evaluateDouble());
			button.getKernel().notifyRepaint();
		}

		@Override
		protected NumberValue getNumberValue() {
			return new MyDouble(button.getKernel(), button.getHeight());
		}
	}
}
