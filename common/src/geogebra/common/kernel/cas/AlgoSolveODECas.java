package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.CasEvaluableFunction;

public class AlgoSolveODECas extends AlgoCasBase {

	public AlgoSolveODECas(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoSolveODECas;
	}

	@Override
	protected void applyCasCommand() {
		g.setUsingCasCommand("SolveODE(%)", f, false);		
	}
	
}
