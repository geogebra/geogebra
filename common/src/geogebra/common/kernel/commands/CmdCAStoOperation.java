package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;

public class CmdCAStoOperation extends CommandProcessor{

	private Operation op;

	public CmdCAStoOperation(Kernel kernel,Operation op) {
		super(kernel);
		this.op = op;
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		ExpressionNode en = null;
		GeoElement[] args = resArgs(c);
		switch(op){
			case FLOOR:
			case YCOORD:
			case XCOORD: en = new ExpressionNode(kernelA,args[0],op,null);
			break;
			case MULTIPLY:
			case VECTORPRODUCT:
				en = new ExpressionNode(kernelA,args[0],op,args[1]);
		}
		return kernelA.getAlgebraProcessor().processExpressionNode(en);
	}

}
