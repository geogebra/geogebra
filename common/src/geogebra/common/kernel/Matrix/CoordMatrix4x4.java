package geogebra.common.kernel.Matrix;

import geogebra.common.main.App;

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
		ret.set(Identity(4));
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
	 * @return matrix
	 */	
	public static final CoordMatrix4x4 Rotation4x4(double angle) {
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		CoordMatrix4x4 m = new CoordMatrix4x4();
		m.set(1,1, cos); m.set(1,2, -sin);
		m.set(2,1, sin); m.set(2,2,  cos);
		m.set(3,3, 1);
		m.set(4,4, 1);
		
		return m;
	}
	
	/**
	 * 4x4 rotation matrix axis parallel to oz through center
	 * @param angle angle of rotation
	 * @param center center of rotation
	 * @return matrix
	 */	
	public static final CoordMatrix4x4 Rotation4x4(double angle, Coords center) {
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		CoordMatrix4x4 m = new CoordMatrix4x4();
		
		// 3x3 sub-matrix "M"
		m.set(1,1, cos); m.set(1,2, -sin);
		m.set(2,1, sin); m.set(2,2,  cos);
		m.set(3,3, 1);
		
		//use (Id-M)center for translation
		m.setOrigin(center.sub(m.mul(center)));
		
		return m;
	}
	
	/**
	 * 4x4 rotation matrix around vector
	 * @param u vector of rotation
	 * @param angle angle of rotation
	 * @param center center of rotation
	 * @return matrix
	 */
	public static final CoordMatrix4x4 Rotation4x4(Coords u, double angle, Coords center) {
		
		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();
		
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		
		double[] vals = new double[16];
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

		CoordMatrix4x4 m = new CoordMatrix4x4(vals);
		
		//use (Id-M)center for translation
		m.setOrigin(center.sub(m.mul(center)));
		
		return m;

	}

	/**
	 * complete a 4 x n matrix to a 4 x 4 matrix, orthogonal method
	 * 
	 * @param a_matrix
	 *            source 4 x n matrix
	 */
	public CoordMatrix4x4(CoordMatrix a_matrix) {

		this();
		Coords l_O;

		switch (a_matrix.getColumns()) {
		case 4:
			set(a_matrix);
			break;
		case 2:
			Coords V = a_matrix.getColumn(1);
			l_O = a_matrix.getColumn(2);

			Coords[] n = CoordMatrix4x4.getOrthoVectors(V);

			set(new Coords[] { V, n[0], n[1], l_O });
			break;
		case 3:
			Coords V1 = a_matrix.getColumn(1);
			Coords V2 = a_matrix.getColumn(2);
			l_O = a_matrix.getColumn(3);

			Coords Vn = V1.crossProduct(V2);
			Vn.normalize();

			set(new Coords[] { V1, V2, Vn, l_O });
			break;
		default:
			break;
		}
	}

	/**
	 * complete a given origin and direction to a 4 x 4 matrix, orthogonal
	 * method
	 * 
	 * @param origin
	 * @param direction
	 * @param type
	 *            says which place takes the direction given (VX, VY or VZ)
	 */
	public CoordMatrix4x4(Coords origin, Coords direction, int type) {

		this();
		Coords[] n = CoordMatrix4x4.getOrthoVectors(direction);

		switch (type) {
		case VX:
			set(new Coords[] { direction, n[0], n[1], origin });
			break;
		case VY:
			set(new Coords[] { n[1], direction, n[0], origin });
			break;
		case VZ:
			set(new Coords[] { n[0], n[1], direction, origin });
			break;
		}
	}

	private static final Coords[] getOrthoVectors(Coords V) {
		Coords[] ret = new Coords[2];

		Coords Vn1 = new Coords(4);

		if (V.get(1) != 0) {
			Vn1.set(1, -V.get(2));
			Vn1.set(2, V.get(1));
			Vn1.normalize();
		} else {
			Vn1.set(1, 1.0);
		}

		Coords Vn2 = V.crossProduct(Vn1);
		Vn2.normalize();

		ret[0] = Vn1;
		ret[1] = Vn2;

		return ret;
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
	 * returns the point at position a_x, a_y, a_z
	 * 
	 * @param a_x
	 *            x coord
	 * @param a_y
	 *            y coord
	 * @param a_z
	 *            z coord
	 * @return the point
	 */
	public Coords getPoint(double a_x, double a_y, double a_z) {
		Coords v = new Coords(new double[] { a_x, a_y, a_z, 1 });
		return this.mul(v);
	}

	/**
	 * return a matrix that describe a quad with corners (a_x1,a_y1) and
	 * (a_x2,a_y2)
	 * 
	 * @param a_x1
	 *            x coord of the first corner
	 * @param a_y1
	 *            y coord of the first corner
	 * @param a_x2
	 *            x coord of the third corner
	 * @param a_y2
	 *            y coord of the third corner
	 * @return matrix describing the quad
	 */
	public CoordMatrix4x4 quad(double a_x1, double a_y1, double a_x2,
			double a_y2) {

		CoordMatrix4x4 ret = new CoordMatrix4x4();

		Coords o = getPoint(a_x1, a_y1, 0);
		Coords px = getPoint(a_x2, a_y1, 0);
		Coords py = getPoint(a_x1, a_y2, 0);

		ret.setOrigin(o);
		ret.setVx(px.sub(o));
		ret.setVy(py.sub(o));
		ret.setVz(getVz());

		return ret;
	}

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

		App.debug(rot);

		return rot.mul(this);
	}

	/**
	 * return this rotated arround (Oz) by angle 90°
	 * 
	 * @return rotated matrix
	 */
	public CoordMatrix4x4 rotate90() {

		CoordMatrix4x4 rot = new CoordMatrix4x4();

		// rotation
		rot.set(2, 1, 1);
		rot.set(1, 2, -1);

		// identity
		rot.set(3, 3, 1);
		rot.set(4, 4, 1);

		App.debug(rot);

		return rot.mul(this);
	}

}
