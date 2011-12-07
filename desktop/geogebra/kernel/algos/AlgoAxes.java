/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAxes.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoVec2D;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAxes extends AlgoElement {

    private GeoConic c; // input
    private GeoLine[] axes; // output          

    private GeoVec2D[] eigenvec;
    private GeoVec2D b;
    private GeoPoint P;

    AlgoAxes(Construction cons, String label, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(label, axes,kernel.getGeoElementSpreadsheet());
    }

    public AlgoAxes(Construction cons, String[] labels, GeoConic c) {
        this(cons, c);
        GeoElement.setLabels(labels, axes,kernel.getGeoElementSpreadsheet());
    }

    @Override
	public String getClassName() {
        return "AlgoAxes";
    }

    private AlgoAxes(Construction cons, GeoConic c) {
        super(cons);
        this.c = c;

        eigenvec = c.eigenvec;
        b = c.b;

        axes = new GeoLine[2];
        axes[0] = new GeoLine(cons);
        axes[1] = new GeoLine(cons);

        P = new GeoPoint(cons);
        axes[0].setStartPoint(P);
        axes[1].setStartPoint(P);

        setInputOutput(); // for AlgoElement

        compute();
    }

    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;
        setOutputLength(2);
        setOutput(0,axes[0]);
        setOutput(1,axes[1]);        
        setDependencies(); // done by AlgoElement
    }

    public GeoLine[] getAxes() {
        return axes;
    }
    GeoConic getConic() {
        return c;
    }

    // calc axes
    @Override
	public final void compute() {
        // axes are lines with directions of eigenvectors
        // through midpoint b        

        axes[0].x = -eigenvec[0].y;
        axes[0].y = eigenvec[0].x;
        axes[0].z = - (axes[0].x * b.x + axes[0].y * b.y);

        axes[1].x = -eigenvec[1].y;
        axes[1].y = eigenvec[1].x;
        axes[1].z = - (axes[1].x * b.x + axes[1].y * b.y);

        P.setCoords(b.x, b.y, 1.0);
    }

    @Override
	public final String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("AxisOfA",c.getLabel());
    }
}
