package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.Drawable;
import geogebra.euclidian.FormulaDimension;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

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
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class MyCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;

	// ggb fields
	private Application app;
	private Kernel kernel;
	private SpreadsheetView view;

	// LaTeX
	private ImageIcon latexIcon, emptyIcon; 
	private String latexStr;

	// Cell formats
	private CellFormat formatHandler;
	private Point cellPoint;
	private Integer alignment = -1;
	private Integer traceBorder = -1;
	private Integer fontStyle;
	boolean isCustomBGColor;

	// Borders (not implemented yet)
	private Border cellPadding = BorderFactory.createEmptyBorder(2, 5, 2, 5);
	private Border bTop = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.RED);
	private Border bLeft = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.RED);
	private Border bBottom = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.RED);
	private Border bRight = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.RED);
	private Border bAll = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED);

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
	 * @param app
	 * @param view
	 * @param formatHandler
	 */
	public MyCellRenderer(Application app, SpreadsheetView view, CellFormat formatHandler) {

		this.app = app;		
		this.kernel = app.getKernel();
		this.formatHandler =  formatHandler;
		this.view = view;

		//Add horizontal padding
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));


		// The cell renderer extends JLabel...its icon is used to display LaTeX.
		latexIcon = new ImageIcon();
		emptyIcon = new ImageIcon();

		cellPoint = new Point(); // used for cell format calls

		// Rendering for booleans, buttons and lists
		checkBox = new JCheckBox();
		button = new JButton();
		comboBox = new JComboBox();
		comboBox.setRenderer(new MyListCellRenderer());

		cbModel = new DefaultComboBoxModel();
		comboBox.setModel(cbModel);
	}



	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{	

		setBorder(cellPadding);		
		cellPoint.setLocation(column, row);
		setIcon(emptyIcon);
		setIconTextGap(0);


		// Set visible formats ... do this before exit with null geo
		// ==================================================
		// set default background color (adjust later if geo exists)

		bgColor = (Color) formatHandler.getCellFormat(cellPoint, 
				CellFormat.FORMAT_BGCOLOR);	
		if(bgColor == null){
			isCustomBGColor = false;
			bgColor = table.getBackground();
		} else {
			isCustomBGColor = true;
		}
		setBackground(bgColor);



		// Get the cell geo, exit if null
		// ==================================================
		if (value != null) {
			geo = (GeoElement)value;
		}else{
			setText("");
			return this;
		}



		// use special rendering for buttons, booleans and lists
		//=======================================================

		if(view.allowSpecialEditor() && kernel.getAlgebraStyle()==Kernel.ALGEBRA_STYLE_VALUE){

			if(geo.isGeoBoolean()){
				checkBox.setBackground(table.getBackground());
				checkBox.setHorizontalAlignment(CENTER);
				checkBox.setEnabled(geo.isIndependent());

				if(geo.isLabelVisible()){					
					//checkBox.setText(geo.getCaption());
				}
				checkBox.setSelected(((GeoBoolean)geo).getBoolean());

				return checkBox;
			}

			if(geo.isGeoButton()){
				//button.setBackground(table.getBackground());
				button.setHorizontalAlignment(CENTER);
				button.setText(geo.getCaption());
				button.setForeground(geo.getObjectColor());
				return button;
			}

			if(geo.isGeoList()){
				GeoList list = (GeoList)geo;
				comboBox.setBackground(table.getBackground());
				cbModel.removeAllElements();
				if(list.size()>0)
					cbModel.addElement(list.get(list.getSelectedIndex()));
				//comboBox.setSelected(((GeoBoolean)geo).getBoolean());

				return comboBox;
			}
		}

		//===============================================
		// (end special rendering)



		// Set text according to algebra style
		//===============================================
		String text = null;
		if (geo.isIndependent()) {
			text = geo.toValueString();
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = geo.toValueString();
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = GeoElement.convertIndicesToHTML(geo.getDefinitionDescription());
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = GeoElement.convertIndicesToHTML(geo.getCommandDescription());
				break;
			}	
		}


		// Set font
		//===============================================
		fontStyle = (Integer) formatHandler.getCellFormat(cellPoint, 
				CellFormat.FORMAT_FONTSTYLE);
		if(fontStyle == null)
			fontStyle = Font.PLAIN;

		setText(text);
		setFont(app.getFontCanDisplay(text, fontStyle));




		// Set foreground and background color
		//===============================================
		if (geo.getBackgroundColor() != null) {
			bgColor = geo.getBackgroundColor();
		}

		if (geo.doHighlighting()) {
			if(isCustomBGColor){
				bgColor = bgColor.darker();
			}else{
				bgColor = MyTable.SELECTED_BACKGROUND_COLOR;
			}
		}
		setBackground(bgColor);
		setForeground(geo.getAlgebraColor());




		// Set horizontal alignment
		//===============================================
		alignment = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			setHorizontalAlignment(alignment);
		} else if (geo.isGeoText()) {
			setHorizontalAlignment(JLabel.LEFT);
		} else {
			setHorizontalAlignment(JLabel.RIGHT);
		}	



		// Set icons for LaTeX and images
		//===============================================
		if(geo.isGeoImage()){		
			latexIcon.setImage(((GeoImage) geo).getFillImage());
			setIcon(latexIcon);
			setHorizontalAlignment(this.CENTER);
			setText("");

		}else{

			boolean isSerif = false;
			if (geo.isDefined() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

				latexStr = geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);
				if (geo.isLaTeXDrawableGeo(latexStr)) {
					try {
						if(geo.isGeoText())
							isSerif = ((GeoText)geo).isSerifFont();
						//System.out.println(latexStr);
						drawLatexImageIcon(latexIcon, latexStr, getFont(), isSerif, geo
								.getAlgebraColor(), bgColor);
						setIcon(latexIcon);
						setText("");

					} catch (Exception e) {
						Application.debug("error in drawing latex" + e);
					}
				}
			}

		}

		return this;
	}





	/**
	 * Draw a LaTeX image in the cell icon. Drawing is done twice. First draw gives 
	 * the needed size of the image. Second draw renders the image with the correct
	 * dimensions.
	 */
	private void drawLatexImageIcon(ImageIcon latexIcon, String latex, Font font, boolean serif, Color fgColor, Color bgColor) {

		// Create image with dummy size, then draw into it to get the correct size
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		FormulaDimension d = new FormulaDimension();
		d = app.getDrawEquation().drawEquation(app, null, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor, true);

		// Now use this size and draw again to get the final image
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		d = app.getDrawEquation().drawEquation(app, null, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor, true);

		latexIcon.setImage(image);

	}





	//======================================================
	//         ComboBox Cell Renderer 
	//======================================================

	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	private static class MyListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {

			setBackground(Color.WHITE);
			JLabel lbl = (JLabel)super.getListCellRendererComponent(
					list, value, index, isSelected, hasFocus);
			lbl.setHorizontalAlignment(LEFT);

			if (value != null) {
				GeoElement geo = (GeoElement) value;
				if(geo.isGeoText())
					setText(geo.toValueString());
				else
					setText(geo.getLabel());
			} else
				setText(" ");

			return lbl;
		}

	}



	/*
	// Set border
	// (not finished ... border cell formats need coding)
//	traceBorder = (Integer) formatHandler.getCellFormat(cellPoint,
		//	CellFormat.FORMAT_TRACING);

	if (traceBorder != null){

		switch (traceBorder){
		case CellFormat.BORDER_STYLE_ALL:
			setBorder(BorderFactory.createCompoundBorder(bAll, cellPadding));
		break;
		case CellFormat.BORDER_STYLE_TOP:
			setBorder(BorderFactory.createCompoundBorder(bTop, cellPadding));
		break;
		case CellFormat.BORDER_STYLE_LEFT:
			setBorder(BorderFactory.createCompoundBorder(bLeft, cellPadding));
		break;
		case CellFormat.BORDER_STYLE_BOTTOM:
			setBorder(BorderFactory.createCompoundBorder(bBottom, cellPadding));
		break;
		case CellFormat.BORDER_STYLE_RIGHT:
			setBorder(BorderFactory.createCompoundBorder(bRight, cellPadding));
		break;

		}

	}else{
		setBorder(cellPadding);	
	}

	 */




}