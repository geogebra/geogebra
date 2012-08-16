/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;




public class AlgoDistanceLines3D extends AlgoElement3D {

	private GeoLineND g3D, h3D;
	private String label;
	private Coords pointOnG, pointOnH;
	
	private GeoNumeric dist;

    public AlgoDistanceLines3D(Construction c,
    		String label, GeoLineND g3D, GeoLineND h3D) {
        super(c);
        this.g3D = g3D;
        this.h3D = h3D;
        dist = new GeoNumeric(cons);
        pointOnG = new Coords(3);
        pointOnH = new Coords(3);
        
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }
    

    @Override
	public Algos getClassName() {
        return Algos.AlgoDistanceLines3D;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement)h3D;
        input[1] = (GeoElement)g3D;

        super.setOutputLength(1);
        super.setOutput(0, dist);
        setDependencies(); // done by AlgoElement
    }
 
    GeoLineND getg() {
        return g3D;
    }
    GeoLineND geth() {
        return h3D;
    }
    Coords getPointOnG() {
        return pointOnG.copyVector();
    }
    Coords getPointOnH() {
        return pointOnH.copyVector();
    }
    public GeoNumeric getDistance() {
    	return dist;
    }
    
    // calc length of vector v   
    @Override
	public void compute() {
    	if (!g3D.isDefined() || !h3D.isDefined()) {
    		dist.setUndefined();
    		pointOnG.set(new double[]{Double.NaN,Double.NaN,Double.NaN});
    		pointOnH.set(new double[]{Double.NaN,Double.NaN,Double.NaN});
    		return;
    	}
    		
    	
        dist.setValue(g3D.distance(h3D));
        
        Coords a1, a2, b1, b2;
        double A1, B1, C1, A2, B2, C2; //A1t1+B1t2=C1, A2t1+B2t2=C2
        double det, t1, t2;
        a1 = g3D.getPointInD(3, 0);
        b1 = g3D.getDirectionInD3();
        a2 = h3D.getPointInD(3, 0);
        b2 = h3D.getDirectionInD3();
        A1 = b1.dotproduct(b1);
        B1 = -b1.dotproduct(b2);
        C1 = -b1.dotproduct(a1.sub(a2));
        A2 = B1;
        B2 = b2.dotproduct(b2);
        C2 = -b2.dotproduct(a2.sub(a1));
        det = det(A1,B1,A2,B2);
        if (!Kernel.isZero(det)) {
        	t1 = det(C1,B1,C2,B2)/det;
        	t2 = det(A1,C1,A2,C2)/det;
        	pointOnG = g3D.getPointInD(3, t1);
        	pointOnH = h3D.getPointInD(3, t2);
        } else {
        	t1 = 0;
        	t2 = -C1/Math.abs(B2);
        	pointOnG = g3D.getPointInD(3, t1);
        	pointOnH = h3D.getPointInD(3, t2);
        }
        
        
    }

    private static double det(double a11, double a12, double a21, double a22) {
    	return a11*a22-a12*a21;
    }

	


	

    
}
