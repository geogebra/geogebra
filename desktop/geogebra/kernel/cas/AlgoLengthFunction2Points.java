package geogebra.kernel.cas;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.roots.RealRootFunction;

/**
 * @author  Victor Franco Espino
 * @version 19-04-2007
 * 
 * Calculate Function Length between the points A and B: integral from A to B on T = sqrt(1+(f')^2)
 */

public class AlgoLengthFunction2Points extends AlgoUsingTempCASalgo {

	private static final long serialVersionUID = 1L;
	private GeoPoint A, B; //input
	private GeoFunction f, f1;//f1 is f'(x)
    private GeoNumeric length; //output
    private RealRootFunction lengthFunction; //is T = sqrt(1+(f')^2)
    	
	public AlgoLengthFunction2Points(Construction cons, String label, GeoFunction f, GeoPoint A, GeoPoint B) {
        super(cons);
        this.A = A;
        this.B = B;
        this.f = f;
        length = new GeoNumeric(cons);
     
        //First derivative of function f
        algoCAS = new AlgoDerivative(cons, f);
        cons.removeFromConstructionList(algoCAS);
        this.f1 = (GeoFunction) ((AlgoDerivative)algoCAS).getResult();        
    	lengthFunction = new LengthFunction();		
		
	    setInputOutput();
	    compute();
        length.setLabel(label); 
	}
	 
    public String getClassName() {
        return "AlgoLengthFunction2Points";
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
    	double a = A.inhomX;
    	double b = B.inhomX;

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