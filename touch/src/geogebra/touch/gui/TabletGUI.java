package geogebra.touch.gui;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HeaderPanel;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.model.TouchModel;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends HeaderPanel implements GeoGebraTouchGUI
{
	private TabletHeaderPanel headerPanel;
	AbsolutePanel contentPanel;
	private ToolBar toolBar;

	EuclidianViewPanel euclidianViewPanel;
	private AlgebraViewPanel algebraViewPanel;
	StylingBar stylingBar;

	public static final int FOOTER_BORDER_WIDTH = 1; 
	
	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI()
	{
		// required to start the kernel
		this.euclidianViewPanel = new EuclidianViewPanel();

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
		this.headerPanel = new TabletHeaderPanel(this, kernel, touchModel.getGuiModel());
		this.setHeaderWidget(this.headerPanel);

		this.contentPanel = new AbsolutePanel();

		TouchController ec = new TouchController(touchModel, kernel.getApplication());
		ec.setKernel(kernel);

		this.euclidianViewPanel.initEuclidianView(ec);
		this.euclidianViewPanel.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		touchModel.getGuiModel().setEuclidianView(this.euclidianViewPanel.getEuclidianView());

		this.stylingBar = new StylingBar(touchModel, this.euclidianViewPanel.getEuclidianView());
		touchModel.getGuiModel().setStylingBar(this.stylingBar);

		this.algebraViewPanel = new AlgebraViewPanel(ec, kernel);

		this.contentPanel.add(this.euclidianViewPanel);
		this.contentPanel.add(this.algebraViewPanel);
		this.contentPanel.add(this.stylingBar);

		this.contentPanel.setWidgetPosition(this.euclidianViewPanel, 0, 0);
		this.contentPanel.setWidgetPosition(this.algebraViewPanel, 0, 10);
		this.contentPanel.setWidgetPosition(this.stylingBar, Window.getClientWidth() - 60, 10);
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
		this.headerPanel.onResize(event);

		this.contentPanel.setPixelSize(event.getWidth(), event.getHeight());
		this.euclidianViewPanel.setPixelSize(event.getWidth(), event.getHeight() - 122);

		this.contentPanel.setWidgetPosition(TabletGUI.this.stylingBar, Window.getClientWidth() - 60, 10);

		this.toolBar.onResize(event);
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

	public TabletHeaderPanel getTabletHeaderPanel()
	{
		return this.headerPanel;
	}
}
