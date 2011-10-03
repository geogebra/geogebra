/*
 * Flash and Java activities in Moodle
 * http://sourceforge.net/projects/flashjavamoodle/
 * 
 * Copyright (C) 2011 Departament d'Ensenyament de la Generalitat de Catalunya
 * 
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation.
 */
package edu.xtec.adapter;

import java.applet.Applet;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

public class AdapterFactory
{
  static String implementerClass="edu.xtec.adapter.impl.AdapterImpl";

  // TODO use weak references to store the applets
  static protected Hashtable ht=new Hashtable();

  /**
   * Creates or gets an adapter associated to a given <code>applet</code>.
   * @return
   */
  static public Adapter getAdapter(Applet applet)
  {
    Adapter adapter=(Adapter)ht.get(applet);
    if (adapter==null)
    {
      adapter=newAdapter(applet);
      ht.put(applet,adapter);
    }
    return adapter;
  }

  static protected Adapter newAdapter(Applet applet)
  {
    Adapter adapter;
    Class cls;
    try
    {
      cls = AdapterFactory.class.forName(implementerClass);
      Constructor ctr=cls.getConstructor(new Class[]{Applet.class});
      adapter = (Adapter)ctr.newInstance(new Object[]{applet});
      adapter.init();
    }
    catch (Exception ex)
    {
      throw new Error(ex);
    }
    
    return adapter;
  }
}
