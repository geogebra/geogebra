// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.emf;

/**
 * EMF Constants
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFConstants.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public interface EMFConstants {

	public static final int UNITS_PER_PIXEL = 1;

	public static final int TWIPS = 20;

	public static final int GRADIENT_FILL_RECT_H = 0x00000000;

	public static final int GRADIENT_FILL_RECT_V = 0x00000001;

	public static final int GRADIENT_FILL_TRIANGLE = 0x00000002;

	public static final int SRCCOPY = 0x00CC0020;

	public static final int ICM_OFF = 1;

	public static final int ICM_ON = 2;

	public static final int ICM_QUERY = 3;

	public static final int ICM_DONE_OUTSIDEDC = 4;

	public static final int FW_DONTCARE = 0;

	public static final int FW_THIN = 100;

	public static final int FW_EXTRALIGHT = 200;

	public static final int FW_LIGHT = 300;

	public static final int FW_NORMAL = 400;

	public static final int FW_MEDIUM = 500;

	public static final int FW_SEMIBOLD = 600;

	public static final int FW_BOLD = 700;

	public static final int FW_EXTRABOLD = 800;

	public static final int FW_HEAVY = 900;

	public static final int PAN_ANY = 0;

	public static final int PAN_NO_FIT = 1;

	public static final int ETO_OPAQUE = 0x0002;

	public static final int ETO_CLIPPED = 0x0004;

	public static final int ETO_GLYPH_INDEX = 0x0010;

	public static final int ETO_RTLREADING = 0x0080;

	public static final int ETO_NUMERICSLOCAL = 0x0400;

	public static final int ETO_NUMERICSLATIN = 0x0800;

	public static final int ETO_IGNORELANGUAGE = 0x1000;

	public static final int ETO_PDY = 0x2000;

	public static final int GM_COMPATIBLE = 1;

	public static final int GM_ADVANCED = 2;

	public static final int FLOODFILLBORDER = 0;

	public static final int FLOODFILLSURFACE = 1;

	public static final int BLACKONWHITE = 1;

	public static final int WHITEONBLACK = 2;

	public static final int COLORONCOLOR = 3;

	public static final int HALFTONE = 4;

	public static final int STRETCH_ANDSCANS = BLACKONWHITE;

	public static final int STRETCH_ORSCANS = WHITEONBLACK;

	public static final int STRETCH_DELETESCANS = COLORONCOLOR;

	public static final int STRETCH_HALFTONE = HALFTONE;

	public static final int R2_BLACK = 1;

	public static final int R2_NOTMERGEPEN = 2;

	public static final int R2_MASKNOTPEN = 3;

	public static final int R2_NOTCOPYPEN = 4;

	public static final int R2_MASKPENNOT = 5;

	public static final int R2_NOT = 6;

	public static final int R2_XORPEN = 7;

	public static final int R2_NOTMASKPEN = 8;

	public static final int R2_MASKPEN = 9;

	public static final int R2_NOTXORPEN = 10;

	public static final int R2_NOP = 11;

	public static final int R2_MERGENOTPEN = 12;

	public static final int R2_COPYPEN = 13;

	public static final int R2_MERGEPENNOT = 14;

	public static final int R2_MERGEPEN = 15;

	public static final int R2_WHITE = 16;

	public static final int ALTERNATE = 1;

	public static final int WINDING = 2;

	public static final int TA_BASELINE = 24;

	public static final int TA_BOTTOM = 8;

	public static final int TA_TOP = 0;

	public static final int TA_CENTER = 6;

	public static final int TA_LEFT = 0;

	public static final int TA_RIGHT = 2;

	public static final int TA_NOUPDATECP = 0;

	public static final int TA_RTLREADING = 256;

	public static final int TA_UPDATECP = 1;

	public static final int MM_TEXT = 1;

	public static final int MM_LOMETRIC = 2;

	public static final int MM_HIMETRIC = 3;

	public static final int MM_LOENGLISH = 4;

	public static final int MM_HIENGLISH = 5;

	public static final int MM_TWIPS = 6;

	public static final int MM_ISOTROPIC = 7;

	public static final int MM_ANISOTROPIC = 8;

	public static final int AD_COUNTERCLOCKWISE = 1;

	public static final int AD_CLOCKWISE = 2;

	public static final int RGN_AND = 1;

	public static final int RGN_OR = 2;

	public static final int RGN_XOR = 3;

	public static final int RGN_DIFF = 4;

	public static final int RGN_COPY = 5;

	public static final int RGN_MIN = RGN_AND;

	public static final int RGN_MAX = RGN_COPY;

	public static final int BKG_TRANSPARENT = 1;

	public static final int BKG_OPAQUE = 2;

	public static final int PT_CLOSEFIGURE = 0x01;

	public static final int PT_LINETO = 0x02;

	public static final int PT_BEZIERTO = 0x04;

	public static final int PT_MOVETO = 0x06;

	public static final int MWT_IDENTITY = 1;

	public static final int MWT_LEFTMULTIPLY = 2;

	public static final int MWT_RIGHTMULTIPLY = 3;

	public static final int BI_RGB = 0;

	public static final int BI_RLE8 = 1;

	public static final int BI_RLE4 = 2;

	public static final int BI_BITFIELDS = 3;

	public static final int BI_JPEG = 4;

	public static final int BI_PNG = 5;

	public static final int BS_SOLID = 0;

	public static final int BS_NULL = 1;

	public static final int BS_HATCHED = 2;

	public static final int BS_PATTERN = 3;

	public static final int BS_INDEXED = 4;

	public static final int BS_DIBPATTERN = 5;

	public static final int BS_DIBPATTERNPT = 6;

	public static final int BS_PATTERN8X8 = 7;

	public static final int BS_DIBPATTERN8X8 = 8;

	public static final int BS_MONOPATTERN = 9;

	public static final int BS_HOLLOW = BS_NULL;

	public static final int DIB_RGB_COLORS = 0;

	public static final int DIB_PAL_COLORS = 1;

	public static final int HS_HORIZONTAL = 0; /* ----- */

	public static final int HS_VERTICAL = 1; /* ||||| */

	public static final int HS_FDIAGONAL = 2; /* \\\\\ */

	public static final int HS_BDIAGONAL = 3; /* ///// */

	public static final int HS_CROSS = 4; /* +++++ */

	public static final int HS_DIAGCROSS = 5; /* xxxxx */

	public static final int PS_GEOMETRIC = 0x00010000;

	public static final int PS_COSMETIC = 0x00000000;

	public static final int PS_SOLID = 0x00000000;

	public static final int PS_DASH = 0x00000001;

	public static final int PS_DOT = 0x00000002;

	public static final int PS_DASHDOT = 0x00000003;

	public static final int PS_DASHDOTDOT = 0x00000004;

	public static final int PS_NULL = 0x00000005;

	public static final int PS_INSIDEFRAME = 0x00000006;

	public static final int PS_USERSTYLE = 0x00000007;

	public static final int PS_ENDCAP_ROUND = 0x00000000;

	public static final int PS_ENDCAP_SQUARE = 0x00000100;

	public static final int PS_ENDCAP_FLAT = 0x00000200;

	public static final int PS_JOIN_ROUND = 0x00000000;

	public static final int PS_JOIN_BEVEL = 0x00001000;

	public static final int PS_JOIN_MITER = 0x00002000;

	public static final int AC_SRC_OVER = 0x00;

	public static final int AC_SRC_ALPHA = 0x01;
}
