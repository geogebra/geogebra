/**
 * 
 */
package geogebra.web.gui.app;

import geogebra.web.Web;
import geogebra.web.presenter.LoadFilePresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * Creates the App base structure.
 *
 */
public class GeoGebraAppFrame extends Composite {

	interface Binder extends UiBinder<DockLayoutPanel, GeoGebraAppFrame> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();
	
	@UiField GGWToolBar ggwToolBar;
	@UiField GGWCommandLine ggwCommandLine;
	@UiField GGWViewWrapper ggwViewWrapper;
	@UiField GGWGraphicsView ggwGraphicsView;
	
	/**
	 * Creates a GUI (main entry point of GUI);
	 * 
	 */
	public void createGui() {
		DockLayoutPanel outer = binder.createAndBindUi(this);

	    // Get rid of scrollbars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
	    Window.enableScrolling(false);
	    Window.setMargin("0px");

	    // Add the outer panel to the RootLayoutPanel, so that it will be
	    // displayed.
	    RootLayoutPanel root = RootLayoutPanel.get();
	    root.add(outer);
	    root.forceLayout();
	    
	    init();
  }

	private void init() {
	    //here will be similar like GeoGebraFrame
    }

}
