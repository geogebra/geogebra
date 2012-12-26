package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * @author  Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 * Calculate Curvature for function:
 */

public class AlgoCurvature extends AlgoElement {

	private GeoPoint A; // input
	private GeoFunction f;
    private GeoNumeric K; //output
    
    public AlgoCurvature(Construction cons, String label, GeoPoint A, GeoFunction f){
    	this(cons, A, f);
    	
    	if (label != null) {
    	    K.setLabel(label);
    	}else{
    		// if we don't have a label we could try k
    	    K.setLabel("k"); 
    	}    	
    }
    
    public AlgoCurvature(Construction cons, GeoPoint A, GeoFunction f) {
        super(cons);
        this.f = f;
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
    	if (f.isDefined())
    		K.setValue( f.evaluateCurvature(A.inhomX) );
    	else     	
    		K.setUndefined();    	
    }   
    
	@Override
	public void remove() {
		if(removed)
			return;
    	super.remove();  
    }

	// TODO Consider locusequability

}