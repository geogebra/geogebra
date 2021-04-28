package org.geogebra.web.full.gui.view.spreadsheet;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.PopupMenuHandler;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Stylebar for SpreadsheetView
 * 
 */
public class SpreadsheetStyleBarW extends StyleBarW implements ClickHandler,
        ValueChangeHandler<Boolean>, PopupMenuHandler {

	private MyTableW table;
	private CellFormat formatHandler;

	private MyToggleButtonW btnLeftAlign;
	private MyToggleButtonW btnCenterAlign;
	private MyToggleButtonW btnRightAlign;
	private MyToggleButtonW btnBold;
	private MyToggleButtonW btnItalic;
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

	private void createButtons() {
		btnBold = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.text_bold_black(), 24));
		btnBold.addClickHandler(this);
		btnBold.addStyleName("btnBold");
		
		btnItalic = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.text_italic_black(), 24));
		btnItalic.addClickHandler(this);
		btnItalic.addStyleName("btnItalic");

		btnLeftAlign = new MyToggleButtonW(
		        MaterialDesignResources.INSTANCE.horizontal_align_left(),
		        this);

		btnCenterAlign = new MyToggleButtonW(
		        MaterialDesignResources.INSTANCE
		                .horizontal_align_center(),
		        this);

		btnRightAlign = new MyToggleButtonW(
		        MaterialDesignResources.INSTANCE.horizontal_align_right(),
		        this);

		btnBgColor = new ColorPopupMenuButton(app,
		        ColorPopupMenuButton.COLORSET_BGCOLOR, false);
		btnBgColor.setEnableTable(true);
		btnBgColor.setKeepVisible(false);
		btnBgColor.setSelectedIndex(7);
		btnBgColor.addActionListener(this);	
		btnBgColor.addPopupHandler(this);
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

		btnBold.setToolTipText(loc.getPlainTooltip(
		        "stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip(
		        "stylebar.Italic"));
		btnBgColor.setToolTipText(loc.getPlainTooltip("stylebar.BgColor"));
		btnLeftAlign.setToolTipText(loc.getPlainTooltip(
		        "stylebar.AlignLeft"));
		btnCenterAlign.setToolTipText(loc.getPlainTooltip(
		        "stylebar.AlignCenter"));
		btnRightAlign.setToolTipText(loc.getPlainTooltip(
		        "stylebar.AlignRight"));
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Object source = event.getSource();
		handleEventHandlers(source);
	}

	@Override
	public void onClick(ClickEvent event) {
		handleEventHandlers(event.getSource());
	}

	private void handleEventHandlers(Object source) {

		if (!allowActionPerformed) {
			return;
		}
		
		ArrayList<CellRange> selectedCells = table.getSelectedCellRanges();

		if (source == btnLeftAlign || source == btnCenterAlign
		        || source == btnRightAlign) {

			Integer align = null;
			if (((MyToggleButtonW) source).isSelected()) {
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
			formatHandler.setFormat(selectedCells, CellFormat.FORMAT_BGCOLOR, bgCol);
			
			// set the color
			ImageOrText data = GeoGebraIconW.createColorSwatchIcon(1.0, null,
			        bgCol);
			data.applyToLabel(btnBgColor.getButtonContent());
			
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
		app.storeUndoInfo();
		table.updateCellFormat(selectedCells);
	}

	/**
	 * Update stylebar for current selection
	 */
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
		data.applyToLabel(btnBgColor.getButtonContent());

		allowActionPerformed = true;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireActionPerformed(PopupMenuButtonW actionButton) {
		handleEventHandlers(actionButton);
    }
}
