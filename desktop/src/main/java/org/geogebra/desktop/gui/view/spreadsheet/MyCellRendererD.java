package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.CellFormat;
import org.geogebra.common.gui.view.spreadsheet.MyTableInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;

public class MyCellRendererD extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppD app;
	private Kernel kernel;
	// private SpreadsheetView view;
	private MyTableInterface myTable;

	// LaTeX
	private ImageIcon latexIcon, emptyIcon;
	private String latexStr;

	// Cell formats
	private CellFormat formatHandler;
	private GPoint cellPoint;
	private Integer alignment = -1;
	private Integer fontStyle;
	boolean isCustomBGColor;

	// Borders (not implemented yet)
	private Border cellPadding = BorderFactory.createEmptyBorder(2, 5, 2, 5);


	// Rendering objects for lists, buttons and booleans
	private JCheckBox checkBox;
	private JButton button;
	private JComboBox comboBox;
	private DefaultComboBoxModel cbModel;
	private Color bgColor;

	// Cell geo
	private GeoElement geo;

	/*********************************************************
	 * Constructor
	 * 
	 * @param app
	 * @param view
	 * @param formatHandler
	 */
	public MyCellRendererD(MyTableInterface table) {

		this.myTable = table;
		app = (AppD) myTable.getApplication();
		kernel = app.getKernel();
		formatHandler = (CellFormat) myTable.getCellFormatHandler();

		// Add horizontal padding
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		// The cell renderer extends JLabel...its icon is used to display LaTeX.
		latexIcon = new ImageIcon();
		emptyIcon = new ImageIcon();

		cellPoint = new GPoint(); // used for cell format calls

		// Rendering for booleans, buttons and lists
		checkBox = new JCheckBox();
		button = new JButton();
		comboBox = new JComboBox();
		comboBox.setRenderer(new MyListCellRenderer());

		cbModel = new DefaultComboBoxModel();
		comboBox.setModel(cbModel);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		setBorder(cellPadding);
		cellPoint.setLocation(column, row);
		setIcon(emptyIcon);
		setIconTextGap(0);

		// Set visible formats ... do this before exit with null geo
		// ==================================================
		// set default background color (adjust later if geo exists)

		Object c = formatHandler.getCellFormat(column, row,
				CellFormat.FORMAT_BGCOLOR);

		if (c instanceof GColor) {
			GColor col = (GColor) c;
			bgColor = GColorD.getAwtColor(col);
		} else {
			bgColor = null;
			// Log.error("problem " + ((c == null) ? "null" : "" +
			// c.getClass()));
		}

		// bgColor = GColorD.getAwtColor(((GColor) formatHandler
		// .getCellFormat(column, row, CellFormat.FORMAT_BGCOLOR));
		if (bgColor == null) {
			isCustomBGColor = false;
			bgColor = table.getBackground();
		} else {
			isCustomBGColor = true;
		}
		setBackground(bgColor);

		// Get the cell geo, exit if null
		// ==================================================
		if (value != null) {
			geo = (GeoElement) value;
		} else {
			setText("");
			return this;
		}

		// use special rendering for buttons, booleans and lists
		// =======================================================

		if (myTable.allowSpecialEditor() && kernel
				.getAlgebraStyleSpreadsheet() == Kernel.ALGEBRA_STYLE_VALUE) {

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
				button.setForeground(GColorD.getAwtColor(geo.getObjectColor()));
				return button;
			}

			if (geo.isGeoList()) {
				GeoList list = (GeoList) geo;
				comboBox.setBackground(table.getBackground());
				cbModel.removeAllElements();
				if (list.size() > 0)
				 {
					cbModel.addElement(list.get(list.getSelectedIndex()));
				// comboBox.setSelected(((GeoBoolean)geo).getBoolean());
				}

				return comboBox;
			}
		}

		// ===============================================
		// (end special rendering)

		// Set text according to algebra style
		// ===============================================
		String text = null;
		if (!geo.isEmptySpreadsheetCell()) {
			if (geo.isIndependent()) {
				text = geo.toValueString(StringTemplate.defaultTemplate);
			} else {

				switch (kernel.getAlgebraStyleSpreadsheet()) {
				default:
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = geo.toValueString(StringTemplate.defaultTemplate);
					break;

				case Kernel.ALGEBRA_STYLE_DESCRIPTION:
					IndexHTMLBuilder builder = new IndexHTMLBuilder(true);
					IndexHTMLBuilder
							.convertIndicesToHTML(
									geo.getDefinitionDescription(
											StringTemplate.defaultTemplate),
									builder);
					text = builder.toString();
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					builder = new IndexHTMLBuilder(true);
					IndexHTMLBuilder.convertIndicesToHTML(
							geo.getDefinition(StringTemplate.defaultTemplate),
							builder);
					text = builder.toString();

					break;
				}
			}
		}

		// Set font
		// ===============================================
		fontStyle = (Integer) formatHandler.getCellFormat(cellPoint.x,
				cellPoint.y, CellFormat.FORMAT_FONTSTYLE);
		if (fontStyle == null) {
			fontStyle = Font.PLAIN;
		}

		setText(text);
		setFont(app.getFontCanDisplayAwt(text, fontStyle));

		// Set foreground and background color
		// ===============================================

		// use geo bgColor if there is no format bgColor
		if (geo.getBackgroundColor() != null && !isCustomBGColor) {
			bgColor = GColorD.getAwtColor(geo.getBackgroundColor());
			isCustomBGColor = true;
		}

		// adjust selection color when there is a bgColor
		if (geo.doHighlighting()) {
			if (isCustomBGColor) {
				bgColor = bgColor.darker();
			} else {
				bgColor = MyTableD.SELECTED_BACKGROUND_COLOR;
			}
		}

		// for testing
		// if(geo.isEmptySpreadsheetCell()){
		// bgColor = MyTableD.SELECTED_BACKGROUND_COLOR_HEADER;
		// }

		setBackground(bgColor);
		setForeground(GColorD.getAwtColor(geo.getLabelColor()));

		// Set horizontal alignment
		// ===============================================
		alignment = (Integer) formatHandler.getCellFormat(cellPoint.x,
				cellPoint.y, CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			setHorizontalAlignment(alignment);
		} else if (geo.isGeoText()) {
			setHorizontalAlignment(SwingConstants.LEFT);
		} else {
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		// Set icons for LaTeX and images
		// ===============================================
		if (geo.isGeoImage()) {

			if (((MyImageD) ((GeoImage) geo).getFillImage()).isSVG()) {
				Log.error(
						"SVG not supported in the spreadsheet in desktop yet");
			} else {

				Image im = ((MyImageD) ((GeoImage) geo).getFillImage())
						.getImage();

				latexIcon.setImage(im);
				setIcon(latexIcon);
			}
			setHorizontalAlignment(SwingConstants.CENTER);
			setText("");

		} else {

			boolean isSerif = false;
			if (geo.isDefined()
					&& kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

				latexStr = geo.getFormulaString(StringTemplate.latexTemplate,
						true);
				if (geo.isLaTeXDrawableGeo()) {
					try {
						if (geo.isGeoText()) {
							isSerif = ((GeoText) geo).isSerifFont();
						}
						// System.out.println(latexStr);
						app.getDrawEquation().drawLatexImageIcon(app, latexIcon,
								latexStr, getFont(), isSerif,
								GColorD.getAwtColor(geo.getAlgebraColor()),
								bgColor);
						setIcon(latexIcon);
						setText("");

					} catch (Exception e) {
						Log.debug("error in drawing latex" + e);
					}
				}
			}

		}

		return this;
	}

	// ======================================================
	// ComboBox Cell Renderer
	// ======================================================

	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	private static class MyListCellRenderer extends DefaultListCellRenderer {
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
				if (geo.isGeoText()) {
					setText(geo.toValueString(StringTemplate.defaultTemplate));
				} else {
					setText(geo.getLabel(StringTemplate.defaultTemplate));
				}
			} else {
				setText(" ");
			}

			return lbl;
		}

	}

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