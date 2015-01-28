package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;

/**
 * class for 3 floats (x, y, z)
 * @author mathieu
 *
 */
public abstract class Coords3 {
	
	/** undefined vector */
	public static final Coords3 UNDEFINED = new CoordsFloat3(0f, 0f, 0f) {
		@Override
		public boolean isNotFinalUndefined() {
			return false;
		}

		@Override
		public boolean isFinalUndefined() {
			return true;
		}
	};
	
	
	
	

	/**
	 * set values
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract public void set(float x, float y, float z);

	/**
	 * set values
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract public void set(double x, double y, double z);

	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	abstract public boolean isDefined();
	

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
	abstract public Coords3 copyVector();
	
	
	/**
	 * add values of v inside this
	 * @param v vector
	 */
	abstract public void addInsideFloat(Coords3 v);
	
	/**
	 * multiply all values by v
	 * @param v factor
	 */
	abstract public void mulInside(float v);

	
	
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
			set(vx/l, vy/l, vz/l);
		}
		
	}
	
	/**
	 * 
	 * @return x coord
	 */
	abstract public double getXd();
	
	/**
	 * 
	 * @return x coord
	 */
	abstract public double getYd();
	
	/**
	 * 
	 * @return x coord
	 */
	abstract public double getZd();
	
	/**
	 * 
	 * @return x coord
	 */
	abstract public float getXf();
	
	/**
	 * 
	 * @return x coord
	 */
	abstract public float getYf();
	
	/**
	 * 
	 * @return x coord
	 */
	abstract public float getZf();
	

}
