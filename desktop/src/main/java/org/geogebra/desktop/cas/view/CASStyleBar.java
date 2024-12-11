package org.geogebra.desktop.cas.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.color.ColorPopupMenuButton;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.ToggleButtonD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Stylebar for CAS
 */
public class CASStyleBar extends JToolBar implements ActionListener {
	/** */
	private static final long serialVersionUID = 1L;
	/** application */
	protected AppD app;
	/** CAS view */
	protected CASViewD casView;

	// buttons and lists of buttons
	private ToggleButtonD[] toggleBtnList;
	private PopupMenuButtonD[] popupBtnList;
	private ColorPopupMenuButton btnTextColor;
	private PopupMenuButtonD btnTextSize;
	/** button for bold text */
	protected ToggleButtonD btnBold;
	/** button for italic text */
	protected ToggleButtonD btnItalic;
	/** use as text button */
	protected ToggleButtonD btnUseAsText;
	/** use as text button */
	protected ToggleButtonD btnShowKeyboard;
	/** height of buttons */
	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	private boolean needUndo = false;

	private ArrayList<GeoElement> selectedRows;
	private LocalizationD loc;

	/**
	 * @param view
	 *            CAS view
	 * @param app
	 *            application
	 */
	public CASStyleBar(CASViewD view, AppD app) {

		this.app = app;
		this.loc = app.getLocalization();
		this.casView = view;

		selectedRows = new ArrayList<>();

		setFloatable(false);

		initGUI();
	}

	/**
	 * 
	 * @param targetGeos
	 *            list of selected cells
	 */
	public void setSelectedRows(ArrayList<GeoElement> targetGeos) {
		if (targetGeos != null) {
			selectedRows = targetGeos;
		}
		updateStyleBar();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		needUndo = false;

		processSource(source, selectedRows);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}

