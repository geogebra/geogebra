package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * Function[ &lt;GeoFunction&gt;, &lt;NumberValue&gt;, &lt;NumberValue&gt; ]
 */
public class CmdDataFunction extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDataFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
		case 2:
		case 3:
			FunctionVariable fv = new FunctionVariable(kernel);
			ExpressionValue en = simplify(c, fv);
			GeoFunction geo = new GeoFunction(kernel, en.wrap(), fv);
			geo.setLabel(c.getLabel());
			return new GeoElement[] { geo };
		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param kernelA
	 *            kernel
	 * @param label
	 *            output label
	 * @param xlist
	 *            x-value list
	 * @param ylist
	 *            y-value list
	 * @param arg0
	 *            function argument
	 * @param fv
	 *            variable
	 * @return data function expression
	 */
	public static ExpressionValue getDataFunction(Kernel kernelA, String label,
			ListValue xlist, ListValue ylist, ExpressionNode arg0,
			FunctionVariable fv) {

		// ml.addListElement(new MyDouble(kernelA));
		// vl.addListElement(new MyDouble(kernelA, -1));
		ExpressionValue arg = fv;
		if (arg0 != null) {
			arg0.replaceVariables("x", fv);
			arg = arg0;
		}
		return new ExpressionNode(kernelA, arg, Operation.DATA,
				new MyNumberPair(kernelA, xlist, ylist));

	}

	@Override
	public ExpressionValue simplify(Command c) {
		return simplify(c, new FunctionVariable(kernel));
	}

	private ExpressionValue simplify(Command c, FunctionVariable fv) {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			return getDataFunction(kernel, c.getLabel(), new MyList(kernel),
					new MyList(kernel), null, fv);
		case 2:
			return getDataFunction(kernel, c.getLabel(), toList(c, 0),
					toList(c, 1), null, fv);
		case 3:
			return getDataFunction(kernel, c.getLabel(), toList(c, 0),
					toList(c, 1), c.getArgument(2), fv);
		}
		return null;
	}

	private ListValue toList(Command c, int argIndex) {
		ExpressionValue ev = c.getArgument(argIndex).unwrap();
		if (ev instanceof Variable) {
			ev = kernel
					.lookupLabel(ev.toString(StringTemplate.noLocalDefault));
		}
		if (ev instanceof ListValue) {
			return (ListValue) ev;
		}

		// eg DataFunction(x({A,B,C}), y({A,B,C}))
		GeoElement res = this.resArg(c.getArgument(argIndex),
				new EvalInfo(false));
		if (res instanceof ListValue) {
			return (ListValue) res;
		}

		throw argErr(c, ev);
	}

	/**
	 * @param kernelA
	 *            kernel
	 * @param label
	 *            output label
	 * @return data function
	 */
	public static GeoElement[] emptyFunction(Kernel kernelA, String label) {
		FunctionVariable fv = new FunctionVariable(kernelA);
		ExpressionValue en = CmdDataFunction.getDataFunction(kernelA, label,
				new MyList(kernelA), new MyList(kernelA), null, fv);
		GeoFunction geo = new GeoFunction(kernelA, en.wrap(), fv);
		geo.setLabel(label);
		return new GeoElement[] { geo };
	}

}