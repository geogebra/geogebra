/*
 * Based on SplitLayoutPanel (Copyright 2009 Google Inc., Apache license)
 * */

package org.geogebra.web.full.gui.layout;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.util.GeoGebraElement;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that adds user-positioned splitters between each of its child
 * widgets.
 *
 * <p>
 * This panel is used in the same way as {@link DockLayoutPanel}, except that
 * its children's sizes are always specified in {@link Unit#PX} units, and each
 * pair of child widgets has a splitter between them that the user can drag.
 * </p>
 *
 * <p>
 * This widget will <em>only</em> work in standards mode, which requires that
 * the HTML page in which it is run have an explicit &lt;!DOCTYPE&gt;
 * declaration.
 * </p>
 *
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-SplitLayoutPanel { the panel itself }</li>
 * <li>.gwt-SplitLayoutPanel .gwt-SplitLayoutPanel-HDragger { horizontal dragger
 * }</li>
 * <li>.gwt-SplitLayoutPanel .gwt-SplitLayoutPanel-VDragger { vertical dragger }
 * </li>
 * </ul>
 *
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.SplitLayoutPanelExample}
 * </p>
 */
public class ZoomSplitLayoutPanel extends DockLayoutPanel {

	private static final int DEFAULT_SPLITTER_SIZE = 8;
	private static final int DOUBLE_CLICK_TIMEOUT = 500;

	/**
	 * The element that masks the screen so we can catch mouse events over
	 * iframes.
	 */
	private static Element glassElem = null;

	private final int splitterSize;
	private GeoGebraElement ai;

  class HSplitter extends Splitter {
    public HSplitter(Widget target, boolean reverse, ZoomSplitLayoutPanel splitPanel) {
      super(target, reverse, splitPanel);
      impl.setToHorizontal(splitterSize);
    }

    @Override
    protected int getAbsolutePosition() {
      return (int) (getAbsoluteLeft() * getZoom());
    }

    @Override
    protected double getCenterSize() {
      return getCenterWidth();
    }

    @Override
    protected int getEventPosition(Event event) {
    	int type = DOM.eventGetType(event);
    	return type == Event.ONTOUCHSTART
		        || type == Event.ONTOUCHMOVE
		        || type == Event.ONTOUCHEND
				? (int) (event.getTouches().get(0).getClientX() * getZoom())
		        : (int) (event.getClientX() * getZoom());
    }

    @Override
    protected int getTargetPosition() {
      return (int) (target.getAbsoluteLeft() * getZoom());
    }

    @Override
    protected int getTargetSize() {
      return target.getOffsetWidth();
    }
  }

  public abstract class Splitter extends Widget {
		protected final Widget target;

		private int offset;
		private boolean mouseDown;
		private ScheduledCommand layoutCommand;

		private final boolean reverse;
		private int minSize;
		private int snapClosedSize = -1;
		private double centerSize;
		private double syncedCenterSize;

		private boolean toggleDisplayAllowed = false;
		private double lastClick = 0;

		private ZoomSplitLayoutPanel splitPanel;

		protected SplitterImpl impl = Browser.isMobile() ? new SplitterImplTouch()
				: new SplitterImpl();

		/**
		 * @param target
		 *            primary component
		 * @param reverse
		 *            whether the primary component is right/bottom
		 * @param splitPanel
		 *            parent split pane
		 */
		public Splitter(Widget target, boolean reverse,
				ZoomSplitLayoutPanel splitPanel) {
			this.target = target;
			this.reverse = reverse;
			this.splitPanel = splitPanel;

			setElement(impl.createElement());
			sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE
					| Event.ONDBLCLICK | Event.ONTOUCHSTART | Event.ONTOUCHMOVE
					| Event.ONTOUCHEND);
		}

		public double getZoom() {
			return 1 / this.splitPanel.ai.getScaleX();
		}

