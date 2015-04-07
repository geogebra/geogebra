package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoTransferFunction;

/**
 * Algo class for Nyquist diagram
 * 
 * @author Giuliano
 * 
 */
public class AlgoTransferFunction extends AlgoElement {

	private GeoTransferFunction gcf;

	private GeoNumberValue omegaStart;
	private GeoList num;
	private GeoList den;

	private GeoFunction function;

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param num
	 *            list of coefficients of numerator
	 * @param den
	 *            list of coefficients of denominator
	 * @param omegaStart
	 *            value of omega for the interval [-omega;omega]
	 * 
	 * @param step
	 *            step for calculus of function
	 */
	public AlgoTransferFunction(Construction c, String label, GeoList num,
			GeoList den, GeoNumberValue omegaStart) {
		super(c);
		this.omegaStart = omegaStart;

		gcf = new GeoTransferFunction(c, label, num, den,
				(int) this.omegaStart.getDouble());
		this.function = gcf.getGeoFunction();
		this.num = num;
		this.den = den;
		setInputOutput();
		compute();
		gcf.setLabel(label);
	}

	public AlgoTransferFunction(Construction c, String label, GeoList num,
			GeoList den) {
		super(c);
		gcf = new GeoTransferFunction(c, label, num, den, 10);
		this.function = gcf.getGeoFunction();
		this.num = num;
		this.den = den;
		setInputOutput();
		compute();
		gcf.setLabel(label);
	}

	@Override
	protected void setInputOutput() {

		setOnlyOutput(gcf);

		if (omegaStart != null) {
			input = new GeoElement[3];
			input[2] = omegaStart.toGeoElement();
		} else {
			input = new GeoElement[2];
		}
		input[0] = num;
		input[1] = den;

		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return GeoFunction if Nyquist, GeoCanvasImage if Bode
	 */
	public GeoElement getResult() {
		return getOutput(0);
	}

	@Override
	public void compute() {
		if (gcf.isDefined()) {
			gcf.evaluate();
		}

	}

	@Override
	public GetCommand getClassName() {
		return Commands.Nyquist;
	}

}
