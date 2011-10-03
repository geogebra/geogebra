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

package edu.xtec.adapter.impl;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.net.URLEncoder;

import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

public class PropertiesUtils
{
  static public String encode(Properties prop)
  {
    try
    {
      StringBuffer sb=new StringBuffer();
      Enumeration e=prop.keys();
      while (e.hasMoreElements())
      {
        String key=(String)e.nextElement();
        String value=prop.getProperty(key);
        String ekey=URLEncoder.encode(key,"utf-8");
        String evalue=URLEncoder.encode(value,"utf-8");
        if (sb.length()>0)
          sb.append("&");
        sb.append(ekey);
        sb.append("=");
        sb.append(evalue);
      }
      return sb.toString();
    }
    catch (UnsupportedEncodingException f)
    {
      throw new Error(f);
    }
  }

  static public Properties decode(String str)
  {
    try
    {
      StringTokenizer st=new StringTokenizer(str,"&");
      Properties prop=new Properties();
      while (st.hasMoreElements())
      {
        String t=st.nextToken();
        int i=t.indexOf("=");
        if (i>0)
        {
          String key=t.substring(0,i);
          String dkey=URLDecoder.decode(key,"utf-8");
          String value=t.substring(i+1);
          String dvalue=URLDecoder.decode(value,"utf-8");
          prop.put(dkey,dvalue);
        }
      }
      return prop;
    }
    catch (UnsupportedEncodingException f)
    {
      throw new Error(f);
    } 
  }
}

