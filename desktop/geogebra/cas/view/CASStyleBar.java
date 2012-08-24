package geogebra.cas.view;

import geogebra.common.cas.view.CASTable;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.color.ColorPopupMenuButton;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.MyToggleButton;
import geogebra.gui.util.PopupMenuButton;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

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
	private MyToggleButton[] toggleBtnList;
	private PopupMenuButton[] popupBtnList;
	private ColorPopupMenuButton btnTextColor;
	private PopupMenuButton btnTextSize;
	/** button for bold text */
	protected MyToggleButton btnBold;
	/** button for italic text */
	protected MyToggleButton btnItalic;
	/** use as text button */
	protected MyToggleButton btnUseAsText;
	/** use as text button */
	protected MyToggleButton btnShowKeyboard;
	/** height of buttons */
	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	private boolean needUndo = false;

	private ArrayList<GeoElement> selectedRows;

	/**
	 * @param view
	 *            CAS view
	 * @param app
	 *            application
	 */
	public CASStyleBar(CASViewD view, AppD app) {

		this.app = app;
		this.casView = view;

		selectedRows = new ArrayList<GeoElement>();

		setFloatable(false);

		initGUI();

	}

	/**
	 * 
	 * @param targetGeos
	 *            list of seleted cells
	 */
	public void setSelectedRows(ArrayList<GeoElement> targetGeos) {
		if (targetGeos != null)
			selectedRows = targetGeos;
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
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos) {

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
			int pos = ((CASTableCellEditorD)casView.getConsoleTable().getCellEditor(i,CASTable.COL_CAS_CELLS)).getCaretPosition();
			applyUseAsText(targetGeos);
			casView.getConsoleTable().startEditingRow(i);
			((CASTableCellEditorD)casView.getConsoleTable().getCellEditor(i,CASTable.COL_CAS_CELLS)).setCaretPosition(pos);
		}else if (source == btnShowKeyboard) {
			if(((GuiManagerD)app.getGuiManager())!=null){
				if (AppD.isVirtualKeyboardActive()
						&& !((GuiManagerD)app.getGuiManager()).showVirtualKeyboard()) {

					// if keyboard is active but hidden, just show it
					((GuiManagerD)app.getGuiManager()).toggleKeyboard(true);
					((GuiManagerD)app.getGuiManager()).getVirtualKeyboard().toggleNumeric(true);

				} else {

					AppD.setVirtualKeyboardActive(!AppD
							.isVirtualKeyboardActive());
					((GuiManagerD)app.getGuiManager()).toggleKeyboard(
							AppD.isVirtualKeyboardActive());
					((GuiManagerD)app.getGuiManager()).getVirtualKeyboard().toggleNumeric(
							AppD.isVirtualKeyboardActive());
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

	private void applyTextColor(ArrayList<GeoElement> geos) {

		Color color = geogebra.awt.GColorD.getAwtColor(btnTextColor
				.getSelectedColor());
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell
					) {
				((GeoCasCell) geo)
						.setFontColor(new geogebra.awt.GColorD(color));
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
		if (btnBold.isSelected())
			fontStyle += 1;
		if (btnItalic.isSelected())
			fontStyle += 2;
		App.printStacktrace(geos.size()+"");
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			App.debug(((GeoCasCell) geo).getGeoText());
			if (geo instanceof GeoCasCell
					&& ((GeoCasCell) geo).getGeoText().getFontStyle() != fontStyle) {
				((GeoCasCell) geo).getGeoText().setFontStyle(fontStyle);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyTextSize(ArrayList<GeoElement> geos) {

		double fontSize = GeoText.getRelativeFontSize(btnTextSize
				.getSelectedIndex()); // transform indices to the range -4, .. ,
										// 4

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell
					&& ((GeoCasCell) geo).getGeoText().getFontSizeMultiplier() != fontSize) {
				((GeoCasCell) geo).setFontSizeMultiplier(fontSize);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	/**
	 * @return array of popup buttons
	 */
	protected PopupMenuButton[] newPopupBtnList() {
		return new PopupMenuButton[] { btnTextColor, btnTextSize };
	}

	/**
	 * @return array of toggle buttons
	 */
	protected MyToggleButton[] newToggleBtnList() {
		return new MyToggleButton[] { btnBold, btnItalic, btnUseAsText };
	}

	private void initGUI() {

		removeAll();

		createTextButtons();

		add(btnUseAsText);
		add(btnTextColor);
		add(btnBold);
		add(btnItalic);
		
		btnShowKeyboard =       new MyToggleButton(app.getImageIcon("cas-keyboard.png"), iconHeight);    
		btnShowKeyboard.addActionListener(this);
		add(btnShowKeyboard);
		//add(btnTextSize); //TODO: Fix text size

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

	private void createTextButtons() {

		// ========================
		// text color button
		final Dimension textColorIconSize = new Dimension(20, iconHeight);

		btnTextColor = new ColorPopupMenuButton(app, textColorIconSize,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private static final long serialVersionUID = 1L;

			private Color geoColor;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					geoColor = geogebra.awt.GColorD
							.getAwtColor(((GeoCasCell) geo).getFontColor());
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
					setFontStyle(((TextProperties) geo).getFontStyle());
				}
			}

			@Override
			public ImageIcon getButtonIcon() {
				return GeoGebraIcon.createTextSymbolIcon("A",
						app.getPlainFont(), textColorIconSize,
						geogebra.awt.GColorD.getAwtColor(getSelectedColor()),
						null);
			}

		};

		btnTextColor.addActionListener(this);

		// ========================================
		// use as text button
		ImageIcon useAsTextIcon = GeoGebraIcon.createStringIcon(
				app.getPlain("Text").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnUseAsText = new MyToggleButton(useAsTextIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				setVisible(true);
				btnUseAsText.setSelected(checkGeoText(geos));

			}
		};
		btnUseAsText.addActionListener(this);

		// ========================================
		// bold text button
		ImageIcon boldIcon = GeoGebraIcon.createStringIcon(app.getPlain("Bold")
				.substring(0, 1), app.getPlainFont(), true, false, true,
				iconDimension, Color.black, null);
		btnBold = new MyToggleButton(boldIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
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
		ImageIcon italicIcon = GeoGebraIcon.createStringIcon(
				app.getPlain("Italic").substring(0, 1), app.getPlainFont(),
				false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButton(italicIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				this.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
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

		String[] textSizeArray = app.getFontSizeStrings();

		btnTextSize = new PopupMenuButton(app, textSizeArray, -1, 1,
				new Dimension(-1, iconHeight),
				geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0]);
					setSelectedIndex(GeoText
							.getFontSizeIndex(((GeoCasCell) geo)
									.getFontSizeMultiplier())); // font size
																// ranges from
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

		btnUseAsText.setToolTipText(app.getPlainTooltip("stylebar.UseAsText"));
		btnTextColor.setToolTipText(app.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(app.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(app.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(app.getPlainTooltip("stylebar.Italic"));
	}

}
