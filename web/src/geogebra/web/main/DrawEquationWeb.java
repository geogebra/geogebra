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
import geogebra.web.helper.ScriptLoadCallback;
import geogebra.web.html5.DynamicScriptElement;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

public class DrawEquationWeb implements DrawEquationInterface {
	
	private static boolean scriptloaded = false;
	private boolean needToDrawEquation = false;
	private Application app;
	
	public DrawEquationWeb(Application app) {
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

	public Dimension drawEquation(AbstractApplication app, GeoElement geo,
            Graphics2D g2, int x, int y, String mathml, Font font, boolean serif,
            Color fgColor, Color bgColor, boolean useCache) {
		JsArrayInteger ret = null;
		if (scriptloaded) {
			ret = drawEquation(((geogebra.web.awt.Graphics2D)g2).getCanvas().getContext2d(), mathml, x, y);
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



}
