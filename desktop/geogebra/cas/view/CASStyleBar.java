package geogebra.cas.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import geogebra.main.Application;
import geogebra.cas.view.CASView;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.kernel.geos.GeoCasCell;

import geogebra.gui.color.ColorPopupMenuButton;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.MyToggleButton;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

public class CASStyleBar extends JToolBar implements ActionListener{
	/** */
	private static final long serialVersionUID = 1L;
	
	protected Application app;
	protected CASView casView;
	
	// buttons and lists of buttons
	private MyToggleButton[] toggleBtnList;
	private PopupMenuButton[] popupBtnList;
	private ColorPopupMenuButton btnTextColor;
	private PopupMenuButton btnTextSize;
	private MyToggleButton btnBold;
	private MyToggleButton btnItalic;
	private MyToggleButton btnUseAsText;
	

	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	private boolean needUndo = false;
	private boolean isIniting = false;
	private ArrayList<GeoElement> selectedRows;
		
	public CASStyleBar(CASView view, Application app){
		isIniting = true;
		this.app = app;
		this.casView = view;
		
		selectedRows = new ArrayList<GeoElement>();
		
		setFloatable(false);
		
		initGUI();
		isIniting = false;
	}
	
	public void setSelectedRows(ArrayList<GeoElement> targetGeos){
		if(targetGeos != null)
			selectedRows = targetGeos;
		updateStyleBar();
	}
	
	public void setSelectedRow(GeoElement geo){
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

		//updateGUI();
	}
	
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
		} else if (source == btnUseAsText){
			applyUseAsText(targetGeos);
		}
		updateStyleBar();
	}
	
	public void updateStyleBar() {
		
			for (int i = 0; i < popupBtnList.length; i++) {
				try{
				popupBtnList[i].update(selectedRows.toArray());
				}catch(Exception e){}//TODO: find problem
			}
			for (int i = 0; i < toggleBtnList.length; i++) {
				try{
				toggleBtnList[i].update(selectedRows.toArray());
				}catch(Exception e){}//TODO: find problem
			}
	
	}
	
	private void applyTextColor(ArrayList<GeoElement> geos) {

		Color color = geogebra.awt.Color.getAwtColor(btnTextColor
				.getSelectedColor());
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell
					&& geogebra.awt.Color.getAwtColor(geo.getObjectColor()) != color) {
				((GeoCasCell)geo).setFontColor(new geogebra.awt.Color(color));
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}
	
	private void applyUseAsText(ArrayList<GeoElement> geos){
		//btnUseAsText
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
	
	private void applyTextSize(ArrayList<GeoElement> geos) {

		int fontSize = GeoText.getRelativeFontSize(btnTextSize
				.getSelectedIndex()); // transform indices to the range -4, .. ,
										// 4

		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof GeoCasCell
					&& ((GeoCasCell) geo).getGeoText().getFontSize() != fontSize) {
				((GeoCasCell) geo).setFontSize(fontSize);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}
	
	protected PopupMenuButton[] newPopupBtnList() {
		return new PopupMenuButton[] {btnTextColor,btnTextSize};
	}

	protected MyToggleButton[] newToggleBtnList() {
		return new MyToggleButton[] {btnBold, btnItalic, btnUseAsText};
	}
	
	private void initGUI() {

		removeAll();
		
		createTextButtons();

		add(btnUseAsText);
		//add(btnTextColor);   //TODO: Fix text color
		add(btnBold);
		add(btnItalic);
		//add(btnTextSize); 	//TODO: Fix text size
		
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
		
		updateStyleBar();
	}
	
	// =====================================================
	// Text Format Buttons
	// =====================================================

	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i]).getGeoElementForPropertiesDialog() instanceof GeoCasCell)) {
				geosOK = false;
				break;
			} else if(! ((GeoCasCell)geos[i]).isUseAsText() ){
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
					geoColor = geogebra.awt.Color.getAwtColor(((GeoCasCell)geo)
							.getFontColor());
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
						geogebra.awt.Color.getAwtColor(getSelectedColor()),
						null);
			}

		};

		btnTextColor.addActionListener(this);

		// ========================================
		// use as text button
		ImageIcon useAsTextIcon = GeoGebraIcon.createStringIcon(app.getPlain("Text")
				.substring(0, 1), app.getPlainFont(), true, false, true,
				iconDimension, Color.black, null);
		btnUseAsText = new MyToggleButton(useAsTextIcon, iconHeight) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = true; //checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					boolean asText = ((GeoCasCell) geo).isUseAsText();
					btnUseAsText.setSelected(asText);
				}
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
				new Dimension(-1, iconHeight), SelectionTable.MODE_TEXT) {

			private static final long serialVersionUID = 1L;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0]);
					setSelectedIndex(GeoText.getFontSizeIndex(((GeoCasCell) geo).getFontSize())); // font size ranges from
														// -4 to 4, transform
														// this to 0,1,..,4
				}
			}
		};
		btnTextSize.addActionListener(this);
		btnTextSize.setKeepVisible(false);
	}
	
	
	public void setLabels() {
		initGUI();
		
		btnUseAsText.setToolTipText(app.getPlainTooltip("stylebar.UseAsText"));
		btnTextColor.setToolTipText(app.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(app.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(app.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(app.getPlainTooltip("stylebar.Italic"));
	}

}
