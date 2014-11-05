package geogebra.web.gui;

import geogebra.web.gui.images.PerspectiveResources;
import geogebra.web.gui.images.PngPerspectiveResources;
import geogebra.web.gui.toolbar.images.MyIconResourceBundle;
import geogebra.web.gui.toolbar.images.ToolbarResources;

import com.google.gwt.core.shared.GWT;

public class PNGImageFactory implements ImageFactory{
	private static ToolbarResources tb;
    public ToolbarResources getToolbarResources() {
    	if(tb == null){
    		tb =  GWT.create(MyIconResourceBundle.class);
    	}
    	return tb;
    }
    
    private static PerspectiveResources pr;
    public PerspectiveResources getPerspectiveResources() {
    	if(pr == null){
    		pr = GWT.create(PngPerspectiveResources.class);
    	}
	    return pr;
	    
    }

}
