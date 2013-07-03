package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.SelectionManager;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;

import com.google.gwt.user.client.Element;

/**
 * An Interface for geogebra.touch.gui.GeoGebraTouchGUI.
 */
public interface GeoGebraTouchGUI
{
	public EuclidianViewPanel getEuclidianViewPanel();

	public AlgebraViewPanel getAlgebraViewPanel();

	public void initComponents(Kernel kernel);
	
	public Element getElement();

	/**
	 * Set labels of all components that were already initialized and need i18n
	 */
	public void setLabels();

	public void updateStylingBar(SelectionManager selectionManager);
}