		@Override
		public void onBrowserEvent(Event event) {
			if (!impl.shouldHandleEvent(event, mouseDown)) {
				return;
			}
			Element splitter = null;
			switch (DOM.eventGetType(event)) {
			default:
				// do nothing
				break;
			case Event.ONTOUCHSTART:
				splitter = impl.getSplitterElement();
				splitter.addClassName("gwt-SplitLayoutPanel-Dragger-ACTIVE");
				startDrag(event);
				break;
			case Event.ONMOUSEDOWN:
				startDrag(event);
				break;
	
			case Event.ONTOUCHEND:
					splitter = impl.getSplitterElement();
				splitter.removeClassName("gwt-SplitLayoutPanel-Dragger-ACTIVE");
				endDrag(event);
				break;
			case Event.ONMOUSEUP:
				endDrag(event);
					break;

			case Event.ONMOUSEMOVE:
			case Event.ONTOUCHMOVE:
				if (splitPanel.hasSplittersFrozen()) {
					event.preventDefault();
					break;
				}
				if (mouseDown) {
					int sizeLeft = getEventPosition(event) - getTargetPosition()
							- offset;
					int size = reverse
							? getTargetSize() - getSplitterSize() - sizeLeft
							: sizeLeft;

					((LayoutData) target.getLayoutData()).hidden = false;
					// needed for prediction of panel size
					setDividerLocationSilent((int) normalize(sizeLeft));
					setAssociatedWidgetSize(size);
					event.preventDefault();
					}
				break;
				}
		}

		private void endDrag(Event event) {
			if (splitPanel.hasSplittersFrozen()) {
				event.preventDefault();
				return;
			}
			mouseDown = false;

			getGlassElem().removeFromParent();

			// Handle double-clicks.
			// Fake them since the double-click event aren't fired.
			if (this.toggleDisplayAllowed) {
				double now = Duration.currentTimeMillis();
				if (now - this.lastClick < DOUBLE_CLICK_TIMEOUT) {
					now = 0;
					LayoutData layout = (LayoutData) target.getLayoutData();
					if (layout.size == 0) {
						// Restore the old size.
						setAssociatedWidgetSize(layout.oldSize);
					} else {
						/*
						 * Collapse to size 0. We change the size instead of
						 * hiding the widget because hiding the widget can cause
						 * issues if the widget contains a flash component.
						 */
						layout.oldSize = layout.size;
						setAssociatedWidgetSize(0);
					}
				}
				this.lastClick = now;
			}

			Event.releaseCapture(getElement());
			event.preventDefault();

		}

		private void startDrag(Event event) {
			if (splitPanel.hasSplittersFrozen()) {
				event.preventDefault();
				return;
			}
			mouseDown = true;

			/*
			 * Resize glassElem to take up the entire scrollable window area,
			 * which is the greater of the scroll size and the client size.
			 */
			int width = Math.max(Window.getClientWidth(),
					Document.get().getScrollWidth());
			int height = Math.max(Window.getClientHeight(),
					Document.get().getScrollHeight());
			Element glass = getGlassElem();
			glass.getStyle().setHeight(height, Unit.PX);
			glass.getStyle().setWidth(width, Unit.PX);
			Document.get().getBody().appendChild(glass);

			offset = getEventPosition(event) - getAbsolutePosition();
			Event.setCapture(getElement());
			event.preventDefault();

		}

		/**
		 * @param minSize
		 *            minimal size
		 */
		public void setMinSize(int minSize) {
			this.minSize = minSize;
			LayoutData layout = (LayoutData) target.getLayoutData();

			// Try resetting the associated widget's size, which will enforce
			// the new
			// minSize value.
			setAssociatedWidgetSize((int) layout.size);
		}

		public void setSnapClosedSize(int snapClosedSize) {
			this.snapClosedSize = snapClosedSize;
		}

		public void setToggleDisplayAllowed(boolean allowed) {
			this.toggleDisplayAllowed = allowed;
		}

		protected abstract int getAbsolutePosition();

		protected abstract double getCenterSize();

		protected abstract int getEventPosition(Event event);

		protected abstract int getTargetPosition();

		protected abstract int getTargetSize();

		private double getMaxSize() {
			// To avoid seeing stale center size values due to deferred layout
			// updates, maintain our own copy up to date and resync when the
			// DockLayoutPanel value changes.
			double newCenterSize = getCenterSize();
			if (syncedCenterSize != newCenterSize) {
				syncedCenterSize = newCenterSize;
				centerSize = newCenterSize;
			}

			return Math.max(
					((LayoutData) target.getLayoutData()).size + centerSize, 0);
		}

		private void setAssociatedWidgetSize(double size0) {
			double size = normalize(size0);

			LayoutData layout = (LayoutData) target.getLayoutData();
			if (size == layout.size) {
				return;
			}

			// Adjust our view until the deferred layout gets scheduled.
			centerSize += layout.size - size;
			layout.size = size;
			// Defer actually updating the layout, so that if we receive many
			// mouse events before layout/paint occurs, we'll only update once.
			if (layoutCommand == null) {
				layoutCommand = new ScheduledCommand() {
					@Override
					public void execute() {
						layoutCommand = null;

						forceLayout();
					}
				};
				Scheduler.get().scheduleDeferred(layoutCommand);
			}
		}

