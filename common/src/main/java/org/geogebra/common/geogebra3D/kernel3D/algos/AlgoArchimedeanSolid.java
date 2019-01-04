package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron.DummyGeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.geogebra3D.kernel3D.solid.PlatonicSolid;
import org.geogebra.common.geogebra3D.kernel3D.solid.PlatonicSolidsFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author ggb3D
 * 
 *         Creates a new GeoPolyhedron
 *
 */
public class AlgoArchimedeanSolid extends AlgoPolyhedron {

	private OutputHandler<GeoPolygon3D> outputPolygons;
	private OutputHandler<GeoSegment3D> outputSegments;

	private GeoPointND A;
	private GeoPointND B;
	private GeoDirectionND v;

	private GeoPolygon polygon;
	// private GeoBoolean isDirect;

	private int inputPointsCount;

	private CoordMatrix4x4 matrix;

	private Coords[] coords;

	private Commands name;

	private PlatonicSolid solidDescription;
	/**
	 * factor to calculate the volume
	 */
	private double volumeFactor;

	/**
	 * factor to calculate the height
	 */
	private double heightFactor;

	/**
	 * factor to calculate the area
	 */
	private double areaFactor;

	private Coords tmpCoords = Coords.createInhomCoorsInD3();

	private boolean polyhedronIsDummy;

	/**
	 * creates an archimedean solid
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param A
	 *            vertex
	 * @param B
	 *            vertex
	 * @param v
	 *            direction
	 * @param name
	 *            solid type
	 */
	public AlgoArchimedeanSolid(Construction c, String[] labels, GeoPointND A,
			GeoPointND B, GeoDirectionND v, Commands name) {
		this(c, name);

		this.A = A;
		this.B = B;
		this.v = v;
		inputPointsCount = 2;

		initInputOutput(labels, (GeoElement) A, (GeoElement) B, (GeoElement) v);
	}

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param poly
	 *            polygon for basis
	 * @param isDirect
	 *            if is direct
	 * @param name
	 *            solid type
	 */
	public AlgoArchimedeanSolid(Construction c, String[] labels, GeoPolygon poly,
			GeoBoolean isDirect, Commands name) {
		this(c, name);

		this.polygon = poly;
		// this.isDirect = isDirect;
		switch (name) {
		default:
		case Tetrahedron:
			inputPointsCount = 3;
			break;
		case Cube:
			inputPointsCount = 4;
			break;
		case Octahedron:
			inputPointsCount = 3;
			break;
		case Dodecahedron:
			inputPointsCount = 5;
			break;
		case Icosahedron:
			inputPointsCount = 5;
			break;
		}

		initInputOutput(labels, poly, isDirect);
	}

	private AlgoArchimedeanSolid(Construction c, Commands name) {
		super(c);
		this.name = name;

		// set polyhedron type
		switch (name) {
		default:
		case Tetrahedron:
			polyhedron.setType(GeoPolyhedron.TYPE_TETRAHEDRON);
			solidDescription = PlatonicSolidsFactory.getTetrahedron();
			break;
		case Cube:
			polyhedron.setType(GeoPolyhedron.TYPE_CUBE);
			solidDescription = PlatonicSolidsFactory.getCube();
			break;
		case Octahedron:
			polyhedron.setType(GeoPolyhedron.TYPE_OCTAHEDRON);
			solidDescription = PlatonicSolidsFactory.getOctahedron();
			break;
		case Dodecahedron:
			polyhedron.setType(GeoPolyhedron.TYPE_DODECAHEDRON);
			solidDescription = PlatonicSolidsFactory.getDodecahedron();
			break;
		case Icosahedron:
			polyhedron.setType(GeoPolyhedron.TYPE_ICOSAHEDRON);
			solidDescription = PlatonicSolidsFactory.getIcosahedron();
			break;
		}

		setVolumeAreaAndHeightFactors();

		matrix = new CoordMatrix4x4();
	}

