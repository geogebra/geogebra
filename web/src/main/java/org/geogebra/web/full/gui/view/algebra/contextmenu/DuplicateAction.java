package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.AppWFull;

public class DuplicateAction extends MenuAction {
	private RadioTreeItem avItem;

	/**
	 * @param avItem
	 *            AV item
	 */
	public DuplicateAction(RadioTreeItem avItem) {
		super("Duplicate", MaterialDesignResources.INSTANCE.duplicate_black());
		this.avItem = avItem;
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		RadioTreeItem input = avItem.getAV().getInputTreeItem();
		String dup = "";
		if ("".equals(geo.getDefinition(StringTemplate.defaultTemplate))) {
			dup = geo.getValueForInputBar();
		} else {
			dup = geo.getDefinitionNoLabel(StringTemplate.editorTemplate);
		}
		avItem.selectItem(false);
		input.setText(dup);
		input.setFocus(true, true);
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo.isAlgebraDuplicateable();
	}
}

