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

package geogebra.util;

import geogebra.main.AppD;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class Util extends geogebra.common.util.Util {

    /** Creates new Util */
    public Util() {
    }
    private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
	    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private static StringBuilder hexSB = null;

    /**
     * copy an object (deep copy)
     *
     final public static Object copy(Object ob) {
     Object ret = null;
     
     try {
     // write object to memory
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ObjectOutputStream os = new ObjectOutputStream(out);
     os.writeObject(ob);
     os.flush();
     os.close();
     out.close();
     
     // get object from memory
     ByteArrayInputStream in =
     new ByteArrayInputStream(out.toByteArray());
     ObjectInputStream is = new ObjectInputStream(in);
     ret = is.readObject();
     is.close();
     in.close();
     } catch (Exception exc) {
     Application.debug(
     "deep copy of " + ob + " failed:\n" + exc.toString());
     }
     return ret;
     }*/

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
	    //        HttpURLConnection.setInstanceFollowRedirects(false)
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("HEAD");
	    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	} catch (Exception e) {
	    AppD.debug("Exception: existsHttpURL: " + url);
	    return false;
	}
    }
    
    
    /**
     * Returns the main version number of the current JRE, e.g.
     * 1.4 for version 1.4.2_03
     */
    public static double getJavaVersion() {
	String vm_version = System.getProperty("java.version");
	if (vm_version == null)
	    return Double.NaN;

	if (vm_version.startsWith("1.4.2"))
	    return 1.42;
	else
	    vm_version = vm_version.substring(0, 3);
	try {
	    return Double.parseDouble(vm_version);
	} catch (Exception e) {
	    return Double.NaN;
	}
    }

    /**
     * Returns the index of ob in array a
     * @return -1 if ob is not in a
     */
    public static int arrayContains(Object[] a, Object ob) {
	if (a == null)
	    return -1;
	for (int i = 0; i < a.length; i++) {
	    if (a[i] == ob)
		return i;
	}
	return -1;
    }

    /**
     * Adds keylistener recursivley to all subcomponents of container.
     * @param l
     */
    public static void addKeyListenerToAll(Container cont, KeyListener l) {
	cont.addKeyListener(l);
	Component[] comps = cont.getComponents();
	for (int i = 0; i < comps.length; i++) {
	    if (comps[i] instanceof Container)
		addKeyListenerToAll((Container) comps[i], l);
	    else {
		comps[i].addKeyListener(l);
	    }
	}
    }

    /**
     *  Writes all contents of the given InputStream to a byte array.
     */
    public static byte[] loadIntoMemory(InputStream is) throws IOException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	copyStream(is, bos);
	bos.close();
	return bos.toByteArray();
    }

    /**
     *  Writes all contents of the given InputStream to a String
     */
    public static String loadIntoString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
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
     * @param dialog JDialog to be closed on escape
     */
    public static void registerForDisposeOnEscape(JDialog dialog) {
	JRootPane root = dialog.getRootPane();
	
	root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "dispose-on-escape");
	root.getActionMap().put("dispose-on-escape", new DisposeDialogAction(dialog));
    }
    
    /**
     * Removes all characters that are neither letters nor digits from the filename
     * and changes the given file accordingly.
     */
    public static String keepOnlyLettersAndDigits(String name) {
		int length = name != null ? name.length() : 0;
    	
    	StringBuilder sb = new StringBuilder();
		for (int i=0; i < length ; i++) {
			char c = name.charAt(i);
			if (Character.isLetterOrDigit(c) ||
				c == '.' || // Michael Borcherds 2007-11-23
				c == '_')  // underscore
			{
				sb.append(c);
			}
			else
			{
				sb.append('_'); // Michael Borcherds 2007-11-23
			}
		}
		
		if (sb.length() == 0) {
			sb.append("geogebra");
		}
		
		return sb.toString();
	}           
    
	private static Comparator<File> comparator;
	
	       
    
	/**
	 * Returns a comparator for GeoText objects.
	 * If equal, doesn't return zero (otherwise TreeSet deletes duplicates)
	 */
	public static Comparator<File> getFileComparator() {
		if (comparator == null) {
			comparator = new Comparator<File>() {
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

}
