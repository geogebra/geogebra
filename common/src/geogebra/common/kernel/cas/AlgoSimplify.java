package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoCasBase;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.CasEvaluableFunction;

/**
 * Algorithm for Simplify(function)
 */
public class AlgoSimplify extends AlgoCasBase {
	/**
     * @param cons construction
     * @param label label for output
     * @param f function
     */
	public AlgoSimplify(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public Commands getClassName() {
        return Commands.Simplify;
    }
	
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		g.setUsingCasCommand("Simplify(%)", f, false,arbconst);		
	}

	// TODO Consider locusequability
	
}