		// updateGUI();
	}

	/**
	 * @param source
	 *            event source
	 * @param targetGeos
	 *            cells that need updating
	 */
	protected void processSource(Object source,
			ArrayList<GeoElement> targetGeos) {

		if (source == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				applyTextColor(targetGeos);
				// btnTextColor.setFgColor((Color)btnTextColor.getSelectedValue());
				// btnItalic.setForeground((Color)btnTextColor.getSelectedValue());
				// btnBold.setForeground((Color)btnTextColor.getSelectedValue());
			}
		} else if (source == btnBold) {
			applyFontStyle(targetGeos);
		} else if (source == btnItalic) {
			applyFontStyle(targetGeos);
		} else if (source == btnTextSize) {
			applyTextSize(targetGeos);
		} else if (source == btnUseAsText) {
			int i = casView.getConsoleTable().getEditingRow();
			int pos = ((CASTableCellEditorD) casView.getConsoleTable()
					.getCellEditor(i, CASTableD.COL_CAS_CELLS))
							.getCaretPosition();
			applyUseAsText(targetGeos);
			casView.getConsoleTable().startEditingRow(i);
			((CASTableCellEditorD) casView.getConsoleTable().getCellEditor(i,
					CASTableD.COL_CAS_CELLS)).setCaretPosition(pos);
		} else if (source == btnShowKeyboard) {
			if ((app.getGuiManager()) != null) {
				if (AppD.isVirtualKeyboardActive()
						&& !((GuiManagerD) app.getGuiManager())
								.showVirtualKeyboard()) {

					// if keyboard is active but hidden, just show it
					((GuiManagerD) app.getGuiManager()).toggleKeyboard(true);
					((GuiManagerD) app.getGuiManager()).getVirtualKeyboard()
							.toggleNumeric(true);

				} else {

					AppD.setVirtualKeyboardActive(
							!AppD.isVirtualKeyboardActive());
					((GuiManagerD) app.getGuiManager())
							.toggleKeyboard(AppD.isVirtualKeyboardActive());
					((GuiManagerD) app.getGuiManager()).getVirtualKeyboard()
							.toggleNumeric(AppD.isVirtualKeyboardActive());
				}
			}
		}
		updateStyleBar();
	}

	/**
	 * Updates the stylebar
	 */
	public void updateStyleBar() {

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

	private void applyTextColor(ArrayList<GeoElement> geos) {

		Color color = GColorD.getAwtColor(btnTextColor.getSelectedColor());
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell) {
				((GeoCasCell) geo).setFontColor(GColorD.newColor(color));
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyUseAsText(ArrayList<GeoElement> geos) {
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
			Log.debug(((GeoCasCell) geo).getGeoText());
			if (geo instanceof GeoCasCell && ((GeoCasCell) geo).getGeoText()
					.getFontStyle() != fontStyle) {
				((GeoCasCell) geo).getGeoText().setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyTextSize(ArrayList<GeoElement> geos) {

		double fontSize = GeoText
				.getRelativeFontSize(btnTextSize.getSelectedIndex()); // transform
																		// indices
																		// to
																		// the
																		// range
																		// -4,
																		// .. ,
																		// 4

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell && ((GeoCasCell) geo).getGeoText()
					.getFontSizeMultiplier() != fontSize) {
				((GeoCasCell) geo).setFontSizeMultiplier(fontSize);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	/**
	 * @return array of popup buttons
	 */
	protected PopupMenuButtonD[] newPopupBtnList() {
		return new PopupMenuButtonD[] { btnTextColor, btnTextSize };
	}

	/**
	 * @return array of toggle buttons
	 */
	protected ToggleButtonD[] newToggleBtnList() {
		return new ToggleButtonD[] { btnBold, btnItalic, btnUseAsText };
	}

	public void reinit() {
		initGUI();
	}

	private void initGUI() {

		removeAll();

		ImageIcon kbdIcon = app.getScaledIcon(GuiResourcesD.CAS_KEYBOARD);

		iconHeight = kbdIcon.getIconHeight();
		iconDimension = new Dimension(iconHeight, iconHeight);
		btnShowKeyboard = new ToggleButtonD(kbdIcon, iconHeight);

		createTextButtons();

		add(btnUseAsText);
		add(btnTextColor);
		add(btnBold);
		add(btnItalic);

		btnShowKeyboard.addActionListener(this);
		add(btnShowKeyboard);
		// add(btnTextSize); //TODO: Fix text size

		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();

		updateStyleBar();
	}

	// =====================================================
	// Text Format Buttons
	// =====================================================

	/**
	 * @param geos
	 *            list of selected cells
	 * @return whether all given objects are cells in text mode
	 */
	private static boolean checkGeoText(List<GeoElement> geos) {
		boolean geosOK = geos.size() > 0;
		for (int i = 0; i < geos.size(); i++) {
			if (!(geos.get(i) instanceof GeoCasCell)) {
				geosOK = false;
				break;
			} else if (!((GeoCasCell) geos.get(i)).isUseAsText()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	private void createTextButtons() {

		// ========================
		// text color button
		final Dimension textColoriconHeight = new Dimension(iconHeight,
				iconHeight);

		btnTextColor = new ColorPopupMenuButton(app, textColoriconHeight,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private static final long serialVersionUID = 1L;

			private GColor geoColor;

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos.get(0))
							.getGeoElementForPropertiesDialog();
					geoColor = ((GeoCasCell) geo).getFontColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}

					setFgColor(geoColor);
					// setFontStyle(((TextProperties) geo).getFontStyle());
				}
			}

			@Override
			public ImageIcon getButtonIcon() {
				return GeoGebraIconD.createTextSymbolIcon("A",
						app.getPlainFont(), textColoriconHeight,
						GColorD.getAwtColor(getSelectedColor()), null);
			}

		};

		btnTextColor.addActionListener(this);

		// ========================================
		// use as text button
		ImageIcon useAsTextIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Text").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnUseAsText = new ToggleButtonD(useAsTextIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(List<GeoElement> geos) {

				setVisible(true);
				btnUseAsText.setSelected(checkGeoText(geos));

			}
		};
		btnUseAsText.addActionListener(this);

		// ========================================
		// bold text button
		ImageIcon boldIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Bold").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnBold = new ToggleButtonD(boldIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos.get(0))
							.getGeoElementForPropertiesDialog();
					int style = ((GeoCasCell) geo).getGeoText().getFontStyle();
					btnBold.setSelected(style == Font.BOLD
							|| style == (Font.BOLD + Font.ITALIC));
				}
			}
		};
		btnBold.addActionListener(this);

		// ========================================
		// italic text button
		ImageIcon italicIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Italic").substring(0, 1), app.getPlainFont(),
				false, true, true, iconDimension, Color.black, null);
		btnItalic = new ToggleButtonD(italicIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				this.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos.get(0))
							.getGeoElementForPropertiesDialog();
					int style = ((GeoCasCell) geo).getGeoText().getFontStyle();
					btnItalic.setSelected(style == Font.ITALIC
							|| style == (Font.BOLD + Font.ITALIC));
				}
			}

		};
		btnItalic.addActionListener(this);

		// ========================================
		// text size button

		String[] textSizeArray = app.getLocalization().getFontSizeStrings();

		btnTextSize = new PopupMenuButtonD(app, textSizeArray, -1, 1,
				new Dimension(-1, iconHeight), SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(List<GeoElement> geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = geos.get(0);
					setSelectedIndex(GeoText.getFontSizeIndex(
							((GeoCasCell) geo).getFontSizeMultiplier())); // font
																			// size
																			// ranges
																			// from
					// -4 to 4, transform
					// this to 0,1,..,4
				}
			}
		};
		btnTextSize.addActionListener(this);
		btnTextSize.setKeepVisible(false);
	}

	/**
	 * Update localization
	 */
	public void setLabels() {
		initGUI();

		btnUseAsText.setToolTipText(loc.getPlainTooltip("stylebar.UseAsText"));
		btnTextColor.setToolTipText(loc.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(loc.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
	}

}
