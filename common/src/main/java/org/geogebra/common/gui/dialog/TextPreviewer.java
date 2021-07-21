package org.geogebra.common.gui.dialog;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.TokenMgrError;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

/**
 * 
 * Abstract class for displaying a preview of a GeoText while editing. The class
 * requires a GUI panel that encloses an instance of EuclidianView. The preview
 * is drawn as a GeoText element in this EuclidianView.
 * 
 * The class maintains two hidden geos (previewGeoIndependent and
 * previewGeoDependent) that are used to preview the two possible types of
 * GeoText, independent and dependent.
 * 
 * 
 * @author G. Sturr
 * 
 */
public abstract class TextPreviewer {

	protected EuclidianView ev;
	protected Kernel kernel;
	private App app;

	private GeoText previewGeoIndependent;
	private GeoText previewGeoDependent;

	private AlgoDependentText textAlgo;
	private final Construction cons;
	private boolean isIndependent;

	/**
	 * @param kernel
	 *            Kernel
	 */
	public TextPreviewer(Kernel kernel) {

		this.kernel = kernel;
		this.cons = kernel.getConstruction();
		this.ev = getEuclidianView();
		this.setApp(kernel.getApplication());

		// set EV display properties
		removeEVMouseListeners();
		ev.setAllowShowMouseCoords(false);
		ev.setAxesCornerCoordsVisible(false);
		ev.updateFonts();
		ev.updateSize();

	}

	protected abstract EuclidianView getEuclidianView();

	protected abstract void removeEVMouseListeners();

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
	 *            preview text
	 */
	protected abstract void updateViewportSize(GeoText previewGeo);

	public void updateFonts() {
		ev.updateFonts();
	}

	/**
	 * Removes the preview geos
	 */
	public void removePreviewGeoText() {
		if (previewGeoIndependent != null) {
			ev.remove(previewGeoIndependent);
			previewGeoIndependent.remove();
			previewGeoIndependent = null;
		}
		if (previewGeoDependent != null) {
			ev.remove(previewGeoDependent);
			previewGeoDependent.remove();
			previewGeoDependent = null;
			textAlgo.remove();
		}
		ev.repaint();
	}

