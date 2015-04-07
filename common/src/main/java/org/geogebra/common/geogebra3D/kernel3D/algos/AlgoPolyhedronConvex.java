package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoPolyhedronConvex extends AlgoElement3D {

	private GeoPointND[] pointList;

	protected OutputHandler<GeoPolyhedron> outputPolyhedron;

	protected OutputHandler<GeoSegment3D> outputSegments;
	protected OutputHandler<GeoPolygon3D> outputPolygons;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoPolyhedronConvex(Construction c, String[] labels,
			GeoElement[] pointList) {

		super(c);

		this.pointList = new GeoPointND[pointList.length];
		for (int i = 0; i < pointList.length; i++) {
			this.pointList[i] = (GeoPointND) pointList[i];
		}

		// set input
		input = pointList;

		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		outputPolyhedron = new OutputHandler<GeoPolyhedron>(
				new elementFactory<GeoPolyhedron>() {
					public GeoPolyhedron newElement() {
						GeoPolyhedron p = new GeoPolyhedron(cons);
						p.setParentAlgorithm(AlgoPolyhedronConvex.this);
						return p;
					}
				});

		outputPolyhedron.adjustOutputSize(1);

		outputPolygons = createOutputPolygons();
		outputSegments = createOutputSegments();

		// temporary code (only for 4 points)
		GeoPolyhedron p = getPolyhedron();
		for (int j = 3; j >= 0; j--) {
			p.startNewFace();
			for (int i = 0; i < 4; i++) {
				if (i != j) {
					p.addPointToCurrentFace(this.pointList[i]);
				}
			}
			p.endCurrentFace();
		}

		p.createFaces();

		refreshOutput();

		// set labels
		setLabels(labels);

		update();

		updateOutputSegmentsAndPolygonsParentAlgorithms();
	}

	@Override
	public void compute() {
		// App.debug("compute");
	}

	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedron getPolyhedron() {
		return outputPolyhedron.getElement(0);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Polyhedron;
	}

	private OutputHandler<GeoSegment3D> createOutputSegments() {
		return new OutputHandler<GeoSegment3D>(
				new elementFactory<GeoSegment3D>() {
					public GeoSegment3D newElement() {
						GeoSegment3D s = new GeoSegment3D(cons);
						// s.setParentAlgorithm(AlgoPolyhedron.this);
						return s;
					}
				});
	}

	private OutputHandler<GeoPolygon3D> createOutputPolygons() {
		return new OutputHandler<GeoPolygon3D>(
				new elementFactory<GeoPolygon3D>() {
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons);
						// p.setParentAlgorithm(AlgoPolyhedron.this);
						return p;
					}
				});
	}

	private void setLabels(String[] labels) {

		if (labels == null || labels.length <= 1)
			getPolyhedron().initLabels(labels);
		else {
			getPolyhedron().setAllLabelsAreSet(true);
			for (int i = 0; i < labels.length; i++) {
				getOutput(i).setLabel(labels[i]);
			}
		}

	}

	/**
	 * force update for segments and polygons at creation
	 */
	private void updateOutputSegmentsAndPolygonsParentAlgorithms() {
		outputSegments.updateParentAlgorithm();
		outputPolygons.updateParentAlgorithm();

	}

	/**
	 * 
	 * @param polygon
	 *            polygon
	 * @return 3D coords of all points
	 */
	protected static final Coords[] getPointsCoords(GeoPolygon polygon) {
		int l = polygon.getPointsLength();
		Coords[] points = new Coords[l];
		for (int i = 0; i < l; i++) {
			points[i] = polygon.getPoint3D(i);
		}
		return points;
	}

	private void setUndefined() {
		getPolyhedron().setUndefined();
	}

	/*
	 * @Override public int getRelatedModeID() { return
	 * EuclidianConstants.MODE_NET; }
	 */

}