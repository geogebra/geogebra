package org.geogebra.web.full.cas.view;

import java.util.ArrayList;
import java.util.List;

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
import org.geogebra.web.full.gui.util.PopupMenuHandler;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

/**
 * StyleBar for CASview
 */
public class CASStylebarW extends StyleBarW implements FastClickHandler,
        PopupMenuHandler {
	ToggleButton btnUseAsText;
	ToggleButton btnBold;
	ToggleButton btnItalic;
	private ColorPopupMenuButton btnTextColor;

	private boolean needUndo = false;
	private ArrayList<GeoElement> selectedRows;
	private CASViewW casView;
	private PopupMenuButtonW[] popupBtnList;
	private ToggleButton[] toggleBtnList;
	private Localization loc;

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
		if (!app.isUnbundledOrWhiteboard()) {
			addViewButton();
		}
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
		btnUseAsText.addFastClickHandler(this);
		btnUseAsText.addStyleName("btnUseAsText");

		btnTextColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = geos.get(0)
					        .getGeoElementForPropertiesDialog();
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
		btnTextColor.addPopupHandler(this);

		btnBold = new ToggleButton(MaterialDesignResources.INSTANCE.text_bold_black()) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = geos.get(0)
					        .getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setSelected(style == GFont.BOLD
							|| style == (GFont.BOLD + GFont.ITALIC));
				}
			}
		};
		btnBold.addFastClickHandler(this);
		btnBold.addStyleName("btnBold");

		btnItalic = new ToggleButton(MaterialDesignResources.INSTANCE.text_italic_black()) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = geos.get(0)
					        .getGeoElementForPropertiesDialog();
					int style = ((GeoCasCell) geo).getGeoText().getFontStyle();
					btnItalic.setSelected(style == GFont.ITALIC
							|| style == (GFont.BOLD + GFont.ITALIC));
				}
			}
		};
		btnItalic.addFastClickHandler(this);
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

	@Override
	public void onClick(Widget source) {
		needUndo = false;
		processSource(source, selectedRows);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}
	}

	private void processSource(Object source, ArrayList<GeoElement> targetGeos) {
		if (source == btnBold) {
			applyFontStyle(targetGeos);
		} else if (source == btnItalic) {
			applyFontStyle(targetGeos);
		} else if (source == btnUseAsText) {
			int i = casView.getConsoleTable().getEditingRow();
			applyUseAsText(targetGeos);
			if (i > 0) {
				casView.getConsoleTable().startEditingRow(i);
			}
		}
		updateStyleBar();
	}

	private void applyFontStyle(ArrayList<GeoElement> geos) {
		int fontStyle = 0;
		if (btnBold.isSelected()) {
			fontStyle += 1;
		}
		if (btnItalic.isSelected()) {
			fontStyle += 2;
		}
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell
			        && ((GeoCasCell) geo).getGeoText().getFontStyle() != fontStyle) {
				((GeoCasCell) geo).getGeoText().setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	/**
	 * @param geo - selected cell
	 */
	public void setSelectedRow(GeoElement geo) {
		selectedRows.clear();
		selectedRows.add(geo);
		updateStyleBar();
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

	private void applyTextColor(ArrayList<GeoElement> geos) {

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
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
	}

	private void applyUseAsText(ArrayList<GeoElement> geos) {
		casView.getConsoleTable().stopEditing();
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell) {
				((GeoCasCell) geo).setUseAsText(btnUseAsText.isSelected());
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	@Override
	public void fireActionPerformed(PopupMenuButtonW actionButton) {
		if (actionButton == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				applyTextColor(selectedRows);
			}
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
