package org.freehep.graphicsio.pdf;

import java.io.IOException;

/**
 * Implements the Page Base Node to accomodate Inheritance of Page Attributes
 * (see Table 3.17)
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFPageBase.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public abstract class PDFPageBase extends PDFDictionary {

    protected PDFPageBase(PDF pdf, PDFByteWriter writer, PDFObject object,
            PDFRef parent) throws IOException {
        super(pdf, writer, object);
        entry("Parent", parent);
    }

    //
    // Inheritable items go here
    //
    public void setResources(String resources) throws IOException {
        entry("Resources", pdf.ref(resources));
    }

    public void setMediaBox(double x, double y, double w, double h)
            throws IOException {
        double[] rectangle = { x, y, w, h };
        entry("MediaBox", rectangle);
    }

    public void setCropBox(double x, double y, double w, double h)
            throws IOException {
        double[] rectangle = { x, y, w, h };
        entry("CropBox", rectangle);
    }

    public void setRotate(int rotate) throws IOException {
        entry("Rotate", rotate);
    }
}
