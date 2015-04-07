package org.geogebra.common.kernel.Matrix;

import org.geogebra.common.kernel.Kernel;

/**
 * class for 3 floats (x, y, z)
 * @author mathieu
 *
 */
public class CoordsFloat3 extends Coords3{
	
	
	
	public float x, y, z;
	
	/**
	 * constructor
	 */
	public CoordsFloat3(){
	}
	
	/**
	 * constructor
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	public CoordsFloat3(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	

	
	@Override
	final public boolean isDefined() {
		return !Float.isNaN(x) && !Float.isNaN(y) && !Float.isNaN(z) ;
	}
	

	
	
	@Override
	final public CoordsFloat3 copyVector() {

		return new CoordsFloat3(x, y, z);

	}
	
	
	
	@Override
	final public void addInside(Coords3 v){
		x += v.getXf();
		y += v.getYf();
		z += v.getZf();
	}
	
	
	
	
	
	@Override
	final public void mulInside(float v){
		x *= v;
		y *= v;
		z *= v;
	}
	
	@Override
	final public void mulInside(double v){
		mulInside((float) v);
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
		set((float) x, (float) y, (float) z);
		
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
		return x;
	}

	@Override
	final public float getYf() {
		return y;
	}

	@Override
	final public float getZf() {
		return z;
	}



	
	
}
