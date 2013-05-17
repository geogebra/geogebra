package geogebra.html5.gawt;

public class CloningUtils {
	CloningUtils() {
	}

	public static double[] copyOf(double[] v, int newlength){
		double[] ret = new double[newlength];
		System.arraycopy(v, 0, ret, 0, Math.min(newlength, v.length));
		return ret;
	}

	public static int[] copyOf(int[] v, int newlength){
		int[] ret = new int[newlength];
		System.arraycopy(v, 0, ret, 0, Math.min(newlength, v.length));
		return ret;
	}
	public static byte[] copyOf(byte[] v, int newlength){
		byte[] ret = new byte[newlength];
		System.arraycopy(v, 0, ret, 0, Math.min(newlength, v.length));
		return ret;
	}
	public static float[] copyOf(float[] v, int newlength){
		float[] ret = new float[newlength];
		System.arraycopy(v, 0, ret, 0, Math.min(newlength, v.length));
		return ret;
	}
}
