package org.geogebra.web.full.gui.layout;

import java.util.ArrayList;

import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Split pane which is used to separate two DockPanels.
 * 
 * @author Florian Sonner, adapted by G.Sturr for web
 */
public class DockSplitPaneW extends ZoomSplitLayoutPanel
		implements DockComponent {

	private Widget leftComponent;
	private Widget rightComponent;
	private int orientation;

	private int savedDividerLocation;
	private int savedSize;

	private double resizeWeight;
	private int dividerLocation;

	private AppW app;

	/**
	 * For calling the onResize method in a deferred way
	 */
	Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			onResize();
		}
	};
	private boolean forcedLayout = false;
	private int preferredWidth;
	private int preferredHeight;

	/**
	 * For calling the onResize method in a deferred way
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	/*********************************************
	 * Constructs a DockSplitPaneW with default horizontal orientation
	 * 
	 * @param app
	 *            application
	 */
	public DockSplitPaneW(AppW app) {
		this(SwingConstants.HORIZONTAL_SPLIT, app);
	}

	/*********************************************
	 * Constructs a DockSplitPaneW with given orientation
	 * 
	 * @param newOrientation
	 *            orientation
	 * @param app
	 *            application
	 */
	public DockSplitPaneW(int newOrientation, AppW app) {
		super(app.getGeoGebraElement());
		this.app = app;
		setOrientation(newOrientation);
		setResizeWeight(0.5);
		// this.addPropertyChangeListener(paneResizeListener);

		dividerLocation = 100;
		if (hasSplittersFrozen()) {
			addStyleName("splitterFixed");
		}
	}

	// ========================================
	// Getters/Setters
	// ========================================

	public int getDividerLocation() {
		return dividerLocation;
	}

	public void setResizeWeight(double d) {
		resizeWeight = d;
	}

	@Override
	public boolean hasSplittersFrozen() {
		return app.isApplet() && !app.isRightClickEnabled()
				&& !app.showMenuBar();
	}

	/**
	 * Recursively compute sizes using the embeded size of panels.
	 * 
	 * @return divider location (relative)
	 */
	public double computeDividerLocationRecursive() {

		double sizeLeft = 0;

		if (getLeftComponent() instanceof DockSplitPaneW) {
			sizeLeft = ((DockSplitPaneW) getLeftComponent())
					.computeSizeRecursive(orientation);
		} else if (getLeftComponent() instanceof DockPanelW) {
			sizeLeft = ((DockPanelW) getLeftComponent()).getEmbeddedSize();
		}

		double sizeAll = computeSizeRecursive(orientation);

		if (sizeAll <= 0) {
			return 0;
		}

		if (sizeLeft < 0) {
			// well, why not return with 0?
			return 0;
		} else if (sizeLeft > sizeAll) {
			// well, why not return with 1?
			return 1;
		}

		return sizeLeft / sizeAll;
	}

	/**
	 * @param parentOrientation
	 *            orientation of parent pane
	 * @return size in pixels
	 */
	public double computeSizeRecursive(int parentOrientation) {

		double size = 0;

		if (orientation == parentOrientation) {
			if (getLeftComponent() instanceof DockSplitPaneW) {
				size += ((DockSplitPaneW) getLeftComponent())
						.computeSizeRecursive(orientation);
			} else if (getLeftComponent() instanceof DockPanelW) {
				size += ((DockPanelW) getLeftComponent()).getEmbeddedSize();
			}

			if (getRightComponent() instanceof DockSplitPaneW) {
				size += ((DockSplitPaneW) getRightComponent())
						.computeSizeRecursive(orientation);
			} else if (getRightComponent() instanceof DockPanelW) {
				size += ((DockPanelW) getRightComponent()).getEmbeddedSize();
			}

			return size;
		}

		double size2 = 0;

		if (getLeftComponent() instanceof DockSplitPaneW) {
			size = ((DockSplitPaneW) getLeftComponent())
					.computeSizeRecursive(parentOrientation);
		} else if (getLeftComponent() instanceof DockPanelW) {
			// if orientation is different, use settings instead of embeddedSize
			if (parentOrientation == SwingConstants.VERTICAL_SPLIT) {
				size = ((DockPanelW) getLeftComponent()).getEstimatedSize()
						.getHeight();
			} else {
				size = ((DockPanelW) getLeftComponent()).getEstimatedSize()
						.getWidth();
			}
		}

		if (getRightComponent() instanceof DockSplitPaneW) {
			size2 = ((DockSplitPaneW) getRightComponent())
					.computeSizeRecursive(parentOrientation);
		} else if (getRightComponent() instanceof DockPanelW) {
			// if orientation is different, use settings instead of embeddedSize
			if (parentOrientation == SwingConstants.VERTICAL_SPLIT) {
				size2 = ((DockPanelW) getRightComponent()).getEstimatedSize()
						.getHeight();
			} else {
				size2 = ((DockPanelW) getRightComponent()).getEstimatedSize()
						.getWidth();
			}
		}

		return Math.max(size, size2);
	}

	/**
	 * Set divider position and update UI.
	 * 
	 * @param location
	 *            divider location in pixels
	 */
	public void setDividerLocation(int location) {
		setDividerLocationSilent(location);
		setComponents();
	}

	/**
	 * Set divider position, no UI update.
	 * 
	 * @param location
	 *            divider location in pixels
	 */
	@Override
	public void setDividerLocationSilent(int location) {
		dividerLocation = location;
	}

	/**
	 * @param proportion
	 *            relative divider location
	 */
	public void setDividerLocation(double proportion) {
		if (getOrientation() == SwingConstants.VERTICAL_SPLIT) {
			setDividerLocation((int) (proportion * getOffsetHeight()));
		} else {
			setDividerLocation((int) (proportion * getOffsetWidth()));
		}
	}

	/**
	 * @return right (or bottom) component
	 */
	public Widget getRightComponent() {
		return rightComponent;
	}

	/**
	 * @return left (or top) component
	 */
	public Widget getLeftComponent() {
		return leftComponent;
	}

	/**
	 * @return orientation: SwingConstants.HORIZONTAL_SPLIT or
	 *         SwingConstants.VERTICAL_SPLIT
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @param newOrientation
	 *            SwingConstants.HORIZONTAL_SPLIT or
	 *            SwingConstants.VERTICAL_SPLIT
	 */
	public void setOrientation(int newOrientation) {
		orientation = newOrientation;
	}

	public double getResizeWeight() {
		return resizeWeight;
	}

	/**
	 * Return the component which is opposite to the parameter.
	 * 
	 * @param component
	 *            component
	 * @return opposite part of the split pane
	 */
	public Widget getOpposite(Widget component) {
		if (component == leftComponent) {
			return rightComponent;
		} else if (component == rightComponent) {
			return leftComponent;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * set the left component and check if it's empty when loading file
	 * 
	 * @param component
	 *            componenent
	 */
	public void setLeftComponentCheckEmpty(Widget component) {

		// ensure visibility flags of dock panels set to false
		if (leftComponent != null) {
			((DockComponent) leftComponent).setDockPanelsVisible(false);
		}

		setLeftComponent(component);
	}

	/**
	 * set the right component and check if it's empty when loading file
	 * 
	 * @param component
	 *            componenent
	 */
	public void setRightComponentCheckEmpty(Widget component) {

		// ensure visibility flags of dock panels set to false
		if (rightComponent != null) {
			((DockComponent) rightComponent).setDockPanelsVisible(false);
		}

		setRightComponent(component);
	}

	/**
	 * Set the left component of this DockSplitPane and remove the divider if
	 * the left component is null.
	 * 
	 * @param component
	 *            left component
	 */
	public void setLeftComponent(Widget component) {
		leftComponent = component;
		setComponents();
	}

	/**
	 * Set the right component of this DockSplitPane and remove the divider if
	 * the right component is null.
	 * 
	 * @param component
	 *            right component
	 */
	public void setRightComponent(Widget component) {
		rightComponent = component;
		setComponents();
	}

	private void setComponentsSilent() {

		// if both components exist give the resizing pane to rightComponent
		// (in Swing this corresponds to resize weight = 1)
		if (leftComponent != null && rightComponent != null) {
			if (orientation == SwingConstants.HORIZONTAL_SPLIT) {
				addWest(leftComponent, dividerLocation);
				add(rightComponent);
			} else {
				addNorth(leftComponent, dividerLocation);
				add(rightComponent);
			}
		}

		// otherwise put the single component into the resizing pane
		else if (leftComponent != null) {
			if (orientation == SwingConstants.HORIZONTAL_SPLIT) {
				add(leftComponent);
			} else {
				add(leftComponent);
			}
		}

		else if (rightComponent != null) {
			add(rightComponent);
		}

		// forceLayout();
	}

	private void setComponents() {
		clear();
		setComponentsSilent();
	}

	/**
	 * Recursively add components to DOM without calling updatePanel on
	 * individual panels
	 */
	public void setComponentsSilentRecursive() {
		setComponentsSilent();
		updateUI();
		forceLayout();
		if (getLeftComponent() instanceof DockSplitPaneW) {
			((DockSplitPaneW) getLeftComponent())
					.setComponentsSilentRecursive();
		} /*
			 * else if (getLeftComponent() instanceof DockPanelW) {
			 * ((DockPanelW)getLeftComponent()).updatePanel(); }
			 */
		if (getRightComponent() instanceof DockSplitPaneW) {
			((DockSplitPaneW) getRightComponent())
					.setComponentsSilentRecursive();
		} /*
			 * else if (getRightComponent() instanceof DockPanelW) {
			 * ((DockPanelW)getRightComponent()).updatePanel(); }
			 */
	}

	/**
	 * Replace a component from the split pane with another.
	 * 
	 * @param component
	 *            component
	 * @param replacement
	 *            replacement component
	 */
	public void replaceComponent(Widget component, Widget replacement) {
		if (component == leftComponent) {
			setLeftComponent(replacement);
		} else if (component == rightComponent) {
			setRightComponent(replacement);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Update the UI by drawing the divider just if the dividerVisible attribute
	 * is set to true.
	 */
	public void updateUI() {

		// super.updateUI();
		// SplitPaneUI splitPaneUI = getUI();
		// if (splitPaneUI instanceof BasicSplitPaneUI) {
		// BasicSplitPaneUI basicUI = (BasicSplitPaneUI) splitPaneUI;
		// basicUI.getDivider().setVisible(dividerVisible);
		// }
	}

	@Override
	public void saveDividerLocation() {

		if (getOrientation() == SwingConstants.VERTICAL_SPLIT) {
			if (getLeftComponent() != null) {
				savedDividerLocation = getLeftComponent().getOffsetHeight();
			}
			savedSize = getOffsetHeight();
		} else {
			if (getLeftComponent() != null) {
				savedDividerLocation = getLeftComponent().getOffsetWidth();
			}
			savedSize = getOffsetWidth();
		}

		if (getLeftComponent() != null) {
			((DockComponent) getLeftComponent()).saveDividerLocation();
		}
		if (getRightComponent() != null) {
			((DockComponent) getRightComponent()).saveDividerLocation();
		}

	}

	@Override
	public void updateDividerLocation(int size, int orientation1) {

		/*
		 * AbstractApplication.debug("\nresizeW= "+getResizeWeight()
		 * +"\nsize= "+size +"\nsavedSize= "+savedSize
		 * +"\nsavedDividerLocation= "+savedDividerLocation
		 * +"\nleft= "+getLeftComponent() +"\nright= "+getRightComponent());
		 */

		if (orientation1 == getOrientation()) {
			if (getResizeWeight() == 0) {
				setDividerLocationRecursive(
						checkLocation(savedDividerLocation, size), size,
						orientation1);
			} else if (DoubleUtil.isEqual(getResizeWeight(), 0.5)) {
				if (savedSize == 0) {
					savedSize = 1;
				}
				setDividerLocationRecursive(
						(size * savedDividerLocation) / savedSize, size,
						orientation1);
			} else {
				setDividerLocationRecursive(size
						- checkLocation(savedSize - savedDividerLocation, size),
						size, orientation1);
			}
		} else {
			propagateDividerLocation(size, size, orientation1);
		}

	}

	private static int checkLocation(int location, int size) {

		int min = MIN_SIZE;
		if (min > size / 2) {
			min = size / 2;
		}

		if (location < min) {
			return min;
		}

		if (location > size - min) {
			return size - min;
		}

		return location;
	}

	private void setDividerLocationRecursive(int location, int size,
			int orientation1) {
		setDividerLocation(location);
		// AbstractApplication.debug("location = "+location);
		propagateDividerLocation(location, size - location, orientation1);
	}

	private void propagateDividerLocation(int sizeLeft, int sizeRight,
			int orientation1) {
		if (getLeftComponent() != null) {
			((DockComponent) getLeftComponent()).updateDividerLocation(sizeLeft,
					orientation1);
		}
		if (getRightComponent() != null) {
			((DockComponent) getRightComponent())
					.updateDividerLocation(sizeRight, orientation1);
		}
	}

	@Override
	public String toString(String prefix) {
		String prefix2 = prefix + "-";
		return "\n" + prefix + "split=" + getDividerLocation() + "\n" + prefix
				+ "width=" + getOffsetWidth() + "\n" + prefix + "left"
				+ ((DockComponent) getLeftComponent()).toString(prefix2) + "\n"
				+ prefix + "right"
				+ ((DockComponent) getRightComponent()).toString(prefix2);
	}

	@Override
	public boolean updateResizeWeight() {
		boolean takesNewSpaceLeft = false;
		boolean takesNewSpaceRight = false;

		if ((getLeftComponent() != null)
				&& ((DockComponent) getLeftComponent()).updateResizeWeight()) {
			takesNewSpaceLeft = true;
		}
		if ((getRightComponent() != null)
				&& ((DockComponent) getRightComponent()).updateResizeWeight()) {
			takesNewSpaceRight = true;
		}

		if (takesNewSpaceLeft) {
			if (takesNewSpaceRight) {
				setResizeWeight(0.5);
			} else {
				setResizeWeight(1);
			}
			return true;
		} else if (takesNewSpaceRight) {
			setResizeWeight(0);
			return true;
		}

		setResizeWeight(0);
		return false;
	}

	@Override
	public void setDockPanelsVisible(boolean visible) {
		if (leftComponent != null) {
			((DockComponent) leftComponent).setDockPanelsVisible(visible);
		}
		if (rightComponent != null) {
			((DockComponent) rightComponent).setDockPanelsVisible(visible);
		}
	}

	/*************************************************************************
	 * A helper class used to get the split pane information array of the
	 * current layout. Use {@link #getInfo(DockSplitPaneW)} with the root pane
	 * as parameter to get the array.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-26
	 */
	public static class TreeReader {
		private AppW app;
		private ArrayList<DockSplitPaneData> splitPaneInfo;
		private int windowWidth;
		private int windowHeight;

		/**
		 * @param app
		 *            application
		 */
		public TreeReader(AppW app) {
			this.app = app;
			splitPaneInfo = new ArrayList<>();
		}

		/**
		 * @param rootPane
		 *            split pane
		 * @return description of the pane
		 */
		public DockSplitPaneData[] getInfo(DockSplitPaneW rootPane) {
			splitPaneInfo.clear();

			// get window dimensions
			// TODO: Are these the correct dimensions needed for calculations
			// below?
			// e.g. do we include menubar height?
			windowWidth = (int) app.getWidth();
			windowHeight = (int) app.getHeight();

			saveSplitPane("", rootPane);

			DockSplitPaneData[] info = new DockSplitPaneData[splitPaneInfo
					.size()];
			return splitPaneInfo.toArray(info);
		}

		/**
		 * Save a split pane into the splitPaneInfo array list
		 * 
		 */
		private void saveSplitPane(String parentLocation0,
				DockSplitPaneW parent) {
			double dividerLocation = 0.2;

			// get relative divider location depending on the current
			// orientation
			if (parent.getOrientation() == SwingConstants.HORIZONTAL_SPLIT) {
				dividerLocation = (double) parent.getDividerLocation()
						/ windowWidth;
			} else {
				dividerLocation = (double) parent.getDividerLocation()
						/ windowHeight;
			}

			splitPaneInfo.add(new DockSplitPaneData(parentLocation0,
					dividerLocation, parent.getOrientation()));
			String parentLocation = parentLocation0;
			if (parentLocation.length() > 0) {
				parentLocation += ",";
			}

			if (parent.getLeftComponent() instanceof DockSplitPaneW) {
				saveSplitPane(parentLocation + "0",
						(DockSplitPaneW) parent.getLeftComponent());
			}

			if (parent.getRightComponent() instanceof DockSplitPaneW) {
				saveSplitPane(parentLocation + "1",
						(DockSplitPaneW) parent.getRightComponent());
			}
		}
	}

	@Override
	public void onResize() {
		if (this.getWidgetCount() > 0) {
			// If the split pane gets really narrow and right view is hidden,
			if (orientation == SwingConstants.HORIZONTAL_SPLIT
					&& getOffsetWidth() > 0
					&& getOffsetWidth() < this.getWidget(0).getOffsetWidth()) {
				this.setWidgetSize(this.getWidget(0), this.getOffsetWidth());
			} else if (orientation == SwingConstants.VERTICAL_SPLIT
					&& getOffsetHeight() > 0 && getOffsetHeight() < this
							.getWidget(0).getOffsetHeight()) {
				this.setWidgetSize(this.getWidget(0), this.getOffsetHeight());
			}
		}
		// it's only important to resize components so that
		// the divider should be inside
		if (getLeftComponent() instanceof RequiresResize) {
			((RequiresResize) getLeftComponent()).onResize();
		}

		if (getRightComponent() instanceof DockSplitPaneW) {
			((RequiresResize) getRightComponent()).onResize();
			((DockSplitPaneW) getRightComponent()).checkDividerIsOutside();
		} else if (getRightComponent() instanceof RequiresResize) {
			((RequiresResize) getRightComponent()).onResize();
		}

		if (orientation == SwingConstants.HORIZONTAL_SPLIT) {
			if (getLeftComponent() != null
					&& getLeftComponent().getOffsetWidth() > 0) {
				setDividerLocationSilent(getLeftComponent().getOffsetWidth());
			}
		} else {
			if (getLeftComponent() != null
					&& getLeftComponent().getOffsetHeight() > 0) {
				setDividerLocationSilent(getLeftComponent().getOffsetHeight());
			}
		}
	}

	/**
	 * Check if divider is outside the component and move it to the middle if
	 * needed
	 */
	public void checkDividerIsOutside() {

		// w, h should contain the dimensions visible on screen
		int w = this.getElement().getClientWidth();
		int h = this.getElement().getClientHeight();

		if (orientation == SwingConstants.HORIZONTAL_SPLIT) {
			if (getDividerLocation() >= w && (w > 0)) {
				setDividerLocation(0.5);
			}
		} else {
			if (getDividerLocation() >= h && (h > 0)) {
				setDividerLocation(0.5);
			}
		}
	}

	@Override
	public void forceLayout() {
		setForcedLayout(true);
		super.forceLayout();
		setForcedLayout(false);
	}

	public boolean isForcedLayout() {
		return forcedLayout;
	}

	public void setForcedLayout(boolean forcedLayout) {
		this.forcedLayout = forcedLayout;
	}

	/**
	 * @param widget
	 *            widget
	 * @return whether widget is the primary widget of this split pane
	 */
	public boolean isCenter(IsWidget widget) {
		LayoutData data = (LayoutData) widget.asWidget().getLayoutData();
		return data.direction == Direction.CENTER;
	}

	/**
	 * @param dockPanelW
	 *            dock panel
	 * @return preferred height of the panel (based on preferred size of this
	 *         and split location)
	 */
	public int getPreferredHeight(DockPanelW dockPanelW) {
		if (this.orientation == SwingConstants.HORIZONTAL_SPLIT
				|| getLeftComponent() == null || getRightComponent() == null) {
			return preferredHeight;
		}
		return dockPanelW == getLeftComponent() ? this.dividerLocation
				: preferredHeight - this.dividerLocation - getSplitterSize();
	}

	/**
	 * @param dockPanelW
	 *            dock panel
	 * @return preferred width of the panel (based on preferred size of this and
	 *         split location)
	 */
	public int getPreferredWidth(DockPanelW dockPanelW) {
		if (this.orientation == SwingConstants.VERTICAL_SPLIT
				|| getLeftComponent() == null || getRightComponent() == null) {
			return preferredWidth;
		}
		return dockPanelW == getLeftComponent() ? this.dividerLocation
				: preferredWidth - this.dividerLocation - getSplitterSize();
	}

	/**
	 * @param width
	 *            preferred width of the pane
	 * @param height
	 *            preferred height of the pane
	 */
	public void setPreferredWidth(int width, int height) {
		this.preferredHeight = height;
		this.preferredWidth = width;
	}

	/**
	 * @return real width when in DOM, estimate otherwisw
	 */
	public int getEstimateWidth() {
		return getOffsetWidth() > 0 ? getOffsetWidth() : preferredWidth;
	}

	/**
	 * @return real height when in DOM, estimate otherwisw
	 */
	public int getEstimateHeight() {
		return getOffsetHeight() > 0 ? getOffsetHeight() : preferredHeight;
	}

	/**
	 * @param needle
	 *            subpanel
	 * @return whether subpanel is rightmost bottom panel
	 */
	public boolean isBottomRight(DockComponent needle) {
		Widget rightChild = rightComponent == null ? leftComponent : rightComponent;
		if (rightChild == needle) {
			return true;
		}
		if (rightChild instanceof DockSplitPaneW) {
			return ((DockSplitPaneW) rightChild).isBottomRight(needle);
		}
		return false;
	}

	@Override
	public void setWidgetSize(Widget widget, double size) {
		LayoutData data = (LayoutData) widget.getLayoutData();
		if (data.direction == Direction.CENTER) {
			Widget opposite = getOpposite(widget);
			if (opposite == null) {
				Log.error("no opposite");
				return;
			}
			double fullSize = getMaxWidgetSize();

			super.setWidgetSize(opposite, fullSize - size);
		} else {
			super.setWidgetSize(widget, size);
		}
	}

	/**
	 * @return total height if for vertical split, width otherwise
	 */
	public int getMaxWidgetSize() {
		return this.orientation == SwingConstants.VERTICAL_SPLIT
				? getOffsetHeight() : getOffsetWidth();
	}
}
