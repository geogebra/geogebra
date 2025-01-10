// Copyright 2000, CERN, Geneva, Switzerland and SLAC, Stanford, U.S.A.
package org.freehep.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Methods for Package Info (version, name, ...)
 *
 * @author Mark Donszelmann
 * @version $Id: PackageInfo.java,v 1.4 2009-06-22 02:18:20 hohenwarter Exp $
 */

public class PackageInfo {

	// static class
	private PackageInfo() {
	}

	/**
	 * retrieves the name
	 */
	public static String getName(Class clazz, String name) {
		return getInfo(clazz, name, "TITLE");
	}

	/**
	 * retrieves the version
	 */
	public static String getVersion(Class clazz, String name) {
		return getInfo(clazz, name, "VERSION");
	}

	/**
	 * retrieves the info for the package of this class either from the MANIFEST
	 * file or from the given text file situated at the root of the jar file
	 */
	public static String getInfo(Class clazz, String name, String property) {
		Package p = clazz.getPackage();
		String info = null;
		if (p != null) {
			if (property.equals("TITLE")) {
				info = p.getSpecificationTitle();
			} else if (property.equals("VERSION")) {
				info = p.getSpecificationVersion();
			}
		}

		if (info == null) {
			try {
				Properties props = new Properties();
				InputStream in = clazz
						.getResourceAsStream("/" + name + "-version.txt");
				props.load(in);
				in.close();

				info = props.getProperty(property);
			} catch (IOException ioe) {
			} catch (NullPointerException npe) {
			}
		}
		return info;
	}
}
