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

package geogebra;

import edu.xtec.adapter.Adapter;
import edu.xtec.adapter.AdapterFactory;
import edu.xtec.adapter.ExitListener;

/**
 * Simple applet that uses an {@link edu.xtec.adapter.Adapter}.
 * @author Sara Arjona Tellez
 */
public class GeoGebraMoodleApplet extends GeoGebraApplet implements ExitListener {
  /**
   * The adapter.
   */
  private Adapter adapter;

  /**
   * Initialize applet: create user interface and the adapter.
   */
  public void init() {
    super.init();

    adapter = AdapterFactory.getAdapter(this);
    adapter.addExitListener(this);
    if (adapter.getState()!=null) {
    	String sState = adapter.getState();
    	super.setBase64(sState);
    }
  }

  /**
   * Callback function called by the adapter when exiting.
   */
  public void onExit() {
    adapter.setGrade(this.getValue("grade"));
    adapter.setAttempts(adapter.getAttempts()+1);
    adapter.setState(super.getBase64());
  }

  /**
   * Callback function called, potentially, by the LMS, Javascript or any other
   * mecanism. The adapter contract requires that the applet implements this 
   * method.
   * @return
   */
  public String doExit() {
    return adapter.doExit();
  }
  
  public Adapter getAdapter() {
    return adapter;
  }  
  
}
