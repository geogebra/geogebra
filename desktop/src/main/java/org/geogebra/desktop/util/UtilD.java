/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/*
 * Util.java
 *
 * Created on 17. November 2001, 18:23
 */

package org.geogebra.desktop.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.geogebra.common.jre.util.StreamUtil;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.debug.Log;

/**
 *
 * @author Markus Hohenwarter
 */
public class UtilD {


	/**
	 * copy an object (deep copy)
	 *
	 * final public static Object copy(Object ob) { Object ret = null;
	 * 
	 * try { // write object to memory ByteArrayOutputStream out = new
	 * ByteArrayOutputStream(); ObjectOutputStream os = new
	 * ObjectOutputStream(out); os.writeObject(ob); os.flush(); os.close();
	 * out.close();
	 * 
	 * // get object from memory ByteArrayInputStream in = new
	 * ByteArrayInputStream(out.toByteArray()); ObjectInputStream is = new
	 * ObjectInputStream(in); ret = is.readObject(); is.close(); in.close(); }
	 * catch (Exception exc) { Application.debug( "deep copy of " + ob +
	 * " failed:\n" + exc.toString()); } return ret; }
	 */

	/**
	 * searches the classpath for a filename and returns a File object
	 */
	final public static File findFile(String filename) {
		// search file
		URL url = ClassLoader.getSystemResource(filename);
		return new File(url.getFile());
	}

	/**
	 * searches the classpath for a filename and returns an URL object
	 */
	final public static URL findURL(String filename) {
		// search file
		URL url = ClassLoader.getSystemResource(filename);
		return url;
	}

