/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;

/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author Michael Borcherds
 */
public class AlgoLocusStroke extends AlgoElement {

	/** output */
	private GeoLocusStroke poly;

	/**
	 * @param cons
	 *            the construction
	 * @param points
	 *            vertices of the polygon
	 */
	public AlgoLocusStroke(Construction cons, ArrayList<MyPoint> points) {
		super(cons);
		poly = new GeoLocusStroke(this.cons);
		poly.appendPointArray(points);

		// updatePointArray already covered compute
		input = new GeoElement[1];

		// dummy to force PolyLine[..., true]
		input[0] = new GeoBoolean(cons, true);

		setInputOutput(); // for AlgoElement
	}

	@Override
	public Commands getClassName() {
		return Commands.PolyLine;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POLYLINE;
	}

	public GeoLocusStroke getPenStroke() {
		return poly;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// set dependencies
		for (GeoElement geoElement : input) {
			geoElement.addAlgorithm(this);
		}

		// set output
		setOutputLength(1);
		setOutput(0, poly);
		setDependencies();
	}

	@Override
	public void update() {
		// compute output from input
		getOutput(0).update();
	}

	@Override
	public void compute() {
		// no recomputation needed
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void getExpXML(StringTemplate tpl, StringBuilder sb) {
		sb.append("<expression");
		if (/* output != null && */getOutputLength() == 1) {
			if (getOutput(0).isLabelSet()) {
				sb.append(" label=\"");
				StringUtil.encodeXML(sb, getOutput(0).getLabel(tpl));
				sb.append("\"");
			}
		}
		StringBuilder xmlPoints = poly.getXMLPointBuilder();
		if (xmlPoints == null) {
			xmlPoints = new StringBuilder();
			// add expression
			xmlPoints.append(" exp=\"PolyLine[");
			appendPoints(xmlPoints);
			xmlPoints.append("]\" />\n");
		}
		poly.setXMLPointBuilder(xmlPoints);
		sb.append(xmlPoints);
	}

	private void appendPoints(final StringBuilder sb) {
		final ScientificFormatAdapter formatter = FormatFactory.getPrototype()
				.getFastScientificFormat(5);
		poly.processPointsWithoutControl(new AsyncOperation<MyPoint>() {

				@Override
				public void callback(MyPoint m) {
					sb.append("(");
					sb.append(formatter.format(m.getX()));
					sb.append(",");
					sb.append(formatter.format(m.getY()));
					sb.append("), ");
				}
		});
		sb.append("true");
	}

	@Override
	protected boolean hasExpXML(String cmd) {
		return true;
	}

	@Override
	public String getDefinition(StringTemplate tpl) {
		String def = "PolyLine";
		// #2706
		if (input == null) {
			return null;
		}
		sbAE.setLength(0);
		if (tpl.isPrintLocalizedCommandNames()) {
			sbAE.append(getLoc().getCommand(def));
		} else {
			sbAE.append(def);
		}

		sbAE.append(tpl.leftSquareBracket());
		// input legth is 0 for ConstructionStep[]
		
		appendPoints(sbAE);
		sbAE.append(tpl.rightSquareBracket());
		return sbAE.toString();
	}
}
