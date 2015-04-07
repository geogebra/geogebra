package org.geogebra.common.kernel.Matrix;

import org.geogebra.common.kernel.Kernel;

/**
 * class for 3 double (x, y, z)
 * @author mathieu
 *
 */
public class CoordsDouble3 extends Coords3{
	
	
	
	public double x, y, z;
	
	/**
	 * constructor
	 */
	public CoordsDouble3(){
	}
	
	/**
	 * constructor
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	public CoordsDouble3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	

	
	@Override
	final public boolean isDefined() {
		return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) ;
	}
	

	
	
	@Override
	final public CoordsDouble3 copyVector() {

		return new CoordsDouble3(x, y, z);

	}
	
	
	
	@Override
	final public void addInside(Coords3 v){
		x += v.getXd();
		y += v.getYd();
		z += v.getZd();
	}
	
	
	
	
	@Override
	final public void mulInside(float v){
		x *= v;
		y *= v;
		z *= v;
	}
	
	@Override
	final public void mulInside(double v){
		x *= v;
		y *= v;
		z *= v;
	}
	
	
	@Override
	public void normalizeIfPossible(){
		double l = Math.sqrt(x*x + y*y + z*z);
		if (!Kernel.isZero(l)){
			mulInside(1/l);
		}
	}

	@Override
	final public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	final public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
	}

	@Override
	final public double getXd() {
		return x;
	}

	@Override
	final public double getYd() {
		return y;
	}

	@Override
	final public double getZd() {
		return z;
	}


	@Override
	final public float getXf() {
		return (float) x;
	}

	@Override
	final public float getYf() {
		return (float) y;
	}

	@Override
	final public float getZf() {
		return (float) z;
	}



	
	
}
