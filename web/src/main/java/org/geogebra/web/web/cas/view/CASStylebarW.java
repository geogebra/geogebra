package org.geogebra.web.web.cas.view;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.color.ColorPopupMenuButton;
import org.geogebra.web.web.gui.util.GeoGebraIcon;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.util.StyleBarW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * StyleBar for CASview
 */
public class CASStylebarW extends StyleBarW implements ClickHandler,
        PopupMenuHandler {


	/** button to set input as text */
	MyToggleButton2 btnUseAsText;
	/** button to set text to bold */
	MyToggleButton2 btnBold;
	/** button to set text to italic */
	MyToggleButton2 btnItalic;
	/** button to set the text color */
	private ColorPopupMenuButton btnTextColor;

	private boolean needUndo = false;
	private ArrayList<GeoElement> selectedRows;
	private CASViewW casView;
	private PopupMenuButton[] popupBtnList;
	private MyToggleButton2[] toggleBtnList;

	/**
	 * @param view
	 *            {@link CASViewW}
	 * @param app
	 *            {@link AppW}
	 */
	public CASStylebarW(CASViewW view, AppW app) {
		super(app, App.VIEW_CAS);
		this.casView = view;
		this.selectedRows = new ArrayList<GeoElement>();
		initGUI();
		setTooltips();
		addStyleName("CASStyleBar");
	}

	private void initGUI() {
		createTextButtons();
		add(btnUseAsText);
		add(btnTextColor);
		add(btnBold);
		add(btnItalic);
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
		addViewButton();
		updateStyleBar();
	}

	private void createTextButtons() {

		btnUseAsText = new MyToggleButton2(app.getPlain("Text").substring(0, 1)) {

			@Override
			public void update(Object[] geos) {
				setVisible(true);
				btnUseAsText.setSelected(checkGeoText(geos));
			}
		};
		btnUseAsText.addClickHandler(this);
		btnUseAsText.addStyleName("btnUseAsText");

		final GDimensionW textColorIconSize = new GDimensionW(20, ICON_HEIGHT);
		btnTextColor = new ColorPopupMenuButton(app, textColorIconSize,
		        ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
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
				return GeoGebraIcon.createTextSymbolIcon("A",
						getSelectedColor(), null);
			}
		};
		btnTextColor.setEnableTable(true);
		btnTextColor.addActionListener(this);
		btnTextColor.addPopupHandler(this);

		btnBold = new MyToggleButton2(app.getMenu("Bold.Short")) {

			@Override
			public void update(Object[] geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
					        .getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setValue(style == GFontW.BOLD
					        || style == (GFontW.BOLD + org.geogebra.common.awt.GFont.ITALIC));
				}
			}
		};
		btnBold.addClickHandler(this);
		btnBold.addStyleName("btnBold");

		btnItalic = new MyToggleButton2(app.getMenu("Italic.Short")) {

			@Override
			public void update(Object[] geos) {
				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
					        .getGeoElementForPropertiesDialog();
					int style = ((GeoCasCell) geo).getGeoText().getFontStyle();
					btnItalic.setSelected(style == GFontW.ITALIC
					        || style == (GFontW.BOLD + GFontW.ITALIC));
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
	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i]).getGeoElementForPropertiesDialog() instanceof GeoCasCell)) {
				geosOK = false;
				break;
			} else if (!((GeoCasCell) geos[i]).isUseAsText()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	public int getOffsetHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub

	}

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
			if (i > 0)
				casView.getConsoleTable().startEditingRow(i);
		}
		updateStyleBar();
	}

	private void applyFontStyle(ArrayList<GeoElement> geos) {
		int fontStyle = 0;
		if (btnBold.isSelected())
			fontStyle += 1;
		if (btnItalic.isSelected())
			fontStyle += 2;
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
				popupBtnList[i].update(selectedRows.toArray());
			} catch (Exception e) {
				// TODO: find problem
			}
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			try {
				toggleBtnList[i].update(selectedRows.toArray());
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: find problem
			}
		}
	}

	/**
	 * @return array of toggle buttons
	 */
	private MyToggleButton2[] newToggleBtnList() {
		return new MyToggleButton2[] { btnBold, btnItalic, btnUseAsText };
	}

	/**
	 * @return array of popup buttons
	 */
	private PopupMenuButton[] newPopupBtnList() {
		return new PopupMenuButton[] { btnTextColor };
	}

	private void applyTextColor(ArrayList<GeoElement> geos) {

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell) {
				btnTextColor.getSelectedColor();
				((GeoCasCell) geo)
				        .setFontColor(btnTextColor.getSelectedColor());
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

	public void fireActionPerformed(PopupMenuButton actionButton) {
		if (actionButton == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				applyTextColor(selectedRows);
			}
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		// with button.setText("...")the text is only set for the current face
		this.btnUseAsText.getDownFace().setText(
		        app.getPlain("Text").substring(0, 1));
		this.btnBold.getDownFace().setText(app.getMenu("Bold.Short"));
		this.btnItalic.getDownFace().setText(app.getMenu("Italic.Short"));
		this.btnUseAsText.getUpFace().setText(
		        app.getPlain("Text").substring(0, 1));
		this.btnBold.getUpFace().setText(app.getMenu("Bold.Short"));
		this.btnItalic.getUpFace().setText(app.getMenu("Italic.Short"));
		setTooltips();
	}

	private void setTooltips() {
		Localization loc = app.getLocalization();
		btnUseAsText.setToolTipText(app.getMenu("CasCellUseAsText"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
		btnTextColor.setToolTipText(loc.getPlainTooltip("stylebar.TextColor"));
	}
}
