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

		double d = equationVector.dotproduct(getOrigin());
		equationVector.set(4, -d);
		
		checkEquationVectorHasJustOneNegativeCoeff();
	}
	
	/** 
	 * check if two of the x, y, z coeff are equal to 0, and if the other is negative,
	 * then change signs
	 * 
	 */
	final private void checkEquationVectorHasJustOneNegativeCoeff(){
		
		int zeros = 0;
		boolean negative = false;
		
		// check if two of the x, y, z coeff are equal to 0, and if the other is negative
		for (int i = 1 ; i <= 3 ; i++){
			double coeff = equationVector.get(i);
			if (Kernel.isZero(coeff)){
				zeros++;
			}else{
				negative = (coeff<0);
			}
		}
		
		// then change signs
		if (zeros == 2 && negative){
			equationVector.mulInside(-1);
		}
		
	}
	
	/**
	 * set equation vector
	 * @param a x coeff
	 * @param b y coeff
	 * @param c z coeff
	 * @param d w coeff
	 */
	final public void setEquationVector(double a, double b, double c, double d){
		equationVector.setX(a);
		equationVector.setY(b);
		equationVector.setZ(c);
		equationVector.setW(d);
		
		checkEquationVectorHasJustOneNegativeCoeff();
	}
	
	/**
	 * set equation vector corresponding to (m-o).n = 0
	 * @param o origin
	 * @param n normal point
	 */
	public void setEquationVector(Coords o, Coords n){
		setEquationVector(n.getX(),n.getY(),n.getZ(),-(n.getX()*o.getX()+n.getY()*o.getY()+n.getZ()*o.getZ()));
	}

	/**
	 * set equation vector 
	 * @param cA first point
	 * @param cB second point
	 * @param cC third point
	 */
	public void setEquationVector(Coords cA, Coords cB, Coords cC){
		setEquationVector(cA, cB.sub(cA).crossProduct(cC.sub(cA)));
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
			}
			return false;

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

	/**
	 * translate the coord sys (matrix orthonormal and drawing matrix)
	 * @param v translation vector
	 */
	public void translate(Coords v) {

		Coords o = matrixOrthonormal.getOrigin();
		o.addInside(v);
		matrixOrthonormal.setOrigin(o);
		if (dimension==2){
			drawingMatrix.setOrigin(o);
		}		

		setFromMatrixOrthonormal();
	}
	
	/**
	 * translate equation vector
	 * @param v translation vector
	 */
	public void translateEquationVector(Coords v){
		equationVector.setW(equationVector.getW()-v.dotproduct(equationVector));	
	}
	
	/**
	 * transform the matrix orthonormal to fit transform represented by m.
	 * Compute {a,b,c} to perform inside coordsys transformation (which keep
	 * orthonormal matrix) :
	 * new x = a*x
	 * new y = b*x + c*y
	 * @param m matrix of transformation
	 * @return {a,b,c} for inside coordsys transformation
	 */
	public double[] matrixTransform(CoordMatrix4x4 m){
		
		double[] ret;
		
		Coords o = m.mul(matrixOrthonormal.getOrigin());
		
		Coords vx = m.mul(matrixOrthonormal.getVx());
		Coords vy = m.mul(matrixOrthonormal.getVy());
		Coords vn = vx.crossProduct4(vy);
		
		
		if (vn.isZero()){ // vx, vy not independant
			if (vx.isZero()){
				if (vy.isZero()){ // all to 0
					ret = new double[] {0, 0, 
										   0};
					matrixOrthonormal = CoordMatrix4x4.Identity();
					matrixOrthonormal.setOrigin(o);
				}else{ // vy != 0
					vy.calcNorm();
					double l = vy.getNorm();
					ret = new double[] {0, 0, 
										   l};
					matrixOrthonormal = new CoordMatrix4x4(o, vy.mul(1/l), CoordMatrix4x4.VY);
				}				
			}else{ // vx != 0
				vx.calcNorm();
				double l = vx.getNorm();
				vx = vx.mul(1/l);
				double a = vy.dotproduct(vx); // vy maybe not 0
				ret = new double[] {l, a, 
									   0};
				matrixOrthonormal = new CoordMatrix4x4(o, vx, CoordMatrix4x4.VX);

			}
		}else{ // none are 0
			
			vx.calcNorm();
			double l = vx.getNorm();
			vx = vx.mul(1/l);
			vn.normalize();
			Coords vyn = vn.crossProduct4(vx);
			double a = vy.dotproduct(vx);
			double b = vy.dotproduct(vyn);
			ret = new double[] {l, a, 
								   b};
			matrixOrthonormal.setVx(vx);
			matrixOrthonormal.setVy(vyn);
			matrixOrthonormal.setVz(vn);
			matrixOrthonormal.setOrigin(o);
		
		}
		
		

		setFromMatrixOrthonormal();
		
		return ret;
	}
	
	private void setFromMatrixOrthonormal(){
		setOrigin(matrixOrthonormal.getOrigin());
		setVx(matrixOrthonormal.getVx());	

		if (dimension==2){
			setVy(matrixOrthonormal.getVy());
			setVz(matrixOrthonormal.getVz());
			Coords o = Coords.O.projectPlane(matrixOrthonormal)[0];
			//if (projectOrigin) // recompute origin for ortho and drawing matrix
			//	matrixOrthonormal.setOrigin(o);

			drawingMatrix = new CoordMatrix4x4(o, matrixOrthonormal.getVz(), CoordMatrix4x4.VZ);
		}
	
	}
	


	/**
	 * rotate by phi around center, parallel to xOy plane
	 * @param phi angle
	 * @param center center point
	 */
	public void rotate(double phi, Coords center){
		
		//create rotation matrix
		CoordMatrix m = CoordMatrix.Rotation3x3(phi);

		Coords o = matrixOrthonormal.getOrigin();
		
		//set multiplication matrix
		matrixOrthonormal = m.mul3x3(matrixOrthonormal);
		//set origin matrix
		matrixOrthonormal.setOrigin(m.mul(o.sub(center)).add(center));
		matrixOrthonormal.set(4,4, 1);


		setFromMatrixOrthonormal();
	}
	
	/**
	 * rotate by phi around axis through center and parallel to direction
	 * @param phi angle
	 * @param center center point
	 * @param direction direction
	 */
	public void rotate(double phi, Coords center, Coords direction){
		
		//create rotation matrix
		CoordMatrix m = CoordMatrix.Rotation3x3(direction, phi);
				
		Coords o = matrixOrthonormal.getOrigin();
		
		//set multiplication matrix
		matrixOrthonormal = m.mul3x3(matrixOrthonormal);
		//set origin matrix
		matrixOrthonormal.setOrigin(m.mul(o.sub(center)).add(center));
		matrixOrthonormal.set(4,4, 1);
		
		setFromMatrixOrthonormal();
	}
	
	/**
	 * dilate at point
	 * @param r ratio
	 * @param point center point
	 */
	public void dilate(double r, Coords point){
		
		if (r < 0){//reverse all values
			matrixOrthonormal.mulInside3x3(-1);
		}
		
		//translate origin matrix
		matrixOrthonormal.mulOrigin(r);
		matrixOrthonormal.addToOrigin(point.mul(1-r));
		
		setFromMatrixOrthonormal();
	}

	/**
	 * dilate equation vector
	 * @param r ratio
	 * @param point center point
	 */
	public void dilateEquationVector(double r, Coords point){
		
		translateEquationVector(point.mul(1-r));
		
	}

	
	
	/**
	 * mirror the coord sys at point
	 * @param point point
	 */
	public void mirror(Coords point){
		
		//reverse all values
		matrixOrthonormal.mulInside(-1);
		//translate origin matrix
		matrixOrthonormal.addToOrigin(point.mul(2));
		
		setFromMatrixOrthonormal();
	}
	
	/**
	 * mirror equation vector at point
	 * @param point point
	 */
	public void mirrorEquationVector(Coords point){
		translateEquationVector(point.mul(2));
	}
	
	/**
	 * mirror the coord sys at line defined by point, direction
	 * @param point point
	 * @param direction direction
	 */
	public void mirror(Coords point, Coords direction){
		
		//origin projected on the line
		Coords o1 = matrixOrthonormal.getOrigin().projectLine(point, direction)[0]; 
		
		//get projection values
		double x = 2*matrixOrthonormal.getVx().dotproduct(direction);
		double y = 2*matrixOrthonormal.getVy().dotproduct(direction);
		double z = 2*matrixOrthonormal.getVz().dotproduct(direction);
		//reverse all values
		matrixOrthonormal.mulInside(-1);
		//translate vectors
		matrixOrthonormal.addToVx(direction.mul(x));
		matrixOrthonormal.addToVy(direction.mul(y));
		matrixOrthonormal.addToVz(direction.mul(z));
		//translate origin matrix
		matrixOrthonormal.addToOrigin(o1.mul(2));
		
		setFromMatrixOrthonormal();
	}
	
	
	/**
	 * mirror the coord sys at plane
	 * @param cs coord sys representing the plane
	 */
	public void mirror(CoordSys cs){
		
		Coords vn = cs.getNormal();
		
		//origin projected on the line
		Coords o = matrixOrthonormal.getOrigin();
		Coords o1 = o.projectPlane(cs.getMatrixOrthonormal())[0];

		//get projection values
		double x = -2*matrixOrthonormal.getVx().dotproduct(vn);
		double y = -2*matrixOrthonormal.getVy().dotproduct(vn);
		double z = -2*matrixOrthonormal.getVz().dotproduct(vn);
		//translate vectors
		matrixOrthonormal.addToVx(vn.mul(x));
		matrixOrthonormal.addToVy(vn.mul(y));
		matrixOrthonormal.addToVz(vn.mul(z));
		//translate origin matrix
		matrixOrthonormal.setOrigin(o1.mul(2).sub(o));
		
		setFromMatrixOrthonormal();
	}

	


}
