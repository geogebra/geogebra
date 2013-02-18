/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDistancePointLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDistancePointObject extends AlgoElement {

    private GeoPointND P; // input
    private GeoElement g; // input
    private GeoNumeric dist; // output       
    private AlgoClosestPoint closePt;
    public AlgoDistancePointObject(
        Construction cons,
        String label,
        GeoPointND P,
        GeoElement g) {
        super(cons);
        this.P = P;
        this.g = g;
        dist = new GeoNumeric(cons);
        closePt = getKernel().getAlgoDispatcher().getNewAlgoClosestPoint(cons, (Path)g, P);
        cons.removeFromConstructionList(closePt);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Distance;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_DISTANCE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) P;
        input[1] = g;

        setOutputLength(1);
        setOutput(0,dist);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getDistance() {
        return dist;
    }
    GeoPointND getP() {
        return P;
    }
    GeoElement getg() {
        return g;
    }

    // calc length of vector v   
    @Override
	public final void compute() {
    	if(closePt!=null)
    		dist.setValue(closePt.getP().distance(P));
    	else
    		dist.setValue(g.distance(P));
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("DistanceOfAandB",P.getLabel(tpl),g.getLabel(tpl));
    }

	// TODO Consider locusequability
}
