package geogebra.mobile.gui;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.gui.algebra.AlgebraViewPanel;
import geogebra.mobile.gui.euclidian.EuclidianViewPanel;

/**
 * An Interface for geogebra.mobile.gui.GeoGebraMobileGUI.
 */
public interface GeoGebraMobileGUI
{
	public EuclidianViewPanel getEuclidianViewPanel();

	public AlgebraViewPanel getAlgebraViewPanel();

	public void initComponents(Kernel kernel);
}
