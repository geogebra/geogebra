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
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.PopupMenuHandler;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * StyleBar for CASview
 */
public class CASStylebarW extends StyleBarW implements ClickHandler,
        PopupMenuHandler {

	/** button to set input as text */
	MyToggleButtonW btnUseAsText;
	/** button to set text to bold */
	MyToggleButtonW btnBold;
	/** button to set text to italic */
	MyToggleButtonW btnItalic;
	/** button to set the text color */
	private ColorPopupMenuButton btnTextColor;

	private boolean needUndo = false;
	private ArrayList<GeoElement> selectedRows;
	private CASViewW casView;
	private PopupMenuButtonW[] popupBtnList;
	private MyToggleButtonW[] toggleBtnList;
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
		btnUseAsText = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.text(), 24)) {

			@Override
			public void update(List<GeoElement> geos) {
				setVisible(true);
				btnUseAsText.setSelected(checkGeoText(geos));
			}
		};
		btnUseAsText.addClickHandler(this);
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
					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return new ImageOrText(
						MaterialDesignResources.INSTANCE.text_color(), 24);
			}
		};
		btnTextColor.setEnableTable(true);
		btnTextColor.addActionListener(this);
		btnTextColor.addPopupHandler(this);

		btnBold = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.text_bold_black(), 24)) {

			@Override
			public void update(List<GeoElement> geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = geos.get(0)
					        .getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setValue(style == GFont.BOLD
							|| style == (GFont.BOLD + GFont.ITALIC));
				}
			}
		};
		btnBold.addClickHandler(this);
		btnBold.addStyleName("btnBold");

		btnItalic = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.text_italic_black(), 24)) {

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
		btnItalic.addClickHandler(this);
		btnItalic.addStyleName("btnItalic");
	}

	/**
	 * @param geos
	 *            list of selected cells
	 * @return whether all given objects are cells in text mode
	 */
	private static boolean checkGeoText(List<GeoElement> geos) {
		boolean geosOK = (geos.size() > 0);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(ClickEvent e) {
		Object source = e.getSource();

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
	 * 
	 * @param geo
	 *            selected cell
	 */
	public void setSelectedRow(GeoElement geo) {
		selectedRows.clear();
		selectedRows.add(geo);
		updateStyleBar();
	}

	private void updateStyleBar() {
		for (int i = 0; i < popupBtnList.length; i++) {
			try {
				popupBtnList[i].update(selectedRows);
			} catch (Exception e) {
				// TODO: find problem
			}
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			try {
				toggleBtnList[i].update(selectedRows);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: find problem
			}
		}
	}

	/**
	 * @return array of toggle buttons
	 */
	private MyToggleButtonW[] newToggleBtnList() {
		return new MyToggleButtonW[] { btnBold, btnItalic, btnUseAsText };
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
		// btnUseAsText
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
		btnUseAsText.setToolTipText(loc.getMenu("CasCellUseAsText"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
		btnTextColor.setToolTipText(loc.getPlainTooltip("stylebar.TextColor"));
	}
}
