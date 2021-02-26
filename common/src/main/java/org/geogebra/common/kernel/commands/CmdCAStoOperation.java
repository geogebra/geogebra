package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * CAS command that gets rewritten as operation in input bar
 * 
 * @author zbynek
 *
 */
public class CmdCAStoOperation extends CommandProcessor {

	private Operation op;

	/**
	 * @param kernel
	 *            kernel
	 * @param op
	 *            operation this command should be rewritten to
	 */
	public CmdCAStoOperation(Kernel kernel, Operation op) {
		super(kernel);
		this.op = op;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		ExpressionNode en = simplify(c);

		GeoElement[] ret = kernel.getAlgebraProcessor()
				.processExpressionNode(en, info);

		if (ret[0] != null) {
			ret[0].setLabel(c.getLabel());
		}

		return ret;
	}

	@Override
	public ExpressionNode simplify(Command c) {
		EvalInfo info = new EvalInfo(false);
		switch (op) {
		case YCOORD:
		case XCOORD:
			if (c.getArgumentNumber() != 1) {
				throw argNumErr(c);
			}
			c.getArgument(0).resolveVariables(info);
			return new ExpressionNode(kernel, c.getArgument(0).unwrap(), op, null);

		case MULTIPLY:
		case VECTORPRODUCT:
		case NPR:
			if (c.getArgumentNumber() != 2) {
				throw argNumErr(c);
			}
			c.getArgument(0).resolveVariables(info);
			c.getArgument(1).resolveVariables(info);
			return new ExpressionNode(kernel, c.getArgument(0).unwrap(), op,
					c.getArgument(1).unwrap());
		default:
			throw new Error("Unhandled operation " + op);
		}
	}

}
