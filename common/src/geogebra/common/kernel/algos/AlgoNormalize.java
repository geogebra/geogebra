
package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Normalize the input numbers or 2D points from [0,1]
 *
 * @author d33Z
 * @version
 */

public class AlgoNormalize extends AlgoElement {

	private GeoList geoList; // input

	private GeoList normList; // output

	private GeoNumeric normValue = new GeoNumeric(cons);


	public AlgoNormalize(Construction cons, GeoList geoList) {
		super(cons);
		this.geoList = geoList;
		normList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Normalize;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = geoList;

		setOutputLength(1);
		setOutput(0, normList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getNormList() {
		return normList;
	}

	@Override
	public final void compute() {
		int size = geoList.size();
		if (!geoList.isDefined() || size == 0) {
			normList.setUndefined();
			return;
		}

		// normnum[i] = (num[i] - num[0])/(num[l-1]-num[0]);

		GeoElement geoF = geoList.get(0);
		GeoElement geoL = geoList.get(size - 1);
		double nF = ((NumberValue) geoF).getDouble(); // First number in list.
		double nL = ((NumberValue) geoL).getDouble(); // Last number in list.

		for (int i = 0; i < size; i++) {
			GeoElement geo = geoList.get(i);

			if (geo instanceof NumberValue) {
				NumberValue num = (NumberValue) geo;
				double n = num.getDouble();
				double normnum = (n - nF) / (nL - nF);
				normList.add(new GeoNumeric(cons, normnum));

			} else {
				normList.setUndefined();
				return;
			}
		}
	}

	// TODO Consider locusequability

}
