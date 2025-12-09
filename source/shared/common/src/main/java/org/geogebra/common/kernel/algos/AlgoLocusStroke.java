/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;

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
		if (!points.isEmpty()) {
			poly.appendPointArray(points);
		}

		// updatePointArray already covered compute
		input = new GeoElement[1];

		// dummy to force PolyLine[..., true]
		input[0] = new GeoBoolean(cons, true);

		setInputOutput(); // for AlgoElement
	}

	@Override
	public Commands getClassName() {
		return Commands.PenStroke;
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
		setOnlyOutput(poly);
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
	protected void getExpXML(StringTemplate tpl, XMLStringBuilder sb) {
		sb.startTag("expression", 0);
		if (getOutputLength() == 1) {
			if (getOutput(0).isLabelSet()) {
				sb.attr("label", getOutput(0).getLabel(tpl));
			}
		}
		sb.attrRaw("exp", "PenStroke[]").endTag();
	}

	@Override
	protected boolean hasExpXML(String cmd) {
		return true;
	}

	@Override
	public String getDefinition(StringTemplate tpl) {
		String def = "PenStroke";
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
		poly.appendPoints(sbAE);
		sbAE.append(tpl.rightSquareBracket());
		return sbAE.toString();
	}

	@Override
	public boolean setFrom(AlgoElement other) {
		if (other instanceof AlgoLocusStroke) {
			GeoLocusStroke otherStroke = ((AlgoLocusStroke) other).poly;
			poly.set(otherStroke);
			poly.updateRepaint();
			return true;
		}
		return false;
	}
}
