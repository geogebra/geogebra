package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.SetRandomValue;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.GetCommand;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.RandomUtil;

/**
 * Computes a list of random numbers using a uniform distribution.
 * 
 * @author G. Sturr
 * 
 */
public class AlgoRandomUniformList extends AlgoElement implements
		SetRandomValue {

	protected NumberValue a, b, length; // input
	protected GeoList list; // output

	private double[] numberArray, parms;

	public AlgoRandomUniformList(Construction cons, String label,
			NumberValue a, NumberValue b, NumberValue length) {
		super(cons);
		this.a = a;
		this.b = b;
		this.length = length;

		parms = new double[2];

		// output is a list of random numbers
		list = new GeoList(cons);
		cons.addRandomGeo(list);

		setInputOutput(); // for AlgoElement
		compute();
		list.setLabel(label);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.RandomUniform;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = a.toGeoElement();
		input[1] = b.toGeoElement();
		input[2] = length.toGeoElement();

		setOnlyOutput(list);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return list;
	}

	private void ensureListSize(int n) {

		//TODO: is suppress labels needed here?
		boolean oldSuppressLabels = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		if (list.size() < n) {
			for (int i = list.size(); i < n; i++) {
				list.add(new GeoNumeric(cons));
			}

		} else if (list.size() > n) {
			for (int i = n - 1; i < list.size(); i++) {
				list.remove(i);
			}
		}

		cons.setSuppressLabelCreation(oldSuppressLabels);

	}

	@Override
	public void compute() {

		if (!a.isDefined() || !b.isDefined() || !length.isDefined()) {
			list.setUndefined();
			return;
		}
		if(length.getDouble() < 1){
			list.setUndefined();
			return;
		}
		
		ensureListSize((int) length.getDouble());

		for (int i = 0; i < list.size(); i++) {
			((GeoNumeric) list.get(i)).setValue(RandomUtil.randomUniform(a.getDouble(), b.getDouble()));
		}

	}

	public void setRandomValue(double d) {
		if (d >= a.getDouble() && d <= b.getDouble()) {
			// list.setValue(d); ????
			list.updateRepaint();
		}
	}

}
