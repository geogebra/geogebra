package geogebra.web.main;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.core.client.JavaScriptObject;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.MacroInterface;
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
import geogebra.web.kernel.gawt.BufferedImage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.zip.ZipOutputStream;

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

    /**
     * This method does something like geogebra.io.MyXMLio.writeGeoGebraFile,
     * just in base64 in Web.
     */
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

			this.@geogebra.web.main.GgbAPI::writeConstructionImages(Lcom/google/gwt/core/client/JavaScriptObject;)(zip);

    		if (mxmlstr != "") {
				this.@geogebra.web.main.GgbAPI::writeMacroImages(Lcom/google/gwt/core/client/JavaScriptObject;)(zip);
    			zip.add(XML_FILE_MACRO, mxmlstr);
    		}

    		zip.add(JAVASCRIPT_FILE, jsstr);
    		if (pystr != "") {
    			zip.add(PYTHON_FILE, pystr);
    		}
    		zip.add(XML_FILE, xmlstr);

			ret = zip.generate();
		} catch (err) {
			ret = "JAVASCRIPT EXCEPTION CATCHED";
		} finally {
			this.@geogebra.web.main.GgbAPI::getKernel()().@geogebra.common.kernel.Kernel::setSaving(Z)(isSaving);
			return ret;
		}
    }-*/;

	private void writeMacroImages(JavaScriptObject jszip) {
		if (kernel.hasMacros()) {
			ArrayList<MacroInterface> macros = kernel.getAllMacros();
			writeMacroImages(macros, jszip, "");
		}
	}

	private void writeMacroImages(ArrayList<MacroInterface> macros, JavaScriptObject jszip, String filePath) {
		// <-- Modified for Intergeo File Format (Yves Kreis)
		if (macros == null)
			return;

		for (int i = 0; i < macros.size(); i++) {
			// save all images in macro construction
			Macro macro = (Macro) macros.get(i);
			// Modified for Intergeo File Format (Yves Kreis) -->
			// writeConstructionImages(macro.getMacroConstruction(), zip);
			writeConstructionImages(macro.getMacroConstruction(), jszip, filePath);
			// <-- Modified for Intergeo File Format (Yves Kreis)

			/*
			// save macro icon
			String fileName = macro.getIconFileName();
			BufferedImage img = ((Application)app).getExternalImage(fileName);
			if (img != null)
				// Modified for Intergeo File Format (Yves Kreis) -->
				// writeImageToZip(zip, fileName, img);
				writeImageToZip(jszip, filePath + fileName, img);
			// <-- Modified for Intergeo File Format (Yves Kreis)
			*/
		}
	}

	private void writeConstructionImages(JavaScriptObject jszip) {
		writeConstructionImages(getKernel().getConstruction(), jszip, "");
	}

    private void writeConstructionImages(Construction cons, JavaScriptObject jszip, String filePath) {
		// save all GeoImage images
		//TreeSet images = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null)
			return;

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo =  it.next();
			// Michael Borcherds 2007-12-10 this line put back (not needed now
			// MD5 code put in the correct place!)
			String fileName = geo.getImageFileName();
			if (fileName != null) {
				BufferedImage img = geogebra.web.awt.BufferedImage.getGawtImage(geo.getFillImage());
				if (img != null && img.getImageElement() != null) {
					Canvas cv = Canvas.createIfSupported();
					cv.setCoordinateSpaceWidth(img.getWidth());
					cv.setCoordinateSpaceHeight(img.getHeight());
					Context2d c2d = cv.getContext2d();
					c2d.drawImage(img.getImageElement(),0,0);
					writeImageToZip(jszip, filePath + fileName, cv.toDataUrl("image/png"));
				}
			}
		}
    }

    private native void writeImageToZip(JavaScriptObject jszip, String filename, String base64img) /*-{
    	var filename2 = filename.replace(/\\/g,"_");
    	// chop data part of PNG data url
    	jszip.add(filename, base64img.substring(22), {base64: true});
    }-*/;
}
