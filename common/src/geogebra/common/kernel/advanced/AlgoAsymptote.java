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

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAsymptote extends AlgoElement {

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

    public AlgoAsymptote(Construction cons, String[] labels, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(labels, asymptotes);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoAsymptote;
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
    @Override
	public void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        super.setOutput(asymptotes);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine[] getAsymptotes() {
        return asymptotes;
    }
    GeoConic getConic() {
        return c;
    }

    // calc asymptotes
    @Override
	public final void compute() {
        // only hyperbolas have asymptotes
        switch (c.type) {
            case GeoConicNDConstants.CONIC_HYPERBOLA :
                // direction0 =  a * eigenvec1 + b * eigenvec2
                // direction1 = -a * eigenvec1 + b * eigenvec2
                // lines through midpoint b

                double vec2x = halfAxes[1] * eigenvec[1].getX();
                double vec2y = halfAxes[1] * eigenvec[1].getY();
                double vec1x = halfAxes[0] * eigenvec[0].getX();
                double vec1y = halfAxes[0] * eigenvec[0].getY();

                asymptotes[0].x = - (vec2y + vec1y);
                asymptotes[0].y = vec2x + vec1x;
                asymptotes[0].z =
                    - (asymptotes[0].x * b.getX() + asymptotes[0].y * b.getY());

                asymptotes[1].x = - (vec2y - vec1y);
                asymptotes[1].y = vec2x - vec1x;
                asymptotes[1].z =
                    - (asymptotes[1].x * b.getX() + asymptotes[1].y * b.getY());

                // point on lines
                P.setCoords(b.getX(), b.getY(), 1.0);
                break;

            default :
                asymptotes[0].setUndefined();
                asymptotes[1].setUndefined();
        }
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("AsymptoteToA",c.getLabel(tpl));
    }

	// TODO Consider locusequability
}
