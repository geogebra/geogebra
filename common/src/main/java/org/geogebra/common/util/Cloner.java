package org.geogebra.common.util;

public class Cloner {
	public static double[] clone(double[] array) {

		double[] arrayClone = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			arrayClone[i] = array[i];
		}
		return arrayClone;
	}

	public static int[] clone(int[] array) {

		int[] arrayClone = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			arrayClone[i] = array[i];
		}
		return arrayClone;
	}

	public static double[][] clone2(double[][] array) {

		double[][] arrayClone = new double[array.length][array[0].length];
		for (int i = 0; i < array.length; i++) {
			arrayClone[i] = clone(array[i]);
		}
		return arrayClone;
	}

	/*
	 * public static void main(String [] args) {
	 * 
	 * double [][] test0 = {{1,2}, {3,4}, {5,6}, {7,8}}; double [][] test =
	 * clone2(test0); double [][] test2 = {{0,0},{0,0},{0,0}, {0,0}};
	 * 
	 * for (int i = 0 ; i < test0.length ; i++) { System.err.println("x"); for
	 * (int j = 0 ; j < test0[0].length ; j++) {
	 * System.err.println(test0[i][j]+""); }
	 * 
	 * }
	 * 
	 * for (int i = 0 ; i < test.length ; i++) { System.err.println("x"); for
	 * (int j = 0 ; j < test[0].length ; j++) { test2[i][j] = test[i][j];
	 * System.err.println(test[i][j]+""); }
	 * 
	 * }
	 * 
	 * double [][] Md =
	 * {{1.0,-0.7963442687575764,-0.06979045741075435,0.634164194383039
	 * ,0.05557723077302395
	 * ,0.004870707945602316},{1.0,-1.800861322021828,0.34591100508994765
	 * ,3.243101501154206
	 * ,-0.6229377499281824,0.11965442344233779},{1.0,-1.6565081306270901
	 * ,2.021124834079937
	 * ,2.7440191868336568,-3.348009720665744,4.084945594934653
	 * },{1.0,0.0,3.0,0.0
	 * ,0.0,9.0},{1.0,1.6565081306270901,2.0211248340799375,2.7440191868336568
	 * ,3.348009720665745
	 * ,4.084945594934655},{1.0,1.8008613220218281,0.3459110050899481
	 * ,3.2431015011542064
	 * ,0.6229377499281833,0.11965442344233809},{1.0,0.7963442687575764
	 * ,-0.06979045741075435
	 * ,0.634164194383039,-0.05557723077302395,0.004870707945602316}}; //
	 * computed by Giac double [][] expectedV =
	 * {{-0.1600457194306,-0.2571741974603
	 * ,-0.9189675127038,-0.252478732664,6.134954255981E-015
	 * ,9.312288130132E-016}
	 * ,{2.792092708021E-016,2.134378240825E-016,-2.395328134286E-015
	 * ,6.036400035815E-015
	 * ,0.5692862939624,0.8221393528512},{-0.3487543593972,-0.009373107044909
	 * ,0.306665278579
	 * ,-0.8855726670239,1.186740030005E-016,-3.262054021043E-016}
	 * ,{-0.2608065371823
	 * ,-0.912241915565,0.2463995341682,0.1976914444557,2.145345293392E-014
	 * ,4.637458572027E-017
	 * },{3.74668595676E-017,-1.397294594507E-016,2.603626211168E-016
	 * ,-4.132848644997E-015
	 * ,0.8221393528512,-0.5692862939624},{-0.8858530999305,
	 * 0.3187291397273,-0.02724678092536
	 * ,0.336055997955,-7.50072084587E-015,-1.591284138866E-016}} ;
	 * 
	 * Array2DRowRealMatrix M = new Array2DRowRealMatrix(Md);
	 * 
	 * SingularValueDecomposition svd = new SingularValueDecompositionImpl(M);
	 * 
	 * RealMatrix V = svd.getV();
	 * 
	 * System.out.println("M = " + M); System.out.println("V = " + V);
	 * System.out.println("expected V = " + (new
	 * Array2DRowRealMatrix(expectedV)));
	 * 
	 * }
	 */
}
