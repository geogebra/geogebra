package geogebra.mobile.gui;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.mobile.algebra.AlgebraViewM;
import geogebra.mobile.controller.MobileAlgebraController;
import geogebra.mobile.controller.MobileEuclidianController;
import geogebra.mobile.euclidian.EuclidianViewM;
import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.gui.elements.TabletHeaderPanel;
import geogebra.mobile.gui.elements.TabletHeaderPanelLeft;
import geogebra.mobile.gui.elements.TabletHeaderPanelRight;
import geogebra.mobile.gui.elements.toolbar.ToolBar;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;

/**
 * Coordinates the GUI of the tablet.
 * 
 */

public class TabletGUI implements GeoGebraMobileGUI
{
	private EuclidianViewPanel euclidianViewPanel;
	private TabletHeaderPanel headerPanel;
	private TabletHeaderPanelLeft leftHeader;
	private TabletHeaderPanelRight rightHeader;
	private AlgebraViewPanel algebraViewPanel;
	private ToolBar toolBar;
	
	private GuiModel guiModel;

	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI()
	{
		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss()
				.ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TabletGUI.css");

		// Initialize GUI Elements
		this.headerPanel = new TabletHeaderPanel();
		this.leftHeader = new TabletHeaderPanelLeft();
		this.rightHeader = new TabletHeaderPanelRight();
		this.toolBar = new ToolBar();
		this.euclidianViewPanel = new EuclidianViewPanel();
		this.algebraViewPanel = new AlgebraViewPanel();
		
		layout();
	}

	/**
	 * Sets the layout of the whole tablet.
	 */
	public void layout()
	{
		RootPanel.get().add(this.euclidianViewPanel);

		RootPanel.get().add(this.headerPanel);

		// TODO: add again
		// RootPanel.get().add(this.rightHeader);
		// RootPanel.get().add(this.leftHeader);

		RootPanel.get().add(this.toolBar);

		RootPanel.get().add(this.algebraViewPanel);		
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
	 * {@link EuclidianViewPanel euclidianViewPanel} and
	 * {@link AlgebraViewPanel algebraViewPanel} according to these instances.
	 * 
	 * @param kernel
	 *            Kernel
	 */
	@Override
	public void initComponents(final Kernel kernel)
	{
		// TODO add other stuff
		this.guiModel = new GuiModel();

		MobileEuclidianController ec = new MobileEuclidianController();
		ec.setKernel(kernel);
		ec.setGuiModel(this.guiModel);
		this.euclidianViewPanel.initEuclidianView(ec);

		MobileAlgebraController ac = new MobileAlgebraController(kernel);
		this.algebraViewPanel.initAlgebraView(ac, kernel);
		this.toolBar.makeTabletToolBar(this.guiModel, ac);

		
		AlgebraViewM algebraViewM = this.algebraViewPanel.getAlgebraViewM(); //for tests only
		EuclidianViewM euclidianViewM = this.euclidianViewPanel.getEuclidianViewM(); //for tests only
		System.out.println(algebraViewM.toString()); // for tests only
		euclidianViewM.setAlgebraViewM(algebraViewM); //for tests only
		
	}
}
