package org.geogebra.common.kernel.Matrix;

import org.geogebra.common.kernel.Kernel;

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
	
	private Coords tmpCoords1 = new Coords(4), tmpCoords2 = new Coords(4),
			tmpCoords3 = new Coords(4), tmpCoords4 = new Coords(4);

	/**
	 * create a coord sys
	 * 
	 * @param dimension
	 *            number of vectors of the coord sys
	 */
	public CoordSys(int dimension) {
		matrix = new CoordMatrix(4, 4);
		matrixOrthonormal = new CoordMatrix4x4();
		drawingMatrix = new CoordMatrix4x4();
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
	public static final CoordSys Identity3D;
	
	static{
		Identity3D = new CoordSys(2);
		Identity3D.makeCoordSys(new double[] { 0, 0, 1, 0 }); // equation z=0
		Identity3D.makeOrthoMatrix(true, true);
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
	
	public Coords getPoint(double x, double y, double z) {
		if (Kernel.isZero(z)){
			return getVector(x, y);
		}
		return getPoint(x/z, y/z);
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
		Coords[] result = new Coords[] { new Coords(4), new Coords(4)};
		coords.projectPlane(this.getMatrixOrthonormal(), result[0], result[1]);
		return result;
	}

	public Coords[] getNormalProjectionForDrawing(Coords coords) {
		Coords[] result = new Coords[] { new Coords(4), new Coords(4)};
		coords.projectPlane(drawingMatrix, result[0], result[1]);
		return result;
	}

	public Coords[] getProjection(Coords coords, Coords willingDirection) {
		Coords[] result = new Coords[] { new Coords(4), new Coords(4)};
		coords.projectPlaneThruV(this.getMatrixOrthonormal(),
				willingDirection, result[0], result[1]);
		return result;
	}
	

	public Coords[] getProjectionThruVIfPossible(Coords coords,
			Coords willingDirection) {
		Coords[] result = new Coords[] { new Coords(4), new Coords(4)};
		coords.projectPlaneThruVIfPossible(this.getMatrixOrthonormal(),
				willingDirection, result[0], result[1]);
		return result;
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
		equationVector.set(getVx().crossProduct(getVy()));
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

			if (projectOrigin){ // recompute origin for ortho matrix
				Coords.O.projectPlane(getMatrixOrthonormal(),tmpCoords1);
			}else{
				tmpCoords1.set(getOrigin());
			}

			// sets orthonormal matrix
			matrixOrthonormal.set(new Coords[] { getVx().normalized(),
					getVy().normalized(), getVz().normalized(), tmpCoords1 });

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
			Coords.O.projectPlane(getMatrixOrthonormal(), tmpCoords3);
			
			if (projectOrigin){ // recompute origin for ortho and drawing matrix
				matrixOrthonormal.setOrigin(tmpCoords3);
			}

			CoordMatrix4x4.createOrthoToDirection(tmpCoords3, vz, CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2, drawingMatrix);

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

		setOrigin(o);

		if (dimension==2){
			Coords.O.projectPlane(matrixOrthonormal, tmpCoords3);
			drawingMatrix.setOrigin(tmpCoords3);
		}
		
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
					CoordMatrix4x4.Identity(matrixOrthonormal);
					matrixOrthonormal.setOrigin(o);
				}else{ // vy != 0
					vy.calcNorm();
					double l = vy.getNorm();
					ret = new double[] {0, 0, 
										   l};
					CoordMatrix4x4.createOrthoToDirection(o, vy.mul(1/l), CoordMatrix4x4.VY, tmpCoords1, tmpCoords2, matrixOrthonormal);
				}				
			}else{ // vx != 0
				vx.calcNorm();
				double l = vx.getNorm();
				vx = vx.mul(1/l);
				double a = vy.dotproduct(vx); // vy maybe not 0
				ret = new double[] {l, a, 
									   0};
				CoordMatrix4x4.createOrthoToDirection(o, vx, CoordMatrix4x4.VX, tmpCoords1, tmpCoords2, matrixOrthonormal);

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
		
		// set original origin and vectors
		setOrigin(o);
		setVx(m.mul(getVx()));	

		if (dimension==2){
			setVy(m.mul(getVy()));
			setVz(m.mul(getVz()));
			setDrawingMatrixFromMatrixOrthonormal();
		}
		
		return ret;
	}
	
	private void setDrawingMatrixFromMatrixOrthonormal(Coords vx){

		Coords.O.projectPlane(matrixOrthonormal, tmpCoords3);
		CoordMatrix4x4.createOrthoToDirection(tmpCoords3, matrixOrthonormal.getVz(), CoordMatrix4x4.VZ, vx, tmpCoords1, tmpCoords2, drawingMatrix);

	}
	


	private void setDrawingMatrixFromMatrixOrthonormal(){

		Coords.O.projectPlane(matrixOrthonormal, tmpCoords3);
		CoordMatrix4x4.createOrthoToDirection(tmpCoords3, matrixOrthonormal.getVz(), CoordMatrix4x4.VZ, tmpCoords1, tmpCoords2, drawingMatrix);

	}
	
	private CoordMatrix tempMatrix3x3;

	/**
	 * rotate by phi around center, parallel to xOy plane
	 * @param phi angle
	 * @param center center point
	 */
	public void rotate(double phi, Coords center){
		
		//create rotation matrix
		if (tempMatrix3x3 == null){
			tempMatrix3x3 = new CoordMatrix(3,3);
		}
		CoordMatrix.Rotation3x3(phi, tempMatrix3x3);

		Coords o = matrixOrthonormal.getOrigin();
		
		//set multiplication matrix
		matrixOrthonormal = tempMatrix3x3.mul3x3(matrixOrthonormal);
		//set origin matrix
		matrixOrthonormal.setOrigin(tempMatrix3x3.mul(o.sub(center)).add(center));
		matrixOrthonormal.set(4,4, 1);


		// set original origin and vectors
		setOrigin(o);
		setVx(tempMatrix3x3.mul(getVx()));	

		if (dimension==2){
			setVy(tempMatrix3x3.mul(getVy()));
			setVz(tempMatrix3x3.mul(getVz()));
			setDrawingMatrixFromMatrixOrthonormal();
		}
	}
	
	
	/**
	 * rotate the 3x3 inside matrix
	 * @param rot rotation matrix
	 * @param center rotation center
	 */
	public void rotate(CoordMatrix rot, Coords center){

		//set multiplication matrix
		Coords o = matrixOrthonormal.getOrigin();
		
		
		Coords newOrigin = rot.mul(o.sub(center)).add(center);
		Coords vx = matrixOrthonormal.getVx();
		Coords vz = new Coords(4);
		vz.setValues(rot.mul(matrixOrthonormal.getVz()), 3);
		CoordMatrix4x4.createOrthoToDirection(newOrigin, vz, CoordMatrix4x4.VZ, vx, tmpCoords1, tmpCoords2, matrixOrthonormal);
		
		/*
		matrixOrthonormal = rot.mul3x3(matrixOrthonormal);

		//set origin matrix
		Coords newOrigin = rot.mul(o.sub(center)).add(center);
		matrixOrthonormal.setOrigin(newOrigin);
		matrixOrthonormal.set(4,4, 1);
		*/
		

		// set original origin and vectors
		setOrigin(newOrigin);
		
		// set vectors
		setVx(matrixOrthonormal.getVx());	
		if (dimension==2){	
			setVy(matrixOrthonormal.getVy());
			setVz(matrixOrthonormal.getVz());
			setDrawingMatrixFromMatrixOrthonormal(drawingMatrix.getVx());
		}
	}
	
	
	/**
	 * rotate by phi around axis through center and parallel to direction
	 * @param phi angle
	 * @param center center point
	 * @param direction direction
	 */
	public void rotate(double phi, Coords center, Coords direction){
		
		//create rotation matrix
		if (tempMatrix3x3 == null){
			tempMatrix3x3 = new CoordMatrix(3,3);
		}
		CoordMatrix.Rotation3x3(direction, phi, tempMatrix3x3);
				
		Coords o = matrixOrthonormal.getOrigin();
		
		//set multiplication matrix
		matrixOrthonormal = tempMatrix3x3.mul3x3(matrixOrthonormal);
		//set origin matrix
		Coords newOrigin = tempMatrix3x3.mul(o.sub(center)).add(center);
		matrixOrthonormal.setOrigin(newOrigin);
		matrixOrthonormal.set(4,4, 1);
		
		// set original origin and vectors
		setOrigin(newOrigin);
		setVx(tempMatrix3x3.mul(getVx()));	

		if (dimension==2){
			setVy(tempMatrix3x3.mul(getVy()));
			setVz(tempMatrix3x3.mul(getVz()));
			setDrawingMatrixFromMatrixOrthonormal();
		}
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
		Coords o = matrixOrthonormal.getOrigin();
		Coords newOrigin = o.mul(r).add(point.mul(1-r));
		matrixOrthonormal.setOrigin(newOrigin);
		

		// set original origin and vectors
		setOrigin(newOrigin);
		if (r < 0){
			getVx().mulInside(-1);
		}

		if (dimension==2){
			if (r < 0){
				getVy().mulInside(-1);
				getVz().mulInside(-1);
			}
			setDrawingMatrixFromMatrixOrthonormal();
		}

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

		// set original origin and vectors
		setOrigin(matrixOrthonormal.getOrigin());
		getVx().mulInside(-1);

		if (dimension==2){
			getVy().mulInside(-1);
			getVz().mulInside(-1);
			setDrawingMatrixFromMatrixOrthonormal();
		}

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
		matrixOrthonormal.getOrigin().projectLine(point, direction, tmpCoords1, null); 
		
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
		matrixOrthonormal.addToOrigin(tmpCoords1.mul(2));
		
		
		// set original origin and vectors
		setOrigin(matrixOrthonormal.getOrigin());
		double p = 2*getVx().dotproduct(direction);
		setVx(getVx().mul(-1).add(direction.mul(p)));

		if (dimension==2){
			p = 2*getVy().dotproduct(direction);
			setVy(getVy().mul(-1).add(direction.mul(p)));
			p = 2*getVz().dotproduct(direction);
			setVz(getVz().mul(-1).add(direction.mul(p)));
			setDrawingMatrixFromMatrixOrthonormal();
		}
	}
	
	
	/**
	 * mirror the coord sys at plane
	 * @param cs coord sys representing the plane
	 */
	public void mirror(CoordSys cs){
		
		Coords vn = cs.getNormal();
		
		//origin projected on the line
		Coords o = matrixOrthonormal.getOrigin();
		o.projectPlane(cs.getMatrixOrthonormal(), tmpCoords1);

		//get projection values
		double x = -2*matrixOrthonormal.getVx().dotproduct(vn);
		double y = -2*matrixOrthonormal.getVy().dotproduct(vn);
		double z = -2*matrixOrthonormal.getVz().dotproduct(vn);
		//translate vectors
		matrixOrthonormal.addToVx(tmpCoords2.setMul(vn, x));
		matrixOrthonormal.addToVy(tmpCoords2.setMul(vn, y));
		matrixOrthonormal.addToVz(tmpCoords2.setMul(vn, z));
		//translate origin matrix
		matrixOrthonormal.setOrigin(tmpCoords2.setSub(tmpCoords2.setMul(tmpCoords1, 2),o));
		
		// set original origin and vectors
		setOrigin(matrixOrthonormal.getOrigin());
		
		double p = -2*getVx().dotproduct(vn);
		setVx(tmpCoords2.setAdd(tmpCoords3.setMul(getVx(), -1),tmpCoords2.setMul(vn,p)));

		if (dimension==2){
			p = -2*getVy().dotproduct(vn);
			setVy(tmpCoords2.setAdd(tmpCoords3.setMul(getVy(), -1),tmpCoords2.setMul(vn,p)));
			p = -2*getVz().dotproduct(vn);
			setVz(tmpCoords2.setAdd(tmpCoords3.setMul(getVz(), -1),tmpCoords2.setMul(vn,p)));
			setDrawingMatrixFromMatrixOrthonormal();
		}
	}

	
	/**
	 * update this to new coord sys with continuity
	 * @param coordsys new coord sys
	 */
	public void updateContinuous(CoordSys coordsys){
		
		matrixOrthonormal.getOrigin().projectPlane(coordsys.getMatrixOrthonormal(), tmpCoords1);
		Coords vz = coordsys.getMatrixOrthonormal().getVz();
		if (matrixOrthonormal.getVz().dotproduct(vz) < 0){
			vz.mulInside3(-1);
		}
		
		CoordMatrix4x4.createOrthoToDirection(tmpCoords1, vz, CoordMatrix4x4.VZ, 
				matrixOrthonormal.getVx(), tmpCoords2, tmpCoords3, matrixOrthonormal);

		setFromMatrixOrthonormal();

	}

	/**
	 * update to contain point
	 * 
	 * @param point
	 *            point
	 */
	public void updateToContainPoint(Coords point) {
		point.projectPlane(matrixOrthonormal, tmpCoords1);
		tmpCoords1.setSub(point, tmpCoords1);
		matrixOrthonormal.addToOrigin(tmpCoords1);

		setFromMatrixOrthonormal();
	}

	/**
	 * update this to contain point and vector with continuity
	 * 
	 * @param point
	 *            point to be contained
	 * @param vector
	 *            vector to be contained
	 * 
	 */
	public void updateContinuousPointVx(Coords point, Coords vector) {

		tmpCoords2.setCrossProduct(matrixOrthonormal.getVz(), vector);
		tmpCoords3.setCrossProduct(vector, tmpCoords2);
		tmpCoords3.setW(0);
		tmpCoords3.normalize();

		CoordMatrix4x4.createOrthoToDirection(matrixOrthonormal.getOrigin(),
				tmpCoords3,
				CoordMatrix4x4.VZ, matrixOrthonormal.getVx(), tmpCoords2,
				tmpCoords4, matrixOrthonormal);

		updateToContainPoint(point);

	}

	/**
	 * set origin, vectors and drawing matrix from orthonormal matrix
	 */
	private void setFromMatrixOrthonormal() {
		// set original origin and vectors
		setOrigin(matrixOrthonormal.getOrigin());

		// set vectors
		setVx(matrixOrthonormal.getVx());
		if (dimension == 2) {
			setVy(matrixOrthonormal.getVy());
			setVz(matrixOrthonormal.getVz());
			setDrawingMatrixFromMatrixOrthonormal(drawingMatrix.getVx());
		}
	}

}
