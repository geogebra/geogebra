package geogebra.common.util;

public class Cloner {
	public static double[] clone(double[] array){
		double[] arrayClone = new double[array.length];
		for (int i = 0 ; i < array.length ; i++)
			arrayClone[i] = array[i];
		return arrayClone;
	}
	
	public static double[][] clone2(double[][] array){
		double[][] arrayClone = new double[array.length][array[0].length];
		for (int i = 0 ; i < array.length ; i++)
			arrayClone[i] = clone(array[i]);
		return arrayClone;
	}
	
	/*
	public static void main(String [] args) {
		
		double [][] test0 = {{1,2},{3,4},{5,6}};
		double [][] test = clone2(test0);
		double [][] test2 = {{0,0},{0,0},{0,0}};
		
		for (int i = 0 ; i < test.length ; i++) {
			for (int j = 0 ; j < test[0].length ; j++) {
				test2[i][j] = test[i][j];
				System.err.println(test[i][j]+"");
			}
			
		}
		
	}*/
}
