package org.freehep.graphicsio.pdf;

import java.io.IOException;
import java.io.OutputStream;

import org.freehep.util.io.CountedByteOutputStream;

/**
 * Implements the real writer for the PDFWriter. This class does byte-counting
 * to eventually build the cross-reference table, block length counting for the
 * length of streams, and platform dependent end-of-line characters.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFByteWriter.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFByteWriter extends CountedByteOutputStream implements
        PDFConstants {

    private int indent;

    private String indentString = "   ";

    PDFByteWriter(OutputStream out) {
        super(out);
        indent = 0;
    }

    public void write(String s) throws IOException {
        write(s.getBytes("ISO-8859-1"));
    }

    public void close() throws IOException {
        out.close();
        super.close();
    }

    public void print(String string) throws IOException {
        for (int i = 0; i < indent; i++) {
            write(indentString);
        }
        printPlain(string);
    }

    public void printPlain(String string) throws IOException {
        write(string);
    }

    public void println() throws IOException {
        write(EOL);
    }

    public void indent() {
        indent++;
    }

    public void outdent() {
        if (indent > 0) {
            indent--;
        }
    }

    // Convenience methods
    public void println(String string) throws IOException {
        print(string);
        println();
    }

    public void print(int number) throws IOException {
        print(Integer.toString(number));
    }

    public void println(int number) throws IOException {
        print(number);
        println();
    }

    public void printPlain(int number) throws IOException {
        printPlain(Integer.toString(number));
    }

    public void print(double number) throws IOException {
        print(Double.toString(number));
    }

    public void println(double number) throws IOException {
        print(number);
        println();
    }

    public void printPlain(double number) throws IOException {
        printPlain(Double.toString(number));
    }

    public void print(Object object) throws IOException {
        print(object.toString());
    }

    public void println(Object object) throws IOException {
        print(object);
        println();
    }

    public void printPlain(Object object) throws IOException {
        printPlain(object.toString());
    }

}