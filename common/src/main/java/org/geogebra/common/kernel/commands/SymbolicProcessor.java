package org.geogebra.common.kernel.commands;

import java.util.ArrayList;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.cas.AlgoDependentSymbolic;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Processor for symbolic elements
 *
 * @author Zbynek
 */
public class SymbolicProcessor {
	private Kernel kernel;
	private Construction cons;

	/**
	 * Detect assignments of the type a=f(a) so that we can treat them as
	 * equations
	 */
	private static final class RecursiveEquationFinder implements Inspecting {
		private final ExpressionValue ve;

		protected RecursiveEquationFinder(ExpressionValue ve) {
			this.ve = ve;
		}

		@Override
		public boolean check(ExpressionValue v) {
			String label = ve.wrap().getLabel();
			if (v instanceof GeoSymbolic && label != null) {
				return ((GeoSymbolic) v).getValue().inspect(this);
			}
			if (v instanceof Variable) {
				return ((Variable) v).getName().equals(label);
			}
			return v instanceof GeoDummyVariable && ((GeoDummyVariable) v)
					.getVarName().equals(label);
		}
	}

	private static final class SubExpressionEvaluator implements Traversing {
		private ExpressionValue root;
		private SymbolicProcessor processor;

		public SubExpressionEvaluator(SymbolicProcessor symbolicProcessor,
				ExpressionValue root) {
			this.processor = symbolicProcessor;
			this.root = root;
		}

		@Override
		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Command && ev != root.unwrap()) {
				return processor.evalSymbolicNoLabel(ev);
			}
			if (ev instanceof GeoDummyVariable && ((GeoDummyVariable) ev)
					.getElementWithSameName() != null) {
				return ((GeoDummyVariable) ev).getElementWithSameName();
			}
			return ev;
		}
	}

	/**
	 * @param kernel kernel
	 */
	public SymbolicProcessor(Kernel kernel) {
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
	}

	/**
	 * @param replaced symbolic expression
	 * @return evaluated expression
	 */
	protected GeoElement doEvalSymbolicNoLabel(ExpressionNode replaced) {
		ExpressionValue expressionValue = replaced.unwrap();
		Command cmd;
		CommandDispatcher cmdDispatcher = kernel.getAlgebraProcessor().cmdDispatcher;

		try {
			if (expressionValue instanceof Command) {
				cmd = (Command) replaced.unwrap();
				if (!cmdDispatcher.isAllowedByNameFilter(Commands.valueOf(cmd.getName()))) {
					throw new MyError(kernel.getLocalization(), "UnknownCommand");
				}
			}
		} catch (Exception e) {
			Log.debug(e.getMessage());
		}

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
	protected GeoElement evalSymbolicNoLabel(ExpressionValue ve) {
		ve.resolveVariables(
				new EvalInfo(false).withSymbolicMode(SymbolicMode.SYMBOLIC_AV));
		if (ve.unwrap() instanceof Command
				&& "Sequence".equals(((Command) ve.unwrap()).getName())) {
			return doEvalSymbolicNoLabel(ve.wrap());
		}
		ExpressionNode replaced = ve
				.traverse(new SubExpressionEvaluator(this, ve)).wrap();
		if (replaced.inspect(new RecursiveEquationFinder(ve))) {
			replaced = new Equation(kernel,
					new GeoDummyVariable(cons, ve.wrap().getLabel()), replaced)
					.wrap();
			ve.wrap().setLabel(null);
		}

		return doEvalSymbolicNoLabel(replaced);
	}

	/**
	 * @param equ
	 *            equation
	 * @param info
	 *            evaluation flags
	 * @return equation or assignment
	 */
	static ValidExpression extractAssignment(Equation equ, EvalInfo info) {
		String lhsName = extractLabel(equ, info);
		if (lhsName !=null) {
			ExpressionNode rhs = equ.getRHS();
			rhs.setLabel(lhsName);
			return rhs;
			
		}
		return equ;
	}

	private static String extractLabel(Equation equ, EvalInfo info) {
		ExpressionNode lhs = equ.getLHS();
		if (lhs.getOperation() == Operation.FUNCTION && lhs.getLeft() instanceof GeoSymbolic
				&& lhs.getRight() instanceof FunctionVariable) {
			String lhsName = ((GeoSymbolic) lhs.getLeft()).getLabelSimple();
			return info.isLabelRedefinitionAllowedFor(lhsName) ? lhsName : null;
		}
		return null;
	}

	public void updateLabel(ValidExpression newValue, EvalInfo info) {
		if (newValue.unwrap() instanceof Equation) {
			String lhsName = extractLabel((Equation) newValue.unwrap(), info);
			if (lhsName != null) {
				newValue.setLabel(lhsName);
			}
		}
	}

}
