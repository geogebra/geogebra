package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.html5.gui.ResizeListener;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.toolbar.ToolBar;
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
	
	public ToolBar getToolBar();

	public void initComponents(Kernel kernel, boolean rtl);
	
	public void initGUIElements();

	public boolean isAlgebraShowing();

	public void resetMode();

	public void resetController();

	public void updateViewSizes();

	public void setAlgebraVisible(boolean visible);

	/**
	 * Set labels of all components that were already initialized and need i18n
	 */
	public void setLabels();

	public void addResizeListener(final ResizeListener rl);
	
	// TODO: use with SelectionManager
	// public void updateStylingBar(SelectionManager selectionManager);
}
