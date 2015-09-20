package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Abstract class for all "euclidian" panels. 
 * 
 * @author arpad (based on EuclidianDockPanelAbstract by matthieu)
 * @remark {@link #getEuclidianView()} has to be overridden if {@link #getComponent()}
 * 			does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelWAbstract extends DockPanelW implements org.geogebra.common.euclidian.GetViewId {
	/** */
	private static final long serialVersionUID = 1L;
	private ConstructionProtocolNavigationW consProtNav;
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
	
	/**
	 * @return view in this dock panel
	 */
	abstract public EuclidianView getEuclidianView();

	@Override
	public void setVisible(boolean sv) {
		super.setVisible(sv);
		// if (getEuclidianView() != null) {// also included in:
		if (getEuclidianView() instanceof EuclidianViewWInterface) {
			((EuclidianViewWInterface) getEuclidianView()).updateFirstAndLast(
					sv,
						false);
			}
		// }
	}

	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		getEuclidianPanel().add(consProtNav.getImpl()); // may be invisible, but
														// made
													// visible later
		updateNavigationBar();
	}

	@Override
	public final void updateNavigationBar() {
		// ConstructionProtocolSettings cps = app.getSettings()
		// .getConstructionProtocol();
		// ((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
		// cps.addListener((ConstructionProtocolNavigation)consProtNav);

		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id)
				&& consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			((RequiresResize) getEuclidianPanel()).onResize();
		}
	}

	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}

	protected abstract Panel getEuclidianPanel();

}
