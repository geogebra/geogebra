package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.AppWFull;

public class DuplicateOutputAction extends DefaultMenuAction<GeoElement> {

	private AlgebraViewW algebraView;

	public DuplicateOutputAction(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void execute(GeoElement item, AppWFull app) {
		ToStringConverter<GeoElement> converter = app.createGeoElementValueConverter();
		RadioTreeItem input = algebraView.getInputTreeItem();
		String geoString = converter.convert(item);

		RadioTreeItem currentNode = algebraView.getNode(item);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		input.setText(geoString);
		input.setFocus(true);
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraDuplicateable()
				&& AlgebraItem.shouldShowBothRows(item);
	}
}
