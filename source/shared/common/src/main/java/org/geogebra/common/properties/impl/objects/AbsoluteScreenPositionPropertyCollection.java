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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.StringPropertyListFacade;
import org.geogebra.common.properties.impl.objects.PlacementProperty.Placement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for changing the absolute position on the screen, one for both axes.
 */
public class AbsoluteScreenPositionPropertyCollection
		extends AbstractPropertyCollection<StringProperty> {
	private static class AbsoluteScreenPositionProperty extends AbstractValuedProperty<String>
			implements StringProperty, GeoElementDependentProperty {
		private final int axis;
		private final AbsoluteScreenLocateable absoluteScreenLocateable;

		AbsoluteScreenPositionProperty(Localization localization, GeoElement element, int axis)
				throws NotApplicablePropertyException {
			super(localization, axis == 0 ? "x" : "y");
			if (!(element instanceof AbsoluteScreenLocateable)
					|| element instanceof GeoNumeric && !((GeoNumeric) element).isSlider()) {
				throw new NotApplicablePropertyException(element);
			}
			this.axis = axis;
			this.absoluteScreenLocateable = (AbsoluteScreenLocateable) element;
		}

		@SuppressWarnings("CheckResult")
		@Override
		public @CheckForNull String validateValue(String value) {
			if (value == null || value.isEmpty()) {
				return "";
			}
			try {
				ValidExpression validExpression = absoluteScreenLocateable.getKernel().getParser()
						.parseGeoGebraExpression(value);
				if (!validExpression.evaluatesToNumber(false)) {
					return "";
				}
				return null;
			} catch (ParseException parseException) {
				return parseException.getLocalizedMessage();
			}
		}

		@Override
		protected void doSetValue(String value) {
			MyVecNode positionDefinition = getPositionDefinition(absoluteScreenLocateable);
			String[] newPositionDefinition = positionDefinition == null ? new String[] {
					String.valueOf(absoluteScreenLocateable.getAbsoluteScreenLocX()),
					String.valueOf(absoluteScreenLocateable.getAbsoluteScreenLocY())
			} : new String [] {
					positionDefinition.getX().toString(StringTemplate.editTemplate),
					positionDefinition.getY().toString(StringTemplate.editTemplate)
			};
			newPositionDefinition[axis] = value;
			GeoPointND newPositionPoint = absoluteScreenLocateable.getKernel().getAlgebraProcessor()
					.evaluateToPoint("(" + String.join(",", newPositionDefinition) + ")",
							ErrorHelper.silent(), true);
			if (Inspecting.isDynamicGeoElement(newPositionPoint)) {
				try {
					absoluteScreenLocateable.setStartPoint(newPositionPoint);
				} catch (CircularDefinitionException circularDefinitionException) { }
			} else {
				absoluteScreenLocateable.setAbsoluteScreenLoc(
						(int) newPositionPoint.getInhomX(), (int) newPositionPoint.getInhomY());
			}
			absoluteScreenLocateable.updateVisualStyleRepaint(GProperty.POSITION);
		}

		@Override
		public String getValue() {
			MyVecNode positionDefinition = getPositionDefinition(absoluteScreenLocateable);
			if (axis == 0) {
				return positionDefinition != null
						? positionDefinition.getX().toString(StringTemplate.editTemplate)
						: String.valueOf(absoluteScreenLocateable.getAbsoluteScreenLocX());
			} else {
				return positionDefinition != null
						? positionDefinition.getY().toString(StringTemplate.editTemplate)
						: String.valueOf(absoluteScreenLocateable.getAbsoluteScreenLocY());
			}
		}

		@Override
		public GeoElement getGeoElement() {
			return (GeoElement) absoluteScreenLocateable;
		}

		@Override
		public boolean isAvailable() {
			return Placement.of((GeoElement) absoluteScreenLocateable)
					== Placement.ABSOLUTE_POSITION_ON_SCREEN;
		}

		private MyVecNode getPositionDefinition(AbsoluteScreenLocateable absoluteScreenLocateable) {
			GeoPointND startingPoint = absoluteScreenLocateable.getStartPoint();
			if (startingPoint == null) {
				return null;
			}
			if (startingPoint.getDefinition() == null) {
				return null;
			}
			if (startingPoint.getDefinition().unwrap() instanceof MyVecNode) {
				return (MyVecNode) startingPoint.getDefinition().unwrap();
			}
			return null;
		}
	}

	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for any given
	 * elements
	 */
	public AbsoluteScreenPositionPropertyCollection(
			GeoElementPropertiesFactory propertiesFactory, Localization localization,
			List<GeoElement> elements) throws NotApplicablePropertyException {
		super(localization, "");
		setProperties(new StringProperty[]{
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new AbsoluteScreenPositionProperty(localization, element, 0),
						StringPropertyListFacade::new),
				propertiesFactory.createPropertyFacadeThrowing(elements,
						element -> new AbsoluteScreenPositionProperty(localization, element, 1),
						StringPropertyListFacade::new)
		});
	}
}
