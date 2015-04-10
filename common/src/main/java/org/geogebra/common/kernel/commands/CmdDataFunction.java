package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
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
			return getDataFunction(kernelA, c.getLabel());
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	public static GeoElement[] getDataFunction(Kernel kernelA, String label) {
		FunctionVariable fv = new FunctionVariable(kernelA);
		MyList ml = new MyList(kernelA);
		ml.addListElement(new MyDouble(kernelA));
		ml.addListElement(new MyDouble(kernelA, -1));
		ExpressionNode en = new ExpressionNode(kernelA,fv,Operation.FREEHAND,
				ml);
		GeoFunction geo = new GeoFunction(en,fv);
		geo.setLabel(label);
		return new GeoElement[]{geo};
	}

	
}