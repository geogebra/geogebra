package org.geogebra.desktop.gui.layout;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.desktop.main.AppD;

/**
 * Split pane which is used to separate two DockPanels.
 * 
 * @author Florian Sonner
 */
public class DockSplitPane extends JSplitPane implements DockComponent {
	private static final long serialVersionUID = 1L;
	private boolean dividerVisible;

	/**
	 * new split pane
	 */
	public DockSplitPane() {
		this(JSplitPane.HORIZONTAL_SPLIT);

		this.addPropertyChangeListener(paneResizeListener);
	}

	/**
	 * @param newOrientation
	 *            orientation
	 */
	public DockSplitPane(int newOrientation) {
		super(newOrientation);

		// setResizeWeight(0.5);
		setBorder(BorderFactory.createEmptyBorder());

		dividerVisible = false;
		this.addPropertyChangeListener(paneResizeListener);
	}

	/**
	 * Listener for split pane resizing. Transfers focus to the split pane after
	 * a resize event, thus removing focus and sending a focus lost event to the
	 * DockSplitPane components.
	 */
	PropertyChangeListener paneResizeListener = changeEvent -> {
		JSplitPane splitPane = (JSplitPane) changeEvent.getSource();
		String propertyName = changeEvent.getPropertyName();
		if (propertyName
				.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
			splitPane.requestFocus();
		}
	};

	/**
	 * Return the component which is opposite to the parameter.
	 * 
	 * @param component
	 *            part of layout
	 * @return opposite component
	 */
	public Component getOpposite(Component component) {
		if (component == leftComponent) {
			return rightComponent;
		} else if (component == rightComponent) {
			return leftComponent;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the left component of this DockSplitPane and remove the divider if
	 * the left component is null.
	 */
	@Override
	public void setLeftComponent(Component component) {

		super.setLeftComponent(component);
		updateDivider();
	}

	/**
	 * Set the right component of this DockSplitPane and remove the divider if
	 * the right component is null.
	 */
	@Override
	public void setRightComponent(Component component) {

		super.setRightComponent(component);
		updateDivider();
	}

	/**
	 * Replace a component from the split pane with another.
	 * 
	 * @param component
	 *            part of layout
	 * @param replacement
	 *            replacement
	 */
	public void replaceComponent(Component component, Component replacement) {
		if (component == leftComponent) {
			setLeftComponent(replacement);
		} else if (component == rightComponent) {
			setRightComponent(replacement);
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Update the visibility of the divider.
	 */
	private void updateDivider() {
		if (leftComponent == null || rightComponent == null) {
			dividerVisible = false;
		} else {
			dividerVisible = true;
		}
	}

	/**
	 * Update the UI by drawing the divider just if the dividerVisible attribute
	 * is set to true.
	 */
	@Override
	public void updateUI() {
		super.updateUI();

		SplitPaneUI splitPaneUI = getUI();
		if (splitPaneUI instanceof BasicSplitPaneUI) {
			BasicSplitPaneUI basicUI = (BasicSplitPaneUI) splitPaneUI;
			basicUI.getDivider().setVisible(dividerVisible);
		}
	}

	/**
	 * @param index child index (0 or 1)
	 * @return child component for given index
	 */
	public Component getChild(int index) {
		return index == 1 ? getRightComponent() : getLeftComponent();
	}

	/**
	 * set the index-th component and check if it's empty when loading file
	 * @param index 0 for left/top, 1 for right/bottom
	 * @param component
	 *            component
	 */
	public void setComponentCheckEmpty(int index, Component component) {
		if (getChild(index) != null) {
			((DockComponent) getChild(index)).setDockPanelsVisible(false);
		}
		if (index == 1) {
			setRightComponent(component);
		} else {
			setLeftComponent(component);
		}
	}

	/**
	 * A helper class used to get the split pane information array of the
	 * current layout. Use {@link #getInfo(DockSplitPane)} with the root pane as
	 * parameter to get the array.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-26
	 */
	public static class TreeReader {
		private AppD app;
		private ArrayList<DockSplitPaneData> splitPaneInfo;
		private int windowWidth;
		private int windowHeight;

		/**
		 * @param app
		 *            application
		 */
		public TreeReader(AppD app) {
			this.app = app;

			splitPaneInfo = new ArrayList<DockSplitPaneData>();
		}

		/**
		 * @param rootPane
		 *            layout root
		 * @return layout description
		 */
		public DockSplitPaneData[] getInfo(DockSplitPane rootPane) {
			splitPaneInfo.clear();

			windowWidth = app.getFrame().getWidth();

			windowHeight = app.getFrame().getHeight();

			saveSplitPane("", rootPane);

			DockSplitPaneData[] info = new DockSplitPaneData[splitPaneInfo
					.size()];
			return splitPaneInfo.toArray(info);
		}

		/**
		 * Save a split pane recursively into the splitPaneInfo array list
		 * 
		 * @param parentLocation0 starting location
		 * @param parent starting pane
		 */
		private void saveSplitPane(String parentLocation0,
				DockSplitPane parent) {
			double dividerLocation = 0.2;
			String parentLocation = parentLocation0;
			// get relative divider location depending on the current
			// orientation
			if (parent.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
				dividerLocation = (double) parent.getDividerLocation()
						/ windowWidth;
			} else {
				dividerLocation = (double) parent.getDividerLocation()
						/ windowHeight;
			}

			splitPaneInfo.add(new DockSplitPaneData(parentLocation,
					dividerLocation, parent.getOrientation()));

			if (parentLocation.length() > 0) {
				parentLocation += ",";
			}

			if (parent.getLeftComponent() instanceof DockSplitPane) {
				saveSplitPane(parentLocation + "0",
						(DockSplitPane) parent.getLeftComponent());
			}

			if (parent.getRightComponent() instanceof DockSplitPane) {
				saveSplitPane(parentLocation + "1",
						(DockSplitPane) parent.getRightComponent());
			}
		}
	}

	private int savedDividerLocation;
	private int savedSize;

	@Override
	public void saveDividerLocation() {

		if (getOrientation() == JSplitPane.VERTICAL_SPLIT) {
			if (getLeftComponent() != null) {
				savedDividerLocation = getLeftComponent().getHeight();
			}
			savedSize = getHeight();
		} else {
			if (getLeftComponent() != null) {
				savedDividerLocation = getLeftComponent().getWidth();
			}
			savedSize = getWidth();
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

}