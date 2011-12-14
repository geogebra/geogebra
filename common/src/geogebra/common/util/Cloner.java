package geogebra.common.util;

public class Cloner {
	public static double[] clone(double[] array){
		double[] arrayClone = new double[array.length];
		for(int i=0;i<array.length;i++)
			arrayClone[i]=array[i];
		return arrayClone;
	}
}