	private void initInputOutput(String[] labels, GeoElement... elements) {
		// input
		setInput(elements);
		addAlgoToInput();

		createPolyhedron();
		if (polyhedronIsDummy) {
			setUndefined();
		} else {
			computeSolid();
		}

		polyhedron.createFaces();
		// faces are oriented to the inside
		polyhedron.setReverseNormals();
		setOutput();
		if (labels == null || labels.length <= 1) {
			polyhedron.initLabels(labels);
		} else {
			polyhedron.setAllLabelsAreSet(true);
			for (int i = 0; i < labels.length; i++) {
				getOutput(i).setLabel(labels[i]);
			}
		}

		update();
	}

	/**
	 * @return first vertex
	 */
	protected GeoPointND getA() {
		if (polygon == null) {
			return A;
		}
		return polygon.getPointND(0);
	}

	/**
	 * @return second vertex
	 */
	protected GeoPointND getB() {
		if (polygon == null) {
			return B;
		}
		return polygon.getPointND(1);
	}

	private Coords getDirection() {
		if (polygon == null) {
			return v.getDirectionInD3();
		}
		return polygon.getCoordSys().getNormal();
	}

	private void setInput(GeoElement... elements) {
		input = elements;
	}

	@Override
	protected void createOutputSegments() {
		outputSegments = createOutputSegmentsHandler();
	}

	@Override
	protected void createOutputPolygons() {
		outputPolygons = createOutputPolygonsHandler();
	}

	@Override
	protected void updateOutput() {

		// add polyhedron's segments and polygons, without setting this algo as
		// algoparent

		outputPolygons.addOutput(polyhedron.getFaces3D(), false, false);
		outputSegments.addOutput(polyhedron.getSegments3D(), false, true);

	}

	/**
	 * create the polyhedron (faces and edges)
	 * 
	 */
	protected void createPolyhedron() {

		int vertexCount = solidDescription.getVertexCount();

		outputPoints.augmentOutputSize(vertexCount - inputPointsCount, false);
		if (getPolyhedron().allLabelsAreSet()) {
			outputPoints.setLabels(null);
		}

		// coords
		coords = solidDescription.getVertices();

		// points
		GeoPointND[] points = new GeoPointND[vertexCount];
		if (polygon == null) {
			points[0] = getA();
			points[1] = getB();
			polyhedronIsDummy = false;
		} else {
			if (polygon.isDefined()
					&& polygon.getPointsLength() == inputPointsCount) {
				GeoPointND[] polygonPoints = polygon.getPointsND();
				for (int i = 0; i < inputPointsCount; i++) {
					points[i] = polygonPoints[i];
				}
				polyhedronIsDummy = false;
			} else {
				// use dummy points to replace polygon points
				for (int i = 0; i < inputPointsCount; i++) {
					points[i] = new DummyGeoPoint3D(cons, i);
				}
				polyhedronIsDummy = true;
			}
		}

		for (int i = inputPointsCount; i < vertexCount; i++) {
			GeoPoint3D point = outputPoints.getElement(i - inputPointsCount);
			points[i] = point;
			point.setCoords(coords[i]);
			polyhedron.addPointCreated(point);
		}

		// faces
		int[][] faces = solidDescription.getFaces();
		int firstPoly;
		if (polygon == null) {
			firstPoly = 0;
		} else {
			polyhedron.addPolygonLinked(polygon);
			firstPoly = 1;
		}
		for (int i = firstPoly; i < faces.length; i++) {
			polyhedron.startNewFace();
			for (int j = 0; j < faces[i].length; j++) {
				polyhedron.addPointToCurrentFace(points[faces[i][j]]);
			}
			polyhedron.endCurrentFace();
		}

	}

	private void setPolyhedronNotDummyIfPossible() {
		if (polygon.isDefined()
				&& polygon.getPointsLength() == inputPointsCount) {
			polyhedron.replaceDummies(polygon.getPointsND(),
					polygon.getSegments());
			polyhedronIsDummy = false;
		}
	}

	@Override
	public void compute() {
		if (polyhedronIsDummy) {
			setPolyhedronNotDummyIfPossible();
		}
		
		if (!polyhedronIsDummy) {
			computeSolid();
		}
	}

