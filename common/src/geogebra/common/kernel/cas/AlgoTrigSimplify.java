package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoCasBase;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.CasEvaluableFunction;
/**
 * Algorithm for TrigSimplify 
 */
public class AlgoTrigSimplify extends AlgoCasBase {
	/**
	 * @param cons construction
	 * @param label label for output
	 * @param f function
	 */
	public AlgoTrigSimplify(Construction cons, String label,
			CasEvaluableFunction f) {
		super(cons, label, f);
		compute();    
	}

	
	@Override
	public Commands getClassName() {
    	return Commands.TrigSimplify;
    } 
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		 g.setUsingCasCommand("TrigSimplify(%)", f, true,arbconst);
	}
}
