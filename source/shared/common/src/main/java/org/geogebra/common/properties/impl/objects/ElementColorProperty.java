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

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.ElementColorPropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Color property
 */
public class ElementColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {

	protected final GeoElement element;
	private final AbstractGeoElementDelegate delegate;

	/**
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public ElementColorProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.Color");
		this.element = element;
		delegate = new ElementColorPropertyDelegate(element);
		setValues(createColorValues());
	}

	/**
	 * Constructor
	 * @param localization - localization
	 * @param delegate - delegate
	 */
	public ElementColorProperty(Localization localization, AbstractGeoElementDelegate delegate) {
		super(localization, "stylebar.Color");
		this.delegate = delegate;
		this.element = delegate.getElement();
	}

	/**
	 * Constructor
	 * @param localization - localization
	 * @param delegate - delegate
	 * @param name - name
	 */
	public ElementColorProperty(Localization localization, AbstractGeoElementDelegate delegate,
			String name) {
		super(localization, name);
		this.delegate = delegate;
		this.element = delegate.getElement();
	}

	@Override
	public GColor getValue() {
		return element.getObjectColor();
	}

	@Override
	public void doSetValue(GColor color) {
		EuclidianStyleBarStatic.applyColor(color, element.getAlphaValue(), element.getApp(),
				List.of(element));
	}

	@Override
	public boolean isEnabled() {
		return element.isEuclidianVisible() && delegate.getElement().isEuclidianVisible();
	}

	/**
	 * @return color values
	 */
	public static List<GColor> createColorValues() {
		GColor[] primColor = GeoGebraColorConstants.getPrimarySwatchColors();
		GColor[] scolors = GeoGebraColorConstants.getMainColorSwatchColors();
		return List.of(
				primColor[0], primColor[2], primColor[4],
				primColor[8], primColor[10], primColor[12], GColor.BLACK,
				GeoGebraColorConstants.GEOGEBRA_OBJECT_RED,
				GeoGebraColorConstants.GGB_ORANGE, scolors[19],
				GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN, scolors[43],
				GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE,
				GeoGebraColorConstants.PURPLE_600, scolors[0],
				scolors[8], scolors[16], scolors[32], scolors[40], scolors[48],
				scolors[56], scolors[1], scolors[9], scolors[17], scolors[24],
				scolors[41], scolors[49], scolors[57], scolors[3], scolors[11],
				primColor[5], scolors[33], primColor[11], scolors[51],
				scolors[59], scolors[4], scolors[12], scolors[20], scolors[36],
				scolors[44], scolors[52], scolors[60], scolors[6], scolors[14],
				scolors[22], scolors[38], scolors[46], scolors[54], scolors[62],
				scolors[7], scolors[15], scolors[23], scolors[39], scolors[47],
				scolors[55], scolors[63]
		);
	}
}
