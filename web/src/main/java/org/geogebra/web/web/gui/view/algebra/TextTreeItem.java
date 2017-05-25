package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Feature;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * ReTeX based implementation of AV Text
 *
 */
public class TextTreeItem extends RadioTreeItem {
	/**
	 * @param geo0
	 *            text geo
	 */
	private GeoText text;

	public TextTreeItem(GeoElement geo0) {
		super(geo0);
		text = (GeoText) geo;
	}

	@Override
	protected void addControls() {
		createControls();
		// no add this time
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		content.clear();

		text.getDescriptionForAV(
				getBuilder(getPlainTextItem()));
		content.add(getPlainTextItem());
	}
	
	/**
	 * @param geo
	 *            element
	 * @return if geo matches to CheckboxTreeItem.
	 */
	public static boolean match(GeoElement geo) {
		return geo.getConstruction().getApplication().has(Feature.AV_TEXT_ITEM)
				&& geo instanceof GeoText;
	}

	public static TextTreeItem as(TreeItem ti) {
		return (TextTreeItem) ti;
	}

	@Override
	public boolean isTextItem() {
		return true;
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}
}
