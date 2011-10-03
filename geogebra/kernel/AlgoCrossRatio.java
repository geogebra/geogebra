package geogebra.kernel;

/**
 * @author  Victor Franco Espino
 * @version 11-02-2007
 * 
 * This class calculate cross ratio of 4 points like the division of 2 affine ratio's:
 *         CrossRatio(A,B,C,D) = affineRatio(B, C, D) / affineRatio(A, C, D)
 */

public class AlgoCrossRatio extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A, B, C, D; // input
    private GeoNumeric M; // output
    
    AlgoCrossRatio(Construction cons, String label, GeoPoint A, GeoPoint B, GeoPoint C, GeoPoint D) {
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

    public String getClassName() {
        return "AlgoCrossRatio";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[4];
        input[0] = A;
        input[1] = B;
        input[2] = C;
        input[3] = D;

        output = new GeoElement[1];
        output[0] = M;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getResult() {
        return M;
    }

    protected final void compute() {
        //Check if the points are aligned
    	if ( !(A.isEqual(D)) && !(B.isEqual(C)) 
        	 && GeoPoint.collinear(B, C, D) && GeoPoint.collinear(A, C, D) ) {
        		M.setValue(GeoPoint.affineRatio(B, C, D) / GeoPoint.affineRatio(A, C, D));
        }else{
        	M.setUndefined();
        }
    }
}