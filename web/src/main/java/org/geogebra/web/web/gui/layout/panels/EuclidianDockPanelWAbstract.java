package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class for all "euclidian" panels. 
 * 
 * @author arpad (based on EuclidianDockPanelAbstract by matthieu)
 * @remark {@link #getEuclidianView()} has to be overridden if {@link #getComponent()}
 * 			does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelWAbstract extends DockPanelW
		implements GetViewId {
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
		if (getEuclidianPanel() == null) {
			loadComponent();
		}
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

	public class EuclidianPanel extends FlowPanel implements RequiresResize {

		EuclidianDockPanelWAbstract dockPanel;
		AbsolutePanel absoluteEuclidianPanel;

		int oldHeight = 0;
		int oldWidth = 0;

		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel) {
			this(dockPanel, new AbsolutePanel());

		}

		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel,
				AbsolutePanel absPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = absPanel);
			absoluteEuclidianPanel.addStyleName("EuclidianPanel");
			absoluteEuclidianPanel.getElement().getStyle()
					.setOverflow(Overflow.VISIBLE);
			checkFocus();
		}

		public void onResize() {

			if (app != null) {

				int h = dockPanel.getComponentInteriorHeight()
						- dockPanel.navHeightIfShown();
				int w = dockPanel.getComponentInteriorWidth();


				// TODO handle this better?
				// exit if new size cannot be determined
				if (h <= 0 || w <= 0) {
					return;
				}
				if (h != oldHeight || w != oldWidth) {
					dockPanel.resizeView(w, h);
					oldHeight = h;
					oldWidth = w;
				} else {
					// it's possible that the width/height didn't change but the
					// position of EV did
					dockPanel.calculateEnvironment();
				}
			}
		}

		// hack to fix GGB-697
		private native void checkFocus() /*-{
			var that = this;
			var forceResize = function() {
				that.@org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel::forceResize()()
			};
			$wnd.addEventListener("focus", function(event) {
				forceResize();
			});
		}-*/ ;

		private void forceResize() {
			EuclidianView view = dockPanel.getEuclidianView();
			if (view instanceof EuclidianViewWInterface) {
				((EuclidianViewWInterface) view).getG2P().forceResize();
				view.repaintView();
				view.suggestRepaint();
			}
		}



		public void add(Widget w, int x, int y) {
			absoluteEuclidianPanel.add(w, x, y);
		}

		@Override
		public boolean remove(Widget w) {
			return absoluteEuclidianPanel.remove(w);
		}

		public AbsolutePanel getAbsolutePanel() {
			return absoluteEuclidianPanel;
		}
	}

	protected abstract EuclidianPanel getEuclidianPanel();

	public final AbsolutePanel getAbsolutePanel() {
		return getEuclidianPanel() == null ? null : getEuclidianPanel()
				.getAbsolutePanel();
	}

	public abstract void calculateEnvironment();

	public abstract void resizeView(int width, int height);

}
