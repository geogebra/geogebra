package geogebra.common.kernel;
/**
 * Interface for algos that are random and hence allow setting result value
 * eg from SetValue[] command
 */
public interface SetRandomValue {
	
	/**
	 * Changes the random value
	 * @param d random value
	 */
	public void setRandomValue(double d);
}
