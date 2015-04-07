package org.freehep.graphicsio.pdf;

import java.io.IOException;

/**
 * Implements the Outline Item Dictionary (see Table 7.4).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFOutline.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFOutline extends PDFDictionary {

    PDFOutline(PDF pdf, PDFByteWriter writer, PDFObject object, PDFRef parent,
            String title, PDFRef prev, PDFRef next) throws IOException {
        super(pdf, writer, object);
        entry("Parent", parent);
        entry("Title", title);
        entry("Prev", prev);
        entry("Next", next);
    }

    public void setFirst(String first) throws IOException {
        entry("First", pdf.ref(first));
    }

    public void setLast(String last) throws IOException {
        entry("Last", pdf.ref(last));
    }

    public void setCount(int count) throws IOException {
        entry("Count", count);
    }

    public void setDest(PDFName dest) throws IOException {
        entry("Dest", dest);
    }

    public void setDest(String dest) throws IOException {
        entry("Dest", dest);
    }

    public void setDest(Object[] dest) throws IOException {
        entry("Dest", dest);
    }

    public void setA(String a) throws IOException {
        entry("A", pdf.ref(a));
    }

    public void setSE(String se) throws IOException {
        entry("SE", pdf.ref(se));
    }
}
