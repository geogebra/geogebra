package org.geogebra.common.kernel.Matrix;

public class Coords3D {
	double[] val = new double[4];
	private double norm, sqNorm;
	private boolean calcNorm = true;
	private boolean calcSqNorm = true;

	public Coords3D(double x, double y, double z, double w) {
		val[0] = x;
		val[1] = y;
		val[2] = z;
		val[3] = w;
	}

	// /////////////////////////////////////////////////:
	// setters and getters
	/**
	 * sets v(i) to val0
	 * 
	 * @param i
	 *            number of the row
	 * @param val0
	 *            value
	 */
	public void set(int i, double val0) {
		switch (i) {
		case 1:
			val[0] = val0;
			break;
		case 2:
			val[1] = val0;
			break;
		case 3:
			val[2] = val0;
			break;
		case 4:
			val[3] = val0;
			break;
		}
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets v to vals0
	 * 
	 * @param vals0
	 *            values {x1, x2, ...}
	 */
	public void set(double[] vals0) {
		val[0] = vals0[0];
		val[1] = vals0[1];
		val[2] = vals0[2];
		val[3] = vals0[3];
		calcNorm = calcSqNorm = true;
	}

	/**
	 * returns v(i)
	 * 
	 * @param i
	 *            number of the row
	 * @return value
	 */
	public double get(int i) {
		switch (i) {
		case 0:
			return val[0];
		case 1:
			return val[1];
		case 2:
			return val[2];
		}
		return val[3];
	}

	/**
	 * returns v "val[0]-coord"
	 * 
	 * @return val[0]-coord
	 */
	public double getX() {
		return val[0];
	}

	/**
	 * returns v "val[1]-coord"
	 * 
	 * @return val[1]-coord
	 */
	public double getY() {
		return val[1];
	}

	/**
	 * returns v "val[2]-coord"
	 * 
	 * @return val[2]-coord
	 */
	public double getZ() {
		return val[2];
	}

	/**
	 * returns v "val[3]-coord"
	 * 
	 * @return val[3]-coord
	 */
	public double getW() {
		return val[3];
	}

	/**
	 * sets the "val[0]-coord"
	 * @param v new value of "val[0]-coord"
	 */
	public void setX(double v) {
		val[0] = v;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "val[1]-coord"
	 * @param v new value of "val[1]-coord"
	 */
	public void setY(double v) {
		val[1] = v;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "val[2]-coord"
	 * 
	 * @param v new value of "val[2]-coord"
	 */
	public void setZ(double v) {
		val[2] = v;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "val[3]-coord"
	 * 
	 * @param v new value of "val[3]-coord"
	 */
	public void setW(double v) {
		val[3] = v;
		calcNorm = calcSqNorm = true;
	}

	// /////////////////////////////////////////////////:
	// basic operations

	/**
	 * returns dot product this * v.
	 * <p>
	 * If this={x1,x2,...} and v={val[0]'1,val[0]'2,...}, the dot product is
	 * x1*val[0]'1+x2*val[0]'2+...
	 * 
	 * @param a
	 *            vector multiplied with
	 * @return value of the dot product
	 */
	public double dotproduct(Coords3D a) {
		return val[0] * a.val[0] + val[1] * a.val[1] + val[2] * a.val[2];
	}

	/**
	 * returns cross product this * v. Attempt that the two vectors are of
	 * dimension 3.
	 * <p>
	 * If this={val[0],val[1],val[2]} and v={val[0]',val[1]',val[2]'}, then
	 * cross product={yz'-val[1]'val[2],zx'-val[2]'val[0],xy'-yx'}
	 * 
	 * @param a
	 *            vector multiplied with
	 * @return vector resulting of the cross product
	 */
	public Coords3D crossProduct(Coords3D a) {
		return new Coords3D(val[1] * a.val[2] - val[2] * a.val[1], val[2]
				* a.val[0] - val[0] * a.val[2], val[0] * a.val[1] - val[1]
				* a.val[0], 0);
	}

	/**
	 * returns the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...). Same result as
	 * Math.sqrt(this.dotproduct(this))
	 * 
	 * @return the scalar norm
	 */
	public double norm() {
		if (calcNorm) {
			norm = Math.sqrt(val[0] * val[0] + val[1] * val[1] + val[2]
					* val[2]);
			calcNorm = false;
		}
		return norm;
	}

	/**
	 * returns the square of the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=x1*x1+x2*x2+... Same result as
	 * this.dotproduct(this)
	 * 
	 * @return the scalar norm
	 */
	public double squareNorm() {
		if (calcSqNorm) {
			sqNorm = val[0] * val[0] + val[1] * val[1] + val[2] * val[2];
			calcSqNorm = false;
		}
		return sqNorm;
	}

	/**
	 * returns this normalized
	 * 
	 * @return this/this.norm()
	 */
	public Coords3D normalized() {

		double inv;
		if (calcNorm)
			inv = 1 / Math.sqrt(val[0] * val[0] + val[1] * val[1] + val[2]
					* val[2]);
		else
			inv = 1 / norm;
		return new Coords3D(val[0] * inv, val[1] * inv, val[2] * inv, val[3]
				* inv);
	}

	/** normalize this */
	public Coords3D normalize() {
		double inv;
		if (calcNorm)
			inv = 1 / Math.sqrt(val[0] * val[0] + val[1] * val[1] + val[2]
					* val[2]);
		else
			inv = 1 / norm;
		val[0] *= inv;
		val[1] *= inv;
		val[2] *= inv;
		norm = sqNorm = 1.0;
		return this;
	}

	/**
	 * returns this-v
	 * 
	 * @param v
	 *            vector subtracted
	 * @return this-v
	 */
	public Coords3D sub(Coords3D v) {
		return new Coords3D(val[0] - v.val[0], val[1] - v.val[1], val[2]
				- v.val[2], 0);
	}

	/**
	 * returns this-v
	 * 
	 * @param v
	 *            vector subtracted
	 * @return this-v
	 */
	public Coords3D add(Coords3D v) {
		return new Coords3D(val[0] + v.val[0], val[1] + v.val[1], val[2]
				+ v.val[2], 0);
	}

	/**
	 * @return
	 */
	public boolean isDefined() {
		return !(val[0] != val[0] || val[1] != val[1] || val[2] != val[2]);
	}

	/**
	 * returns a copy of the vector
	 * 
	 * @return a copy of the vector
	 */
	public Coords3D copyVector() {
		return new Coords3D(val[0], val[1], val[2], val[3]);

	}

	public boolean isFinite() {
		return !((val[0] == Double.POSITIVE_INFINITY)
				|| (val[0] == Double.NEGATIVE_INFINITY)
				|| (val[1] == Double.POSITIVE_INFINITY)
				|| (val[1] == Double.NEGATIVE_INFINITY)
				|| (val[2] == Double.POSITIVE_INFINITY) || (val[2] == Double.NEGATIVE_INFINITY));
	}
}

// package geogebra.Matrix;
//
// import geogebra.kernel.Kernel;
//
// public class GgbVector3D extends GgbVector{
// private double norm, sqNorm;
// private boolean calcNorm = true;
// private boolean calcSqNorm = true;
//
// public GgbVector3D(double x, double y, double z, double w) {
// super(x,y,z,w);
// val[0]=x; val[1]=y; val[2]=z; val[3]=w;
// }
//
// public GgbVector3D(int i) {
// super(i);
// }
//
// ///////////////////////////////////////////////////:
// //setters and getters
// /** sets v(i) to val0
// * @param i number of the row
// * @param val0 value
// */
// public void set(int i, double val0){
// if(i==0) val[0]=val0;
// if(i==1) val[1]=val0;
// if(i==2) val[2]=val0;
// calcNorm=calcSqNorm=true;
// }
//
// /** sets v to vals0
// * @param vals0 values {x1, x2, ...}
// */
// public void set(double[] vals0){
// val[0]=vals0[0];
// val[1]=vals0[1];
// val[2]=vals0[2];
// val[3]=vals0[3];
// calcNorm=calcSqNorm=true;
// }
//
// /** returns v(i)
// * @param i number of the row
// * @return value*/
// public double get(int i){
// if(i==0) return val[0];
// if(i==1) return val[1];
// if(i==2) return val[2];
// else return val[3];
// }
//
// /** returns v "val[0]-coord"
// * @return val[0]-coord*/
// public double getX(){ return val[0]; }
//
// /** returns v "val[1]-coord"
// * @return val[1]-coord*/
// public double getY(){ return val[1]; }
//
// /** returns v "val[2]-coord"
// * @return val[2]-coord*/
// public double getZ(){ return val[2]; }
//
// /** returns v "val[3]-coord"
// * @return val[3]-coord*/
// public double getW(){ return val[3]; }
//
// /** sets the "val[0]-coord"
// * @param val
// */
// public void setX(double v){ val[0]=v; calcNorm=calcSqNorm=true; }
//
// /** sets the "val[1]-coord"
// * @param val
// */
// public void setY(double v){ val[1]=v; calcNorm=calcSqNorm=true; }
//
// /** sets the "val[2]-coord"
// * @param val
// */
// public void setZ(double v){ val[2]=v; calcNorm=calcSqNorm=true; }
//
// /** sets the "val[3]-coord"
// * @param val
// */
// public void setW(double v){ val[3]=v; calcNorm=calcSqNorm=true; }
//
// ///////////////////////////////////////////////////:
// //basic operations
//
// /** returns dot product this * v.
// * <p>
// * If this={x1,x2,...} and v={val[0]'1,val[0]'2,...}, the dot product is
// x1*val[0]'1+x2*val[0]'2+...
// * @param v vector multiplied with
// * @return value of the dot product*/
// public double dotproduct(GgbVector3D a){
// return val[0]*a.val[0]+val[1]*a.val[1]+val[2]*a.val[2];
// }
//
// /** returns cross product this * v.
// * Attempt that the two vectors are of dimension 3.
// * <p>
// * If this={val[0],val[1],val[2]} and v={val[0]',val[1]',val[2]'}, then cross
// product={yz'-val[1]'val[2],zx'-val[2]'val[0],xy'-yx'}
// * @param v vector multiplied with
// * @return vector resulting of the cross product
// */
// public GgbVector3D crossProduct(GgbVector3D a){
// return new
// GgbVector3D(val[1]*a.val[2]-val[2]*a.val[1],val[2]*a.val[0]-val[0]*a.val[2],
// val[0]*a.val[1]-val[1]*a.val[0],0);
// }
//
//
//
// /** returns the scalar norm.
// * <p>
// * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...).
// * Same result as Math.sqrt(this.dotproduct(this))
// * @return the scalar norm*/
// public double norm(){
// if(calcNorm){
// norm=Math.sqrt(val[0]*val[0]+val[1]*val[1]+val[2]*val[2]);
// calcNorm=false;
// }
// return norm;
// }
//
// /** returns the square of the scalar norm.
// * <p>
// * If this={x1,x2,...}, then norm=x1*x1+x2*x2+...
// * Same result as this.dotproduct(this)
// * @return the scalar norm*/
// public double squareNorm(){
// if(calcSqNorm){
// sqNorm=val[0]*val[0]+val[1]*val[1]+val[2]*val[2];
// calcSqNorm=false;
// }
// return sqNorm;
// }
//
// /** returns this normalized
// * @return this/this.norm()
// */
// public GgbVector3D normalized(){
//
// double inv;
// if(calcNorm)
// inv=1/Math.sqrt(val[0]*val[0]+val[1]*val[1]+val[2]*val[2]);
// else
// inv=1/norm;
// return new GgbVector3D(val[0]*inv, val[1]*inv, val[2]*inv, val[3]*inv);
// }
//
//
// /** normalize this */
// public GgbVector3D normalize(){
// double inv;
// if(calcNorm)
// inv=1/Math.sqrt(val[0]*val[0]+val[1]*val[1]+val[2]*val[2]);
// else
// inv=1/norm;
// val[0]*=inv;
// val[1]*=inv;
// val[2]*=inv;
// norm=sqNorm=1.0;
// return this;
// }
//
// /** returns this-v
// * @param v vector subtracted
// * @return this-v
// */
// public GgbVector3D sub(GgbVector3D v){
// return new GgbVector3D(val[0]-v.val[0],val[1]-v.val[1],val[2]-v.val[2],0);
// }
//
// /** returns this-v
// * @param v vector subtracted
// * @return this-v
// */
// public GgbVector3D add(GgbVector3D v){
// return new GgbVector3D(val[0]+v.val[0],val[1]+v.val[1],val[2]+v.val[2],0);
// }
//
// /**
// * @return
// */
// public boolean isDefined() {
// return !(val[0]!=val[0] || val[1]!=val[1] || val[2]!=val[2]);
// }
//
// /** returns a copy of the vector
// * @return a copy of the vector
// */
// public GgbVector3D copyVector(){
// return new GgbVector3D(val[0],val[1],val[2],val[3]);
//
// }
//
// public boolean isFinite() {
// return !((val[0] == Double.POSITIVE_INFINITY) || (val[0] ==
// Double.NEGATIVE_INFINITY) ||
// (val[1] == Double.POSITIVE_INFINITY) || (val[1] == Double.NEGATIVE_INFINITY)
// ||
// (val[2] == Double.POSITIVE_INFINITY) || (val[2] ==
// Double.NEGATIVE_INFINITY));
// }
// }
