package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.CategoryPopup;
import org.geogebra.web.html5.main.AppW;

public class PenCategoryPopup extends CategoryPopup {


	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 */
	public PenCategoryPopup(AppW app, List<Integer> tools,
			Consumer<Integer> updateParentCallback) {
		super(app, tools, updateParentCallback);

	}
}
