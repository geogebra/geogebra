package org.geogebra.common.kernel.cas;

import java.util.ArrayList;
import java.util.Map;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * @author zbynek
 *
 */
public class AlgoSolveODECas extends AlgoUsingTempCASalgo {
	private CasEvaluableFunction f;
	private GeoElementND g;
	private GeoPointND pt;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            input function
	 * @param info
	 *            eval info
	 */
	public AlgoSolveODECas(Construction cons, String label,
			CasEvaluableFunction f, EvalInfo info) {
		super(cons);
		this.nocas = !info.isUsingCAS();
		cons.addCASAlgo(this);
		this.f = f;
		/** g is created in compute */
		compute();
		setInputOutput();
		g.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            input function
	 * @param pt
	 *            point through which the integral line should go
	 */
	public AlgoSolveODECas(Construction cons, String label,
			CasEvaluableFunction f, GeoPointND pt) {
		super(cons);
		cons.addCASAlgo(this);
		this.f = f;
		this.pt = pt;
		/** g is created in compute */
		compute();
		setInputOutput();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.SolveODE;
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	protected void setInputOutput() {
		if (pt == null) {
			input = new GeoElement[] { f.toGeoElement() };
		} else {
			input = new GeoElement[] { f.toGeoElement(), pt.toGeoElement() };
		}
		setOnlyOutput(g);
		setDependencies();

	}

	private String oldCASstring;
	private boolean nocas = false;

	@Override
	public void compute() {
		if (!f.isDefined() && g != null) {
			g.setUndefined();
			return;
		}

		// function expression
		String funExp = f.toString(StringTemplate.prefixedDefault);

		// remove f(x,y) =
		if (funExp.indexOf('=') > -1) {
			funExp = funExp.split("=")[1];
		}

		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);

		if (pt != null) {
			sb.append("SolveODEPoint(");
			sb.append(funExp);
			sb.append(",");
			sb.append(pt.toValueString(StringTemplate.prefixedDefault));
		} else {
			sb.append("SolveODE(");
			sb.append(funExp);
		}
		sb.append(")");

		String casString = sb.toString();

		if (!casString.equals(oldCASstring)) {
			updateG(casString);
			oldCASstring = casString;
		}
		if (pt != null && arbconst.getTotalNumberOfConsts() == 1) {
			findPathThroughPoint();
		}

	}

	private void findPathThroughPoint() {
		GeoNumeric c1 = arbconst.getConst(0);
		if (c1 == null) {
			g.setUndefined();
			return;
		}
		c1.setAlgebraVisible(false);
	}

	private void updateG(String casString) {
		String functionOut;
		boolean ok = false;
		try {
			// TODO put caching back
			functionOut = kernel.evaluateGeoGebraCAS(casString,
					nocas ? getSilentArbConst() : arbconst);
			boolean flag = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoElementND[] res = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionsOrErrors(functionOut,
							false);
			cons.setSuppressLabelCreation(flag);
			if (res != null && res.length > 0) {

				// convert eg y=2 into a function
				if (res[0].isGeoFunctionable()) {
					res[0] = ((GeoFunctionable) res[0]).getGeoFunction();
				}

				if (g == null) {
					g = res[0];
				} else {
					g.set(res[0]);
				}
				ok = true;
			}
		} catch (Throwable e) {
			Log.debug("AlgoSolveODECas: " + e.getMessage());
		}
		if (!ok) {
			if (g != null) {
				g.setUndefined();
			} else {
				g = new GeoFunction(cons);
			}
		}
	}

	private MyArbitraryConstant getSilentArbConst() {
		return new MyArbitraryConstant(this) {
			@Override
			protected GeoNumeric nextConst(ArrayList<GeoNumeric> consts2,
					Map<Integer, GeoNumeric> map, String prefix, double index) {
				return new GeoNumeric(cons, 0.0);
			}
		};
	}

	/**
	 * @return resulting function, conic or line
	 */
	public GeoElement getResult() {
		return g.toGeoElement();
	}

	@Override
	public void refreshCASResults() {
		this.oldCASstring = "";

	}

	
}
