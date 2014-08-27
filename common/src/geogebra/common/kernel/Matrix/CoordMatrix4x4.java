package geogebra.common.kernel.Matrix;

import geogebra.common.util.MyMath;


/**
 * 4x4 matrix for 3D transformations, planes descriptions, lines, etc.
 * 
 * @author ggb3D
 * 
 */
public class CoordMatrix4x4 extends CoordMatrix {

	final static public int VX = 0;
	final static public int VY = 1;
	final static public int VZ = 2;

	final static public CoordMatrix4x4 IDENTITY = Identity();
	final static public CoordMatrix4x4 MIRROR_O = Identity().mirrorO();
	final static public CoordMatrix4x4 MIRROR_X = Identity().mirrorX();
	final static public CoordMatrix4x4 MIRROR_Y = Identity().mirrorY();
	final static public CoordMatrix4x4 ROTATION_OZ_90 = RotationOz(Math.PI / 2);
	final static public CoordMatrix4x4 ROTATION_OZ_M90 = RotationOz(-Math.PI / 2);

	// /////////////////////////////////////////////////
	// CONSTRUCTORS

	/**
	 * basic constructor.
	 */
	public CoordMatrix4x4() {
		super(4, 4);
	}

	/**
	 * construct matrix with values
	 * @param vals values
	 */
	public CoordMatrix4x4(double[] vals) {
		super(4, 4, vals);
	}

	/**
	 * Transforms the object using the matrix
	 * a00 a01 a02
	 * a10 a11 a12
	 * a20 a21 a22
	 * @param a00 a00
	 * @param a01 a01
	 * @param a02 a02
	 * @param a10 a10
	 * @param a11 a11
	 * @param a12 a12
	 * @param a20 a20
	 * @param a21 a21
	 * @param a22 a22
	 */
	public CoordMatrix4x4(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		this();
		val = new double[]{
				a00, a10, a20, 0,
				a01, a11, a21, 0,
				a02, a12, a22,  0,
				0, 0, 0, 1};
	}


	/**
	 * create a 4x4 identity matrix.
	 * 
	 * @return 4x4 identity matrix
	 */
	static final public CoordMatrix4x4 Identity() {
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		for (int i = 1; i <= 4; i++) {
			ret.set(i, i, 1.0);
		}
		return ret;
	}
	
	/**
	 * set 4x4 identity matrix
	 * @param ret matrix set
	 */
	static final public void Identity(CoordMatrix4x4 ret) {
		for (int i = 0 ; i < 16 ; i++){
			ret.val[i] = 0;
		}
		for (int i = 1; i <= 4; i++) {
			ret.set(i, i, 1.0);
		}
	}
	
	/**
	 * create a 4x4 dilate matrix.
	 * @param f dilate factor
	 * 
	 * @return 4x4 dilate matrix
	 */
	static final public CoordMatrix4x4 Dilate(double f) {
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		for (int i = 1; i <= 3; i++) {
			ret.set(i, i, f);
		}
		ret.set(4, 4, 1);
		return ret;
	}

	static final public CoordMatrix4x4 RotationOz(double angle) {
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		ret.set(1, 1, c);
		ret.set(2, 2, c);
		ret.set(2, 1, s);
		ret.set(1, 2, -s);
		ret.set(3, 3, 1);
		ret.set(4, 4, 1);
		return ret;
	}
	
	
	/**
	 * 4x4 rotation matrix around oz
	 * @param angle angle of rotation
	 * @param m ret matrix
	 * 
	 */	
	public static final void Rotation4x4(double angle, CoordMatrix4x4 m) {
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);

