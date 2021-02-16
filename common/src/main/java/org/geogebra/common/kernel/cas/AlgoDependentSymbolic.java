package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;

/**
 * Algo for updating GeoSymbolic when dependencies change
 * 
 * @author Zbynek
 *
 */
public class AlgoDependentSymbolic extends AlgoElement implements UsesCAS {

	private GeoSymbolic symbolic;
	private ArrayList<GeoElement> vars;

	/**
	 * @param c
	 *            construction
	 * @param def
	 *            symbolic variable definition
	 * @param vars
	 *            parent variables
	 */
	public AlgoDependentSymbolic(Construction c, ExpressionNode def,
			ArrayList<GeoElement> vars, MyArbitraryConstant constant) {
		super(c, def.isRootNode());
		this.symbolic = new GeoSymbolic(cons);
		symbolic.setArbitraryConstant(constant);
		symbolic.setDefinition(def);
		this.vars = vars;
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = vars.toArray(new GeoElement[1]);
		setOnlyOutput(symbolic);
		setDependencies();
	}

	@Override
	public void compute() {
		symbolic.computeOutput();
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return symbolic.getDefinition().toString(tpl);
	}

}
