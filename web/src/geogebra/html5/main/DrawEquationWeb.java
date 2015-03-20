package geogebra.html5.main;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.DrawEquation;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;
import geogebra.common.util.Language;
import geogebra.common.util.Unicode;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;

public class DrawEquationWeb extends DrawEquation {

	static boolean scriptloaded = false;

	// private HashMap<String, SpanElement> equations = new HashMap<String,
	// SpanElement>();
	// private HashMap<String, Integer> equationAges = new HashMap<String,
	// Integer>();

	private DrawElementManager elementManager;

	public DrawEquationWeb() {
		elementManager = new DrawElementManager();
	}

	protected native void cvmBoxInit(String moduleBaseURL) /*-{
		$wnd.cvm.box.init(moduleBaseURL);
	}-*/;

	public void setUseJavaFontsForLaTeX(App app, boolean b) {
		// not relevant for web
	}

	public static String inputLatexCosmetics(String eqstringin) {

		String eqstring = eqstringin;

		eqstring = eqstring.replace('\n', ' ');

		eqstring = eqstring.replace("\\%", "%");

		if (eqstring.indexOf("{\\it ") > -1) {

			// replace {\it A} by A
			// (not \italic{A} )

			RegExp italic = RegExp.compile("(.*)\\{\\\\it (.*?)\\}(.*)");

			MatchResult matcher;

			while ((matcher = italic.exec(eqstring)) != null) {

				eqstring = matcher.getGroup(1) + " " + matcher.getGroup(2)
				        + matcher.getGroup(3);
			}
		}

		// make sure eg FractionText[] works (surrounds with {} which doesn't
		// draw well in MathQuillGGB)
		if (eqstring.length() >= 2)
			if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

		// remove $s
		eqstring = eqstring.trim();
		if (eqstring.length() > 2) {
			while (eqstring.startsWith("$"))
				eqstring = eqstring.substring(1).trim();
			while (eqstring.endsWith("$"))
				eqstring = eqstring.substring(0, eqstring.length() - 1).trim();
		} else if ("$$".equals(eqstring)) {
			eqstring = "";
			// the rest cases: do not remove single $
		} else if ("\\$".equals(eqstring)) {
			eqstring = "\\text{$}";
		}

		eqstring = eqstring.replace("\\\\", "\\cr ");

		// remove all \; and \,
		// doesn't work inside \text eg \text{some\;text}
		eqstring = eqstring.replace("\\;", "\\space ");
		eqstring = eqstring.replace("\\:", "\\space ");
		eqstring = eqstring.replace("\\,", "\\space ");
		eqstring = eqstring.replace("\\ ", "\\space ");

		// negative space is not implemented, let it be positive space
		// the following code might avoid e.g. x\\!1
		eqstring = eqstring.replace("\\! ", " ");
		eqstring = eqstring.replace(" \\!", " ");
		eqstring = eqstring.replace("\\!", " ");

		// substitute every \$ with $
		eqstring = eqstring.replace("\\$", "$");

		// eqstring = eqstring.replace("\\left\\{", "\\lbrace ");
		// eqstring = eqstring.replace("\\right\\}", "\\rbrace ");

		// this might remove necessary space
		// eqstring = eqstring.replace(" ", "");

		// this does not work
		// eqstring = eqstring.replace("\\sqrt[ \\t]+\\[", "\\sqrt[");

		// that's why this programmatically slower solution:
		while ((eqstring.indexOf("\\sqrt ") != -1)
		        || (eqstring.indexOf("\\sqrt\t") != -1)) {
			eqstring = eqstring.replace("\\sqrt ", "\\sqrt");
			eqstring = eqstring.replace("\\sqrt\t", "\\sqrt");
		}

		// exchange \\sqrt[x]{y} with \\nthroot{x}{y}
		int index1 = 0, index2 = 0;
		while ((index1 = eqstring.indexOf("\\sqrt[")) != -1) {
			index2 = eqstring.indexOf("]", index1);
			eqstring = eqstring.substring(0, index1) + "\\nthroot{"
			        + eqstring.substring(index1 + 6, index2) + "}"
			        + eqstring.substring(index2 + 1);
		}

		// avoid grey rectangle
		if (eqstring.trim().equals("")) {
			eqstring = "\\text{}";
		}

		// and now, only for presentational purposes (blue highlighting)
		// we can make every ( to \left( and every ) to \right), etc.
		// eqstring = eqstring.replace("(", "\\left(");
		// eqstring = eqstring.replace("\\left\\left(", "\\left(");
		// in case of typo
		// eqstring = eqstring.replace("\\right\\left(", "\\right(");
		// eqstring = eqstring.replace(")", "\\right)");
		// eqstring = eqstring.replace("\\right\\right)", "\\right)");
		// in case of typo
		// eqstring = eqstring.replace("\\left\\right)", "\\left)");
		// but we do not do it as editing x in f(x)=x+1 gives error anyway
		// so not having \\left there seems to be a feature, not a bug
		// otherwise, Line[A,B] and {1,2,3,4} are working, so probably Okay

		return eqstring;
	}

	/**
	 * This should make all the LaTeXes temporarily disappear
	 * 
	 * @param ev
	 *            latexes of only this EuclidianView - TODO: implement
	 */
	public void clearLaTeXes(EuclidianViewW ev) {

		elementManager.clearLaTeXes(ev);

		/*
		 * Iterator<String> eei = equations.keySet().iterator();
		 * ArrayList<String> dead = new ArrayList<String>(); while
		 * (eei.hasNext()) { String eqID = eei.next();
		 * 
		 * if (eqID.length() < 1) continue; else if (!eqID.substring(0,
		 * 1).equals("0") && !eqID.substring(0,
		 * 1).equals(""+ev.getEuclidianViewNo())) continue;
		 * 
		 * Integer age = equationAges.get(eqID); if (age == null) age = 0; if
		 * (age > 5) {// clearLaTeXes can be called this much until redraw
		 * Element toclear = equations.get(eqID); Element tcparent =
		 * toclear.getParentElement(); tcparent.removeChild(toclear);
		 * dead.add(eqID);// avoid concurrent modification exception } else {
		 * equationAges.put(eqID, ++age);
		 * equations.get(eqID).getStyle().setDisplay(Style.Display.NONE); } }
		 * for (int i = dead.size() - 1; i >= 0; i--) {
		 * equations.remove(dead.get(i)); equationAges.remove(dead.get(i)); }
		 */
	}

	/**
	 * Does not only clear the latexes, but also deletes them (on special
	 * occasions)
	 * 
	 * @param ev
	 *            latexes of only this EuclidianView - TODO: implement
	 */
	public void deleteLaTeXes(EuclidianViewW ev) {

		elementManager.deleteLaTeXes(ev);

		/*
		 * Iterator<SpanElement> eei = equations.values().iterator(); while
		 * (eei.hasNext()) { Element toclear = eei.next(); Element tcparent =
		 * toclear.getParentElement(); tcparent.removeChild(toclear); }
		 * equations.clear(); equationAges.clear();
		 */
	}

