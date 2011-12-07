/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.gui.view;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

/**
 * Proof of concept
 * This class will collect update events in a time slice and bundles
 * them in a Set and after the time slice it will handle them to its
 * attached view. (Multiple updates of the same GeoElement in a time
 * slice are only handled down once at the end of the time slice.)
 * @author Lucas Binter
 */
public class ThreadedView implements View {
  private View view;
  private int fps;
  private Timer updateTimer;
  private Set<GeoElement> updateSet;

  public ThreadedView(View handledView, int framesPerSecond) {
    view = handledView;
    fps = framesPerSecond;
    updateTimer = new Timer(1000 / fps, updateListener);
    updateSet = new HashSet<GeoElement>();
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
      synchronized (updateSet) {
        updateSet.add(geo);
      }
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
    boolean isWorking = false;

    public void actionPerformed(ActionEvent e) {
      if (updateSet.isEmpty()) {
        return;
      }
      if (isWorking) {
        updateTimer.start();
        return;
      }
      updateTimer.start();
      isWorking = true;
      GeoElement[] work;
      synchronized (updateSet) {
        work = new GeoElement[updateSet.size()];
        updateSet.toArray(work);
        updateSet.clear();
      }
      for (GeoElement geo : work) {
        view.update(geo);
      }
      isWorking = false;
    }
  };
  
  public void add(GeoElementInterface geo) {
		add((GeoElement)geo);
		
	}

	public void remove(GeoElementInterface geo) {
		remove((GeoElement)geo);
		
	}

	public void rename(GeoElementInterface geo) {
		rename((GeoElement)geo);
		
	}

	public void update(GeoElementInterface geo) {
		update((GeoElement)geo);
		
	}

	public void updateVisualStyle(GeoElementInterface geo) {
		updateVisualStyle((GeoElement)geo);
		
	}

	public void updateAuxiliaryObject(GeoElementInterface geo) {
		updateAuxiliaryObject((GeoElement)geo);
		
	}
}