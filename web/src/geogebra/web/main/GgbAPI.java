package geogebra.web.main;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.JavaScriptAPI;
import geogebra.common.io.MyXMLio;

import geogebra.web.gui.app.GeoGebraFrame;
import geogebra.web.helper.ScriptLoadCallback;
import geogebra.web.html5.DynamicScriptElement;
import geogebra.web.util.DataUtil;
import geogebra.web.jso.JsUint8Array;

public class GgbAPI  extends geogebra.common.plugin.GgbAPI implements JavaScriptAPI {

	public GgbAPI(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.algebraprocessor=kernel.getAlgebraProcessor();
        this.construction=kernel.getConstruction();

		DynamicScriptElement script = (DynamicScriptElement) Document.get().createScriptElement();
		script.setSrc(GWT.getModuleBaseURL()+"js/jszip.js");
		Document.get().getBody().appendChild(script);

		DynamicScriptElement script2 = (DynamicScriptElement) Document.get().createScriptElement();
		script2.setSrc(GWT.getModuleBaseURL()+"js/jszip-deflate.js");
		Document.get().getBody().appendChild(script2);
	}

    public byte[] getGGBfile() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public String getBase64(boolean includeThumbnail) {
	    // TODO Auto-generated method stub
	    return null;
    }

    public Context2d getContext2D() {
	    return ((Application)app).getCanvas().getContext2d();
    }

	
    public void setBase64(String base64) {
	    // TODO Auto-generated method stub
	    
    }

	
    public void setErrorDialogsActive(boolean flag) {
	    // TODO Auto-generated method stub
	    
    }

	
    public void reset() {
	    // TODO Auto-generated method stub
	    
    }


    public void refreshViews() {
    	app.refreshViews();
    }


    public String getIPAddress() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public String getHostname() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public void openFile(String strURL) {
	    // TODO Auto-generated method stub
	    
    }

	
    public String getGraphicsViewCheckSum(String algorithm, String format) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public boolean writePNGtoFile(String filename, double exportScale,
            boolean transparent, double DPI) {
	    // TODO Auto-generated method stub
	    return false;
    }

	
    public String getPNGBase64(double exportScale, boolean transparent,
            double DPI) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public void drawToImage(String label, double[] x, double[] y) {
	    // TODO Auto-generated method stub
	    
    }


    public void clearImage(String label) {
		GeoElement ge = kernel.lookupLabel(label);
		
		if(!ge.isGeoImage()){
			debug("Bad drawToImage arguments");
			return;
		}
		((GeoImage)ge).clearFillImage();
    }

    public native String getBase64() /*-{

		var isSaving = this.@geogebra.web.main.GgbAPI::getKernel()().@geogebra.common.kernel.Kernel::isSaving()();
		this.@geogebra.web.main.GgbAPI::getKernel()().@geogebra.common.kernel.Kernel::setSaving(Z)(true);

		var ret = "";

		try {
			var xmlstr = this.@geogebra.web.main.GgbAPI::getApplication()().@geogebra.common.main.AbstractApplication::getXML()();
			var mxmlstr = this.@geogebra.web.main.GgbAPI::getApplication()().@geogebra.common.main.AbstractApplication::getMacroXMLorEmpty()();
			var jsstr = this.@geogebra.web.main.GgbAPI::getKernel()().@geogebra.common.kernel.Kernel::getLibraryJavaScript()();
			var pystr = this.@geogebra.web.main.GgbAPI::getKernel()().@geogebra.common.kernel.Kernel::getLibraryPythonScript()();

			var XML_FILE_MACRO = @geogebra.common.io.MyXMLio::XML_FILE_MACRO;
			var PYTHON_FILE = @geogebra.common.io.MyXMLio::PYTHON_FILE;
			var JAVASCRIPT_FILE = @geogebra.common.io.MyXMLio::JAVASCRIPT_FILE;
			var XML_FILE = @geogebra.common.io.MyXMLio::XML_FILE;

    		var zip = new $wnd.JSZip("DEFLATE");
    		if (mxmlstr != "") {
    			zip.add(XML_FILE_MACRO, mxmlstr);
    		}

    		if (pystr != "") {
    			zip.add(PYTHON_FILE, pystr);
    		}
    		zip.add(JAVASCRIPT_FILE, jsstr);
    		zip.add(XML_FILE, xmlstr);

			ret = zip.generate();
		} catch (err) {
		} finally {
			this.@geogebra.web.main.GgbAPI::getKernel()().@geogebra.common.kernel.Kernel::setSaving(Z)(isSaving);
			return ret;
		}
    }-*/;
}
