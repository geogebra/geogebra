package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.MyImageW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.FontStyle;
import org.gwtproject.dom.style.shared.FontWeight;
import org.gwtproject.dom.style.shared.TextAlign;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Grid;
import org.gwtproject.user.client.ui.ListBox;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.base.Js;

/**
 * Cell content renderer
 */
public class MyCellRendererW implements MouseDownHandler, MouseUpHandler {

	// ggb fields
	private AppW app;
	private Kernel kernel;
	private SpreadsheetViewW view;
	private Grid table;
	
	// Cell formats
	private CellFormat formatHandler;

	// Cell geo
	private GeoElement geo;

	/*********************************************************
	 * Constructor
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            view
	 * @param table
	 *            table
	 * @param formatHandler
	 *            formatter
	 */
	public MyCellRendererW(AppW app, SpreadsheetViewW view,
			MyTableW table, CellFormat formatHandler) {
		this.app = app;
		this.kernel = app.getKernel();
		this.formatHandler = formatHandler;
		this.view = view;
		this.table = table.getGrid();
	}

	/**
	 * @param cell
	 *            spreadsheet cell
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void updateCellFormat(GeoElement cell, int row, int column) {
		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		//cellPoint.setLocation(column, row);

		// Font style
		Integer fontStyle = (Integer) formatHandler.getCellFormat(column, row,
		        CellFormat.FORMAT_FONTSTYLE);
		s.setFontSize(app.getFontSize(), Unit.PX);
		if (fontStyle == null) {
			s.setFontStyle(FontStyle.NORMAL);
			s.setFontWeight(FontWeight.NORMAL);

		} else {
			if (fontStyle == CellFormat.STYLE_ITALIC
			        || fontStyle == CellFormat.STYLE_BOLD_ITALIC) {
				s.setFontStyle(FontStyle.ITALIC);
			} else {
				s.setFontStyle(FontStyle.NORMAL);
			}

			if (fontStyle == CellFormat.STYLE_BOLD
			        || fontStyle == CellFormat.STYLE_BOLD_ITALIC) {
				s.setFontWeight(FontWeight.BOLD);
			} else {
				s.setFontWeight(FontWeight.NORMAL);
			}
		}

		// Foreground color
		if (cell != null) {
			if (cell.getLabelColor() != null) {
				s.setColor(cell.getLabelColor().toString());
			} else {
				s.clearColor();
			}
		}

		// Alignment
		int alignment = formatHandler.getAlignment(column, row,
				cell != null && cell.isGeoText());
		switch (alignment) {
                default:
		case CellFormat.ALIGN_LEFT:
			s.setProperty("textAlign", "left");
			break;
		case CellFormat.ALIGN_RIGHT:
			s.setProperty("textAlign", "right");
			break;
		case CellFormat.ALIGN_CENTER:
			s.setProperty("textAlign", "center");
			break;
		}

		// Background color
		updateCellBackground(cell, row, column);

		// Border
		updateCellBorder(row, column);
	}

	/**
	 * Remove bottom + right border styles from a cell.
	 * 
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void clearBorder(int row, int column) {
		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		s.clearProperty("borderBottomColor");
		s.clearProperty("borderRightColor");
	}

	/**
	 * @param column
	 *            column
	 */
	public void updateColumnBorder(int column) {
		Byte border = (Byte) formatHandler.getCellFormat(column, -1,
				CellFormat.FORMAT_BORDER);
		if (border != null) {
			if (!CellFormat.isZeroBit(border, 0)) {
				for (int row = 0; row < 20; row++) {
					// left borders' width is 0px, so left neighbor's right
					// border
					// will be colored.
					if (column > 0) {
						Style sLeft = table.getCellFormatter()
								.getElement(row, column - 1).getStyle();
						sLeft.setProperty("borderRightColor", "#000000");
					}
				}
			}

			if (!CellFormat.isZeroBit(border, 2)) {
				for (int row = 0; row < 20; row++) {
					Style s = table.getCellFormatter().getElement(row, column)
							.getStyle();
					s.setProperty("borderRightColor", "#000000");
				}
			}
		}
	}

	/**
	 * @param row
	 *            row
	 */
	public void updateRowBorder(int row) {
		Byte border = (Byte) formatHandler.getCellFormat(-1, row,
				CellFormat.FORMAT_BORDER);
		if (border != null) {
			if (!CellFormat.isZeroBit(border, 1)) {
				// top borders' width is 0px, so top neighbor's bottom border
				// will be colored.
				for (int column = 0; column < 20; column++) {
					if (row > 0) {
						Style sTop = table.getCellFormatter()
								.getElement(row - 1, column).getStyle();
						sTop.setProperty("borderBottomColor", "#000000");
					}
				}
			}
			if (!CellFormat.isZeroBit(border, 3)) {
				for (int column = 0; column < 20; column++) {
					Style s = table.getCellFormatter()
							.getElement(row - 1, column).getStyle();
					s.setProperty("borderBottomColor", "#000000");

				}
			}
		}
	}

