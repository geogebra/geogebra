package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
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
    private GeoConic gc=null;
    
    public AlgoCurvatureCurve(Construction cons, String label, GeoPoint A, GeoCurveCartesian f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}    	
    }
    
    public AlgoCurvatureCurve(Construction cons, String label, GeoPoint A, GeoConic f){
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
 
    public AlgoCurvatureCurve(Construction cons, GeoPoint A, GeoConic gc) {
        super(cons);
        this.gc=gc;
        this.A = A;
        K = new GeoNumeric(cons);             
		        
        setInputOutput();
        compute();
    }
    @Override
	public Commands getClassName() {
        return Commands.Curvature;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = A;
        if (gc!=null){
        	f=new GeoCurveCartesian(cons);
        	gc.toGeoCurveCartesian(f);
        	input[1] = gc;
        } else
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
    		if (gc!=null){
    			f=new GeoCurveCartesian(cons);
    			gc.toGeoCurveCartesian(f);  			
    		}
    		try{
    			double t = f.getClosestParameter(A, f.getMinParameter());	    		        
    			K.setValue( f.evaluateCurvature(t) );
    		}catch(Exception ex){
    			K.setUndefined();
    		}
    	} else {
    		K.setUndefined();
    	}
    }

	// TODO Consider locusequability   
}