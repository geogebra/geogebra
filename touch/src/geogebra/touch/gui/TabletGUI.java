package geogebra.touch.gui;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.gui.laf.DefaultLAF;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.gui.laf.WindowsStoreLAF;
import geogebra.touch.model.TouchModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends HeaderPanel implements GeoGebraTouchGUI
{
	SplitLayoutPanel contentPanel;
	private ToolBar toolBar;

	EuclidianViewPanel euclidianViewPanel;
	private AlgebraViewPanel algebraViewPanel;
	StylingBar stylingBar;
	private LookAndFeel laf;
	List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();

	public static final int FOOTER_BORDER_WIDTH = 1;

	public void addResizeListener(ResizeListener rl)
	{
		this.resizeListeners.add(rl);
	}

	public static GColor getBackgroundColor()
	{
		return GColor.LIGHT_GRAY;
	}

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI()
	{
		// required to start the kernel
		this.euclidianViewPanel = new EuclidianViewPanel();
		if ("win".equals(RootPanel.getBodyElement().getAttribute("data-param-laf")))
		{
			this.laf = new WindowsStoreLAF();
		}
		else
		{
			this.laf = new DefaultLAF();
		}

	}

	/**
	 * Creates a new instance of {@link TouchController} and
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
		TouchModel touchModel = new TouchModel(kernel);

		// Initialize GUI Elements
		this.laf.buildHeader(this, (TouchApp) kernel.getApplication(), touchModel.getGuiModel());

		this.contentPanel = new SplitLayoutPanel();

		TouchController ec = new TouchController(touchModel, kernel.getApplication());
		ec.setKernel(kernel);

		this.euclidianViewPanel.initEuclidianView(ec);
		this.euclidianViewPanel.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		touchModel.getGuiModel().setEuclidianView(this.euclidianViewPanel.getEuclidianView());

		this.stylingBar = new StylingBar(touchModel, this.euclidianViewPanel.getEuclidianView());
		touchModel.getGuiModel().setStylingBar(this.stylingBar);

		this.algebraViewPanel = new AlgebraViewPanel(ec, kernel);

		this.contentPanel.addWest(this.algebraViewPanel, (int) (Window.getClientWidth() * 0.2));
		this.contentPanel.addEast(this.euclidianViewPanel, (int) (Window.getClientWidth() * 0.8));

		this.euclidianViewPanel.add(this.stylingBar);
		this.euclidianViewPanel.setWidgetPosition(this.stylingBar, Window.getClientWidth() - 60, 10);

		this.contentPanel.setWidgetMinSize(this.algebraViewPanel, (int) (Window.getClientWidth() * 0.2));
		this.contentPanel.setWidgetMinSize(this.euclidianViewPanel, (int) (Window.getClientWidth() * 0.8));

		this.setContentWidget(this.contentPanel);

		this.toolBar = new ToolBar(touchModel);
		this.setFooterWidget(this.toolBar);
		this.getFooterWidget().getElement().getStyle().setBorderWidth(FOOTER_BORDER_WIDTH, Unit.PX);
		this.getFooterWidget().getElement().getStyle().setBorderColor(GColor.BLACK.toString());
		this.getFooterWidget().getElement().getStyle().setBorderStyle(BorderStyle.SOLID);

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(ResizeEvent event)
			{
				TabletGUI.this.onResize(event);
			}
		});
	}

	protected void onResize(ResizeEvent event)
	{
		for (ResizeListener res : this.resizeListeners)
		{
			res.onResize(event);
		}

		this.contentPanel.setPixelSize(event.getWidth(), event.getHeight());
		this.euclidianViewPanel.setPixelSize(event.getWidth(), event.getHeight() - this.laf.getPanelsHeight());

		this.euclidianViewPanel.setWidgetPosition(TabletGUI.this.stylingBar, Window.getClientWidth() - 60, 10);

		this.toolBar.setWidth(event.getWidth() + "px");
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

	public LookAndFeel getLAF()
	{
		return this.laf;
	}
}
