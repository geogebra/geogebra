package geogebra.web.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

import geogebra.common.awt.BufferedImageAdapter;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.euclidian.EuclidianViewInterface2D;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.common.main.settings.Settings;
import geogebra.common.util.ResourceBundleAdapter;
import geogebra.web.euclidian.EuclidianController;
import geogebra.web.euclidian.EuclidianView;
import geogebra.web.io.ConstructionException;
import geogebra.web.io.MyXMLio;
import geogebra.web.kernel.Kernel;
import geogebra.web.util.DataUtil;


public class Application extends AbstractApplication {
	
	private EuclidianView euclidianview;
	private EuclidianController euclidiancontroller;
	
	MyXMLio myXMLio;
	
	private boolean[] showAxes = {true,true};
	private boolean showGrid = false;
	
	private Map<String, ImageElement> images = new HashMap<String, ImageElement>();
	

	@Override
	public String getCommand(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3, String param4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlain(String cmdName, String param, String param2,
	        String param3, String param4, String param5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMenu(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getError(String cmdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRightToLeftReadingOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTooltipFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearTooltipFlag() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void storeUndoInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUsingFullGui() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean showView(int view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void callJavaScript(String jsFunction, Object[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUsingLocalizedLabels() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean languageIs(String s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean letRedefine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String translationFix(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void traceToSpreadsheet(GeoElement o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetTraceColumn(GeoElement o) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReverseNameDescriptionLanguage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBlockUpdateScripts() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBlockUpdateScripts(boolean flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getScriptingLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScriptingLanguage(String lang) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInternalCommand(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showError(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isScriptingDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean useBrowserForJavaScript() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initJavaScriptViewWithoutJavascript() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getTraceXML(GeoElement geoElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSelectedGeo(GeoElement geoElement, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeLayer(GeoElement ge, int layer, int layer2) {
		// TODO Auto-generated method stub

	}

	public boolean freeMemoryIsCritical() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long freeMemory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLabelingStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getOrdinalNumber(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getXmin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getXmax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getXminForFunctions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getXmaxForFunctions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double countPixels(double min, double max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxLayerUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getAlgebraView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EuclidianViewInterfaceSlim getEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EuclidianViewInterfaceSlim getActiveEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EuclidianViewInterface2D createEuclidianViewForPlane(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRightToLeftDigits() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShowingEuclidianView2() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AbstractKernel getKernel() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String translateCommand(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void evalScript(AbstractApplication app, String script, String arg) {
		// TODO Auto-generated method stub

	}

	public void init(Canvas canvas) {
		kernel = new Kernel(this);
		
		euclidiancontroller = new EuclidianController((Kernel)kernel);
		euclidianview = new EuclidianView(canvas, euclidiancontroller, showAxes, showGrid);
		
		myXMLio = new MyXMLio((Kernel) kernel, ((Kernel)kernel).getConstruction());
	    // TODO Auto-generated method stub
	    
    }
	
	public void loadGgbFile(Map<String, String> archiveContent) throws Exception {
		euclidianview.setDisableRepaint(true);
		loadFile(archiveContent);
		euclidianview.setDisableRepaint(false);
		euclidianview.repaintView();
	}

	public static void log(String message) {
	   GWT.log(message);
    }
	
	private void loadFile(Map<String, String> archive) throws Exception {
		// Reset file
		images.clear();
		
		// Handling of construction and macro file
		String construction = archive.remove("geogebra.xml");
		String macros = archive.remove("geogebra_macro.xml");
		
		// Construction (required)
		if (construction == null) {
			throw new ConstructionException("File is corrupt: No GeoGebra data found");
		}
		
		// Macros (optional)
		if (macros != null) {
			macros = DataUtil.utf8Decode(macros);
			//tmpaddMacroXML(macros);
		}	
		
		/* This code is buggy, maybe because of GWT versions?
		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
			//tmpmaybeProcessImage(entry.getKey(), entry.getValue());
				GWT.log(entry.getKey()+" "+entry.getValue());
			}
		}
		*/
		ArrayList<String> keys = new ArrayList<String>(archive.keySet());
		for (String key : keys) {
			//GWT.log(key+" :  "+archive.remove(key));
			maybeProcessImage(key,archive.remove(key));
		}
		
		// Process Construction
		construction = DataUtil.utf8Decode(construction);
		myXMLio.processXmlString(construction, true, false);
	}
	
	private static final ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
	static {
		IMAGE_EXTENSIONS.add("bmp");
		IMAGE_EXTENSIONS.add("gif");
		IMAGE_EXTENSIONS.add("jpg");
		IMAGE_EXTENSIONS.add("png");
	}
	private void maybeProcessImage(String filename, String binaryContent) {
		String fn = filename.toLowerCase();
		if (fn.equals("geogebra_thumbnail.png")) {
			return;			// Ignore thumbnail
		}
		
		int index = fn.lastIndexOf('.');
		if (index == -1) {
			return;			// Ignore files without extension
		}
		
		String ext = fn.substring(index + 1);
		if (! IMAGE_EXTENSIONS.contains(ext)) {
			return;			// Ignore non image files
		}
		
		String base64 = DataUtil.base64Encode(binaryContent);
		images.put(filename, createImage(ext, base64));
	}
	
	private ImageElement createImage(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		ImageElement image = Document.get().createImageElement();
		image.setSrc(dataUrl);
		
		return image;
	}


	@Override
    public int getMode() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void updateConstructionProtocol() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public int getCurrentLabelingStyle() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public Object getImageManager() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String reverseGetColor(String colorName) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getColor(String string) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public BufferedImageAdapter getExternalImageAdapter(String filename) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String getCommandSyntax(String cmd) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void showRelation(GeoElement geoElement, GeoElement geoElement2) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showError(MyError e) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showError(String string, String str) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public View getView(int id) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Settings getSettings() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void setScriptingDisabled(boolean scriptingDisabled) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public DrawEquationInterface getDrawEquation() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void setShowConstructionProtocolNavigation(boolean show,
            boolean playButton, double playDelay, boolean showProtButton) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public double getWidth() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public double getHeight() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public Font getFontCanDisplay(String labelDesc, boolean serif, int style,
            int size) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void updateSelection() {
	    // TODO Auto-generated method stub
	    
    }
}