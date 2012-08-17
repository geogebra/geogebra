package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * @author  Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 * Calculate Curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
 */
public class AlgoCurvatureCurve extends AlgoElement {

	private GeoPoint A; // input
	private GeoCurveCartesian f;
    private GeoNumeric K; //output
    
    public AlgoCurvatureCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}    	
    }
    
    public AlgoCurvatureCurve(Construction cons, GeoPoint A, GeoCurveCartesian f) {
        super(cons);
        this.f = f;
        this.A = A;
        K = new GeoNumeric(cons);             
		        
        setInputOutput();
        compute();
    }
 
    @Override
	public Algos getClassName() {
        return Algos.AlgoCurvatureCurve;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        input[1] = f;
       
        super.setOutputLength(1);
        super.setOutput(0, K);
        setDependencies(); // done by AlgoElement
    }
    
    public GeoNumeric getResult() {
        return K;
    }

    @Override
	public final void compute() {
    	if (f.isDefined()) {	    	
	    	double t = f.getClosestParameter(A, f.getMinParameter());	    		        
	        K.setValue( f.evaluateCurvature(t) );
    	} else {
    		K.setUndefined();
    	}
    }

	// TODO Consider locusequability   
}