package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.linear.RealMatrix;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;

//import org.geogebra.common.kernel.arithmetic.FunctionalNVar;

/**
 * Represents set of functions in linear regression. Collects functions and
 * builds linear combinations of them.
 * 
 * @author Zbynek
 */
public interface FunctionListND {
	/**
	 * Linear combination of functions
	 */
	public class XY implements FunctionListND {

		private GeoFunctionable[] array;

		@Override
		public boolean set(int i, GeoElement geo) {
			if (!(geo instanceof GeoFunctionable)) {
				return false;
			}
			array[i] = (GeoFunctionable) geo;
			return true;
		}

		@Override
		public double evaluate(int c, GeoPointND point) {
			return array[c].value(point.getInhomX());
		}

		@Override
		public double extractValueCoord(GeoPointND point) {
			return point.getInhomY();
		}

		// Making GeoFunction fit(x)= p1*f(x)+p2*g(x)+p3*h(x)+...
		@Override
		public final CasEvaluableFunction makeFunction(
				CasEvaluableFunction template, GeoList functionlist,
				RealMatrix P) {
			double p;
			GeoFunctionable gf = null;
			GeoFunction product = new GeoFunction(template.getConstruction());

			// First product:
			p = P.getEntry(0, 0); // parameter
			// Checks done in makeMatrixes...
			gf = array[0];
			GeoFunction fitfunction2 = GeoFunction.mult((GeoFunction) template,
					p, gf); // p1*f(x)
			for (int i = 1; i < array.length; i++) {
				p = P.getEntry(i, 0);
				gf = array[i];
				product = GeoFunction.mult(product, p, gf); // product= p*func
				fitfunction2 = GeoFunction.add(fitfunction2, fitfunction2,
						product, Operation.PLUS); // fit(x)=...+p*func
			}

			return fitfunction2;
		}

		@Override
		public CasEvaluableFunction getTemplate(Construction cons) {
			return new GeoFunction(cons);
		}

		@Override
		public void setSize(int size) {
			array = new GeoFunctionable[size];

		}

	}

	/**
	 * Linear combination of 2var functions
	 */
	public class XYZ implements FunctionListND {

		private Evaluate2Var[] array;

		@Override
		public boolean set(int i, GeoElement geo) {
			if (!(geo instanceof Evaluate2Var)) {
				return false;
			}
			array[i] = (Evaluate2Var) geo;
			return true;
		}

		@Override
		public double evaluate(int c, GeoPointND point) {
			return array[c].evaluate(point.getInhomX(), point.getInhomY());
		}

		@Override
		public double extractValueCoord(GeoPointND point) {
			return point.getInhomZ();
		}

		@Override
		public CasEvaluableFunction makeFunction(CasEvaluableFunction template,
				GeoList functionlist, RealMatrix P) {
			double p;
			Evaluate2Var gf = null;
			GeoFunctionNVar product = new GeoFunctionNVar(
					template.getConstruction());

			// First product:
			p = P.getEntry(0, 0); // parameter
			// Checks done in makeMatrixes...
			gf = array[0];
			GeoFunctionNVar fTemplate = (GeoFunctionNVar) template;
			mult(fTemplate, p, gf, Operation.MULTIPLY); // p1*f(x)

			for (int i = 1; i < array.length; i++) {
				p = P.getEntry(i, 0);
				gf = array[i];
				mult(product, p, gf, Operation.MULTIPLY); //

				add(fTemplate, fTemplate, product, Operation.PLUS); // fit(x)=...+p*func
			}
			fTemplate.setDefined(true);
			return template;
		}

		private static void add(GeoFunctionNVar res, GeoFunctionNVar lt,
				GeoFunctionNVar rt, Operation op) {
			Kernel kernel1 = res.getKernel();
			FunctionNVar fRes = GeoFunction.operationSymb(op, rt, lt)
					.deepCopy(kernel1);

			fRes.setExpression(AlgoDependentFunction
					.expandFunctionDerivativeNodes(fRes.getExpression(), true)
					.wrap());

			res.setFunction(fRes);

		}

		private static void mult(GeoFunctionNVar res, double lt,
				Evaluate2Var rt, Operation op) {
			Kernel kernel1 = res.getKernel();
			FunctionNVar fRes = GeoFunction
					.applyNumberSymb(op, rt, new MyDouble(kernel1, lt), false)
					.deepCopy(kernel1);
			fRes.setExpression(AlgoDependentFunction
					.expandFunctionDerivativeNodes(fRes.getExpression(), true)
					.wrap());
			res.setFunction(fRes);
		}

		@Override
		public CasEvaluableFunction getTemplate(Construction cons) {
			return new GeoFunctionNVar(cons);
		}

		@Override
		public void setSize(int size) {
			array = new Evaluate2Var[size];

		}

	}

	/**
	 * @param i
	 *            index
	 * @param geo
	 *            element
	 * @return whether element has acceptable type
	 */
	boolean set(int i, GeoElement geo);

	/**
	 * @param i
	 *            function index
	 * @param point
	 *            point used for evaluation
	 * @return function value
	 */
	double evaluate(int i, GeoPointND point);

	/**
	 * Pick coord of the point that should be compared with value of the
	 * regression function
	 * 
	 * @param point
	 *            point
	 * @return y or z coord of the point
	 */
	double extractValueCoord(GeoPointND point);

	/**
	 * Multiply functions from functionlist by coefficients from matrix p and
	 * return sum of the results
	 * 
	 * @param fitfunction
	 *            template function
	 * @param functionlist
	 *            list of functions
	 * @param p
	 *            coefficient matrix
	 * @return linear combination
	 */
	CasEvaluableFunction makeFunction(CasEvaluableFunction fitfunction,
			GeoList functionlist, RealMatrix p);

	/**
	 * @param cons
	 *            construction
	 * @return template function
	 */
	CasEvaluableFunction getTemplate(Construction cons);

	/**
	 * @param functionsize
	 *            number of functions
	 */
	void setSize(int functionsize);

}
