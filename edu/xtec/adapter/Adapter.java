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

import java.util.Vector;

public interface Adapter
{
  public void init();
  public void addExitListener(ExitListener el);
  public String doExit();
  public void setGrade(double g);
  public double getGrade();
  public void setMaxGrade(double g);
  public double getMaxGrade();
  public int getAttempts();
  public void setAttempts(int attempts);
  public int getDuration();
  public void setDuration(int duration);
  public String getLog();
  public void setLog(String log);
  public Vector getLogList();
  public void setLogList(Vector logList);
  public String getUserMode();
  public void setUserMode(String userMode);
  public String getState();
  public void setState(String state);
}
