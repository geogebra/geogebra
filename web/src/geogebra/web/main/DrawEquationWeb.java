package geogebra.web.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.web.euclidian.EuclidianViewW;
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

public class DrawEquationWeb implements DrawEquationInterface {
	
	private static boolean scriptloaded = false;

	private static HashMap<String, SpanElement> equations = new HashMap<String, SpanElement>();
	private static HashMap<String, Integer> equationAges = new HashMap<String, Integer>();
	private boolean needToDrawEquation = false;
	private App app;
	
	public DrawEquationWeb(App app) {
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

	public void setUseJavaFontsForLaTeX(App app, boolean b) {
	    // not relevant for web
    }

	/**
	 * This should make all the LaTeXes temporarily disappear
	 * 
	 * @param ev: latexes of only this EuclidianView - TODO: implement
	 */
	public static void clearLaTeXes(EuclidianViewW ev) {
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
	public static void deleteLaTeXes(EuclidianViewW ev) {
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
	public static void drawEquationAlgebraView(Element parentElement, String eqstring, GColor fgColor, GColor bgColor) {
		// no scriptloaded check yet (is it necessary?)
		// no EuclidianView 1,2 yet

		// logging takes too much time
		//App.debug("Algebra View: "+eqstring);

		DivElement ih = DOM.createDiv().cast();
		ih.getStyle().setPosition(Style.Position.RELATIVE);

		drawEquationMathQuill(ih, eqstring, parentElement);

		//ih.getStyle().setBackgroundColor(Color.getColorString(bgColor));
		ih.getStyle().setColor(GColor.getColorString(fgColor));
	}

	public GDimension drawEquation(App app, GeoElement geo,
            GGraphics2D g2, int x, int y, String eqstring, GFont font, boolean serif,
            GColor fgColor, GColor bgColor, boolean useCache) {
		

		 // the new way to draw an Equation (latex)
			// no scriptloaded check yet (is it necessary?)
			// no EuclidianView 1,2 yet
			
			// make sure eg FractionText[] works (surrounds with {} which doesn't draw well in MathQuill)
			if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

			// remove all \; and \,
			eqstring = eqstring.replace("\\;","");
			eqstring = eqstring.replace("\\,","");

			// logging takes too much time
			//App.debug(eqstring);

			// remove $s
			eqstring = eqstring.trim();
			while (eqstring.startsWith("$")) eqstring = eqstring.substring(1).trim();
			while (eqstring.endsWith("$")) eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

			String eqstringid = eqstring + "@" + geo.getID();

			SpanElement ih = equations.get(eqstringid);
			equationAges.put(eqstringid, 0);
			if (ih == null) {
				ih = DOM.createSpan().cast();
				ih.getStyle().setPosition(Style.Position.ABSOLUTE);
				drawEquationMathQuill(ih, eqstring,
					((AppW)app).getCanvas().getCanvasElement().getParentElement());
				equations.put(eqstringid, ih);

				// set a flag that the kernel needs a new update
				app.getKernel().setUpdateAgain(true);
			} else {
				ih.getStyle().setDisplay(Style.Display.INLINE);
			}
			ih.getStyle().setLeft(x, Style.Unit.PX);
			ih.getStyle().setTop(y, Style.Unit.PX);
			ih.getStyle().setBackgroundColor(GColor.getColorString(bgColor));
			ih.getStyle().setColor(GColor.getColorString(fgColor));
			return new geogebra.web.awt.GDimensionW(ih.getOffsetWidth(), ih.getOffsetHeight());
		
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
		$wnd.jQuery(elsecond).mathquill();

		// Make sure the length of brackets and square roots are OK
		$wnd.setTimeout(function() {
			$wnd.jQuery(elsecond).mathquill('latex', htmlt);
		});
	}-*/;

	/**
	 * Edits a MathQuill equation which was created by drawEquationMathQuill
	 * 
	 * @param rbti: the tree item for callback
	 * @param parentElement: the same element as in drawEquationMathQuill
	 */
	public static native void editEquationMathQuill(RadioButtonTreeItem rbti, Element parentElement) /*-{

		var elfirst = parentElement.firstChild.firstChild;
		
		elfirst.style.display = 'none';

		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		$wnd.jQuery(elsecond).mathquill('revert').mathquill('editable').focus();

		$wnd.jQuery(elsecond).keyup(function(event) {
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
		$wnd.jQuery(elsecond).focusout(function(event) {
			if (mousein.mout) {
				@geogebra.web.main.DrawEquationWeb::escEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
			}
			event.stopPropagation();
			event.preventDefault();
			return false;
		}).mouseenter(function(event2) {
			mousein.mout = false;
		}).mouseleave(function(event3) {
			mousein.mout = true;
			$(this).focus();
		});
	}-*/;

	public static native void escEditingEquationMathQuill(RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var thisjq = $wnd.jQuery(elsecond);
		var latexq = null;
		elsecond.previousSibling.style.display = "block";
		@geogebra.web.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		thisjq.mathquill('revert').mathquill();
	}-*/;

	public static native void endEditingEquationMathQuill(RadioButtonTreeItem rbti, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		var thisjq = $wnd.jQuery(elsecond);
		var latexq = thisjq.mathquill('text');
		elsecond.previousSibling.style.display = "block";
		@geogebra.web.main.DrawEquationWeb::endEditingEquationMathQuill(Lgeogebra/web/gui/view/algebra/RadioButtonTreeItem;Ljava/lang/String;)(rbti,latexq);
		thisjq.mathquill('revert').mathquill();
	}-*/;

	public static void endEditingEquationMathQuill(RadioButtonTreeItem rbti, String latex) {
		rbti.stopEditing(latex);
	}

	/**
	 * Updates a MathQuill equation which was created by drawEquationMathQuill
	 * @param parentElement: the same element as in drawEquationMathQuill
	 */
	public static native void updateEquationMathQuill(String htmlt, Element parentElement) /*-{
		var elsecond = parentElement.firstChild.firstChild.nextSibling;

		$wnd.jQuery(elsecond).mathquill('revert').html(htmlt).mathquill();

		// Make sure the length of brackets and square roots are OK
		$wnd.setTimeout(function() {
			$wnd.jQuery(elsecond).mathquill('latex', htmlt);
		});
	}-*/;
}
