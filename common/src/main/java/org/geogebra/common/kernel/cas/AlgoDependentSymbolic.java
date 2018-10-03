package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;

public class AlgoDependentSymbolic extends AlgoElement implements UsesCAS {

	private GeoSymbolic symbolic;
	private ArrayList<GeoElement> vars;

	public AlgoDependentSymbolic(Construction c, GeoSymbolic symbolic,
			ArrayList<GeoElement> vars) {
		super(c);
		this.symbolic = symbolic;
		this.vars = vars;
		setInputOutput();
	}

	@Override
	protected void setInputOutput() {
		input = vars.toArray(new GeoElement[1]);
		setOnlyOutput(symbolic);
	}

	@Override
	public void compute() {
		symbolic.computeOutput();
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

}
