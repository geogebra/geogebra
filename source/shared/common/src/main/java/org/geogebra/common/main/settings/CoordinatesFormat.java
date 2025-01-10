package org.geogebra.common.main.settings;

/**
 * Text format of points and vectors.
 */
public class CoordinatesFormat {
    /** A = (3, 2) and B = (3; 90^o) */
    public static final int COORD_FORMAT_DEFAULT = 0;
    /** A(3|2) and B(3; 90^o) */
    public static final int COORD_FORMAT_AUSTRIAN = 1;
    /** A: (3, 2) and B: (3; 90^o) */
    public static final int COORD_FORMAT_FRENCH = 2;
}
