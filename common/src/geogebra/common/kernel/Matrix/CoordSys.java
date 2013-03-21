package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;

/**
 * Class describing 1D, 2D and 3D coordinate systems.
 * 
 * @author ggb3D
 * 
 */
public class CoordSys {

	// matrix for the coord sys
	private CoordMatrix matrix;
	private int dimension;
	private int madeCoordSys;
	private CoordMatrix4x4 matrixOrthonormal, drawingMatrix;

	/** vector used for equation of hyperplanes, like ax+by+cz+d=0 for planes */
	private Coords equationVector;

	private Coords origin;
	private Coords[] vectors;

	/** dimension of the space (2 for 2D, 3 for 3D, ...) */
	private int spaceDimension = 3;

	/**
	 * create a coord sys
	 * 
	 * @param dimension
	 *            number of vectors of the coord sys
	 */
	public CoordSys(int dimension) {
		matrix = new CoordMatrix(4, 4);
		matrixOrthonormal = new CoordMatrix4x4();
		this.dimension = dimension;

		origin = new Coords(spaceDimension + 1);
		origin.set(spaceDimension + 1, 1);
		vectors = new Coords[spaceDimension];
		for (int i = 0; i < spaceDimension; i++) {
			vectors[i] = new Coords(spaceDimension + 1);
		}

		equationVector = new Coords(spaceDimension + 1);

		resetCoordSys();
	}

	public void set(CoordSys cs) {

		setOrigin(cs.getOrigin());
		for (int i = 0; i < spaceDimension; i++) {
			setV(cs.getV(i), i);
		}

		matrix.set(cs.matrix);
		matrixOrthonormal.set(cs.matrixOrthonormal);
		if (drawingMatrix == null) // TODO remove that
			drawingMatrix = new CoordMatrix4x4();
		drawingMatrix.set(cs.drawingMatrix);
		// dimension=cs.dimension;
		equationVector.set(cs.equationVector);
		madeCoordSys = cs.madeCoordSys;
		// origin.set(cs.origin);
		// spaceDimension=cs.spaceDimension;
		// vectors=cs.vectors;
	}

	/**
	 * @return "identity" coord sys
	 */
	public static final CoordSys Identity3D() {
		CoordSys ret = new CoordSys(2);
		ret.makeCoordSys(new double[] { 0, 0, 1, 0 }); // equation z=0
		ret.makeOrthoMatrix(true, true);
		return ret;
	}

	public CoordMatrix getMatrix() {
		return matrix;
	}

	public int getDimension() {
		return dimension;
	}

	// //////////////////////////
	// setters

	public void setOrigin(Coords o) {
		origin.set(o);
	}

	public void setVx(Coords v) {
		setV(v, 0);
	}

	public void setVy(Coords v) {
		setV(v, 1);
	}

	public void setVz(Coords v) {
		setV(v, 2);
	}

	public void setV(Coords v, int i) {
		vectors[i].set(v);
	}

	public Coords getV(int i) {
		return vectors[i];
	}

	public Coords getOrigin() {
		return origin;
	}

	public Coords getVx() {
		return getV(0);
	}

	public Coords getVy() {
		return getV(1);
	}

	public Coords getVz() {
		return getV(2);
	}

	public Coords getPoint(Coords coords2D) {
		return getPoint(coords2D.getX(), coords2D.getY());
	}

	public Coords getPoint(double x, double y) {
		return matrixOrthonormal.getOrigin().add(getVector(x, y));
	}

	public Coords getPointForDrawing(double x, double y) {
		return drawingMatrix.mul(new Coords(x, y, 0, 1));
	}

	public Coords getPoint(double x) {
		return getOrigin().add(getVx().mul(x));
	}

	public Coords getVector(Coords coords2D) {
		return getVector(coords2D.getX(), coords2D.getY());
	}

	public Coords getVector(double x, double y) {
		return matrixOrthonormal.getVx().mul(x)
				.add(matrixOrthonormal.getVy().mul(y));
	}

	public Coords getNormal() {
		return matrixOrthonormal.getVz();// getVx().crossProduct(getVy()).normalized();
	}

	// ///////////////////////////////////
	//
	// FOR REGION3D INTERFACE
	//
	// ///////////////////////////////////

	public Coords[] getNormalProjection(Coords coords) {
		return coords.projectPlane(this.getMatrixOrthonormal());
	}

	public Coords[] getNormalProjectionForDrawing(Coords coords) {
		return coords.projectPlane(drawingMatrix);
	}

	public Coords[] getProjection(Coords coords, Coords willingDirection) {
		return coords.projectPlaneThruV(this.getMatrixOrthonormal(),
				willingDirection);
	}
	

	public Coords[] getProjectionThruVIfPossible(Coords coords,
			Coords willingDirection) {
		return coords.projectPlaneThruVIfPossible(this.getMatrixOrthonormal(),
				willingDirection);
	}

	// /////////////////////////////////////
	// creating a coord sys

