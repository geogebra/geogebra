package geogebra.common.euclidian.clipping;

/**
 * Dummy implementation for DoubleArrayFactory only caring for known usage
 * For more professional implementation, the license should be minded 
 */
public class DoubleArrayFactoryImpl extends DoubleArrayFactory {

	private double [] double2 = new double[2];
	private double [] double6 = new double[6];

	/**
	 * Dummy implementation for DoubleArrayFactory.getArray only caring for known usage
	 * For more professional implementation, the license should be minded 
	 */
	@Override
	public double[] getArray(int size) {
		if (size == 2)
			return double2;
		else if (size == 6)
			return double6;
		return new double[size];
	}

	/**
	 * Dummy implementation for DoubleArrayFactory.putArray only caring for known usage
	 * For more professional implementation, the license should be minded
	 */
	@Override
	public void putArray(double[] array) {
		if (array.length == 2)
			double2 = array;
		else if (array.length == 6)
			double6 = array;
	}
}
