package org.freehep.graphicsio.pdf;

import java.io.IOException;

/**
 * Implements a numbered PDFObject.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFObject.java,v 1.7 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFObject implements PDFConstants {

    protected PDF pdf;

    private PDFByteWriter out;

    private String open;

    private boolean ok;

    PDFObject(PDF pdf, PDFByteWriter writer, int objectNumber,
            int generationNumber) throws IOException {
        this.pdf = pdf;
        out = writer;
        out.println(objectNumber + " " + generationNumber + " obj");
        out.indent();
        ok = true;
    }

    void close() throws IOException {
        out.outdent();
        out.println("endobj");
        out.println();
        ok = false;
    }

    public void entry(int number) throws IOException {
        if (!ok)
            System.err.println("PDFWriter: 'PDFObject' was closed");
        out.println(number);
    }

    public void entry(Object[] objs) throws IOException {
        if (!ok)
            System.err.println("PDFWriter: 'PDFObject' was closed");
        out.print("[");
        for (int i = 0; i < objs.length; i++) {
            if (i != 0)
                out.printPlain(" ");
            out.printPlain(objs[i]);
        }
        out.printPlain("]");
        out.println();
    }

    // public void entry(String string) throws IOException {
    // if (!ok) System.err.println("PDFWriter: 'PDFObject' was closed");
    // out.println("("+PDFUtil.escape(string)+")");
    // }

    PDFDictionary openDictionary() throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFDictionary";
        PDFDictionary dictionary = new PDFDictionary(pdf, out, this);
        return dictionary;
    }

    void close(PDFDictionary dictionary) throws IOException {
        dictionary.close();
        open = null;
    }

    PDFStream openStream(String name, String[] encode) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFStream' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFStream";
        PDFStream stream = new PDFStream(pdf, out, name, this, encode);
        return stream;
    }

    void close(PDFStream stream) throws IOException {
        stream.close();
        open = null;
    }

    PDFDocInfo openDocInfo(PDF pdf) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDocInfo' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFDocInfo";
        PDFDocInfo info = new PDFDocInfo(pdf, out, this);
        return info;
    }

    PDFCatalog openCatalog(PDF pdf, PDFRef pageTree) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFCatalog' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFCatalog";
        PDFCatalog catalog = new PDFCatalog(pdf, out, this, pageTree);
        return catalog;
    }

    PDFPageTree openPageTree(PDF pdf, PDFRef parent) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFPageTree' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFPageTree";
        PDFPageTree tree = new PDFPageTree(pdf, out, this, parent);
        return tree;
    }

    PDFPage openPage(PDF pdf, PDFRef parent) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFPage' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFPage";
        PDFPage page = new PDFPage(pdf, out, this, parent);
        return page;
    }

    PDFViewerPreferences openViewerPreferences(PDF pdf) throws IOException {
        if (!ok)
            System.err
                    .println("PDFWriter error: 'PDFViewerPreferences' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFViewerPreferences";
        PDFViewerPreferences prefs = new PDFViewerPreferences(pdf, out, this);
        return prefs;
    }

    PDFOutlineList openOutlineList(PDF pdf, PDFRef first, PDFRef last)
            throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFOutlineList' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFOutlineList";
        PDFOutlineList list = new PDFOutlineList(pdf, out, this, first, last);
        return list;
    }

    PDFOutline openOutline(PDF pdf, PDFRef parent, String title, PDFRef prev,
            PDFRef next) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFOutline' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFOutline";
        PDFOutline outline = new PDFOutline(pdf, out, this, parent, title,
                prev, next);
        return outline;
    }

}
