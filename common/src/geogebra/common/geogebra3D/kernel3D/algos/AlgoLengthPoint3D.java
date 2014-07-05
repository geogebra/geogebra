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
 */

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.MyMath;


/**
 * Distance origin to point 
 * @author  mathieu
 * @version 
 */
public class AlgoLengthPoint3D extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointND p; // input
    private GeoNumeric num; // output 
    
    public AlgoLengthPoint3D(Construction cons, String label, GeoPointND p) {
        super(cons);
        this.p = p;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Length;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = (GeoElement)p;

        setOnlyOutput(num);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getLength() {
        return num;
    }

    // calc length of vector v   
    @Override
	public final void compute() {
    	Coords coords = p.getInhomCoordsInD(3);
        num.setValue(MyMath.length(coords.getX(), coords.getY(), coords.getZ()));
    }

    @Override
	final public String toString(StringTemplate tpl) {
        return loc.getPlain("LengthOfA",((GeoElement)p).getLabel(tpl));
    }

	// TODO Consider locusequability
}
