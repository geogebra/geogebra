package org.geogebra.common.kernel.commands;

import java.util.ArrayList;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.cas.AlgoDependentSymbolic;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;

/**
 * Processor for symbolic elements
 * 
 * @author Zbynek
 */
public class SymbolicProcessor {
	private Kernel kernel;
	private Construction cons;

	private static final class RecursiveEquationFinder implements Inspecting {
		private final ExpressionValue ve;

		protected RecursiveEquationFinder(ExpressionValue ve) {
			this.ve = ve;
		}

		@Override
		public boolean check(ExpressionValue v) {
			return v instanceof GeoDummyVariable && ((GeoDummyVariable) v)
					.getVarName().equals(ve.wrap().getLabel());
		}
	}

	/**
	 * @param kernel
	 *            kernel
	 */
	public SymbolicProcessor(Kernel kernel) {
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
	}

	/**
	 * @param replaced
	 *            symbolic expression
	 * @return evaluated expression
	 */
	protected GeoElement doEvalSymbolicNoLabel(ExpressionNode replaced) {
		HashSet<GeoElement> vars = replaced
				.getVariables(SymbolicMode.SYMBOLIC_AV);
		ArrayList<GeoElement> noDummyVars = new ArrayList<>();
		if (vars != null) {
			for (GeoElement var : vars) {
				if (!(var instanceof GeoDummyVariable)) {
					noDummyVars.add(var);
				} else {
					cons.getCASdummies()
							.add(((GeoDummyVariable) var).getVarName());
				}
			}
		}
		GeoSymbolic sym;
		if (noDummyVars.size() > 0) {
			AlgoDependentSymbolic ads = new AlgoDependentSymbolic(cons,
					replaced, noDummyVars);
			sym = (GeoSymbolic) ads.getOutput(0);
		} else {
			sym = new GeoSymbolic(cons);
			if (replaced.unwrap() instanceof FunctionNVar) {
				sym.setVariables(((FunctionNVar) replaced.unwrap())
						.getFunctionVariables());
			}
			sym.setDefinition(replaced);
			sym.computeOutput();
		}
		return sym;
	}

	/**
	 * @param ve
	 *            input expression
	 * @return processed geo
	 */
	protected GeoElement evalSymbolicNoLabel(final ExpressionValue ve) {
		ve.resolveVariables(
				new EvalInfo(false).withSymbolicMode(SymbolicMode.SYMBOLIC_AV));
		if (ve.unwrap() instanceof Command
				&& "Sequence".equals(((Command) ve.unwrap()).getName())) {
			return doEvalSymbolicNoLabel(ve.wrap());
		}
		ExpressionNode replaced = ve.traverse(new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof Command && ev != ve.unwrap()) {
					return evalSymbolicNoLabel(ev);
				}
				if (ev instanceof GeoDummyVariable && ((GeoDummyVariable) ev)
						.getElementWithSameName() != null) {
					return ((GeoDummyVariable) ev).getElementWithSameName();
				}
				return ev;
			}
		}).wrap();
		if (replaced.inspect(new RecursiveEquationFinder(ve))) {
			replaced = new Equation(kernel,
					new GeoDummyVariable(cons, ve.wrap().getLabel()), replaced)
							.wrap();
		}

		return doEvalSymbolicNoLabel(replaced);
	}

}
