/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.gui.view;

import geogebra.kernel.View;
import geogebra.kernel.geos.GeoElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;

import javax.swing.Timer;

/**
 * 
 * @author Lucas Binter
 */
public class ThreadedView implements View {
  private View view;
  private int fps;
  private Timer updateTimer;
  private Queue<GeoElement> updateQueue;

  public ThreadedView(View handledView, int framesPerSecond) {
    view = handledView;
    fps = framesPerSecond;
    updateTimer = new Timer(1000 / fps, updateListener);
  }

  public void add(GeoElement geo) {
    view.add(geo);
  }

  public void remove(GeoElement geo) {
    view.remove(geo);
  }

  public void rename(GeoElement geo) {
    view.rename(geo);
  }

  public void update(final GeoElement geo) {
    if (updateTimer.isRunning()) {
      updateQueue.add(geo);
    } else {
      updateTimer.start();
      view.update(geo);
    }
  }

  public void updateVisualStyle(GeoElement geo) {
    view.updateVisualStyle(geo);
  }

  public void updateAuxiliaryObject(GeoElement geo) {
    view.updateAuxiliaryObject(geo);
  }

  public void repaintView() {
    view.repaintView();
  }

  public void reset() {
    view.reset();
  }

  public void clearView() {
    view.clearView();
  }

  public void setMode(int mode) {
    view.setMode(mode);
  }

  public int getViewID() {
    return view.getViewID();
  }

  ActionListener updateListener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      if (updateQueue.isEmpty()) {
        return;
      }
      updateTimer.start();
      view.update(updateQueue.poll());
    }
  };
}