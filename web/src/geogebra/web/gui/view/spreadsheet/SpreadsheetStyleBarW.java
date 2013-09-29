package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.spreadsheet.CellFormat;
import geogebra.common.gui.view.spreadsheet.CellRange;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.Localization;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.gui.color.ColorPopupMenuButton;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class SpreadsheetStyleBarW extends StyleBarW implements ClickHandler,
        ValueChangeHandler<Boolean>, PopupMenuHandler {
	private static final long serialVersionUID = 1L;
	private SpreadsheetViewW view;
	private AppW app;
	private MyTableW table;
	private CellFormat formatHandler;
	private ArrayList<CellRange> selectedCells;

	private MyToggleButton2 btnFormulaBar;
	private MyToggleButton2 btnLeftAlign, btnCenterAlign, btnRightAlign;
	private ColorPopupMenuButton btnBgColor;
	private MyToggleButton2 btnBold;
	private MyToggleButton2 btnItalic;
	private boolean allowActionPerformed = true;
	private PopupMenuButton btnBorderStyle;

	protected int iconHeight = 18;

	// ?//private Dimension iconDimension = new Dimension(16, iconHeight);

	public SpreadsheetStyleBarW(SpreadsheetViewW view) {

		super();

		this.view = view;
		this.app = view.getApplication();
		this.table = (MyTableW) view.getSpreadsheetTable();
		this.formatHandler = (CellFormat) table.getCellFormatHandler();
		this.selectedCells = table.selectedCellRanges;

		// create and add the buttons
		createButtons();

		// add(btnFormulaBar);
		// addSeparator();

		add(btnBold);
		add(btnItalic);

		addSeparator();
		add(btnLeftAlign);
		add(btnCenterAlign);
		add(btnRightAlign);

		addSeparator();

		add(btnBgColor);

		// addSeparator();
		// add(btnBorderStyle);

		setLabels();

		updateStyleBar();
	}

	private void createButtons() {

		btnBold = new MyToggleButton2(AppResources.INSTANCE.format_text_bold(),
		        this, iconHeight);

		btnItalic = new MyToggleButton2(
		        AppResources.INSTANCE.format_text_italic(), this, iconHeight);

		btnLeftAlign = new MyToggleButton2(
		        AppResources.INSTANCE.format_justify_left(), this, iconHeight);

		btnCenterAlign = new MyToggleButton2(
		        AppResources.INSTANCE.format_justify_center(), this, iconHeight);

		btnRightAlign = new MyToggleButton2(
		        AppResources.INSTANCE.format_justify_right(), this, iconHeight);

		final GDimensionW bgColorIconSize = new GDimensionW(18, iconHeight);

		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
		        ColorPopupMenuButton.COLORSET_BGCOLOR, false);
		
		btnBgColor.setKeepVisible(false);
		btnBgColor.setSelectedIndex(7);
		btnBgColor.addActionListener(this);	
		btnBgColor.addPopupHandler(this);
		
		/*
		 * ? btnFormulaBar = new
		 * MyToggleButton(app.getImageIcon("formula_bar.png"), iconHeight); //
		 * btnFormulaBar
		 * .setSelectedIcon(app.getImageIcon("formula_bar_hide.png"));
		 * btnFormulaBar.addActionListener(this);
		 * 
		 * 
		 * final Dimension bgColorIconSize = new Dimension(18, iconHeight);
		 * btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
		 * ColorPopupMenuButton.COLORSET_BGCOLOR, false) {
		 * 
		 * private static final long serialVersionUID = 1L;
		 * 
		 * @Override public ImageIcon getButtonIcon() { Color c =
		 * geogebra.awt.GColorD.getAwtColor(getSelectedColor()); if (c == null)
		 * { return GeoGebraIcon.createNullSymbolIcon( bgColorIconSize.width,
		 * bgColorIconSize.height); } return
		 * GeoGebraIcon.createCellGridIcon(Color.DARK_GRAY, c); } };
		 * btnBgColor.setKeepVisible(false); btnBgColor.setSelectedIndex(7); //
		 * Light Purple btnBgColor.addActionListener(this);
		 * 
		 * ImageIcon[] borderStyleIcon = { app.getImageIcon("border_none.png"),
		 * app.getImageIcon("border_frame.png"),
		 * app.getImageIcon("border_inside.png"),
		 * app.getImageIcon("border_all.png"),
		 * app.getImageIcon("border_top.png"),
		 * app.getImageIcon("border_bottom.png"),
		 * app.getImageIcon("border_left.png"),
		 * app.getImageIcon("border_right.png") };
		 * 
		 * btnBorderStyle = new PopupMenuButton(app, borderStyleIcon, 2, -1,
		 * iconDimension, geogebra.common.gui.util.SelectionTable.MODE_ICON);
		 * btnBorderStyle.setKeepVisible(false);
		 * btnBorderStyle.setSelectedIndex(1);
		 * btnBorderStyle.addActionListener(this);
		 */
	}

	public void setLabels() {
		
		Localization loc = app.getLocalization();

		// btnFormulaBar.setToolTipText(loc.getMenu("ShowInputField"));

		btnBold.setToolTipText(loc.getPlainTooltip(
		        "stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip(
		        "stylebar.Italic"));
		// btnBorderStyle.setToolTipText(loc.getPlainTooltip("stylebar.Border"));
		btnBgColor.setToolTipText(loc.getPlainTooltip("stylebar.BgColor"));
		btnLeftAlign.setToolTipText(loc.getPlainTooltip(
		        "stylebar.AlignLeft"));
		btnCenterAlign.setToolTipText(loc.getPlainTooltip(
		        "stylebar.AlignCenter"));
		btnRightAlign.setToolTipText(loc.getPlainTooltip(
		        "stylebar.AlignRight"));
	}

	public void onValueChange(ValueChangeEvent event) {
		Object source = event.getSource();
		handleEventHandlers(source);
	}

	public void onClick(ClickEvent event) {
		handleEventHandlers(event.getSource());
	}

	public void handleEventHandlers(Object source) {

		if (!allowActionPerformed)
			return;

		// Object source = e.getSource();

		if (source == btnLeftAlign || source == btnCenterAlign
		        || source == btnRightAlign) {

			Integer align = null;
			if (((MyToggleButton2) source).isSelected()) {
				if (source == btnLeftAlign)
					align = CellFormat.ALIGN_LEFT;
				else if (source == btnRightAlign)
					align = CellFormat.ALIGN_RIGHT;
				else
					align = CellFormat.ALIGN_CENTER;
			}

			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_ALIGN,
			        align);
			if (align == null) {
				btnLeftAlign.setSelected(false);
				btnRightAlign.setSelected(false);
				btnCenterAlign.setSelected(false);
			} else {
				btnLeftAlign.setSelected(align == CellFormat.ALIGN_LEFT);
				btnRightAlign.setSelected(align == CellFormat.ALIGN_RIGHT);
				btnCenterAlign.setSelected(align == CellFormat.ALIGN_CENTER);
			}
		}

		else if (source == btnBold || source == btnItalic) {
			Integer fontStyle = CellFormat.STYLE_PLAIN;
			if (btnBold.isSelected())
				fontStyle += CellFormat.STYLE_BOLD;
			if (btnItalic.isSelected())
				fontStyle += CellFormat.STYLE_ITALIC;
			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_FONTSTYLE,
			        fontStyle);
		}

		else if (source == btnBgColor) {
			
			// set color in table (needed as geos can be renamed, deleted etc)
			GColor bgCol = btnBgColor.getSelectedColor();
			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_BGCOLOR, bgCol);
			
			// set color for the actual geos
			for (int i = 0; i < selectedCells.size(); i++) {
				CellRange cr = selectedCells.get(i);
				ArrayList<GeoElement> ar = cr.toGeoList();
				for (int j = 0; j < ar.size(); j++) {
					GeoElement geo = ar.get(i);
					geo.setBackgroundColor(bgCol);
					geo.updateRepaint();
				}
			}		  

		}

		else if (source == btnBorderStyle) {
			formatHandler.setBorderStyle(selectedCells,
			        btnBorderStyle.getSelectedIndex());
		}

		else if (source == btnBorderStyle) {
			formatHandler.setBorderStyle(selectedCells.get(0),
			        btnBorderStyle.getSelectedIndex());
		}

		/**
		 * else if (source == btnFormulaBar) {
		 * app.getSettings().getSpreadsheet()
		 * .setShowFormulaBar(btnFormulaBar.isSelected()); if
		 * (view.getSpreadsheetTable().isSelectNone())
		 * view.getSpreadsheetTable().setSelection(0, 0);
		 * view.updateFormulaBar(); }
		 */

		// this.requestFocus();
		app.storeUndoInfo();

		table.renderCells();

		table.repaint();
	}

	public void updateStyleBar() {

		allowActionPerformed = false;

		CellRange range = table.getSelectedCellRanges().get(0);

		// update font style buttons
		Integer fontStyle = (Integer) formatHandler.getCellFormat(range,
		        CellFormat.FORMAT_FONTSTYLE);
		if (fontStyle == null) {
			btnBold.setSelected(false);
			btnItalic.setSelected(false);
		} else {
			btnBold.setSelected(fontStyle == CellFormat.STYLE_BOLD
			        || fontStyle == CellFormat.STYLE_BOLD_ITALIC);
			btnItalic.setSelected(fontStyle == CellFormat.STYLE_ITALIC
			        || fontStyle == CellFormat.STYLE_BOLD_ITALIC);
		}

		// update alignment buttons
		Integer align = (Integer) formatHandler.getCellFormat(range,
		        CellFormat.FORMAT_ALIGN);
		if (align == null) {
			btnLeftAlign.setSelected(false);
			btnRightAlign.setSelected(false);
			btnCenterAlign.setSelected(false);
		} else {
			btnLeftAlign.setSelected(align == CellFormat.ALIGN_LEFT);
			btnRightAlign.setSelected(align == CellFormat.ALIGN_RIGHT);
			btnCenterAlign.setSelected(align == CellFormat.ALIGN_CENTER);
		}

		// btnFormulaBar.setSelected(view.getShowFormulaBar());

		allowActionPerformed = true;

	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub

	}

	public void fireActionPerformed(Object actionButton) {
		handleEventHandlers(actionButton);
    }


	/*
	 * 
	 * private void setTraceBorder(){
	 * 
	 * CellRange cr = new CellRange(table);
	 * 
	 * 
	 * cr.setCellRange(t.traceColumn1, t.traceRow1, t.traceColumn2,
	 * t.traceRow2); table.getCellFormatHandler().setFormat(cr,
	 * CellFormat.FORMAT_TRACING, CellFormat.BORDER_TOP);
	 * 
	 * if(t.doRowLimit){ cr.setCellRange(t.traceColumn1, t.traceRow2,
	 * t.traceColumn2, t.traceRow2); table.getCellFormatHandler().setFormat(cr,
	 * CellFormat.FORMAT_TRACING, CellFormat.BORDER_BOTTOM); }
	 * 
	 * if(t.doRowLimit){ cr.setCellRange(t.traceColumn1, t.traceRow1,
	 * t.traceColumn1, t.traceRow2); }else{ cr.setCellRange(t.traceColumn1,
	 * t.traceRow1, t.traceColumn1, view.MAX_ROWS); }
	 * 
	 * table.getCellFormatHandler().setFormat(cr, CellFormat.FORMAT_TRACING,
	 * CellFormat.BORDER_LEFT);
	 * 
	 * if(t.doRowLimit){ cr.setCellRange(t.traceColumn2, t.traceRow1,
	 * t.traceColumn2, t.traceRow2); }else{ cr.setCellRange(t.traceColumn2,
	 * t.traceRow1, t.traceColumn2, view.MAX_ROWS); }
	 * table.getCellFormatHandler().setFormat(cr, CellFormat.FORMAT_TRACING,
	 * CellFormat.BORDER_RIGHT);
	 * 
	 * }
	 */

}
