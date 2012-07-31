package geogebra.plugin;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * <pre>
 * &lt;h3&gt;PluginManager for GeoGebra&lt;/h3&gt;
 * &lt;ul&gt;&lt;b&gt;Interface:&lt;/b&gt;
 * &lt;li&gt;PluginManager(Application);                         //Owned by Application
 * &lt;li&gt;getPluginMenu():JMenu                               //Menubar &lt;- Application &lt;- PluginManager
 * &lt;li&gt;getClassPathManipulator():ClassPathManipulator()    //For interactive adding of plugins
 * &lt;li&gt;addPath(String)                                     //For interactive adding of plugins
 * &lt;li&gt;addPlugin(classname,args)                           //For interactive adding of plugins
 * &lt;/ul&gt;
 * </pre>
 * 
 * Log:
 * 14.02.09: Updated for the new module loading fascility. (JarManager)
 * 
 * @author H-P Ulven
 * @version 23.02.09
 */
public class PluginManager implements ActionListener { // Listens on PluginMenu

	private final static boolean DEBUG = false;
	private final static String PLUGINFILE = "plugin.properties";
	// private final static String nl= System.getProperty("line.separator");

	// /// ----- Properties ----- /////

	private Hashtable<String, PlugLetIF> plugintable = new Hashtable<String, PlugLetIF>(); // 1.4.2: Not generics :-\
														// String
														// classname,pluginclass
	// private geogebra.GeoGebra ggb= null;
	private geogebra.main.AppD app = null;
	private JMenu pluginmenu = null; // Make it here, let Application and
										// Menubar get it
	private ArrayList<String> lines = new ArrayList<String>();
	private ClassPathManipulator cpm = new ClassPathManipulator();

	// /// ----- Interface ----- /////

	/** Constructor */
	public PluginManager(AppD app) {
		this.app = app; // ref to Ggb application

		//ClassPathManipulator.addURL(addPathToJar("."), null);
		String cb=AppD.getCodeBase().toString();
		if(!cb.startsWith("http://")){								//16.02.09: Don't use plugins with webstart
			ClassPathManipulator.addURL(AppD.getCodeBase(), null);            //14.02.09
		}//if webstart
		loadProperties();

	}// PluginManager()

	/** Returns pluginmenu. Called from Application */
	public JMenu getPluginMenu() {
		if (pluginmenu == null) {
			this.installPlugins(); //
		}// if null
		return pluginmenu;
	}// getPluginMenu()

	/** Returns reference to ClassPathManipulator */
	public ClassPathManipulator getClassPathManipulator() {
		return this.cpm;
	}// getClassPathManipulator()

	/** Add path for a plugin on the net to classpath */
	public void addURL(String path) {
		URL url = null;
		try {
			url = new URL(path);
			ClassPathManipulator.addURL(url, null);
		} catch (MalformedURLException e) {
			App
					.debug("addPath: MalformedURLExcepton for "
							+ path);
		} catch (Throwable e) {
			App.debug("addPath: " + e.getMessage()
					+ " for " + path);
		}// try-catch
	}// addURL(String)

	/**
	 * Installs a plugin given classname and args (Public: Can be used in
	 * scripting and interactively.)
	 */
	public void addPlugin(String cname, String args) {

		PlugLetIF plugin = null;
		JMenuItem menuitem = null;
		String menutext=null;

		// Reflect out class and install:		debug("addPlugin: " + cname + "," + args);
		plugin = getPluginInstance(cname); // reflect out an instance of plugin
		if (plugin != null) {
			debug("plugin.getMenuText(): " + plugin.getMenuText());
			try {
				plugin.init(app.getGgbApi(), args); // new syntax
				menutext=plugin.getMenuText();		//23.02.09 Use menutext instead of cname! More flexible!
				plugintable.put(menutext,plugin); //23.02.09  cname, plugin); // put in hashtable
				menuitem = new JMenuItem(menutext);//23.02.09 plugin.getMenuText()); // make menuitem
				menuitem.setName(menutext);//23.02.09 cname);
				menuitem.addActionListener(this);
				if (pluginmenu == null) {
					pluginmenu = new JMenu("Plugins");
				}
				pluginmenu.add(menuitem); // add to menu
			} catch (Throwable t) {
				App.debug("addPlugin: " + t.toString());
			}// try-catch
		} else {
			App.debug("PluginManager could not reflect out plugin "
					+ cname);
		}// if plugin null
	}// addPlugin(cname,patharray[],args)

	// /// ----- Private ----- /////

	/*
	 * installPlugins - called from constructor Makes pluginmenu from lines read
	 * from plugin.properties
	 */
	private void installPlugins() {
		String cname, args, line, rest, token = "";
		String[] tokens;
		File file;
		URL url;
		ArrayList<String> paths = new ArrayList<String>();
		Class<?> pluginclass; // class
		PlugLetIF plugin; // object
		JMenuItem menuitem = null;
		String menutext = null;
		// not here, only if not empty in addPlugin: pluginmenu=new
		// JMenu("Plugins");
		plugintable.clear();
		for (int i = 0; i < lines.size(); i++) { // for all lines in
													// plugin.properties
			paths.clear();
			args="";					//23.02.09
			line = lines.get(i);
			line = line.trim();
			if (line.startsWith("#") || // comment or
					(line.indexOf("=") == -1)) { // wrong syntax or blank
				// ignore, nothing to do...
				// debug("PluginManager ignored: "+line);
			} else {
				tokens = line.split("=");
				cname = tokens[0].trim();
				if (!cname.equals("")) { // cname exists
					args = "";
					debug("Class " + cname + ":");
					if (tokens.length > 1) { // if more...
						rest = tokens[1].trim();
						tokens = rest.split(",");
						for (int j = 0; j < tokens.length; j++) {
							token = tokens[j].trim();
							if (token.matches(".*\\.jar")) {
								debug("\tPath " + token);																
								paths.add(token);
							} else if (token.matches("\\{.*\\}")) { // args
								token = token.substring(1, token.length() - 1);
								debug("\tArgs: " + args);
								args = token; // remember args, only one...(if
												// more the last one counts)
							} else { // Must be just a path or an error...
								paths.add(token);
								debug("\tPath " + token);
							}// if-else
						}// for all params
					}// if more tokens: If not: just a class,
					// install cname:
					addPaths(paths);
					addPlugin(cname, args); // debug(cpm.getClassPath());
				}// if cname not blank
			}// if not plugin statement
		}// for all lines in plugin.properties
	}// installPlugins()

