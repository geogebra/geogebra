package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.DrawEquation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.web.awt.GGraphics2DW;
import geogebra.web.euclidian.EuclidianViewWeb;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;
import geogebra.web.helper.ScriptLoadCallback;
import geogebra.web.html5.DynamicScriptElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

public class DrawEquationWeb extends DrawEquation {

	static boolean scriptloaded = false;

	private HashMap<String, SpanElement> equations = new HashMap<String, SpanElement>();
	private HashMap<String, Integer> equationAges = new HashMap<String, Integer>();

	public DrawEquationWeb() {
		// export module base url;
		exportGetModuleBaseUrl();
		// Load script first
		DynamicScriptElement script = (DynamicScriptElement) Document.get()
		        .createScriptElement();
		script.setSrc(GWT.getModuleBaseURL() + GeoGebraConstants.MATHML_URL);
		script.addLoadHandler(new ScriptLoadCallback() {

			public void onLoad() {
				scriptloaded = true;
				cvmBoxInit(GWT.getModuleBaseURL());
			}
		});
		Document.get().getBody().appendChild(script);
	}

	private native void exportGetModuleBaseUrl() /*-{
		if (!$wnd.ggw) {
			$wnd.ggw = {};
		}
		$wnd.ggw.getGWTModuleBaseURL = $entry(@com.google.gwt.core.client.GWT::getModuleBaseURL());
	}-*/;

	protected native void cvmBoxInit(String moduleBaseURL) /*-{
		$wnd.cvm.box.init(moduleBaseURL);
	}-*/;

	public void setUseJavaFontsForLaTeX(App app, boolean b) {
		// not relevant for web
	}

	public static String inputLatexCosmetics(String eqstringin) {

		String eqstring = eqstringin;

		// make sure eg FractionText[] works (surrounds with {} which doesn't
		// draw well in MathQuill)
		if (eqstring.length() >= 2)
			if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

		// remove $s
		eqstring = eqstring.trim();
		while (eqstring.startsWith("$"))
			eqstring = eqstring.substring(1).trim();
		while (eqstring.endsWith("$"))
			eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

		// remove all \; and \,
		eqstring = eqstring.replace("\\;", "");
		eqstring = eqstring.replace("\\,", "");
		eqstring = eqstring.replace("\\ ", "");

		eqstring = eqstring.replace("\\left\\{", "\\lbrace ");
		eqstring = eqstring.replace("\\right\\}", "\\rbrace ");

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
		return eqstring;
	}

	/**
	 * This should make all the LaTeXes temporarily disappear
	 * 
	 * @param ev
	 *            latexes of only this EuclidianView - TODO: implement
	 */
	public void clearLaTeXes(EuclidianViewWeb ev) {
		Iterator<String> eei = equations.keySet().iterator();
		ArrayList<String> dead = new ArrayList<String>();
		while (eei.hasNext()) {
			String eqID = eei.next();
			Integer age = equationAges.get(eqID);
			if (age == null)
				age = 0;
			if (age > 5) {// clearLaTeXes can be called this much until redraw
				Element toclear = equations.get(eqID);
				Element tcparent = toclear.getParentElement();
				tcparent.removeChild(toclear);
				dead.add(eqID);// avoid concurrent modification exception
			} else {
				equationAges.put(eqID, ++age);
				equations.get(eqID).getStyle().setDisplay(Style.Display.NONE);
			}
		}
		for (int i = dead.size() - 1; i >= 0; i--) {
			equations.remove(dead.get(i));
			equationAges.remove(dead.get(i));
		}
	}

	/**
	 * Does not only clear the latexes, but also deletes them (on special
	 * occasions)
	 * 
	 * @param ev
	 *            latexes of only this EuclidianView - TODO: implement
	 */
	public void deleteLaTeXes(EuclidianViewWeb ev) {
		Iterator<SpanElement> eei = equations.values().iterator();
		while (eei.hasNext()) {
			Element toclear = eei.next();
			Element tcparent = toclear.getParentElement();
			tcparent.removeChild(toclear);
		}
		equations.clear();
		equationAges.clear();
	}

	/**
	 * Draws an equation on the algebra view in display mode (not editing)
	 * 
	 * @param parentElement
	 *            adds the equation as the child of this element
	 * @param latexString
	 *            the equation in LaTeX
	 * @param fgColor
	 *            foreground color
	 * @param bgColor
	 *            background color
	 */
	public static void drawEquationAlgebraView(Element parentElement,
	        String latexString, GColor fgColor, GColor bgColor) {
		// no scriptloaded check yet (is it necessary?)
		// no EuclidianView 1,2 yet

		// logging takes too much time
		// App.debug("Algebra View: "+eqstring);

		DivElement ih = DOM.createDiv().cast();
		ih.getStyle().setPosition(Style.Position.RELATIVE);

		int el = latexString.length();
		String eqstring = stripEqnArray(latexString);
		drawEquationMathQuill(ih, eqstring, parentElement, true,
		        el == eqstring.length(), true);

		// ih.getStyle().setBackgroundColor(Color.getColorString(bgColor));

		if (fgColor != null)
			ih.getStyle().setColor(GColor.getColorString(fgColor));
	}

