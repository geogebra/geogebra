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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.PointStyleProperty.PointStyle;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the style of points.
 */
public class PointStyleProperty extends AbstractEnumeratedProperty<PointStyle>
		implements IconsEnumeratedProperty<PointStyle> {
	/**
	 * Different styles a point can have.
	 */
	public enum PointStyle {
		DOT(EuclidianStyleConstants.POINT_STYLE_DOT,
				PropertyResource.ICON_POINT_STYLE_DOT),
		CIRCLE(EuclidianStyleConstants.POINT_STYLE_CIRCLE,
				PropertyResource.ICON_POINT_STYLE_CIRCLE),
		NO_OUTLINE(EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE,
				PropertyResource.ICON_POINT_STYLE_NO_OUTLINE),
		CROSS(EuclidianStyleConstants.POINT_STYLE_CROSS,
				PropertyResource.ICON_POINT_STYLE_CROSS),
		PLUS(EuclidianStyleConstants.POINT_STYLE_PLUS,
				PropertyResource.ICON_POINT_STYLE_PLUS),
		EMPTY_DIAMOND(EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND,
				PropertyResource.ICON_POINT_STYLE_EMPTY_DIAMOND),
		FILLED_DIAMOND(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND,
				PropertyResource.ICON_POINT_STYLE_FILLED_DIAMOND),
		NORTH_TRIANGLE(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH,
				PropertyResource.ICON_POINT_STYLE_TRIANGLE_NORTH),
		SOUTH_TRIANGLE(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH,
				PropertyResource.ICON_POINT_STYLE_TRIANGLE_SOUTH),
		EAST_TRIANGLE(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST,
				PropertyResource.ICON_POINT_STYLE_TRIANGLE_EAST),
		WEST_TRIANGLE(EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST,
				PropertyResource.ICON_POINT_STYLE_TRIANGLE_WEST);

		final int euclidianStyleConstant;
		final PropertyResource iconResource;

		PointStyle(int euclidianStyleConstant, PropertyResource iconResource) {
			this.euclidianStyleConstant = euclidianStyleConstant;
			this.iconResource = iconResource;
		}

		static @CheckForNull PointStyle withEuclidianStyleConstant(int euclidianStyleConstant) {
			return Arrays.stream(values()).filter(pointStyle ->
					pointStyle.euclidianStyleConstant == euclidianStyleConstant)
					.findFirst().orElse(null);
		}
	}

	private final PointProperties pointProperties;

	/**
	 * Constructs a property for the point style.
	 * @param localization localization for property name
	 * @param geoElement GeoElement to create property for
	 * @throws NotApplicablePropertyException if the property is not applicable to the element
	 */
	public PointStyleProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "PointStyle");
		if (!(geoElement instanceof PointProperties pointProperties)
				|| !pointProperties.showPointProperties() || geoElement.isGeoElement3D()) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.pointProperties = pointProperties;
		setValues(Arrays.stream(PointStyle.values()).collect(Collectors.toList()));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return Arrays.stream(PointStyle.values())
				.map(pointStyle -> pointStyle.iconResource)
				.toArray(PropertyResource[]::new);
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(PointStyle value) {
		pointProperties.setPointStyle(value.euclidianStyleConstant);
		((GeoElement) pointProperties).updateVisualStyleRepaint(GProperty.POINT_STYLE);
	}

	@Override
	public PointStyle getValue() {
		return PointStyle.withEuclidianStyleConstant(pointProperties.getPointStyle());
	}
}
