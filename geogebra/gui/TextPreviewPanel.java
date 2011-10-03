package geogebra.gui;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.kernel.AlgoDependentText;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.kernel.arithmetic.TextValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.parser.ParseException;
import geogebra.kernel.parser.TokenMgrError;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * 
 * Extension of EuclidianView that can be used to preview GeoText while editing.
 * 
 * The class maintains two hidden geos (previewGeoIndependent and
 * previewGeoDependent) that are used to preview the two possible types of
 * GeoText, independent and dependent.
 * 
 * 
 * @author gsturr 2010-6-30
 * 
 */
public class TextPreviewPanel extends EuclidianView {

	private EuclidianController ec;
	private static boolean[] showAxes = { false, false };
	private static boolean showGrid = false;
	private GeoText previewGeoIndependent, previewGeoDependent;

	private AlgoDependentText textAlgo;
	private Construction cons ;
	private boolean isIndependent;


	public TextPreviewPanel(Kernel kernel) {

		super(new EuclidianController(kernel), showAxes, showGrid, EuclidianView.EVNO_GENERAL);
		this.ec = this.getEuclidianController();

		this.cons = kernel.getConstruction();

		// set EV display properties 
		setAntialiasing(true);
		setAllowShowMouseCoords(false);
		setAxesCornerCoordsVisible(false);
		updateFonts();
		updateSize();

		// remove EV mouse listeners
		removeMouseListener(ec);
		removeMouseMotionListener(ec);
		removeMouseWheelListener(ec);

	}


	/**
	 * Removes the preview geos
	 */
	public void removePreviewGeoText() {
		if(previewGeoIndependent != null){
			this.remove(previewGeoIndependent);
			previewGeoIndependent.remove();
			previewGeoIndependent = null;
		}
		if(previewGeoDependent != null){
			this.remove(previewGeoDependent);
			previewGeoDependent.remove();
			previewGeoDependent = null;
			textAlgo.remove();
		}
		this.repaint();
	}


	/**
	 * Overrides attachView with an empty method to prevent this panel from
	 * attaching to the kernel
	 */
	@Override
	public void attachView() {

	}



	/**
	 * Updates the preview geos and creates new geos if needed.
	 * Changes are determined by the inputValue string and the visual style of the targetGeo. 
	 *  
	 * @param targetGeo
	 * @param inputValue
	 * @param isLaTeX
	 */
	public void updatePreviewText(GeoText targetGeo, String inputValue, boolean isLaTeX) {

		//Application.printStacktrace("inputValue: " + inputValue);
		// initialize variables
		ValidExpression exp = null;
		ExpressionValue eval = null;	
		boolean hasParseError = false;
		boolean showErrorMessage = false;
		isIndependent = false;

		// create previewGeoIndependent 
		if (previewGeoIndependent == null){
			previewGeoIndependent = new GeoText(kernel.getConstruction());
			previewGeoIndependent.setFontSize(0);
			previewGeoIndependent.addView(this.getViewID());
			add(previewGeoIndependent);
		}


		// prepare the input string for processing
		//String formattedInput = formatInputValue(inputValue);	


		// parse the input text 
		try{
			//Application.debug("parsing: "+inputValue);
			exp = kernel.getParser().parseGeoGebraExpression(inputValue);
		}

		catch (ParseException e) {
			isIndependent = true;
			hasParseError = true;  	
			if(inputValue.length() > 0) // no error message if we have an empty string
				showErrorMessage = true;
			//Application.debug("parse exception");
		} 
		catch (MyError e) {
			isIndependent = true;
			hasParseError = true;  // odd numbers of quotes give parse errors 
			showErrorMessage = true;
			//Application.debug("parse error");
		} catch (TokenMgrError e) {
			//Application.debug("parse error");
			isIndependent = true;
			hasParseError = true;  // odd numbers of quotes give parse errors 
			showErrorMessage = true;
		}



		// resolve variables and evaluate the expression
		if(!(hasParseError)){
			try
			{
				exp.resolveVariables();
				isIndependent = exp.isConstant();
				eval = exp.evaluate();
			} 

			catch (Error e) {
				isIndependent = true;
				showErrorMessage = true;
				//Application.debug("resolve error:" + e.getCause());
			} 
			catch (Exception e) {
				showErrorMessage = true;
				isIndependent = true;
				//Application.debug("resolve exception");
			}
		}


		// ====================================
		// update the preview Geo

		// case1: independent text based on string only, including error messages
		if (isIndependent) 
		{
			// set the text string for the geo
			String text = "";			
			if(showErrorMessage){
				text  = app.getError("InvalidInput");
			} else if(eval != null){
				MyStringBuffer eval2 = ((TextValue) eval).getText();
				text = eval2.toValueString();
			}				
			previewGeoIndependent.setTextString(text);

			// update the display style
			updateVisualProperties(previewGeoIndependent, targetGeo, isLaTeX, showErrorMessage);
		} 


		// case 2: dependent GeoText, needs AlgoDependentText	
		else
		{
			if(previewGeoDependent != null){
				this.remove(previewGeoDependent);
				previewGeoDependent.remove();
				textAlgo.remove();
			}

			// create new  previewGeoDependent 
			textAlgo = new AlgoDependentText(cons, (ExpressionNode) exp);
			cons.removeFromConstructionList(textAlgo);
			previewGeoDependent = textAlgo.getGeoText();
			previewGeoDependent.addView(this.getViewID());
			add(previewGeoDependent);

			// set the display style
			updateVisualProperties(previewGeoDependent, targetGeo, isLaTeX, showErrorMessage);
		}



		// hide/show the preview geos  
		previewGeoIndependent.setEuclidianVisible(isIndependent);
		previewGeoIndependent.updateRepaint();
		this.update(previewGeoIndependent);
		if(previewGeoDependent !=null){
			previewGeoDependent.setEuclidianVisible(!isIndependent);
			previewGeoDependent.updateRepaint();
			this.update(previewGeoDependent);
		}

		// update the panel size to match the geo
		if(previewGeoIndependent != null && previewGeoIndependent.isEuclidianVisible())
			updateViewportSize(previewGeoIndependent);
		if(previewGeoDependent != null && previewGeoDependent.isEuclidianVisible())
			updateViewportSize(previewGeoDependent);

		this.repaintView();

	}