	/**
	 * Draws an equation on the algebra view in display mode (not editing).
	 * Color is supposed to be handled in outer span element.
	 * 
	 * @param parentElement
	 *            adds the equation as the child of this element
	 * @param latexString
	 *            the equation in LaTeX
	 */
	public static void drawEquationAlgebraView(Element parentElement,
	        String latexString, boolean nonGeneral) {
		// no scriptloaded check yet (is it necessary?)

		// logging takes too much time
		// App.debug("Algebra View: "+eqstring);

		DivElement ih = DOM.createDiv().cast();
		ih.getStyle().setPosition(Style.Position.RELATIVE);
		ih.setDir("ltr");

		int el = latexString.length();
		String eqstring = stripEqnArray(latexString);
		drawEquationMathQuillGGB(ih, eqstring, 0, 0, parentElement, true,
		        el == eqstring.length(), true, 0, nonGeneral);
	}

	@Override
	public GDimension drawEquation(App app1, GeoElement geo, GGraphics2D g2,
	        int x, int y, String latexString0, GFont font, boolean serif,
	        GColor fgColor, GColor bgColor, boolean useCache,
	        boolean updateAgain) {

		String latexString = latexString0;

		if (latexString == null)
			return null;

		double rotateDegree = 0;

		if (latexString.startsWith("\\rotatebox{")) {
			// getting rotation degree...

			// chop "\\rotatebox{"
			latexString = latexString.substring(11);

			// get value
			int index = latexString.indexOf("}{ ");
			rotateDegree = Double.parseDouble(latexString.substring(0, index));

			// chop "}{"
			latexString = latexString.substring(index + 3);

			// chop " }"
			latexString = latexString.substring(0, latexString.length() - 2);

			if (latexString.startsWith("\\text{ ")) {
				// chop "text", seems to prevent the sqrt sign showing
				latexString = latexString.substring(7);
				latexString = latexString
				        .substring(0, latexString.length() - 3);

				// put back "\\text{ " if it is not harmful
				if (latexString.indexOf("{") <= 0
				        && latexString.indexOf("\\") <= 0
				        && latexString.indexOf("}") <= 0) {
					// heuristics: no latex
					latexString = "\\text{ " + latexString + " } ";
				}
			}
		}

		// which?
		int fontSize = g2.getFont().getSize();
		int fontSize2 = font.getSize();

		boolean shouldPaintBackground = bgColor != null;

		View view = ((GGraphics2DW) g2).getView();
		if (view != null && view instanceof EuclidianView) {
			if (((EuclidianView) view).getBackgroundCommon() == bgColor) {
				shouldPaintBackground = false;
			}
		}

		/*
		 * if (bgColor == null) shouldPaintBackground = false; else if
		 * (!geo.isVisibleInView(App.VIEW_EUCLIDIAN) &&
		 * !geo.isVisibleInView(App.VIEW_EUCLIDIAN2)) shouldPaintBackground =
		 * false; else if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN2) &&
		 * (app1.getEuclidianView1().getBackgroundCommon() == bgColor))
		 * shouldPaintBackground = false; else if
		 * (!geo.isVisibleInView(App.VIEW_EUCLIDIAN) &&
		 * !app1.hasEuclidianView2EitherShowingOrNot()) shouldPaintBackground =
		 * false; else if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN) &&
		 * (app1.getEuclidianView2().getBackgroundCommon() == bgColor))
		 * shouldPaintBackground = false; else if
		 * ((app1.getEuclidianView1().getBackgroundCommon() == bgColor) &&
		 * !app1.hasEuclidianView2EitherShowingOrNot()) shouldPaintBackground =
		 * false; else if ((app1.getEuclidianView1().getBackgroundCommon() ==
		 * bgColor) && (app1.getEuclidianView2().getBackgroundCommon() ==
		 * bgColor)) shouldPaintBackground = false;
		 */

		if (geo.isGeoText() && ((GeoText) geo).isMathML()) {
			// assume that the script is loaded; it is part of resources
			// so we will probably get width and height OK, no need to update
			// again
			JsArrayInteger jai = drawEquationCanvasMath(
			        ((GGraphics2DW) g2).getCanvas().getContext2d(),
			        latexString,
			        x,
			        y,
			        (fgColor == null) ? null : GColor.getColorString(fgColor),
			        !shouldPaintBackground ? null : GColor
			                .getColorString(bgColor));
			return new geogebra.html5.awt.GDimensionW(jai.get(0), jai.get(1));
		}

		// the new way to draw an Equation (latex)
		// no scriptloaded check yet (is it necessary?)

		String eqstring = inputLatexCosmetics(latexString);

		if (geo instanceof TextProperties) {
			if ((((TextProperties) geo).getFontStyle() & GFont.ITALIC) == 0) {
				// set to be not italic
				eqstring = "\\mathrm{" + eqstring + "}";
			} // else {
			  // italics needed? Try this! (Testing needed...)
			  // eqstring = "\\mathit{"+ eqstring +"}";
			  // }
			  // if ((((TextProperties)geo).getFontStyle() & GFont.BOLD) != 0) {
			  // bold needed? Try this! (Testing needed...)
			  // eqstring = "\\mathbf{"+ eqstring +"}";
			  // }
			if (!((TextProperties) geo).isSerifFont()) {
				// forcing sans-serif
				eqstring = "\\mathsf{" + eqstring + "}";
			}
		}

		/*
		 * // whether we are painting on EV1 now boolean visible1 =
		 * (((GGraphics2DW)g2).getCanvas() == ((AppWeb)app1).getCanvas());
		 * 
		 * // whether we are painting on EV2 now boolean visible2 = false; if
		 * (((AppWeb)app1).hasEuclidianView2()) { if
		 * (((GGraphics2DW)g2).getCanvas() ==
		 * ((GGraphics2DW)app1.getEuclidianView2
		 * ().getGraphicsForPen()).getCanvas()) { visible2 = true; } }
		 */

		/*************************************************************************
		 * If g2 is not painting in EV1 or EV2, then assume g2 is being used for
		 * temporary drawing. In this case, the canvas associated with g2 does
		 * not have a parent element so we cannot add HTML elements to it. To
		 * handle this problem elements are instead drawn invisibly into either
		 * EV1 or EV2.
		 *************************************************************************/

		/*
		 * GGraphics2DW g2visible = (GGraphics2DW)g2; if (!visible1 &&
		 * !visible2) { if
		 * (((AppWeb)app1).hasEuclidianView2EitherShowingOrNot()) { if
		 * (app1.getEuclidianView2().getTempGraphics2D(font) == g2) { g2visible
		 * =
		 * (GGraphics2DW)((AppWeb)app1).getEuclidianView2().getGraphicsForPen();
		 * } else if (app1.getEuclidianView1().getTempGraphics2D(font) == g2) {
		 * g2visible =
		 * (GGraphics2DW)((EuclidianView)((AppWeb)app1).getEuclidianView1
		 * ()).getGraphicsForPen(); } } else { g2visible =
		 * (GGraphics2DW)((EuclidianView
		 * )((AppWeb)app1).getEuclidianView1()).getGraphicsForPen(); } }
		 */

		boolean hasActualParent = ((GGraphics2DW) g2).getCanvas().isAttached();

		// Set relative font size
		int fontSizeR = 16;
		if (fontSize <= 10)
			fontSizeR = 10;

		// Determine id string
		String eqstringid = eqstring;
		if (rotateDegree != 0) {
			// adding rotateDegree again, just for the id
			eqstringid = "\\rotatebox{" + rotateDegree + "}{ " + eqstring
			        + " }";
		}
		eqstringid = "\\scaling{" + eqstringid + "}{ " + fontSize + "}";
		eqstringid = eqstringid + "@" + geo.getID();

		// Try to get an existing element that uses this id string.
		// If no matching element exists a new element is created.
		SpanElement ih = (SpanElement) elementManager.getElement(
		        (GGraphics2DW) g2, eqstringid);

		if (ih == null) {

			// create a new latex element
			ih = DOM.createSpan().cast();
			ih.getStyle().setPosition(Style.Position.ABSOLUTE);
			ih.setDir("ltr");
			int el = eqstring.length();
			eqstring = stripEqnArray(eqstring);

			// register the element with initial age = 0
			elementManager
			        .registerElement((GGraphics2DW) g2, ih, eqstringid, 0);

			// draw it
			Element parentElement = elementManager
			        .getParentElement((GGraphics2DW) g2);
			drawEquationMathQuillGGB(ih, eqstring, fontSize, fontSizeR,
			        parentElement, true, el == eqstring.length(), true,
			        rotateDegree, true);

			if (updateAgain)
				// set a flag that the kernel needs a new update
				app1.getKernel().setUpdateAgain(true, geo);

		} else {

			// reset the element's age counter
			elementManager.setElementAge((GGraphics2DW) g2, eqstringid, 0);
		}

		if (hasActualParent) {

			// set position
			ih.getStyle().setLeft(x, Style.Unit.PX);
			ih.getStyle().setTop(y, Style.Unit.PX);

			// as the background is usually (or always) the background of the
			// canvas, it is better if this is transparent, because the grid
			// should be shown just like in the Java version
			if (shouldPaintBackground) {
				// note: there was a bug when painting scaled formulas,
				// so the background color should be set to ih's last child
				if (ih.getLastChild() != null) {
					Element ihc = ih.getLastChild().cast();
					ihc.getStyle().setBackgroundColor(
					        GColor.getColorString(bgColor));
				} else {
					ih.getStyle().setBackgroundColor(
					        GColor.getColorString(bgColor));
				}
			}

			if (fgColor != null)
				ih.getStyle().setColor(GColor.getColorString(fgColor));
		}

		ih.getStyle().setDisplay(Style.Display.INLINE);

		// get the dimensions
		GDimension ret = null;
		ret = new geogebra.html5.awt.GDimensionW(
		        (int) Math.ceil(getScaledWidth(ih, true)),
		        (int) Math.ceil(getScaledHeight(ih, true)));

		// adjust dimensions for rotation
		if (rotateDegree != 0) {
			GDimension ret0 = new geogebra.html5.awt.GDimensionW(
			        (int) Math.ceil(getScaledWidth(ih, false)),
			        (int) Math.ceil(getScaledHeight(ih, false)));

			GDimension corr = computeCorrection(ret, ret0, rotateDegree);

			if (hasActualParent) {
				// if it's not visible, leave at its previous place to prevent
				// lag
				ih.getStyle().setLeft(x - corr.getWidth(), Style.Unit.PX);
				ih.getStyle().setTop(y - corr.getHeight(), Style.Unit.PX);
			}
		}

		return ret;
	}

