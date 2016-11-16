package org.geogebra.common.kernel.statistics;

import org.apache.commons.math.linear.RealMatrix;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
//import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;

public interface FunctionListND {

	public class XY implements FunctionListND {

		private GeoFunctionable[] array;

		public boolean set(int i, GeoElement geo) {
			if (!(geo instanceof GeoFunctionable)) {
				return false;
			}
			array[i] = (GeoFunctionable) geo;
			return true;
		}

		public double evaluate(int c, GeoPointND point) {
			return array[c].getGeoFunction().evaluate(point.getInhomX());
		}

		public double extractValueCoord(GeoPointND point) {
			return point.getInhomY();
		}

		// Making GeoFunction fit(x)= p1*f(x)+p2*g(x)+p3*h(x)+...
		public final CasEvaluableFunction makeFunction(
				CasEvaluableFunction template, GeoList functionlist,
				RealMatrix P) {
			double p;
			GeoFunction gf = null;
			GeoFunction product = new GeoFunction(template.getConstruction());

			// First product:
			p = P.getEntry(0, 0); // parameter
			// Checks done in makeMatrixes...
			gf = array[0].getGeoFunction();
			GeoFunction fitfunction2 = GeoFunction.mult((GeoFunction) template,
					p, gf); // p1*f(x)
			for (int i = 1; i < array.length; i++) {
				p = P.getEntry(i, 0);
				gf = array[i].getGeoFunction();
				product = GeoFunction.mult(product, p, gf); // product= p*func
				fitfunction2 = GeoFunction.add(fitfunction2, fitfunction2,
						product, Operation.PLUS); // fit(x)=...+p*func
			}

			return fitfunction2;
		}

		public CasEvaluableFunction getTemplate(Construction cons) {
			return new GeoFunction(cons);
		}

		public void setSize(int size) {
			array = new GeoFunctionable[size];

		}

	}

	public class XYZ implements FunctionListND {

		private Evaluate2Var[] array;


		public boolean set(int i, GeoElement geo) {
			if (!(geo instanceof Evaluate2Var)) {
				return false;
			}
			array[i] = (Evaluate2Var) geo;
			return true;
		}

		public double evaluate(int c, GeoPointND point) {
			return array[c].evaluate(point.getInhomX(), point.getInhomY());
		}

		public double extractValueCoord(GeoPointND point) {
			return point.getInhomZ();
		}

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
			mult(fTemplate, p,
					gf, Operation.MULTIPLY); // p1*f(x)


			for (int i = 1; i < array.length; i++) {
				p = P.getEntry(i, 0);
				gf = array[i];
				mult(product, p, gf, Operation.MULTIPLY); //

				add(fTemplate, fTemplate,
						product, Operation.PLUS); // fit(x)=...+p*func
			}
			fTemplate.setDefined(true);
			return template;
		}

		private void add(GeoFunctionNVar res, GeoFunctionNVar lt,
				GeoFunctionNVar rt, Operation op) {
			Kernel kernel1 = res.getKernel();
			FunctionNVar fRes = GeoFunction.operationSymb(op, rt, lt)
					.deepCopy(kernel1);

			fRes.setExpression(AlgoDependentFunction
					.expandFunctionDerivativeNodes(fRes.getExpression(), true)
					.wrap());

			res.setFunction(fRes);

		}

		private void mult(GeoFunctionNVar res, double lt, Evaluate2Var rt,
				Operation op) {
			Kernel kernel1 = res.getKernel();
			FunctionNVar fRes = GeoFunction
					.applyNumberSymb(op, rt,
					new MyDouble(kernel1, lt), false).deepCopy(kernel1);
			fRes.setExpression(AlgoDependentFunction
					.expandFunctionDerivativeNodes(fRes.getExpression(), true)
					.wrap());
			res.setFunction(fRes);
		}

		public CasEvaluableFunction getTemplate(Construction cons) {
			return new GeoFunctionNVar(cons);
		}

		public void setSize(int size) {
			array = new Evaluate2Var[size];

		}

	}

	boolean set(int i, GeoElement geo);

	double evaluate(int c, GeoPointND point);

	double extractValueCoord(GeoPointND point);

	CasEvaluableFunction makeFunction(CasEvaluableFunction fitfunction,
			GeoList functionlist, RealMatrix p);

	CasEvaluableFunction getTemplate(Construction cons);

	void setSize(int functionsize);

}