	final public static boolean existsHttpURL(URL url) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (RuntimeException e) {
			Log.debug("Exception: existsHttpURL: " + url);
			return false;
		} catch (Exception e) {
			Log.debug("Exception: existsHttpURL: " + url);
			return false;
		}
	}

	/**
	 * Returns the index of ob in array a
	 * 
	 * @return -1 if ob is not in a
	 */
	public static int arrayContains(Object[] a, Object ob) {
		if (a == null) {
			return -1;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] == ob) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Adds keylistener recursively to all subcomponents of container.
	 * 
	 * @param l
	 */
	public static void addKeyListenerToAll(Container cont, KeyListener l) {
		cont.addKeyListener(l);
		Component[] comps = cont.getComponents();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof Container) {
				addKeyListenerToAll((Container) comps[i], l);
			} else {
				comps[i].addKeyListener(l);
			}
		}
	}

	/**
	 * Writes all contents of the given InputStream to a byte array.
	 */
	public static byte[] loadIntoMemory(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		copyStream(is, bos);
		bos.close();
		return bos.toByteArray();
	}

	public static String loadFileIntoString(String filename) {

		InputStream ios = null;
		try {
			ios = new FileInputStream(new File(filename));
			return loadIntoString(ios);
		} catch (Exception e) {
			Log.error("problem loading " + filename);
		} finally {
			StreamUtil.closeSilent(ios);
		}

		return null;

	}

	public static byte[] loadFileIntoByteArray(String filename) {

		File file = new File(filename);

		byte[] buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if (ios.read(buffer) == -1) {
				Log.error("problem loading " + filename);
				return null;
			}
			return buffer;
		} catch (RuntimeException e) {
			Log.error("problem loading " + filename);
		} catch (Exception e) {
			Log.error("problem loading " + filename);
		} finally {
			try {
				if (ios != null) {
					ios.close();
				}
			} catch (IOException e) {
				Log.error("problem loading " + filename);
			}
		}
		return null;

	}

	/**
	 * Writes all contents of the given InputStream to a String
	 */
	public static String loadIntoString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is, Charsets.getUtf8()));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > -1) {
			out.write(buf, 0, len);
		}
	}

	/**
	 * Registers dialog for disposal on escape key-press.
	 * 
	 * @param dialog
	 *            JDialog to be closed on escape
	 */
	public static void registerForDisposeOnEscape(JDialog dialog) {
		JRootPane root = dialog.getRootPane();

		root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke("ESCAPE"), "dispose-on-escape");
		root.getActionMap().put("dispose-on-escape",
				new DisposeDialogAction(dialog));
	}

	/**
	 * Removes all characters that are neither letters nor digits from the
	 * filename and changes the given file accordingly.
	 */
	public static String keepOnlyLettersAndDigits(String name) {
		int length = name != null ? name.length() : 0;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = name.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '.' || c == '_') // underscore
			{
				sb.append(c);
			} else {
				sb.append('_');
			}
		}

		if (sb.length() == 0) {
			sb.append("geogebra");
		}

		return sb.toString();
	}

	private static Comparator<File> comparator;

	/**
	 * Returns a comparator for GeoText objects. If equal, doesn't return zero
	 * (otherwise TreeSet deletes duplicates)
	 */
	public static Comparator<File> getFileComparator() {
		if (comparator == null) {
			comparator = new Comparator<File>() {
				@Override
				public int compare(File itemA, File itemB) {

					return itemA.getName().compareTo(itemB.getName());
				}
			};
		}

		return comparator;
	}

	public static String getIPAddress() {
		return (String) AccessController
				.doPrivileged(new PrivilegedAction<Object>() {
					@Override
					public Object run() {
						try {
							InetAddress addr = InetAddress.getLocalHost();
							// Get host name
							return addr.getHostAddress();
						} catch (UnknownHostException e) {
							return "";
						}
					}
				});
	}

	public static String getHostname() {
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				try {
					InetAddress addr = InetAddress.getLocalHost();
					// Get host name
					return addr.getHostName();
				} catch (UnknownHostException e) {
					return "";
				}
			}
		});
	}

	/**
	 * 
	 * Writes file as UTF-8
	 * 
	 * @param s
	 *            string to write
	 * @param filename
	 *            filename
	 */
	public static void writeStringToFile(String s, String filename) {

		Writer out;
		try {

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), Charsets.getUtf8()));

			try {
				out.write(s);
			} finally {
				out.close();
			}

		} catch (Exception e) {
			Log.error("problem writing file " + filename);
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Writes file as UTF-8
	 * 
	 * @param s
	 *            string to write
	 * @param file
	 *            filename
	 */
	public static void writeStringToFile(String s, File file) {

		Writer out;
		try {

			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), Charsets.getUtf8()));

			try {
				out.write(s);
			} finally {
				out.close();
			}

		} catch (Exception e) {
			Log.error("problem writing file " + file.getName());
			e.printStackTrace();
		}

	}

	/**
	 * @param bytes
	 *            to write
	 * @param filename
	 *            filename
	 */
	public static void writeByteArrayToFile(byte[] bytes, String filename) {
		try {

			FileOutputStream out = new FileOutputStream(filename);

			try {
				out.write(bytes);
			} finally {
				out.close();
			}

		} catch (Exception e) {
			Log.error("problem writing file " + filename);
			e.printStackTrace();
		}

	}

	private static String tempDir = null;

	public static String getTempDir() {

		if (tempDir == null) {
			tempDir = System.getProperty("java.io.tmpdir");

			// Mac OS doesn't add "/" at the end of directory path name
			if (!tempDir.endsWith(File.separator)) {
				tempDir += File.separator;
			}
		}

		return tempDir;

	}

	/**
	 * Creates a directory
	 * 
	 * @param prefDir
	 *            directory
	 */
	public static void mkdirs(File prefDir) {
		if (!prefDir.exists() && !prefDir.mkdirs()) {
			Log.warn("Could not create " + prefDir.getAbsolutePath());
		}
	}

	/**
	 * Deletes a file
	 * 
	 * @param dest
	 *            file
	 */
	public static void delete(File dest) {
		if (!dest.delete()) {
			Log.warn("Could not delete " + dest.getAbsolutePath());
		}
	}

}
