package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoElement3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;

/**
 * Dependent implicit surface
 * 
 * @author Shamshad Alam
 *
 */
public class AlgoDependentImplicitSurface extends AlgoElement3D {
	private Equation equation;
	private GeoImplicitSurface geoElem;

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
		c.addToConstructionList(this, false);
		this.geoElem = new GeoImplicitSurface(c, equ);
		this.equation = equ;
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

	@SuppressWarnings("unused")
	private void compute(boolean first) {
		geoElem.updateSurface();
	}

	/**
	 * 
	 * @return {@link GeoImplicitSurface}
	 */
	public GeoImplicitSurface getGeo() {
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
