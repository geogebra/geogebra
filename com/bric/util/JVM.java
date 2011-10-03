/*
 * @(#)JVM.java
 *
 * $Date: 2010-01-26 10:38:02 -0600 (Tue, 26 Jan 2010) $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */
package com.bric.util;

import java.security.AccessControlException;

/** Static methods relating to the JVM environment.
 * <P>Instead of burying a constant like "isQuartz" in its most
 * relevant class (such as OptimizedGraphics2D), it should be
 * stored here so if other classes need to access it they don't
 * necessary have to 
 */
public class JVM {

	/** Prints basic information about this session's JVM:
	 * the OS name & version, the Java version, and (on Mac) whether Quartz is being used.
	 */
	public static void printProfile() {
		System.out.println(getProfile());
	}
	
	/** Gets basic information about this session's JVM:
	 * the OS name & version, the Java version, and (on Mac) whether Quartz is being used.
	 */
	public static String getProfile() {
		StringBuffer sb = new StringBuffer();
		sb.append("OS = "+System.getProperty("os.name")+" ("+System.getProperty("os.version")+"), "+System.getProperty("os.arch")+"\n");
		sb.append("Java Version = "+System.getProperty("java.version")+"\n");
		if(JVM.isMac) {
			sb.append("apple.awt.graphics.UseQuartz = "+usingQuartz);
		}
		return sb.toString();
	}
	
	/** The major Java version being used (1.4, 1.5, 1.6, etc.), or
	 * -1 if this value couldn't be correctly determined.
	 */
	public static final float javaVersion = JVM.getMajorJavaVersion(true);

	/** Whether this session is on a Mac. */
	public static final boolean isMac = (System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1);
	
	/** Whether this session is on Windows. */
	public static final boolean isWindows = (System.getProperty("os.name").toLowerCase().indexOf("windows")!=-1);

	/** Whether this session is on Vista. */
    public static final boolean isVista = (System.getProperty("os.name").toLowerCase().indexOf("vista")!=-1);
	
	/** If on a Mac: whether Quartz is the rendering pipeline. */
	public static final boolean usingQuartz = isMac && ((javaVersion>0 && javaVersion<1.4f) ||
		(System.getProperty("apple.awt.graphics.UseQuartz")!=null && System.getProperty("apple.awt.graphics.UseQuartz").toString().equals("true")));
	
	/** This converts the system property "java.version" to a float value.
	 * This drops rightmost digits until a legitimate float can be parsed.
	 * <BR>For example, this converts "1.6.0_05" to "1.6".
	 * <BR>This value is cached as the system property "java.major.version".  Although
	 * technically this value is a String, it will always be parseable as a float.
	 * @throws AccessControlException this may be thrown in unsigned applets!  Beware!
	 */
	public static float getMajorJavaVersion() throws AccessControlException {
		String majorVersion = System.getProperty("java.major.version");
		if(majorVersion==null) {
			String s = System.getProperty("java.version");
			float f = -1;
			int i = s.length();
			while(f<0 && i>0) {
				try {
					f = Float.parseFloat(s.substring(0,i));
				} catch(Exception e) {}
				i--;
			}
			majorVersion = Float.toString(f);
			System.setProperty("java.major.version",majorVersion);
		}
		return Float.parseFloat(majorVersion);
	}

	/** 
	 * 
	 * @param catchSecurityException if true and an exception occurs,
	 * then -1 is returned.
	 * @return the major java version, or -1 if this can't be determined/
	 */
	public static float getMajorJavaVersion(boolean catchSecurityException) {
		try {
			return getMajorJavaVersion();
		} catch(RuntimeException t) {
			if(catchSecurityException) {
				System.err.println("this exception was ignored without incident, but it means we can't determine the major java version:");
				t.printStackTrace();
				return -1;
			}
			throw t;
		}
	}
}
