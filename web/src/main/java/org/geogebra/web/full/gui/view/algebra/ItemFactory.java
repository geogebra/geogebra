package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

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
	 * @param ob
	 *            geo element
	 * @return AV item
	 */
	public final RadioTreeItem createAVItem(final GeoElement ob) {
		RadioTreeItem ti;
		if (matchSlider(ob)) {
			ti = new SliderTreeItemRetex(ob);
		} else if (matchCheckbox(ob)) {
			ti = new CheckboxTreeItem(ob);
		} else if (AlgebraItem.isTextItem(ob)) {
			ti = new TextTreeItem(ob);
		} else {
			ti = new RadioTreeItem(ob);
		}
		ti.setUserObject(ob);
		ti.addStyleName("avItem");
		return ti;
	}
}