	public static native double getScaledWidth(Element el, boolean inside) /*-{
		var ell = el;
		if (el.lastChild) {//elsecond
			ell = el.lastChild;
			if (ell.lastChild && inside) {//elsecondInside 
				ell = ell.lastChild;
			}
		}
		if (ell.getBoundingClientRect) {
			var cr = ell.getBoundingClientRect();
			if (cr.width) {
				return cr.width;
			} else if (cr.right) {
				return cr.right - cr.left;
			}
		}
		return el.offsetWidth || 0;
	}-*/;

	public static native double getScaledHeight(Element el, boolean inside) /*-{
		var ell = el;
		if (el.lastChild) {//elsecond
			ell = el.lastChild;
			if (ell.lastChild && inside) {//elsecondInside 
				ell = ell.lastChild;
			}
		}
		if (ell.getBoundingClientRect) {
			var cr = ell.getBoundingClientRect();
			if (cr.height) {
				return cr.height;
			} else if (cr.bottom) {
				return cr.bottom - cr.top;
			}
		}
		return el.offsetHeight || 0;
	}-*/;

	/**
	 * The JavaScript/$ggbQuery bit of drawing an equation with MathQuillGGB
	 * More could go into GWT, but it was easier with JSNI
	 * 
	 * @param el
	 *            the element which should be drawn
	 * @param htmlt
	 *            the equation
	 * @param parentElement
	 *            parent of el
	 * @param addOverlay
	 *            true to add an overlay div
	 * @param noEqnArray
	 *            true = normal LaTeX, flase = LaTeX with \begin{eqnarray} in
	 *            the beginning
	 */
	public static native void drawEquationMathQuillGGB(Element el,
	        String htmlt, int fontSize, int fontSizeRel, Element parentElement,
	        boolean addOverlay, boolean noEqnArray, boolean visible,
	        double rotateDegree, boolean nonav) /*-{

		el.style.cursor = "default";
		if (typeof el.style.MozUserSelect != "undefined") {
			el.style.MozUserSelect = "-moz-none";
		} else if (typeof el.style.webkitUserSelect != "undefined") {
			el.style.webkitUserSelect = "none";
		} else if (typeof el.style.khtmlUserSelect != "undefined") {
			el.style.khtmlUserSelect = "none";
		} else if (typeof el.style.oUserSelect != "undefined") {
			el.style.oUserSelect = "none";
		} else if (typeof el.style.userSelect != "undefined") {
			el.style.userSelect = "none";
		} else if (typeof el.onselectstart != "undefined") {
			el.onselectstart = function(event) {
				return false;
			}
			if (nonav) {
				el.ondragstart = function(event) {
					return false;
				}
			}
		}
		if (nonav) {
			el.onmousedown = function(event) {
				if (event.preventDefault)
					event.preventDefault();
				return false;
			}
		}
		if (addOverlay) {
			var elfirst = $doc.createElement("div");
			el.appendChild(elfirst);
		}

		var elsecond = $doc.createElement("div");

		if (addOverlay) {
			var elthirdBefore = $doc.createElement("span");
			elthirdBefore.style.position = "absolute";
			elthirdBefore.style.zIndex = 2;
			elthirdBefore.style.top = "0px";
			elthirdBefore.style.bottom = "0px";
			elthirdBefore.style.left = "0px";
			elthirdBefore.style.right = "0px";
			elsecond.appendChild(elthirdBefore);
		}

		var elsecondInside = $doc.createElement("span");
		elsecondInside.innerHTML = htmlt;

		if (fontSizeRel != 0) {
			elsecond.style.fontSize = fontSizeRel + "px";
		}
		elsecond.appendChild(elsecondInside);
		el.appendChild(elsecond);

		if (!visible) {
			el.style.visibility = "hidden";
		}

		parentElement.appendChild(el);

		if (noEqnArray) {
			$wnd.$ggbQuery(elsecondInside).mathquillggb();

			// Make sure the length of brackets and square roots are OK
			$wnd.setTimeout(function() {
				$wnd.$ggbQuery(elsecondInside).mathquillggb('latex', htmlt);
			}, 500);

			// it's not ok for IE8, but it's good for ie9 and above
			//$doc.addEventListener('readystatechange', function() {
			//	if ($doc.readyState === 'complete' ||
			//		$doc.readyState === 'loaded') {
			//		$wnd.$ggbQuery(elsecond).mathquillggb('latex', htmlt);
			//	}
			//}, false);
		} else {
			$wnd.$ggbQuery(elsecondInside).mathquillggb('eqnarray');

			// Make sure the length of brackets and square roots are OK
			//			$wnd.setTimeout(function() {
			//				// TODO: this needs more testing,
			//				// also for the editing of it
			//				//$wnd.$ggbQuery(elsecond).mathquillggb('latex', htmlt);
			//				$wnd.$ggbQuery(elsecond).mathquillggb('eqnarray');
			//			});
		}

		if ((fontSize != 0) && (fontSizeRel != 0) && (fontSize != fontSizeRel)) {
			// floating point division in JavaScript!
			var sfactor = "scale(" + (fontSize / fontSizeRel) + ")";

			elsecond.style.transform = sfactor;
			elsecond.style.MozTransform = sfactor;
			elsecond.style.MsTransform = sfactor;
			elsecond.style.OTransform = sfactor;
			elsecond.style.WebkitTransform = sfactor;

			elsecond.style.transformOrigin = "0px 0px";
			elsecond.style.MozTransformOrigin = "0px 0px";
			elsecond.style.MsTransformOrigin = "0px 0px";
			elsecond.style.OTransformOrigin = "0px 0px";
			elsecond.style.WebkitTransformOrigin = "0px 0px";
		}

		if (rotateDegree != 0) {
			var rfactor = "rotate(-" + rotateDegree + "deg)";

			elsecondInside.style.transform = rfactor;
			elsecondInside.style.MozTransform = rfactor;
			elsecondInside.style.MsTransform = rfactor;
			elsecondInside.style.OTransform = rfactor;
			elsecondInside.style.WebkitTransform = rfactor;

			elsecondInside.style.transformOrigin = "center center";
			elsecondInside.style.MozTransformOrigin = "center center";
			elsecondInside.style.MsTransformOrigin = "center center";
			elsecondInside.style.OTransformOrigin = "center center";
			elsecondInside.style.WebkitTransformOrigin = "center center";

			if (addOverlay) {
				elthirdBefore.style.transform = rfactor;
				elthirdBefore.style.MozTransform = rfactor;
				elthirdBefore.style.MsTransform = rfactor;
				elthirdBefore.style.OTransform = rfactor;
				elthirdBefore.style.WebkitTransform = rfactor;

				elthirdBefore.style.transformOrigin = "center center";
				elthirdBefore.style.MozTransformOrigin = "center center";
				elthirdBefore.style.MsTransformOrigin = "center center";
				elthirdBefore.style.OTransformOrigin = "center center";
				elthirdBefore.style.WebkitTransformOrigin = "center center";
			}
		}

	}-*/;

