/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDiameterLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDiameterLine extends AlgoElement {

    private GeoConic c; // input
    private GeoLine g; // input
    private GeoLine diameter; // output

    private GeoVector v;

    /** Creates new AlgoJoinPoints */
    public AlgoDiameterLine(Construction cons, String label, GeoConic c, GeoLine g) {
        super(cons);
        this.c = c;
        this.g = g;
        diameter = new GeoLine(cons);
        v = new GeoVector(cons);

        setInputOutput(); // for AlgoElement

        compute();
        diameter.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Diameter;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLAR_DIAMETER;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = c;

        super.setOutputLength(1);
        super.setOutput(0, diameter);
        setDependencies(); // done by AlgoElement
    }

    // Made public for LocusEqu
    public GeoLine getLine() {
        return g;
    }
    //Made public for LocusEqu
    public GeoConic getConic() {
        return c;
    }
    public GeoLine getDiameter() {
        return diameter;
    }

    // calc diameter line of v relativ to c
    @Override
	public final void compute() {
        g.getDirection(v);
        c.diameterLine(v, diameter);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("DiameterOfAConjugateToB",c.getLabel(tpl),g.getLabel(tpl));
    }

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnDiameterLine(geo, this, scope);
	}
}
