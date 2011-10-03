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

import java.applet.Applet;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import edu.xtec.adapter.Adapter;
import edu.xtec.adapter.ExitListener;

public class AdapterImpl implements Adapter
{
  static public final String defaultUserMode="user";

  private Date startTime;
  private double grade;
  private double maxGrade;
  private ExitListener exitListener;
  private int attempts;
  private int duration;
  private String log;
  private Vector logList;
  private String userMode;
  private String state;
  private Applet applet;
  
  public AdapterImpl(Applet applet)
  {
    attempts=0;
    duration=-1;
    this.applet=applet;
  }

  public String doExit()
  {
    if (exitListener!=null)
    {
      exitListener.onExit();
    }
    Properties prop=new Properties();
    prop.put("grade",""+grade);
    prop.put("maxGrade",""+maxGrade);
    prop.put("version","1.0");
    prop.put("implementation",this.getClass().getName());
    prop.put("test-encoding","&=á ");
    if (getState()!=null)
    {
      prop.put("state",getState());
    }
    if (duration>0)
      prop.put("duration",""+duration);
    else
      prop.put("duration",""+(new Date().getTime()-startTime.getTime())/1000);
    prop.put("attempts",""+getAttempts());

    return PropertiesUtils.encode(prop);
  }

  public void init()
  {
    String param=applet.getParameter("edu.xtec.adapter.parameters");
    if (param!=null)
    {
      String tmp;
      Properties prop=PropertiesUtils.decode(param);
      setState(prop.getProperty("state"));
      try{
    	  String sGrade = prop.getProperty("grade");
    	  if (sGrade!=null){
    		  double dGrade = Double.parseDouble(sGrade);
    		  setGrade(dGrade);
    	  }
      }catch (NumberFormatException nfe){ }
      try{
    	  String sAttempts = prop.getProperty("attempts");
    	  if (sAttempts!=null){
    		  int iAttempts = Integer.parseInt(sAttempts);
    		  setAttempts(iAttempts);
    	  }
      }catch (NumberFormatException nfe){ }
      tmp=prop.getProperty("userMode");
      if (tmp!=null)
      {
        this.setUserMode(tmp);
      }
    }
    startTime=new Date();
  }

  public void addExitListener(ExitListener el)
  {
    exitListener=el;
  }

  public void setGrade(double g)
  {
    grade=g;
  }

  public double getGrade()
  {
    return grade;
  }

  public int getAttempts()
  {
    return attempts;
  }

  public void setAttempts(int attempts)
  {
    this.attempts = attempts;
  }

  public int getDuration()
  {
    return duration;
  }

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public String getLog()
  {
    return log;
  }

  public void setLog(String log)
  {
    this.log = log;
  }

  public Vector getLogList()
  {
    return logList;
  }

  public void setLogList(Vector logList)
  {
    this.logList = logList;
  }

  public String getUserMode()
  {
    if (userMode==null) return defaultUserMode;
    return userMode;
  }

  public void setUserMode(String userMode)
  {
    this.userMode = userMode;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public void setMaxGrade(double g)
  {
    maxGrade=g;
  }

  public double getMaxGrade()
  {
    return maxGrade;
  }
}
