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
		GeoElement[] arg;
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
			
			
			arg = resArgsLocalListVar(c, 1, 2);
						
			
			//App.debug(arg[0].getClassName()+" "+arg[1].getClassName()+" "+arg[2].getClassName()+" ");

			if ((ok[0] = arg[0] instanceof GeoBoolean)&&(ok[2] = arg[2].isGeoList())) {

				AlgoKeepIf3 algo = new AlgoKeepIf3(cons, c.getLabel(), (GeoBoolean)arg[0], arg[1], ((GeoList) arg[2]));
				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			ok[1] = true;
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		case 2:
			arg = resArgs(c);

			if ((ok[0] = arg[0] instanceof GeoFunction)&&(ok[1] = arg[1].isGeoList())) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					AlgoKeepIf algo2 = new AlgoKeepIf(cons, c.getLabel(), booleanFun, ((GeoList) arg[1]));
					GeoElement[] ret2 = { algo2.getResult() };
					return ret2;
				}
			}

			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
