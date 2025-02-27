package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.PenColorValues;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.CategoryPopup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentSlider;

public class PenCategoryPopup extends CategoryPopup implements SettingListener {
	private final PenCategoryController controller;
	private ColorChooserPanel colorChooser;
	private ComponentSlider sliderComponent;

	/**
	 * Constructor
	 * @param app - application
	 * @param tools - list of tools
	 * @param updateParentCallback - callback to update anchor
	 */
	public PenCategoryPopup(AppW app, List<Integer> tools,
			Consumer<Integer> updateParentCallback) {
		super(app, tools, updateParentCallback, true);

		controller = new PenCategoryController(app, this);

		addStyleName("penCategory");
		buildGui();
	}

	private void buildGui() {
		List<GColor> penColors = Arrays.stream(PenColorValues.values()).map(
				PenColorValues::getColor).collect(Collectors.toList());

		colorChooser = new ColorChooserPanel((AppW) getApplication(), penColors, (color) -> {
			int mode = getLastSelectedMode();
			if (mode == MODE_PEN) {
				controller.setLastPenColor(color);
			} else if (mode == MODE_HIGHLIGHTER) {
				controller.setLastHighlighterColor(color);
			}
			controller.updatePenColor(color);
			sliderComponent.update(mode);
		});
		addContent(colorChooser);

		sliderComponent = new ComponentSlider((AppW) app);
		addContent(sliderComponent);

		colorChooser.updateColorSelection(PenColorValues.BLACK.getColor());
	}

	/**
	 * update color palette
	 */
	public void update() {
		int mode = getLastSelectedMode();
		if (mode == MODE_PEN) {
			colorChooser.updateColorSelection(controller.getLastPenColor());
		} else if (mode == MODE_HIGHLIGHTER) {
			colorChooser.updateColorSelection(controller.getLastHighlighterColor());
		}
		colorChooser.setDisabled(mode == MODE_ERASER);
		sliderComponent.update(mode);
		controller.getPen().updateMode();
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		controller.getPen().updateMode();
	}
}
