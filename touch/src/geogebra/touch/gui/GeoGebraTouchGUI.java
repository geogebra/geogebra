package geogebra.touch.gui;

import com.google.gwt.user.client.Element;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;

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
}