	/**
	 * Edits a MathQuillGGB equation which was created by
	 * drawEquationMathQuillGGB
	 * 
	 * @param rbti
	 *            the tree item for callback
	 * @param parentElement
	 *            the same element as in drawEquationMathQuillGGB
	 */
	public static native void editEquationMathQuillGGB(
	        RadioButtonTreeItem rbti, Element parentElement,
	        boolean newCreationMode) /*-{

		var elfirst = parentElement.firstChild.firstChild;

		elfirst.style.display = 'none';

		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var elsecondInside = elsecond.lastChild;

		$wnd.$ggbQuery(elsecondInside).mathquillggb('revert').mathquillggb(
				'editable').focus();

		$wnd
				.$ggbQuery(elsecondInside)
				.keyup(
						function(event) {
							// in theory, the textarea inside elsecondInside will be
							// selected, and it will capture other key events before
							// these execute

							// Note that for keys like Hungarian U+00F3 or &oacute;
							// both event.keyCode and event.which, as well as event.charCode
							// will be zero, that's why we should not leave the default
							// value of "var code" at 13, but it should be 0 instead
							// never mind, as this method only does something for 13 and 27
							var code = 0;
							// otherwise, MathQuill still listens to keypress which will
							// capture the &oacute;

							if (event.keyCode) {
								code = event.keyCode;
							} else if (event.which) {
								code = event.which;
							}
							if (code == 13) {//enter
								if (newCreationMode) {
									@geogebra.html5.main.DrawEquationWeb::newFormulaCreatedMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								} else {
									@geogebra.html5.main.DrawEquationWeb::endEditingEquationMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								}
							} else if (code == 27) {//esc
								if (newCreationMode) {
									@geogebra.html5.main.DrawEquationWeb::stornoFormulaMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								} else {
									@geogebra.html5.main.DrawEquationWeb::escEditingEquationMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								}
							} else {
								// it would be counterproductive to call autoScroll and history popup
								// after the editing/new formula creation ends! so put their code here

								// we should also auto-scroll the cursor in the formula!
								// but still better to put this in keypress later,
								// just it should be assigned in the bubbling phase of keypress
								// after MathQuillGGB has executed its own code, just it is not easy...
								@geogebra.html5.main.DrawEquationWeb::scrollCursorIntoView(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;Z)(rbti,parentElement,newCreationMode);

								if (newCreationMode) {
									var querr = elsecondInside;
									if (querr.GeoGebraSuggestionPopupCanShow !== undefined) {
										// when the suggestions should pop up, we make them pop up,
										// when not, there may be two possibilities: we should hide the old,
										// or we should not hide the old... e.g. up/down arrows should not hide...
										// is there any other case? (up/down will unset later here)
										if (querr.GeoGebraSuggestionPopupCanShow === true) {
											@geogebra.html5.main.DrawEquationWeb::popupSuggestions(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;)(rbti);
										} else {
											@geogebra.html5.main.DrawEquationWeb::hideSuggestions(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;)(rbti);
										}
									}
								}
							}

							event.stopPropagation();
							event.preventDefault();
							return false;
						})
				.keypress(function(event2) {
					// the main reason of calling stopPropagation here
					// is to prevent calling preventDefault later
					// code style is not by me, but automatic formatting
					event2.stopPropagation();
				})
				.keydown(function(event3) {
					// to prevent focus moving away
					event3.stopPropagation();
				})
				.mousedown(function(event4) {
					// otherwise RadioButtonTreeItem would call preventDefault
					event4.stopPropagation();
				})
				.click(function(event6) {
					event6.stopPropagation();
				})
				.select(
						function(event7) {
							@geogebra.html5.main.DrawEquationWeb::scrollSelectionIntoView(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;Z)(rbti,parentElement,newCreationMode);
						});
		// could be: mouseover, mouseout, mousemove, mouseup, but this seemed to be enough

		// hacking to deselect the editing when the user does something else like in Desktop
		if (!newCreationMode) {
			var mqggbmousein = {};
			mqggbmousein.mout = false;
			$wnd.mqggbmousein = mqggbmousein;
			$wnd.$ggbQuery(elsecondInside).mouseenter(function(event2) {
				$wnd.mqggbmousein.mout = false;
			}).mouseleave(function(event3) {
				$wnd.mqggbmousein.mout = true;

				// focus and blur propagate the event
				// to its textarea, so "focusin" is also called
				// on calling .focus(), which makes the following
				// part of code harmful: 
				//$wnd.$ggbQuery(this).focus();

				// but still, we can make use of the following:
				$wnd.$ggbQuery(this).find('textarea').focus();
				// which is the simplest workaround of the appearing
				// bug, as it makes it almost equivalent to the previous
				// state, together with the following:
			});
			$wnd
					.$ggbQuery(elsecondInside)
					.find('textarea')
					.focusout(
							function(event) {
								// note that this will be called every time
								// focus is called as well
								if ($wnd.mqggbmousein.mout) {
									@geogebra.html5.main.DrawEquationWeb::escEditingEquationMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								}
								event.stopPropagation();
								event.preventDefault();
								return false;
							});
		} else {
			// if newCreationMode is active, we should catch some Alt-key events!
			var keydownfun = function(event) {
				var code = 0;
				if (event.keyCode) {
					code = event.keyCode;
				} else if (event.which) {// not sure this would be right here
					code = event.which;
				}
				if (code == 38) {//up-arrow
					// in this case, .GeoGebraSuggestionPopupCanShow may be its old value,
					// so let's change it:
					delete elsecondInside.GeoGebraSuggestionPopupCanShow;

					@geogebra.html5.main.DrawEquationWeb::shuffleSuggestions(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Z)(rbti, false);
					event.stopPropagation();
					event.preventDefault();
					return false;
				} else if (code == 40) {//down-arrow
					// in this case, .GeoGebraSuggestionPopupCanShow may be its old value,
					// so let's change it:
					delete elsecondInside.GeoGebraSuggestionPopupCanShow;

					@geogebra.html5.main.DrawEquationWeb::shuffleSuggestions(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Z)(rbti, true);
					event.stopPropagation();
					event.preventDefault();
					return false;
				}
				var captureSuccess = @geogebra.html5.main.DrawEquationWeb::specKeyDown(IZZLcom/google/gwt/dom/client/Element;Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;)(code, event.altKey, event.shiftKey, parentElement, rbti);
				if (captureSuccess) {
					// in this case, .GeoGebraSuggestionPopupCanShow may be its old value,
					// so let's change it: (it should not be true for pi, o and i!)
					delete elsecondInside.GeoGebraSuggestionPopupCanShow;

					// to prevent MathQuillGGB adding other kind of Alt-shortcuts,
					// e.g. unlaut a besides our alpha, or more accurately,
					// call preventDefault because it is a default action here
					event.stopPropagation();
					event.preventDefault();
					return false;
				}
			}
			if (elsecondInside.addEventListener) {//IE9 OK
				// event capturing before the event handlers of MathQuillGGB
				elsecondInside.addEventListener("keydown", keydownfun, true);
			}

			// Also switching off the AV horizontal scrollbar when this has focus
			// multiple focus/blur handlers can be attached to the same event in JQuery
			// but for the blur() event this did not work, so moved this code to
			// mathquillggb.js, textarea.focus and blur handlers - "NoHorizontalScroll"
			// style in web-styles.css... but at least set newCreationMode here!
			elsecondInside.newCreationMode = true;

			// Will this work?
			var memoHack = function() {
				// focusin should remember scroll position, and
				// the focus handler should use this information
				// to scroll back!
				// .focusin() is not yet supported in Firefox!!!
				elsecondInside.memorizedScrollLeft = $wnd.$ggbQuery(
						elsecondInside).parents('.NewRadioButtonTreeItem')
						.scrollLeft();
			}
			$wnd.$ggbQuery(elsecondInside).find('textarea')[0].focusmemo = $wnd
					.$ggbQuery(elsecondInside).find('textarea')[0].focus;
			$wnd.$ggbQuery(elsecondInside).find('textarea')[0].focus = function() {
				memoHack();
				this.focusmemo();
			}

			$wnd
					.$ggbQuery(elsecondInside)
					.find('textarea')
					.focus(
							function() {
								setTimeout(
										function() {
											if (elsecondInside.memorizedScrollLeft !== undefined) {
												$wnd
														.$ggbQuery(
																elsecondInside)
														.parents(
																'.NewRadioButtonTreeItem')
														.scrollLeft(
																elsecondInside.memorizedScrollLeft);
											}
											// old school: scrolling cursor into view
											//	@geogebra.html5.main.DrawEquationWeb::scrollCursorIntoView(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
										}, 50);
							});
		}
	}-*/;

