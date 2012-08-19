package geogebra.plugin;


/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
<h3>PlugLetInterface - Interface for GeoGebra plugin modules </h3>
<pre>
    Should also implement the Singleton DP:
    
    <B>public static PlugLetIF getInstance(); </B>
    
    (Not given in interface below, as static is not allowed in interfaces.)
</pre>
@author     H-P Ulven
@version    29.05.08
*/

public interface PlugLetIF {

	 /**
     *For GeoGebra to get information from the PlugLet 
     *  @return String  with menu text
     */
    public String getMenuText();

    /** Initializing when loaded by PluginManager
     *  
     *  @param api The API the plugin can use
     *  @param args The args given in plugin.properties
     */
    public void init(GgbAPID api,String args);
    
    /** The method to run the plugin program
     *  Called by choosing in menu.
     */
    public void execute();
    
    /** For possible future use
     *  More natural in Runnable/Threads 
     */
    public void start();

    /** For possible future use
     *  More natural in Runnable/Threads 
     */
    public void stop();
    
    /** For possible future use */
    public void destroy();
    
}//interface PlugLetIF
