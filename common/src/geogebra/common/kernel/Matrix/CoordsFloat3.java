package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;

/**
 * class for 3 floats (x, y, z)
 * @author mathieu
 *
 */
public class CoordsFloat3 {
	
	/** undefined vector */
	public static final CoordsFloat3 UNDEFINED = new CoordsFloat3(0f, 0f, 0f) {
		@Override
		public boolean isNotFinalUndefined() {
			return false;
		}

		@Override
		public boolean isFinalUndefined() {
			return true;
		}
	};
	
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

	/**
	 * set values
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	public boolean isDefined() {
		return !Float.isNaN(x) && !Float.isNaN(y) && !Float.isNaN(z) ;
	}
	

	/**
	 * @return true if not a final (constant) undefined
	 */
	public boolean isNotFinalUndefined() {
		return true;
	}

	/**
	 * @return true if a final (constant) undefined
	 */
	public boolean isFinalUndefined() {
		return false;
	}
	
	/**
	 * returns a copy of the vector
	 * 
	 * @return a copy of the vector
	 */
	public CoordsFloat3 copyVector() {

		return new CoordsFloat3(x, y, z);

	}
	
	
	/**
	 * add values of v inside this
	 * @param v vector
	 */
	public void addInside(CoordsFloat3 v){
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	/**
	 * multiply all values by v
	 * @param v factor
	 */
	public void mulInside(float v){
		x *= v;
		y *= v;
		z *= v;
	}

	/**
	 * set this to v normalized
	 * @param v vector
	 */
	public void setNormalized(Coords v) {
		double l = v.calcNorm();
		x = (float) (v.getX()/l);
		y = (float) (v.getY()/l);		
		z = (float) (v.getZ()/l);
		
	}

	
	/**
	 * set this to v normalized or (0, 0, 0) if v=0 
	 * @param v vector
	 */
	public void setNormalizedIfPossible(Coords v) {
		double vx = v.getX();
		double vy = v.getY();
		double vz = v.getZ();
		
		double l = vx * vx + vy * vy + vz * vz;
		
		if (Kernel.isZero(l)){
			set(0, 0, 0);
		}else{	
			l = Math.sqrt(l);
			x = (float) (vx/l);
			y = (float) (vy/l);		
			z = (float) (vz/l);
		}
		
	}
}
