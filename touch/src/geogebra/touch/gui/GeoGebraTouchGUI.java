package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;

import com.google.gwt.user.client.Element;

/**
 * An Interface for geogebra.touch.gui.GeoGebraTouchGUI.
 */
public interface GeoGebraTouchGUI {
  public void allowEditing(boolean b);

  public AlgebraViewPanel getAlgebraViewPanel();

  public Element getElement();

  public EuclidianViewPanel getEuclidianViewPanel();

  public void initComponents(Kernel kernel);

  public boolean isAlgebraShowing();

  public void resetMode();

  public void setAlgebraVisible(boolean visible);

  /**
   * Set labels of all components that were already initialized and need i18n
   */
  public void setLabels();

  // TODO: use with SelectionManager
  // public void updateStylingBar(SelectionManager selectionManager);
}
