/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRotateVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRotateVector extends AlgoElement {

    private GeoVector A; // input
    private GeoNumeric angle; // input
    private GeoVector B; // output        

    /**
     * Creates new algo for vector rotation
     * @param cons
     * @param label
     * @param A
     * @param angle
     */
    AlgoRotateVector(
        Construction cons,
        String label,
        GeoVector A,
        GeoNumeric angle) {
        super(cons);
        this.A = A;
        this.angle = angle;

        // create new Vector
        B = new GeoVector(cons);
        setInputOutput();

        compute();
        B.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoRotateVector;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = angle;

        setOutputLength(1);
        setOutput(0,B);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the input vector
     * @return input vector
     */
    GeoVector getVector() {
        return A;
    }
    
    /**
     * Returns the rotation angle
     * @return rotation angle
     */
    GeoNumeric getAngle() {
        return angle;
    }
    
    /**
     * Returns the resulting vector
     * @return resulting vector
     */
    GeoVector getRotatedVector() {
        return B;
    }

    // calc rotated Vector
    @Override
	public final void compute() {
        B.setCoords(A);
        B.rotate(angle);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("ARotatedByAngleB",A.getLabel(tpl),angle.getLabel(tpl));
    }

	// TODO Consider locusequability
}
