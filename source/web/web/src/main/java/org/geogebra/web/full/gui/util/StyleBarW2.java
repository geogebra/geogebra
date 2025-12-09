/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.undo.UpdateStyleActionStore;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.euclidian.EuclidianLineStylePopup;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.dialog.options.OptionsTab.ColorPanel;
import org.geogebra.web.html5.main.AppW;

/**
 * a version of StyleBarW that also includes the buttons for color, line style
 * and point style and (parts of) their handling.
 */
public abstract class StyleBarW2 extends StyleBarW {

	protected ColorPopupMenuButton btnColor;
	protected EuclidianLineStylePopup btnLineStyle;
	protected PointStylePopup btnPointStyle;

	public int mode = -1;

	/**
	 * @param app
	 *            application
	 * @param viewID
	 *            parent view ID
	 */
	public StyleBarW2(AppW app, int viewID) {
		super(app, viewID);
	}

	protected void createLineStyleBtn() {
		btnLineStyle = new EuclidianLineStylePopup(app);
		btnLineStyle.getSlider().setMinimum(1);
		btnLineStyle.getSlider().setMaximum(13);
		btnLineStyle.getSlider().setTickSpacing(1);
		setPopupHandlerWithUndoAction(btnLineStyle, this::processLineStyle);
	}

	protected void setPopupHandlerWithUndoAction(PopupMenuButtonW popupBtn,
			ElementPropertySetter action) {
		popupBtn.addPopupHandler(w -> processSelectionWithUndoAction(action));
		// no undo in slider handler
		UndoableSliderHandler ush = new UndoableSliderHandler(action, this);
		popupBtn.setChangeEventHandler(ush);
	}

	protected void setPopupHandlerWithUndoPoint(PopupMenuButtonW popupBtn,
			Function<ArrayList<GeoElement>, Boolean> action) {
		popupBtn.addPopupHandler(w -> processSelectionWithUndo(action));
	}

	protected void createPointStyleBtn(int mode) {
		btnPointStyle = PointStylePopup.create(app, mode, true);

		btnPointStyle.getSlider().setMinimum(1);
		btnPointStyle.getSlider().setMaximum(9);
		btnPointStyle.getSlider().setTickSpacing(1);

		setPopupHandlerWithUndoAction(btnPointStyle, this::processPointStyle);
	}

	/**
	 * Opens color chooser dialog in MOW or properties view elsewhere.
	 */
	protected void openColorChooser(boolean background) {
		openPropertiesForColor(background);
	}

	private boolean processPointStyle(List<GeoElement> targetGeos) {
		if (btnPointStyle.getSelectedValue() != null) {
			int pointStyleSelIndex = btnPointStyle.getSelectedIndex();
			int pointSize = btnPointStyle.getSliderValue();
			return EuclidianStyleBarStatic.applyPointStyle(targetGeos,
					pointStyleSelIndex, pointSize);
		}
		return false;
	}

	private boolean processLineStyle(List<GeoElement> targetGeos) {
		if (btnLineStyle.getSelectedValue() != null) {
			int selectedIndex = btnLineStyle.getSelectedIndex();
			int lineSize = btnLineStyle.getSliderValue();
			btnLineStyle.setSelectedIndex(selectedIndex);
			return EuclidianStyleBarStatic.applyLineStyle(selectedIndex, lineSize, app,
					targetGeos);
		}
		return false;
	}

	private boolean processColor(List<GeoElement> targetGeos) {
		GColor color = btnColor.getSelectedColor();
		if (color == null && !(targetGeos.get(0) instanceof GeoImage)) {
			openColorChooser(false);
		} else {
			double alpha = btnColor.getSliderValue() / 100.0;
			return EuclidianStyleBarStatic.applyColor(color,
					alpha, app, targetGeos);
		}
		return false;
	}

	protected void openPropertiesForColor(boolean background) {
		((GuiManagerW) app.getGuiManager())
				.getPropertiesView(OptionType.OBJECTS)
				.setOptionPanel(OptionType.OBJECTS, 3);
		app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);

