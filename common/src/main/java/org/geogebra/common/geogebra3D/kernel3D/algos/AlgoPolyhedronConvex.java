package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.quickhull3d.Point3d;
import org.geogebra.common.geogebra3D.quickhull3d.QuickHull3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionElementCycle;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

public class AlgoPolyhedronConvex extends AlgoElement3D {

	private GeoPointND[] pointList;

	protected OutputHandler<GeoPolyhedron> outputPolyhedron;

	protected OutputHandler<GeoSegment3D> outputSegments;
	protected OutputHandler<GeoPolygon3D> outputPolygons;

	// convex hull stuff
	private Point3d[] point3dList;
	private QuickHull3D quickHull3D;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoPolyhedronConvex(Construction c, String[] labels,
			GeoElement[] pointList) {

		super(c);

		this.pointList = new GeoPointND[pointList.length];
		point3dList = new Point3d[pointList.length];
		for (int i = 0; i < pointList.length; i++) {
			this.pointList[i] = (GeoPointND) pointList[i];
			point3dList[i] = new Point3d();
		}
		quickHull3D = new QuickHull3D();

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

		// temporary code
		updateHull();

		GeoPolyhedron p = getPolyhedron();

		int[][] faceIndices = quickHull3D.getFaces(QuickHull3D.POINT_RELATIVE
				| QuickHull3D.CLOCKWISE);
		for (int i = 0; i < faceIndices.length; i++) {
			p.startNewFace();
			for (int k = 0; k < faceIndices[i].length; k++) {
				int index = faceIndices[i][k];
				System.out.print(index + " ");
				p.addPointToCurrentFace(this.pointList[index]);
			}
			p.endCurrentFace();
			System.out.println("");
		}

		p.createFaces();

		refreshOutput();

		// set labels
		setLabels(labels);

		update();

		updateOutputSegmentsAndPolygonsParentAlgorithms();
	}

	private void updateHull() {
		for (int i = 0; i < pointList.length; i++) {
			Coords coords = pointList[i].getInhomCoordsInD3();
			point3dList[i].set(coords.getX(), coords.getY(), coords.getZ());
		}

		quickHull3D.build(point3dList);
	}

	@Override
	public void compute() {
		updateHull();

		GeoPolyhedron p = getPolyhedron();

		ArrayList<ConstructionElementCycle> newFaces = new ArrayList<ConstructionElementCycle>();
		TreeSet<Integer> availableIndices = new TreeSet<Integer>();
		availableIndices.addAll(p.getPolygonsIndices());

		int[][] faceIndices = quickHull3D.getFaces(QuickHull3D.POINT_RELATIVE
				| QuickHull3D.CLOCKWISE);
		for (int i = 0; i < faceIndices.length; i++) {
			p.startNewFace();
			for (int k = 0; k < faceIndices[i].length; k++) {
				int index = faceIndices[i][k];
				p.addPointToCurrentFace(this.pointList[index]);
			}

			Integer index = p.getCurrentFaceIndex();
			if (index == null) {
				newFaces.add(p.getCurrentFace());
			} else {
				availableIndices.remove(index);
			}
		}

		String s = "\nnew faces:";
		for (ConstructionElementCycle face : newFaces) {
			s += "\n   " + face;
		}
		s += "\navailable indices:";
		for (Integer index : availableIndices) {
			s += " " + index;
		}
		Log.debug(s);

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


	private void setUndefined() {
		getPolyhedron().setUndefined();
	}

	/*
	 * @Override public int getRelatedModeID() { return
	 * EuclidianConstants.MODE_NET; }
	 */

}