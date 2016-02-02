package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Dependent implicit surface
 * 
 * @author Shamshad Alam
 *
 */
public class AlgoDependentImplicitSurface extends AlgoElement3D {
	private Equation equation;
	private GeoElement geoElem;
	private ExpressionValue[] ev = new ExpressionValue[10];

	/**
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param equ
	 *            equation
	 * @param simplify
	 *            simplify the equation
	 */
	public AlgoDependentImplicitSurface(Construction c, String label,
			Equation equ, boolean simplify) {
		super(c, false);
		equation = equ;

		c.addToConstructionList(this, false);

		switch (equ.preferredDegree()) {
		// linear equation -> LINE
		case 1:
			geoElem = new GeoPlane3D(c);
			break;
		// quadratic equation -> CONIC
		case 2:
			geoElem = new GeoQuadric3D(c);
			break;
		default:
			geoElem = new GeoImplicitSurface(c);
		}

		geoElem.setDefinition(equ.wrap());
		setInputOutput(); // for AlgoElement

		compute(true);


		geoElem.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		if (input == null) {
			input = equation.getGeoElementVariables();
		}

		if (getOutputLength() == 0) {
			setOutputLength(1);
		}

		setOutput(0, geoElem);
		setDependencies();
	}

	@Override
	public void compute() {
		compute(false);
	}

	@Override
	public GetCommand getClassName() {
		return Algos.Expression;
	}

	private void compute(boolean first) {
		if (!first
				&& (equation.hasVariableDegree() || equation
						.isFunctionDependent())) {
			equation.initEquation();
		}
		int degree = equation.preferredDegree();
		App.debug(degree + "");
		switch (degree) {
		// linear equation -> LINE
		case 1:
			if (geoElem instanceof GeoPlane3D) {
				setPlane();
			} else {
				if (geoElem.hasChildren())
					geoElem.setUndefined();
				else {
					replaceGeoElement(new GeoLine(getConstruction()));
					setPlane();
				}
			}
			break;
		// quadratic equation -> CONIC
		case 2:
			if (geoElem instanceof GeoQuadric3D) {
				setQuadric();
			} else {
				if (geoElem.hasChildren())
					geoElem.setUndefined();
				else {
					replaceGeoElement(new GeoConic(getConstruction()));
					setQuadric();
				}
			}
			break;
		default:
			if (geoElem instanceof GeoImplicit) {
				((GeoImplicitSurface) geoElem).updateSurface();
			} else {
				if (geoElem.hasChildren())
					geoElem.setUndefined();
				else {
					replaceGeoElement(new GeoImplicitSurface(getConstruction()));
					((GeoImplicitSurface) geoElem).fromEquation(equation);
				}
			}

		}


	}

	private void setQuadric() {
		double[] coeffs = new double[10];
		Polynomial lhs = equation.getNormalForm();
		ev[0] = lhs.getCoefficient("xx");
		ev[1] = lhs.getCoefficient("yy");
		ev[2] = lhs.getCoefficient("zz");
		ev[3] = lhs.getConstantCoefficient();

		// further will be divided by 2
		ev[4] = lhs.getCoefficient("xy");
		ev[5] = lhs.getCoefficient("xz");
		ev[6] = lhs.getCoefficient("yz");
		ev[7] = lhs.getCoefficient("x");
		ev[8] = lhs.getCoefficient("y");
		ev[9] = lhs.getCoefficient("z");

		for (int i = 0; i < 4; i++) {
			coeffs[i] = ev[i].evaluateDouble();
		}
		for (int i = 4; i < 10; i++) {
			coeffs[i] = ev[i].evaluateDouble() / 2;
		}

		((GeoQuadric3D) geoElem).setMatrix(coeffs);

	}

	private void setPlane() {
		double a, b, c, d;

		Polynomial lhs = equation.getNormalForm();
		a = lhs.getCoeffValue("x");
		b = lhs.getCoeffValue("y");
		c = lhs.getCoeffValue("z");
		d = lhs.getCoeffValue("");
		Log.debug(new double[] { a, b, c, d });
		((GeoPlane3D) geoElem).setEquation(a, b, c, d);
		// ((GeoPlane3D) geoElem).upd

	}

	protected void replaceGeoElement(GeoElementND newElem) {
		String label = geoElem.getLabelSimple();
		geoElem.doRemove();
		geoElem = newElem.toGeoElement();
		setInputOutput();
		geoElem.setLabel(label);
	}

	/**
	 * 
	 * @return {@link GeoImplicitSurface}
	 */
	public GeoElement getGeo() {
		return geoElem;
	}

	/**
	 * 
	 * @return {@link Equation}
	 */
	public Equation getEquation() {
		return equation;
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return equation.toString(tpl);
	}

	@Override
	protected String toExpString(StringTemplate tpl) {
		return geoElem.getLabel(tpl) + ": " + equation.toString(tpl);
	}

}