		ColorPanel colorPanel = ((GuiManagerW) app.getGuiManager())
				.getColorPanel();
		if (colorPanel != null) {
			colorPanel.setBackground(background);
		}
	}

	/**
	 * Process selected geos and create undo checkpoint if necessary
	 * @param action action to be executed on geos
	 */
	public void processSelectionWithUndo(Function<ArrayList<GeoElement>, Boolean> action) {
		boolean needUndo = action.apply(getTargetGeos());
		if (needUndo) {
			app.storeUndoInfo();
		}
	}

	/**
	 * Process selected geos and create undoable action if necessary
	 * @param action action to be executed on geos
	 */
	public void processSelectionWithUndoAction(ElementPropertySetter action) {
		UpdateStyleActionStore store = new UpdateStyleActionStore(getTargetGeos(),
				app.getUndoManager());
		boolean needUndo = action.apply(getTargetGeos()) && store.needUndo();
		if (needUndo) {
			store.storeUndo();
		}
	}

	protected abstract ArrayList<GeoElement> getTargetGeos();

	protected boolean applyColor(List<GeoElement> targetGeos, GColor color,
			double alpha) {
		return EuclidianStyleBarStatic.applyColor(color,
				alpha, app, targetGeos);
	}

	protected void createColorBtn() {
		Localization loc = app.getLocalization();
		btnColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, true) {

			@Override
			public void update(List<GeoElement> geos) {
				if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE) {
					super.setVisible(false);
					Log.debug(
							"MODE_FREEHAND_SHAPE not working in StyleBar yet");
				} else {
					boolean geosOK = !geos.isEmpty()
							|| EuclidianView.isPenMode(mode);
					boolean hasOpacity = true;
					for (GeoElement geoElement : geos) {
						GeoElement geo = geoElement
								.getGeoElementForPropertiesDialog();
						if (geo instanceof TextStyle || geo instanceof GeoWidget) {
							geosOK = false;
							break;
						}
						if (geoElement instanceof GeoLocusStroke) {
							hasOpacity = false;
						}
					}

					super.setVisible(geosOK);
					if (geosOK) {
						// get color from first geo
						GColor geoColor;
						geoColor = !geos.isEmpty()
								? geos.get(0).getObjectColor()
								: GColor.BLACK;
						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha
						// value
						double alpha = 1.0;
						boolean hasFillable = false;
						for (GeoElement geo : geos) {
							if (geo.isFillable()) {
								hasFillable = true;
								alpha = geo.getAlphaValue();
								break;
							}
							if (geo instanceof GeoPolyLine
									&& EuclidianView.isPenMode(mode)) {
								hasFillable = true;
								alpha = geo.getLineOpacity();

								break;
							}
						}

						if (hasFillable) {
							if (geos.get(0) instanceof GeoImage) {
								if (hasOpacity) {
									setTitle(loc.getMenu("Opacity"));
								} else {
									super.setVisible(false);
								}
							} else {
								setTitle(loc.getMenu("stylebar.ColorTransparency"));
							}
						} else {
							setTitle(loc.getMenu("stylebar.Color"));
						}

						setSliderVisible(hasFillable && hasOpacity);

						if (EuclidianView.isPenMode(mode)) {
							setSliderValue(
									(int) Math.round(alpha * 100 / 255));
						} else {
							setSliderValue((int) Math.round(alpha * 100));
						}

						updateColorTable();
						setEnableTable(!geos.isEmpty()
								&& !(geos.get(0) instanceof GeoImage));
						// find the geoColor in the table and select it
						int index = this.getColorIndex(geoColor);
						setSelectedIndex(index);
						if (EuclidianView.isPenMode(mode)) {
							setDefaultColor(alpha / 255, geoColor);
						} else {
							setDefaultColor(alpha, geoColor);
						}

						this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
					}
				}
			}
		};
		setPopupHandlerWithUndoAction(btnColor, this::processColor);
	}

	/**
	 * @param geoElement element
	 * @return whether element has text color setting
	 */
	public boolean hasTextColor(GeoElement geoElement) {
		return geoElement instanceof TextStyle;
	}
}
