package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * Derivative[ &lt;GeoFunction&gt; ] Derivative[ &lt;GeoFunctionNVar&gt;, &lt;var&gt; ]
 * Derivative[ &lt;GeoCurveCartesian&gt; ]
 */
public final class CmdDerivative extends CommandProcessor {

	private Commands cmd;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param cmd
	 *            command
	 */
	public CmdDerivative(Kernel kernel, Commands cmd) {
		super(kernel);
		this.cmd = cmd;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		String label = c.getLabel();
		GeoElement[] arg, arg2;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null) {
					label = getDerivLabel(f.toGeoElement(), 1);
				}
				GeoElement[] ret = { derivative(label, f, null, null, info) };
				return ret;
			}
			throw argErr(c, arg[0]);

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
					GeoElement[] ret = { derivative(label, f, null,
							(GeoNumberValue) arg[1], info) };
					return ret;

				}
			} catch (Throwable t) {
				Log.debug(t);
			}
			cons.setSuppressLabelCreation(suppress);
			// Derivative[ f(a,b), a ]
			try {
				arg2 = resArgsLocalNumVar(c, 1, 1, -1);

				if (arg2[0] instanceof CasEvaluableFunction
						&& arg2[1].isGeoNumeric()) {

					CasEvaluableFunction f = (CasEvaluableFunction) arg2[0];
					FunctionVariable[] vars = f.getFunctionVariables();

					String var = arg2[1].getLabelSimple();

					// distinguish between Derivative[ f, a] and Derivative[ f,
					// p] for f(a,b) = a + b and slider 'p'
					// skip this check on file loading
					boolean ok = kernel.getLoadingMode();
					if (vars != null) {
						for (int i = 0; i < vars.length && !ok; i++) {
							ok = vars[i].getSetVarString().equals(var);
						}
					}
					if (ok) {
						GeoElement[] ret = { derivative(label,
								(CasEvaluableFunction) arg2[0], // function
								(GeoNumeric) arg2[1], null, info) }; // var
						return ret;
					} // else fall through

				}
			} catch (Throwable t) {
				Log.debug(t);
			}

			// Derivative[ f(x, y), x]
			arg = resArgs(c);
			if (arg[0] instanceof CasEvaluableFunction
					&& arg[1].isGeoFunction()) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(
						arg[1].toString(StringTemplate.defaultTemplate));
				GeoElement[] ret = {
						derivative(label, (CasEvaluableFunction) arg[0], // function
								var, null, info) }; // var
				return ret;
			}

			// if we get here, the first argument must have been wrong
			throw argErr(c, arg[0]);

		case 3:
			// Derivative[ f(a,b), a, 2 ]
			try {
				arg = resArgsLocalNumVar(c, 1, 1, -1);
				if (arg[0] instanceof CasEvaluableFunction
						&& arg[1].isGeoNumeric()
						&& arg[2] instanceof GeoNumberValue) {
					GeoElement[] ret = {
							derivative(label, (CasEvaluableFunction) arg[0], // function
									(GeoNumeric) arg[1],
									(GeoNumberValue) arg[2], info) }; // var
					return ret;
				}
			} catch (Throwable t) {
				Log.debug(t);
			}

			arg = resArgs(c);
			// Derivative[ f(x, y), x, 2]
			if (arg[0] instanceof GeoFunctionNVar && arg[1].isGeoFunction()
					&& arg[2] instanceof GeoNumberValue) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(
						arg[1].toString(StringTemplate.defaultTemplate));
				GeoElement[] ret = { derivative(label, (GeoFunctionNVar) arg[0], // function
						var, (GeoNumberValue) arg[2], info) }; // var
				return ret;
			}
			// if we get here, the first argument must have been wrong
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

	/**
	 * Returns eg f''_1' for third derivative of f when f''' is already used
	 * 
	 * @param geo
	 *            function
	 * @param order
	 *            derivative order
	 * @return next free label for derivative
	 */
	static String getDerivLabel(GeoElementND geo, int order) {
		String label = null;

		if (geo.isLabelSet()) {
			StringBuilder labelBuilder = new StringBuilder(
					geo.getLabel(StringTemplate.defaultTemplate));
			for (int i = 0; i < order; i++) {
				labelBuilder.append('\'');
			}
			label = labelBuilder.toString();
		} else {
			if (geo.getParentAlgorithm() instanceof AlgoDependentGeoCopy) {
				return getDerivLabel(geo.getParentAlgorithm().getInput(0),
						order);
			}
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
	 * @param info
	 *            evaluation flags
	 * @return derivaive
	 */
	public GeoElement derivative(String label, CasEvaluableFunction f,
			GeoNumeric var, GeoNumberValue n, EvalInfo info) {
		boolean numeric = cmd == Commands.NDerivative
				|| !app.getSettings().getCasSettings().isEnabled();
		AlgoDerivative algo = new AlgoDerivative(cons, label, f, var, n, numeric, info);
		return algo.getResult();
	}

}