	private void computeSolid() {

		polyhedron.setDefined();

		if (polygon != null && (!polygon.isDefined()
				|| polygon.getPointsLength() != inputPointsCount)) {
			setUndefined();
			return;
		}

		Coords o = getA().getInhomCoordsInD3();

		Coords v1l = getB().getInhomCoordsInD3().sub(o);

		// check if A!=B
		if (v1l.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			setUndefined();
			return;
		}

		v1l.calcNorm();
		double l = v1l.getNorm();
		Coords v1 = v1l.mul(1 / l);

		// check if vn!=0
		Coords vn = getDirection();
		if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			setUndefined();
			return;
		}

		// check if vn is ortho to AB
		if (!DoubleUtil.isZero(vn.dotproduct(v1))) {
			setUndefined();
			return;
		}

		Coords v2 = getDirection().crossProduct(v1);
		v2.normalize();

		Coords v3 = v1.crossProduct(v2);

		matrix.setOrigin(o);
		matrix.setVx(v1l);
		matrix.setVy(v2.mul(l));
		matrix.setVz(v3.mul(l));

		if (polygon != null) {
			GeoPointND[] polygonPoints = polygon.getPointsND();
			for (int i = 2; i < inputPointsCount; i++) {
				tmpCoords.setMul3(matrix, coords[i]);
				if (!tmpCoords.equalsForKernel3(
						polygonPoints[i].getInhomCoordsInD3())) {
					setUndefined();
					return;
				}
			}
		}

		for (int i = 0; i < coords.length - inputPointsCount; i++) {
			outputPoints.getElement(i)
					.setCoords(matrix.mul(coords[i + inputPointsCount]),
					true);
		}

		// update volume
		polyhedron.setVolume(l * l * l * volumeFactor);

		// update area
		polyhedron.setArea(l * l * areaFactor);
		// Log.debug("Aire "+polyhedron.getArea());

		// update height
		polyhedron.setOrientedHeight(l * heightFactor);

	}

	private void setVolumeAreaAndHeightFactors() {

		switch (name) {
		default:
		case Tetrahedron:
			volumeFactor = Math.sqrt(2) / 12;
			heightFactor = Math.sqrt(2. / 3.);
			areaFactor = Math.sqrt(3);
			break;

		case Cube:
			volumeFactor = 1;
			heightFactor = 1;
			areaFactor = 6;
			break;

		case Octahedron:
			volumeFactor = Math.sqrt(2) / 3;
			heightFactor = Math.sqrt(2. / 3.);
			areaFactor = 2 * Math.sqrt(3);
			break;

		case Dodecahedron:
			volumeFactor = (15 + 7 * Math.sqrt(5)) / 4;
			heightFactor = Math.sqrt(2.5 + 1.1 * Math.sqrt(5));
			areaFactor = 3 * Math.sqrt(25 + 10 * Math.sqrt(5));
			break;

		case Icosahedron:
			volumeFactor = (15 + 5 * Math.sqrt(5)) / 12;
			heightFactor = (3 + Math.sqrt(5)) / (2 * Math.sqrt(3));
			areaFactor = 5 * Math.sqrt(3);
			break;

		}

	}

	private void setUndefined() {
		polyhedron.setUndefined();

		for (int i = 0; i < outputPoints.size(); i++) {
			outputPoints.getElement(i).setUndefined();
		}

	}

	// ///////////////////////////////////////////
	// END OF THE CONSTRUCTION
	// //////////////////////////////////////////

	@Override
	protected void updateDependentGeos() {
		super.updateDependentGeos();
		outputPoints.update();

		// force update of segments and polygons when e.g. in a list
		if (!getPolyhedron().allLabelsAreSet()) {
			outputSegments.updateParentAlgorithm();
			outputPolygons.updateParentAlgorithm();
		}

	}

	@Override
	public Commands getClassName() {
		return name;
	}

	@Override
	final protected boolean isFirstInputPointVisible() {
		return true;
	}

	@Override
	final protected boolean isFirstInputPointLabelVisible() {
		return true;
	}

}
