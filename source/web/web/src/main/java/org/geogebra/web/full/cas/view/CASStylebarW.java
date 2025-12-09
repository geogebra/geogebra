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

package org.geogebra.web.full.cas.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;

/**
 * StyleBar for CASview
 */
public class CASStylebarW extends StyleBarW {
	ToggleButton btnUseAsText;
	ToggleButton btnBold;
	ToggleButton btnItalic;
	private ColorPopupMenuButton btnTextColor;

	private final ArrayList<GeoElement> selectedRows;
	private final CASViewW casView;
	private PopupMenuButtonW[] popupBtnList;
	private ToggleButton[] toggleBtnList;
	private final Localization loc;

	/**
	 * @param view
	 *            {@link CASViewW}
	 * @param app
	 *            {@link AppW}
	 */
	public CASStylebarW(CASViewW view, AppW app) {
		super(app, App.VIEW_CAS);
		this.loc = app.getLocalization();
		this.casView = view;
		this.selectedRows = new ArrayList<>();
		initGUI();
		setTooltips();
		addStyleName("CASStyleBar");
		optionType = OptionType.CAS;
	}

	private void initGUI() {
		createTextButtons();
		add(btnUseAsText);
		add(btnTextColor);
		add(btnBold);
		add(btnItalic);
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
		addMenuButton();
		addViewButton();

		updateStyleBar();
	}

	private void createTextButtons() {
		btnUseAsText = new ToggleButton(MaterialDesignResources.INSTANCE.text()) {

			@Override
			public void update(List<GeoElement> geos) {
				setVisible(true);
				btnUseAsText.setSelected(checkGeoText(geos));
			}
		};
		addFastClickHandler(btnUseAsText, this::applyUseAsTextKeepEditing);
		btnUseAsText.addStyleName("btnUseAsText");

		btnTextColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = geos.get(0).getGeoElementForPropertiesDialog();
					GColor geoColor = ((GeoCasCell) geo).getFontColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return new ImageOrText(
						MaterialDesignResources.INSTANCE.text_color(), 24);
			}
		};
		btnTextColor.setEnableTable(true);
		btnTextColor.addPopupHandler(this::applyTextColor);

		btnBold = new ToggleButton(MaterialDesignResources.INSTANCE.text_bold_black()) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = geos.get(0).getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setSelected(style == GFont.BOLD
							|| style == (GFont.BOLD + GFont.ITALIC));
				}
			}
		};
		addFastClickHandler(btnBold, this::applyFontStyle);
		btnBold.addStyleName("btnBold");

		btnItalic = new ToggleButton(MaterialDesignResources.INSTANCE.text_italic_black()) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = geos.get(0).getGeoElementForPropertiesDialog();
					int style = ((GeoCasCell) geo).getGeoText().getFontStyle();
					btnItalic.setSelected(style == GFont.ITALIC
							|| style == (GFont.BOLD + GFont.ITALIC));
				}
			}
		};
		addFastClickHandler(btnItalic, this::applyFontStyle);
		btnItalic.addStyleName("btnItalic");
	}

	/**
	 * @param geos
	 *            list of selected cells
	 * @return whether all given objects are cells in text mode
	 */
	private static boolean checkGeoText(List<GeoElement> geos) {
		boolean geosOK = geos.size() > 0;
		for (GeoElement geo : geos) {
			if (!(geo.getGeoElementForPropertiesDialog() instanceof GeoCasCell)) {
				geosOK = false;
				break;
			} else if (!((GeoCasCell) geo).isUseAsText()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	@Override
	public int getOffsetHeight() {
		return 0;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// nothing to do here
	}

	private void addFastClickHandler(ToggleButton source, Supplier<Boolean> action) {
		source.addFastClickHandler(w -> {
			boolean needUndo = action.get();
			updateStyleBar();
			if (needUndo) {
				app.storeUndoInfo();
			}
		});
	}

	private boolean applyFontStyle() {
		int fontStyle = 0;
		if (btnBold.isSelected()) {
			fontStyle += 1;
		}
		if (btnItalic.isSelected()) {
			fontStyle += 2;
		}
		boolean needUndo = false;
		for (GeoElement geo : selectedRows) {
			if (geo instanceof GeoCasCell
					&& ((GeoCasCell) geo).getGeoText().getFontStyle() != fontStyle) {
				((GeoCasCell) geo).getGeoText().setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
		return needUndo;
	}

	/**
	 * @param geo - selected cell
	 */
	public void setSelectedRow(GeoElement geo) {
		if (geo != null) {
			selectedRows.clear();
			selectedRows.add(geo);
			updateStyleBar();
		}
	}

	private void updateStyleBar() {
		for (PopupMenuButtonW popupButton : popupBtnList) {
			try {
				popupButton.update(selectedRows);
			} catch (Exception e) {
				Log.debug(e);
			}
		}
		for (ToggleButton toggleButton : toggleBtnList) {
			try {
				toggleButton.update(selectedRows);
			} catch (Exception e) {
				Log.debug(e);
			}
		}
	}

	/**
	 * @return array of toggle buttons
	 */
	private ToggleButton[] newToggleBtnList() {
		return new ToggleButton[] { btnBold, btnItalic, btnUseAsText };
	}

	/**
	 * @return array of popup buttons
	 */
	private PopupMenuButtonW[] newPopupBtnList() {
		return new PopupMenuButtonW[] { btnTextColor };
	}

	private void applyTextColor() {
		boolean needUndo = false;
		for (GeoElement geo : selectedRows) {
			if (geo instanceof GeoCasCell) {
				GColor color = btnTextColor.getSelectedColor();
				if (color == null) {
					app.getSelectionManager().addSelectedGeo(geo);
					((GuiManagerW) app.getGuiManager()).getPropertiesView(
							OptionType.OBJECTS).setOptionPanel(
							OptionType.OBJECTS, 3);
					app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
					return;
				}
				((GeoCasCell) geo).setFontColor(color);
				geo.updateRepaint();
				needUndo = true;
			}
		}
		if (needUndo) {
			app.storeUndoInfo();
		}
	}

	private boolean applyUseAsTextKeepEditing() {
		int editingRow = casView.getConsoleTable().getEditingRow();
		boolean ret = applyUseAsText(selectedRows);
		if (editingRow > 0) {
			casView.getConsoleTable().startEditingRow(editingRow);
		}
		return ret;
	}

	private boolean applyUseAsText(ArrayList<GeoElement> geos) {
		casView.getConsoleTable().stopEditing();
		boolean needUndo = false;
		for (GeoElement geo : geos) {
			if (geo instanceof GeoCasCell) {
				((GeoCasCell) geo).setUseAsText(btnUseAsText.isSelected());
				geo.updateRepaint();
				needUndo = true;
			}
		}
		return needUndo;
	}

	private void applyTextColor(int index) {
		if (index >= 0) {
			applyTextColor();
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		setTooltips();
	}

	private void setTooltips() {
		btnUseAsText.setTitle(loc.getMenu("CasCellUseAsText"));
		btnBold.setTitle(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setTitle(loc.getPlainTooltip("stylebar.Italic"));
		btnTextColor.setTitle(loc.getPlainTooltip("stylebar.TextColor"));
	}
}
