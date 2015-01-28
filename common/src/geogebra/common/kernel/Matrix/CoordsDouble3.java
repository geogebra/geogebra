package geogebra.common.kernel.Matrix;

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
	public boolean isDefined() {
		return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) ;
	}
	

	
	
	@Override
	public CoordsDouble3 copyVector() {

		return new CoordsDouble3(x, y, z);

	}
	
	
	
	@Override
	public void addInside(Coords3 v){
		x += v.getXd();
		y += v.getYd();
		z += v.getZd();
	}
	
	
	
	
	@Override
	public void mulInside(float v){
		x *= v;
		y *= v;
		z *= v;
	}
	
	@Override
	public void mulInside(double v){
		x *= v;
		y *= v;
		z *= v;
	}
	

	@Override
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
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
		return (float) x;
	}

	@Override
	public float getYf() {
		return (float) y;
	}

	@Override
	public float getZf() {
		return (float) z;
	}



	
	
}
