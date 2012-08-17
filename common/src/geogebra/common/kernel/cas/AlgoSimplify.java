package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;

public class AlgoSimplify extends AlgoCasBase {

	public AlgoSimplify(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoSimplify;
	}
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		g.setUsingCasCommand("Simplify(%)", f, false,arbconst);		
	}

	// TODO Consider locusequability
	
}
