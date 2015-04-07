package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Derivative[ <GeoFunction> ] Derivative[ <GeoFunctionNVar>, <var> ]
 * Derivative[ <GeoCurveCartesian> ]
 */
public class CmdDerivative extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDerivative(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		String label = c.getLabel();
		GeoElement[] arg, arg2;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null)
					label = getDerivLabel(f.toGeoElement(), 1);
				GeoElement[] ret = { Derivative(label, f, null, null) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			boolean suppress = cons.isSuppressLabelsActive(); // we need to
																// reset this
																// later #2356
			try {
				arg = resArgs(c);
				// Derivative[ f(x), 2]
				if ((arg[0].isGeoFunction() || arg[0].isGeoCurveCartesian())
						&& arg[1] instanceof GeoNumberValue) {
					double order = ((GeoNumberValue) arg[1]).getDouble();

					// default for arg[1] not GeoNumeric (eg Segment)
					// don't want f''' for name
					boolean constant = false;

					if (arg[1].isGeoNumeric()) {
						if (arg[1].getParentAlgorithm() == null) {
							// Derivative[f,n] -> don't want f'' for name
							// Derivative[f,2] -> do want f'' for name
							constant = !arg[1].isLabelSet();
						} else {
							// eg Derivative[f,n+2] -> don't want f'''' for name
							constant = false;
						}
					}

					CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
					if (label == null && constant) {
						int iorder = (int) Math.round(order);
						label = getDerivLabel(f.toGeoElement(), iorder);
					}
					GeoElement[] ret = { Derivative(label, f, null,
							(GeoNumberValue) arg[1]) };
					return ret;

				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			cons.setSuppressLabelCreation(suppress);
			// Derivative[ f(a,b), a ]
			try {
				arg2 = resArgsLocalNumVar(c, 1, 1);

				if (arg2[0] instanceof CasEvaluableFunction
						&& arg2[1].isGeoNumeric()) {

					CasEvaluableFunction f = (CasEvaluableFunction) arg2[0];
					FunctionVariable[] vars = f.getFunctionVariables();

					String var = arg2[1].getLabelSimple();

					// distinguish between Derivative[ f, a] and Derivative[ f,
					// p] for f(a,b) = a + b and slider 'p'
					boolean ok = false;
					if (vars != null) {
						for (int i = 0; i < vars.length; i++) {
							if (vars[i].getSetVarString().equals(var)) {
								ok = true;
								break;
							}
						}
					}

					if (ok) {
						GeoElement[] ret = { Derivative(label,
								(CasEvaluableFunction) arg2[0], // function
								(GeoNumeric) arg2[1], null) }; // var
						return ret;
					} // else fall through

				}
			} catch (Throwable t) {
				t.printStackTrace();
			}

			// Derivative[ f(x, y), x]
			arg = resArgs(c);
			if (arg[0] instanceof CasEvaluableFunction
					&& arg[1].isGeoFunction()) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(arg[1]
						.toString(StringTemplate.defaultTemplate));
				GeoElement[] ret = { Derivative(label,
						(CasEvaluableFunction) arg[0], // function
						var, null) }; // var
				return ret;
			}

			// if we get here, the first argument must have been wrong
			throw argErr(app, c.getName(), arg[0]);

		case 3:
			// Derivative[ f(a,b), a, 2 ]
			try {
				arg = resArgsLocalNumVar(c, 1, 1);
				if (arg[0] instanceof CasEvaluableFunction
						&& arg[1].isGeoNumeric()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = { Derivative(label,
							(CasEvaluableFunction) arg[0], // function
							(GeoNumeric) arg[1], (GeoNumberValue) arg[2]) }; // var
					return ret;
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}

			arg = resArgs(c);
			// Derivative[ f(x, y), x, 2]
			if (arg[0] instanceof GeoFunctionNVar && arg[1].isGeoFunction()
					&& arg[2] instanceof GeoNumberValue) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(arg[1]
						.toString(StringTemplate.defaultTemplate));
				GeoElement[] ret = { Derivative(label,
						(GeoFunctionNVar) arg[0], // function
						var, (GeoNumberValue) arg[2]) }; // var
				return ret;
			}
			// if we get here, the first argument must have been wrong
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

	private static String getDerivLabel(GeoElement geo, int order) {
		String label = null;

		if (geo.isLabelSet()) {
			label = geo.getLabel(StringTemplate.defaultTemplate);
			for (int i = 0; i < order; i++)
				label = label + "'";
		}

		return geo.getFreeLabel(label);
	}

	/**
	 * Computes n-th derivative of f
	 * 
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param var
	 *            variable
	 * @param n
	 *            derivative degree
	 * @return derivaive
	 */
	public GeoElement Derivative(String label, CasEvaluableFunction f,
			GeoNumeric var, NumberValue n) {
		AlgoDerivative algo = new AlgoDerivative(cons, label, f, var, n);
		return algo.getResult();
	}

}
