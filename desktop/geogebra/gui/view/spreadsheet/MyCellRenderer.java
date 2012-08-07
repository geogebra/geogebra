package geogebra.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

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

public class MyCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppD app;
	private Kernel kernel;
	private SpreadsheetView view;

	// LaTeX
	private ImageIcon latexIcon, emptyIcon;
	private String latexStr;

	// Cell formats
	private CellFormat formatHandler;
	private GPoint cellPoint;
	private Integer alignment = -1;
	private Integer traceBorder = -1;
	private Integer fontStyle;
	boolean isCustomBGColor;

	// Borders (not implemented yet)
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
	public MyCellRenderer(AppD app, SpreadsheetView view,
			CellFormat formatHandler) {

		this.app = app;
		this.kernel = app.getKernel();
		this.formatHandler = formatHandler;
		this.view = view;

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

		bgColor = (Color) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_BGCOLOR);
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

		if (view.allowSpecialEditor()
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
		}

		// ===============================================
		// (end special rendering)

		// Set text according to algebra style
		// ===============================================
		String text = null;
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
			fontStyle = Font.PLAIN;

		setText(text);
		setFont(app.getFontCanDisplayAwt(text, fontStyle));

		// Set foreground and background color
		// ===============================================

		// use geo bgColor if there is no format bgColor
		if (geo.getBackgroundColor() != null && !isCustomBGColor) {
			bgColor = geogebra.awt.GColorD
					.getAwtColor(geo.getBackgroundColor());
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
		setBackground(bgColor);
		setForeground(geogebra.awt.GColorD.getAwtColor(geo.getLabelColor()));

		// Set horizontal alignment
		// ===============================================
		alignment = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_ALIGN);
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
					try {
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
				if (geo.isGeoText())
					setText(geo.toValueString(StringTemplate.defaultTemplate));
				else
					setText(geo.getLabel(StringTemplate.defaultTemplate));
			} else
				setText(" ");

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