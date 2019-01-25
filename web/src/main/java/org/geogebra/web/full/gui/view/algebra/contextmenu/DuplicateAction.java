package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.AppWFull;

import com.google.gwt.user.client.ui.TreeItem;

public class DuplicateAction extends MenuAction {
	private AlgebraViewW algebraView;

	/**
	 * @param av
	 *            algebra view
	 */
	public DuplicateAction(AlgebraViewW av) {
		super("Duplicate", MaterialDesignResources.INSTANCE.duplicate_black());
		this.algebraView = av;
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		RadioTreeItem input = algebraView.getInputTreeItem();
		String dup = "";
		if ("".equals(geo.getDefinition(StringTemplate.defaultTemplate))) {
			dup = geo.getValueForInputBar();
		} else {
			dup = geo.getDefinitionNoLabel(StringTemplate.editorTemplate);
		}
		TreeItem currentNode = algebraView.getNode(geo);
		if(currentNode instanceof RadioTreeItem){
			((RadioTreeItem) currentNode).selectItem(false);
		}
		input.setText(dup);
		input.setFocus(true, true);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo.isAlgebraDuplicateable();
	}
}

