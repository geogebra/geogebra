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

import com.google.j2objc.annotations.Weak;

/**
 * Processor for symbolic elements
 *
 * @author Zbynek
 */
public class SymbolicProcessor {
	@Weak
	private Kernel kernel;
	@Weak
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
		private EvalInfo evalInfo;

		public SubExpressionEvaluator(SymbolicProcessor symbolicProcessor,
				ExpressionValue root, EvalInfo evalInfo) {
			this.processor = symbolicProcessor;
			this.root = root;
			this.evalInfo = evalInfo;
		}

		@Override
		public ExpressionValue process(ExpressionValue ev) {
			if (ev instanceof Command && ev != root.unwrap()) {
				return processor.evalSymbolicNoLabel(ev, evalInfo);
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
	protected GeoElement doEvalSymbolicNoLabel(ExpressionNode replaced, EvalInfo info) {
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
				if (var instanceof GeoDummyVariable) {
					cons.getCASdummies()
							.add(((GeoDummyVariable) var).getVarName());
				} else if (var != null) {
					noDummyVars.add(var);
				}
			}
		}
		GeoSymbolic sym;
		if (noDummyVars.size() > 0) {
			AlgoDependentSymbolic ads = new AlgoDependentSymbolic(cons,
					replaced, noDummyVars, info.getArbitraryConstant());
			sym = (GeoSymbolic) ads.getOutput(0);
		} else {
			sym = new GeoSymbolic(cons);
			sym.setArbitraryConstant(info.getArbitraryConstant());
			sym.setDefinition(replaced);
			sym.computeOutput();
		}
		return sym;
	}

	protected GeoElement evalSymbolicNoLabel(ExpressionValue ve) {
		return evalSymbolicNoLabel(ve, new EvalInfo());
	}

	/**
	 * @param ve
	 *            input expression
	 * @return processed geo
	 */
	protected GeoElement evalSymbolicNoLabel(ExpressionValue ve, EvalInfo info) {
		ve.resolveVariables(
				new EvalInfo(false).withSymbolicMode(SymbolicMode.SYMBOLIC_AV));
		if (ve.unwrap() instanceof Command
				&& "Sequence".equals(((Command) ve.unwrap()).getName())) {
			return doEvalSymbolicNoLabel(ve.wrap(), info);
		}
		EvalInfo subInfo = new EvalInfo().withArbitraryConstant(info.getArbitraryConstant());
		ExpressionNode replaced = ve
				.traverse(new SubExpressionEvaluator(this, ve, subInfo)).wrap();
		if (replaced.inspect(new RecursiveEquationFinder(ve))) {
			replaced = new Equation(kernel,
					new GeoDummyVariable(cons, ve.wrap().getLabel()), replaced)
					.wrap();
			ve.wrap().setLabel(null);
		}

		return doEvalSymbolicNoLabel(replaced, info);
	}

	/**
	 * @param equ
	 *            equation
	 * @param info
	 *            evaluation flags
	 * @return equation or assignment
	 */
	protected ValidExpression extractAssignment(Equation equ, EvalInfo info) {
		String lhsName = extractLabel(equ, info);
		if (lhsName != null) {
			ExpressionNode rhs = equ.getRHS();
			FunctionNVar lhs = getRedefiningFunction(equ);

			ValidExpression extractedFunction = rhs;
			if (lhs != null) {
				extractedFunction =
						kernel.getArithmeticFactory().newFunction(rhs, lhs.getFunctionVariables());
			}
			extractedFunction.setLabel(lhsName);
			return extractedFunction;
		}
		return equ;
	}

	private String extractLabel(Equation equ, EvalInfo info) {
		ExpressionNode lhs = equ.getLHS();
		GeoSymbolic symbolic = getRedefinitionObject(equ);
		if (symbolic != null && !kernel.getConstruction().isFileLoading()) {
			String lhsName = ((GeoSymbolic) lhs.getLeft()).getLabelSimple();
			return info.isLabelRedefinitionAllowedFor(lhsName) ? lhsName : null;
		}
		return null;
	}

	private GeoSymbolic getRedefinitionObject(Equation equation) {
		ExpressionNode lhs = equation.getLHS();
		if (lhs.getOperation() == Operation.FUNCTION
				&& lhs.getLeft() instanceof GeoSymbolic
				&& lhs.getRight() instanceof FunctionVariable) {
			return (GeoSymbolic) lhs.getLeft();
		}
		return null;
	}

	private FunctionNVar getRedefiningFunction(Equation equation) {
		GeoSymbolic symbolic = getRedefinitionObject(equation);
		if (symbolic != null) {
			ExpressionNode node = symbolic.getDefinition();
			ExpressionValue value = node != null ? node.unwrap() : null;
			if (value instanceof FunctionNVar) {
				return (FunctionNVar) value;
			}
		}
		return null;
	}

	/**
	 * @param expression
	 *            expression
	 * @param info
	 *            evaluation flags
	 */
	public void updateLabel(ValidExpression expression, EvalInfo info) {
		if (expression.unwrap() instanceof Equation) {
			String lhsName = extractLabel((Equation) expression.unwrap(), info);
			if (lhsName != null) {
				expression.setLabel(lhsName);
			}
		}
	}

}