	/**
	 * @param row
	 *            cell row
	 * @param column
	 *            cell column
	 */
	public void updateCellBorder(int row, int column) {
		Byte border = (Byte) formatHandler.getCellFormat(column, row,
				CellFormat.FORMAT_BORDER);
		if (row == -1 || column == -1 || border == null) {
			return;
		}

		final int TOP_BIT = 1;
		final int BOTTOM_BIT = 3;
		final int LEFT_BIT = 0;
		final int RIGHT_BIT = 2;

		// correct order for borderWidth
		// top right bottom left
		boolean top = CellFormat.isOneBit(border, TOP_BIT);

		// drawn by the adjacent cells (if possible, see below)
		boolean right = false;
		boolean bottom = false;

		boolean left = CellFormat.isOneBit(border, LEFT_BIT);
		
		if (column > 0) {
			Byte borderLeft = (Byte) formatHandler.getCellFormat(column - 1,
					row, CellFormat.FORMAT_BORDER);
			left = left || CellFormat.isOneBit(borderLeft, RIGHT_BIT);
		}

		if (row > 0) {
			Byte borderTop = (Byte) formatHandler.getCellFormat(column, row - 1,
					CellFormat.FORMAT_BORDER);
			top = top || CellFormat.isOneBit(borderTop, BOTTOM_BIT);

		}

		// right border drawn only if adjacent cell has no formatting
		if (formatHandler.getCellFormat(column + 1, row,
				CellFormat.FORMAT_BORDER) == null) {
			right = CellFormat.isOneBit(border, RIGHT_BIT);
		}

		// bottom border drawn only if adjacent cell has no formatting
		if (formatHandler.getCellFormat(column, row + 1,
				CellFormat.FORMAT_BORDER) == null) {
			bottom = CellFormat.isOneBit(border, BOTTOM_BIT);
		}

		final String NONE = " 0px";
		final String LINE = " 2px";

		// make a string like "0px 2px 2px 0px"
		// top right bottom left
		String borderWidth = top ? LINE : NONE;
		borderWidth += right ? LINE : NONE;
		borderWidth += bottom ? LINE : NONE;
		borderWidth += left ? LINE : NONE;

		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		s.setProperty("borderStyle", "solid");
		// top right bottom left
		s.setProperty("borderWidth", borderWidth);

		if (top) {
			s.setProperty("borderTopColor", "#000000");
		}
		if (right) {
			s.setProperty("borderRightColor", "#000000");
		}

		if (bottom) {
			s.setProperty("borderBottomColor", "#000000");
		}

		if (left) {
			s.setProperty("borderLeftColor", "#000000");
		}
	}

	/**
	 * @param cellGeo
	 *            cell geo
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void updateCellBackground(GeoElement cellGeo, int row, int column) {

		GColor bgColor = (GColor) formatHandler.getCellFormat(column, row,
		        CellFormat.FORMAT_BGCOLOR);
		
		if (cellGeo != null) {

			if (bgColor == null && cellGeo.getBackgroundColor() != null) {
				bgColor = cellGeo.getBackgroundColor();
			}

			// adjust selection color when there is a bgColor
			if (cellGeo.doHighlighting()) {
				if (bgColor != null) {
					bgColor = bgColor.darker();
				} else {
					bgColor = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR;
				}
			}
		}

		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		if (bgColor != null) {
			s.setBackgroundColor(bgColor.toString());
		} else {
			s.clearBackgroundColor();
		}
	}

	/**
	 * @param table1
	 *            table
	 * @param value
	 *            cell value
	 * @param row
	 *            row
	 * @param column
	 *            column
	 */
	public void updateTableCellValue(Grid table1, Object value, final int row, final int column) {

		// Get the cell geo, exit if null
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			table1.setText(row, column, " ");
			return;
		}

		if (geo.isGeoImage()) {
			final MyImageW mw = (MyImageW) geo.getFillImage();
			if (mw != null) {
				SimplePanel sp = new SimplePanel();
				sp.addStyleName("SVCenterParent");
				Canvas canv = Canvas.createIfSupported();
				canv.addStyleName("SVCenterContent");
				canv.setCoordinateSpaceWidth(mw.getWidth());
				canv.setCoordinateSpaceHeight(mw.getHeight());
				canv.setWidth(mw.getWidth() + "px");
				canv.setHeight(mw.getHeight() + "px");
				CanvasRenderingContext2D c2d = Js.uncheckedCast(canv.getContext2d());
				c2d.drawImage(mw.getImage(), 0, 0);
				sp.add(canv);
				table1.setWidget(row, column, sp);
				table1.getCellFormatter().getElement(row, column)
						.addClassName("SVCenterTD");
				return;
			}
		}

		table1.getCellFormatter().getElement(row, column)
				.removeClassName("SVCenterTD");

