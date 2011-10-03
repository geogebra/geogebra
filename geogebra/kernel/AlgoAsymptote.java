/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAsymptote.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAsymptote extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoLine[] asymptotes; // output          

    private GeoVec2D[] eigenvec;
    private double[] halfAxes;
    private GeoVec2D b;
    private GeoPoint P; // point on asymptotes = b

    /** Creates new AlgoJoinPoints */
    AlgoAsymptote(Construction cons, String label, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(label, asymptotes);
    }

    AlgoAsymptote(Construction cons, String[] labels, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(labels, asymptotes);
    }

    public String getClassName() {
        return "AlgoAsymptote";
    }

    private AlgoAsymptote(Construction cons, GeoConic c) {
        super(cons);
        this.c = c;

        eigenvec = c.eigenvec;
        halfAxes = c.halfAxes;
        b = c.b;

        asymptotes = new GeoLine[2];
        asymptotes[0] = new GeoLine(cons);
        asymptotes[1] = new GeoLine(cons);

        P = new GeoPoint(cons);
        asymptotes[0].setStartPoint(P);
        asymptotes[1].setStartPoint(P);

        setInputOutput(); // for AlgoElement

        compute();
    }

    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = asymptotes;
        setDependencies(); // done by AlgoElement
    }

    GeoLine[] getAsymptotes() {
        return asymptotes;
    }
    GeoConic getConic() {
        return c;
    }

    // calc asymptotes
    protected final void compute() {
        // only hyperbolas have asymptotes
        switch (c.type) {
            case GeoConic.CONIC_HYPERBOLA :
                // direction0 =  a * eigenvec1 + b * eigenvec2
                // direction1 = -a * eigenvec1 + b * eigenvec2
                // lines through midpoint b

                double vec2x = halfAxes[1] * eigenvec[1].x;
                double vec2y = halfAxes[1] * eigenvec[1].y;
                double vec1x = halfAxes[0] * eigenvec[0].x;
                double vec1y = halfAxes[0] * eigenvec[0].y;

                asymptotes[0].x = - (vec2y + vec1y);
                asymptotes[0].y = vec2x + vec1x;
                asymptotes[0].z =
                    - (asymptotes[0].x * b.x + asymptotes[0].y * b.y);

                asymptotes[1].x = - (vec2y - vec1y);
                asymptotes[1].y = vec2x - vec1x;
                asymptotes[1].z =
                    - (asymptotes[1].x * b.x + asymptotes[1].y * b.y);

                // point on lines
                P.setCoords(b.x, b.y, 1.0);
                break;

            default :
                asymptotes[0].setUndefined();
                asymptotes[1].setUndefined();
        }
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("AsymptoteToA",c.getLabel());
    }
}
