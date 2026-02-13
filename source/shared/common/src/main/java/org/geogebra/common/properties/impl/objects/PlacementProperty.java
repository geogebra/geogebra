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

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Map;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.PlacementProperty.Placement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for defining how a {@code GeoElement} is positioned.
 * <p>
 * {@code GeoBoolean}s can only be positioned absolutely on the screen.
 * Most {@code GeoElement}s can also be placed relative to a starting point,
 * and {@code GeoImage}s can be positioned based on given corners or centered around a point.
 */
public class PlacementProperty extends AbstractNamedEnumeratedProperty<Placement>
		implements GeoElementDependentProperty {
	private final GeoElement geoElement;

	/** Different placement options */
	public enum Placement {
		/** Absolute position on the screen */
		ABSOLUTE_POSITION_ON_SCREEN,
		/** Placement relative to a starting point */
		STARTING_POINT,
		/** Placement determined by up to 3 given corners, possible only for images */
		CORNERS,
		/** Centered placement around a point, possible only for images */
		CENTER_IMAGE;

		/**
		 * Determines which type of placement is applied for the given {@code GeoElement}.
		 * @param geoElement the element for which to determine the placement
		 * @return the placement of the given element
		 */
		public static Placement of(GeoElement geoElement) {
			if (geoElement instanceof AbsoluteScreenLocateable
					&& ((AbsoluteScreenLocateable) geoElement).isAbsoluteScreenLocActive()) {
				return Placement.ABSOLUTE_POSITION_ON_SCREEN;
			} else if (geoElement instanceof GeoImage && ((GeoImage) geoElement).isCentered()) {
				return Placement.CENTER_IMAGE;
			} else if (geoElement instanceof GeoImage) {
				return Placement.CORNERS;
			} else {
				return Placement.STARTING_POINT;
			}
		}
	}

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public PlacementProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "ObjectProperties.Placement");
		if (!(geoElement instanceof Locateable)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;

		ArrayList<Map.Entry<Placement, String>> namedValues = new ArrayList<>();
		namedValues.add(entry(Placement.ABSOLUTE_POSITION_ON_SCREEN, "AbsoluteScreenLocation"));
		if (!(geoElement instanceof GeoBoolean) && !(geoElement instanceof GeoImage)) {
			namedValues.add(entry(Placement.STARTING_POINT, "StartingPoint"));
		}
		if (geoElement instanceof GeoImage) {
			namedValues.add(entry(Placement.CORNERS, "ObjectProperties.Corners"));
			namedValues.add(entry(Placement.CENTER_IMAGE, "CenterImage"));
		}
		setNamedValues(namedValues);
	}

	@Override
	protected void doSetValue(Placement placement) {
		// Unset previous placement
		switch (getValue()) {
		case ABSOLUTE_POSITION_ON_SCREEN:
			toggleAbsoluteScreenPosition(false);
			break;
		case STARTING_POINT:
			break;
		case CORNERS:
			try {
				((Locateable) geoElement).setStartPoint(null, 1);
				((Locateable) geoElement).setStartPoint(null, 2);
			} catch (CircularDefinitionException ignored) { }
			break;
		case CENTER_IMAGE:
			((GeoImage) geoElement).setCentered(false);
			break;
		}

		// Set new placement
		switch (placement) {
		case ABSOLUTE_POSITION_ON_SCREEN:
			toggleAbsoluteScreenPosition(true);
			break;
		case STARTING_POINT:
		case CORNERS:
			break;
		case CENTER_IMAGE:
			((GeoImage) geoElement).setCentered(true);
			break;
		}
	}

	@Override
	public Placement getValue() {
		return Placement.of(geoElement.toGeoElement());
	}

	@Override
	public GeoElement getGeoElement() {
		return geoElement;
	}

	private void toggleAbsoluteScreenPosition(boolean active) {
		AbsoluteScreenLocateable element = (AbsoluteScreenLocateable) geoElement;
		EuclidianViewInterfaceCommon euclidianView = geoElement.getApp().getActiveEuclidianView();
		if (active) {
			element.setAbsoluteScreenLoc(
					euclidianView.toScreenCoordX(element.getRealWorldLocX()),
					euclidianView.toScreenCoordY(element.getRealWorldLocY()));
		} else {
			element.setRealWorldLoc(
					euclidianView.toRealWorldCoordX(element.getAbsoluteScreenLocX()),
					euclidianView.toRealWorldCoordY(element.getAbsoluteScreenLocY()));
		}
		element.setAbsoluteScreenLocActive(active);
		if (element.needsUpdatedBoundingBox()) {
			element.updateCascade();
		}
		element.updateVisualStyleRepaint(GProperty.POSITION);
		geoElement.notifyUpdate();
	}

	@Override
	public boolean isAvailable() {
		return !(geoElement instanceof GeoBoolean);
	}
}
