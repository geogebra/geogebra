/*
 * GeoGebra - Dynamic Mathematics for Everyone http://www.geogebra.org
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation.
 */
package geogebra.gui.view;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

/**
 * Proof of concept - <b>no working implementation</b> (example of a working
 * implementation: geogebra.gui.view.ThreadedAlgebraView)
 * 
 * This class will collect update events in a time slice and bundles them in a
 * Set and after the time slice it will handle them to its attached view.
 * (Multiple updates of the same GeoElement in a time slice are only handled
 * down once at the end of the time slice.)
 * 
 * based on http://www.geogebra.org/trac/ticket/913
 * 
 * @author Lucas Binter
 */
public abstract class CompressedViewExample implements View {

  private int fps;
  private Timer updateTimer;
  private Set<GeoElement> updateSet;

  private final ReentrantLock lock = new ReentrantLock();

  /**
   * @param framesPerSecond
   *          the update cycles per second handled down to the extended View
   */
  public CompressedViewExample(int framesPerSecond) {
    fps = framesPerSecond;
    updateTimer = new Timer(1000 / fps, updateListener);
    updateSet = new HashSet<GeoElement>();
  }

  /**
   * @param geo
   *          the geo element to update
   */
  public void update(GeoElement geo) {
    if (updateTimer.isRunning()) {
      lock.lock();
      updateSet.add(geo);
      lock.unlock();
    } else {
      updateTimer.start();
      update(geo); // super.update(geo);
    }
  }

  private ActionListener updateListener = new ActionListener() {
    boolean isWorking = false;

    @SuppressWarnings("synthetic-access")
    public void actionPerformed(ActionEvent e) {
      if (updateSet.isEmpty()) {
        return;
      }
      if (isWorking) {
        updateTimer.start();
        return;
      }
      lock.lock();
      updateTimer.start();
      isWorking = true;
      GeoElement[] work;
      {
        work = new GeoElement[updateSet.size()];
        updateSet.toArray(work);
        updateSet.clear();
      }
      lock.unlock();
      for (GeoElement geo : work) {
        update(geo); // ThreadedAlgebraView.super.update(geo);
      }
      isWorking = false;
    }
  };
}