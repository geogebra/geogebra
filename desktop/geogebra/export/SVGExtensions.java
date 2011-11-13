package geogebra.export;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

/**
 * adds support for grouping objects in SVG files
 * 
 * needs this line changed in SVGGraphics2D.java (was private)
 *     protected PrintWriter os;

 * @author Michael Borcherds
 */

public class SVGExtensions extends org.freehep.graphicsio.svg.SVGGraphics2D {
	
    public SVGExtensions(File file, Dimension size) throws IOException {
        super(file,size);
    }
    
     public void startGroup(String s) {
        os.println("<g id=\"" + s + "\">");
    }

    public void endGroup(String s)  {
        os.println("</g><!-- " + s + " -->");
    }
    

}