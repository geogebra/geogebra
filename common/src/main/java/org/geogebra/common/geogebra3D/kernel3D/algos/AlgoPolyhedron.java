package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author ggb3D
 * 
 *         Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedron extends AlgoElement3D {

	/** points generated as output */
	protected OutputHandler<GeoPoint3D> outputPoints;

	protected OutputHandler<GeoPolyhedron> outputPolyhedron;

	protected GeoPolyhedron polyhedron;

	// ///////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	// //////////////////////////////////////////

	protected AlgoPolyhedron(Construction c) {
		super(c);

		cons.addToAlgorithmList(this);

		outputPolyhedron = new OutputHandler<GeoPolyhedron>(
				new elementFactory<GeoPolyhedron>() {
					public GeoPolyhedron newElement() {
						GeoPolyhedron p = new GeoPolyhedron(cons);
						p.setParentAlgorithm(AlgoPolyhedron.this);
						return p;
					}
				});

		outputPolyhedron.adjustOutputSize(1);
		polyhedron = getPolyhedron();

		outputPoints = new OutputHandler<GeoPoint3D>(
				new elementFactory<GeoPoint3D>() {
					public GeoPoint3D newElement() {
						GeoPoint3D p = new GeoPoint3D(cons);
						p.setCoords(0, 0, 0, 1);
						p.setParentAlgorithm(AlgoPolyhedron.this);

						boolean visible = false;
						boolean labelVisible = false;
						int size = outputPoints.size();
						if (size > 0) { // check if at least one element is
										// visible
							for (int i = 0; i < size && !visible
									&& !labelVisible; i++) {
								visible = visible
										|| outputPoints.getElement(i)
												.isEuclidianVisible();
								labelVisible = labelVisible
										|| outputPoints.getElement(i)
												.getLabelVisible();
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

						if (getPolyhedron().getShowObjectCondition() != null) {
							try {
								p.setShowObjectCondition(getPolyhedron()
										.getShowObjectCondition());
							} catch (Exception e) {
								// circular definition
							}
						}

						getPolyhedron().addPointCreated(p);

						return p;
					}
				});

		createOutputPolygons();

		createOutputSegments();

	}

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
		return new OutputHandler<GeoSegment3D>(
				new elementFactory<GeoSegment3D>() {
					public GeoSegment3D newElement() {
						GeoSegment3D s = new GeoSegment3D(cons);
						// s.setParentAlgorithm(AlgoPolyhedron.this);
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
		return new OutputHandler<GeoPolygon3D>(
				new elementFactory<GeoPolygon3D>() {
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons);
						// p.setParentAlgorithm(AlgoPolyhedron.this);
						return p;
					}
				});
	}

	protected void addAlgoToInput() {
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
	}

	abstract protected void updateOutput();

	protected void setOutput() {

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
			for (int j = 0; j < input.length; j++)
				input[j].removeFromUpdateSets(algo);
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

}
