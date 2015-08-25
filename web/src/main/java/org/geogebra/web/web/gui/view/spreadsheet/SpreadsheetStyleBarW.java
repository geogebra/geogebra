package org.geogebra.web.web.gui.view.spreadsheet;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.CellRange;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.web.gui.color.ColorPopupMenuButton;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.util.GeoGebraIcon;
import org.geogebra.web.web.gui.util.ImageOrText;
import org.geogebra.web.web.gui.util.MyToggleButton2;
import org.geogebra.web.web.gui.util.PopupMenuButton;
import org.geogebra.web.web.gui.util.PopupMenuHandler;
import org.geogebra.web.web.gui.util.StyleBarW;

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

	private final int ICON_SIZE = 18;

	private MyTableW table;
	private CellFormat formatHandler;

	private MyToggleButton2 btnLeftAlign, btnCenterAlign, btnRightAlign;
	private MyToggleButton2 btnBold;
	private MyToggleButton2 btnItalic;
	private ColorPopupMenuButton btnBgColor;

	private boolean allowActionPerformed = true;

	/**
	 * @param view
	 *            {@link SpreadsheetViewW}
	 */
	public SpreadsheetStyleBarW(SpreadsheetViewW view) {
		super(view.getApplication(), App.VIEW_SPREADSHEET);

		this.table = (MyTableW) view.getSpreadsheetTable();
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
		btnBold = new MyToggleButton2(app.getMenu("Bold.Short"));
		btnBold.addClickHandler(this);
		btnBold.addStyleName("btnBold");
		
		btnItalic = new MyToggleButton2(app.getMenu("Italic.Short"));
		btnItalic.addClickHandler(this);
		btnItalic.addStyleName("btnItalic");

		btnLeftAlign = new MyToggleButton2(
		        StyleBarResources.INSTANCE.stylingbar_spreadsheet_align_left(),
		        this);

		btnCenterAlign = new MyToggleButton2(
		        StyleBarResources.INSTANCE
		                .stylingbar_spreadsheet_align_center(),
		        this);

		btnRightAlign = new MyToggleButton2(
		        StyleBarResources.INSTANCE.stylingbar_spreadsheet_align_right(),
		        this);

		final GDimensionW bgColorIconSize = new GDimensionW(ICON_SIZE,
		        ICON_SIZE);

		btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
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
		addViewButton();
	}

	@Override
	public void setLabels() {
		super.setLabels();
		// set labels for buttons with text e.g. button "bold" or "italic"
		this.btnBold.getDownFace().setText(app.getMenu("Bold.Short"));
		this.btnItalic.getDownFace().setText(app.getMenu("Italic.Short"));
		this.btnBold.getUpFace().setText(app.getMenu("Bold.Short"));
		this.btnItalic.getUpFace().setText(app.getMenu("Italic.Short"));
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

	public void onValueChange(ValueChangeEvent event) {
		Object source = event.getSource();
		handleEventHandlers(source);
	}

	public void onClick(ClickEvent event) {
		handleEventHandlers(event.getSource());
	}

	private void handleEventHandlers(Object source) {

		if (!allowActionPerformed)
			return;
		
		ArrayList<CellRange> selectedCells = table.getSelectedCellRanges();

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
			
			// set the color
			ImageOrText data = GeoGebraIcon.createColorSwatchIcon(1.0f, null,
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
		
		// update the color
		GColor color = (GColor) formatHandler.getCellFormat(range, CellFormat.FORMAT_BGCOLOR);
		if (color == null) {
			color = GColor.WHITE;
		}
		ImageOrText data = GeoGebraIcon
		        .createColorSwatchIcon(1.0f, null, color);
		data.applyToLabel(btnBgColor.getButtonContent());

		allowActionPerformed = true;
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		// TODO Auto-generated method stub

	}

	public void fireActionPerformed(PopupMenuButton actionButton) {
		handleEventHandlers(actionButton);
    }
}
