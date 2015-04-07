package org.freehep.graphicsio.pdf;

import java.text.SimpleDateFormat;

/**
 * Specifies constants for use with the PDFWriter, PDFStream and PDFUtil.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFConstants.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public interface PDFConstants {

    public final static String EOL = System.getProperty("line.separator");

    //
    // Constants for PDFStream
    //

    // Line Cap Styles (see Table 4.4)
    public static final int CAP_BUTT = 0;

    public static final int CAP_ROUND = 1;

    public static final int CAP_SQUARE = 2;

    // Line Join Styles (see Table 4.5)
    public static final int JOIN_MITTER = 0;

    public static final int JOIN_ROUND = 1;

    public static final int JOIN_BEVEL = 2;

    // Rendering Modes (see Table 5.3)
    public static final int MODE_FILL = 0;

    public static final int MODE_STROKE = 1;

    public static final int MODE_FILL_STROKE = 2;

    public static final int MODE_INVISIBLE = 3;

    public static final int MODE_FILL_CLIP = 4;

    public static final int MODE_STROKE_CLIP = 5;

    public static final int MODE_FILL_STROKE_CLIP = 6;

    public static final int MODE_CLIP = 7;

    // Date Format for PDF: (D:YYYYMMDDHHmmSSOHH'mm')
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");
}
