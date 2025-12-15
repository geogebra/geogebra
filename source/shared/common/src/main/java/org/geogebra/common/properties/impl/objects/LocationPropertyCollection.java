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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.factory.GeoElementPropertiesFactory;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;
import org.geogebra.common.properties.impl.facade.BooleanPropertyListFacade;

/**
 * {@code PropertyCollection} containing {@code BooleanProperty}s, each representing a location
 * and indicating whether the related {@code GeoElement} is visible at that location.
 */
public final class LocationPropertyCollection extends AbstractPropertyCollection<BooleanProperty> {
	private final static class AlgebraViewLocationProperty extends AbstractValuedProperty<Boolean>
			implements BooleanProperty, GeoElementDependentProperty {
		private final GeoElement element;

		AlgebraViewLocationProperty(Localization localization, GeoElement element) {
			super(localization, "Algebra");
			this.element = element;
		}

		@Override
		protected void doSetValue(Boolean value) {
			element.setAlgebraVisible(value);
			if (value) {
				element.getApp().getAlgebraView().add(element);
			} else {
				element.getApp().getAlgebraView().remove(element);
			}
			element.updateRepaint();
		}

		@Override
		public Boolean getValue() {
			return element.isAlgebraVisible();
		}

		@Override
		public GeoElement getGeoElement() {
			return element;
		}
	}

	private static final class GraphicsViewLocationProperty extends AbstractValuedProperty<Boolean>
			implements BooleanProperty, GeoElementDependentProperty {
		private final GeoElement element;

		GraphicsViewLocationProperty(Localization localization, GeoElement element) {
			super(localization, "DrawingPad");
			this.element = element;
		}

		@Override
		protected void doSetValue(Boolean value) {
			if (value) {
				element.getApp().addToEuclidianView(element);
			} else {
				element.getApp().removeFromEuclidianView(element);
			}
		}

		@Override
		public Boolean getValue() {
			return element.isVisibleInView(App.VIEW_EUCLIDIAN);
		}

		@Override
		public GeoElement getGeoElement() {
			return element;
		}
	}
	
	private static final class GraphicsView2LocationProperty
			extends AbstractValuedProperty<Boolean>
			implements BooleanProperty, GeoElementDependentProperty {
		private final GeoElement element;

		GraphicsView2LocationProperty(Localization localization, GeoElement element) {
			super(localization, "DrawingPad2");
			this.element = element;
		}
		
		@Override
		protected void doSetValue(Boolean value) {
			if (value) {
				element.addView(App.VIEW_EUCLIDIAN2);
				element.getApp().getEuclidianView2(1).add(element);
			} else {
				element.removeView(App.VIEW_EUCLIDIAN2);
				element.getApp().getEuclidianView2(1).remove(element);
			}
		}

		@Override
		public Boolean getValue() {
			return element.isVisibleInView(App.VIEW_EUCLIDIAN2);
		}

		@Override
		public boolean isAvailable() {
			return element.getApp().getEuclidianView2(1) != null;
		}

		@Override
		public GeoElement getGeoElement() {
			return element;
		}
	}

	private static final class GraphicsView3DLocationProperty
			extends AbstractValuedProperty<Boolean>
			implements BooleanProperty, GeoElementDependentProperty {
		private final GeoElement element;

		GraphicsView3DLocationProperty(Localization localization, GeoElement element) {
			super(localization, "GraphicsView3D");
			this.element = element;
		}

		@Override
		protected void doSetValue(Boolean value) {
			if (element.getApp().getEuclidianView3D() == null) {
				return;
			}
			if (value) {
				element.addViews3D();
				element.getApp().getEuclidianView3D().add(element);
			} else {
				element.removeViews3D();
				element.getApp().getEuclidianView3D().remove(element);
			}
		}

		@Override
		public Boolean getValue() {
			return element.isVisibleInView3D();
		}

		@Override
		public boolean isAvailable() {
			return element.getApp().getEuclidianView3D() != null && element.hasDrawable3D();
		}

		@Override
		public GeoElement getGeoElement() {
			return element;
		}
	}

	/**
	 * Constructs the property for the given elements.
	 * @param propertiesFactory properties factory for creating property facades for the given list
	 * of elements
	 * @param localization localization for translating property names
	 * @param elements the elements to create the property for
	 */
	public LocationPropertyCollection(GeoElementPropertiesFactory propertiesFactory,
			Localization localization, List<GeoElement> elements) {
		super(localization, "Location");
		setProperties(new BooleanProperty[]{
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new AlgebraViewLocationProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new GraphicsViewLocationProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new GraphicsView2LocationProperty(localization, element),
						BooleanPropertyListFacade::new),
				propertiesFactory.createOptionalPropertyFacade(elements,
						element -> new GraphicsView3DLocationProperty(localization, element),
						BooleanPropertyListFacade::new)
		});
	}
}
