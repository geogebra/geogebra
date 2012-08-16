/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDiameterLineVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDiameterVector extends AlgoElement {

    private GeoConic c; // input
    private GeoVector v; // input
    private GeoLine diameter; // output

    /** Creates new AlgoDiameterVector */
    public AlgoDiameterVector(
        Construction cons,
        String label,
        GeoConic c,
        GeoVector v) {
        super(cons);
        this.v = v;
        this.c = c;
        diameter = new GeoLine(cons);

        setInputOutput(); // for AlgoElement

        compute();
        diameter.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoDiameterVector;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;
        input[1] = c;

        super.setOutputLength(1);
        super.setOutput(0, diameter);
        setDependencies(); // done by AlgoElement
    }

    GeoVector getVector() {
        return v;
    }
    GeoConic getConic() {
        return c;
    }
    public GeoLine getDiameter() {
        return diameter;
    }

    // calc diameter line of v relativ to c
    @Override
	public final void compute() {
        c.diameterLine(v, diameter);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DiameterOfAConjugateToB",c.getLabel(tpl),v.getLabel(tpl));
    }

	// TODO Consider locusequability
}
