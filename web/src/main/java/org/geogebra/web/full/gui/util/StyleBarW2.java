package org.geogebra.web.full.gui.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.statistics.GeoPieChart;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
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
	protected final InlineTextFormatter inlineFormatter;
	public int mode = -1;

	/**
	 * @param app
	 *            application
	 * @param viewID
	 *            parent view ID
	 */
	public StyleBarW2(AppW app, int viewID) {
		super(app, viewID);
		inlineFormatter = new InlineTextFormatter();
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
						new PointStyleModel(app));

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
	protected void openColorChooser(ArrayList<GeoElement> targetGeos, boolean background) {
		if (app.isWhiteboardActive()) {
			openColorDialogForWhiteboard(targetGeos, background);
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
						alpha, app, targetGeos);
			}
		} else if (source == btnLineStyle) {
			if (btnLineStyle.getSelectedValue() != null) {
				int selectedIndex = btnLineStyle.getSelectedIndex();
				int lineSize = btnLineStyle.getSliderValue();
				btnLineStyle.setSelectedIndex(selectedIndex);
				needUndo = EuclidianStyleBarStatic.applyLineStyle(selectedIndex,
						lineSize, app, targetGeos);
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

	protected void openColorDialogForWhiteboard(final ArrayList<GeoElement> targetGeos,
												final boolean background) {
		final GeoElement geo0 = targetGeos.get(0);
		DialogManagerW dm = (DialogManagerW) (app.getDialogManager());

		GColor originalColor;
		if (background) {
			originalColor = geo0.getBackgroundColor();
		} else {
			originalColor = geo0.getObjectColor();
		}

		dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {

			@Override
			public void onForegroundSelected() {
				// no foreground/background switcher
			}

			@Override
			public void onColorChange(GColor color) {
				boolean changed;
				if (background) {
					changed = EuclidianStyleBarStatic.applyBgColor(targetGeos, color,
								geo0.getAlphaValue());
				} else {
					changed = applyColor(targetGeos, color, geo0.getAlphaValue());
				}
				if (changed) {
					app.storeUndoInfo();
				}
			}

			@Override
			public void onClearBackground() {
				// no clear background button
			}

			@Override
			public void onBarSelected() {
				// no bar chart support
			}

			@Override
			public void onBackgroundSelected() {
				// no foreground / background switcher
			}

			@Override
			public void onAlphaChange() {
				// no alpha slider
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

	protected boolean applyColor(ArrayList<GeoElement> targetGeos, GColor color,
			double alpha) {
		boolean ret = EuclidianStyleBarStatic.applyColor(color,
				alpha, app, targetGeos);
		String htmlColor = StringUtil.toHtmlColor(color);
		return inlineFormatter.formatInlineText(targetGeos, "color", htmlColor)
				|| ret;
	}

	protected abstract void handleEventHandlers(Object source);

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
					boolean geosOK = (geos.size() > 0
							|| EuclidianView.isPenMode(mode));
					boolean hasOpacity = true;
					for (GeoElement geoElement : geos) {
						GeoElement geo = geoElement
								.getGeoElementForPropertiesDialog();
						if (hasTextColor(geo) || geo instanceof GeoWidget
								|| geo instanceof GeoPieChart) {
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
						geoColor = geos.size() > 0
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

						if (!app.isUnbundled()) {
							if (hasFillable) {
								if (app.isWhiteboardActive()
										&& geos.get(0) instanceof GeoImage) {
									if (hasOpacity) {
										setTitle(loc.getMenu("Opacity"));
									} else {
										super.setVisible(false);
									}
								} else {
									setTitle(loc.getMenu(
											"stylebar.ColorTransparency"));
								}
							} else {
								setTitle(loc.getMenu("stylebar.Color"));
							}
						}

						setSliderVisible(hasFillable && hasOpacity);

						if (EuclidianView.isPenMode(mode)) {
							setSliderValue(
									(int) Math.round((alpha * 100) / 255));
						} else {
							setSliderValue((int) Math.round(alpha * 100));
						}

						updateColorTable();
						setEnableTable(geos.size() > 0
								&& !(geos.get(0) instanceof GeoImage));
						// find the geoColor in the table and select it
						int index = this.getColorIndex(geoColor);
						setSelectedIndex(index);
						if (EuclidianView.isPenMode(mode)) {
							setDefaultColor(alpha / 255, geoColor);
						} else {
							setDefaultColor(alpha, geoColor);
						}

						this.setKeepVisible(!app.isUnbundledOrWhiteboard()
								&& EuclidianConstants
								.isMoveOrSelectionMode(mode));
					}
				}
			}

			@Override
			public void onClickAction() {
				onColorClicked();
			}
		};
		btnColor.addPopupHandler(this);
	}

	public boolean hasTextColor(GeoElement geoElement) {
		return geoElement instanceof TextStyle || geoElement instanceof GeoFormula;
	}

	protected void onColorClicked() {
		// only in EV
	}
}