		// Set text according to algebra style
		String text = "";
		String latex = null;
		if (geo.isEmptySpreadsheetCell()) {
			text = "";
		} else {
			StringTemplate template = StringTemplate.defaultTemplate.deriveWithFractions(false);
			if (geo.isIndependent()) {
				if (geo.isLaTeXDrawableGeo()
						&& (!geo.isGeoText() || ((GeoText) geo).isLaTeX())) {

					latex = geo.getLaTeXdescription();
				}
				text = geo.toValueString(template);
			} else {
				switch (kernel.getAlgebraStyleSpreadsheet()) {
				default:
				case Kernel.ALGEBRA_STYLE_VALUE:
					if (geo.isLaTeXDrawableGeo()
							&& (!geo.isGeoText() || ((GeoText) geo).isLaTeX())) {

						latex = geo.getLaTeXdescription();
					}
					text = geo.toValueString(template);
					break;

				case Kernel.ALGEBRA_STYLE_DESCRIPTION:
					text = geo
							.getDefinitionDescription(template);
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = geo
							.getDefinition(template);
					break;

				}
			}
		}
		
		if (useSpecialEditor()) {
			updateSpecialEditor(table1, geo, row, column);
			return;
		}

		Canvas c = null;
		if (latex != null) {
			Widget current = table1.getWidget(row, column);
			if (!(current instanceof Canvas)) {
				c = Canvas.createIfSupported();
				table1.setWidget(row, column, c);
			} else {
				c = (Canvas) current;
			}
		}
		if (c == null) {
			table1.setText(row, column, text);
		} else {
			DrawEquationW.paintOnCanvas(geo, latex, c, app.getFontSize());
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent e) {
		// TODO: maybe use CancelEvents.instance?
		e.stopPropagation();
	}

	@Override
	public void onMouseUp(MouseUpEvent e) {
		// TODO: maybe use CancelEvents.instance?
		e.stopPropagation();
	}
	
	private boolean useSpecialEditor() {
		if (!view.allowSpecialEditor()
				|| (kernel.getAlgebraStyleSpreadsheet() != Kernel.ALGEBRA_STYLE_VALUE)) {
			return false;
		}
		return geo.isGeoBoolean() || geo.isGeoButton() || geo.isGeoList();
	}
	
	private void updateSpecialEditor(Grid grid, GeoElement cellGeo, int row,
	        int column) {

		if (cellGeo.isGeoBoolean()) {
			FlowPanel fp = new FlowPanel();
			final GeoBoolean geoBoolean = (GeoBoolean) cellGeo;
			ComponentCheckbox checkbox = new ComponentCheckbox(app.getLocalization(),
					true, "");
			checkbox.addDomHandler(event -> {
				event.stopPropagation();
				if (view.allowSpecialEditor()) {
					geoBoolean.setValue(!geoBoolean.getBoolean());

					// Don't update all cell, see #5153
					// kernel.updateConstruction();
					geoBoolean.updateRepaint();
				}
			}, MouseDownEvent.getType());

			fp.add(checkbox);
			fp.getElement().getStyle().setTextAlign(TextAlign.CENTER);

			checkbox.setDisabled(!cellGeo.isIndependent());
			checkbox.setSelected(((GeoBoolean) cellGeo).getBoolean());

			grid.setWidget(row, column, fp);
			return;
		}

		if (cellGeo.isGeoButton()) {
			final GeoButton gb = (GeoButton) cellGeo;
			final StandardButton button = new StandardButton("");
			button.getElement()
			        .getStyle()
			        .setBackgroundColor(
			                grid.getElement().getStyle().getBackgroundColor());
			button.setText(cellGeo.getCaption(StringTemplate.defaultTemplate));
			button.getElement().getStyle()
			        .setColor(cellGeo.getObjectColor().toString());
			button.getElement().addClassName("buttonSpreadsheet");

			button.addFastClickHandler(ce -> {
				gb.runClickScripts(null);
			});

			grid.setWidget(row, column, button);
			return;
		}

		if (cellGeo.isGeoList()) {
			final GeoList list = (GeoList) cellGeo;
			final ListBox lb = new ListBox();
			lb.setVisibleItemCount(1);
			lb.setEnabled(true);

			lb.getElement()
			        .getStyle()
			        .setBackgroundColor(
			                grid.getElement().getStyle().getBackgroundColor());
			lb.getElement().addClassName("geogebraweb-select-spreadsheet");

			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					// toString doesn't work for some reason
					lb.addItem(list.get(i).toValueString(
					        StringTemplate.defaultTemplate));
				}
				lb.setSelectedIndex(list.getSelectedIndex());
			}

			lb.addMouseDownHandler(this);
			lb.addMouseUpHandler(this);

			lb.addChangeHandler(ce -> {
				if (view.allowSpecialEditor()) {
					list.setSelectedIndexUpdate(lb.getSelectedIndex());
				}
			});

			grid.setWidget(row, column, lb);
			return;
		}
	}

}
