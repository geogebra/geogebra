package geogebra.web.main;

import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.JavaScriptAPI;

public class GgbAPI  extends geogebra.common.plugin.GgbAPI implements JavaScriptAPI {

	public GgbAPI(Application app) {
		this.app = app;
	}

	
    public byte[] getGGBfile() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
    public String getBase64(boolean includeThumbnail) {
	    // TODO Auto-generated method stub
	    return null;
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
	    // TODO Auto-generated method stub
	    
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
	    // TODO Auto-generated method stub
	    
    }

	
    public String getBase64() {
	    // TODO Auto-generated method stub
	    return null;
    }


}
