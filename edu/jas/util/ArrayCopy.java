package edu.jas.util;

public class ArrayCopy {

    public static <T> T[] copyOf(T[] a, int length) {
    	T [] copy = (T[]) new Object[length];
    	for (int i=0; i < length; i++) {
    		copy[i] = a[i];
    	}
    	return copy;
    }
}
