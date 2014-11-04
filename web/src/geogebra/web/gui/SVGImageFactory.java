package geogebra.web.gui;

import geogebra.web.gui.images.PerspectiveResources;
import geogebra.web.gui.images.SvgPerspectiveResources;
import geogebra.web.gui.toolbar.images.ToolbarResources;
import geogebra.web.gui.toolbar.svgimages.SvgToolbarResources;

import com.google.gwt.core.shared.GWT;

public class SVGImageFactory implements ImageFactory {
	private static ToolbarResources tb;
    public ToolbarResources getToolbarResources() {
    	if(tb == null){
    		tb = GWT.create(SvgToolbarResources.class);
    	}
	    return tb;
	    
    }
    
    private static PerspectiveResources pr;
    public PerspectiveResources getPerspectiveResources() {
    	if(pr == null){
    		pr = GWT.create(SvgPerspectiveResources.class);
    	}
	    return pr;
	    
    }
}
