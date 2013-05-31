package geogebra.html5.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.DrawEquation;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.html5.Browser;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.ScriptLoadCallback;

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

			if (eqID.length() < 1)
				continue;
			else if (!eqID.substring(0, 1).equals("0") &&
					 !eqID.substring(0, 1).equals(""+ev.getEuclidianViewNo()))
				continue;

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

		// logging takes too much time
		// App.debug("Algebra View: "+eqstring);

		DivElement ih = DOM.createDiv().cast();
		ih.getStyle().setPosition(Style.Position.RELATIVE);

		int el = latexString.length();
		String eqstring = stripEqnArray(latexString);
		drawEquationMathQuill(ih, eqstring, 0, parentElement, true,
		        el == eqstring.length(), true);

		// ih.getStyle().setBackgroundColor(Color.getColorString(bgColor));

		if (fgColor != null)
			ih.getStyle().setColor(GColor.getColorString(fgColor));
	}

	public GDimension drawEquation(App app1, GeoElement geo, GGraphics2D g2,
	        int x, int y, String latexString, GFont font, boolean serif,
	        GColor fgColor, GColor bgColor, boolean useCache) {

		// which?
		int fontSize = g2.getFont().getSize();
		int fontSize2 = font.getSize();

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
			return new geogebra.html5.awt.GDimensionW(jai.get(0), jai.get(1));
		}

		// the new way to draw an Equation (latex)
		// no scriptloaded check yet (is it necessary?)

		String eqstring = inputLatexCosmetics(latexString);

		if (geo instanceof TextProperties) {
			if ((((TextProperties)geo).getFontStyle() & GFont.ITALIC) == 0) {
				// set to be not italic
				eqstring = "\\mathrm{"+ eqstring +"}";
			}
		}

		// whether we are painting on EV1 now
		boolean visible1 =
				(((GGraphics2DW)g2).getCanvas() == ((AppWeb)app1).getCanvas());

		// whether we are painting on EV2 now
		boolean visible2 = false;
		if (((AppWeb)app1).hasEuclidianView2()) {
			if (((GGraphics2DW)g2).getCanvas() == ((GGraphics2DW)app1.getEuclidianView2().getGraphicsForPen()).getCanvas()) {
				visible2 = true;
			}
		}

		GGraphics2DW g2visible = (GGraphics2DW)g2;
		if (!visible1 && !visible2) {
			if (((AppWeb)app1).hasEuclidianView2EitherShowingOrNot()) {
				if (app1.getEuclidianView2().getTempGraphics2D(font) == g2) {
					g2visible = (GGraphics2DW)((EuclidianView)((AppWeb)app1).getEuclidianView2()).getGraphicsForPen();
				} else if (app1.getEuclidianView1().getTempGraphics2D(font) == g2) {
					g2visible = (GGraphics2DW)((EuclidianView)((AppWeb)app1).getEuclidianView1()).getGraphicsForPen();
				}
			} else {
				g2visible = (GGraphics2DW)((EuclidianView)((AppWeb)app1).getEuclidianView1()).getGraphicsForPen();
			}
		}

		String prestring = "0";
		if (visible1)
			prestring = "1";
		else if (visible2)
			prestring = "2";

		String eqstringid = prestring + "@" + eqstring + "@" + geo.getID();

		SpanElement ih = equations.get(eqstringid);
		equationAges.put(eqstringid, 0);
		if (ih == null) {
			ih = DOM.createSpan().cast();
			ih.getStyle().setPosition(Style.Position.ABSOLUTE);
			int el = eqstring.length();
			eqstring = stripEqnArray(eqstring);

			drawEquationMathQuill(ih, eqstring, fontSize,
					g2visible.getCanvas().getCanvasElement().getParentElement(),
					true, el == eqstring.length(), visible1 || visible2);

			equations.put(eqstringid, ih);

			// set a flag that the kernel needs a new update
			app1.getKernel().setUpdateAgain(true);
		} else {
			ih.getStyle().setDisplay(Style.Display.INLINE);
			if (visible1 || visible2)
				ih.getStyle().setVisibility(Style.Visibility.VISIBLE);
			// otherwise do not set it invisible, just leave everything as it is
		}
		if (visible1 || visible2) {
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

		if (Browser.isFirefox() && (fontSize != 12)) {
			return new geogebra.html5.awt.GDimensionW(getScaledWidth(ih),
			        getScaledHeight(ih));
		}

		return new geogebra.html5.awt.GDimensionW(ih.getOffsetWidth(),
		        ih.getOffsetHeight());
	}

	public static native int getScaledWidth(Element el) /*-{
		var ell = el;
		if (el.lastChild) {//elsecond
			ell = el.lastChild;
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

	public static native int getScaledHeight(Element el) /*-{
		var ell = el;
		if (el.lastChild) {//elsecond
			ell = el.lastChild;
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
	public static native void drawEquationMathQuill(Element el, String htmlt, int fontSize,
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

		if ((fontSize != 0) && (fontSize != 12)) {
			// floating point division in JavaScript!
			elsecond.style.zoom = fontSize / 12;
			elsecond.style.MsZoom = fontSize / 12;
			elsecond.style.MozTransform = "scale(" + (fontSize / 12) + ")";
			elsecond.style.MozTransformOrigin = "0px 0px";
			elsecond.style.OTransform = "scale(" + (fontSize / 12) + ")";
			elsecond.style.OTransformOrigin = "0px 0px";
			if (addOverlay) {
				elfirst.style.zoom = fontSize / 12;
				elfirst.style.MsZoom = fontSize / 12;
				elfirst.style.MozTransform = "scale(" + (fontSize / 12) + ")";
				elfirst.style.MozTransformOrigin = "0px 0px";
				elfirst.style.OTransform = "scale(" + (fontSize / 12) + ")";
				elfirst.style.OTransformOrigin = "0px 0px";
			}
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
								@geogebra.html5.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
							} else if (code == 27) {
								@geogebra.html5.main.DrawEquationWeb::escEditingEquationMathQuill(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
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
								@geogebra.html5.main.DrawEquationWeb::escEditingEquationMathQuill(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
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
		@geogebra.html5.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		thisjq.mathquill('revert').mathquill();
	}-*/;

	public static native void endEditingEquationMathQuill(
	        RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var thisjq = $wnd.jQuery(elsecond);
		var latexq = thisjq.mathquill('text');
		elsecond.previousSibling.style.display = "block";
		@geogebra.html5.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/html5/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
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
			box = $wnd.cvm.box.Frame.instanciate({ background: bg }, box);
		}

		var height = box.ascent - box.descent;

		box.drawOnCanvas(ctx, x, y + box.ascent);

		return [ $wnd.parseInt(box.width, 10), $wnd.parseInt(height, 10) ]; 
	}-*/; 
}
