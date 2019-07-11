package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * @author ggb3D
 * 
 *         Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedron extends AlgoElement3D {

	/** points generated as output */
	protected OutputHandler<GeoPoint3D> outputPoints;
	/** output handler */
	protected OutputHandler<GeoPolyhedron> outputPolyhedron;
	/** output polyhedron */
	protected GeoPolyhedron polyhedron;

	// ///////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	// //////////////////////////////////////////

	/**
	 * @param c
	 *            construction
	 */
	protected AlgoPolyhedron(Construction c) {
		super(c);
	}

	protected void init() {
		cons.addToAlgorithmList(this);

		outputPolyhedron = new OutputHandler<>(
				new ElementFactory<GeoPolyhedron>() {
					@Override
					public GeoPolyhedron newElement() {
						GeoPolyhedron p = new GeoPolyhedron(cons,
								getPolyhedronType());
						p.setParentAlgorithm(AlgoPolyhedron.this);
						return p;
					}
				});

		outputPolyhedron.adjustOutputSize(1);
		polyhedron = getPolyhedron();
		polyhedron.setChildrenLabelsSet(true);

		outputPoints = createOutputPointsHandler();
		createOutputPolygons();

		createOutputSegments();

	}

	/**
	 * 
	 * @return type for the output polyhedron
	 */
	abstract protected int getPolyhedronType();

	/**
	 * 
	 * @return true if no input point or if first input point is visible
	 */
	abstract protected boolean isFirstInputPointVisible();

	/**
	 * 
	 * @return true if no input point or if first input point has label visible
	 */
	abstract protected boolean isFirstInputPointLabelVisible();

	/**
	 * create the faces of the polyhedron
	 */
	protected void createFaces() {
		polyhedron.createFaces();
	}

	/**
	 * create the output segments handlers
	 */
	abstract protected void createOutputSegments();

	/**
	 * @return an output handler for segments
	 */
	protected OutputHandler<GeoSegment3D> createOutputSegmentsHandler() {
		return new OutputHandler<>(
				new ElementFactory<GeoSegment3D>() {
					@Override
					public GeoSegment3D newElement() {
						GeoSegment3D s = new GeoSegment3D(cons);
						s.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
						return s;
					}
				});
	}

	/**
	 * create the output polygons handlers
	 */
	abstract protected void createOutputPolygons();

	/**
	 * @return an output handler for polygons
	 */
	protected OutputHandler<GeoPolygon3D> createOutputPolygonsHandler() {
		return new OutputHandler<>(
				new ElementFactory<GeoPolygon3D>() {
					@Override
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons);
						p.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
						return p;
					}
				});
	}

	/**
	 * Add this to dependency set of all inputs
	 */
	protected void addAlgoToInput() {
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
	}

	/**
	 * Update output dependencies
	 */
	abstract protected void updateOutput();

	/**
	 * Alias for updateOutput TODO only use one
	 */
	protected final void setOutput() {
		updateOutput();
		// cons.addToAlgorithmList(this);
	}

	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedron getPolyhedron() {
		return outputPolyhedron.getElement(0);
	}

	@Override
	public void removeOutputExcept(GeoElement keepGeo) {
		for (int i = 0; i < super.getOutputLength(); i++) {
			GeoElement geo = super.getOutput(i);
			if (geo != keepGeo) {
				if (geo.isGeoPoint()) {
					removePoint(geo);
				} else {
					geo.doRemove();
				}
			}
		}
	}

	private void removePoint(GeoElement oldPoint) {

		// remove dependent algorithms (e.g. segments) from update sets of
		// objects further up (e.g. polygon) the tree
		ArrayList<AlgoElement> algoList = oldPoint.getAlgorithmList();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = algoList.get(k);
			for (int j = 0; j < input.length; j++) {
				input[j].removeFromUpdateSets(algo);
			}
		}

		// remove old point
		oldPoint.setParentAlgorithm(null);

		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well
		GeoPolyhedron poly = getPolyhedron();
		for (int k = 0; k < algoList.size(); k++) {
			AlgoElement algo = algoList.get(k);
			// make sure we don't remove the polygon as well
			if (algo instanceof AlgoJoinPoints3D
					&& ((AlgoJoinPoints3D) algo).getPoly() == poly) {
				continue;
			} else if (algo instanceof AlgoPolygon3D
					&& ((AlgoPolygon3D) algo).getPolyhedron() == poly) {
				continue;
			}
			algo.remove();

		}

		algoList.clear();
		// remove point
		oldPoint.doRemove();

	}

	@Override
	protected void updateDependentGeos() {
		getPolyhedron().update();
	}

	/**
	 * @return an output handler for points
	 */
	protected OutputHandler<GeoPoint3D> createOutputPointsHandler() {
		return new OutputHandler<>(new PointFactory());
	}

	/**
	 * Creates 3D points, sets visibility flags
	 *
	 */
	protected class PointFactory implements ElementFactory<GeoPoint3D> {
		@Override
		public GeoPoint3D newElement() {
			GeoPoint3D p = new GeoPoint3D(cons);
			p.setCoords(0, 0, 0, 1);
			p.setParentAlgorithm(AlgoPolyhedron.this);

			boolean visible = false;
			boolean labelVisible = false;
			int size = outputPoints.size();
			if (size > 0) { // check if at least one element is
							// visible
				for (int i = 0; i < size && !visible && !labelVisible; i++) {
					visible = visible
							|| outputPoints.getElement(i).isEuclidianVisible();
					labelVisible = labelVisible
							|| outputPoints.getElement(i).getLabelVisible();
				}
			} else { // no element yet
				visible = isFirstInputPointVisible();
				labelVisible = isFirstInputPointLabelVisible();
			}

			p.setEuclidianVisible(visible);
			if (!visible) { // if not visible, we don't want
							// setParentAlgorithm() to change it
				p.dontSetEuclidianVisibleBySetParentAlgorithm();
			}
			p.setLabelVisible(labelVisible);
			p.setAuxiliaryObject(Auxiliary.YES_DEFAULT);

			if (getPolyhedron().getShowObjectCondition() != null) {
				try {
					p.setShowObjectCondition(
							getPolyhedron().getShowObjectCondition());
				} catch (Exception e) {
					// circular definition
				}
			}

			getPolyhedron().addPointCreated(p);

			return p;
		}
	}

	@Override
	protected void getOutputXML(StringBuilder sb) {
		super.getOutputXML(sb);
		
		// append XML for polygon and segments linked once more, to avoid
		// override of specific properties
		for (GeoPolygon polygon : polyhedron.getPolygonsLinked()) {
			if (polygon.isLabelSet()) {
				polygon.getXML(false, sb);
			}
		}
		for (GeoSegmentND segment : polyhedron.getSegmentsLinked()) {
			if (segment.isLabelSet()) {
				((GeoElement) segment).getXML(false, sb);
			}
		}
	}

}