	/**
	 * set how much the coord sys is made
	 * 
	 * @param i
	 *            value of made coord sys
	 */
	public void setMadeCoordSys(int i) {
		madeCoordSys = i;
	}

	/**
	 * set the coord sys is finish
	 */
	public void setMadeCoordSys() {
		setMadeCoordSys(dimension);
	}

	/**
	 * reset the coord sys
	 */
	public void resetCoordSys() {
		setMadeCoordSys(-1);
	}

	/**
	 * return how much the coord sys is made
	 * 
	 * @return how much the coord sys is made
	 */
	public int getMadeCoordSys() {
		return madeCoordSys;
	}
	
	/**
	 * complete the coord sys for 2 dimension
	 */
	public void completeCoordSys2D(){
		switch(getMadeCoordSys()){
		case 0:
			addVectorWithoutCheckMadeCoordSys(Coords.VX);
			addVectorWithoutCheckMadeCoordSys(Coords.VY);
			break;
		case 1:
			Coords vx = getVx();
			if (Kernel.isZero(vx.getX()))
				addVectorWithoutCheckMadeCoordSys(new Coords(0,-vx.getZ(),vx.getY(),0));
			else
				addVectorWithoutCheckMadeCoordSys(new Coords(-vx.getY(),vx.getX(),0,0));
			break;
		}
	}

	/**
	 * return if the coord sys is made
	 * 
	 * @return if the coord sys is made
	 */
	public boolean isMadeCoordSys() {
		return (getMadeCoordSys() == dimension);
	}

	/**
	 * Try to add the point described by p to complete the coord sys.
	 * 
	 * @param p
	 *            a point (x,y,z,1)
	 * 
	 */
	public void addPoint(Coords p) {

		if (isMadeCoordSys())
			return;

		if (getMadeCoordSys() == -1) {
			// add the origin
			setOrigin(p);
			setMadeCoordSys(0);
		} else {
			// point is the end of a vector
			addVectorWithoutCheckMadeCoordSys(p.sub(getOrigin()));
		}
	}

	/**
	 * Try to add the vector described by v to complete the coord sys.
	 * 
	 * @param v
	 *            a vector (x,y,z,1)
	 * 
	 */
	public void addVector(Coords v) {

		if (isMadeCoordSys()) {
			return;
		}
		addVectorWithoutCheckMadeCoordSys(v);
	}

	/**
	 * Try to add the vector described by v to complete the coord sys.
	 * 
	 * @param v
	 *            a vector (x,y,z,1)
	 * 
	 */
	public void addVectorWithoutCheckMadeCoordSys(Coords v) {

		switch (getMadeCoordSys()) {
		case 0: // add first vector
			// check if v==0
			if (!Kernel.isEqual(v.norm(), 0,
					Kernel.STANDARD_PRECISION)) {
				setVx(v);
				setMadeCoordSys(1);
			}
			break;
		case 1: // add second vector
			// calculate normal vector to check if v1 depends to vx
			Coords vn = getVx().crossProduct(v);
			// check if vn==0
			if (!Kernel.isEqual(vn.norm(), 0,
					Kernel.STANDARD_PRECISION)) {
				setVy(v);
				setVz(getVx().crossProduct(getVy()));
				setMadeCoordSys(2);
			}
			break;
		}

		// Application.printStacktrace("v["+getMadeCoordSys()+"]=\n"+v);
	}

	/**
	 * creates the equation vector
	 */
	public void makeEquationVector() {
		equationVector.set(getVx().crossProduct(getVy()), 1);
		equationVector.set(4, 0);
		if (equationVector.getX() < Kernel.STANDARD_PRECISION
				&& equationVector.getY() < Kernel.STANDARD_PRECISION
				&& equationVector.getZ() < Kernel.STANDARD_PRECISION)
			equationVector = equationVector.mul(-1);

		double d = equationVector.dotproduct(getOrigin());
		equationVector.set(4, -d);
		// Application.debug("equationVector:\n"+equationVector);
	}

	/**
	 * @return the equation vector
	 */
	public Coords getEquationVector() {
		return equationVector;
	}

	/*
	 * set the equation vector
	 * 
	 * @param vals
	 * 
	 * public void setEquationVector(double[] vals){ equationVector.set(vals); }
	 */

	/**
	 * creates the coord sys from the equation, e.g. ax+by+cz+d=0 for planes
	 * 
	 * @param vals
	 */
	public void makeCoordSys(double[] vals) {

		resetCoordSys();

		equationVector.set(vals);

		// sets the origin : first non-zero value sets the coord ( - const value
		// / coeff value)
		Coords o = new Coords(4);
		boolean originSet = false;
		for (int i = 0; i < vals.length - 1; i++) {
			if (!originSet)
				if (vals[i] != 0) {
					o.set(i + 1, -vals[vals.length - 1] / vals[i]);
					originSet = true;
				}
		}
		// check if at least one coeff is non-zero
		if (!originSet)
			return;

		o.set(vals.length, 1);
		addPoint(o);

		// creates the coordsys vectors
		Coords[] v = equationVector.completeOrthonormal();
		addVectorWithoutCheckMadeCoordSys(v[1]);
		addVectorWithoutCheckMadeCoordSys(v[0]);

		// Application.debug("O=\n"+getOrigin().toString()+"\nVx=\n"+getVx().toString()+"\nVy=\n"+getVy().toString());
	}

