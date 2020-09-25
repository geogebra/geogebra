package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.AppWFull;

public class DuplicateInputAction extends DefaultMenuAction<GeoElement> {

	private AlgebraViewW algebraView;

	public DuplicateInputAction(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void execute(GeoElement item, AppWFull app) {
		RadioTreeItem input = algebraView.getInputTreeItem();
		String dup = AlgebraItem.getDuplicateFormulaForGeoElement(item);
		RadioTreeItem currentNode = algebraView.getNode(item);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		input.setText(dup);
		input.setFocus(true);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraDuplicateable();
	}
}
