/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoDenominator extends AlgoNumerator {

    


	public AlgoDenominator(Construction cons, String label, GeoFunction f) {
		super(cons, label, f);
	}

	/*
     * over-rides AlgoNumerator
     */
	@Override
    protected ExpressionValue getPart(ExpressionNode node) {
    	return node.right;
    }

    public String getClassName() {
        return "AlgoDenominator";
    }
}
