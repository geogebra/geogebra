package geogebra.mobile.gui;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.ClientFactory;
import geogebra.mobile.activity.TabletGuiActivity;
import geogebra.mobile.controller.MobileController;
import geogebra.mobile.gui.algebra.AlgebraViewPanel;
import geogebra.mobile.gui.elements.header.TabletHeaderPanel;
import geogebra.mobile.gui.elements.header.TabletHeaderPanelLeft;
import geogebra.mobile.gui.elements.header.TabletHeaderPanelRight;
import geogebra.mobile.gui.elements.stylingbar.StylingBar;
import geogebra.mobile.gui.elements.toolbar.ToolBar;
import geogebra.mobile.gui.euclidian.EuclidianViewPanel;
import geogebra.mobile.model.MobileModel;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends LayoutPanel implements GeoGebraMobileGUI, AcceptsOneWidget, Presenter
{
	EuclidianViewPanel euclidianViewPanel;
	LayoutPanel evAVpanel;
	TabletHeaderPanel headerPanel;
	TabletHeaderPanelLeft leftHeader;
	TabletHeaderPanelRight rightHeader;
	AlgebraViewPanel algebraViewPanel;
	ToolBar toolBar;
	StylingBar stylingBar;

	private TabletGuiActivity tabletGuiActivity;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI()
	{
		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss().ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TabletGUI.css");

		// Handle orientation changes
		MGWT.addOrientationChangeHandler(new OrientationChangeHandler()
		{
			@Override
			public void onOrientationChanged(OrientationChangeEvent event)
			{
				// TODO update whatever is shown right now, not necessarily the
				// euclidianViewPanel,
				// this is just a temporary workaround
				TabletGUI.this.euclidianViewPanel.getEuclidianView().updateSize(); 
				TabletGUI.this.euclidianViewPanel.repaint();
			}
		});

		// required to start the kernel
		this.euclidianViewPanel = new EuclidianViewPanel();
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel()
	{
		return this.euclidianViewPanel;
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		return this.algebraViewPanel;
	}

	/**
	 * Creates a new instance of {@link MobileController} and
	 * {@link MobileAlgebraController} and initializes the
	 * {@link EuclidianViewPanel euclidianViewPanel} and {@link AlgebraViewPanel
	 * algebraViewPanel} according to these instances.
	 * 
	 * @param kernel
	 *          Kernel
	 */
	@Override
	public void initComponents(final Kernel kernel)
	{
		MobileModel mobileModel = new MobileModel(kernel);

		// Initialize GUI Elements
		this.headerPanel = new TabletHeaderPanel();
		this.leftHeader = new TabletHeaderPanelLeft(this, kernel, mobileModel.getGuiModel());
		
		if(this.tabletGuiActivity != null){
			this.leftHeader.setPresenter(this.tabletGuiActivity); 
		}
		
		this.rightHeader = new TabletHeaderPanelRight(kernel);
		this.toolBar = new ToolBar();
		this.algebraViewPanel = new AlgebraViewPanel();

		MobileController ec = new MobileController(mobileModel,kernel.getApplication());
		ec.setKernel(kernel);
		this.evAVpanel = new LayoutPanel();
		this.evAVpanel.getElement().setClassName("evAVpanel");
		this.euclidianViewPanel.initEuclidianView(ec);
		mobileModel.getGuiModel().setEuclidianView(this.euclidianViewPanel.getEuclidianView());

		this.stylingBar = new StylingBar(mobileModel, this.euclidianViewPanel.getEuclidianView());
		mobileModel.getGuiModel().setStylingBar(this.stylingBar);

		this.algebraViewPanel.initAlgebraView(ec, kernel);
		this.toolBar.makeTabletToolBar(mobileModel);
		this.evAVpanel.add(this.euclidianViewPanel);
		
		this.add(this.evAVpanel);
		this.add(this.headerPanel);
		this.add(this.leftHeader);
		this.add(this.rightHeader);
		this.add(this.stylingBar);
		
		this.evAVpanel.add(this.algebraViewPanel); 
		
		this.add(this.toolBar);
	}

	public TabletHeaderPanel getTabletHeaderPanel()
	{
		return this.headerPanel;
	}

	@Override
	public void setWidget(IsWidget w)
	{
		add(w.asWidget());
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	@Override
  public void goTo(Place place)
	{
		ClientFactory.getPlaceController().goTo(place);
	}

	public void setPresenter(TabletGuiActivity tabletGuiActivity)
  {
	  if(this.leftHeader != null){
	  	this.leftHeader.setPresenter(tabletGuiActivity); 
	  }
	  this.tabletGuiActivity = tabletGuiActivity; 
  }

	
}
