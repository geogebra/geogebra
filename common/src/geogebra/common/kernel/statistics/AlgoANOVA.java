/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import java.util.ArrayList;

import org.apache.commons.math.stat.inference.OneWayAnovaImpl;

/**
 * Performs a one way ANOVA test.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoANOVA extends AlgoElement {

	private GeoList geoList; // input
	private GeoList result; // output

	private ArrayList<double[]> categoryData;
	private double p, testStat;
	private OneWayAnovaImpl anovaImpl;

	/**
	 * @param cons construction
	 * @param label label
	 * @param geoList list of lists of values
	 */
	public AlgoANOVA(Construction cons, String label, GeoList geoList) {
		super(cons);
		this.geoList = geoList;
		result = new GeoList(cons);
		setInputOutput(); // for AlgoElement

		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ANOVA;
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[1];
		input[0] = geoList;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting list of Pvalues and Fvalues
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {

		int size = geoList.size();
		// System.out.println(geoList.toOutputValueString());
		// exit if less than two data lists
		if (size < 2) {
			result.setUndefined();
			return;
		}

		// exit if data lists are not defined or have less than two values
		for (int index = 0; index < size; index++) {

			if (!geoList.get(index).isDefined()
					|| !geoList.get(index).isGeoList()
					|| ((GeoList) geoList.get(index)).size() < 2) {
				result.setUndefined();
				return;
			}
		}

		// create an array list of data arrays
		if (categoryData == null) {
			categoryData = new ArrayList<double[]>();
		} else {
			categoryData.clear();
		}

		// load the data arrays from the input GeoList
		GeoList list;
		for (int index = 0; index < size; index++) {

			list = (GeoList) geoList.get(index);
			double[] val = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {
				GeoElement geo = list.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					val[i] = num.getDouble();
				} else {
					result.setUndefined();
					return;
				}
			}
			categoryData.add(val);
		}

		try {

			// get the test statistic and p value
			if (anovaImpl == null)
				anovaImpl = new OneWayAnovaImpl();
			p = anovaImpl.anovaPValue(categoryData);
			testStat = anovaImpl.anovaFValue(categoryData);

			// put these results into the output list
			result.clear();
			result.add(new GeoNumeric(cons, p));
			result.add(new GeoNumeric(cons, testStat));

		} catch (Exception e) {
			result.setUndefined();
			e.printStackTrace();
		}

	}

	// TODO Consider locusequability

}