	public static boolean specKeyDown(int keyCode, boolean altDown,
	        boolean shiftDown, Element parentElement, RadioButtonTreeItem rbti) {

		boolean Hungarian = false;
		String locale = rbti.getApplication().getLocalization().getLocaleStr();
		String hunloc = Language.Hungarian.localeGWT;
		if (locale != null && locale.equals(hunloc)) {
			Hungarian = true;
		}

		if (altDown) {

			// char c = (char) keyCode;

			String s = "";

			if (keyCode == 79) {// (c == 'o' || c == 'O')
				s += Unicode.degree;
			} else if (keyCode == 80) {// (c == 'p' || c == 'P')
				if (shiftDown) {
					s += Unicode.Pi;
				} else {
					s += Unicode.pi;
				}
			} else if (keyCode == 73) {// (c == 'i' || c == 'I')
				s += Unicode.IMAGINARY;
			} else if (keyCode == 65) {
				// A, OK in Hungarian, although invisibly ä
				if (shiftDown) {
					s += Unicode.Alpha;
				} else {
					s += Unicode.alpha;
				}
			} else if (keyCode == 66 && !Hungarian) {
				// B, not OK in Hungarian
				if (shiftDown) {
					s += Unicode.Beta;
				} else {
					s += Unicode.beta;
				}
			} else if (keyCode == 71 && !Hungarian) {
				// G, not OK in Hungarian
				if (shiftDown) {
					s += Unicode.Gamma;
				} else {
					s += Unicode.gamma;
				}
			} else if (keyCode == 85 && !Hungarian) {
				// U, euro sign is shown on HU
				s += Unicode.Infinity;
			} else if (keyCode == 76 && !Hungarian) {
				// L, Ł sign is shown on HU
				if (shiftDown) {
					s += Unicode.Lambda;
				} else {
					s += Unicode.lambda;
				}
			} else if (keyCode == 77 && !Hungarian) {
				// M: Although Alt-Í the same as Alt-M,
				// not sure Í is present on all kinds of Hungarian keyboard
				if (shiftDown) {
					s += Unicode.Mu;
				} else {
					s += Unicode.mu;
				}
			} else if (keyCode == 87 && !Hungarian) {
				// Alt-W is | needed for abs()
				if (shiftDown) {
					s += Unicode.Omega;
				} else {
					s += Unicode.omega;
				}
			} else if (keyCode == 82) {// OK in Hungarian
				s += Unicode.SQUARE_ROOT;
			} else if (keyCode == 50) {// 2
				s += "^2";
			} else if (keyCode == 51 && !Hungarian) {
				// in the Hungarian case Alt-3 triggers one "^"
				s += "^3";
			} else {
				return false;
			}

			if (s != null && !"".equals(s)) {
				for (int i = 0; i < s.length(); i++) {
					triggerPaste(parentElement, "" + s.charAt(i));
				}
				// triggerPaste(parentElement, s);
				// writeLatexInPlaceOfCurrentWord(parentElement, s, "", false);
				return true;
			}
		}
		return false;
	}

