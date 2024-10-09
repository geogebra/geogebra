package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Computes a list of random numbers using a uniform distribution.
 * 
 * @author G. Sturr
 * 
 */
public class AlgoRandomUniformList extends AlgoElement
		implements SetRandomValue {
	// input
	private GeoNumberValue a;
	private GeoNumberValue b;
	private GeoNumberValue length;
	// output
	private GeoList list;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            lower bound for uniform distribution
	 * @param b
	 *            upper bound for uniform distribution
	 * @param length
	 *            list length
	 */
	public AlgoRandomUniformList(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue length) {
		super(cons);
		this.a = a;
		this.b = b;
		this.length = length;

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

	/**
	 * @param n - size of list
	 */
	public void ensureListSize(int n) {
		// TODO: is suppress labels needed here?
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
		if (length.getDouble() < 1) {
			list.setUndefined();
			return;
		}

		ensureListSize((int) length.getDouble());

		for (int i = 0; i < list.size(); i++) {
			((GeoNumeric) list.get(i)).setValue(getRandomNumber(a.getDouble(),
					b.getDouble()));
		}
	}

	/**
	 * @param a - low
	 * @param b - high
	 * @return random number between a and b
	 */
	public double getRandomNumber(double a, double b) {
		return cons.getApplication().randomUniform(a, b);
	}

	@Override
	public boolean setRandomValue(GeoElementND d) {
		if (d instanceof ListValue) {
			ListValue lv = (ListValue) d;
			int size = Math.min(list.size(), lv.size());
			for (int i = 0; i < size; i++) {
				((GeoNumeric) list.get(i)).setValue(Math.max(a.getDouble(),
						Math.min(lv.get(i).evaluateDouble(), b.getDouble())));
			}
			return true;
		}
		return false;
	}

}
