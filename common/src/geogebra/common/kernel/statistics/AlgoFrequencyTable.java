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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;

import java.util.ArrayList;

/**
 * FrequencyTable[] algorithm based on AlgoFrequency
 * 
 * @author Zbynek Konecny
 * 
 */
public class AlgoFrequencyTable extends AlgoElement {

	private GeoList dataList; // input
	private GeoList classList; // input
	private GeoBoolean isCumulative; // input
	private GeoBoolean useDensity; // input
	private GeoNumeric density; // input

	// private GeoList frequency; //output
	private GeoText table; // output
	// for compute
	private AlgoFrequency freq;
	private StringBuilder sb = new StringBuilder();

	public AlgoFrequencyTable(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList) {
		this(cons, label, isCumulative, classList, dataList, null, null);
	}

	public AlgoFrequencyTable(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric density) {
		super(cons);

		this.classList = classList;
		this.dataList = dataList;
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;
		freq = new AlgoFrequency(cons, isCumulative, classList, dataList,
				useDensity, density);
		cons.removeFromConstructionList(freq);
		table = new GeoText(cons);
		setInputOutput();
		compute();
		table.isTextCommand = true;
		table.setLaTeX(true, false);
		table.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.FrequencyTable;
	}

	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

		if (isCumulative != null)
			tempList.add(isCumulative);

		if (classList != null)
			tempList.add(classList);

		tempList.add(dataList);

		if (useDensity != null)
			tempList.add(useDensity);

		if (density != null)
			tempList.add(density);

		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);

		setOutputLength(1);
		setOutput(0, table);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return table;
	}

	@Override
	public final void compute() {

		// Validate input arguments
		// =======================================================

		if (!freq.getResult().isDefined()) {
			table.setUndefined();
			return;
		}

		boolean useDens = useDensity != null && useDensity.getBoolean();

		// If classList does not exist,
		// get the unique value list and compute frequencies for this list
		// =======================================================
		sb.setLength(0);
		GeoList fr = freq.getResult();
		sb.append("\\begin{array}{c|c}");
		int length = fr.size();
		if (classList == null) {
			GeoList va = freq.getValue();
			sb.append(app.getMenu("Value"));
			sb.append("&");
			sb.append(useDens ? app
					.getMenu("Frequency") : app.getMenu("Count"));
			sb.append(" \\\\\\hline ");
			for (int i = 0; i < length; i++) {
				sb.append(va.get(i).toValueString(table.getStringTemplate()));
				sb.append("&");
				sb.append(fr.get(i).toValueString(table.getStringTemplate()));
				sb.append("\\\\");
			}

		}
		// If classList exists, compute frequencies using the classList
		// =======================================================

		else {
			sb.append(app.getMenu("Interval"));
			sb.append("&");
			sb.append(useDens ? app
					.getMenu("Frequency") : app.getMenu("Count"));
			sb.append(" \\\\\\hline ");
			for (int i = 0; i < length; i++) {
				sb.append(classList.get(i).toValueString(table.getStringTemplate()));
				sb.append("\\text{ -- }");
				sb.append(classList.get(i + 1).toValueString(table.getStringTemplate()));
				sb.append("&");
				sb.append(fr.get(i).toValueString(table.getStringTemplate()));
				sb.append("\\\\");
			}
		}
		sb.append("\\end{array}");
		table.setTextString(sb.toString());

	}

	// TODO Consider locusequability

}
