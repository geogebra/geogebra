package geogebra.touch.gui;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.ArrowImageButton;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarCommand;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends HeaderPanel implements GeoGebraTouchGUI
{
	public static final float ALGEBRA_VIEW_WIDTH_FRACTION = 0.2f;
	public static final int FOOTER_BORDER_WIDTH = 1;
	private static final int ALGEBRA_BUTTON_WIDTH = 50;

	List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();

	private TouchModel touchModel;

	DockLayoutPanel contentPanel;
	private ToolBar toolBar;

	EuclidianViewPanel euclidianViewPanel;
	AlgebraViewPanel algebraViewPanel;
	StylingBar stylingBar;

	private LayoutPanel algebraViewButtonPanel;
	private Panel algebraViewArrowPanel;
	StandardImageButton algebraViewButton;
	private TouchApp app;

	private boolean editing = true;

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
		this.touchModel = new TouchModel(kernel, this);
		this.app = (TouchApp) kernel.getApplication();
		// Initialize GUI Elements
		TouchEntryPoint.getLookAndFeel().buildHeader(this, this.app, this.touchModel);

		this.contentPanel = new DockLayoutPanel(Unit.PX);

		TouchController ec = new TouchController(this.touchModel, this.app);
		ec.setKernel(kernel);

		int width = Window.getClientWidth() - computeAlgebraWidth();
		int height = Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getPanelsHeight();
		this.euclidianViewPanel.setPixelSize(width, height);
		this.euclidianViewPanel.initEuclidianView(ec, super.getHeaderWidget(), width, height);

		this.stylingBar = new StylingBar(this.touchModel, this.euclidianViewPanel.getEuclidianView(), this.euclidianViewPanel);
		this.touchModel.getGuiModel().setStylingBar(this.stylingBar);

		this.algebraViewPanel = new AlgebraViewPanel(ec, kernel);

		this.contentPanel.addEast(this.algebraViewPanel, computeAlgebraWidth());
		this.contentPanel.add(this.euclidianViewPanel);
		this.contentPanel.setHeight("100%");

		this.euclidianViewPanel.add(this.stylingBar);
		this.euclidianViewPanel.setWidgetPosition(this.stylingBar, 0, 0);

		this.setContentWidget(this.contentPanel);

		this.toolBar = new ToolBar(this.touchModel, this.app, this);
		this.setFooterWidget(this.toolBar);

		// show/hide AlgebraView Button
		this.algebraViewButtonPanel = new LayoutPanel();
		this.algebraViewArrowPanel = new FlowPanel();
		this.algebraViewButton = new ArrowImageButton(getLaf().getIcons().triangle_left());

		this.algebraViewArrowPanel.setStyleName("algebraViewArrowPanel");
		this.algebraViewButton.setStyleName("arrowRight");

		// this.algebraViewButtonPanel.setAutoHideEnabled(false);
		// this.algebraViewButtonPanel.show();

		// for Win8 position it on top, for others right under appbar

		// this.algebraViewButtonPanel.setPopupPosition(width -
		// ALGEBRA_BUTTON_WIDTH,
		// TouchEntryPoint.getLookAndFeel().getAppBarHeight());
		this.euclidianViewPanel.add(this.algebraViewButtonPanel);
		this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel, width - TabletGUI.ALGEBRA_BUTTON_WIDTH, 0);

		this.algebraViewButtonPanel.setStyleName("algebraViewButtonPanel");

		this.algebraViewButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.algebraViewButton.addDomHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				event.stopPropagation();
				TabletGUI.this.toggleAlgebraView();
				if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null)
				{
					TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel().enableDisableButtons();
				}
			}
		}, MouseDownEvent.getType());

		this.algebraViewButton.addDomHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				event.stopPropagation();
			}
		}, TouchStartEvent.getType());

		this.algebraViewButtonPanel.add(this.algebraViewArrowPanel);
		this.algebraViewArrowPanel.add(this.algebraViewButton);

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(ResizeEvent event)
			{
				TabletGUI.this.onResize(event);
			}
		});
	}

	public static int computeAlgebraWidth()
	{
		return Math.max(250, (int) (Window.getClientWidth() * ALGEBRA_VIEW_WIDTH_FRACTION));
	}

	private static LookAndFeel getLaf()
	{
		return TouchEntryPoint.getLookAndFeel();
	}

	public void addResizeListener(ResizeListener rl)
	{
		this.resizeListeners.add(rl);
	}

	public static GColor getBackgroundColor()
	{
		return GColor.LIGHT_GRAY;
	}

	protected void onResize(ResizeEvent event)
	{
		for (ResizeListener res : this.resizeListeners)
		{
			res.onResize(event);
		}

		this.contentPanel.setPixelSize(event.getWidth(), event.getHeight() - getLaf().getPanelsHeight());
		this.contentPanel.onResize();
		updateViewSizes(this.algebraViewPanel.isVisible());

		this.toolBar.setWidth(event.getWidth() + "px");

		this.touchModel.getGuiModel().closeOptions();
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel()
	{
		return this.euclidianViewPanel;
	}

	public Widget getEuWidget()
	{
		return this.getContentWidget();
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		return this.algebraViewPanel;
	}

	void toggleAlgebraView()
	{
		this.setAlgebraVisible(!this.algebraViewPanel.isVisible());
		this.app.setUnsaved();
	}

	public void updateViewSizes(boolean algebraVisible)
	{

		int panelHeight = this.editing ? TouchEntryPoint.getLookAndFeel().getPanelsHeight() : TouchEntryPoint.getLookAndFeel().getAppBarHeight();
		if (!algebraVisible)
		{
			this.contentPanel.setWidgetSize(this.algebraViewPanel, 0);
			this.euclidianViewPanel.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - panelHeight);

			// for Win8 position it on top, for others under appbar

			// this.algebraViewButtonPanel.setPopupPosition(Window.getClientWidth() -
			// ALGEBRA_BUTTON_WIDTH,
			// TouchEntryPoint.getLookAndFeel().getAppBarHeight());
			this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel, Window.getClientWidth() - ALGEBRA_BUTTON_WIDTH, 0);

			this.algebraViewButton.setStyleName("arrowLeft");

			// Set algebraviewbutton transparent, when algebra view is closed
			this.algebraViewButtonPanel.addStyleName("transparent");
		}
		else
		{
			this.contentPanel.setWidgetSize(this.algebraViewPanel, computeAlgebraWidth());

			int euclidianWidth = Window.getClientWidth() - computeAlgebraWidth();

			this.euclidianViewPanel.setPixelSize(euclidianWidth, Window.getClientHeight() - panelHeight);

			// for Win8 position it on top, for others under appbar
			this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel, Window.getClientWidth() - TabletGUI.computeAlgebraWidth()
			    - ALGEBRA_BUTTON_WIDTH, 0);

			// this.algebraViewButtonPanel.setPopupPosition(euclidianWidth -
			// ALGEBRA_BUTTON_WIDTH,
			// TouchEntryPoint.getLookAndFeel().getAppBarHeight());
			this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel, Window.getClientWidth() - TabletGUI.computeAlgebraWidth()
			    - ALGEBRA_BUTTON_WIDTH, 0);

			this.algebraViewButton.setStyleName("arrowRight");

			// Set algebraviewbutton nontransparent, when algebra view is open
			this.algebraViewButtonPanel.removeStyleName("transparent");
		}
	}

	@Override
	public void setLabels()
	{
		if (this.algebraViewPanel != null)
		{
			this.algebraViewPanel.setLabels();
		}
		if (TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel() != null)
		{
			TouchEntryPoint.getLookAndFeel().getTabletHeaderPanel().setLabels();
		}
		this.toolBar.setLabels();
	}

	// @Override
	// protected void onUnload()
	// {
	// super.onUnload();
	// this.algebraViewButtonPanel.hide();
	// }
	//
	// @Override
	// protected void onLoad()
	// {
	// super.onLoad();
	// if (this.algebraViewButtonPanel != null)
	// {
	// this.algebraViewButtonPanel.show();
	// }
	// }

	public TouchModel getTouchModel()
	{
		return this.touchModel;
	}

	public String getConstructionTitle()
	{
		if (this.getHeaderWidget() instanceof TabletHeaderPanel)
		{
			return ((TabletHeaderPanel) this.getHeaderWidget()).getConstructionTitle();
		}
		return "";
	}

	public void editTitle()
	{
		if (this.getHeaderWidget() instanceof TabletHeaderPanel)
		{
			((TabletHeaderPanel) this.getHeaderWidget()).editTitle();
		}
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (this.algebraViewButtonPanel != null)
			this.algebraViewButtonPanel.setVisible(this.editing && visible);
	}

	@Override
	public void setAlgebraVisible(boolean visible)
	{
		updateViewSizes(visible);
		this.algebraViewPanel.setVisible(visible);
	}

	@Override
	public void allowEditing(boolean b)
	{
		if (this.editing == b)
		{
			return;
		}
		this.editing = b;

		this.toolBar.setVisible(b);
		this.algebraViewButtonPanel.setVisible(b);
		this.setAlgebraVisible(this.isAlgebraShowing());
		this.stylingBar.setVisible(b);
		resetMode();

		if (b)
		{
			this.touchModel.getGuiModel().setStylingBar(this.stylingBar);
		}
		else
		{
			this.touchModel.getGuiModel().setStylingBar(null);
		}
	}

	public boolean isEditable()
	{
		return this.editing;
	}

	@Override
	public void resetMode()
	{
		this.touchModel.setCommand(ToolBarCommand.Move_Mobile);
		this.touchModel.getGuiModel().updateStylingBar();
	}

	@Override
	public boolean isAlgebraShowing()
	{
		return this.algebraViewPanel.isVisible();
	}

	public void restoreEuclidian(DockLayoutPanel panel)
	{
		this.contentPanel = panel;
		this.setContentWidget(this.contentPanel);
		this.contentPanel.setPixelSize(Window.getClientWidth(), Window.getClientHeight() - getLaf().getPanelsHeight());
	}

	public DockLayoutPanel getContentPanel()
	{
		return this.contentPanel;
	}

	public TouchApp getApp()
	{
		return this.app;
	}
	// TODO: use with SelelctionManager
	// @Override
	// public void updateStylingBar(SelectionManager selectionManager) {
	// this.stylingBar.updateGeos(selectionManager);
	// }
}
