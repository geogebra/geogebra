package geogebra.common.euclidian.clipping;

public class DoubleArrayFactoryImpl extends DoubleArrayFactory {

	double [] double2 = new double[2];
	double [] double6 = new double[6];

	/**
	 * Dummy implementation for DoubleArrayFactory.getArray only caring for known usage
	 * For more professional implementation, the license should be minded 
	 */
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
	public void putArray(double[] array) {
		if (array.length == 2)
			double2 = array;
		else if (array.length == 6)
			double6 = array;
	}
}
