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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraStyle;

/**
 * Helper methods for creating new AV items
 */
public class ItemFactory {

	/**
	 *
	 * @param geo
	 *            geo element
	 * @return if geo matches to SliderTreeItem.
	 */
	public boolean matchSlider(GeoElement geo) {
		return AlgebraItem.shouldShowSlider(geo);
	}

	/**
	 * @param geo
	 *            element
	 * @return if geo matches to CheckboxTreeItem.
	 */
	public static boolean matchCheckbox(GeoElement geo) {
		return geo instanceof GeoBoolean && geo.isSimple();
	}

	/**
	 * @param geo element
	 * @return Whether the element should be depicted as a linear notation item.
	 */
	public static boolean matchLinearNotation(GeoElement geo) {
		return geo.getApp().getAlgebraStyle() == AlgebraStyle.LINEAR_NOTATION;
	}

	/**
	 * @param ob geo element
	 * @return AV item
	 */
	public final RadioTreeItem createAVItem(final GeoElement ob) {
		RadioTreeItem ti;
		if (matchCheckbox(ob)) {
			ti = new CheckboxTreeItem(ob);
		} else if (matchLinearNotation(ob)) {
			ti = new LinearNotationTreeItem(ob);
		} else if (matchSlider(ob)) {
			ti = new SliderTreeItemRetex(ob);
		} else if (AlgebraItem.isTextItem(ob)) {
			ti = new TextTreeItem(ob);
		} else {
			ti = new LaTeXTreeItem(ob);
		}
		ti.setUserObject(ob);
		ti.addStyleName("avItem");
		return ti;
	}
}
