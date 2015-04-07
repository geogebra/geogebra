package org.geogebra.desktop.plugin;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * <h3>Class to manipulate Classpath</h3> Hack: Use reflection to overcome
 * protected modifiers, not nice, but it works...
 * <ul>
 * Interface:
 * <li>addFile(String)
 * <li>addFile(File)
 * <li>addURL(URL)
 * <li>getClassPath():String
 * <li>listClassPath()
 * </ul>
 * 
 * @author H-P Ulven
 * @version 04.06.08
 */
public final class ClassPathManipulator {

	// private static String nl = System.getProperty("line.separator");

	/** Adds a file give as String to the Classpath */
	public synchronized static boolean addFile(String s) {
		File f = new File(s);
		return addFile(f);
	}// end method

	/** Adds a file given as File to the Classpath */
	public synchronized static boolean addFile(File f) {
		try {
			addURL(f.toURI().toURL(), null); // System.err.println(f.toURL());
			return true;
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException for " + f.getName());
			return false;
		}// try-catch
	}// addFile(File)

	/** Adds a URL to the Classpath */
	public synchronized static boolean addURL(URL u, ClassLoader loader) {
		// URLClassLoader sysloader =
		// (URLClassLoader)ClassLoader.getSystemClassLoader();
		// URLClassLoader sysloader =
		// (URLClassLoader)geogebra.gui.menubar.MenubarImpl
		// .class.getClassLoader();

		if (loader == null)
			loader = ClassLoader.getSystemClassLoader();
		URLClassLoader sysloader = (URLClassLoader) loader;
		Class<URLClassLoader> sysclass = URLClassLoader.class;

		// check if URL u is already on classpath
		URL[] classpath = sysloader.getURLs();
		if (classpath != null) {
			for (int i = 0; i < classpath.length; i++) {
				// u found on classpath
				if (classpath[i].equals(u)) {
					// System.out.println("ClassPathManipulator.addURL(): already on classpath: "
					// + u);
					return true;
				}
			}
		}

		try {
			Class[] parameter = new Class[] { URL.class };
			Method method = sysclass.getDeclaredMethod("addURL", parameter);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
			return true;
		} catch (NoSuchMethodException t) {
			System.err
					.println("ClassPathManipulator: addURL gives NoSuchMethodExcepton.");
			return false;
		} catch (IllegalAccessException e) {
			System.err
					.println("ClassPathManipulator: addURL gives IllegalAccesException.");
			return false;
		} catch (InvocationTargetException e) {
			System.err
					.println("ClassPathManipulator: addURL gives InvocationTargetException");
			return false;
		} catch (Throwable t) {
			System.err.println("ClassPathManipulator: addURL gives "
					+ t.getMessage());
			return false;
		}// end try catch
	}// addURL(URL)
		//
	// /** Lists the URLs int the Classpath */
	// public static void listClassPath(ClassLoader cl) {
	// System.out.println(getClassPath(cl));
	// }// listClassPath()
	//
	// public static String getClassPath(ClassLoader loader) {
	// String urlsstr = "Classpath:" + nl;
	// Class[] emptyparameter = new Class[] {};
	//
	// if (loader == null)
	// loader = ClassLoader.getSystemClassLoader();
	// URLClassLoader sysloader = (URLClassLoader) loader;
	// Class sysclass = URLClassLoader.class;
	// URL[] urls = null;
	// try {
	// Method method = sysclass.getDeclaredMethod("getURLs",
	// emptyparameter);
	// method.setAccessible(true);
	// Object obs = method.invoke(sysloader, new Object[] {});
	// urls = (URL[]) obs;
	// for (int i = 0; i < urls.length; i++) {
	// urlsstr += urls[i].toString() + nl;
	// }// for
	// } catch (NoSuchMethodException t) {
	// System.err.println("ClassPathManipulator: getURL gives NoSuchMethodExcepton.");
	// } catch (IllegalAccessException e) {
	// System.err.println("ClassPathManipulator: getURL gives IllegalAccesException.");
	// } catch (InvocationTargetException e) {
	// System.err.println("ClassPathManipulator: getURL gives InvocationTargetException");
	// } catch (Throwable t) {
	// System.err.println("ClassPathManipulator: getURL gives "
	// + t.getMessage());
	// }// end try catch
	// return urlsstr;
	// }// getClassPath()
	//
	//
	//

}// class ClassPathManipulator