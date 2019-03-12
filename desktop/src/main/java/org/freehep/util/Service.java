// Copyright 2003, FreeHEP.
package org.freehep.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;

/**
 * This class does the same as sun.misc.Service, which may become public in some
 * java or javax package at some point. See Sun BUG# 4640520.
 *
 * @author Mark Donszelmann
 * @version $Id: Service.java,v 1.5 2008-10-23 19:04:04 hohenwarter Exp $
 */
public class Service {

	private Service() {
	}

	public static Collection providers(Class service, ClassLoader loader) {
		List classList = new ArrayList();
		List nameSet = new ArrayList();
		String name = "META-INF/services/" + service.getName();
		Enumeration services;
		try {
			services = (loader == null) ? ClassLoader.getSystemResources(name)
					: loader.getResources(name);
		} catch (IOException ioe) {
			Log.debug("Service: cannot load " + name);
			return classList;
		}

		while (services.hasMoreElements()) {
			URL url = (URL) services.nextElement();
			// Application.debug(url);
			InputStream input = null;
			BufferedReader reader = null;
			try {
				input = url.openStream();
				reader = new BufferedReader(
						new InputStreamReader(input, Charsets.getUtf8()));
				String line = reader.readLine();
				while (line != null) {
					int ci = line.indexOf('#');
					if (ci >= 0) {
						line = line.substring(0, ci);
					}
					line = line.trim();
					int si = line.indexOf(' ');
					if (si >= 0) {
						line = line.substring(0, si);
					}
					line = line.trim();
					if (line.length() > 0) {
						if (!nameSet.contains(line)) {
							nameSet.add(line);
						}
					}
					line = reader.readLine();
				}
			} catch (IOException ioe) {
				Log.debug("Service: problem with: " + url);
			} finally {
				try {
					if (input != null) {
						input.close();
					}
					if (reader != null) {
						reader.close();
					}
				} catch (IOException ioe2) {
					Log.debug("Service: problem with: " + url);
				}
			}
		}

		Iterator names = nameSet.iterator();
		while (names.hasNext()) {
			String className = (String) names.next();
			try {
				classList.add(
						Class.forName(className, true, loader).newInstance());
			} catch (ClassNotFoundException e) {
				Log.debug("Service: cannot find class: " + className);
			} catch (InstantiationException e) {
				Log.debug("Service: cannot instantiate: " + className);
			} catch (IllegalAccessException e) {
				Log.debug("Service: illegal access to: " + className);
			} catch (NoClassDefFoundError e) {
				Log.debug("Service: " + e + " for " + className);
			} catch (Exception e) {
				Log.debug("Service: exception for: " + className + " " + e);
			}
		}
		return classList;
	}

	public static Collection providers(Class service) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return Service.providers(service, loader);
	}

	public static Collection installedProviders(Class service) {
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		ClassLoader previous = null;
		while (loader != null) {
			previous = loader;
			loader = loader.getParent();
		}
		return providers(service, previous);
	}
}
