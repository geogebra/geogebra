// Copyright 2000, CERN, Geneva, Switzerland and University of Santa Cruz, California, U.S.A.
package org.freehep.graphicsio;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines a set of constants which describe a page. Convenience
 * objects are provided for various margins, orientations, rescaling, and
 * standard page sizes.
 * 
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: PageConstants.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public class PageConstants {

	private PageConstants() {
	}

	// Orientation
	public static final String ORIENTATION = "Orientation";

	public static final String PORTRAIT = "Portrait";

	public static final String LANDSCAPE = "Landscape";

	public static final String BEST_FIT = "Best Fit";

	public final static String[] getOrientationList() {
		return new String[] { PORTRAIT, LANDSCAPE, /* BEST_FIT */ };
	}

	// Page Sizes
	public static final String PAGE_SIZE = "PageSize";

	public static final String INTERNATIONAL = "International";

	public static final String A3 = "A3";

	public static final String A4 = "A4";

	public static final String A5 = "A5";

	public static final String A6 = "A6";

	public static final String LETTER = "Letter";

	public static final String LEGAL = "Legal";

	public static final String EXECUTIVE = "Executive";

	public static final String LEDGER = "Ledger";

	public static final String[] getSizeList() {
		return new String[] { INTERNATIONAL, A4, LETTER, A3, LEGAL, A5, A6,
				EXECUTIVE, LEDGER };
	}

	public static final Dimension getSize(String size) {
		return getSize(size, PORTRAIT);
	}

	public static final Dimension getSize(String size, String orientation) {
		Dimension d = (Dimension) sizeTable.get(size);
		if (orientation.equals(PORTRAIT)) {
			return d;
		}
		return new Dimension(d.height, d.width);
	}

	private static final Map sizeTable = new HashMap();
	static {
		sizeTable.put(INTERNATIONAL, new Dimension(595, 791));
		sizeTable.put(A3, new Dimension(842, 1191));
		sizeTable.put(A4, new Dimension(595, 842));
		sizeTable.put(A5, new Dimension(420, 595));
		sizeTable.put(A6, new Dimension(298, 420));
		sizeTable.put(LETTER, new Dimension(612, 791));
		sizeTable.put(LEGAL, new Dimension(612, 1009));
		sizeTable.put(EXECUTIVE, new Dimension(539, 720));
		sizeTable.put(LEDGER, new Dimension(791, 1225));
	}

	// Margins
	public static final String PAGE_MARGINS = "PageMargins";

	public static final String SMALL = "Small";

	public static final String MEDIUM = "Medium";

	public static final String LARGE = "Large";

	private static final Map marginTable = new HashMap();
	static {
		marginTable.put(SMALL, new Insets(20, 20, 20, 20));
		marginTable.put(MEDIUM, new Insets(30, 30, 30, 30));
		marginTable.put(LARGE, new Insets(40, 40, 40, 40));
	}

	public static final Insets getMargins(String size) {
		return (Insets) marginTable.get(size);
	}

	public static final Insets getMargins(Insets insets, String orientation) {
		if (orientation.equals(PORTRAIT)) {
			return insets;
		}
		// turn page to right
		return new Insets(insets.left, insets.bottom, insets.right, insets.top);
	}

	// Fit
	public static final String FIT_TO_PAGE = "FitToPage";

	// FIXME: should move?
	public static final String TRANSPARENT = "Transparent";

	public static final String BACKGROUND = "Background";

	public static final String BACKGROUND_COLOR = "BackgroundColor";
}
