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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
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
	private GeoBoolean isDirectGeo;

	private int inputPointsCount;

	private final CoordMatrix4x4 matrix;

	private Coords[] coords;

	private final Commands name;

	private final PlatonicSolid solidDescription;
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

	private final Coords tmpCoords = Coords.createInhomCoorsInD3();

	private boolean polyhedronIsDummy;

	private final Coords o = Coords.createInhomCoorsInD3();
	private final Coords v1 = new Coords(4);
	private final Coords v1l = new Coords(4);
	private final Coords v2 = new Coords(4);
	private final Coords v2l = new Coords(4);
	private final Coords v3 = new Coords(4);
	private final Coords v3l = new Coords(4);
	private final Coords vn = new Coords(4);

	private boolean wasDirect;

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
		this.isDirectGeo = isDirect;

		switch (name) {
		default: // Icosahedron, Octahedron
		case Tetrahedron:
			inputPointsCount = 3;
			break;
		case Cube:
			inputPointsCount = 4;
			break;
		case Dodecahedron:
			inputPointsCount = 5;
			break;
		}

		initInputOutput(labels, poly, isDirect);
	}

	private AlgoArchimedeanSolid(Construction c, Commands name) {
		super(c);
		this.name = name;
		init();
		solidDescription = computeSolidDescription(name);
		setVolumeAreaAndHeightFactors();
		matrix = new CoordMatrix4x4();
	}

	/**
	 * 
	 * @param name
	 *            command name
	 * @return solid description for the command name
	 */
	static public PlatonicSolid computeSolidDescription(Commands name) {
		switch (name) {
		default:
		case Tetrahedron:
			return PlatonicSolidsFactory.getTetrahedron();
		case Cube:
			return PlatonicSolidsFactory.getCube();
		case Octahedron:
			return PlatonicSolidsFactory.getOctahedron();
		case Dodecahedron:
			return PlatonicSolidsFactory.getDodecahedron();
		case Icosahedron:
			return PlatonicSolidsFactory.getIcosahedron();
		}
	}

	@Override
	protected int getPolyhedronType() {
		return getPolyhedronType(name);
	}

	/**
	 * 
	 * @param name
	 *            command name
	 * @return polyhedron type for the command name
	 */
	static public int getPolyhedronType(Commands name) {
		switch (name) {
		default:
		case Tetrahedron:
			return GeoPolyhedron.TYPE_TETRAHEDRON;
		case Cube:
			return GeoPolyhedron.TYPE_CUBE;
		case Octahedron:
			return GeoPolyhedron.TYPE_OCTAHEDRON;
		case Dodecahedron:
			return GeoPolyhedron.TYPE_DODECAHEDRON;
		case Icosahedron:
			return GeoPolyhedron.TYPE_ICOSAHEDRON;
		}
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
		initIsDirect();
		setOutput();
		setLabels(labels);
		update();
	}

	/**
	 * @return first vertex
	 */
	protected GeoPointND getA() {
		if (polygon == null) {
			return A;
		}
		return getPolygonPoint(0);
	}

	/**
	 * @return second vertex
	 */
	protected GeoPointND getB() {
		if (polygon == null) {
			return B;
		}
		return getPolygonPoint(1);
	}

	private Coords getDirection() {
		if (polygon == null) {
			return v.getDirectionInD3();
		}
		return polygon.getDirectionInD3();
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
				for (int i = 0; i < inputPointsCount; i++) {
					points[i] = getPolygonPoint(i);
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

	private GeoPointND getPolygonPoint(int i) {
		return polygon.getPointND(i);
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

		o.set3(getA().getInhomCoordsInD3());
		v1l.setSub3(getB().getInhomCoordsInD3(), o);

		// check if A!=B
		if (v1l.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			setUndefined();
			return;
		}

		v1l.calcNorm();
		double dist = v1l.getNorm();
		v1.setMul3(v1l, 1 / dist);

		Coords vnd;
		if (polygon == null) {
			// check if vn!=0
			vnd = getDirection();
			if (vnd.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
				setUndefined();
				return;
			}

			// check if vn is ortho to AB
			if (!DoubleUtil.isZero(vnd.dotproduct(v1))) {
				setUndefined();
				return;
			}
		} else {
			v2.setSub3(getPolygonPoint(2).getInhomCoordsInD3(), o);
			vn.setCrossProduct3(v1, v2);
			if (vn.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
				setUndefined();
				return;
			}
			vn.normalize();
			vnd = vn;
		}

		v2.setCrossProduct3(vnd, v1);
		v2.normalize();

		v3.setCrossProduct3(v1, v2);

		v2l.setMul3(v2, dist);
		if (isDirect()) {
			v3l.setMul3(v3, dist);
		} else {
			v3l.setMul3(v3, -dist);
		}

		matrix.setOrigin(o);
		matrix.setVx(v1l);
		matrix.setVy(v2l);
		matrix.setVz(v3l);

		if (polygon != null) {
			for (int i = 2; i < inputPointsCount; i++) {
				tmpCoords.setMul3(matrix, coords[i]);
				if (!tmpCoords.equalsForKernel3(
						getPolygonPoint(i).getInhomCoordsInD3())) {
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
		polyhedron.setVolume(dist * dist * dist * volumeFactor);

		// update area
		polyhedron.setArea(dist * dist * areaFactor);
		// Log.debug("Aire "+polyhedron.getArea());

		// update height
		polyhedron.setOrientedHeight(dist * heightFactor);

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

	private boolean computeIsDirect() {
		if (polygon == null) {
			return true;
		}
		return polygon.hasReverseNormal() ^ isDirectGeo.getBoolean();
	}

	private boolean isDirect() {
		boolean ret = computeIsDirect();
		if (wasDirect != ret) {
			polyhedron.setReverseNormals(ret);
			wasDirect = ret;
		}
		return ret;
	}

	private void initIsDirect() {
		wasDirect = computeIsDirect();
		polyhedron.setReverseNormals(wasDirect);
	}

}
