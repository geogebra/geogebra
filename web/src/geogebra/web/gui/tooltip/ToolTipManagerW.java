package geogebra.web.gui.tooltip;

import geogebra.common.main.App;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Singleton class that maintains a gwt panel for displaying tooltips. Design
 * adapted from Java's ToolTipManager.
 * 
 * @author G. Sturr
 */
public class ToolTipManagerW {

	// private AppW app;
	protected SimplePanel tipPanel;
	private HTML tipHTML = new HTML();

	protected int mouseX = 0;
	protected int mouseY = 0;

	protected int scrollLeft = 0;
	protected int scrollTop = 0;

	private Timer timer;

	/** Default timer settings based on Java ToolTipManager defaults */
	private int initialDelay = 1750;
	private int dismissDelay = 4000;
	private int reshowDelay = 500;

	private boolean enableDelay = true;
	protected boolean showImmediately = false;
	private String oldText = "";

	protected Element tipElement;

	/**
	 * Singleton instance of ToolTipManager
	 */
	final static ToolTipManagerW sharedInstance = new ToolTipManagerW();

	/**
	 * Constructor
	 */
	private ToolTipManagerW() {
		initTooltipManagerW();
	}

	/**
	 * All methods are accessed from this instance.
	 * 
	 * @return Singleton instance of this class
	 */
	public static ToolTipManagerW sharedInstance() {
		return sharedInstance;
	}

	public void initTooltipManagerW() {

		if (tipPanel != null) {
			return;
		}

		tipPanel = new SimplePanel();
		tipPanel.getElement().getStyle().setProperty("visibility", "hidden");
		tipPanel.add(tipHTML);
		RootPanel.get().add(tipPanel);
		tipPanel.setStyleName("ToolTip");

		registerMouseListeners();

	}

	// =====================================
	// Getters/Setters
	// =====================================

	public int getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	public int getDismissDelay() {
		return dismissDelay;
	}

	public void setDismissDelay(int dismissDelay) {
		this.dismissDelay = dismissDelay;
	}

	public void setEnableDelay(boolean enableDelay) {
		this.enableDelay = enableDelay;
	}

	// =====================================
	// Mouse Listeners
	// =====================================

	/**
	 * Register mouse listeners to keep track of the mouse position and hide the
	 * tooltip on a mouseDown event.
	 */
	private void registerMouseListeners() {

		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent e = event.getNativeEvent();

				if (event.getTypeInt() == Event.ONMOUSEDOWN) {
					hideToolTip(true);
				}

				mouseX = e.getClientX();
				mouseY = e.getClientY();

			}
		});

		// observe scroll event, so we can offset x and y for tooltip
		Window.addWindowScrollHandler(new ScrollHandler() {
			public void onWindowScroll(ScrollEvent event) {

				scrollLeft = event.getScrollLeft();
				scrollTop = event.getScrollTop();

				App.debug("scrollLeft: " + scrollLeft + " scrollTop: "
				        + scrollTop);

			}
		});
	}

	// ======================================
	// Show ToolTip
	// ======================================

	/**
	 * Shows toolTip relative to a given element
	 * 
	 * @param element
	 *            element associated with tooltip
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(Element element, String toolTipText) {

		tipElement = element;

		if (element == null || toolTipText == null) {
			hideToolTip(false);
			return;
		}

		if (oldText.equals(toolTipText)) {
			return;
		}

		oldText = toolTipText;
		tipHTML.setHTML(toolTipText);

		showToolTipWithDelay();

	}

	/**
	 * Shows toolTip at mouse position
	 * 
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(String toolTipText) {

		tipElement = null;

		if (toolTipText == null) {
			hideToolTip(false);
			return;
		}

		if (oldText.equals(toolTipText)) {
			return;
		}

		oldText = toolTipText;
		tipHTML.setHTML(toolTipText);

		showToolTipWithDelay();

	}

	protected void setToolTipLocation() {
		int left, top;
		// Widget rootPane = AppW.getRootComponent(app);
		// if(rootPane == null){
		// return;
		// }

		if (tipElement == null) {
			left = scrollLeft + mouseX;
			top = scrollTop + mouseY + 18;

		} else {
			left = tipElement.getAbsoluteLeft();
			top = tipElement.getAbsoluteBottom();
		}

		// handle tooltip overflow at left and bottom edge
		int w = tipPanel.getOffsetWidth();

		int windowLeft = RootPanel.get().getAbsoluteLeft()
		        + RootPanel.get().getOffsetWidth();

		// int windowLeft = rootPane.getAbsoluteLeft()
		// + rootPane.getOffsetWidth();

		if (left + w > windowLeft) {
			left = left - w;
		}
		int h = tipPanel.getOffsetHeight();

		int windowBottom = RootPanel.get().getAbsoluteTop()
		        + RootPanel.get().getOffsetHeight();

		// int windowBottom = rootPane.getAbsoluteTop()
		// + rootPane.getOffsetHeight();

		if (top + h > windowBottom) {
			top = windowBottom - h;
		}

		// left = (int) (left * ((EuclidianControllerW)
		// app.getActiveEuclidianView().getEuclidianController()).getScaleXMultiplier());
		// top = (int) (top * ((EuclidianControllerW)
		// app.getActiveEuclidianView().getEuclidianController()).getScaleYMultiplier());

		// set the tooltip location
		RootPanel.get().setWidgetPosition(tipPanel, left, top);
	}

	private void showToolTipWithDelay() {

		cancelTimer();

		if (enableDelay) {
			timer = new Timer() {
				@Override
				public void run() {
					show();
				}
			};

			if (showImmediately) {
				timer.schedule(0);
			} else {
				timer.schedule(initialDelay);
			}

		} else {
			show();
		}
	}

	protected void show() {

		cancelTimer();

		setToolTipLocation();
		tipPanel.getElement().getStyle().setProperty("visibility", "visible");
		showImmediately = true;

		if (enableDelay) {
			timer = new Timer() {
				@Override
				public void run() {
					hideToolTip(true);
				}
			};
			timer.schedule(dismissDelay);
		}
	}

	// ======================================
	// Cancel/Hide ToolTip
	// ======================================

	/**
	 * TODO temporary fix --- do we need the reshow param?
	 */
	public void hideToolTip() {
		hideToolTip(false);
	}

	public void hideToolTip(boolean doReshow) {
		if (tipPanel.getElement().getPropertyBoolean("visibility")) {
			// TODO: do nothing ?
		}
		oldText = "";
		tipPanel.getElement().getStyle().setProperty("visibility", "hidden");
		setReshowTimer();
	}

	private void setReshowTimer() {
		App.debug("start reshow timer 1");
		if (showImmediately) {
			cancelTimer();
			timer = new Timer() {
				@Override
				public void run() {
					showImmediately = false;
				}
			};
			App.debug("start reshow timer 2");
			timer.schedule(reshowDelay);
		}
	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
