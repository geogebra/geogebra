package geogebra.web.gui.layout.panels;

import geogebra.common.euclidian.EuclidianView;
import geogebra.web.gui.layout.DockPanelW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * Abstract class for all "euclidian" panels. 
 * 
 * @author arpad (based on EuclidianDockPanelAbstract by matthieu)
 * @remark {@link #getEuclidianView()} has to be overridden if {@link #getComponent()}
 * 			does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelWAbstract extends DockPanelW implements geogebra.common.euclidian.GetViewId {
	/** */
	private static final long serialVersionUID = 1L;
	
	private boolean hasEuclidianFocus;

	/**
	 * default constructor
	 * @param id
	 * @param title
	 * @param toolbar
	 * @param hasStyleBar
	 * @param menuOrder
	 */
	public EuclidianDockPanelWAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, int menuOrder, char shortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder, shortcut);
	}

	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 * @param hasFocus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
	}
	
	@Override
	protected boolean titleIsBold(){
		return super.titleIsBold() || hasEuclidianFocus;
	}

	@Override
	public boolean updateResizeWeight(){
		return true;
	}

	public abstract AbsolutePanel getEuclidianPanel();
	public abstract Canvas getCanvas();
	public abstract EuclidianView getEuclidianView();
}