	/**
	 * Updates the preview geos and creates new geos if needed. Changes are
	 * determined by the inputValue string and the visual style of the
	 * targetGeo.
	 * 
	 * @param targetGeo
	 *            geo being edited
	 * @param inputValue
	 *            input text
	 * @param isLaTeXset
	 *            whether user set it to LaTeX
	 * @param mayDetectLaTeX
	 *            whether we may change the LaTeX property
	 * @return whether this is latex
	 */
	public boolean updatePreviewText(GeoText targetGeo, String inputValue,
			boolean isLaTeXset, boolean mayDetectLaTeX) {
		boolean isLaTeX = isLaTeXset;
		if (mayDetectLaTeX && !isLaTeXset) {
			isLaTeX = isLaTeX || guessLaTeX(inputValue);
		}
		// Application.printStacktrace("inputValue: " + inputValue);
		// initialize variables
		ValidExpression exp = null;
		StringTemplate tpl = targetGeo == null ? StringTemplate.defaultTemplate
				: targetGeo.getStringTemplate();
		ExpressionValue eval = null;
		boolean hasParseError = false;
		boolean showErrorMessage = false;
		isIndependent = false;

		// create previewGeoIndependent
		if (previewGeoIndependent == null) {
			previewGeoIndependent = new GeoText(kernel.getConstruction());
			previewGeoIndependent.setFontSizeMultiplier(1.0);
			previewGeoIndependent.addView(ev.getViewID());
			ev.add(previewGeoIndependent);
		}

		// prepare the input string for processing
		// String formattedInput = formatInputValue(inputValue);

		// parse the input text
		try {
			exp = kernel.getParser().parseGeoGebraExpression(inputValue);
		} catch (ParseException e) {
			isIndependent = true;
			hasParseError = true;
			if (inputValue.length() > 0) {
				showErrorMessage = true;
			}
		} catch (MyError e) {
			isIndependent = true;
			hasParseError = true; // odd numbers of quotes give parse errors
			showErrorMessage = true;

		} catch (TokenMgrError e) {
			isIndependent = true;
			hasParseError = true; // odd numbers of quotes give parse errors
			showErrorMessage = true;
		}

		// resolve variables and evaluate the expression
		if (!(hasParseError)) {
			try {
				exp.resolveVariables(new EvalInfo(false));
				isIndependent = exp.isConstant();
				eval = exp.evaluate(tpl);
			} catch (Error e) {
				isIndependent = true;
				showErrorMessage = true;
				// Log.debug("resolve error:" + e.getCause());
			} catch (Exception e) {
				showErrorMessage = true;
				isIndependent = true;
				// Log.debug("resolve exception");
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
				text = ev.getApplication().getLocalization()
						.getInvalidInputError();
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
				ev.remove(previewGeoDependent);
				previewGeoDependent.remove();
				textAlgo.remove();
			}

			// if eg FormulaText["\sqrt{x}"] is entered, it should be displayed
			// in the preview as LaTeX
			// NB FormulaText[a] is displayed as-is
			// FormulaText[a]+"" needs to have LaTeX box manually checked
			if (exp.evaluate(tpl).isGeoElement()
					&& ((GeoText) (exp.evaluate(tpl))).isLaTeXTextCommand()) {
				isLaTeX = true;
			}

			// eg just an x in the "empty box"
			// (otherwise leads to NPE so
			// cons.removeFromConstructionList(textAlgo); doesn't get called
			if (((ExpressionNode) exp).getGeoElementVariables(
					SymbolicMode.NONE) == null) {
				// can't make an AlgoDependentText
				return isLaTeX;
			}

			// create new previewGeoDependent
			textAlgo = new AlgoDependentText(cons, (ExpressionNode) exp, false);
			cons.removeFromConstructionList(textAlgo);
			previewGeoDependent = textAlgo.getGeoText();
			previewGeoDependent.addView(ev.getViewID());
			ev.add(previewGeoDependent);

			// set the display style
			updateVisualProperties(previewGeoDependent, targetGeo, isLaTeX,
					showErrorMessage);
			// needed to reflect change of significant digits
			textAlgo.update();
		}

		// hide/show the preview geos
		previewGeoIndependent.setEuclidianVisible(isIndependent);
		previewGeoIndependent.updateRepaint();
		ev.update(previewGeoIndependent);
		if (previewGeoDependent != null) {
			previewGeoDependent.setEuclidianVisible(!isIndependent);
			previewGeoDependent.updateRepaint();
			ev.update(previewGeoDependent);
		}

		// update the panel size to match the geo
		if (previewGeoIndependent.isEuclidianVisible()) {
			updateViewportSize(previewGeoIndependent);
		}
		if ((previewGeoDependent != null)
				&& previewGeoDependent.isEuclidianVisible()) {
			updateViewportSize(previewGeoDependent);
		}

		ev.repaintView();
		return isLaTeX;
	}

	private static boolean guessLaTeX(String textString) {
		return textString != null
				&& (textString.contains("\\") || textString.contains("^"));
	}

	/**
	 * Sets the visual properties of a preview geo
	 */
	private void updateVisualProperties(GeoText geo, GeoText targetGeo,
			boolean isLaTeX, boolean isErrorMessage) {

		// set error message style
		if (isErrorMessage) {
			geo.setVisualStyle(cons.getConstructionDefaults()
					.getDefaultGeo(ConstructionDefaults.DEFAULT_TEXT));
			geo.setObjColor(GColor.RED);
			geo.setBackgroundColor(GColor.WHITE);
			geo.setFontStyle(GFont.ITALIC);
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
				geo.setObjColor(GColor.BLACK);
			}
			geo.setLaTeX(isLaTeX, true);
		}

		// set geo position in upper left corner (it might need changing after
		// isLaTeX change)
		locateTextGeo(geo);

		// Log.debug("preview text geo loc:" + geo.getAbsoluteScreenLocX() +
		// " , "
		// + geo.getAbsoluteScreenLocY());

	}

	/**
	 * Positions the preview geo in the upper left corner of the panel two
	 * settings are needed to account for differences in the way LaTeX and
	 * standard text is drawn
	 */
	private static void locateTextGeo(GeoText geo) {
		int xInset = 4;
		int yInset = (int) (geo.isLaTeX() ? 4
				: 18 + 12 * (geo.getFontSizeMultiplier() - 1));

		geo.setAbsoluteScreenLocActive(true);
		geo.setAbsoluteScreenLoc(xInset, yInset);
	}

	protected App getApp() {
		return app;
	}

	protected void setApp(App app) {
		this.app = app;
	}

}