	/**
	 * Simulates a paste event, or anything that happens on pasting/entering
	 * text most naturally used with single characters, but string may be okay
	 * as well, provided that they are interpreted as pasting and not
	 * necessarily latex
	 */
	public static native void triggerPaste(Element parentElement, String str) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		if (elsecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elsecondInside.GeoGebraSuggestionPopupCanShow;
		}

		$wnd.$ggbQuery(elsecondInside).mathquillggb('simpaste', str);
	}-*/;

	// documentation in RadioButtonTreeItem.keydown
	public static native void triggerKeydown(Element parentElement,
	        int keycode, boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		if (elsecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elsecondInside.GeoGebraSuggestionPopupCanShow;
		}

		var textarea = $wnd.$ggbQuery(elsecondInside).find('textarea');
		if ((textarea !== undefined) && (textarea[0] !== undefined)) {
			var evt = $wnd.$ggbQuery.Event("keydown", {
				keyCode : keycode,
				which : keycode,
				altKey : altk,
				ctrlKey : ctrlk,
				shiftKey : shiftk
			});
			textarea.trigger(evt);
		}
	}-*/;

	// documentation in RadioButtonTreeItem.keypress
	public static native void triggerKeypress(Element parentElement,
	        int charcode, boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		if (elsecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elsecondInside.GeoGebraSuggestionPopupCanShow;
		}

		var textarea = $wnd.$ggbQuery(elsecondInside).find('textarea');
		if ((textarea !== undefined) && (textarea[0] !== undefined)) {
			textarea.val(String.fromCharCode(charcode));
			var evt = $wnd.$ggbQuery.Event("keypress", {
				charCode : charcode,
				which : charcode,
				// maybe the following things are not necessary
				altKey : altk,
				ctrlKey : ctrlk,
				shiftKey : shiftk
			});
			textarea.trigger(evt);
		}
	}-*/;

	public static native void triggerKeyUp(Element parentElement, int keycode,
	        boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		if (elsecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elsecondInside.GeoGebraSuggestionPopupCanShow;
		}

		var textarea = $wnd.$ggbQuery(elsecondInside).find('textarea');
		if ((textarea !== undefined) && (textarea[0] !== undefined)) {
			var evt = $wnd.$ggbQuery.Event("keyup", {
				keyCode : keycode,
				which : keycode,
				altKey : altk,
				ctrlKey : ctrlk,
				shiftKey : shiftk
			});
			textarea.trigger(evt);
		}
	}-*/;

	public static void popupSuggestions(RadioButtonTreeItem rbti) {
		rbti.popupSuggestions();
	}

	public static void hideSuggestions(RadioButtonTreeItem rbti) {
		rbti.hideSuggestions();
	}

	public static void shuffleSuggestions(RadioButtonTreeItem rbti, boolean down) {
		rbti.shuffleSuggestions(down);
	}

	public static native void focusEquationMathQuillGGB(Element parentElement,
	        boolean focus) /*-{
		//var elsecond = parentElement.firstChild.firstChild.nextSibling;
		//var elsecondInside = elsecond.lastChild;
		//$wnd.$ggbQuery(elsecondInside).focus();
		if (focus) {
			$wnd.$ggbQuery(parentElement).find(".mathquillggb-editable")
					.focus();
		} else {
			$wnd.$ggbQuery(parentElement).find(".mathquillggb-editable").blur();
		}
	}-*/;

	public static native void newFormulaCreatedMathQuillGGB(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elsecondInside);
		var latexq = thisjq.mathquillggb('text');
		var latexx = thisjq.mathquillggb('latex');

		//elsecond.previousSibling.style.display = "block"; // this does not apply here!!

		@geogebra.html5.main.DrawEquationWeb::newFormulaCreatedMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;Ljava/lang/String;Ljava/lang/String;)(rbti,parentElement,latexq,latexx);

		// this method also takes care of calling more JSNI code in a callback,
		// that originally belonged here: newFormulaCreatedMathQuillGGBCallback
	}-*/;

	public static native void stornoFormulaMathQuillGGB(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		$wnd.$ggbQuery(elsecondInside).mathquillggb('revert');
		elsecondInside.innerHTML = '';
		$wnd.$ggbQuery(elsecondInside).mathquillggb('latex', '');
		$wnd.$ggbQuery(elsecondInside).mathquillggb('editable').focus();
	}-*/;

	public static native void updateEditingMathQuillGGB(Element parentElement,
	        String newFormula) /*-{
		// this method must not freeze, otherwise the historyPopup would not
		// get focus! It is necessary, however, to get focus
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		$wnd.$ggbQuery(elsecondInside).mathquillggb('revert');
		elsecondInside.innerHTML = newFormula;

		//console.log(newFormula);

		// note: we use this from historyPopup, so it should not ask focus!
		$wnd.$ggbQuery(elsecondInside).mathquillggb('editable');
	}-*/;

	public static native String getActualEditedValue(Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elsecondInside);
		return thisjq.mathquillggb('text');
	}-*/;

	public static native int getCaretPosInEditedValue(Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elsecondInside);
		var str1 = thisjq.mathquillggb('text');
		var str2 = thisjq.mathquillggb('textpluscursor');
		var inx = 0;
		while (inx < str1.length && inx < str2.length
				&& str1.charAt(inx) === str2.charAt(inx)) {
			inx++;
		}
		return inx - 1;
	}-*/;

	public static native void writeLatexInPlaceOfCurrentWord(
	        Element parentElement, String latex, String currentWord,
	        boolean command) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elsecondInside);
		var eqstring = latex;

		if (command) {
			// we should write \\left[ instead of [
			// and \\right] instead of ]
			eqstring = eqstring.replace("[", "\\left[");
			eqstring = eqstring.replace("\\left\\left[", "\\left[");
			// in case of typo
			eqstring = eqstring.replace("\\right\\left[", "\\right[");
			eqstring = eqstring.replace("]", "\\right]");
			eqstring = eqstring.replace("\\right\\right]", "\\right]");
			// in case of typo
			eqstring = eqstring.replace("\\left\\right]", "\\left]");

			// ln(<x>)
			eqstring = eqstring.replace("(", "\\left(");
			eqstring = eqstring.replace("\\left\\left(", "\\left(");
			// in case of typo
			eqstring = eqstring.replace("\\right\\left(", "\\right(");
			eqstring = eqstring.replace(")", "\\right)");
			eqstring = eqstring.replace("\\right\\right)", "\\right)");
			// in case of typo
			eqstring = eqstring.replace("\\left\\right)", "\\left)");
		}

		// IMPORTANT! although the following method is called with
		// 1+3 parameters, it is assumed that there is a fourth kind
		// of input added, which is the place of the Cursor
		thisjq.mathquillggb('replace', eqstring, currentWord, command);

		// this does not work, why?
		// make sure the length of brackets (e.g. Quotation marks) are Okay
		//$wnd.setTimeout(function() {
		//	$wnd.$ggbQuery(elsecondInside).mathquillggb('redraw');
		//}, 500);
	}-*/;

	public static boolean newFormulaCreatedMathQuillGGB(
	        final RadioButtonTreeItem rbti, final Element parentElement, final String input, final String latex) {
		AsyncOperation callback = new AsyncOperation() {
			public void callback(Object o) {
				// this should only be called when the new formula creation
				// is really successful! i.e. return true as old behaviour
				stornoFormulaMathQuillGGB(rbti, parentElement);
			}
		};
		// return value is not reliable, callback is
		return rbti.stopNewFormulaCreation(input, latex, callback);
	}

	public static native void escEditingEquationMathQuillGGB(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var elsecondInside = elsecond.lastChild;
		var thisjq = $wnd.$ggbQuery(elsecondInside);

		var latexq = null;
		elsecond.previousSibling.style.display = "block";
		@geogebra.html5.main.DrawEquationWeb::endEditingEquationMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		thisjq.mathquillggb('revert').mathquillggb();
	}-*/;

	public static native void endEditingEquationMathQuillGGB(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elsecondInside);
		var latexq = thisjq.mathquillggb('text');
		elsecond.previousSibling.style.display = "block";
		//var rett =
		@geogebra.html5.main.DrawEquationWeb::endEditingEquationMathQuillGGB(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		//if (rett) {
		thisjq.mathquillggb('revert').mathquillggb();
		//}
	}-*/;

	public static boolean endEditingEquationMathQuillGGB(
	        RadioButtonTreeItem rbti,
	        String latex) {
		return rbti.stopEditing(latex);
	}

	/**
	 * Updates a MathQuillGGB equation which was created by
	 * drawEquationMathQuillGGB
	 * 
	 * @param parentElement
	 *            the same element as in drawEquationMathQuillGGB
	 */
	public static native void updateEquationMathQuillGGB(String htmlt,
	        Element parentElement, boolean noEqnArray) /*-{

		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		if (noEqnArray) {
			$wnd.$ggbQuery(elsecondInside).mathquillggb('revert').html(htmlt)
					.mathquillggb();

			// Make sure the length of brackets and square roots are OK
			$wnd.setTimeout(function() {
				$wnd.$ggbQuery(elsecondInside).mathquillggb('latex', htmlt);
			});
		} else {
			$wnd.$ggbQuery(elsecondInside).mathquillggb('revert').html(htmlt)
					.mathquillggb('eqnarray');

			// Make sure the length of brackets and square roots are OK
			//			$wnd.setTimeout(function() {
			//				// TODO: needs testing
			//				//$wnd.$ggbQuery(elsecond).mathquillggb('latex', htmlt);
			//				$wnd.$ggbQuery(elsecond).mathquillggb('eqnarray');
			//			});
		}
	}-*/;

	public static native JavaScriptObject grabCursorForScrollIntoView(
	        Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;
		var elsecondInside = elsecond.lastChild;

		var jQueryObject = $wnd.$ggbQuery(elsecondInside).find('.cursor');
		if ((jQueryObject !== undefined) && (jQueryObject.length > 0)) {
			return jQueryObject[0];
		}
		return null;
	}-*/;

	public static native JavaScriptObject grabSelectionFocusForScrollIntoView(
	        Element parentElement) /*-{

		var jqel = $wnd.$ggbQuery(parentElement).find('.selection');

		if ((jqel !== undefined) && (jqel.length !== undefined)
				&& (jqel.length > 0)) {
			return jqel[0];
		} else {
			return null;
		}
	}-*/;/*

		// The following code (based on $wnd.getSelection) does not work!
		var selectionRang = $wnd.getSelection();
		var resultNode = null;
		if (selectionRang.rangeCount > 1) {
			// select the range that is not the textarea!
			for (var ii = 0; ii < selectionRang.rangeCount; ii++) {
				resultNode = selectionRang.getRangeAt(ii).endContainer;
				// it is probably a textNode, so let's get its parent node!
				while (resultNode.nodeType === 3) {
					resultNode = resultNode.parentNode;
				}
				// now if it is the textarea, then continue,
				// otherwise break!
				if (resultNode.nodeName.toLowerCase() === 'textarea') {
					continue;
				} else {
					break;
				}
			}
		} else if (selectionRang.rangeCount == 1) {
			resultNode = selectionRang.focusNode;
			// selectionRang is probably a textNode, so let's get its parent node!
			while (resultNode.nodeType === 3) {
				resultNode = resultNode.parentNode;
			}
		} else {
			return null;
		}
		if (resultNode.nodeName.toLowerCase() === 'textarea') {
			// now what? return null...
			return null;
		}
		//resultNode.style.backgroundColor = 'red';
		//resultNode.className += ' redimportant';
		return resultNode;
	}-*//*;*/

	public static void scrollSelectionIntoView(RadioButtonTreeItem rbti,
	        Element parentElement, boolean newCreationMode) {
		JavaScriptObject jo = grabSelectionFocusForScrollIntoView(parentElement);
		if (jo != null)
			scrollJSOIntoView(jo, rbti, parentElement, false);
	}

	/**
	 * This is an autoScroll to the edited formula in theory, so it could be
	 * just a _scrollToBottom_ in practice, but there is a case when the
	 * construction is long and a formula on its top is edited...
	 * 
	 * It's lucky that GWT's Element.scrollIntoView exists, so we can call that
	 * method...
	 * 
	 * Moreover, we also need to scroll to the cursor, which can be done in one
	 * operation in cases we need that...
	 */
	public static void scrollCursorIntoView(RadioButtonTreeItem rbti,
	        Element parentElement, boolean newCreationMode) {
		JavaScriptObject jo = grabCursorForScrollIntoView(parentElement);
		if (jo != null) {
			scrollJSOIntoView(jo, rbti, parentElement, newCreationMode);
		} else {
			rbti.scrollIntoView();
		}
	}

	private static void scrollJSOIntoView(JavaScriptObject jo,
	        RadioButtonTreeItem rbti, Element parentElement,
	        boolean newCreationMode) {

		Element joel = Element.as(jo);
		joel.scrollIntoView();

		// Note: the following hacks should only be made in
		// new creation mode! so boolean introduced...
		if (newCreationMode) {
			// if the cursor is on the right or on the left,
			// it would be good to scroll some more, to show the "X" closing
			// sign and the blue border of the window! How to know that?
			// let's compare their places, and if the difference is little,
			// scroll to the left/right!
			if (joel.getAbsoluteLeft() - parentElement.getAbsoluteLeft() < 50) {
				// NewRadioButtonTreeItem class in theory
				rbti.getElement().setScrollLeft(0);
			} else if (parentElement.getAbsoluteRight()
			        - joel.getAbsoluteRight() < 50) {
				// NewRadioButtonTreeItem class in theory
				rbti.getElement().setScrollLeft(Integer.MAX_VALUE);
			} else if (joel.getAbsoluteLeft()
			        - rbti.getElement().getAbsoluteLeft() < 50) {
				// we cannot show the "X" sign all the time anyway!
				// but it would be good not to keep the cursor on the
				// edge...
				// so if it is around the edge by now, scroll!
				rbti.getElement().setScrollLeft(
				        rbti.getElement().getScrollLeft() - 50
				                + joel.getAbsoluteLeft()
				                - rbti.getElement().getAbsoluteLeft());
			} else if (rbti.getElement().getAbsoluteRight()
			        - joel.getAbsoluteRight() < 50) {
				// similarly
				rbti.getElement().setScrollLeft(
				        rbti.getElement().getScrollLeft() + 50
				                - rbti.getElement().getAbsoluteRight()
				                + joel.getAbsoluteRight());
			}
		}
	}

	/**
	 * Removes the "\begin{eqnarray}" and "\end{eqnarray}" notations from the
	 * beginning and end of the string, or returns the string kept intact
	 * 
	 * @param htmlt
	 *            LaTeX equation string
	 * @return input without "\begin{eqnarray}" and "\end{eqnarray}"
	 */
	public static String stripEqnArray(String htmlt) {
		if (htmlt.startsWith("\\begin{eqnarray}")
		        && htmlt.endsWith("\\end{eqnarray}")) {
			return htmlt.substring(16, htmlt.length() - 14);
		}
		return htmlt;
	}

	public static native JsArrayInteger drawEquationCanvasMath(Context2d ctx,
	        String mathmlStr, int x, int y, String fg, String bg) /*-{

		// Gabor's code a bit simplified

		var script_loaded = @geogebra.html5.main.DrawEquationWeb::scriptloaded;
		if (!script_loaded) {
			return [ 50, 50 ];
		}

		var layout = $wnd.cvm.layout;
		var mathMLParser = $wnd.cvm.mathml.parser;
		var domParser = new $wnd.DOMParser();

		var mathML2Expr = function(text) {
			var mathml = domParser.parseFromString(text, "text/xml").firstChild;
			return mathMLParser.parse(mathml);
		};

		var getBox = function(e) {
			return layout.ofExpr(e).box();
		};

		var expression = mathML2Expr(mathmlStr);
		var box = getBox(expression);

		if (fg) {
			box = $wnd.cvm.box.ColorBox.instanciate(fg, box);
		}

		if (bg) {
			box = $wnd.cvm.box.Frame.instanciate({
				background : bg
			}, box);
		}

		var height = box.ascent - box.descent;

		box.drawOnCanvas(ctx, x, y + box.ascent);

		return [ $wnd.parseInt(box.width, 10), $wnd.parseInt(height, 10) ];
	}-*/;

	public static GDimension computeCorrection(GDimension dim,
	        GDimension dimSmall, double rotateDegree) {

		int dimWidth = dim.getWidth();
		if (dimWidth <= 0)
			dimWidth = 1;

		int dimHeight = dim.getHeight();
		if (dimHeight <= 0)
			dimHeight = 1;

		double dimTopCorr = 0;
		double dimLeftCorr = 0;

		if (rotateDegree != 0) {
			double rotateDegreeForTrig = rotateDegree;

			while (rotateDegreeForTrig < 0)
				rotateDegreeForTrig += 360;

			if (rotateDegreeForTrig > 180)
				rotateDegreeForTrig -= 180;

			if (rotateDegreeForTrig > 90)
				rotateDegreeForTrig = 180 - rotateDegreeForTrig;

			// Now rotateDegreeForTrig is between 0 and 90 degrees

			rotateDegreeForTrig *= Math.PI / 180;

			// Now rotateDegreeForTrig is between 0 and PI/2, it is in radians
			// actually!
			// INPUT for algorithm got: rotateDegreeForTrig, dimWidth, dimHeight

			// dimWidth and dimHeight are the scaled and rotated dims...
			// only the scaled, but not rotated versions should be computed from
			// them:

			double helper = Math.cos(2 * rotateDegreeForTrig);

			double dimWidth0;
			double dimHeight0;
			if (Kernel.isZero(helper)) {
				// PI/4, PI/4
				dimWidth0 = dimHeight0 = Math.sqrt(2) * dimHeight / 2;
				dimWidth0 = dimSmall.getWidth();
				if (dimWidth0 <= 0)
					dimWidth0 = 1;

				dimHeight0 = dimSmall.getHeight();
				if (dimHeight0 <= 0)
					dimHeight0 = 1;

				helper = (dimHeight + dimWidth) / 2.0 * Math.sqrt(2);

				dimWidth0 *= helper / (dimHeight0 + dimWidth0);
				dimHeight0 = helper - dimWidth0;
			} else {
				dimHeight0 = (dimHeight * Math.cos(rotateDegreeForTrig) - dimWidth
				        * Math.sin(rotateDegreeForTrig))
				        / helper;
				dimWidth0 = (dimWidth * Math.cos(rotateDegreeForTrig) - dimHeight
				        * Math.sin(rotateDegreeForTrig))
				        / helper;
			}

			// dimHeight0 and dimWidth0 are the values this algorithm needs

			double dimHalfDiag = Math.sqrt(dimWidth0 * dimWidth0 + dimHeight0
			        * dimHeight0) / 2.0;

			// We also have to compute the bigger and lesser degrees at the
			// diagonals
			// Tangents will be positive, as they take positive numbers (and in
			// radians)
			// between 0 and Math.PI / 2

			double diagDegreeWidth = Math.atan(dimHeight0 / dimWidth0);
			double diagDegreeHeight = Math.atan(dimWidth0 / dimHeight0);

			diagDegreeWidth += rotateDegreeForTrig;
			diagDegreeHeight += rotateDegreeForTrig;

			// diagDegreeWidth might slide through the other part, so substract
			// it from Math.PI, if necessary
			if (diagDegreeWidth > Math.PI / 2)
				diagDegreeWidth = Math.PI - diagDegreeWidth;

			// doing the same for diagDegreeHeight
			if (diagDegreeHeight > Math.PI / 2)
				diagDegreeHeight = Math.PI - diagDegreeHeight;

			// half-height of new formula: dimHalfDiag * sin(diagDegreeWidth)
			dimTopCorr = dimHalfDiag * Math.sin(diagDegreeWidth);
			dimTopCorr = dimHeight0 / 2.0 - dimTopCorr;

			// half-width of new formula: dimHalfDiag * sin(diagDegreeHeight)
			dimLeftCorr = dimHalfDiag * Math.sin(diagDegreeHeight);
			dimLeftCorr = dimWidth0 / 2.0 - dimLeftCorr;
		}

		return new GDimensionW((int) dimLeftCorr, (int) dimTopCorr);
	}
}