	private Dimension d = new Dimension();
	private ImageIcon testIcon = new ImageIcon();
	private JTextPane dummyText = new JTextPane();
	private int padding = 5;  // account for inset
	/**
	 * Updates the preferred size of this panel to match the estimated size of
	 * the given preview geo. This forces the enclosing scrollpane to show
	 * scrollbars when the size of the preview geo grows larger than the
	 * scrollpane viewport.
	 * 
	 * Note: The preview geo uses absolute screen coords, so we can't easily get
	 * the bounding box dimensions and must use dummy containers to estimate
	 * these dimensions.
	 * 
	 * @param previewGeo
	 */
	private void updateViewportSize(GeoText previewGeo){
		if(previewGeo == null) return;

		if(previewGeo.isLaTeX()){  
			// LaTex geo, use dummy ImageIcon 
			
			GeoGebraIcon.drawLatexImageIcon(app, testIcon, previewGeo.getTextString(), app.getPlainFont(), true, Color.black, null);
			//System.out.println("=============> " + testIcon.getIconHeight() + " : " + testIcon.getIconWidth());

			// get the dimensions from the icon and add some padding
			d.height = testIcon.getIconHeight() + padding;
			d.width = testIcon.getIconWidth() + padding;


		}else {  
			// Plain text geo, use dummy JTextArea
			
			// set font and line spacing (guessing at this value)
			dummyText.setFont(app.getPlainFont());
			MutableAttributeSet set = new SimpleAttributeSet();
			StyleConstants.setLineSpacing(set, (float) 1);
			//StyleConstants.setSpaceBelow(set, (float) 0.5);
			dummyText.setParagraphAttributes(set, true);
			

			dummyText.setText(previewGeo.getTextString());
			d = dummyText.getPreferredSize();

			// add some padding 
			d.height += padding;
			d.width += padding;
		}

		// update this panel 
		this.setPreferredSize(d);
		this.revalidate();


	}






	/**
	 * Sets the visual properties of a preview geo
	 */
	private void updateVisualProperties(GeoText geo, GeoText targetGeo, boolean isLaTeX, boolean isErrorMessage ){

		// set error message style
		if(isErrorMessage)
		{
			geo.setVisualStyle(cons.getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_TEXT));					
			geo.setObjColor(Color.red);
			geo.setBackgroundColor(Color.white);
			//geo.setFontSize(app.getFontSize());
			geo.setFontStyle(Font.ITALIC);
			geo.setLaTeX(false, true);
		}

		// set text style
		else
		{	
			if(targetGeo != null){
				geo.setVisualStyle(targetGeo);
			} else {
				if (isLaTeX) geo.setSerifFont(true);
				geo.setObjColor(Color.black);
			}
			geo.setLaTeX(isLaTeX, true);
		}

		// set geo position in upper left corner (it might need changing after isLaTeX change)
		locateTextGeo(geo);

	}



	/**
	 * Positions the preview geo in the upper left corner of the panel
	 * two settings are needed to account for differences in the way
	 * LaTeX and standard text is drawn
	 */
	private void locateTextGeo(GeoText geo) {
		int xInset = 4;
		int yInset = geo.isLaTeX() ? 4 : 4 + app.getGUIFontSize();
		geo.setAbsoluteScreenLocActive(true);
		geo.setAbsoluteScreenLoc( xInset, yInset);
	}


	/**
	 * Prepares the inputValue string for the parser
	 */
	private String formatInputValue(String inputValue){

		// if inputValue is null then use the current definition
		if(inputValue == null){
			//System.out.println("=== null input === ");
			if(previewGeoIndependent.isIndependent()){ 
				inputValue = previewGeoIndependent.getTextString();
				if(previewGeoIndependent.getKernel().lookupLabel(inputValue) != null)
					inputValue = "\"" + inputValue + "\"";            		 		
			}
			else{
				inputValue = previewGeoIndependent.getCommandDescription(); 
			}				
		} 

		// inputValue is not null, so process it as done in TextInputDialog ---> TextInputHandler 	
		else {

			// no quotes?
			if (inputValue.indexOf('"') < 0) 
			{	
				// this should become either
				// (1) a + "" where a is an object label or
				// (2) "text", a plain text 

				// ad (1) OBJECT LABEL 
				// add empty string to end to make sure
				// that this will become a text object
				if (kernel.lookupLabel(inputValue.trim()) != null) {
					inputValue = "(" + inputValue + ") + \"\"";
				} 

				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					inputValue = "\"" + inputValue + "\"";
				}        			
			} 

			else 
			{
				// replace \n\" by \"\n, this is useful for e.g.:
				//    "a = " + a + 
				//	"b = " + b 
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}
		}

		return inputValue;
	}

	public int getViewID() {
		return Application.VIEW_TEXT_PREVIEW;
	}





}
