package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.color.ColorPopupMenuButton;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.MyToggleButtonD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * JToolBar with buttons to format spreadsheet cells.
 * 
 * @author George Sturr 2010-4-3
 * 
 */
public class SpreadsheetStyleBar extends JToolBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	private SpreadsheetViewD view;
	private AppD app;
	private MyTableD table;
	private CellFormat formatHandler;
	private ArrayList<CellRange> selectedCells;

	private MyToggleButtonD btnFormulaBar;
	private MyToggleButtonD btnLeftAlign, btnCenterAlign, btnRightAlign;
	private ColorPopupMenuButton btnBgColor;
	private MyToggleButtonD btnBold, btnItalic;
	private boolean allowActionPerformed = true;
	private PopupMenuButtonD btnBorderStyle;

	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	private LocalizationD loc;

	public SpreadsheetStyleBar(SpreadsheetViewD view) {

		this.view = view;
		this.app = view.getApplication();
		this.loc = app.getLocalization();
		this.table = (MyTableD) view.getSpreadsheetTable();
		this.formatHandler = (CellFormat) table.getCellFormatHandler();
		this.selectedCells = table.selectedCellRanges;

		// toolbar settings
		setFloatable(false);
		reinit();
	}

	public void reinit() {
		removeAll();
		Dimension d = getPreferredSize();
		iconHeight = app.getScaledIconSize();
		d.height = iconHeight + 8;
		setPreferredSize(d);
		setFloatable(false);

		// create and add the buttons
		createButtons();
		add(btnFormulaBar);

		this.addSeparator();
		add(btnBold);
		add(btnItalic);

		this.addSeparator();
		add(btnLeftAlign);
		add(btnCenterAlign);
		add(btnRightAlign);

		this.addSeparator();
		add(btnBgColor);

		this.addSeparator();
		add(btnBorderStyle);

		setLabels();
		updateStyleBar();
	}

	private void createButtons() {
		iconHeight = app.getScaledIconSize();
		iconDimension = new Dimension(iconHeight, iconHeight);
		btnFormulaBar = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMULA_BAR), iconHeight);
		btnFormulaBar.addActionListener(this);

		ImageIcon boldIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Bold").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnBold = new MyToggleButtonD(boldIcon, iconHeight);
		btnBold.addActionListener(this);
		btnBold.setPreferredSize(iconDimension);

		ImageIcon italicIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Italic").substring(0, 1), app.getPlainFont(),
				false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButtonD(italicIcon, iconHeight);
		btnItalic.addActionListener(this);

		btnLeftAlign = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_LEFT),
				iconHeight);
		btnLeftAlign.addActionListener(this);

		btnCenterAlign = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_CENTER),
				iconHeight);
		btnCenterAlign.addActionListener(this);

		btnRightAlign = new MyToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_RIGHT),
				iconHeight);
		btnRightAlign.addActionListener(this);

		final Dimension bgColorIconSize = new Dimension(iconHeight, iconHeight);
		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
				ColorPopupMenuButton.COLORSET_BGCOLOR, false) {

			private static final long serialVersionUID = 1L;

			@Override
			public ImageIcon getButtonIcon() {
				Color c = GColorD.getAwtColor(getSelectedColor());
				if (c == null) {
					return GeoGebraIconD.createNullSymbolIcon(
							bgColorIconSize.width, bgColorIconSize.height);
				}
				return GeoGebraIconD.createCellGridIcon(Color.DARK_GRAY, c);
			}
		};
		btnBgColor.setKeepVisible(false);
		btnBgColor.setSelectedIndex(7); // Light Purple
		btnBgColor.addActionListener(this);

		ImageIcon[] borderStyleIcon = {
				app.getScaledIcon(GuiResourcesD.BORDER_NONE),
				app.getScaledIcon(GuiResourcesD.BORDER_FRAME),
				app.getScaledIcon(GuiResourcesD.BORDER_INSIDE),
				app.getScaledIcon(GuiResourcesD.BORDER_ALL),
				app.getScaledIcon(GuiResourcesD.BORDER_TOP),
				app.getScaledIcon(GuiResourcesD.BORDER_BOTTOM),
				app.getScaledIcon(GuiResourcesD.BORDER_LEFT),
				app.getScaledIcon(GuiResourcesD.BORDER_RIGHT) };

		btnBorderStyle = new PopupMenuButtonD(app, borderStyleIcon, 2, -1,
				iconDimension, SelectionTable.MODE_ICON);
		btnBorderStyle.setKeepVisible(false);
		btnBorderStyle.setSelectedIndex(1);
		btnBorderStyle.addActionListener(this);

	}

	public void setLabels() {
		btnFormulaBar.setToolTipText(loc.getMenu("ShowFileBrowser"));
		btnFormulaBar.setToolTipText(loc.getMenu("ShowInputField"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
		btnBorderStyle.setToolTipText(loc.getPlainTooltip("stylebar.Border"));
		btnBgColor.setToolTipText(loc.getPlainTooltip("stylebar.BgColor"));
		btnLeftAlign.setToolTipText(loc.getPlainTooltip("stylebar.AlignLeft"));
		btnCenterAlign
				.setToolTipText(loc.getPlainTooltip("stylebar.AlignCenter"));
		btnRightAlign
				.setToolTipText(loc.getPlainTooltip("stylebar.AlignRight"));

		ImageIcon boldIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Bold").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnBold.setIcon(boldIcon);

		ImageIcon italicIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Italic").substring(0, 1), app.getPlainFont(),
				false, true, true, iconDimension, Color.black, null);
		btnItalic.setIcon(italicIcon);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (!allowActionPerformed) {
			return;
		}

		Object source = e.getSource();

		if (source == btnLeftAlign || source == btnCenterAlign
				|| source == btnRightAlign) {

			Integer align = null;
			if (((MyToggleButtonD) source).isSelected()) {
				if (source == btnLeftAlign) {
					align = CellFormat.ALIGN_LEFT;
				} else if (source == btnRightAlign) {
					align = CellFormat.ALIGN_RIGHT;
				} else {
					align = CellFormat.ALIGN_CENTER;
				}
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
			if (btnBold.isSelected()) {
				fontStyle += CellFormat.STYLE_BOLD;
			}
			if (btnItalic.isSelected()) {
				fontStyle += CellFormat.STYLE_ITALIC;
			}
			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_FONTSTYLE,
					fontStyle);
		}

		else if (source == btnBgColor) {

			// set color in table (needed as geos can be renamed, deleted etc)
			GColor bgCol = btnBgColor.getSelectedColor();
			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_BGCOLOR,
					bgCol

			// could simply be btnBgColor.getSelectedColor(), not sure...
			// bgCol == null ? null : bgCol.getColor()

			);

			// set color for the actual geos
			for (int i = 0; i < selectedCells.size(); i++) {
				CellRange cr = selectedCells.get(i);
				ArrayList<GeoElement> ar = cr.toGeoList();
				for (int j = 0; j < ar.size(); j++) {
					GeoElement geo = ar.get(j);
					if (bgCol == null) {
						geo.setBackgroundColor(null);
					} else {
						geo.setBackgroundColor(bgCol);
					}
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

		else if (source == btnFormulaBar) {
			app.getSettings().getSpreadsheet()
					.setShowFormulaBar(btnFormulaBar.isSelected());
			if (view.getSpreadsheetTable().isSelectNone()) {
				view.getSpreadsheetTable().setSelection(0, 0);
			}
			view.updateFormulaBar();

		}

		this.requestFocus();
		app.storeUndoInfo();

		table.repaint();
	}

	public void updateStyleBar() {

		allowActionPerformed = false;

		CellRange range = table.getSelectedCellRanges().get(0);

		// update font style buttons

		Integer fontStyle = (Integer) formatHandler.getCellFormat(range,
				CellFormat.FORMAT_FONTSTYLE);
		// Application.debug(fontStyle);
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

		btnFormulaBar.setSelected(view.getShowFormulaBar());

		allowActionPerformed = true;

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