	/** Implementing ActionListener for MenuItems */
	public void actionPerformed(ActionEvent ae) {
		JMenuItem mi = (JMenuItem) ae.getSource();
		String name = mi.getName();
		Object o = plugintable.get(name);
		if (o instanceof PlugLetIF) {
			PlugLetIF plugin = plugintable.get(name);
			// plugin.execute(app.getGgbApi());
			plugin.execute();
		} else {
			App
					.debug("No PlugLetIF called " + name + "in plugintable!");
		}// if-else
	}// actionPerformed(ActionEvent)

	// / --- Private: --- ///
	private void addPaths(ArrayList<String> paths) {
		String path = null;
		int n = paths.size();
		for (int i = 0; i < n; i++) {
			path = paths.get(i);
			ClassPathManipulator.addURL(addPathToJar(path), null);
		}// for all paths
	}// addPaths(ArrayList)

	/** Get instance from class in plugintable */
	private static PlugLetIF getPluginInstance(String name) {
		PlugLetIF pluglet = null;
		String method = name + ".getInstance()"; // For exception messages
		try {
			Class<?> c = Class.forName(name);
			debug(c.getName());
			Class<?>[] empty = new Class[] {};
			Method get = c.getMethod("getInstance", empty); // Use Singleton DP!
			// Not:
			// PlugLetIF o = (PlugLetIF)c.newInstance(); //c.newInstance();
			// which is not able to enforce Singleton DP...
			// Later: Preferable not to instantiate plugin before it is
			// used/called...
			Object[] emptyobj = new Object[] {};
			/*** change */
			Object o = get.invoke(c, emptyobj);
			pluglet = (PlugLetIF) o;
		} catch (NoSuchMethodException t) {
			App.debug(method
					+ " gives NoSuchMethodExcepton.");
		} catch (IllegalAccessException e) {
			App.debug(method
					+ " gives IllegalAccesException.");
		} catch (InvocationTargetException e) {
			App.debug(method
					+ " gives InvocationTargetException");
		} catch (Throwable t) {
			App.debug(method + " gives "
					+ t.toString());
		}// end try catch
		return pluglet;
	}// getPluginInstance()

	/** Loads properties from plugin.properties */
	private void loadProperties() {
		ClassLoader loader = app.getClass().getClassLoader();

		//Application.debug("PluginManager.loadProperties " + PLUGINFILE);

		InputStream is = loader.getResourceAsStream(PLUGINFILE);
		if (is == null) {
			//Application.debug("Cannot find " + PLUGINFILE);
		} else {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				String firstLine = br.readLine();
				if (StringUtil.toLowerCase(firstLine).startsWith(
						"# geogebra plugin properties")) { // check first line
					while ((line = br.readLine()) != null) {
						lines.add(line); // debug(line);
					}// while lines
				} else {
					App.debug("Not a valid plugin.properties file");
				}// if br
			} catch (IOException ioe) {
				App.debug("IOException reading "
						+ PLUGINFILE);
			}// try-catch
		}// if is
	}// loadProperties();

	// /// ----- Debug ----- /////
	private final static void debug(String s) {
		if (DEBUG) {
			App.debug(s);
		}// if()
	}// debug()
	
	 private static URL addPathToJar(String path){
	        File file=null;
	        URL  url=null;        
	        try{
	        	if(path.startsWith("http://")){	//url!
	                //Application.debug("addPath1 "+path);
	        		url=new URL(path);
	        	}
	        	else if (path.startsWith("file:/")) { // local file in correct form
	                //Application.debug("addPath2 "+path);
	        		url = new URL(path);
	        	}else{							//file without path!

	        		//URL codeBase=GeoGebraAppletBase.codeBase; doesn't work, returns base of HTML
	        		
	        		// get applet codebase
	        		//URL codeBase = (app.getApplet()!=null) ? app.getCodeBase() : null;
	        		
	                if (AppD.getCodeBase()!=null)
	                {
	                    //Application.debug("addPath3"+path);
	                	url = new URL(AppD.getCodeBase() + path); // running as applet
	                }
	                else
	                {
	                    //Application.debug("addPath4"+path);
	                	file=new File(path);
	        		    url=file.toURI().toURL();
	                }
	        	}
	        	
	        	debug("addPath "+url.toString());
	        	
	        	return url;
	        }catch(MalformedURLException e) {
	            App.debug("addPath: MalformedURLExcepton for "+path);
	            return null;
	        }catch(Throwable e){
	            App.debug("addPath: "+e.getMessage()+" for "+path);
	            return null;
	        }//try-catch        
	    }//addPath(String)

}// class PluginManager

