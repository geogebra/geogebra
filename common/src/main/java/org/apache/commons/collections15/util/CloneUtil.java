/**
 * 
 */
package org.apache.commons.collections15.util;

import java.util.Arrays;

/**
 * @author dave.trudes
 *
 */
public class CloneUtil {

	@SuppressWarnings("unchecked")
	public static <T> T[] clone(T[] array) {
        return (T[])Arrays.asList(array).toArray();
    }
	
	public static byte[] clone(byte[] array){
		System.out.println("clone byte[] called");
		byte[] copy = new byte[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static char[] clone(char[] array){
		System.out.println("clone char[] called");
		char[] copy = new char[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static int[] clone(int[] array){
		System.out.println("clone int[] called");
		int[] copy = new int[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static double[] clone(double[] array){
		System.out.println("clone double[] called");
		double[] copy = new double[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static float[] clone(float[] array){
		System.out.println("clone float[] called");
		float[] copy = new float[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static long[] clone(long[] array){
		System.out.println("clone long[] called");
		long[] copy = new long[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static short[] clone(short[] array){
		System.out.println("clone short[] called");
		short[] copy = new short[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
	
	public static boolean[] clone(boolean[] array){
		System.out.println("clone boolean[] called");
		boolean[] copy = new boolean[array.length];
		for (int i=0; i<array.length; i++) copy[i] = array[i];
		return copy;
	}
}
