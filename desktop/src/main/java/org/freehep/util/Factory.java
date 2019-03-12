// Copyright 2000-2004, FreeHEP.
package org.freehep.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;

/**
 * Methods to use factories (a la JAXP).
 *
 * @author Mark Donszelmann
 * @version $Id: Factory.java,v 1.5 2008-10-23 19:04:04 hohenwarter Exp $
 */
public class Factory {

	// static class
	private Factory() {
	}

	/**
	 * Find the correct factory name, based on the following procedure:
	 * <ol>
	 * <li>Use the specified system property: factoryName.
	 * <li>Use the specified property file in JAVA_HOME/lib/&lt;factoryFile&gt; and
	 * the specified property: factoryName.
	 * <li>Use the Services API (as detailed in the JAR specification) to look
	 * for the classname in META-INF/services/&lt;factoryName&gt;.
	 * <li>Use the specfied default factory: defaultFactory.
	 * </ol>
	 *
	 * @param factoryName
	 *            name to be used for lookup
	 * @param factoryFile
	 *            filename to look for in JAVA_HOME/lib
	 * @param defaultFactory
	 *            name of the default factory
	 * @return the name of the factory according to the procedure above
	 */
	public static String findFactory(String factoryName, String factoryFile,
			String defaultFactory) {
		// try System Property
		String factory = null;
		try {
			factory = System.getProperty(factoryName);
			if (factory != null) {
				return factory;
			}
		} catch (SecurityException securityexception) {
		}

		// try JAVA_HOME/lib
		try {
			String javaHome = System.getProperty("java.home");
			String configFile = javaHome + File.separator + "lib"
					+ File.separator + factoryFile;
			File file = new File(configFile);
			if (file.exists()) {
				Properties props = new Properties();
				props.load(new FileInputStream(file));
				factory = props.getProperty(factoryName);
				if (factory != null) {
					return factory;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try Services API
		String service1 = "meta-inf/services/" + factoryName;
		String service2 = "META-INF/services/" + factoryName;
		try {
			ClassLoader loader = null;
			try {
				loader = Class.forName("org.freehep.util.Factory")
						.getClassLoader();
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}

			InputStream stream = null;
			if (loader == null) {
				stream = ClassLoader.getSystemResourceAsStream(service1);
			} else {
				stream = loader.getResourceAsStream(service1);
			}

			if (stream == null) {
				if (loader == null) {
					stream = ClassLoader.getSystemResourceAsStream(service2);
				} else {
					stream = loader.getResourceAsStream(service2);
				}
			}

			if (stream != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stream, Charsets.getUtf8()));
				factory = reader.readLine();
				reader.close();
				if (factory != null && !factory.equals("")) {
					return factory;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultFactory;
	}

	public static Object loadFactory(String name, String file,
			String defaultImplementation) {
		String factoryName = findFactory(name, file, defaultImplementation);
		try {
			Log.debug("Loading factory: " + factoryName);
			Class factoryClass = Class.forName(factoryName);
			return factoryClass.newInstance();
		} catch (Exception e) {
			Log.debug("Unable to load factory: " + factoryName);
			e.printStackTrace();
		}
		return null;
	}
}
