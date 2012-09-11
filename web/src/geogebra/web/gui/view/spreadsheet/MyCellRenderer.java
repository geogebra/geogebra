package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.CellFormat;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MyCellRenderer {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppW app;
	private Kernel kernel;
	private SpreadsheetView view;

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
	public MyCellRenderer(AppW app, SpreadsheetView view,
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

	public Widget getTableCellRendererWidget(Grid table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Widget retwidget = new Label();

		//TODO//setBorder(cellPadding);
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
			//TODO//bgColor = table.getBackground();
		} else {
			isCustomBGColor = true;
		}

		// Get the cell geo, exit if null
		// ==================================================
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			((Label)retwidget).setText("");
			if (bgColor != null)
				retwidget.getElement().getStyle().setBackgroundColor(bgColor.toString());
			retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
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
				text = GeoElement
						.convertIndicesToHTML(geo
								.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = GeoElement.convertIndicesToHTML(geo
						.getCommandDescription(StringTemplate.defaultTemplate));
				break;

			}
		}

		// Set font
		// ===============================================
		fontStyle = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_FONTSTYLE);
		if (fontStyle == null)
			fontStyle = GFont.PLAIN;

		((Label)retwidget).setText(text);
		if (bgColor != null)
			retwidget.getElement().getStyle().setBackgroundColor(bgColor.toString());
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

		} else {*/

			boolean isSerif = false;
			if (geo.isDefined()
					&& kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

				latexStr = geo.getFormulaString(StringTemplate.latexTemplate,
						true);
				if (geo.isLaTeXDrawableGeo(latexStr)) {
					/*TODO//try {//Widget will be custom
						if (geo.isGeoText())
							isSerif = ((GeoText) geo).isSerifFont();
						// System.out.println(latexStr);
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
					}*/
				}
			}

		/*}*/

		retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
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
