package org.geogebra.common.util.clipper;

import org.geogebra.common.util.clipper.Point.DoublePoint;

public interface Clipper {
    public enum ClipType {
        INTERSECTION, UNION, DIFFERENCE, XOR
    }

    enum Direction {
        RIGHT_TO_LEFT, LEFT_TO_RIGHT
    };

    public enum EndType {
        CLOSED_POLYGON, CLOSED_LINE, OPEN_BUTT, OPEN_SQUARE, OPEN_ROUND
    };

    public enum JoinType {
        SQUARE, ROUND, MITER
    };

    public enum PolyFillType {
        EVEN_ODD, NON_ZERO, POSITIVE, NEGATIVE
    };

    public enum PolyType {
        SUBJECT, CLIP
    };

	/**
	 * modified to be compatible with double
	 */
    public interface ZFillCallback {
        void zFill( DoublePoint bot1, DoublePoint top1, DoublePoint bot2, DoublePoint top2, DoublePoint pt );
    };

    //InitOptions that can be passed to the constructor ...
    public final static int REVERSE_SOLUTION = 1;

    public final static int STRICTLY_SIMPLE = 2;

    public final static int PRESERVE_COLINEAR = 4;

    boolean addPath( Path pg, PolyType polyType, boolean Closed );

    boolean addPaths( Paths ppg, PolyType polyType, boolean closed );

    void clear();

    boolean execute( ClipType clipType, Paths solution );

    boolean execute( ClipType clipType, Paths solution, PolyFillType subjFillType, PolyFillType clipFillType );

    boolean execute( ClipType clipType, PolyTree polytree );

    public boolean execute( ClipType clipType, PolyTree polytree, PolyFillType subjFillType, PolyFillType clipFillType );
}
