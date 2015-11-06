package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.MyImageW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MyCellRendererW implements MouseDownHandler, MouseUpHandler {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppW app;
	private Kernel kernel;
	private SpreadsheetViewW view;
	private Grid table;
	
	// LaTeX
	// private ImageIcon latexIcon, emptyIcon;
	private String latexStr;

	// Cell formats
	private CellFormat formatHandler;
	private Integer alignment = -1;
	private Integer traceBorder = -1;
	private Integer fontStyle;
	boolean isCustomBGColor;

	/*
	 * // Borders (not implemented yet) private Border cellPadding =
	 * BorderFactory.createEmptyBorder(2, 5, 2, 5); private Border bTop =
	 * BorderFactory .createMatteBorder(1, 0, 0, 0, Color.RED); private Border
	 * bLeft = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.RED); private
	 * Border bBottom = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.RED);
	 * private Border bRight = BorderFactory.createMatteBorder(0, 0, 0, 1,
	 * Color.RED); private Border bAll = BorderFactory .createMatteBorder(1, 1,
	 * 1, 1, Color.RED);
	 * 
	 * // Rendering objects for lists, buttons and booleans private JCheckBox
	 * checkBox; private JButton button; private JComboBox comboBox; private
	 * DefaultComboBoxModel cbModel;
	 */


	// Cell geo
	private GeoElement geo;

	/*********************************************************
	 * Constructor
	 * 
	 * @param app
	 * @param view
	 * @param formatHandler
	 */
	public MyCellRendererW(AppW app, SpreadsheetViewW view,
			MyTableW table, CellFormat formatHandler) {

		this.app = app;
		this.kernel = app.getKernel();
		this.formatHandler = formatHandler;
		this.view = view;
		this.table = table.getGrid();

	}

	public void updateCellFormat(GeoElement geo, int row, int column) {

		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		//cellPoint.setLocation(column, row);

		// Font style
		fontStyle = (Integer) formatHandler.getCellFormat(column, row,
		        CellFormat.FORMAT_FONTSTYLE);
		s.setFontSize(app.getFontSizeWeb(), Unit.PX);
		if (fontStyle == null) {
			s.setFontStyle(Style.FontStyle.NORMAL);
			s.setFontWeight(Style.FontWeight.NORMAL);

		} else {
			if (fontStyle == CellFormat.STYLE_ITALIC
			        || fontStyle == CellFormat.STYLE_BOLD_ITALIC) {
				s.setFontStyle(Style.FontStyle.ITALIC);
			} else {
				s.setFontStyle(Style.FontStyle.NORMAL);
			}

			if (fontStyle == CellFormat.STYLE_BOLD
			        || fontStyle == CellFormat.STYLE_BOLD_ITALIC) {
				s.setFontWeight(Style.FontWeight.BOLD);
			} else {
				s.setFontWeight(Style.FontWeight.NORMAL);
			}
		}

		// Foreground color
		if (geo != null) {
			if (geo.getLabelColor() != null) {
				s.setColor(geo.getLabelColor().toString());
			} else {
				s.clearColor();
			}
		}

		// Alignment
		alignment = (Integer) formatHandler.getCellFormat(column, row,
		        CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			s.setProperty("textAlign",
			        alignment == CellFormat.ALIGN_LEFT ? "left"
			                : (alignment == CellFormat.ALIGN_RIGHT ? "right"
			                        : "center"));
		} else if (geo != null && geo.isGeoText()) {
			s.setProperty("textAlign", "left");
		} else {
			s.setProperty("textAlign", "right");
		}

		// Background color
		updateCellBackground(geo, row, column);

		// Border
		updateCellBorder(row, column);

	}

	public void clearBorder(int row, int column) {
		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		s.clearProperty("borderBottomColor");
		s.clearProperty("borderRightColor");
	}

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

	public void updateCellBorder(int row, int column) {
		Byte border = (Byte) formatHandler.getCellFormat(column, row,
				CellFormat.FORMAT_BORDER);
		if ((row == -1) || (column == -1)) {
			return;
		}
		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		// s.clearProperty("borderBottomColor");
		// s.clearProperty("borderRightColor");
		if (border != null) {
			// left bar, 0
			if (!CellFormat.isZeroBit(border, 0)) {
				// left borders' width is 0px, so left neighbor's right border
				// will be colored.
				if (column > 0) {
					Style sLeft = table.getCellFormatter()
							.getElement(row, column - 1).getStyle();
					sLeft.setProperty("borderRightColor", "#000000");
				}
			}
			
			// top bar, 1
			if (!CellFormat.isZeroBit(border, 1)) {
				// top borders' width is 0px, so top neighbor's bottom border
				// will be colored.
				if (row > 0) {
					Style sTop = table.getCellFormatter()
							.getElement(row - 1, column).getStyle();
					sTop.setProperty("borderBottomColor", "#000000");
				}
			}
			
			// right bar, 2
			if (!CellFormat.isZeroBit(border, 2)) {
				s.setProperty("borderRightColor", "#000000");
			}
			
			// bottom bar, 3
			if (!CellFormat.isZeroBit(border, 3)) {
				s.setProperty("borderBottomColor", "#000000");
			}
		}
	}

	public void updateCellBackground(GeoElement geo, int row,
	        int column) {

		GColor bgColor = (GColor) formatHandler.getCellFormat(column, row,
		        CellFormat.FORMAT_BGCOLOR);
		
		if (geo != null) {

			if (bgColor == null && geo.getBackgroundColor() != null)
				bgColor = geo.getBackgroundColor();

			// adjust selection color when there is a bgColor
			if (geo.doHighlighting()) {
				if (bgColor != null) {
					bgColor = bgColor.darker();
				} else {
					bgColor = MyTableW.SELECTED_BACKGROUND_COLOR;
				}
			}
		}

		Style s = table.getCellFormatter().getElement(row, column).getStyle();
		if (bgColor != null)
			s.setBackgroundColor(bgColor.toString());
		else
			s.clearBackgroundColor();

	}

	public void updateTableCellValue(Grid table, Object value, final int row, final int column) {

		// Get the cell geo, exit if null
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			table.setText(row, column, " ");
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
				Context2d c2d = canv.getContext2d();
				c2d.drawImage(mw.getImage(), 0, 0);
				sp.add(canv);
				table.setWidget(row, column, sp);
				table.getCellFormatter().getElement(row, column)
						.addClassName("SVCenterTD");
				return;
			}
		}

		table.getCellFormatter().getElement(row, column)
				.removeClassName("SVCenterTD");

		// Set text according to algebra style
		String text = "";
		String latex = null;
		if (geo.isIndependent()) {
			if (geo.isLaTeXDrawableGeo()
			        && (geo.isGeoText() ? ((GeoText) geo).isLaTeX() : true)) {

				latex = geo.getLaTeXdescription();
			}
			text = geo.toValueString(StringTemplate.defaultTemplate);
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				if (geo.isLaTeXDrawableGeo()
				        && (geo.isGeoList() ? !((GeoList) geo).isMatrix()
				                : true)
				        && (geo.isGeoText() ? ((GeoText) geo).isLaTeX() : true)) {

					latex = geo.getLaTeXdescription();
				}
				text = geo.toValueString(StringTemplate.defaultTemplate);
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = geo
				        .getDefinitionDescription(StringTemplate.defaultTemplate);
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = geo
				        .getCommandDescription(StringTemplate.defaultTemplate);
				break;

			}
		}
		
		if (useSpecialEditor(row, column)) {
			updateSpecialEditor(table, geo, row, column);
			return;
		}

		Canvas c = null;
		if (latex != null) {
			Widget current = table.getWidget(row, column);
			if (!(current instanceof Canvas)) {
				c = Canvas.createIfSupported();
				table.setWidget(row, column, c);
			} else {
				c = (Canvas) current;
			}
		}
		if (c == null) {
			table.setText(row, column, text);
		} else {
			DrawEquationW.paintOnCanvas(geo, latex, c, app.getFontSizeWeb());
		}
	}

	public void onMouseDown(MouseDownEvent e) {
		// TODO: maybe use CancelEvents.instance?
		e.stopPropagation();
	}

	public void onMouseUp(MouseUpEvent e) {
		// TODO: maybe use CancelEvents.instance?
		e.stopPropagation();
	}
	
	private boolean useSpecialEditor(int row, int column) {
		if (!view.allowSpecialEditor() || (kernel.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_VALUE)) {
			return false;
		}
		return geo.isGeoBoolean() || geo.isGeoButton() || geo.isGeoList();
	}
    
	
	private void updateSpecialEditor(Grid table, GeoElement geo, int row,
	        int column) {

		if (geo.isGeoBoolean()) {
			FlowPanel fp = new FlowPanel();
			final CheckBox checkbox = new CheckBox();
			fp.add(checkbox);
			checkbox.getElement()
			        .getStyle()
			        .setBackgroundColor(
			                table.getElement().getStyle().getBackgroundColor());
			fp.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
			checkbox.setEnabled(geo.isIndependent());

			checkbox.getElement().addClassName(
			        "geogebraweb-checkbox-spreadsheet");

			if (geo.isLabelVisible()) {
				// checkBox.setText(geo.getCaption());
			}
			checkbox.setValue(((GeoBoolean) geo).getBoolean());

			final GeoBoolean geoBoolean = (GeoBoolean) geo;

			checkbox.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent ce) {
					if (view.allowSpecialEditor()) {
						geoBoolean.setValue(!geoBoolean.getBoolean());

						// Don't update all cell, see #5153
						// kernel.updateConstruction();
						geoBoolean.updateRepaint();
					}
				}
			});

			table.setWidget(row, column, fp);
			return;
		}

		if (geo.isGeoButton()) {
			final GeoButton gb = (GeoButton) geo;
			final Button button = new Button();
			button.getElement()
			        .getStyle()
			        .setBackgroundColor(
			                table.getElement().getStyle().getBackgroundColor());
			button.setText(geo.getCaption(StringTemplate.defaultTemplate));
			button.getElement().getStyle()
			        .setColor(geo.getObjectColor().toString());
			button.getElement().addClassName("geogebraweb-button-spreadsheet");

			button.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent ce) {
					gb.runClickScripts(null);
					gb.getKernel().updateConstruction();
				}
			});

			table.setWidget(row, column, button);
			return;
		}

		if (geo.isGeoList()) {
			final GeoList list = (GeoList) geo;
			final ListBox lb = new ListBox();
			lb.setVisibleItemCount(1);
			lb.setEnabled(true);

			lb.getElement()
			        .getStyle()
			        .setBackgroundColor(
			                table.getElement().getStyle().getBackgroundColor());
			lb.getElement().addClassName("geogebraweb-select-spreadsheet");

			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++)
					// toString doesn't work for some reason
					lb.addItem(list.get(i).toValueString(
					        StringTemplate.defaultTemplate));
				lb.setSelectedIndex(list.getSelectedIndex());
			}

			lb.addMouseDownHandler(this);
			lb.addMouseUpHandler(this);

			lb.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent ce) {
					if (view.allowSpecialEditor()) {
						list.setSelectedIndex(lb.getSelectedIndex(), true);
					}
				}
			});

			table.setWidget(row, column, lb);
			return;
		}
	}

}
