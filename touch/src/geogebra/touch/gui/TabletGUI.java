package geogebra.touch.gui;

import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.laf.TabletLAF;

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

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends TouchGUI {
	
	private static final float ALGEBRA_VIEW_WIDTH_FRACTION = 0.2f;
	public static final int ALGEBRA_BUTTON_WIDTH = 50;
	private static final int MINIMAL_WIDTH_FOR_TWO_VIEWS = 400;
	
	
	
	public static int computeAlgebraWidth() {
		if (Window.getClientWidth() < MINIMAL_WIDTH_FOR_TWO_VIEWS) {
			return Window.getClientWidth();
		}
		return Math.max(250,
				(int) (Window.getClientWidth() * ALGEBRA_VIEW_WIDTH_FRACTION));
	}

	private DockLayoutPanel contentPanel;
	private FlowPanel algebraViewButtonPanel, algebraViewArrowPanel;
	private FastButton algebraButton;
	private TabletHeaderPanel hp;
	private TabletLAF laf;
	
	/**
	 * Sets the viewport and other settings, creates a link element at the end
	 * of the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI() {
		super();
	}

	public void editTitle() {
		if (this.hp != null) {
			this.hp.editTitle();
		}
	}

	public String getConstructionTitle() {
		return this.hp != null ? this.hp.getConstructionTitle() : "";
	}

	public DockLayoutPanel getContentPanel() {
		return this.contentPanel;
	}

	@Override
	public void initGUIElements() {
		this.laf = ((TabletLAF) TouchEntryPoint.getLookAndFeel());
		
		//header
		this.laf.buildTabletHeader(this.touchModel);
		this.hp = this.laf.getTabletHeaderPanel();
		this.add(this.hp);

		//euclidian and algebra
		this.contentPanel = new DockLayoutPanel(Unit.PX);
		this.contentPanel.setStyleName("appContentPanel");

		this.algebraViewPanel = new AlgebraViewPanel(this.touchController, this.kernel);

		final int contentWidth = Window.getClientWidth();
		final int contentHeight = this.laf.getCanvasHeight();
		int euclidianWidth = contentWidth - computeAlgebraWidth();
		
		this.contentPanel.setPixelSize(contentWidth, contentHeight);
		
		this.euclidianViewPanel.setStyleName("euclidianViewPanel");
		this.euclidianViewPanel.setPixelSize(euclidianWidth, contentHeight);
		this.euclidianViewPanel.initEuclidianView(this.touchController);

		this.styleBar = new StyleBar(this.touchModel, this.euclidianViewPanel.getEuclidianView());
		this.touchModel.getGuiModel().setStyleBar(this.styleBar);
		this.euclidianViewPanel.add(this.styleBar);
		this.euclidianViewPanel.setWidgetPosition(this.styleBar, this.rtl ? euclidianWidth - STYLEBAR_WIDTH :0, 0);
		if(this.rtl){
			this.contentPanel.addWest(this.algebraViewPanel, computeAlgebraWidth());
		}else{
			this.contentPanel.addEast(this.algebraViewPanel, computeAlgebraWidth());
		}
		this.contentPanel.add(this.euclidianViewPanel);

		
		this.algebraViewPanel.onResize();
		this.add(this.contentPanel);		

		// show/hide AlgebraView Button
		this.algebraViewButtonPanel = new FlowPanel();

		// Prevent events from getting through to the canvas
		this.algebraViewButtonPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.algebraViewButtonPanel.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(final MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, MouseDownEvent.getType());

		this.algebraViewButtonPanel.addDomHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(final TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, TouchStartEvent.getType());

		this.algebraViewArrowPanel = new FlowPanel();
		this.algebraViewArrowPanel.setStyleName("algebraViewArrowPanel");

		this.algebraButton = new StandardButton(this.laf.getIcons().triangle_left());
		this.algebraButton.setStyleName("arrowRight");

		if (this.laf.useClickHandlerForOpenClose()) {
			this.algebraButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					TabletGUI.this.toggleAlgebraView();
				}
			});
		} else {
			this.algebraButton.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick() {
					TabletGUI.this.toggleAlgebraView();
				}
			});
		}

		this.algebraViewArrowPanel.add(this.algebraButton);

		this.euclidianViewPanel.add(this.algebraViewButtonPanel);
		this.euclidianViewPanel.setWidgetPosition(this.algebraViewButtonPanel,
				this.rtl? 0 : euclidianWidth - TabletGUI.ALGEBRA_BUTTON_WIDTH, 0);

		this.algebraViewButtonPanel.setStyleName("algebraViewButtonPanel");
		this.algebraViewButtonPanel.add(this.algebraViewArrowPanel);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				TabletGUI.this.updateViewSizes();
			}
		});
		
		
		//footer

		// init toolBar before setting the size of algebraView and euclidianView
		this.toolBar = new ToolBar(this.touchModel, this.app);
		this.add(this.toolBar);
	}

	private void positionAlgebraViewButtonPanel() {
		if (!isAlgebraShowing()) {
			this.algebraButton.setStyleName("arrowLeft");
			this.contentPanel.setWidgetSize(this.algebraViewPanel, 0);
			this.algebraViewButtonPanel.addStyleName("transparent");
		} else {
			this.algebraButton.setStyleName("arrowRight");
			this.algebraViewButtonPanel.removeStyleName("transparent");
			this.contentPanel.setWidgetSize(this.algebraViewPanel,
					TabletGUI.computeAlgebraWidth());
		}
	}

	public void restoreEuclidian(final DockLayoutPanel panel) {
		this.contentPanel = panel;
		this.add(this.contentPanel);
	}

	public void toggleAlgebraView() {
		this.setAlgebraVisible(!this.algebraViewPanel.isVisible());
		this.app.setUnsaved();
	}

	@Override
	public FlowPanel getAlgebraViewButtonPanel() {
		return this.algebraViewButtonPanel;
	}

	@Override
	public void setAlgebraVisible(final boolean visible) {
		this.algebraViewPanel.setVisible(visible);
		this.updateViewSizes();
	}

	@Override
	public FlowPanel getStylebar() {
		return this.styleBar;
	}

	@Override
	public void setLabels() {
		if (this.algebraViewPanel != null) {
			this.algebraViewPanel.setLabels();
		}
		if (this.laf.getTabletHeaderPanel() != null) {
//			this.laf.getTabletHeaderPanel().setLabels();
		}
		this.toolBar.setLabels();
	}
	
	@Override
	public void updateViewSizes() {
		// not closeAllOptions, because that would deselect the textField if the on-screen-keyboard is shown
		this.touchModel.getGuiModel().closeOptions();
//		super.onResize();
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
	}
	
	@Override
	public boolean isAlgebraShowing() {
		return this.algebraViewPanel.isVisible();
	}
}
