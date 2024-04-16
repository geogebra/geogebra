package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.CategoryPopup;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.uibinder.client.impl.AbstractUiRenderer;

public class PenCategoryPopup extends CategoryPopup {
	private PenCategoryController controller;
	private ColorChooserPanel colorChooser;
	private PenIconButton penButton;

	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 */
	public PenCategoryPopup(AppW app, List<Integer> tools,
			Consumer<Integer> updateParentCallback, PenIconButton penButton) {
		super(app, tools, updateParentCallback, true);

		this.penButton = penButton;
		controller = new PenCategoryController(app);

		addStyleName("penCategory");
		buildGui();
	}

	private void buildGui() {
		colorChooser = new ColorChooserPanel((AppW) getApplication(), (color) -> {
			if (penButton.getMode() == MODE_PEN) {
				controller.setLastPenColor(color);
			} else if (penButton.getMode() == MODE_HIGHLIGHTER) {
				controller.setLastHighlighterColor(color);
			}
			controller.updatePenColor(color);
		});
		addContent(colorChooser);
	}

	/**
	 * disable or enable color palette
	 * @param disable - true or false
	 */
	public void disableColorChooser(boolean disable) {
		colorChooser.setDisabled(disable);
	}
}