	/**
	 * makes an orthonormal matrix describing this coord sys
	 * 
	 * @param projectOrigin
	 *            if true, origin of the coord sys is the projection of 0
	 * @param firstVectorParallelToXOY
	 *            says if the first vector has to be parallel to xOy
	 * @return true if it's possible
	 */
	public boolean makeOrthoMatrix(boolean projectOrigin,
			boolean firstVectorParallelToXOY) {

		if (!isMadeCoordSys()) {
			if (dimension == 1) {
				matrixOrthonormal.set(0);
				if (getMadeCoordSys() == 0)
					matrixOrthonormal.setOrigin(getOrigin());
				getVx().set(0);
				return false;
			}

		}

		// if the coord sys is made, the drawing matrix is updated
		if (dimension == 1) {
			// compute Vy and Vz
			Coords vy = (new Coords(new double[] { 0, 0, 1, 0 }))
					.crossProduct(getVx());
			// check if vy=0 (if so, vx is parallel to Oz)
			if (vy.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
				setVy(new Coords(new double[] { 1, 0, 0, 0 }));
				setVz(new Coords(new double[] { 0, 1, 0, 0 }));
			} else {
				setVy(vy);
				setVz(getVx().crossProduct(getVy()));
			}

			Coords o = getOrigin();
			if (projectOrigin) // recompute origin for ortho matrix
				o = (new Coords(0, 0, 0, 1))
						.projectPlane(getMatrixOrthonormal())[0];

			// sets orthonormal matrix
			matrixOrthonormal.set(new Coords[] { getVx().normalized(),
					getVy().normalized(), getVz().normalized(), o });

			return true;
		}

		if (dimension == 2) { // vy and Vz are computed
			Coords vx, vy, vz;

			if (firstVectorParallelToXOY) {
				// vector Vx parallel to xOy plane
				vz = new Coords(0, 0, 1, 0);
				vx = getVz().crossProduct(vz);
				// if (!Kernel.isEqual(vx.norm(), 0,
				// Kernel.STANDARD_PRECISION)){
				if (!vx.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
					vx.normalize();
					vy = getVz().crossProduct(vx);
					vy.normalize();
					vz = getVz().normalized();
				} else {
					vx = new Coords(1, 0, 0, 0);
					vy = new Coords(0, 1, 0, 0);
				}
			} else {
				vx = getVx().normalized(true);
				// vz is computed and vy recomputed to make orthonormal matrix
				vz = vx.crossProduct(getVy()).normalized(true);
				vy = vz.crossProduct(vx);
			}

			matrixOrthonormal.setOrigin(getOrigin());
			matrixOrthonormal.setVx(vx);
			matrixOrthonormal.setVy(vy);
			matrixOrthonormal.setVz(vz);
			Coords o = (new Coords(0, 0, 0, 1))
					.projectPlane(getMatrixOrthonormal())[0];
			if (projectOrigin) // recompute origin for ortho and drawing matrix
				matrixOrthonormal.setOrigin(o);

			drawingMatrix = new CoordMatrix4x4(o, vz, CoordMatrix4x4.VZ);

			// Application.debug("matrix ortho=\n"+getMatrixOrthonormal());

			return true;
		}

		return false;
	}

	public boolean isDefined() {
		return isMadeCoordSys();
	}

	public void setUndefined() {
		resetCoordSys();
	}

	/** returns orthonormal matrix */
	public CoordMatrix4x4 getMatrixOrthonormal() {
		return matrixOrthonormal;
	}

	/** returns drawing matrix */
	public CoordMatrix4x4 getDrawingMatrix() {
		return drawingMatrix;
	}
	
	
	/**
	 * set simple coord sys with m for origin
	 * @param m origin point
	 */
	public void setSimpleCoordSysWithOrigin(Coords m){
		setOrigin(m);
		setVx(Coords.VX);
		setVy(Coords.VY);
		setVz(Coords.VZ);
		
		matrixOrthonormal = CoordMatrix4x4.Identity();
		matrixOrthonormal.setOrigin(m);
		
		drawingMatrix = CoordMatrix4x4.Identity();
		drawingMatrix.setOrigin(m);
	}

	// ///////////////////////////
	// TRANSFORMATIONS
	// //////////////////////////

	public void translate(Coords v) {
		setOrigin(getOrigin().add(v));
		matrixOrthonormal.setOrigin(getOrigin());
	}

	public void translateDrawingMatrix() {
		drawingMatrix.setOrigin(getOrigin());
	}
	
	public void matrixTransform(CoordMatrix4x4 m){
		matrixOrthonormal = m.mul(matrixOrthonormal);
		setOrigin(matrixOrthonormal.getOrigin());
		setVx(matrixOrthonormal.getVx());		
	}

}
