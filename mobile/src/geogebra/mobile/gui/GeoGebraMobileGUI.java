package geogebra.mobile.gui;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.main.App;
import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;

public interface GeoGebraMobileGUI
{
	public EuclidianViewPanel getEuclidianViewPanel();

	public AlgebraViewPanel getAlgebraViewPanel();
	
	public void initComponents(EuclidianController ec); 
}
