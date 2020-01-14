package org.geogebra.web.full.gui.util;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.euclidian.EuclidianLineStylePopup;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.dialog.options.OptionsTab.ColorPanel;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.html5.main.AppW;

/**
 * a version of StyleBarW that also includes the buttons for color, line style
 * and point style and (parts of) their handling.
 */
public abstract class StyleBarW2 extends StyleBarW implements PopupMenuHandler {

	protected ColorPopupMenuButton btnColor;
	protected EuclidianLineStylePopup btnLineStyle;
	protected PointStylePopup btnPointStyle;

	protected boolean needUndo = false;

	public StyleBarW2(AppW app, int viewID) {
		super(app, viewID);
	}

	protected void createLineStyleBtn() {
		btnLineStyle = app.isWhiteboardActive()
				? new MOWLineStyleButton(app)
				: new EuclidianLineStylePopup(app, 5, true);
		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider()
				.setMaximum(app.isWhiteboardActive()
						? 2 * EuclidianConstants.MAX_PEN_HIGHLIGHTER_SIZE : 13);
		btnLineStyle.getMySlider().setTickSpacing(1);
		btnLineStyle.addPopupHandler(this);
	}

	protected void createPointStyleBtn(int mode) {
		btnPointStyle = app.isWhiteboardActive()
				? MOWPointStyleButton.create(app)
				: PointStylePopup.create(app, mode, true,
						new PointStyleModel(app), false);

		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider().setMaximum(9);
		btnPointStyle.getMySlider().setTickSpacing(1);

		btnPointStyle.addPopupHandler(this);

	}

	/**
	 * Opens color chooser dialog in MOW or properties view elsewhere.
	 * 
	 * @param targetGeos
	 *            The geos color needs to be set.
	 */
	protected void openColorChooser(ArrayList<GeoElement> targetGeos,
			boolean background) {
		if (app.isWhiteboardActive()) {
			openColorDialog(targetGeos, background);
		} else {
			openPropertiesForColor(background);
		}
	}

	/**
	 * process the action performed
	 * 
	 * @param source
	 *            event source
	 * @param targetGeos
	 *            selected objects
	 * @return processed successfully
	 */
	protected boolean processSource(Object source,
			ArrayList<GeoElement> targetGeos) {
		if (source == btnColor) {
			GColor color = btnColor.getSelectedColor();
			if (color == null && !(targetGeos.get(0) instanceof GeoImage)) {
				openColorChooser(targetGeos, false);
			} else {
				double alpha = btnColor.getSliderValue() / 100.0;
				needUndo = EuclidianStyleBarStatic.applyColor(color,
						alpha, app);
			}
		} else if (source == btnLineStyle) {
			if (btnLineStyle.getSelectedValue() != null) {
				int selectedIndex = btnLineStyle.getSelectedIndex();
				int lineSize = btnLineStyle.getSliderValue();
				needUndo = EuclidianStyleBarStatic.applyLineStyle(selectedIndex, lineSize, app);
			}
		} else if (source == btnPointStyle) {
			if (btnPointStyle.getSelectedValue() != null) {
				int pointStyleSelIndex = btnPointStyle.getSelectedIndex();
				int pointSize = btnPointStyle.getSliderValue();
				needUndo = EuclidianStyleBarStatic.applyPointStyle(targetGeos,
						pointStyleSelIndex, pointSize);
			}
		} else {
			return false;
		}
		return true;
	}

	protected void openPropertiesForColor(boolean background) {
		((GuiManagerW) app.getGuiManager())
				.getPropertiesView(OptionType.OBJECTS)
				.setOptionPanel(OptionType.OBJECTS, 3);
		if (app.isUnbundledOrWhiteboard()) {
			((PropertiesViewW) app.getGuiManager().getPropertiesView()).open();
		} else {
			app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
		}
		ColorPanel colorPanel = ((GuiManagerW) app.getGuiManager())
				.getColorPanel();
		if (colorPanel != null) {
			colorPanel.setBackground(background);
		}
	}

	protected void openColorDialog(final ArrayList<GeoElement> targetGeos,
			final boolean background) {
		if (!app.isWhiteboardActive()) {
			return;
		}

		final GeoElement geo0 = targetGeos.get(0);
		DialogManagerW dm = (DialogManagerW) (app.getDialogManager());

		GColor originalColor;
		if (app.isUnbundledOrWhiteboard() && background) {
			originalColor = geo0.getBackgroundColor();
		} else {
			originalColor = geo0.getObjectColor();
		}

		dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {

			@Override
			public void onForegroundSelected() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onColorChange(GColor color) {
				if (background) {
					if (app.isUnbundledOrWhiteboard()) {
						EuclidianStyleBarStatic.applyBgColor(targetGeos, color,
								geo0.getAlphaValue());
					}
					return;
				}
				EuclidianStyleBarStatic.applyColor(color,
						geo0.getAlphaValue(), app);
			}

			@Override
			public void onClearBackground() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBarSelected() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBackgroundSelected() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAlphaChange() {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @param actionButton
	 *            runs programatically the action performed event.
	 */
	@Override
	public void fireActionPerformed(PopupMenuButtonW actionButton) {
		handleEventHandlers(actionButton);
	}

	protected abstract void handleEventHandlers(Object source);

}
