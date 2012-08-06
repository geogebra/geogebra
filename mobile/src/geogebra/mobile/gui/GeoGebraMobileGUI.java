package geogebra.mobile.gui;

import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;

public interface GeoGebraMobileGUI
{
	public EuclidianViewPanel getEuclidianViewPanel();

	public AlgebraViewPanel getAlgebraViewPanel();
}