		private double normalize(double size0) {
			double maxSize = getMaxSize();
			double size = size0;
			if (size > maxSize) {
				size = maxSize;
			}

			if (snapClosedSize > 0 && size < snapClosedSize) {
				size = 0;
			} else if (size < minSize) {
				size = minSize;
			}
			return size;
		}
  }

  class VSplitter extends Splitter {
    public VSplitter(Widget target, boolean reverse, ZoomSplitLayoutPanel splitPanel) {
      super(target, reverse, splitPanel);
      impl.setToVertical(splitterSize);
    }

    @Override
    protected int getAbsolutePosition() {
      return (int) (getAbsoluteTop() * getZoom());
    }

    @Override
    protected double getCenterSize() {
      return getCenterHeight();
    }

    @Override
    protected int getEventPosition(Event event) {
		int type = DOM.eventGetType(event);
    	return type == Event.ONTOUCHSTART
		        || type == Event.ONTOUCHMOVE
		        || type == Event.ONTOUCHEND
				? (int) (event.getTouches().get(0).getClientY() * getZoom())
		        : (int) (event.getClientY() * getZoom());
    }

    @Override
    protected int getTargetPosition() {
      return (int) (target.getAbsoluteTop() * getZoom());
    }

    @Override
    protected int getTargetSize() {
      return target.getOffsetHeight();
    }
  }

	/**
	 * @return glass pane
	 */
	static Element getGlassElem() {
		if (glassElem == null) {
			glassElem = Document.get().createDivElement();
			glassElem.getStyle().setPosition(Position.ABSOLUTE);
			glassElem.getStyle().setTop(0, Unit.PX);
			glassElem.getStyle().setLeft(0, Unit.PX);
			glassElem.getStyle().setMargin(0, Unit.PX);
			glassElem.getStyle().setPadding(0, Unit.PX);
			glassElem.getStyle().setBorderWidth(0, Unit.PX);

			// We need to set the background color or mouse events will go right
			// through the glassElem. If the SplitPanel contains an iframe, the
			// iframe will capture the event and the slider will stop moving.
			glassElem.getStyle().setProperty("background", "white");
			glassElem.getStyle().setOpacity(0.0);
		}
		return glassElem;
	}

	/**
	 * @return whether splitter id frozen; override in subclasses
	 */
	public boolean hasSplittersFrozen() {
		return false;
	}

	/**
	 * Construct a new {@link SplitLayoutPanel} with the default splitter size
	 * of 8px.
	 */
	public ZoomSplitLayoutPanel(GeoGebraElement ai) {
		this(DEFAULT_SPLITTER_SIZE, ai);
	}

  /**
   * Construct a new {@link SplitLayoutPanel} with the specified splitter size
   * in pixels.
   *
   * @param splitterSize the size of the splitter in pixels
   */
	public ZoomSplitLayoutPanel(int splitterSize, GeoGebraElement ai) {
		super(Unit.PX);
		this.splitterSize = splitterSize;
		setStyleName("gwt-SplitLayoutPanel");
		this.ai = ai;
	}

  /**
   * Return the size of the splitter in pixels.
   *
   * @return the splitter size
   */
  public int getSplitterSize() {
    return splitterSize;
  }

  @Override
  public void insert(Widget child, Direction direction, double size, Widget before) {
    super.insert(child, direction, size, before);
    if (direction != Direction.CENTER) {
      insertSplitter(child, before);
    }
  }

  @Override
  public boolean remove(Widget child) {
    assert !(child instanceof Splitter) : "Splitters may not be directly removed";

    int idx = getWidgetIndex(child);
    if (super.remove(child)) {
      // Remove the associated splitter, if any.
      // Now that the widget is removed, idx is the index of the splitter.
      if (idx < getWidgetCount()) {
        // Call super.remove(), or we'll end up recursing.
        super.remove(getWidget(idx));
      }
      return true;
    }
    return false;
  }

  @Override
  public void setWidgetHidden(Widget widget, boolean hidden) {
    super.setWidgetHidden(widget, hidden);
    Splitter splitter = getAssociatedSplitter(widget);
    if (splitter != null) {
      // The splitter is null for the center element.
      super.setWidgetHidden(splitter, hidden);
    }
  }

  	/**
	 * Sets the minimum allowable size for the given widget.
	 *
	 * <p>
	 * Its associated splitter cannot be dragged to a position that would make
	 * it smaller than this size. This method has no effect for the
	 * {@link com.google.gwt.user.client.ui.DockLayoutPanel.Direction#CENTER}
	 * widget.
	 * </p>
	 *
	 * @param child
	 *            the child whose minimum size will be set
	 * @param minSize
	 *            the minimum size for this widget
	 */
  public void setWidgetMinSize(Widget child, int minSize) {
    assertWidgetIsChild(child);
    Splitter splitter = getAssociatedSplitter(child);
    // The splitter is null for the center element.
    if (splitter != null) {
      splitter.setMinSize(minSize);
    }
  }

  	/**
	 * Sets a size below which the slider will close completely. This can be
	 * used in conjunction with {@link #setWidgetMinSize} to provide a
	 * speed-bump effect where the slider will stick to a preferred minimum size
	 * before closing completely.
	 *
	 * <p>
	 * This method has no effect for the
	 * {@link com.google.gwt.user.client.ui.DockLayoutPanel.Direction#CENTER}
	 * widget.
	 * </p>
	 *
	 * @param child
	 *            the child whose slider should snap closed
	 * @param snapClosedSize
	 *            the width below which the widget will close or -1 to disable.
	 */
  public void setWidgetSnapClosedSize(Widget child, int snapClosedSize) {
    assertWidgetIsChild(child);
    Splitter splitter = getAssociatedSplitter(child);
    // The splitter is null for the center element.
    if (splitter != null) {
      splitter.setSnapClosedSize(snapClosedSize);
    }
  }

  /**
   * Sets whether or not double-clicking on the splitter should toggle the
   * display of the widget.
   *
   * @param child the child whose display toggling will be allowed or not.
   * @param allowed whether or not display toggling is allowed for this widget
   */
  public void setWidgetToggleDisplayAllowed(Widget child, boolean allowed) {
    assertWidgetIsChild(child);
    Splitter splitter = getAssociatedSplitter(child);
    // The splitter is null for the center element.
    if (splitter != null) {
      splitter.setToggleDisplayAllowed(allowed);
    }
  }

  private Splitter getAssociatedSplitter(Widget child) {
    // If a widget has a next sibling, it must be a splitter, because the only
    // widget that *isn't* followed by a splitter must be the CENTER, which has
    // no associated splitter.
    int idx = getWidgetIndex(child);
    if (idx > -1 && idx < getWidgetCount() - 1) {
      Widget splitter = getWidget(idx + 1);
      assert splitter instanceof Splitter : "Expected child widget to be splitter";
      return (Splitter) splitter;
    }
    return null;
  }

	private void insertSplitter(Widget widget, Widget before) {
		assert getChildren()
				.size() > 0 : "Can't add a splitter before any children";

		LayoutData layout = (LayoutData) widget.getLayoutData();
		Splitter splitter = null;
		String cssdir = "x";
		switch (getResolvedDirection(layout.direction)) {
		case WEST:
			cssdir = "y";
			splitter = new HSplitter(widget, false, this);
			break;
		case EAST:
			cssdir = "y";
			splitter = new HSplitter(widget, true, this);
			break;
		case NORTH:
			splitter = new VSplitter(widget, false, this);
			break;
		case SOUTH:
			splitter = new VSplitter(widget, true, this);
			break;
		default:
			assert false : "Unexpected direction";
		}

		super.insert(splitter, layout.direction, splitterSize, before);

		LayoutData layoutData = (LayoutData) splitter.getLayoutData();
		splitter.impl.splitterInsertedIntoLayer(layoutData.layer);
		Element parentDiv = splitter.getElement().getParentElement();
		parentDiv.addClassName("draggerParent");
		parentDiv.setAttribute("style", parentDiv.getAttribute("style")
				+ ";overflow-" + cssdir + ":hidden !important");
	}

	void assertWidgetIsChild(Widget widget) {
		assert (widget == null) || (widget
				.getParent() == this) : "The specified widget is not a child of this panel";
	}

	/**
	 * Save divider location.
	 * 
	 * @param size
	 *            divider location
	 */
	protected void setDividerLocationSilent(int size) {
		// implement in subclass
	}
}
