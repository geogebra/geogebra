package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.geos.GeoInterval;

/**
 * Algorithm for dependent intervals, eg a<x<a+1
 *
 */
public class AlgoDependentInterval extends AlgoDependentFunction implements AlgoDependent {


	/**
	 * @param cons construction
	 * @param fun input interval
	 */
	AlgoDependentInterval(Construction cons, Function fun) {
        super(cons);
        this.fun = fun;
        f = new GeoInterval(cons);
        f.setFunction(fun);
        
  
        setInputOutput(); // for AlgoElement
        
        compute();
    }

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param fun input interval
	 */
	public AlgoDependentInterval(Construction cons, String label,
			Function fun) {
        this(cons, fun);
            
       	f.setLabel(label);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoDependentInterval;
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
    	return f.toSymbolicString(tpl);
    }

}
