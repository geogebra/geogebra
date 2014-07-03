package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.html5.gui.ResizeListener;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.TouchModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TouchGUI extends VerticalPanel implements GeoGebraTouchGUI {

	public static final int STYLEBAR_WIDTH = 140;
	public static final int TOOLBAR_HEIGHT = 60;
	protected final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	protected TouchModel touchModel;
	
	protected ToolBar toolBar;
	protected EuclidianViewPanel euclidianViewPanel;
	protected AlgebraViewPanel algebraViewPanel;
	protected StyleBar styleBar;

	protected TouchApp app;
	protected Kernel kernel;
	private boolean editing = true;
	protected boolean rtl;

	protected TouchController touchController;

	public TouchGUI() {
		this.euclidianViewPanel = new EuclidianViewPanel();
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
	}
	
	@Override
	public void allowEditing(boolean b) {
		if (this.editing == b) {
			return;
		}
		this.editing = b;
		this.resetMode();
		this.toolBar.setVisible(b);
		this.setAlgebraVisible(this.isAlgebraShowing());
		this.styleBar.setVisible(b);

		if (b) {
			this.touchModel.getGuiModel().setStyleBar(this.styleBar);
		} else {
			this.touchModel.getGuiModel().setStyleBar(null);
		}
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel() {
		return this.algebraViewPanel;
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel() {
		return this.euclidianViewPanel;
	}

	@Override
	public void initComponents(Kernel kernel, boolean isRtl) {
		this.rtl = isRtl;
		this.kernel = kernel;
		if(this.rtl){
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.rtlStyle().getText());
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.additionalRtlStyle().getText());
			TouchEntryPoint.getLookAndFeel().loadRTLStyles();
		}
		if(!TouchEntryPoint.isTablet()) {
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.phoneStyle().getText());
		}
		//url parameter for debugging, cannot be used in app
		if("true".equals(Location.getParameter("ios7")) ||
				Window.Navigator.getUserAgent().contains("CPU OS 7")){
			StyleInjector.injectStylesheet(DefaultResources.INSTANCE.ios7Style().getText());
		}
		this.touchModel = new TouchModel(this.kernel);
		this.app = (TouchApp) this.kernel.getApplication();

		this.touchController = new TouchController(this.touchModel, this.app);
		this.touchController.setKernel(this.kernel);
		
		initGUIElements();
	}

	@Override
	public boolean isAlgebraShowing() {
		return false;
	}

	@Override
	public void resetMode() {
		this.touchModel.getGuiModel().setActive(
				this.touchModel.getGuiModel().getDefaultButton());
	}

	@Override
	public void resetController() {
		this.touchController.reset();
	}

	@Override
	public void updateViewSizes() {

	}

	@Override
	public void setAlgebraVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLabels() {
	}

	@Override
	public ToolBar getToolBar() {
		return this.toolBar;
	}

	@Override
	public void addResizeListener(ResizeListener rl) {
		this.resizeListeners.add(rl);
	}
	
	public TouchModel getTouchModel() {
		return this.touchModel;
	}

	@Override
	public void initGUIElements() {
		// TODO Auto-generated method stub
		
	}

	public boolean isRTL() {
		return this.rtl;
	}

	public Object getAlgebraViewButtonPanel() {
		return null;
	}

	public Object getStylebar() {
		// TODO Auto-generated method stub
		return null;
	}

}
