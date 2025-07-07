package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * ReTeX based implementation of AV Text
 *
 */
public class TextTreeItem extends LaTeXTreeItem {

	/**
	 * @param geo0
	 *            text geo
	 */
	public TextTreeItem(GeoElement geo0) {
		super(geo0);
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		if (typeChanged()) {
			updateTreeItemAfterTypeChanged();
			return;
		}
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		content.clear();

		((GeoText) geo).getDescriptionForAV(
				new DOMIndexHTMLBuilder(getDefinitionValuePanel(), app));
		content.add(getDefinitionValuePanel());
		getDefinitionValuePanel().getElement().addClassName("textWrap");
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
