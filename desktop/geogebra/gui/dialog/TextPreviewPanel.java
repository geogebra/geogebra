package geogebra.gui.dialog;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentText;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.arithmetic.TextValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.kernel.parser.TokenMgrError;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.util.GeoGebraIcon;

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
public class TextPreviewPanel extends EuclidianViewD {

	private final EuclidianControllerD ec;
	private static boolean[] showAxes = { false, false };
	private static boolean showGrid = false;
	private GeoText previewGeoIndependent, previewGeoDependent;

	private AlgoDependentText textAlgo;
	private final Construction cons;
	private boolean isIndependent;

	public TextPreviewPanel(Kernel kernel) {

		super(new EuclidianControllerD(kernel), showAxes, showGrid,
				EuclidianView.EVNO_GENERAL, null);
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
		if (previewGeoIndependent != null) {
			this.remove(previewGeoIndependent);
			previewGeoIndependent.remove();
			previewGeoIndependent = null;
		}
		if (previewGeoDependent != null) {
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
	 * Updates the preview geos and creates new geos if needed. Changes are
	 * determined by the inputValue string and the visual style of the
	 * targetGeo.
	 * 
	 * @param targetGeo
	 * @param inputValue
	 * @param isLaTeX
	 */
	public void updatePreviewText(GeoText targetGeo, String inputValue,
			boolean isLaTeX) {

		// Application.printStacktrace("inputValue: " + inputValue);
		// initialize variables
		ValidExpression exp = null;
		StringTemplate tpl = targetGeo == null ? StringTemplate.defaultTemplate : 
			targetGeo.getStringTemplate();
		ExpressionValue eval = null;
		boolean hasParseError = false;
		boolean showErrorMessage = false;
		isIndependent = false;

		// create previewGeoIndependent
		if (previewGeoIndependent == null) {
			previewGeoIndependent = new GeoText(kernel.getConstruction());
			previewGeoIndependent.setFontSizeMultiplier(1.0);
			previewGeoIndependent.addView(this.getViewID());
			add(previewGeoIndependent);
		}

		// prepare the input string for processing
		// String formattedInput = formatInputValue(inputValue);

		// parse the input text
		try {
			// Application.debug("parsing: "+inputValue);
			exp = kernel.getParser().parseGeoGebraExpression(inputValue);
		}

		catch (ParseException e) {
			isIndependent = true;
			hasParseError = true;
			if (inputValue.length() > 0) {
				showErrorMessage = true;
				// Application.debug("parse exception");
			}
		} catch (MyError e) {
			isIndependent = true;
			hasParseError = true; // odd numbers of quotes give parse errors
			showErrorMessage = true;
			// Application.debug("parse error");
		} catch (TokenMgrError e) {
			// Application.debug("parse error");
			isIndependent = true;
			hasParseError = true; // odd numbers of quotes give parse errors
			showErrorMessage = true;
		}

		// resolve variables and evaluate the expression
		if (!(hasParseError)) {
			try {
				exp.resolveVariables(false);
				isIndependent = exp.isConstant();
				eval = exp.evaluate(tpl);
			}

			catch (Error e) {
				isIndependent = true;
				showErrorMessage = true;
				// Application.debug("resolve error:" + e.getCause());
			} catch (Exception e) {
				showErrorMessage = true;
				isIndependent = true;
				// Application.debug("resolve exception");
			}
		}

		// ====================================
		// update the preview Geo

		// case1: independent text based on string only, including error
		// messages
		if (isIndependent) {
			// set the text string for the geo
			String text = "";
			if (showErrorMessage) {
				text = getApplication().getLocalization().getError("InvalidInput");
			} else if (eval != null) {
				MyStringBuffer eval2 = ((TextValue) eval).getText();
				text = eval2.toValueString(tpl);
			}
			previewGeoIndependent.setTextString(text);

			// update the display style
			updateVisualProperties(previewGeoIndependent, targetGeo, isLaTeX,
					showErrorMessage);
		}

		// case 2: dependent GeoText, needs AlgoDependentText
		else {
			if (previewGeoDependent != null) {
				this.remove(previewGeoDependent);
				previewGeoDependent.remove();
				textAlgo.remove();
			}


			// if eg FormulaText["\sqrt{x}"] is entered, it should be displayed in the preview as LaTeX
			// NB FormulaText[a] is displayed as-is
			// FormulaText[a]+"" needs to have LaTeX box manually checked
			if (exp.evaluate(tpl).isGeoElement() &&
					((GeoText)(exp.evaluate(tpl))).isLaTeXTextCommand()) {
				isLaTeX = true;
			}


			// create new previewGeoDependent
			textAlgo = new AlgoDependentText(cons, (ExpressionNode) exp);
			cons.removeFromConstructionList(textAlgo);
			previewGeoDependent = textAlgo.getGeoText();
			previewGeoDependent.addView(this.getViewID());
			add(previewGeoDependent);

			// set the display style
			updateVisualProperties(previewGeoDependent, targetGeo, isLaTeX,
					showErrorMessage);
			//needed to reflect change of significant digits
			textAlgo.update();
		}

		// hide/show the preview geos
		previewGeoIndependent.setEuclidianVisible(isIndependent);
		previewGeoIndependent.updateRepaint();
		this.update(previewGeoIndependent);
		if (previewGeoDependent != null) {
			previewGeoDependent.setEuclidianVisible(!isIndependent);
			previewGeoDependent.updateRepaint();
			this.update(previewGeoDependent);
		}

		// update the panel size to match the geo
		if ((previewGeoIndependent != null)
				&& previewGeoIndependent.isEuclidianVisible()) {
			updateViewportSize(previewGeoIndependent);
		}
		if ((previewGeoDependent != null)
				&& previewGeoDependent.isEuclidianVisible()) {
			updateViewportSize(previewGeoDependent);
		}

		this.repaintView();

	}

	private Dimension d = new Dimension();
	private final ImageIcon testIcon = new ImageIcon();
	private final JTextPane dummyText = new JTextPane();
	private final int padding = 5; // account for inset

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
	private void updateViewportSize(GeoText previewGeo) {
		if (previewGeo == null) {
			return;
		}

		if (previewGeo.isLaTeX()) {
			// LaTex geo, use dummy ImageIcon

			GeoGebraIcon.drawLatexImageIcon(getApplication(), testIcon,
					previewGeo.getTextString(), getApplication().getPlainFont(), true,
					Color.black, null);
			// System.out.println("=============> " + testIcon.getIconHeight() +
			// " : " + testIcon.getIconWidth());

			// get the dimensions from the icon and add some padding
			d.height = testIcon.getIconHeight() + padding;
			d.width = testIcon.getIconWidth() + padding;

		} else {
			// Plain text geo, use dummy JTextArea

			// set font and line spacing (guessing at this value)
			dummyText.setFont(getApplication().getPlainFont());
			MutableAttributeSet set = new SimpleAttributeSet();
			StyleConstants.setLineSpacing(set, 1);
			// StyleConstants.setSpaceBelow(set, (float) 0.5);
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
	private void updateVisualProperties(GeoText geo, GeoText targetGeo,
			boolean isLaTeX, boolean isErrorMessage) {

		// set error message style
		if (isErrorMessage) {
			geo.setVisualStyle(cons.getConstructionDefaults().getDefaultGeo(
					ConstructionDefaults.DEFAULT_TEXT));
			geo.setObjColor(new geogebra.awt.GColorD(Color.red));
			geo.setBackgroundColor(new geogebra.awt.GColorD(Color.white));
			// geo.setFontSize(app.getFontSize());
			geo.setFontStyle(Font.ITALIC);
			geo.setLaTeX(false, true);
		}

		// set text style
		else {
			if (targetGeo != null) {
				geo.setVisualStyle(targetGeo);
			} else {
				if (isLaTeX) {
					geo.setSerifFont(true);
				}
				geo.setObjColor(new geogebra.awt.GColorD(Color.black));
			}
			geo.setLaTeX(isLaTeX, true);
		}

		// set geo position in upper left corner (it might need changing after
		// isLaTeX change)
		locateTextGeo(geo);
	}

	/**
	 * Positions the preview geo in the upper left corner of the panel two
	 * settings are needed to account for differences in the way LaTeX and
	 * standard text is drawn
	 */
	private static void locateTextGeo(GeoText geo) {
		int xInset = 4;
		int yInset = (int) (geo.isLaTeX() ? 4 : 18 + 12 * (geo.getFontSizeMultiplier() - 1));
		geo.setAbsoluteScreenLocActive(true);
		geo.setAbsoluteScreenLoc(xInset, yInset);
	}

	/**
	 * Prepares the inputValue string for the parser
	 */
	private String formatInputValue(String inputValue) {

		// if inputValue is null then use the current definition
		if (inputValue == null) {
			// System.out.println("=== null input === ");
			if (previewGeoIndependent.isIndependent()) {
				inputValue = previewGeoIndependent.getTextString();
				if (previewGeoIndependent.getKernel().lookupLabel(inputValue) != null) {
					inputValue = "\"" + inputValue + "\"";
				}
			} else {
				inputValue = previewGeoIndependent.getCommandDescription(null);//kernel.getStringTemplate());
			}
		}

		// inputValue is not null, so process it as done in TextInputDialog --->
		// TextInputHandler
		else {

			// no quotes?
			if (inputValue.indexOf('"') < 0) {
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

			else {
				// replace \n\" by \"\n, this is useful for e.g.:
				// "a = " + a +
				// "b = " + b
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}
		}

		return inputValue;
	}

	@Override
	public int getViewID() {
		return App.VIEW_TEXT_PREVIEW;
	}

}