		m.set(1,1, cos); m.set(1,2, -sin);
		m.set(2,1, sin); m.set(2,2,  cos);
		m.set(3,3, 1);
		m.set(4,4, 1);
		
	}
	
	/**
	 * 4x4 rotation matrix axis parallel to oz through center
	 * @param angle angle of rotation
	 * @param center center of rotation
	 * @param m ret matrix
	 */	
	public static final void Rotation4x4(double angle, Coords center, CoordMatrix4x4 m) {
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		// 3x3 sub-matrix "M"
		m.set(1,1, cos); m.set(1,2, -sin);
		m.set(2,1, sin); m.set(2,2,  cos);
		m.set(3,3, 1);
		
		//use (Id-M)center for translation
		m.setOrigin(center.sub(m.mul(center)));
		
	}
	
	/**
	 * 4x4 rotation matrix around vector
	 * @param u vector of rotation
	 * @param angle angle of rotation
	 * @param center center of rotation
	 * @return matrix
	 */
	public static final void Rotation4x4(Coords u, double angle, Coords center, CoordMatrix4x4 m) {
		
		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();
		
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		
		double[] vals = m.val;
		vals[0] = ux*ux*(1-c) + c;
		vals[1] = ux*uy*(1-c) + uz*s;
		vals[2] = ux*uz*(1-c) - uy*s;
		//vals[3] = 0;
		
		vals[4] = ux*uy*(1-c) - uz*s;
		vals[5] = uy*uy*(1-c) + c;
		vals[6] = uy*uz*(1-c) + ux*s;
		//vals[7] = 0;		
		
		vals[8] = ux*uz*(1-c) + uy*s;
		vals[9] = uy*uz*(1-c) - ux*s;
		vals[10] = uz*uz*(1-c) + c;
		//vals[11] = 0;

		
		//use (Id-M)center for translation
		m.setOrigin(center.sub(m.mul(center)));
		

	}
	
	
	/**
	 * Axial symetry matrix around line
	 * @param u direction of line
	 * @param center point on line
	 * @return matrix
	 */
	public static final CoordMatrix4x4 AxialSymetry(Coords u, Coords center) {
		
		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();
		
		double[] vals = new double[16];
		vals[0] = 2*ux*ux - 1;
		vals[1] = 2*ux*uy;
		vals[2] = 2*ux*uz;
		
		vals[4] = 2*ux*uy;
		vals[5] = 2*uy*uy - 1;
		vals[6] = 2*uy*uz;
		
		vals[8] = 2*ux*uz;
		vals[9] = 2*uy*uz;
		vals[10] = 2*uz*uz - 1;

		CoordMatrix4x4 m = new CoordMatrix4x4(vals);
		
		//use (Id-M)center for translation
		m.setOrigin(center.sub(m.mul(center)));
		
		return m;

	}

	/**
	 * Plane symetry matrix 
	 * @param n direction of line
	 * @param center point on plane
	 * @return matrix
	 */
	public static final CoordMatrix4x4 PlaneSymetry(Coords n, Coords center) {
		
		double nx = n.getX();
		double ny = n.getY();
		double nz = n.getZ();
		
		
		double[] vals = new double[16];
		vals[0] = 1 - 2*nx*nx;
		vals[1] = - 2*nx*ny;
		vals[2] = - 2*nx*nz;
		
		vals[4] = - 2*nx*ny;
		vals[5] = 1 - 2*ny*ny;
		vals[6] = - 2*ny*nz;
		
		vals[8] = - 2*nx*nz;
		vals[9] = - 2*ny*nz;
		vals[10] = 1 - 2*nz*nz;

		CoordMatrix4x4 m = new CoordMatrix4x4(vals);
		
		//use center for translation
		m.setOrigin(n.mul(2*center.dotproduct(n)));
		m.set(4,4, 1);
		
		return m;

	}


	/**
	 * complete the matrix to a 4 x 4 matrix, orthogonal method.
	 * @param V first vector
	 * 
	 * @param Vn1 first normal vector (maybe changed)
	 * @param Vn2 second normal vector (maybe changed)
	 * @param ret matrix to complete
	 */
	public static void completeOrtho(Coords V, Coords Vn1, Coords Vn2, CoordMatrix4x4 ret) {

		CoordMatrix4x4.getOrthoVectors(V, Vn1, Vn2);

		ret.setVx(V);
		ret.setVy(Vn1);
		ret.setVz(Vn2);

	}

	/**
	 * complete a given origin and direction to a 4 x 4 matrix, orthogonal
	 * method
	 * 
	 * @param origin
	 * @param direction
	 * @param type
	 *            says which place takes the direction given (VX, VY or VZ)
	 * @param Vn1 first normal vector (maybe changed)
	 * @param Vn2 second normal vector (maybe changed)
	 */
	public static void createOrthoToDirection(Coords origin, Coords direction, int type, Coords Vn1, Coords Vn2, CoordMatrix4x4 ret) {

		getOrthoVectors(direction, Vn1, Vn2);

		ret.setOrigin(origin);
		
		switch (type) {
		case VX:
			ret.setVx(direction);
			ret.setVy(Vn1);
			ret.setVz(Vn2);
			break;
		case VY:
			ret.setVx(Vn2);
			ret.setVy(direction);
			ret.setVz(Vn1);
			break;
		case VZ:
			ret.setVx(Vn1);
			ret.setVy(Vn2);
			ret.setVz(direction);
			break;
		}
	}
	

	private static final void getOrthoVectors(Coords V, Coords Vn1, Coords Vn2) {

		double y = V.getX();
		if (y != 0) {
			double x = -V.getY();
			double l = MyMath.length(x, y);
			Vn1.setX(x/l);
			Vn1.setY(y/l);
			Vn1.setZ(0);
			Vn1.setW(0);
		} else {
			Vn1.setX(1);
			Vn1.setY(0);
			Vn1.setZ(0);
			Vn1.setW(0);
		}

		Vn2.setCrossProduct(V, Vn1);
		Vn2.setW(0);
		Vn2.normalize();


	}
	

	// /////////////////////////////////////////////////
	// OVERWRITE Ggb3DMatrix

	// matrix multiplication
	/**
	 * returns this * m
	 * 
	 * @param m
	 *            matrix
	 * @return resulting matrix
	 */
	public CoordMatrix4x4 mul(CoordMatrix4x4 m) {

		CoordMatrix4x4 result = new CoordMatrix4x4();
		this.mul(m, result);

		return result;

	}
	



	// /////////////////////////////////////////////////
	// OPERATIONS

	/**
	 * multiply all values by v (but not origin column)
	 * @param v value
	 */
	public void mulAllButOrigin(double v) {
		for (int i = 0; i < 12; i++)
			val[i] *= v;

	}
	
	/**
	 * set the diag values to v (not on origin column)
	 * @param v value
	 */
	public void setDiag(double v) {
		for (int i = 1; i <= 3; i++)
			set(i, i, v);

	}
	

	// /////////////////////////////////////////////////
	// LENGTHS

	/**
	 * return length of unit for each axis
	 * 
	 * @param a_axis
	 *            number of the axis
	 * @return length of unit
	 */
	public double getUnit(int a_axis) {
		return getColumn(a_axis).norm();
	}

	// /////////////////////////////////////////////////
	// GEOMETRIES

	
	/**
	 * return a matrix that describe a segment along x-axis from x=x1 to x=x2
	 * 
	 * @param a_x1
	 *            x-start of the segment
	 * @param a_x2
	 *            x-end of the segment
	 * @return matrix describing the segment
	 */
	public CoordMatrix segmentX(double a_x1, double a_x2) {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		ret.setOrigin(getOrigin().add(getVx().mul(a_x1)));
		ret.setVx(getVx().mul(a_x2 - a_x1));
		ret.setVy(getVy());
		ret.setVz(getVz());

		return ret;
	}

	/**
	 * return this translated along y axis
	 * 
	 * @param a_y
	 *            value of the y-translation
	 * @return matrix translated
	 */
	public CoordMatrix4x4 translateY(double a_y) {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		ret.setOrigin(getOrigin().add(getVy().mul(a_y)));
		ret.setVx(getVx());
		ret.setVy(getVy());
		ret.setVz(getVz());

		return ret;
	}

	/**
	 * return this mirrored by x=y plane
	 * 
	 * @return mirrored matrix
	 */
	public CoordMatrix4x4 mirrorXY() {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		ret.setOrigin(getOrigin());
		ret.setVx(getVy());
		ret.setVy(getVx());
		ret.setVz(getVz().mul(-1));

		return ret;
	}

	/**
	 * return this mirrored by Oy line
	 * 
	 * @return mirrored matrix
	 */
	public CoordMatrix4x4 mirrorY() {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		ret.setOrigin(getOrigin());
		ret.setVx(getVx().mul(-1));
		ret.setVy(getVy());
		ret.setVz(getVz().mul(-1));

		return ret;
	}

	/**
	 * return this mirrored by Ox line
	 * 
	 * @return mirrored matrix
	 */
	public CoordMatrix4x4 mirrorX() {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		ret.setOrigin(getOrigin());
		ret.setVx(getVx());
		ret.setVy(getVy().mul(-1));
		ret.setVz(getVz().mul(-1));

		return ret;
	}

	/**
	 * return this mirrored by Origin
	 * 
	 * @return mirrored matrix
	 */
	public CoordMatrix4x4 mirrorO() {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		ret.setOrigin(getOrigin());
		ret.setVx(getVx().mul(-1));
		ret.setVy(getVy().mul(-1));
		ret.setVz(getVz());

		return ret;
	}

	/**
	 * return this rotated arround (Oz) by angle -90°
	 * 
	 * @return rotated matrix
	 */
	public CoordMatrix4x4 rotateM90() {

		CoordMatrix4x4 rot = new CoordMatrix4x4();

		// rotation
		rot.set(2, 1, -1);
		rot.set(1, 2, 1);

		// identity
		rot.set(3, 3, 1);
		rot.set(4, 4, 1);

		//Log.debug(rot);

		return rot.mul(this);
	}



	/**
	 * mul 3x3 submatrix by v
	 * @param v value
	 */
	public void mulInside3x3(double v){
		for (int i = 0 ; i < 3; i++){
			for (int j = 0 ; j < 3; j++){
				val[i + 4*j] *= v;
			}
		}
	}

}
