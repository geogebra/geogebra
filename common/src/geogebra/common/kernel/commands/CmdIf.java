package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDependentFunction;
import geogebra.common.kernel.algos.AlgoIf;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;

/**
 * If[ <GeoBoolean>, <GeoElement> ] If[ <GeoBoolean>, <GeoElement>, <GeoElement>
 * ]
 */
public class CmdIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIf(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2: // if - then
		case 3: // if - then - else
			if(kernelA.getConstruction().getRegistredFunctionVariable()!=null){
				String varName = kernelA.getConstruction().getRegistredFunctionVariable();
			FunctionVariable fv = new FunctionVariable(kernelA,varName);
			int r=	c.getArgument(0).replaceVariables(varName, fv);
			if(r>0){
				boolean oldFlag =kernelA.getConstruction().isSuppressLabelsActive() ;
				kernelA.getConstruction().setSuppressLabelCreation(true);
				GeoFunction elseFun = null;
				c.getArgument(1).replaceVariables(varName, fv);
				
				//AbstractApplication.debug("LEFT"+.getClass());
				if(n==3){
					c.getArgument(2).replaceVariables(varName, fv);
					elseFun=resolveFunction(c,2,fv);
				}
				GeoFunction ifFun = resolveFunction(c,1,fv);
				GeoFunction condFun = resolveFunction(c,0,fv);
				kernelA.getConstruction().setSuppressLabelCreation(oldFlag);
				return new GeoElement[]{If(c.getLabel(), 
						condFun,ifFun, elseFun)};
			}
			}
			arg =resArgs(c);
			GeoElement geoElse = n == 3 ? arg[2] : null;
			// standard case: simple boolean condition
			if (arg[0].isGeoBoolean()) {
				
				AlgoIf algo = new AlgoIf(cons, c.getLabel(),
						(GeoBoolean) arg[0], arg[1], geoElse);

				GeoElement[] ret = { algo.getGeoElement() };
				return ret;
			}

			// SPECIAL CASE for functions:
			// boolean function in x as condition
			// example: If[ x < 2, x^2, x + 2 ]
			// DO NOT change instanceof here (see
			// GeoFunction.isGeoFunctionable())
			else if (arg[0] instanceof GeoFunction) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						// now that lines are functionable, need to disallow eg if[x<=40, y=20]
						&& (ok[1] = (arg[1].isGeoFunctionable() && !arg[1].isGeoLine()))
						&& (geoElse == null || geoElse.isGeoFunctionable())) {
					GeoFunction elseFun = geoElse == null ? null
							: ((GeoFunctionable) geoElse).getGeoFunction();

					GeoElement[] ret = { If(c.getLabel(),
							booleanFun,
							((GeoFunctionable) arg[1]).getGeoFunction(),
							elseFun) };
					return ret;
				}
			}

			throw argErr(app, c.getName(), getBadArg(ok,arg));
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	private GeoFunction resolveFunction(Command c, int i, FunctionVariable fv) {
		c.getArgument(i).resolveVariables(false);
		return (GeoFunction)kernelA.getAlgebraProcessor().processFunction(new Function(c.getArgument(i),fv))[0];
	}
	

	/**
	 * If-then-else construct for functions. example: If[ x < 2, x^2, x + 2 ]
	 */
	final private GeoFunction If(String label, GeoFunction boolFun,
			GeoFunction ifFun, GeoFunction elseFun) {
		FunctionVariable fv = ifFun.getFunctionVariables()[0];
		ExpressionNode expr;
		if(elseFun==null){
			expr = new ExpressionNode(kernelA,wrap(boolFun,fv),Operation.IF,wrap(ifFun,fv));
		}else{
			expr = new ExpressionNode(kernelA,new MyNumberPair(kernelA,wrap(boolFun,fv),wrap(ifFun,fv)),Operation.IF_ELSE,wrap(elseFun,fv));
		}
		Function fun = new Function(expr,fv);
		AlgoDependentFunction algo = new AlgoDependentFunction(cons,label,fun);
		return algo.getFunction();
	}

	private ExpressionNode wrap(GeoFunction boolFun, FunctionVariable fv) {
		return new ExpressionNode(kernelA,boolFun,Operation.FUNCTION,fv);
	}
}
