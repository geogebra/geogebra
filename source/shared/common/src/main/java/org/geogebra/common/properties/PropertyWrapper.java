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

package org.geogebra.common.properties;

import java.util.List;
import java.util.function.Function;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.undo.UndoActionObserver;
import org.geogebra.common.main.undo.UndoActionType;

public class PropertyWrapper {
	private final App app;

	public PropertyWrapper(App application) {
		this.app = application;
	}

	/**
	 * Provides mutable property collection with undo action observer.
	 * @param map updates list of properties when underlying list of geos changes
	 * @param activeGeoList initial list of elements
	 * @return mutable collection of properties
	 */
	public PropertySupplier withStrokeSplitting(Function<List<GeoElement>, Property> map,
			List<GeoElement> activeGeoList) {

		return new PropertySupplier() {

			Property current = map.apply(activeGeoList);

			@Override
			public Property updateAndGet() {
				if (!app.getActiveEuclidianView()
						.getEuclidianController().splitSelectedStrokes(true)) {
					return current;
				}
				current = map.apply(app.getSelectionManager().getSelectedGeos());
				addUndoActionObserver(new PropertySupplier[]{current},
						app.getSelectionManager().getSelectedGeos(),
						UndoActionType.STYLE);
				return current;
			}

			@Override
			public Property get() {
				return current;
			}
		};
	}

	/**
	 * Adds undo action observer to each supplied property.
	 * @param properties collection of properties
	 * @param geos list of elements
	 * @param undoActionType undoable action type, determines which properties need to be stored
	 */
	public void addUndoActionObserver(PropertySupplier[] properties, List<GeoElement> geos,
			UndoActionType undoActionType) {
		for (PropertySupplier propertySupplier: properties) {
			Property property = propertySupplier.get();
			if (property instanceof ValuedProperty) {
				((ValuedProperty<?>) property).addValueObserver(
						new UndoActionObserver(geos, undoActionType));
			}
		}
	}

}
