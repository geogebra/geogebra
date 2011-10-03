package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;

public class AlgoDependentInterval extends AlgoDependentFunction {


	AlgoDependentInterval(Construction cons, Function fun) {
        super(cons);
        this.fun = fun;
        f = new GeoInterval(cons);
        f.setFunction(fun);
        
  
        setInputOutput(); // for AlgoElement
        
        compute();
    }

	public AlgoDependentInterval(Construction cons, String label,
			Function fun) {
        this(cons, fun);
            
       	f.setLabel(label);
	}

	public String getClassName() {
        return "AlgoDependentInterval";
    }
    
    final public String toString() {
    	return f.toSymbolicString();
    }

}
