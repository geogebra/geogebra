package geogebra.kernel.cas;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.roots.RealRootFunction;

/**
 * @author  Victor Franco Espino
 * @version 19-04-2007
 * 
 * Calculate Function Length between the numbers A and B: integral from A to B on T = sqrt(1+(f')^2)
 */

public class AlgoLengthFunction extends AlgoUsingTempCASalgo {

	private static final long serialVersionUID = 1L;
	private GeoNumeric A, B; //input
	private GeoFunction f, f1; //f1 is f'(x)
    private GeoNumeric length; //output
	private RealRootFunction lengthFunction; //is T = sqrt(1+(f')^2)
    	
	public AlgoLengthFunction(Construction cons, String label, GeoFunction f, GeoNumeric A, GeoNumeric B){
    	this(cons, f, A, B);
        length.setLabel(label); 	
    }
		
	public AlgoLengthFunction(Construction cons, GeoFunction f, GeoNumeric A, GeoNumeric B) {
        super(cons);
        this.A = A;
        this.B = B;
        this.f = f;
        length = new GeoNumeric(cons);
        
        //First derivative of function f
        algoCAS = new AlgoDerivative(cons, f);
        this.f1 = (GeoFunction) ((AlgoDerivative)algoCAS).getResult();               
                
        // Integral of length function        
    	lengthFunction = new LengthFunction();
        cons.removeFromConstructionList(algoCAS);
        
        setInputOutput();
        compute(); 
	}
	 
    public String getClassName() {
        return "AlgoLengthFunction";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = f;
        input[1] = A;
        input[2] = B;
        
        output = new GeoElement[1];
        output[0] = length;
        setDependencies(); // done by AlgoElement
    }
    
    public GeoNumeric getLength() {
        return length;
    }

    protected final void compute() {
    	double a = A.getValue();
    	double b = B.getValue();
    	
    	double lenVal = Math.abs(AlgoIntegralDefinite.numericIntegration(lengthFunction, a, b));
		length.setValue(lenVal);	
	}
    
    /**
	 * T = sqrt( 1 + f'(x)^2) 
	 */
	private class LengthFunction implements RealRootFunction {
		public double evaluate(double t) {
			double p = f1.evaluate(t);
			return Math.sqrt(1 + p*p);
		}
	}
}