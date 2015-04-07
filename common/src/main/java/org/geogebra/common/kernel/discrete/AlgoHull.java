/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;
import java.util.Collection;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VPoint;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.VoronoiAlgorithm;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.AbstractRepresentation;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.RepresentationFactory;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.RepresentationInterface;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.representation.triangulation.TriangulationRepresentation;
import org.geogebra.common.kernel.discrete.signalprocessor.voronoi.statusstructure.VLinkedNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Mode of a list. Adapted from AlgoMode
 * 
 * @author Michael Borcherds
 * @version
 */

public class AlgoHull extends AlgoElement  implements GraphAlgo {

	
	protected GeoList inputList; // input
	private GeoNumeric percentage; // input
	protected GeoLocus locus; // output
	protected ArrayList<MyPoint> al;
	protected ArrayList<VPoint> vl;
	protected int size;

	public AlgoHull(Construction cons, String label, GeoList inputList,
			GeoNumeric percentage) {
		super(cons);
		this.inputList = inputList;
		this.percentage = percentage;

		locus = new GeoLocus(cons);

		setInputOutput();
		compute();
		locus.setLabel(label);
	}

	public Commands getClassName() {
		return Commands.Hull;
	}

	protected void setInputOutput() {
		input = new GeoElement[percentage == null ? 1 : 2];
		input[0] = inputList;
		if (percentage != null)
			input[1] = percentage;

		setOnlyOutput(locus);
		setDependencies(); // done by AlgoElement
	}

	public GeoLocus getResult() {
		return locus;
	}

	public void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			locus.setUndefined();
			return;
		}

		if (vl == null)
			vl = new ArrayList<VPoint>();
		else
			vl.clear();

		double inhom[] = new double[2];

		TriangulationRepresentation.CalcCutOff calccutoff = new TriangulationRepresentation.CalcCutOff() {
			public int calculateCutOff(TriangulationRepresentation rep) {
				// Get variables
				double percent = (percentage == null || !percentage.isDefined()) ? 1
						: percentage.getDouble();

				if (percent < 0)
					percent = 0;
				else if (percent > 1)
					percent = 1;

				double min = rep.getMinLength();
				double max = rep.getMaxLength();

				// Calculate normalised length based off percentage
				int val = (int) (percent * (max - min) + min);

				return val;
			}
		};

		AbstractRepresentation representation;
		representation = RepresentationFactory
				.createTriangulationRepresentation();

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND) geo;
				p.getInhomCoords(inhom);
				vl.add(representation.createPoint(inhom[0], inhom[1]));
			}
		}

		representation = RepresentationFactory
				.createTriangulationRepresentation();

		TriangulationRepresentation trianglarrep = (TriangulationRepresentation) representation;
		trianglarrep.setCalcCutOff(calccutoff);

		TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
		representationwrapper.innerrepresentation = representation;

		VoronoiAlgorithm.generateVoronoi(representationwrapper, vl);

		ArrayList<VPoint> edge = ((TriangulationRepresentation) representation)
				.getPointsFormingOutterBoundary();
		if (edge == null) {
			locus.setUndefined();
			return;
		}
		if (al == null)
			al = new ArrayList<MyPoint>();
		else
			al.clear();
		for (int i = 0; i < edge.size(); i++) {
			VPoint p = edge.get(i);
			al.add(new MyPoint(p.x, p.y, i != 0));

		}

		locus.setPoints(al);
		locus.setDefined(true);

	}

	public class TestRepresentationWrapper implements RepresentationInterface {

		/* ***************************************************** */
		// Variables

		private final ArrayList<VPoint> circleevents = new ArrayList<VPoint>();

		RepresentationInterface innerrepresentation = null;

		/* ***************************************************** */
		// Data/Representation Interface Method

		// Executed before the algorithm begins to process (can be used to
		// initialise any data structures required)
		public void beginAlgorithm(Collection<VPoint> points) {
			// Reset the triangle array list
			circleevents.clear();

			// Call the inner representation
			if (innerrepresentation != null) {
				innerrepresentation.beginAlgorithm(points);
			}
		}

		// Called to record that a vertex has been found
		public void siteEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3) {
			// Call the inner representation
			if (innerrepresentation != null) {
				innerrepresentation.siteEvent(n1, n2, n3);
			}
		}

		public void circleEvent(VLinkedNode n1, VLinkedNode n2, VLinkedNode n3,
				int circle_x, int circle_y) {
			// Add the circle event
			circleevents.add(new VPoint(circle_x, circle_y));

			// Call the inner representation
			if (innerrepresentation != null) {
				innerrepresentation.circleEvent(n1, n2, n3, circle_x, circle_y);
			}
		}

		// Called when the algorithm has finished processing
		public void endAlgorithm(Collection<VPoint> points,
				double lastsweeplineposition, VLinkedNode headnode) {
			// Call the inner representation
			if (innerrepresentation != null) {
				innerrepresentation.endAlgorithm(points, lastsweeplineposition,
						headnode);
			}
		}

		/* ***************************************************** */
	}

	// TODO Consider locusequability

}
