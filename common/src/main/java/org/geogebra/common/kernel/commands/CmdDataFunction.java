package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;


/**
 * Function[ <GeoFunction>, <NumberValue>, <NumberValue> ]
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
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0: 
		case 2:
		case 3:
			FunctionVariable fv = new FunctionVariable(kernelA);
			ExpressionValue en = simplify(c, fv);
			GeoFunction geo = new GeoFunction(en.wrap(), fv);
			geo.setLabel(c.getLabel());
			return new GeoElement[] { geo };
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	public static ExpressionNode getDataFunction(Kernel kernelA, String label,
			MyList ml, MyList vl, ExpressionNode arg0, FunctionVariable fv) {

		// ml.addListElement(new MyDouble(kernelA));
		// vl.addListElement(new MyDouble(kernelA, -1));
		ExpressionValue arg = fv;
		if (arg0 != null) {
			arg0.replaceVariables("x", fv);
			arg = arg0;
		}
		return new ExpressionNode(kernelA, arg, Operation.DATA,
				new MyNumberPair(kernelA, ml, vl));

	}

	@Override
	public ExpressionValue simplify(Command c) {
		return simplify(c, new FunctionVariable(kernelA));
	}

	private ExpressionValue simplify(Command c, FunctionVariable fv) {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			return getDataFunction(kernelA, c.getLabel(), new MyList(kernelA),
					new MyList(kernelA), null, fv);
		case 2:
			return getDataFunction(kernelA, c.getLabel(), (MyList) c
					.getArgument(0).unwrap(), (MyList) c.getArgument(1)
					.unwrap(), null, fv);
		case 3:
			return getDataFunction(kernelA, c.getLabel(), (MyList) c
					.getArgument(0).unwrap(), (MyList) c.getArgument(1)
					.unwrap(), c.getArgument(2), fv);
		}
		return null;
	}

	public static GeoElement[] emptyFunction(Kernel kernelA, String label) {
		FunctionVariable fv = new FunctionVariable(kernelA);
		ExpressionValue en = CmdDataFunction.getDataFunction(kernelA, label,
				new MyList(kernelA), new MyList(kernelA), null, fv);
		GeoFunction geo = new GeoFunction(en.wrap(), fv);
		geo.setLabel(label);
		return new GeoElement[] { geo };
	}
	
}