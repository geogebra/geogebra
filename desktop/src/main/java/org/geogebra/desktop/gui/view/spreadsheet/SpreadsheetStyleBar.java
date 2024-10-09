package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.color.ColorPopupMenuButton;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.gui.util.ToggleButtonD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * JToolBar with buttons to format spreadsheet cells.
 * 
 * @author George Sturr 2010-4-3
 * 
 */
public class SpreadsheetStyleBar extends JToolBar implements ActionListener, SetLabels {
	private static final long serialVersionUID = 1L;
	private final SpreadsheetViewD view;
	private final AppD app;
	private final MyTableD table;
	private final CellFormat formatHandler;
	private final ArrayList<TabularRange> selectedCells;

	private ToggleButtonD btnFormulaBar;
	private ToggleButtonD btnLeftAlign;
	private ToggleButtonD btnCenterAlign;
	private ToggleButtonD btnRightAlign;
	private ColorPopupMenuButton btnBgColor;
	private ToggleButtonD btnBold;
	private ToggleButtonD btnItalic;
	private boolean allowActionPerformed = true;
	private PopupMenuButtonD btnBorderStyle;

	protected int iconHeight = 18;
	private Dimension iconDimension = new Dimension(16, iconHeight);
	private final LocalizationD loc;

	/**
	 * @param view spreadsheet
	 */
	public SpreadsheetStyleBar(SpreadsheetViewD view) {
		this.view = view;
		this.app = view.getApplication();
		this.loc = app.getLocalization();
		this.table = (MyTableD) view.getSpreadsheetTable();
		this.formatHandler = (CellFormat) table.getCellFormatHandler();
		this.selectedCells = table.getSelectedRanges();

		// toolbar settings
		setFloatable(false);
		reinit();
	}

	/**
	 * Reinitialize the view
	 */
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
		btnFormulaBar = new ToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMULA_BAR), iconHeight);
		btnFormulaBar.addActionListener(this);

		ImageIcon boldIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Bold").substring(0, 1), app.getPlainFont(), true,
				false, true, iconDimension, Color.black, null);
		btnBold = new ToggleButtonD(boldIcon, iconHeight);
		btnBold.addActionListener(this);
		btnBold.setPreferredSize(iconDimension);

		ImageIcon italicIcon = GeoGebraIconD.createStringIcon(
				loc.getMenu("Italic").substring(0, 1), app.getPlainFont(),
				false, true, true, iconDimension, Color.black, null);
		btnItalic = new ToggleButtonD(italicIcon, iconHeight);
		btnItalic.addActionListener(this);

		btnLeftAlign = new ToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_LEFT),
				iconHeight);
		btnLeftAlign.addActionListener(this);

		btnCenterAlign = new ToggleButtonD(
				app.getScaledIcon(GuiResourcesD.FORMAT_JUSTIFY_CENTER),
				iconHeight);
		btnCenterAlign.addActionListener(this);

		btnRightAlign = new ToggleButtonD(
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

	@Override
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
			if (((ToggleButtonD) source).isSelected()) {
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
			int fontStyle = CellFormat.STYLE_PLAIN;
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
				TabularRange tr = selectedCells.get(i);
				ArrayList<GeoElement> ar = CellRangeUtil.toGeoList(tr, app);
				for (int j = 0; j < ar.size(); j++) {
					GeoElement geo = ar.get(j);
					geo.setBackgroundColor(bgCol);
					geo.updateVisualStyleRepaint(GProperty.COLOR);
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
			if (((MyTableD) view.getSpreadsheetTable()).isSelectNone()) {
				view.getSpreadsheetTable().setSelection(0, 0);
			}
			view.updateFormulaBar();

		}

		this.requestFocus();
		app.storeUndoInfo();

		table.repaint();
	}

	/**
	 * Update the stylebar
	 */
	public void updateStyleBar() {
		allowActionPerformed = false;

		TabularRange range = table.getFirstSelection();

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

		btnFormulaBar.setSelected(view.getShowFormulaBar());
		allowActionPerformed = true;
	}

}
