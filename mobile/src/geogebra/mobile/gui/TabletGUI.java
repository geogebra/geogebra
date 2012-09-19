package geogebra.mobile.gui;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.controller.MobileAlgebraController;
import geogebra.mobile.controller.MobileEuclidianController;
import geogebra.mobile.gui.algebra.AlgebraViewPanel;
import geogebra.mobile.gui.elements.header.TabletHeaderPanel;
import geogebra.mobile.gui.elements.header.TabletHeaderPanelLeft;
import geogebra.mobile.gui.elements.header.TabletHeaderPanelRight;
import geogebra.mobile.gui.elements.stylingbar.StylingBar;
import geogebra.mobile.gui.elements.toolbar.ToolBar;
import geogebra.mobile.gui.euclidian.EuclidianViewPanel;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.model.MobileModel;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI implements GeoGebraMobileGUI
{
	EuclidianViewPanel euclidianViewPanel;
	TabletHeaderPanel headerPanel;
	TabletHeaderPanelLeft leftHeader;
	TabletHeaderPanelRight rightHeader;
	AlgebraViewPanel algebraViewPanel;
	ToolBar toolBar;
	StylingBar stylingBar;

	private GuiModel guiModel = new GuiModel();

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

				TabletGUI.this.euclidianViewPanel.repaint();

			}

		});
		// Initialize GUI Elements
		this.headerPanel = new TabletHeaderPanel();
		this.leftHeader = new TabletHeaderPanelLeft();
		this.rightHeader = new TabletHeaderPanelRight();
		this.toolBar = new ToolBar();
		this.euclidianViewPanel = new EuclidianViewPanel();
		this.algebraViewPanel = new AlgebraViewPanel();
		this.stylingBar = new StylingBar(this.guiModel);

		this.guiModel.setStylingBar(this.stylingBar);

		RootPanel.get().add(this.euclidianViewPanel);
		RootPanel.get().add(this.headerPanel);
		RootPanel.get().add(this.leftHeader);
		RootPanel.get().add(this.rightHeader);
		RootPanel.get().add(this.stylingBar);
		RootPanel.get().add(this.algebraViewPanel);
		RootPanel.get().add(this.toolBar);
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
	 * Creates a new instance of {@link MobileEuclidianController} and
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
		MobileModel mobileModel = new MobileModel(this.guiModel, kernel);

		MobileEuclidianController ec = new MobileEuclidianController(mobileModel, this.guiModel);
		ec.setKernel(kernel);
		this.euclidianViewPanel.initEuclidianView(ec);
		this.guiModel.setEuclidianView(this.euclidianViewPanel.getEuclidianView());

		MobileAlgebraController ac = new MobileAlgebraController(kernel, mobileModel);
		this.algebraViewPanel.initAlgebraView(ac, kernel);
		this.toolBar.makeTabletToolBar(this.guiModel, ac);

	}
}
