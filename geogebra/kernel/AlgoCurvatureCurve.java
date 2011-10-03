package geogebra.kernel;

/**
 * @author  Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 * Calculate Curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
 */
public class AlgoCurvatureCurve extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A; // input
	private GeoCurveCartesian f;
    private GeoNumeric K; //output
    
    AlgoCurvatureCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}    	
    }
    
    AlgoCurvatureCurve(Construction cons, GeoPoint A, GeoCurveCartesian f) {
        super(cons);
        this.f = f;
        this.A = A;
        K = new GeoNumeric(cons);             
		        
        setInputOutput();
        compute();
    }
 
    public String getClassName() {
        return "AlgoCurvatureCurve";
    }

    // for AlgoElement
    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        input[1] = f;
       
        output = new GeoElement[1];
        output[0] = K;
        setDependencies(); // done by AlgoElement
    }
    
    GeoNumeric getResult() {
        return K;
    }

    protected final void compute() {
    	if (f.isDefined()) {	    	
	    	double t = f.getClosestParameter(A, f.getMinParameter());	    		        
	        K.setValue( f.evaluateCurvature(t) );
    	} else {
    		K.setUndefined();
    	}
    }   
}