package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * ReTeX based implementation of AV Text
 *
 */
public class TextTreeItem extends RadioTreeItem {
	private GeoText text = null;

	/**
	 * @param geo0
	 *            text geo
	 */
	public TextTreeItem(GeoElement geo0) {
		super(geo0);
		text = (GeoText) geo;
		doUpdate();
	}

	@Override
	protected void doUpdate() {
		if (text == null) {
			return; // called from super constructor
		}
		setNeedsUpdate(false);
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		content.clear();

		text.getDescriptionForAV(
				new DOMIndexHTMLBuilder(getDefinitionValuePanel(), app));
		content.add(getDefinitionValuePanel());
		getDefinitionValuePanel().getElement().addClassName("textWrap");
	}
	
	/**
	 * @param geo
	 *            element
	 * @return if geo matches to CheckboxTreeItem.
	 */
	public static boolean match(GeoElement geo) {
		return AlgebraItem.isTextItem(geo);
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
