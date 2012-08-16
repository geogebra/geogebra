/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLengthVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Length of an arc.
 * @author  michael
 */
public class AlgoArcLength extends AlgoElement {

    private GeoConicPart arc; // input
    private GeoNumeric num; // output 
    
    /**
     * @param cons cons
     * @param label label
     * @param arc partial conic
     */
    public AlgoArcLength(Construction cons, String label, GeoConicPart arc) {
        super(cons);
        this.arc = arc;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoLengthSegment;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = arc;

        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return arc length
     */
    public GeoNumeric getArcLength() {
        return num;
    }
    

    // calc length of vector v   
    @Override
	public final void compute() {
    	
        num.setValue(arc.getArcLength());
    }

    @Override
	final public String toString(StringTemplate tpl) {
        return app.getPlain("ArcLengthOfA",((GeoElement) arc).getLabel(tpl));

    }

	// TODO Consider locusequability
}
