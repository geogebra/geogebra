package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;

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
	public boolean isDefined() {
		return !Float.isNaN(x) && !Float.isNaN(y) && !Float.isNaN(z) ;
	}
	

	
	
	@Override
	public CoordsFloat3 copyVector() {

		return new CoordsFloat3(x, y, z);

	}
	
	
	
	@Override
	public void addInside(Coords3 v){
		x += v.getXf();
		y += v.getYf();
		z += v.getZf();
	}
	
	
	
	
	
	@Override
	public void mulInside(float v){
		x *= v;
		y *= v;
		z *= v;
	}
	
	@Override
	public void mulInside(double v){
		mulInside((float) v);
	}	

	@Override
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void set(double x, double y, double z) {
		set((float) x, (float) y, (float) z);
		
	}

	@Override
	public double getXd() {
		return x;
	}

	@Override
	public double getYd() {
		return y;
	}

	@Override
	public double getZd() {
		return z;
	}


	@Override
	public float getXf() {
		return x;
	}

	@Override
	public float getYf() {
		return y;
	}

	@Override
	public float getZf() {
		return z;
	}



	
	
}
