package org.freehep.graphicsio.pdf;

import java.io.IOException;
import java.util.Calendar;

/**
 * Implements a PDF Dictionary. All PDFObjects (including java Strings and
 * arrays) can be entered into the dictionary.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFDictionary.java,v 1.7 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFDictionary implements PDFConstants {

    private String open = null;

    protected PDFByteWriter out;

    private boolean ok;

    private PDFObject object;

    protected PDF pdf;

    PDFDictionary(PDF pdf, PDFByteWriter writer) throws IOException {
        this(pdf, writer, null);
    }

    PDFDictionary(PDF pdf, PDFByteWriter writer, PDFObject parent)
            throws IOException {
        this.pdf = pdf;
        object = parent;
        out = writer;
        out.println("<< ");
        out.indent();
        ok = true;
    }

    void close() throws IOException {
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        out.outdent();
        out.println(">>");
        if (object != null)
            object.close();
        ok = false;
    }

    public void entry(String key, String string) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " (" + PDFUtil.escape(string) + ")");
    }

    public void entry(String key, PDFName name) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " " + name);
    }

    public void entry(String key, int number) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " " + number);
    }

    public void entry(String key, double number) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " " + PDFUtil.fixedPrecision(number));
    }

    public void entry(String key, boolean bool) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " " + (bool ? "true" : "false"));
    }

    public void entry(String key, PDFRef ref) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " " + ref);
    }

    public void entry(String key, Calendar date) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.println("/" + key + " " + PDFUtil.date(date));
    }

    public void entry(String key, Object[] objs) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.print("/" + key + " [");
        for (int i = 0; i < objs.length; i++) {
            if (i != 0)
                out.printPlain(" ");
            out.printPlain(objs[i]);
        }
        out.printPlain("]");
        out.println();
    }

    public void entry(String key, int[] numbers) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.print("/" + key + " [");
        for (int i = 0; i < numbers.length; i++) {
            if (i != 0)
                out.printPlain(" ");
            out.printPlain(numbers[i]);
        }
        out.printPlain("]");
        out.println();
    }

    public void entry(String key, double[] numbers) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.print("/" + key + " [");
        for (int i = 0; i < numbers.length; i++) {
            if (i != 0)
                out.printPlain(" ");
            out.printPlain(PDFUtil.fixedPrecision(numbers[i]));
        }
        out.printPlain("]");
        out.println();
    }

    public void entry(String key, boolean[] bool) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        out.print("/" + key + " [");
        for (int i = 0; i < bool.length; i++) {
            if (i != 0)
                out.printPlain(" ");
            out.printPlain(bool[i] ? "true" : "false");
        }
        out.printPlain("]");
        out.println();
    }

    public PDFDictionary openDictionary(String name) throws IOException {
        if (!ok)
            System.err.println("PDFWriter error: 'PDFDictionary' was closed");
        if (open != null)
            System.err
                    .println("PDFWriter error: '" + open + "' was not closed");
        open = "PDFDictionary: " + name;
        out.println("/" + name);
        PDFDictionary dictionary = new PDFDictionary(pdf, out);
        return dictionary;
    }

    public void close(PDFDictionary dictionary) throws IOException {
        dictionary.close();
        open = null;
    }

}
