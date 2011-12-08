/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDistanceLineLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoLine;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDistanceLineLine extends AlgoElement {

    private GeoLine g, h; // input
    private GeoNumeric dist; // output       

    public AlgoDistanceLineLine(
        Construction cons,
        String label,
        GeoLine g,
        GeoLine h) {
        super(cons);
        this.h = h;
        this.g = g;
        dist = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoDistanceLineLine";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_DISTANCE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = h;
        input[1] = g;

        super.setOutputLength(1);
        super.setOutput(0, dist);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getDistance() {
        return dist;
    }
    GeoLine getg() {
        return g;
    }
    GeoLine geth() {
        return h;
    }

    // calc length of vector v   
    @Override
	public final void compute() {
        dist.setValue(g.distance(h));
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DistanceOfAandB",g.getLabel(),h.getLabel());
    }
}
