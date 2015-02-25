package geogebra.web.gui.layout;

import geogebra.common.gui.layout.DockComponent;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.html5.main.AppW;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Split pane which is used to separate two DockPanels.
 * 
 * @author Florian Sonner, adapted by G.Sturr for web
 */
public class DockSplitPaneW extends ZoomSplitLayoutPanel implements DockComponent {

	private static final long serialVersionUID = 1L;

	// these constants match JSplitPane.VERTICAL_SPLIT and
	// JSplitPane.HORIZONTAL_SPLIT
	public static final int VERTICAL_SPLIT = 0;
	public static final int HORIZONTAL_SPLIT = 1;

	private boolean dividerVisible;
	private Widget leftComponent;
	private Widget rightComponent;
	private int orientation;

	private int savedDividerLocation;
	private int savedSize;

	private double resizeWeight;
	private int dividerLocation;

	private AppW app;

	private boolean splittersFrozen = false;

	/**
	 * For calling the onResize method in a deferred way
	 */
	Scheduler.ScheduledCommand deferredOnRes = new Scheduler.ScheduledCommand() {
		public void execute() {
			onResize();
		}
	};

	/**
	 * For calling the onResize method in a deferred way
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	/* other option; maybe not so good...
	public void deferredOnResize() {
		for (Widget c : getChildren()) {
			if (c instanceof DockSplitPaneW) {
				((DockSplitPaneW)c).deferredOnResize();
			} else if (c instanceof DockPanelW) {
				((DockPanelW)c).deferredOnResize();
			} else if (c instanceof RequiresResize) {
				((RequiresResize)c).onResize();
			}
		}
	}*/

	/*********************************************
	 * Constructs a DockSplitPaneW with default horizontal orientation
	 */
	public DockSplitPaneW(AppW app) {
		this(HORIZONTAL_SPLIT, app);
	}

	/*********************************************
	 * Constructs a DockSplitPaneW with given orientation
	 * 
	 * @param newOrientation
	 */
	public DockSplitPaneW(int newOrientation, AppW app) {
		super(1 / app.getArticleElement().getScaleX());
		this.app = app;
		setOrientation(newOrientation);
		setResizeWeight(0.5);
		dividerVisible = false;
		// this.addPropertyChangeListener(paneResizeListener);

		dividerLocation = 100;
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
		return frozen || (app.isApplet() && !app.isRightClickEnabled());
	}

