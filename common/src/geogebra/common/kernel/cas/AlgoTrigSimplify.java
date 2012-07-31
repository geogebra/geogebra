package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;

public class AlgoTrigSimplify extends AlgoCasBase {

	public AlgoTrigSimplify(Construction cons, String label,
			CasEvaluableFunction f) {
		super(cons, label, f);
		compute();    
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTrigSimplify;
	}
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		 g.setUsingCasCommand("TrigSimplify(%)", f, true,arbconst);
	}
}
