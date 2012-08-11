package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoKeepIf;
import geogebra.common.kernel.algos.AlgoKeepIf3;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.main.MyError;

/**
 *KeepIf
 */
public class CmdKeepIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdKeepIf(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement arg;
		GeoElement[] args;
		String arg1Str;
		switch (n) {

		case 3:
			// eg KeepIf[x(A)<2,A,{(1,1),(2,2),(3,3)}]
			
			// eg KeepIf[x(A)<3, A+B, {1,2,3}]
			arg1Str = c.getArgument(1).toString(StringTemplate.defaultTemplate);
			try {
				if(!arg1Str.equals(kernelA.getParser().parseLabel(arg1Str))){
					throw argErr(app, c.getName(), new MyStringBuffer(kernelA, arg1Str));
				}
			} catch (ParseException e) {
				throw argErr(app, c.getName(), new MyStringBuffer(kernelA, arg1Str));
			}
			
			GeoElement[] vars = new GeoElement[1];
			GeoList[] over = new GeoList[1];
			arg = resArgsForZip(c, vars, over);
						
			
			//App.debug(arg[0].getClassName()+" "+arg[1].getClassName()+" "+arg[2].getClassName()+" ");

			if (arg instanceof GeoBoolean) {

				AlgoKeepIf3 algo = new AlgoKeepIf3(cons, c.getLabel(), (GeoBoolean)arg, vars[0], over[0]);
				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			throw argErr(app, c.getName(), arg);

		case 2:
			args = resArgs(c);

			if ((ok[0] = args[0] instanceof GeoFunction)&&(ok[1] = args[1].isGeoList())) {
				GeoFunction booleanFun = (GeoFunction) args[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = args[1].isGeoList())) {

					AlgoKeepIf algo2 = new AlgoKeepIf(cons, c.getLabel(), booleanFun, ((GeoList) args[1]));
					GeoElement[] ret2 = { algo2.getResult() };
					return ret2;
				}
			}

			throw argErr(app, c.getName(), getBadArg(ok,args));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