	public double computeDividerLocationRecursive() {

		double sizeLeft = 0;

		if (getLeftComponent() instanceof DockSplitPaneW) {
			sizeLeft = ((DockSplitPaneW)getLeftComponent()).computeSizeRecursive(orientation);
		} else if (getLeftComponent() instanceof DockPanelW) {
			sizeLeft = ((DockPanelW)getLeftComponent()).getEmbeddedSize();
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

	public double computeSizeRecursive(int parentOrientation) {

		double size = 0;

		if (orientation == parentOrientation) {
			if (getLeftComponent() instanceof DockSplitPaneW) {
				size += ((DockSplitPaneW)getLeftComponent()).computeSizeRecursive(orientation);
			} else if (getLeftComponent() instanceof DockPanelW) {
				size += ((DockPanelW)getLeftComponent()).getEmbeddedSize();
			}

			if (getRightComponent() instanceof DockSplitPaneW) {
				size += ((DockSplitPaneW)getRightComponent()).computeSizeRecursive(orientation);
			} else if (getRightComponent() instanceof DockPanelW) {
				size += ((DockPanelW)getRightComponent()).getEmbeddedSize();
			}

			return size;
		}

		double size2 = 0;

		if (getLeftComponent() instanceof DockSplitPaneW) {
			size = ((DockSplitPaneW)getLeftComponent()).computeSizeRecursive(parentOrientation);
		} else if (getLeftComponent() instanceof DockPanelW) {
			// if orientation is different, use settings instead of embeddedSize
			if (parentOrientation == VERTICAL_SPLIT) {
				size = ((DockPanelW)getLeftComponent()).getEstimatedSize().getHeight();
			} else {
				size = ((DockPanelW)getLeftComponent()).getEstimatedSize().getWidth();
			}
		}

		if (getRightComponent() instanceof DockSplitPaneW) {
			size2 = ((DockSplitPaneW)getRightComponent()).computeSizeRecursive(parentOrientation);
		} else if (getRightComponent() instanceof DockPanelW) {
			// if orientation is different, use settings instead of embeddedSize
			if (parentOrientation == VERTICAL_SPLIT) {
				size2 = ((DockPanelW)getRightComponent()).getEstimatedSize().getHeight();
			} else {
				size2 = ((DockPanelW)getRightComponent()).getEstimatedSize().getWidth();
			}
		}

		return Math.max(size, size2);
	}

	public void setDividerLocation(int location) {
		dividerLocation = location;
		setComponents();
	}

	public void setDividerLocationSilent(int location) {
		dividerLocation = location;
	}

	public void setDividerLocation(double proportion) {
		if (getOrientation() == VERTICAL_SPLIT) {
			setDividerLocation((int) (proportion * getOffsetHeight()));
		} else {
			setDividerLocation((int) (proportion * getOffsetWidth()));
		}
	}

	public Widget getRightComponent() {
		return rightComponent;
	}

	public Widget getLeftComponent() {
		return leftComponent;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int newOrientation) {
		orientation = newOrientation;
	}

	public double getResizeWeight() {
		return resizeWeight;
	}

	/**
	 * Listener for split pane resizing. Transfers focus to the split pane after
	 * a resize event, thus removing focus and sending a focus lost event to the
	 * DockSplitPane components.
	 */
	// PropertyChangeListener paneResizeListener = new PropertyChangeListener()
	// {
	// public void propertyChange(PropertyChangeEvent changeEvent) {
	// JSplitPane splitPane = (JSplitPane) changeEvent.getSource();
	// String propertyName = changeEvent.getPropertyName();
	// if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
	// splitPane.requestFocus();
	// }
	// }
	// };

	/**
	 * Return the component which is opposite to the parameter.
	 * 
	 * @param component
	 * @return
	 */
	public Widget getOpposite(Widget component) {
		if (component == leftComponent)
			return rightComponent;
		else if (component == rightComponent)
			return leftComponent;
		else
			throw new IllegalArgumentException();
	}

	/**
	 * set the left component and check if it's empty when loading file
	 * 
	 * @param component
	 *            componenent
	 */
	public void setLeftComponentCheckEmpty(Widget component) {

		// ensure visibility flags of dock panels set to false
		if (leftComponent != null)
			((DockComponent) leftComponent).setDockPanelsVisible(false);

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
		if (rightComponent != null)
			((DockComponent) rightComponent).setDockPanelsVisible(false);

		setRightComponent(component);
	}

	/**
	 * Set the left component of this DockSplitPane and remove the divider if
	 * the left component is null.
	 */
	public void setLeftComponent(Widget component) {
		leftComponent = component;
		setComponents();
	}

	/**
	 * Set the right component of this DockSplitPane and remove the divider if
	 * the right component is null.
	 */
	public void setRightComponent(Widget component) {
		rightComponent = component;
		setComponents();
	}

	public void setComponentsSilent() {

		// if both components exist give the resizing pane to rightComponent
		// (in Swing this corresponds to resize weight = 1)
		if (leftComponent != null && rightComponent != null) {
			if (orientation == HORIZONTAL_SPLIT) {
				addWest(leftComponent, dividerLocation);
				add(rightComponent);
			} else {
				addNorth(leftComponent, dividerLocation);
				add(rightComponent);
			}
		}
		
		// otherwise put the single component into the resizing pane
		else if (leftComponent != null) {
			if (orientation == HORIZONTAL_SPLIT) {
				add(leftComponent);
			} else {
				add(leftComponent);
			}
		}

		else if (rightComponent != null) {
			add(rightComponent);
		}

		// forceLayout();
		updateDivider();
	}

	private void setComponents() {
		clear();
		setComponentsSilent();
	}

	public void setComponentsSilentRecursive() {
		setComponentsSilent();
		updateUI();
		forceLayout();
		if (getLeftComponent() instanceof DockSplitPaneW) {
			((DockSplitPaneW)getLeftComponent()).setComponentsSilentRecursive();
		} /*else if (getLeftComponent() instanceof DockPanelW) {
			((DockPanelW)getLeftComponent()).updatePanel();
		}*/
		if (getRightComponent() instanceof DockSplitPaneW) {
			((DockSplitPaneW)getRightComponent()).setComponentsSilentRecursive();
		} /*else if (getRightComponent() instanceof DockPanelW) {
			((DockPanelW)getRightComponent()).updatePanel();
		}*/
	}

	/**
	 * Replace a component from the split pane with another.
	 * 
	 * @param component
	 * @param replacement
	 */
	public void replaceComponent(Widget component, Widget replacement) {
		if (component == leftComponent)
			setLeftComponent(replacement);
		else if (component == rightComponent)
			setRightComponent(replacement);
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Update the visibility of the divider.
	 */
	private void updateDivider() {
		if (leftComponent == null || rightComponent == null)
			dividerVisible = false;
		else
			dividerVisible = true;
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

	public void saveDividerLocation() {

		if (getOrientation() == VERTICAL_SPLIT) {
			if (getLeftComponent() != null)
				savedDividerLocation = getLeftComponent().getOffsetHeight();
			savedSize = getOffsetHeight();
		} else {
			if (getLeftComponent() != null)
				savedDividerLocation = getLeftComponent().getOffsetWidth();
			savedSize = getOffsetWidth();
		}

		if (getLeftComponent() != null)
			((DockComponent) getLeftComponent()).saveDividerLocation();
		if (getRightComponent() != null)
			((DockComponent) getRightComponent()).saveDividerLocation();

	}

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
			} else if (getResizeWeight() == 0.5) {
				if (savedSize == 0)
					savedSize = 1;
				setDividerLocationRecursive((size * savedDividerLocation)
				        / savedSize, size, orientation1);
			} else {
				setDividerLocationRecursive(
				        size
				                - checkLocation(savedSize
				                        - savedDividerLocation, size), size,
				        orientation1);
			}
		} else
			propagateDividerLocation(size, size, orientation1);

	}

	private static int checkLocation(int location, int size) {

		int min = MIN_SIZE;
		if (min > size / 2)
			min = size / 2;

		if (location < min)
			return min;

		if (location > size - min)
			return size - min;

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
		if (getLeftComponent() != null)
			((DockComponent) getLeftComponent()).updateDividerLocation(
			        sizeLeft, orientation1);
		if (getRightComponent() != null)
			((DockComponent) getRightComponent()).updateDividerLocation(
			        sizeRight, orientation1);
	}

	public String toString(String prefix) {
		String prefix2 = prefix + "-";
		return "\n" + prefix + "split=" + getDividerLocation() + "\n" + prefix
		        + "width=" + getOffsetWidth() + "\n" + prefix + "left"
		        + ((DockComponent) getLeftComponent()).toString(prefix2) + "\n"
		        + prefix + "right"
		        + ((DockComponent) getRightComponent()).toString(prefix2);

	}

	public boolean updateResizeWeight() {
		boolean takesNewSpaceLeft = false;
		boolean takesNewSpaceRight = false;

		if ((getLeftComponent() != null)
		        && ((DockComponent) getLeftComponent()).updateResizeWeight())
			takesNewSpaceLeft = true;
		if ((getRightComponent() != null)
		        && ((DockComponent) getRightComponent()).updateResizeWeight())
			takesNewSpaceRight = true;

		if (takesNewSpaceLeft) {
			if (takesNewSpaceRight)
				setResizeWeight(0.5);
			else
				setResizeWeight(1);
			return true;
		} else if (takesNewSpaceRight) {
			setResizeWeight(0);
			return true;
		}

		setResizeWeight(0);
		return false;

	}

	public void setDockPanelsVisible(boolean visible) {
		if (leftComponent != null)
			((DockComponent) leftComponent).setDockPanelsVisible(visible);
		if (rightComponent != null)
			((DockComponent) rightComponent).setDockPanelsVisible(visible);
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

		public TreeReader(AppW app) {
			this.app = app;
			splitPaneInfo = new ArrayList<DockSplitPaneData>();
		}

		public DockSplitPaneData[] getInfo(DockSplitPaneW rootPane) {
			splitPaneInfo.clear();

			// get window dimensions
			// TODO: Are these the correct dimensions needed for calculations below?
			// e.g. do we include menubar height?
			windowWidth = (int) app.getWidth();
			windowHeight = (int) app.getHeight();
			
			saveSplitPane("", rootPane);

			DockSplitPaneData[] info = new DockSplitPaneData[splitPaneInfo
			        .size()];
			return (DockSplitPaneData[]) splitPaneInfo.toArray(info);
		}

		/**
		 * Save a split pane into the splitPaneInfo array list
		 * 
		 * @param parentLocation
		 * @param parent
		 */
		private void saveSplitPane(String parentLocation, DockSplitPaneW parent) {
			double dividerLocation = 0.2;

			// get relative divider location depending on the current
			// orientation
			if (parent.getOrientation() == HORIZONTAL_SPLIT) {
				dividerLocation = (double) parent.getDividerLocation()
				        / windowWidth;
			} else {
				dividerLocation = (double) parent.getDividerLocation()
				        / windowHeight;
			}

			splitPaneInfo.add(new DockSplitPaneData(parentLocation,
			        dividerLocation, parent.getOrientation()));

			if (parentLocation.length() > 0)
				parentLocation += ",";

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

		// If the split pane gets really narrow and right view is hidden,
		if (orientation == HORIZONTAL_SPLIT && getOffsetWidth() > 0
		        && getOffsetWidth() < this.getWidget(0).getOffsetWidth()) {
			this.setWidgetSize(this.getWidget(0), this.getOffsetWidth());
		}
 else if (orientation == VERTICAL_SPLIT && getOffsetHeight() > 0
		        && getOffsetHeight() < this.getWidget(0).getOffsetHeight()) {
			this.setWidgetSize(this.getWidget(0), this.getOffsetHeight());
		}
		// it's only important to resize components so that
		// the divider should be inside
		if (getLeftComponent() instanceof RequiresResize) {
			((RequiresResize)getLeftComponent()).onResize();
		}

		if (getRightComponent() instanceof DockSplitPaneW) {
			((RequiresResize)getRightComponent()).onResize();
			((DockSplitPaneW)getRightComponent()).checkDividerIsOutside();
		} else if (getRightComponent() instanceof RequiresResize) {
			((RequiresResize)getRightComponent()).onResize();
		}

		if (orientation == HORIZONTAL_SPLIT) {
			if (getLeftComponent() != null && getLeftComponent().getOffsetWidth() > 0) {
				setDividerLocationSilent(getLeftComponent().getOffsetWidth());
			}
		} else {
			if (getLeftComponent() != null && getLeftComponent().getOffsetHeight() > 0) {
				setDividerLocationSilent(getLeftComponent().getOffsetHeight());
			}
		}
    }

	public void checkDividerIsOutside() {

		// w, h should contain the dimensions visible on screen
		int w = this.getElement().getClientWidth();
		int h = this.getElement().getClientHeight();

		if (orientation == HORIZONTAL_SPLIT) {
			if (getDividerLocation() >= w && (w > 0)) {
				setDividerLocation(0.5);
			}
		} else {
			if (getDividerLocation() >= h && (h > 0)) {
				setDividerLocation(0.5);
			}
		}
	}
}
