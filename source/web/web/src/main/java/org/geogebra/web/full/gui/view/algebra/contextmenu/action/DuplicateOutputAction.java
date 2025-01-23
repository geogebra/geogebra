package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

public class DuplicateOutputAction implements MenuAction<GeoElement> {

	private AlgebraViewW algebraView;

	public DuplicateOutputAction(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void execute(GeoElement item) {
		ToStringConverter<GeoElement> converter = algebraView.getApp()
				.getGeoElementValueConverter();
		RadioTreeItem input = algebraView.getInputTreeItem();
		String geoString = converter.convert(item);

		RadioTreeItem currentNode = algebraView.getNode(item);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		if (input != null) {
			input.setText(geoString);
			input.setFocus(true);
		}
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraDuplicateable()
				&& AlgebraItem.shouldShowBothRows(item);
	}
}
