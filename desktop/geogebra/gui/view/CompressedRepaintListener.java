package geogebra.gui.view;

import geogebra.common.kernel.geos.GeoElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

/**
 * 
 * @author Lucas Binter
 */
public class CompressedRepaintListener implements ActionListener {
  private Timer repaintTimer;

  private CompressedView view;

  /**
   * @param timer
   *          the updateTimer to invoke / use
   * @param set
   *          a set containing all changed geo elements
   * @param view
   *          the compressedView attached this ActionListener is attached to
   * @param lock
   *          the lock to avoid loosing changed GeoElements (view.update(geo) =>
   *          set.add(geo) lock)
   */
  public CompressedRepaintListener(CompressedView view, Timer timer) {
    repaintTimer = timer;
    this.view = view;
  }

  public void actionPerformed(ActionEvent e) {
    repaintTimer.start();
    view.repaintNow();
  }

}
