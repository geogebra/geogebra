package org.freehep.graphicsio.pdf;

/**
 * Specifies a PDFName object.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFName.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFName implements PDFConstants {

    private String name;

    PDFName(String name) {
        this.name = name;
    }

    public String toString() {
        return "/" + name;
    }
}