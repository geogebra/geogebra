package org.geogebra.common.kernel.advanced;

import java.util.Arrays;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.plugin.Operation;

public class AlgoIsVertexForm extends AlgoElement {
	private final GeoFunctionable function;
	private final GeoBoolean result;

	/**
	 * @param c construction
	 * @param function function or conic
	 */
	public AlgoIsVertexForm(Construction c, GeoFunctionable function) {
		super(c);
		this.function = function;
		this.result = new GeoBoolean(c);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{function.toGeoElement()};
		setOnlyOutput(result);
		setDependencies();
	}

	@Override
	public void compute() {
		if (!function.isDefined()) {
			result.setValue(false);
			return;
		}
		ExpressionNode fn = function.getFunction().getExpression();

		result.setValue(isVertexForm(fn));
	}

	private boolean isVertexForm(ExpressionNode expr) {
		// remove all additive constants: a*b*(x+t)^2+c-d  -> a*b*(x+t)^2
		ExpressionNode term = removeConstants(expr, Operation.PLUS, Operation.MINUS);
		// remove all multiplicative constants: a*b*(x+t)^2  -> (x+t)^2
		ExpressionNode normalizedTerm = removeConstants(term, Operation.MULTIPLY);
		// check that we really have (x+t)^2
		return isASquare(normalizedTerm)
				&& isNormalizedLinearExpr(normalizedTerm.getLeftTree());
	}

	/**
	 * @param base expression
	 * @return whether expression is in the form x+C or C-x where C is a constant expression
	 */
	private boolean isNormalizedLinearExpr(ExpressionNode base) {
		ExpressionNode nonConstantTem = removeConstants(base, Operation.PLUS, Operation.MINUS);
		return nonConstantTem.unwrap() instanceof FunctionVariable;
	}

	private boolean isASquare(ExpressionNode normalizedTerm) {
		return normalizedTerm.getOperation() == Operation.POWER
				&& ExpressionNode.isConstantDouble(normalizedTerm.getRight(), 2);
	}

	private ExpressionNode removeConstants(ExpressionNode expr, Operation... ops) {
		if (Arrays.asList(ops).contains(expr.getOperation())) {
			if (expr.getLeft().isConstant()) {
				return removeConstants(expr.getRightTree(), ops);
			}
			if (expr.getRight() != null && expr.getRight().isConstant()) {
				return removeConstants(expr.getLeftTree(), ops);
			}
		}
		return expr;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.IsVertexForm;
	}
}
