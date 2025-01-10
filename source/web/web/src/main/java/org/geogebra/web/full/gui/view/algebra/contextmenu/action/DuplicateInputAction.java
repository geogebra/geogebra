package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

public class DuplicateInputAction implements MenuAction<GeoElement> {

	private AlgebraViewW algebraView;

	public DuplicateInputAction(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void execute(GeoElement item) {
		RadioTreeItem input = algebraView.getInputTreeItem();
		String dup = AlgebraItem.getDuplicateFormulaForGeoElement(item);
		RadioTreeItem currentNode = algebraView.getNode(item);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		if (input != null) {
			input.setText(dup);
			input.setFocus(true);
		}
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraDuplicateable();
	}
}
