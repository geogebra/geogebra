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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.InlineHTML;

import java.util.HashMap;
import java.util.Iterator;

public class DrawEquationWeb implements DrawEquationInterface {
	
	private static boolean scriptloaded = false;

	private static HashMap<String, InlineHTML> equations = new HashMap<String, InlineHTML>();
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
		Iterator<InlineHTML> eei = equations.values().iterator();
		while(eei.hasNext())
			eei.next().getElement().getStyle().setDisplay(Style.Display.NONE);
	}

	/**
	 * Does not only clear the latexes, but also deletes them (on special occasions)
	 * 
	 * @param ev: latexes of only this EuclidianView - TODO: implement
	 */
	public static void deleteLaTeXes(EuclidianView ev) {
		Iterator<InlineHTML> eei = equations.values().iterator();
		while(eei.hasNext())
			eei.next().getElement().removeFromParent();
		equations.clear();
	}

	public Dimension drawEquation(AbstractApplication app, GeoElement geo,
            Graphics2D g2, int x, int y, String eqstring, Font font, boolean serif,
            Color fgColor, Color bgColor, boolean useCache) {

		if (true) { // the new way to draw an Equation (latex)
			// no scriptloaded things yet
			// no remove of unused equations yet
			// no setcolor yet
			AbstractApplication.debug(eqstring);

			// remove $s
			eqstring = eqstring.trim();
			while (eqstring.startsWith("$")) eqstring = eqstring.substring(1).trim();
			while (eqstring.endsWith("$")) eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

			InlineHTML ih = equations.get(eqstring);
			if (ih == null) {
				ih = new InlineHTML();
				ih.setHTML(eqstring);
				ih.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
				drawEquationMathQuill(
					//((geogebra.web.awt.Graphics2D)g2).getCanvas().getCanvasElement(),
					((Application)app).getCanvas().getCanvasElement(),
					ih.getElement());
				equations.put(eqstring, ih);
			} else {
				ih.getElement().getStyle().setDisplay(Style.Display.INLINE);
			}
			ih.getElement().getStyle().setLeft(x, Style.Unit.PX);
			ih.getElement().getStyle().setTop(y, Style.Unit.PX);
			return new geogebra.web.awt.Dimension(ih.getElement().getOffsetWidth(), ih.getElement().getOffsetHeight());
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
	 * 
	 * @param canv: the canvas element to draw over to
	 * @param el: the element which should be drawn  
	 */
	public static native void drawEquationMathQuill(CanvasElement canv, Element el) /*-{
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

		// this should be fixed as MathQuill makes the subelements selectable by default
		$wnd.jQuery(el).appendTo($wnd.jQuery(canv).parent()).mathquill();
	}-*/;
}
