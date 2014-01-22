package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellFormat;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class MyCellRendererW {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppW app;
	private Kernel kernel;
	private SpreadsheetViewW view;

	// LaTeX
	//private ImageIcon latexIcon, emptyIcon;
	private String latexStr;

	// Cell formats
	private CellFormat formatHandler;
	private GPoint cellPoint;
	private Integer alignment = -1;
	private Integer traceBorder = -1;
	private Integer fontStyle;
	boolean isCustomBGColor;

	/*// Borders (not implemented yet)
	private Border cellPadding = BorderFactory.createEmptyBorder(2, 5, 2, 5);
	private Border bTop = BorderFactory
			.createMatteBorder(1, 0, 0, 0, Color.RED);
	private Border bLeft = BorderFactory.createMatteBorder(0, 1, 0, 0,
			Color.RED);
	private Border bBottom = BorderFactory.createMatteBorder(0, 0, 1, 0,
			Color.RED);
	private Border bRight = BorderFactory.createMatteBorder(0, 0, 0, 1,
			Color.RED);
	private Border bAll = BorderFactory
			.createMatteBorder(1, 1, 1, 1, Color.RED);

	// Rendering objects for lists, buttons and booleans
	private JCheckBox checkBox;
	private JButton button;
	private JComboBox comboBox;
	private DefaultComboBoxModel cbModel;*/

	private GColor bgColor;

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
			CellFormat formatHandler) {

		this.app = app;
		this.kernel = app.getKernel();
		this.formatHandler = formatHandler;
		this.view = view;

		// Add horizontal padding
		//TODO//setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		// The cell renderer extends JLabel...its icon is used to display LaTeX.
		//?//latexIcon = new ImageIcon();
		//?//emptyIcon = new ImageIcon();

		cellPoint = new GPoint(); // used for cell format calls

		// Rendering for booleans, buttons and lists
		//?//checkBox = new JCheckBox();
		//?//button = new JButton();
		//?//comboBox = new JComboBox();
		//?//comboBox.setRenderer(new MyListCellRenderer());

		//?//cbModel = new DefaultComboBoxModel();
		//?//comboBox.setModel(cbModel);
	}

	public Widget changeTableCellRendererWidget(Widget retwidget, Grid table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		// so row and column in table model coordinates
		cellPoint.setLocation(column, row);

		// Set visible formats ... do this before exit with null geo
		// ==================================================
		// set default background color (adjust later if geo exists)

		bgColor = (GColor)formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_BGCOLOR);
		if (bgColor == null) {
			isCustomBGColor = false;
			bgColor = GColor.white;//table.getBackground();
		} else {
			isCustomBGColor = true;
		}
		if (bgColor != null)
			retwidget.getElement().getStyle().setBackgroundColor(bgColor.toString());

		// Get the cell geo, exit if null
		// ==================================================
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			((Label)retwidget).setText(" ");
			//retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
			//retwidget.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
			//retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
			return retwidget;
		}

		// use special rendering for buttons, booleans and lists
		// =======================================================

		// ===============================================
		// (end special rendering)

		// Set text according to algebra style
		// ===============================================
		String text = "";
		if (geo.isIndependent()) {
			text = geo.toValueString(StringTemplate.defaultTemplate);
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = geo.toValueString(StringTemplate.defaultTemplate);
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = geo.getDefinitionDescription(StringTemplate.defaultTemplate);

				// Label accepts text, not HTML!
				//text = GeoElement
				//		.convertIndicesToHTML(geo
				//				.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = geo.getCommandDescription(StringTemplate.defaultTemplate);

				// Label accepts text, not HTML!
				//text = GeoElement.convertIndicesToHTML(geo
				//		.getCommandDescription(StringTemplate.defaultTemplate));
				break;

			}
		}

		// Set font
		// ===============================================
		fontStyle = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_FONTSTYLE);
		if (fontStyle == null)
			fontStyle = GFont.PLAIN;

		retwidget.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		((Label)retwidget).setText(text);

		GFont gf = app.getFontCanDisplay(text, fontStyle);
		((Label)retwidget).getElement().getStyle().setFontSize(gf.getSize(), Style.Unit.PX);
		((Label)retwidget).getElement().getStyle().setFontStyle(
			gf.isItalic() ? Style.FontStyle.ITALIC : Style.FontStyle.NORMAL);
		((Label)retwidget).getElement().getStyle().setFontWeight(
			gf.isBold() ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);

		// Set foreground and background color
		// ===============================================

		// use geo bgColor if there is no format bgColor
		if (geo.getBackgroundColor() != null && !isCustomBGColor) {
			bgColor = geo.getBackgroundColor();
			isCustomBGColor = true;
		}

		// adjust selection color when there is a bgColor
		if (geo.doHighlighting()) {
			if (isCustomBGColor) {
				bgColor = bgColor.darker();
			} else {
				bgColor = MyTableW.SELECTED_BACKGROUND_COLOR;
			}
		}
		if (bgColor != null)// here was an exception
			((Label)retwidget).getElement().getStyle().setBackgroundColor(bgColor.toString());

		if (geo.getLabelColor() != null)
			((Label)retwidget).getElement().getStyle().setColor(geo.getLabelColor().toString());

		// Set horizontal alignment
		// ===============================================
		alignment = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			((Label)retwidget).getElement().getStyle().setProperty("textAlign",
				alignment == 2 ? "left" : (alignment == 4 ? "right" : "center"));
		} else if (geo.isGeoText()) {
			((Label)retwidget).getElement().getStyle().setProperty("textAlign", "left");
		} else {
			((Label)retwidget).getElement().getStyle().setProperty("textAlign", "right");
		}

		// Set icons for LaTeX and images
		// ===============================================
		/*if (geo.isGeoImage()) {
			latexIcon.setImage(geogebra.awt.GBufferedImageD
					.getAwtBufferedImage(((GeoImage) geo).getFillImage()));
			setIcon(latexIcon);
			setHorizontalAlignment(SwingConstants.CENTER);
			setText("");

		} else {

			boolean isSerif = false;
			if (geo.isDefined()
					&& kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

				latexStr = geo.getFormulaString(StringTemplate.latexTemplate,
						true);
				if (geo.isLaTeXDrawableGeo(latexStr)) {
					//try {//Widget will be custom
						if (geo.isGeoText())
							isSerif = ((GeoText) geo).isSerifFont();
						// App.debug(latexStr);
						app.getDrawEquation().drawLatexImageIcon(
								app,
								latexIcon,
								latexStr,
								getFont(),
								isSerif,
								geogebra.awt.GColorD.getAwtColor(geo
										.getAlgebraColor()), bgColor);
						setIcon(latexIcon);
						setText("");

					} catch (Exception e) {
						App.debug("error in drawing latex" + e);
					}
				}
			}

		}*/

		//retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
		//retwidget.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		//retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		return retwidget;
	}

	
	public void updateTableCell(Grid table, Object value,int row, int column) {

	
		// Get the cell geo, exit if null
		// ==================================================
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			table.setText(row, column, " ");
			return;
		}

		// Set text according to algebra style
		// ===============================================
		String text = "";
		String latex = null;
		if (geo.isIndependent()) {
			if (geo.isLaTeXDrawableGeo() &&
				(geo.isGeoList() ? !((GeoList)geo).isMatrix() : true) &&
				(geo.isGeoText() ? ((GeoText)geo).isLaTeX() : true)) {

				latex = geo.getLaTeXdescription(); 
			}
			text = geo.toValueString(StringTemplate.defaultTemplate);
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				if (geo.isLaTeXDrawableGeo() &&
					(geo.isGeoList() ? !((GeoList)geo).isMatrix() : true) &&
					(geo.isGeoText() ? ((GeoText)geo).isLaTeX() : true)) {

					latex = geo.getLaTeXdescription();
				}
				text = geo.toValueString(StringTemplate.defaultTemplate);
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = geo.getDefinitionDescription(StringTemplate.defaultTemplate);

				// Label accepts text, not HTML!
				//text = GeoElement
				//		.convertIndicesToHTML(geo
				//				.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = geo.getCommandDescription(StringTemplate.defaultTemplate);

				// Label accepts text, not HTML!
				//text = GeoElement.convertIndicesToHTML(geo
				//		.getCommandDescription(StringTemplate.defaultTemplate));
				break;

			}
		}

		if (view.allowSpecialEditor() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			if (geo.isGeoBoolean()) {
				FlowPanel fp = new FlowPanel();
				final CheckBox checkbox = new CheckBox();
				fp.add(checkbox);
				checkbox.getElement().getStyle().setBackgroundColor(table.getElement().getStyle().getBackgroundColor());
				checkbox.getElement().getStyle().setProperty("display", "-moz-inline-box");
				checkbox.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
				fp.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
				checkbox.setEnabled(geo.isIndependent());

				// styling to overcome the selection frame
				checkbox.getElement().getStyle().setPosition(Style.Position.RELATIVE);
				checkbox.getElement().getStyle().setZIndex(10);

				if (geo.isLabelVisible()) {
					// checkBox.setText(geo.getCaption());
				}
				checkbox.setValue(((GeoBoolean) geo).getBoolean());

				final GeoBoolean geoBoolean = (GeoBoolean) geo;

				checkbox.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent ce) {
						if (view.allowSpecialEditor()) {
							checkbox.setValue(!geoBoolean.getBoolean());
							geoBoolean.setValue(checkbox.getValue());
							geoBoolean.updateCascade();
						}
					}
				});

				
				table.setWidget(row, column, fp);
				return;
			}

			/*if (geo.isGeoButton()) {
				// button.setBackground(table.getBackground());
				button.setHorizontalAlignment(CENTER);
				button.setText(geo.getCaption(StringTemplate.defaultTemplate));
				button.setForeground(geogebra.awt.GColorD.getAwtColor(geo
						.getObjectColor()));
				return button;
			}*/

			if (geo.isGeoList()) {
				GeoList list = (GeoList) geo;
				ListBox lb = new ListBox();
				lb.setVisibleItemCount(1);
				lb.getElement().getStyle().setBackgroundColor(table.getElement().getStyle().getBackgroundColor());
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++)
						// toString doesn't work for some reason
						lb.addItem(list.get(i).toValueString(StringTemplate.defaultTemplate));
					lb.setSelectedIndex(list.getSelectedIndex());
				}

				table.setWidget(row, column, lb);
				lb.setWidth("100%");
				lb.setHeight("100%");
				return;
			}
		}

		if (latex == null) {
			table.setText(row, column, text);
		} else {
			InlineHTML widg = new InlineHTML();
			SpanElement wele = DOM.createSpan().cast();
			wele.getStyle().setProperty("display", "-moz-inline-box");
			wele.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			widg.getElement().appendChild(wele);

			table.setWidget(row, column, widg);

			latex = DrawEquationWeb.inputLatexCosmetics(latex);
			DrawEquationWeb.drawEquationAlgebraView(wele, "\\mathrm {"+latex+"}");
		}

		// it is also important to format the text!

		// 1. bold/italic
		cellPoint.setLocation(column - 1, row - 1);
		fontStyle = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_FONTSTYLE);
		if (fontStyle == null) {
			fontStyle = GFont.PLAIN;
		}
		GFont gf = app.getFontCanDisplay(text, fontStyle);
		//table.getCellFormatter().getElement(row, column).getStyle().setFontSize(gf.getSize(), Style.Unit.PX);
		table.getCellFormatter().getElement(row, column).getStyle().setFontStyle(
			gf.isItalic() ? Style.FontStyle.ITALIC : Style.FontStyle.NORMAL);
		table.getCellFormatter().getElement(row, column).getStyle().setFontWeight(
			gf.isBold() ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);

		// 2. foreground color
		if (geo.getLabelColor() != null)
			table.getCellFormatter().getElement(row, column).getStyle().setColor(
					geo.getLabelColor().toString());

		// 3. alignment
		alignment = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			table.getCellFormatter().getElement(row, column).getStyle().setProperty("textAlign",
				alignment == 2 ? "left" : (alignment == 4 ? "right" : "center"));
		} else if (geo.isGeoText()) {
			table.getCellFormatter().getElement(row, column).getStyle().setProperty("textAlign", "left");
		} else {
			table.getCellFormatter().getElement(row, column).getStyle().setProperty("textAlign", "right");
		}
	}

	public Widget getTableCellRendererWidget(Grid table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Widget retwidget = new Label();

		//TODO//setBorder(cellPadding);

		// so row and column in table model coordinates
		cellPoint.setLocation(column, row);

		//?//setIcon(emptyIcon);
		//?//setIconTextGap(0);

		// Set visible formats ... do this before exit with null geo
		// ==================================================
		// set default background color (adjust later if geo exists)

		bgColor = (GColor)formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_BGCOLOR);
		if (bgColor == null) {
			isCustomBGColor = false;
			bgColor = GColor.white;//table.getBackground();
		} else {
			isCustomBGColor = true;
		}
		if (bgColor != null)
			retwidget.getElement().getStyle().setBackgroundColor(bgColor.toString());

		// Get the cell geo, exit if null
		// ==================================================
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			((Label)retwidget).setText(" ");
			retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
			retwidget.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
			retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
			return retwidget;
		}

		// use special rendering for buttons, booleans and lists
		// =======================================================

		/*if (view.allowSpecialEditor()
				&& kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

			if (geo.isGeoBoolean()) {
				checkBox.setBackground(table.getBackground());
				checkBox.setHorizontalAlignment(CENTER);
				checkBox.setEnabled(geo.isIndependent());

				if (geo.isLabelVisible()) {
					// checkBox.setText(geo.getCaption());
				}
				checkBox.setSelected(((GeoBoolean) geo).getBoolean());

				return checkBox;
			}

			if (geo.isGeoButton()) {
				// button.setBackground(table.getBackground());
				button.setHorizontalAlignment(CENTER);
				button.setText(geo.getCaption(StringTemplate.defaultTemplate));
				button.setForeground(geogebra.awt.GColorD.getAwtColor(geo
						.getObjectColor()));
				return button;
			}

			if (geo.isGeoList()) {
				GeoList list = (GeoList) geo;
				comboBox.setBackground(table.getBackground());
				cbModel.removeAllElements();
				if (list.size() > 0)
					cbModel.addElement(list.get(list.getSelectedIndex()));
				// comboBox.setSelected(((GeoBoolean)geo).getBoolean());

				return comboBox;
			}
		}*/

		// ===============================================
		// (end special rendering)

		// Set text according to algebra style
		// ===============================================
		String text = "";
		if (geo.isIndependent()) {
			text = geo.toValueString(StringTemplate.defaultTemplate);
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = geo.toValueString(StringTemplate.defaultTemplate);
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = geo.getDefinitionDescription(StringTemplate.defaultTemplate);

				// Label accepts text, not HTML!
				//text = GeoElement
				//		.convertIndicesToHTML(geo
				//				.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = geo.getCommandDescription(StringTemplate.defaultTemplate);

				// Label accepts text, not HTML!
				//text = GeoElement.convertIndicesToHTML(geo
				//		.getCommandDescription(StringTemplate.defaultTemplate));
				break;

			}
		}

		// Set font
		// ===============================================
		fontStyle = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_FONTSTYLE);
		if (fontStyle == null)
			fontStyle = GFont.PLAIN;

		retwidget.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		((Label)retwidget).setText(text);

		GFont gf = app.getFontCanDisplay(text, fontStyle);
		((Label)retwidget).getElement().getStyle().setFontSize(gf.getSize(), Style.Unit.PX);
		((Label)retwidget).getElement().getStyle().setFontStyle(
			gf.isItalic() ? Style.FontStyle.ITALIC : Style.FontStyle.NORMAL);
		((Label)retwidget).getElement().getStyle().setFontWeight(
			gf.isBold() ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);

		// Set foreground and background color
		// ===============================================

		// use geo bgColor if there is no format bgColor
		if (geo.getBackgroundColor() != null && !isCustomBGColor) {
			bgColor = geo.getBackgroundColor();
			isCustomBGColor = true;
		}

		// adjust selection color when there is a bgColor
		if (geo.doHighlighting()) {
			if (isCustomBGColor) {
				bgColor = bgColor.darker();
			} else {
				bgColor = MyTableW.SELECTED_BACKGROUND_COLOR;
			}
		}
		if (bgColor != null)// here was an exception
			((Label)retwidget).getElement().getStyle().setBackgroundColor(bgColor.toString());

		if (geo.getLabelColor() != null)
			((Label)retwidget).getElement().getStyle().setColor(geo.getLabelColor().toString());

		// Set horizontal alignment
		// ===============================================
		alignment = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			((Label)retwidget).getElement().getStyle().setProperty("textAlign",
				alignment == 2 ? "left" : (alignment == 4 ? "right" : "center"));
		} else if (geo.isGeoText()) {
			((Label)retwidget).getElement().getStyle().setProperty("textAlign", "left");
		} else {
			((Label)retwidget).getElement().getStyle().setProperty("textAlign", "right");
		}

		// Set icons for LaTeX and images
		// ===============================================
		/*if (geo.isGeoImage()) {
			latexIcon.setImage(geogebra.awt.GBufferedImageD
					.getAwtBufferedImage(((GeoImage) geo).getFillImage()));
			setIcon(latexIcon);
			setHorizontalAlignment(SwingConstants.CENTER);
			setText("");

		} else {

			boolean isSerif = false;
			if (geo.isDefined()
					&& kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

				latexStr = geo.getFormulaString(StringTemplate.latexTemplate,
						true);
				if (geo.isLaTeXDrawableGeo(latexStr)) {
					//try {//Widget will be custom
						if (geo.isGeoText())
							isSerif = ((GeoText) geo).isSerifFont();
						// App.debug(latexStr);
						app.getDrawEquation().drawLatexImageIcon(
								app,
								latexIcon,
								latexStr,
								getFont(),
								isSerif,
								geogebra.awt.GColorD.getAwtColor(geo
										.getAlgebraColor()), bgColor);
						setIcon(latexIcon);
						setText("");

					} catch (Exception e) {
						App.debug("error in drawing latex" + e);
					}
				}
			}

		}*/

		retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
		retwidget.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
		return retwidget;
	}


	// ======================================================
	// ComboBox Cell Renderer
	// ======================================================

	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	/*private static class MyListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {

			setBackground(Color.WHITE);
			JLabel lbl = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, hasFocus);
			lbl.setHorizontalAlignment(LEFT);

			if (value != null) {
				GeoElement geo = (GeoElement) value;
				if (geo.isGeoText())
					setText(geo.toValueString(StringTemplate.defaultTemplate));
				else
					setText(geo.getLabel(StringTemplate.defaultTemplate));
			} else
				setText(" ");

			return lbl;
		}

	}*/

	/*
	 * // Set border // (not finished ... border cell formats need coding) //
	 * traceBorder = (Integer) formatHandler.getCellFormat(cellPoint, //
	 * CellFormat.FORMAT_TRACING);
	 * 
	 * if (traceBorder != null){
	 * 
	 * switch (traceBorder){ case CellFormat.BORDER_STYLE_ALL:
	 * setBorder(BorderFactory.createCompoundBorder(bAll, cellPadding)); break;
	 * case CellFormat.BORDER_STYLE_TOP:
	 * setBorder(BorderFactory.createCompoundBorder(bTop, cellPadding)); break;
	 * case CellFormat.BORDER_STYLE_LEFT:
	 * setBorder(BorderFactory.createCompoundBorder(bLeft, cellPadding)); break;
	 * case CellFormat.BORDER_STYLE_BOTTOM:
	 * setBorder(BorderFactory.createCompoundBorder(bBottom, cellPadding));
	 * break; case CellFormat.BORDER_STYLE_RIGHT:
	 * setBorder(BorderFactory.createCompoundBorder(bRight, cellPadding));
	 * break;
	 * 
	 * }
	 * 
	 * }else{ setBorder(cellPadding); }
	 */

}