	public GDimension drawEquation(App app1, GeoElement geo, GGraphics2D g2,
	        int x, int y, String latexString, GFont font, boolean serif,
	        GColor fgColor, GColor bgColor, boolean useCache) {

		boolean shouldPaintBackground = true;

		if (bgColor == null)
			shouldPaintBackground = false;
		else if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN) && !geo.isVisibleInView(App.VIEW_EUCLIDIAN2))
			shouldPaintBackground = false;
		else if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN2) && 
			(app1.getEuclidianView1().getBackgroundCommon() == bgColor))
			shouldPaintBackground = false;
		else if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN) &&
				(app1.getEuclidianView2() == null))
			shouldPaintBackground = false;
		else if (!geo.isVisibleInView(App.VIEW_EUCLIDIAN) &&
				(app1.getEuclidianView2().getBackgroundCommon() == bgColor))
			shouldPaintBackground = false;
		else if ((app1.getEuclidianView1().getBackgroundCommon() == bgColor) &&
				(app1.getEuclidianView2().getBackgroundCommon() == bgColor))
			shouldPaintBackground = false;

		if (geo.isGeoText() && ((GeoText)geo).isMathML()) {
			// assume that the script is loaded; it is part of resources
			// so we will probably get width and height OK, no need to update again
			JsArrayInteger jai = drawEquationCanvasMath(
				((GGraphics2DW)g2).getCanvas().getContext2d(), latexString, x, y,
				(fgColor == null) ? null : GColor.getColorString(fgColor),
				!shouldPaintBackground ? null : GColor.getColorString(bgColor));
			return new geogebra.web.awt.GDimensionW(jai.get(0), jai.get(1));
		}

		// the new way to draw an Equation (latex)
		// no scriptloaded check yet (is it necessary?)
		// no EuclidianView 1,2 yet

		String eqstring = "\\mathrm{"+inputLatexCosmetics(latexString)+"}";

		String eqstringid = eqstring + "@" + geo.getID();

		boolean visible =
				(((GGraphics2DW)g2).getCanvas() == ((AppWeb)app1).getCanvas());

		SpanElement ih = equations.get(eqstringid);
		equationAges.put(eqstringid, 0);
		if (ih == null) {
			ih = DOM.createSpan().cast();
			ih.getStyle().setPosition(Style.Position.ABSOLUTE);
			int el = eqstring.length();
			eqstring = stripEqnArray(eqstring);

			drawEquationMathQuill(ih, eqstring,
					((AppWeb)app1).getCanvas().getCanvasElement().getParentElement(),
					true, el == eqstring.length(), visible);

			equations.put(eqstringid, ih);

			// set a flag that the kernel needs a new update
			app1.getKernel().setUpdateAgain(true);
		} else {
			ih.getStyle().setDisplay(Style.Display.INLINE);
			if (visible)
				ih.getStyle().setVisibility(Style.Visibility.VISIBLE);
			// otherwise do not set it invisible, just leave everything as it is
		}
		if (visible) {
			// if it's not visible, leave at its previous place to prevent lag
			ih.getStyle().setLeft(x, Style.Unit.PX);
			ih.getStyle().setTop(y, Style.Unit.PX);

			// as the background is usually (or always) the background of the
			// canvas,
			// it is better if this is transparent, because the grid should be shown
			// just like in the Java version
			if (shouldPaintBackground)
				ih.getStyle().setBackgroundColor(GColor.getColorString(bgColor));

			if (fgColor != null)
				ih.getStyle().setColor(GColor.getColorString(fgColor));
		}

		return new geogebra.web.awt.GDimensionW(ih.getOffsetWidth(),
		        ih.getOffsetHeight());
	}

	/**
	 * The JavaScript/JQuery bit of drawing an equation with MathQuill More
	 * could go into GWT, but it was easier with JSNI
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
	public static native void drawEquationMathQuill(Element el, String htmlt,
	        Element parentElement, boolean addOverlay, boolean noEqnArray, boolean visible) /*-{

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
			el.ondragstart = function(event) {
				return false;
			}
		}
		el.onmousedown = function(event) {
			if (event.preventDefault)
				event.preventDefault();
			return false;
		}
		if (addOverlay) {
			var elfirst = $doc.createElement("div");
			elfirst.style.position = "absolute";
			elfirst.style.zIndex = 2;
			elfirst.style.width = "100%";
			elfirst.style.height = "100%";
			el.appendChild(elfirst);
		}

		var elsecond = $doc.createElement("span");
		elsecond.innerHTML = htmlt;
		el.appendChild(elsecond);

		if (!visible) {
			el.style.visibility = "hidden";
		}

		parentElement.appendChild(el);

		if (noEqnArray) {
			$wnd.jQuery(elsecond).mathquill();

			// Make sure the length of brackets and square roots are OK
			$wnd.setTimeout(function() {
				$wnd.jQuery(elsecond).mathquill('latex', htmlt);
			});
		} else {
			$wnd.jQuery(elsecond).mathquill('eqnarray');

			// Make sure the length of brackets and square roots are OK
			//			$wnd.setTimeout(function() {
			//				// TODO: this needs more testing,
			//				// also for the editing of it
			//				//$wnd.jQuery(elsecond).mathquill('latex', htmlt);
			//				$wnd.jQuery(elsecond).mathquill('eqnarray');
			//			});
		}
	}-*/;

	/**
	 * Edits a MathQuill equation which was created by drawEquationMathQuill
	 * 
	 * @param rbti
	 *            the tree item for callback
	 * @param parentElement
	 *            the same element as in drawEquationMathQuill
	 */
	public static native void editEquationMathQuill(RadioButtonTreeItem rbti,
	        Element parentElement) /*-{

		var elfirst = parentElement.firstChild.firstChild;

		elfirst.style.display = 'none';

		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		$wnd.jQuery(elsecond).mathquill('revert').mathquill('editable').focus();

		$wnd
				.jQuery(elsecond)
				.keyup(
						function(event) {
							var code = 13;
							if (event.keyCode) {
								code = event.keyCode;
							} else if (event.which) {
								code = event.which;
							}
							if (code == 13) {
								@geogebra.web.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
							} else if (code == 27) {
								@geogebra.web.main.DrawEquationWeb::escEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
							}
							event.stopPropagation();
							event.preventDefault();
							return false;
						});

		// hacking to deselect the editing when the user does something else like in Desktop
		var mousein = {};
		mousein.mout = false;
		$wnd.mousein = mousein;
		$wnd
				.jQuery(elsecond)
				.focusout(
						function(event) {
							if ($wnd.mousein.mout) {
								@geogebra.web.main.DrawEquationWeb::escEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
							}
							event.stopPropagation();
							event.preventDefault();
							return false;
						}).mouseenter(function(event2) {
					$wnd.mousein.mout = false;
				}).mouseleave(function(event3) {
					$wnd.mousein.mout = true;
					$(this).focus();
				});
	}-*/;

	public static native void escEditingEquationMathQuill(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var thisjq = $wnd.jQuery(elsecond);
		var latexq = null;
		elsecond.previousSibling.style.display = "block";
		@geogebra.web.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		thisjq.mathquill('revert').mathquill();
	}-*/;

	public static native void endEditingEquationMathQuill(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var thisjq = $wnd.jQuery(elsecond);
		var latexq = thisjq.mathquill('text');
		elsecond.previousSibling.style.display = "block";
		@geogebra.web.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		thisjq.mathquill('revert').mathquill();
	}-*/;

	public static void endEditingEquationMathQuill(RadioButtonTreeItem rbti,
	        String latex) {
		rbti.stopEditing(latex);
	}

	/**
	 * Updates a MathQuill equation which was created by drawEquationMathQuill
	 * 
	 * @param parentElement
	 *            the same element as in drawEquationMathQuill
	 */
	public static native void updateEquationMathQuill(String htmlt,
	        Element parentElement, boolean noEqnArray) /*-{

		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		if (noEqnArray) {
			$wnd.jQuery(elsecond).mathquill('revert').html(htmlt).mathquill();

			// Make sure the length of brackets and square roots are OK
			$wnd.setTimeout(function() {
				$wnd.jQuery(elsecond).mathquill('latex', htmlt);
			});
		} else {
			$wnd.jQuery(elsecond).mathquill('revert').html(htmlt).mathquill(
					'eqnarray');

			// Make sure the length of brackets and square roots are OK
			//			$wnd.setTimeout(function() {
			//				// TODO: needs testing
			//				//$wnd.jQuery(elsecond).mathquill('latex', htmlt);
			//				$wnd.jQuery(elsecond).mathquill('eqnarray');
			//			});
		}

	}-*/;

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

	public static native JsArrayInteger drawEquationCanvasMath(
			Context2d ctx, String mathmlStr, int x, int y, String fg, String bg) /*-{

		// Gabor's code a bit simplified

		var script_loaded = @geogebra.web.main.DrawEquationWeb::scriptloaded;
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
			box = $wnd.cvm.box.Frame.instanciate({ background: bg }, box);
		}

		var height = box.ascent - box.descent;

		box.drawOnCanvas(ctx, x, y + box.ascent);

		return [ $wnd.parseInt(box.width, 10), $wnd.parseInt(height, 10) ]; 
	}-*/; 
}
