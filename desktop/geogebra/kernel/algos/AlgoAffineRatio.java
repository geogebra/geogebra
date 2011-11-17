package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;

/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * This class calculate affine ratio of 3 points: (A,B,C) = (t(C)-t(A)) : (t(C)-t(B))
 */

public class AlgoAffineRatio extends AlgoElement {

	private GeoPoint A, B, C; // input
    private GeoNumeric M; // output

    AlgoAffineRatio(Construction cons, String label, GeoPoint A, GeoPoint B, GeoPoint C) {
    	super(cons);
        this.A = A;
        this.B = B;
        this.C = C;
        // create new GeoNumeric Object to return the result
        M = new GeoNumeric(cons);
        setInputOutput();
        compute();        
        M.setLabel(label);
    }
 
    @Override
	public String getClassName() {
        return "AlgoAffineRatio";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = C;
        
        setOutputLength(1);
        setOutput(0,M);
        setDependencies(); // done by AlgoElement
    }
    
    GeoNumeric getResult() {
        return M;
    }

    @Override
	protected final void compute() {
        //Check if the points are aligned
        if (GeoPoint.collinear(A, B, C)){
        	if (B.isEqual(C)) {
        		M.setValue(1.0); // changed, was undefined
        	}else{ 
        		M.setValue(GeoPoint.affineRatio(A, B, C));
        	}
        }else{
        	M.setUndefined();
        }
    }
}