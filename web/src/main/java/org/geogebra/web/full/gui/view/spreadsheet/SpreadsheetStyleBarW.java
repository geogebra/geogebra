package org.geogebra.web.full.gui.view.spreadsheet;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.resources.SVGResource;

/**
 * Stylebar for SpreadsheetView
 * 
 */
public class SpreadsheetStyleBarW extends StyleBarW {

	private MyTableW table;
	private CellFormat formatHandler;

	private ToggleButton btnLeftAlign;
	private ToggleButton btnCenterAlign;
	private ToggleButton btnRightAlign;
	private ToggleButton btnBold;
	private ToggleButton btnItalic;
	private ColorPopupMenuButton btnBgColor;

	private boolean allowActionPerformed = true;

	/**
	 * @param view
	 *            {@link SpreadsheetViewW}
	 */
	public SpreadsheetStyleBarW(SpreadsheetViewW view) {
		super(view.getApplication(), App.VIEW_SPREADSHEET);

		this.table = view.getSpreadsheetTable();
		this.formatHandler = (CellFormat) table.getCellFormatHandler();

		// create and add the buttons
		createButtons();
		addButtons();

		updateStyleBar();

		addStyleName("SpreadsheetStyleBar");
		optionType = OptionType.SPREADSHEET;
		setToolTips();
	}

	private ToggleButton createFontStyleBtn(SVGResource svg, String style) {
		ToggleButton btn = new ToggleButton(svg);
		btn.addFastClickHandler((e) -> handleFormatting());
		btn.addStyleName(style);
		return btn;
	}

	private ToggleButton createAlignmentBtn(SVGResource svg) {
		ToggleButton btn = new ToggleButton(svg);
		btn.addFastClickHandler((e) -> handleAlignment(btn));
		return btn;
	}

	private void createButtons() {
		btnBold = createFontStyleBtn(
				MaterialDesignResources.INSTANCE.text_bold_black(),
				"btnBold");
		btnItalic = createFontStyleBtn(
				MaterialDesignResources.INSTANCE.text_italic_black(),
				"btnItalic");

		btnLeftAlign = createAlignmentBtn(
		        MaterialDesignResources.INSTANCE.horizontal_align_left());
		btnCenterAlign = createAlignmentBtn(
		        MaterialDesignResources.INSTANCE.horizontal_align_center());
		btnRightAlign = createAlignmentBtn(
		        MaterialDesignResources.INSTANCE.horizontal_align_right());

		btnBgColor = new ColorPopupMenuButton(app,
		        ColorPopupMenuButton.COLORSET_BGCOLOR, false);
		btnBgColor.setEnableTable(true);
		btnBgColor.setKeepVisible(false);
		btnBgColor.setSelectedIndex(7);
		btnBgColor.addPopupHandler(w -> handleEventHandlers());
	}

	private void addButtons() {
		add(btnBold);
		add(btnItalic);

		addSeparator();

		add(btnLeftAlign);
		add(btnCenterAlign);
		add(btnRightAlign);

		addSeparator();

		add(btnBgColor);

		addMenuButton();
		if (!app.isUnbundledOrWhiteboard()) {
			addViewButton();
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		// set labels for ToolTips
		setToolTips();
	}

	private void setToolTips() {
		Localization loc = app.getLocalization();
		btnBold.setTitle(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setTitle(loc.getPlainTooltip("stylebar.Italic"));
		btnBgColor.setTitle(loc.getPlainTooltip("stylebar.BgColor"));
		btnLeftAlign.setTitle(loc.getPlainTooltip("stylebar.AlignLeft"));
		btnCenterAlign.setTitle(loc.getPlainTooltip("stylebar.AlignCenter"));
		btnRightAlign.setTitle(loc.getPlainTooltip("stylebar.AlignRight"));
	}

	private void handleAlignment(ToggleButton button) {
		ArrayList<TabularRange> selectedCells = table.getSelectedRanges();
		Integer align = null;
		if (button.isSelected()) {
			if (button == btnLeftAlign) {
				align = CellFormat.ALIGN_LEFT;
			} else if (button == btnRightAlign) {
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
		app.storeUndoInfo();
		table.updateCellFormat(selectedCells);
	}

	private void handleFormatting() {
		ArrayList<TabularRange> selectedCells = table.getSelectedRanges();
		int fontStyle = CellFormat.STYLE_PLAIN;
		if (btnBold.isSelected()) {
			fontStyle += CellFormat.STYLE_BOLD;
		}
		if (btnItalic.isSelected()) {
			fontStyle += CellFormat.STYLE_ITALIC;
		}
		formatHandler.setFormat(selectedCells, CellFormat.FORMAT_FONTSTYLE,
				fontStyle);
		app.storeUndoInfo();
		table.updateCellFormat(selectedCells);
	}

	private void handleEventHandlers() {
		if (!allowActionPerformed) {
			return;
		}

		ArrayList<TabularRange> selectedCells = table.getSelectedRanges();

		// set color in table (needed as geos can be renamed, deleted etc)
		GColor bgCol = btnBgColor.getSelectedColor();
		formatHandler.setFormat(selectedCells, CellFormat.FORMAT_BGCOLOR, bgCol);

		// set the color
		ImageOrText data = GeoGebraIconW.createColorSwatchIcon(1.0, null,
				bgCol);
		data.applyToLabel(btnBgColor.getColorLabel());

		// set color for the actual geos
		for (int i = 0; i < selectedCells.size(); i++) {
			TabularRange tr = selectedCells.get(i);
			ArrayList<GeoElement> ar = CellRangeUtil.toGeoList(tr, app);
			for (int j = 0; j < ar.size(); j++) {
				GeoElement geo = ar.get(i);
				geo.setBackgroundColor(bgCol);
				geo.updateRepaint();
			}
		}
		app.storeUndoInfo();
		table.updateCellFormat(selectedCells);
	}

	/**
	 * Update stylebar for current selection
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
		Object align = formatHandler.getCellFormat(range,
		        CellFormat.FORMAT_ALIGN);
		if (align == null) {
			btnLeftAlign.setSelected(false);
			btnRightAlign.setSelected(false);
			btnCenterAlign.setSelected(false);
		} else {
			int alignVal = (Integer) align;
			btnLeftAlign.setSelected(alignVal == CellFormat.ALIGN_LEFT);
			btnRightAlign.setSelected(alignVal == CellFormat.ALIGN_RIGHT);
			btnCenterAlign.setSelected(alignVal == CellFormat.ALIGN_CENTER);
		}
		
		// update the color
		GColor color = (GColor) formatHandler.getCellFormat(range, CellFormat.FORMAT_BGCOLOR);
		if (color == null) {
			color = GColor.WHITE;
		}
		ImageOrText data = GeoGebraIconW
				.createColorSwatchIcon(1.0, null, color);
		data.applyToLabel(btnBgColor.getColorLabel());

		allowActionPerformed = true;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub

	}
}
