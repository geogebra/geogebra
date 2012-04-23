package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.Color;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.css.GuiResources;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.helper.ScriptLoadCallback;
import geogebra.web.html5.DynamicScriptElement;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

public class DrawEquationWeb implements DrawEquationInterface {
	
	private static boolean scriptloaded = false;

	private static HashMap<String, SpanElement> equations = new HashMap<String, SpanElement>();
	private static HashMap<String, Integer> equationAges = new HashMap<String, Integer>();
	private boolean needToDrawEquation = false;
	private AbstractApplication app;
	
	public DrawEquationWeb(AbstractApplication app) {
		//export module base url;
		exportGetModuleBaseUrl();
		this.app = app;
		//Load script first
		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc(GWT.getModuleBaseURL()+GeoGebraConstants.MATHML_URL);
		script.addLoadHandler(new ScriptLoadCallback() {
			
			public void onLoad() {
				scriptloaded = true;
				cvmBoxInit(GWT.getModuleBaseURL());
				checkIfNeedToDraw();
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

	protected void checkIfNeedToDraw() {
	  if (needToDrawEquation) {
		  app.getEuclidianView1().repaintView();
	  }
    }

	protected native void cvmBoxInit(String moduleBaseURL) /*-{
	    $wnd.cvm.box.init(moduleBaseURL);
    }-*/;

	public void setUseJavaFontsForLaTeX(AbstractApplication app, boolean b) {
	    // not relevant for web
    }

	/**
	 * This should make all the LaTeXes temporarily disappear
	 * 
	 * @param ev: latexes of only this EuclidianView - TODO: implement
	 */
	public static void clearLaTeXes(EuclidianView ev) {
		Iterator<String> eei = equations.keySet().iterator();
		ArrayList<String> eeii = new ArrayList<String>();
		while(eei.hasNext()) {
			String eein = eei.next();
			Integer age = equationAges.get(eein);
			if (age == null)
				age = 0;
			if (age > 5) {// clearLaTeXes can be called this much until redraw
				Element toclear = equations.get(eein);
				Element tcparent = toclear.getParentElement();
				tcparent.removeChild(toclear);
				eeii.add(eein);// avoid concurrent modification exception
			} else {
				equationAges.put(eein, ++age);
				equations.get(eein).getStyle().setDisplay(Style.Display.NONE);
			}
		}
		for (int i = eeii.size() - 1; i >= 0; i--) {
			equations.remove(eeii.get(i));
			equationAges.remove(eeii.get(i));
		}
	}

	/**
	 * Does not only clear the latexes, but also deletes them (on special occasions)
	 * 
	 * @param ev: latexes of only this EuclidianView - TODO: implement
	 */
	public static void deleteLaTeXes(EuclidianView ev) {
		Iterator<SpanElement> eei = equations.values().iterator();
		while(eei.hasNext()) {
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
	 * @param parentElement: adds the equation as the child of this element
	 * @param eqstring: the equation in LaTeX
	 * @param fgColor: foreground color
	 * @param bgColor: background color
	 */
	public static void drawEquationAlgebraView(Element parentElement, String eqstring, Color fgColor, Color bgColor) {
		// no scriptloaded check yet (is it necessary?)
		// no EuclidianView 1,2 yet
		
		// make sure eg FractionText[] works (surrounds with {} which doesn't draw well in MathQuill)
		if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
			eqstring = eqstring.substring(1, eqstring.length() - 1);
		}

		// remove $s
		eqstring = eqstring.trim();
		while (eqstring.startsWith("$")) eqstring = eqstring.substring(1).trim();
		while (eqstring.endsWith("$")) eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

		// TODO: replace this with something better
		//eqstring = eqstring.replace("\\begin{tabular}", "");
		//eqstring = eqstring.replace("\\end{tabular}", "");

		// TODO: hack bad absolute value - sqrt is also bad, so another solution is needed
		//eqstring = eqstring.replace("\\left|", "|");
		//eqstring = eqstring.replace("\\right|", "|");

		// remove all \; and \,
		eqstring = eqstring.replace("\\;","");
		eqstring = eqstring.replace("\\,","");

		AbstractApplication.debug("Algebra View: "+eqstring);

		SpanElement ih = DOM.createSpan().cast();

		// these two doesn't work either
		//ih.getStyle().setHeight(50, Style.Unit.PX);
		//parentElement.getStyle().setHeight(50, Style.Unit.PX);

		drawEquationMathQuill(ih, eqstring, parentElement);
		//ih.getStyle().setPosition(Style.Position.STATIC);

		//ih.getStyle().setBackgroundColor(Color.getColorString(bgColor));
		ih.getStyle().setColor(Color.getColorString(fgColor));
	}

	public Dimension drawEquation(AbstractApplication app, GeoElement geo,
            Graphics2D g2, int x, int y, String eqstring, Font font, boolean serif,
            Color fgColor, Color bgColor, boolean useCache) {
		

		if (true) { // the new way to draw an Equation (latex)
			// no scriptloaded check yet (is it necessary?)
			// no EuclidianView 1,2 yet
			
			// make sure eg FractionText[] works (surrounds with {} which doesn't draw well in MathQuill)
			if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

			
			AbstractApplication.debug(eqstring);

			// remove $s
			eqstring = eqstring.trim();
			while (eqstring.startsWith("$")) eqstring = eqstring.substring(1).trim();
			while (eqstring.endsWith("$")) eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

			SpanElement ih = equations.get(eqstring);
			equationAges.put(eqstring, 0);
			if (ih == null) {
				ih = DOM.createSpan().cast();
				ih.getStyle().setPosition(Style.Position.ABSOLUTE);
				drawEquationMathQuill(ih, eqstring,
					((Application)app).getCanvas().getCanvasElement().getParentElement());
				equations.put(eqstring, ih);
			} else {
				ih.getStyle().setDisplay(Style.Display.INLINE);
			}
			ih.getStyle().setLeft(x, Style.Unit.PX);
			ih.getStyle().setTop(y, Style.Unit.PX);
			ih.getStyle().setBackgroundColor(Color.getColorString(bgColor));
			ih.getStyle().setColor(Color.getColorString(fgColor));
			return new geogebra.web.awt.Dimension(ih.getOffsetWidth(), ih.getOffsetHeight());
		}

		// the old way to draw an Equation (mathml)
		JsArrayInteger ret = null;
		if (scriptloaded) {
			AbstractApplication.debug(eqstring);
			ret = drawEquation(((geogebra.web.awt.Graphics2D)g2).getCanvas().getContext2d(), eqstring, x, y);
		} else {
			needToDrawEquation  = true;
		}
			
	    return new geogebra.web.awt.Dimension(ret == null ? 100 : ret.get(0),ret == null ? 100 : ret.get(1));
    }

	public static native JsArrayInteger drawEquation(Context2d ctx, String mathmlStr, int x, int y) /*-{
		var script_loaded = @geogebra.web.main.DrawEquationWeb::scriptloaded;
		if (script_loaded) {
			var layout = $wnd.cvm.layout;
			var mathMLParser = $wnd.cvm.mathml.parser;
	
			// Steal the XML parser from the browser :)
			var domParser = new $wnd.DOMParser();
			
			// Define some helper functions
			var mathML2Expr = function (text) {
			    var mathml = domParser.parseFromString(text, "text/xml").firstChild;
			    return mathMLParser.parse(mathml);
			};
			
			var getBox = function (e) {
			    return layout.ofExpr(e).box();
			};
			
			// The mathML text of the expression to be displayed
			//var text = "<apply><root/><apply><divide/><cn>1</cn><apply><plus/><ci>x</ci><cn>1</cn></apply></apply></apply>";
			
			// How to display it
			var expression = mathML2Expr(mathmlStr);
			
			
			var box = getBox(expression);
			
			var height = box.ascent - box.descent;
			
			box.drawOnCanvas(ctx, x, y + box.ascent);
			
			var ret = [$wnd.parseInt(box.width,10), $wnd.parseInt(height,10)];
			
			return ret;
		} else {
			return [50,50];
		}
	}-*/;

	/**
	 * The JavaScript/JQuery bit of drawing an equation with MathQuill
	 * More could go into GWT, but it was easier with JSNI
	 * 
	 * @param canv: the canvas element to draw over to
	 * @param el: the element which should be drawn  
	 */
	public static native void drawEquationMathQuill(Element el, String htmlt, Element parentElement) /*-{

		//el.style.position = "absolute";
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

		var elfirst = $doc.createElement("div");
		elfirst.style.position = "absolute";
		elfirst.style.zIndex = 2;
		elfirst.style.width = "100%";
		elfirst.style.height = "100%";
		el.appendChild(elfirst);

		var elsecond = $doc.createElement("span");
		elsecond.innerHTML = htmlt;
		el.appendChild(elsecond);

		parentElement.appendChild(el);
		$wnd.jQuery(elsecond).mathquill();//.delay(2000).mathquill('redraw');
	}-*/;
}
