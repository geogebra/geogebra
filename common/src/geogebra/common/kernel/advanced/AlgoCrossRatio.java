package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * This class calculate cross ratio of 4 points like the division of 2 affine ratio's:
 *         CrossRatio(A,B,C,D) = affineRatio(B, C, D) / affineRatio(A, C, D)
 */

public class AlgoCrossRatio extends AlgoElement {

	private GeoPoint A, B, C, D; // input
    private GeoNumeric M; // output
    
    public AlgoCrossRatio(Construction cons, String label, GeoPoint A, GeoPoint B, GeoPoint C, GeoPoint D) {
    	super(cons);
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        M = new GeoNumeric(cons);
        setInputOutput();
        compute();   
        M.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.CrossRatio;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[4];
        input[0] = A;
        input[1] = B;
        input[2] = C;
        input[3] = D;

        super.setOutputLength(1);
        super.setOutput(0, M);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return M;
    }

    @Override
	public final void compute() {
        //Check if the points are aligned
    	if ( !(A.isEqual(D)) && !(B.isEqual(C)) 
        	 && GeoPoint.collinear(B, C, D) && GeoPoint.collinear(A, C, D) ) {
        		M.setValue(GeoPoint.affineRatio(B, C, D) / GeoPoint.affineRatio(A, C, D));
        }else{
        	M.setUndefined();
        }
    }

	// TODO Consider locusequability
}