package org.geogebra.common.gui.view.table.regression;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.editor.share.util.Unicode;

public class CustomRegressionSpecification implements RegressionSpecification {

	private final String label;
	private final double[] exponents;
	private final Type type;

	public enum Type {
		LINEAR, EXPONENTIAL, EXP_PLUS_CONSTANT
	}

	/**
	 * Linear regression spec.
	 * @param label label for selection dropdown
	 * @param exponents term exponents
	 */
	public CustomRegressionSpecification(String label, double... exponents) {
		this.label = asUnicode(label);
		this.exponents = exponents;
		type = Type.LINEAR;
	}

	/**
	 * Non-linear regression spec
	 * @param label label for selection dropdown
	 * @param type regression type
	 */
	public CustomRegressionSpecification(String label, Type type) {
		this.label = asUnicode(label);
		this.exponents = new double[0];
		this.type = type;
	}

	private String asUnicode(String label) {
		return label.replace("^2", String.valueOf(Unicode.SUPERSCRIPT_2))
				.replace("*", String.valueOf(Unicode.CENTER_DOT));
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Command buildCommand(Kernel kernel, MyVecNode points) {
		Command cleanData = new Command(kernel, Commands.RemoveUndefined.getCommand(),
				false);
		cleanData.addArgument(points.wrap());
		Commands cmdName = type == Type.EXPONENTIAL ? Commands.FitExp : Commands.Fit;
		Command cmd = new Command(kernel, cmdName.name(), false);
		cmd.addArgument(cleanData.wrap());
		if (type == Type.LINEAR) {
			MyList functions = new MyList(kernel);
			FunctionVariable x = new FunctionVariable(kernel);
			for (double exponent : exponents) {
				functions.addListElement(x.wrap().power(exponent));
			}
			cmd.addArgument(functions.wrap());
		} else if (type == Type.EXP_PLUS_CONSTANT) {
			FunctionVariable x = new FunctionVariable(kernel);
			ExpressionNode model = new ExpressionNode(kernel, Double.NaN)
					.multiplyR(new ExpressionNode(kernel, Double.NaN).multiply(x).exp())
					.plus(new ExpressionNode(kernel, Double.NaN));
			cmd.addArgument(model);
		}
		cmd.setRespectingFilters(false);
		return cmd;
	}

	@Override
	public String getFormula() {
		return null;
	}

	@Override
	public String getCoeffOrdering() {
		if (label.contains("c")) {
			return "abc";
		} else if (label.contains("b")) {
			return "ab";
		}
		return "a";
	}

	@Override
	public boolean hasCorrelationCoefficient() {
		return true;
	}

	@Override
	public boolean canPlot() {
		return false;
	}

	@Override
	public boolean hasCoefficientOfDetermination() {
		return false;
	}
}
