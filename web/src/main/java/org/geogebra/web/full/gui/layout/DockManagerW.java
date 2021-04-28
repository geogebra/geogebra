package org.geogebra.web.full.gui.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.gui.layout.DockComponent;
import org.geogebra.common.gui.layout.DockManager;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.io.layout.ShowDockPanelListener;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.StringConsumer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.base.Js;

/**
 * Class responsible to manage the whole docking area of the window.
 * 
 * Based on desktop implementation by Florian Sonner
 */
public class DockManagerW extends DockManager {
	/** default keyboard height */
	public static final int DEFAULT_KEYBOARD_HEIGHT = 228;
	/** application */
	AppW app;
	private LayoutW layout;
	private double kbHeight = 0;

	// null it to trigger orientation change for the first time.
	private ExtendedBoolean portrait = ExtendedBoolean.UNKNOWN;

	/**
	 * The root split pane.
	 */
	DockSplitPaneW rootPane;

	/**
	 * The dock panel which has the focus at the moment.
	 */
	private DockPanelW focusedDockPanel;

	/**
	 * The euclidian dock panel which had the focus the last.
	 */
	private EuclidianDockPanelWAbstract focusedEuclidianDockPanel;

	/**
	 * A list with all registered dock panels.
	 */
	private ArrayList<DockPanelW> dockPanels;

	/**
	 * List of DockPanelListeners, informed when some dockpanel is shown.
	 */
	private List<ShowDockPanelListener> showDockPanelListener;
	private boolean panelsMoved;

	private TreeSet<Integer> viewsInPerspective = new TreeSet<>();

	/**
	 * @param layout
	 *            app layout
	 */
	public DockManagerW(LayoutW layout) {
		this.layout = layout;
		this.app = layout.getApplication();

		dockPanels = new ArrayList<>();
		showDockPanelListener = new ArrayList<>();
	}

	/**
	 * Register a new dock panel. Use Layout::registerPanel() as public
	 * interface.
	 * 
	 * @param dockPanel
	 *            new panel
	 */
	public void registerPanel(DockPanelW dockPanel) {
		dockPanels.add(dockPanel);
		dockPanel.register(this);
	}

	/**
	 * remove panel for the dock panels list
	 * 
	 * @param dockPanel
	 *            panel
	 */
	@Override
	public void unRegisterPanel(DockPanel dockPanel) {
		dockPanels.remove(dockPanel);
	}

	/**
	 * Apply a certain perspective by arranging the dock panels in the requested
	 * order.
	 * 
	 * @param spData
	 *            split panes
	 * @param dpData
	 *            panels
	 * 
	 * @see LayoutW#applyPerspective(org.geogebra.common.io.layout.Perspective)
	 */
	public void applyPerspective(DockSplitPaneData[] spData,
			DockPanelData[] dpData) {

		if (dockPanels != null) {
			updatePanelsForPerspective(dpData);
		}
		// int panelDim;

		if (spData.length > 0) {
			DockSplitPaneW[] splitPanes = new DockSplitPaneW[spData.length];

			// hm should mean
			HashMap<DockSplitPaneW, Integer> spw = new HashMap<>();
			HashMap<DockSplitPaneW, Integer> sph = new HashMap<>();

			// construct the split panes
			for (int i = 0; i < spData.length; ++i) {
				splitPanes[i] = new DockSplitPaneW(spData[i].getOrientation(),
						app);
				spw.put(splitPanes[i], 0);
				sph.put(splitPanes[i], 0);
			}

			// cascade the split panes
			if (rootPane != null) {
				Widget rootPaneParent = rootPane.getParent();
				String styles = rootPane.getStyleName();
				if (rootPaneParent != null) {
					if (rootPaneParent instanceof VerticalPanel) {
						rootPane.clear();
						rootPane.removeFromParent();
						rootPane = splitPanes[0];
						((VerticalPanel) rootPaneParent).add(rootPane);
					} else if (rootPaneParent instanceof DockLayoutPanel) {
						rootPane.clear();
						rootPane.removeFromParent();
						rootPane = splitPanes[0];
						((DockLayoutPanel) rootPaneParent).add(rootPane);
					} else {
						rootPane.clear();
						rootPane = splitPanes[0];
					}
				} else {
					rootPane.clear();
					rootPane = splitPanes[0];
				}
				rootPane.setStyleName(styles);
			} else {
				rootPane = splitPanes[0];
			}

			// loop through every but the first split pane
			for (int i = 1; i < spData.length; ++i) {
				DockSplitPaneW currentParent = rootPane;

				// a similar system as it's used to determine the position of
				// the dock panels (see comment in DockManager::show())
				// 0: turn left/up, 1: turn right/down
				String[] directions = spData[i].getLocation().split(",");

				// get the parent split pane, the last position is reserved for
				// the location
				// of the current split pane and therefore ignored here
				for (int j = 0; j < directions.length - 1; ++j) {
					if (directions[j].equals("0")) {
						currentParent = (DockSplitPaneW) currentParent
								.getLeftComponent();
					} else {
						currentParent = (DockSplitPaneW) currentParent
								.getRightComponent();
					}
				}

				// insert the split pane
				if (directions[directions.length - 1].equals("0")) {
					currentParent.setLeftComponentCheckEmpty(splitPanes[i]);
				} else {
					currentParent.setRightComponentCheckEmpty(splitPanes[i]);
				}
			}
			// sort panels right to left: needed for fullscreen button
			Arrays.sort(dpData, new Comparator<DockPanelData>() {

				@Override
				public int compare(DockPanelData o1, DockPanelData o2) {
					// bottom to top sorting: need to replace 0 (top) with st
					// bigger than 2 (bottom)
					return o1.getEmbeddedDef().replace('0', '4')
							.compareTo(o2.getEmbeddedDef().replace('0', '4'));
				}
			});
			// now insert the dock panels
			for (DockPanelData dpItem : dpData) {
				DockPanelW panel = getPanel(dpItem.getViewId());
				// skip panels which will not be drawn in the main window
				if (!dpItem.isVisible() || dpItem.isOpenInFrame()
				// eg run "no 3D" with 3D View open in saved settings
						|| panel == null || !PerspectiveDecoder.isAllowed(panel.getViewId(),
						app.getConfig().getForcedPerspective())) {
					continue;
				}
				// attach view to kernel (being attached multiple times is
				// ignored)
				app.getGuiManager().attachView(panel.getViewId());

				// if(dpData[i].isOpenInFrame()) {
				// show(panel);
				// continue;
				// }

				DockSplitPaneW currentParent = rootPane;
				String[] directions = dpItem.getEmbeddedDef().split(",");

				/*
				 * Get the parent split pane of this dock panel and ignore the
				 * last direction as its reserved for the position of the dock
				 * panel itself.
				 * 
				 */
				for (int j = 0; j < directions.length - 1; ++j) {
					Widget current;
					if (directions[j].equals("0")
							|| directions[j].equals("3")) {
						current = currentParent.getLeftComponent();
					} else {
						current = currentParent.getRightComponent();
					}
					if (current instanceof  DockSplitPaneW) {
						currentParent = (DockSplitPaneW) current;
					}
				}
				if (currentParent == null) {
					Log.error("Invalid perspective");
					currentParent = rootPane;
				} else if (directions[directions.length - 1].equals("0")
						|| directions[directions.length - 1].equals("3")) {
					currentParent.setLeftComponentCheckEmpty(panel);
				} else {
					currentParent.setRightComponentCheckEmpty(panel);
				}

				panel.setEmbeddedSize(dpItem.getEmbeddedSize());

				panel.updatePanel(true);

				// this might have to belong to panel.updatePanel
				// maybe not needed if updatePanel is right
				// maybe wrong if onResize makes things wrong
				if (currentParent
						.getOrientation() == SwingConstants.VERTICAL_SPLIT) {
					panel.setHeight(dpItem.getEmbeddedSize() + "px");
				} else {
					panel.setWidth(dpItem.getEmbeddedSize() + "px");
				}

				if (currentParent
						.getOrientation() == SwingConstants.VERTICAL_SPLIT) {
					int panelDim = panel.getEstimatedSize().getWidth();
					sph.put(currentParent, sph.get(currentParent)
							+ dpItem.getEmbeddedSize());
					spw.put(currentParent,
							Math.max(spw.get(currentParent), panelDim));
				} else {
					int panelDim = panel.getEstimatedSize().getHeight();
					spw.put(currentParent, spw.get(currentParent)
							+ dpItem.getEmbeddedSize());
					sph.put(currentParent,
							Math.max(sph.get(currentParent), panelDim));
				}

				DockSplitPaneW oldParent = currentParent;
				while (oldParent != rootPane) {
					if (oldParent.getParent() instanceof DockSplitPaneW) {
						DockSplitPaneW otherParent = oldParent;
						oldParent = (DockSplitPaneW) oldParent.getParent();

						if (oldParent
								.getOrientation() == SwingConstants.VERTICAL_SPLIT) {
							sph.put(oldParent, sph.get(oldParent)
									+ dpItem.getEmbeddedSize());
							spw.put(oldParent, Math.max(spw.get(oldParent),
									spw.get(otherParent)));
						} else {
							spw.put(oldParent, spw.get(oldParent)
									+ dpItem.getEmbeddedSize());
							sph.put(oldParent, Math.max(sph.get(oldParent),
									spw.get(otherParent)));
						}
					} else {
						break;
					}
				}

				// move toolbar to main container
				// if(panel.hasToolbar()) {
				// ToolbarContainer mainContainer = ((GuiManagerD)
				// app.getGuiManager()).getToolbarPanel();
				// mainContainer.addToolbar(getPanel(dpData[i].getViewId()).getToolbar());
				// }
			}

			// recursive update resize weights for giving new space to euclidian
			// views

			updateSplitPanesResizeWeight();

			int windowWidth;
			int windowHeight;
			// TODO this is how the layout should work in all cases, only the
			// old AppWApplication needs the other way
			if (app instanceof AppWFull) {

				// Emulate the way split panes i n Java applets are sized:
				// 2) Use the ggb xml window size to set dividers
				// 1) Resize the applet to the data-param dimensions

				// Now find the correct applet window dimensions and resize the
				// rootPane.

				windowWidth = app.getWidthForSplitPanel(spw.get(rootPane) <= 0
						? app.getPreferredSize().getWidth()
						: spw.get(rootPane));

				// this is applet window height. Priority: applet params > split
				// pane sizes > guess based on overall window size (assumes
				// desktop toolbar)

				windowHeight = app.getHeightForSplitPanel(sph.get(rootPane) <= 0
						? app.getPreferredSize().getHeight()
						: sph.get(rootPane));

				rootPane.clear();
				rootPane.setPixelSize(windowWidth, windowHeight);

				// Set the window dimensions to the ggb xml <window> tag size.
				int windowWidth2 = app.getPreferredSize().getWidth();
				int windowHeight2 = app.getPreferredSize().getHeight();

				// Set the split pane dividers
				if (windowWidth2 == 0) {
					windowWidth2 = windowWidth;
					windowHeight2 = windowHeight;
					app.setPreferredSize(
							new Dimension(windowWidth2, windowHeight2));
				}

				setSplitPaneDividers(spData, splitPanes, windowHeight2,
						windowWidth2, windowHeight, windowWidth);

				// for debugging
				// rootPane.setPixelSize(spw.get(rootPane), sph.get(rootPane));

			}

			markAlonePanel();

			setActiveToolBarDefault(dpData);

			// is focused dock panel not visible anymore => reset
			if (focusedDockPanel != null && !focusedDockPanel.isVisible()) {
				focusedDockPanel = null;
				focusedEuclidianDockPanel = null;
			}

		}

		panelsMoved = false;
		// update all labels at once
		setLabels();
		app.updateVoiceover();
	}

	private void updatePanelsForPerspective(DockPanelData[] dpData) {
		// hide existing external windows
		for (DockPanelW panel : dockPanels) {
			panel.setAlone(false);
		}
		TreeSet<Integer> updated = new TreeSet<>();
		// copy dock panel info settings
		for (int i = 0; i < dpData.length; ++i) {
			updated.add(dpData[i].getViewId());
			DockPanelW panel = getPanel(dpData[i]);
			if (panel != null) {
				panel.setToolbarString(dpData[i].getToolbarString());
				panel.setFrameBounds((Rectangle) dpData[i].getFrameBounds());
				panel.setEmbeddedDef(dpData[i].getEmbeddedDef());
				panel.setEmbeddedSize(dpData[i].getEmbeddedSize());
				panel.setShowStyleBar(dpData[i].showStyleBar());
				panel.setTabId(dpData[i].getTabId());

				// detach views which were visible, but are not in the new
				// perspective
				if (panel.isVisible() && (!dpData[i].isVisible()
						|| dpData[i].isOpenInFrame())) {
					app.getGuiManager().detachView(panel.getViewId());
				}

				panel.setVisible(
						dpData[i].isVisible() && !dpData[i].isOpenInFrame());

				if (dpData[i].getViewId() == App.VIEW_EUCLIDIAN
						|| dpData[i].getViewId() == App.VIEW_EUCLIDIAN2) {
					((EuclidianDockPanelWAbstract) panel).reset();
				}
			}
		}
		for (DockPanelW dockPanel : dockPanels) {
			if (!dockPanel.hasPlane()
					&& !updated.contains(dockPanel.getViewId())) {
				dockPanel.setVisible(false);
			}
		}
	}

	private void setPreferredSizes(DockSplitPaneW pane, int h, int w) {
		pane.setPreferredWidth(w, h);
		if (pane.getOrientation() == SwingConstants.VERTICAL_SPLIT) {
			if (pane.getLeftComponent() instanceof DockSplitPaneW) {
				setPreferredSizes((DockSplitPaneW) pane.getLeftComponent(), h,
						w);
			}
			if (pane.getRightComponent() instanceof DockSplitPaneW) {
				setPreferredSizes((DockSplitPaneW) pane.getRightComponent(),
						h - pane.getDividerLocation() - pane.getSplitterSize(),
						w);
			}
		} else { // horizontal
			if (pane.getLeftComponent() instanceof DockSplitPaneW) {
				setPreferredSizes((DockSplitPaneW) pane.getLeftComponent(), h,
						pane.getDividerLocation());
			}
			if (pane.getRightComponent() instanceof DockSplitPaneW) {
				setPreferredSizes((DockSplitPaneW) pane.getRightComponent(), h,
						w - pane.getDividerLocation() - pane.getSplitterSize());
			}
		}
	}

	@Override
	public void ensureFocus() {
		if (this.focusedDockPanel != null) {
			return;
		}
		boolean focusDone = false;
		for (int i = 0; i < dockPanels.size() && !focusDone; ++i) {
			if (!dockPanels.get(i).hasPlane()) { // we can't focus on
				// view for plane
				// otherwise we will
				// recreate it
				if (dockPanels.get(i) != null
						&& dockPanels.get(i).isVisible()) {
					setFocusedPanel(dockPanels.get(i));
					// don't like algebra view as focused view
					if (dockPanels.get(i).getViewId() != App.VIEW_ALGEBRA
							&& dockPanels.get(i)
									.getViewId() != App.VIEW_PROPERTIES) {
						focusDone = true;
					}
				}
			}
		}
	}

	/**
	 * Sets split pane divider locations
	 * 
	 * @param spData
	 *            split pane sizes
	 * @param splitPanes
	 *            actual split panes
	 * @param windowHeight
	 *            center pane width of original file
	 * @param windowWidth
	 *            center pane height of original file
	 * @param theRealWindowHeight
	 *            target center pane height (might be affected by data param)
	 * @param theRealWindowWidth
	 *            target center pane width (might be affected by data param)
	 */
	protected void setSplitPaneDividers(DockSplitPaneData[] spData,
			DockSplitPaneW[] splitPanes, int windowHeight, int windowWidth,
			int theRealWindowHeight, int theRealWindowWidth) {

		int divLoc;

		// set the dividers of the split panes
		for (int i = 0; i < spData.length; ++i) {
			splitPanes[i].clear();
			// don't set splitpane width/height here, because that would call
			// onResize

			double sdl = spData[i].getDividerLocation();
			if (Double.isNaN(sdl) || Double.isInfinite(sdl)) {

				// if omitted, divider locations should be computed (fixing
				// problems in most cases, but maybe not in all)
				// sdl should be x that 0 <= x <= 1
				sdl = splitPanes[i].computeDividerLocationRecursive();
			}
			int split = splitPanes[i].getSplitterSize();
			if (spData[i].getOrientation() == SwingConstants.VERTICAL_SPLIT) {
				divLoc = Math.max(Math.min((int) (sdl * windowHeight),
						theRealWindowHeight - split), 0);
				splitPanes[i].setDividerLocationSilent(divLoc);

			} else {
				divLoc = Math.max(Math.min((int) (sdl * windowWidth),
						theRealWindowWidth - split), 0);
				splitPanes[i].setDividerLocationSilent(divLoc);
			}
		}
		setPreferredSizes(rootPane, theRealWindowHeight, theRealWindowWidth);
		rootPane.setComponentsSilentRecursive();

	}

	/**
	 * update dispatching of new space with split panes
	 */
	private void updateSplitPanesResizeWeight() {

		rootPane.updateResizeWeight();
	}

	/**
	 * Start the drag'n'drop process of a DockPanel.
	 * 
	 * @param panel
	 *            dragged panel
	 */
	public void drag(DockPanelW panel) {
		// Do not allow docking in case this is the last view
		if (panel.getParentSplitPane() == rootPane) {
			if (rootPane.getOpposite(panel) == null) {
				return;
			}
		}

		if (app.getAppletParameters().getDataParamShowMenuBar(false)) {
			DockGlassPaneW glassPane = ((AppWFull) app).getGlassPane();
			if (glassPane.getGeoGebraElement() == null) {
				glassPane.setGeoGebraElement(app.getGeoGebraElement());
			}
			glassPane.attach(this, (int) app.getWidth(), (int) app.getHeight());
			glassPane.startDrag(new DnDState(panel));
		}
	}

	/**
	 * Stop the drag'n'drop procedure and drop the component to the the defined
	 * location.
	 * 
	 * @param dndState
	 *            state
	 */
	public void drop(DnDState dndState) {

		DockPanelW source = dndState.getSource();
		DockSplitPaneW sourceParent = source.getParentSplitPane();
		DockPanelW target = dndState.getTarget();
		Widget opposite = sourceParent.getOpposite(source);

		// No action required
		if (target == null || target == source && !dndState.isRegionOut()) {
			return;
		}

		// Hide the source first
		hide(source, false, true);

		source.setVisible(true);

		// Add the source panel at the new position
		DockSplitPaneW newSplitPane = new DockSplitPaneW(app);
		int dndRegion = dndState.getRegion();

		// Determine the orientation of the new split pane
		if (dndRegion == DnDState.LEFT || dndRegion == DnDState.LEFT_OUT
				|| dndRegion == DnDState.RIGHT
				|| dndRegion == DnDState.RIGHT_OUT) {
			newSplitPane.setOrientation(SwingConstants.HORIZONTAL_SPLIT);
		} else {
			newSplitPane.setOrientation(SwingConstants.VERTICAL_SPLIT);
		}

		if (dndState.isRegionOut()
				&& (target.getParent() == sourceParent || target == source)) {
			dndRegion >>= 4;
			dndState.setRegion(dndRegion);
		}

		boolean updatedRootPane = false;

		if (dndState.isRegionOut()) {
			DockSplitPaneW targetParent = target.getParentSplitPane();

			if (targetParent == rootPane) {
				rootPane = newSplitPane;
			} else {
				((DockSplitPaneW) targetParent.getParent())
						.replaceComponent(targetParent, newSplitPane);
			}

			if (dndRegion == DnDState.LEFT_OUT
					|| dndRegion == DnDState.TOP_OUT) {
				newSplitPane.setRightComponent(targetParent);
				newSplitPane.setLeftComponent(source);
			} else {
				newSplitPane.setRightComponent(source);
				newSplitPane.setLeftComponent(targetParent);
			}
		} else {
			if (source == target) {
				if (opposite instanceof DockPanel) {
					if (((DockPanelW) opposite).getParentSplitPane()
							.getOpposite(opposite) == null) {
						rootPane = newSplitPane;
					} else {
						((DockPanelW) opposite).getParentSplitPane()
								.replaceComponent(opposite, newSplitPane);
					}
				} else {
					if (opposite == rootPane) {
						rootPane = newSplitPane;
					} else {
						((DockSplitPaneW) opposite.getParent())
								.replaceComponent(opposite, newSplitPane);
					}
				}

				if (dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					newSplitPane.setRightComponent(opposite);
					newSplitPane.setLeftComponent(source);
				} else {
					newSplitPane.setRightComponent(source);
					newSplitPane.setLeftComponent(opposite);
				}
			} else if (target.getParentSplitPane().getOpposite(target) == null
					&& target.getParentSplitPane() == rootPane) {
				rootPane.clear();

				if (dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					rootPane.setLeftComponent(source);
					rootPane.setRightComponent(target);
				} else {
					rootPane.setLeftComponent(target);
					rootPane.setRightComponent(source);
				}

				updatedRootPane = true;
				rootPane.setOrientation(newSplitPane.getOrientation());
			} else {
				target.getParentSplitPane().replaceComponent(target,
						newSplitPane);
				if (dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					newSplitPane.setRightComponent(target);
					newSplitPane.setLeftComponent(source);
				} else {
					newSplitPane.setRightComponent(source);
					newSplitPane.setLeftComponent(target);
				}
			}
		}

		app.updateCenterPanel();
		// updatePanels();

		double dividerLocation;

		if (dndRegion == DnDState.LEFT || dndRegion == DnDState.LEFT_OUT
				|| dndRegion == DnDState.TOP || dndRegion == DnDState.TOP_OUT) {
			dividerLocation = 0.4;
		} else {
			dividerLocation = 0.6;
		}

		if (updatedRootPane) {
			setDividerLocation(rootPane, dividerLocation);
		} else {
			setDividerLocation(newSplitPane, dividerLocation);
		}

		// update new space dispatching
		updateSplitPanesResizeWeight();

		// has to be called *after* the toolbar was added to the container
		setFocusedPanel(source);

		unmarkAlonePanels();
		markAlonePanel();
		panelsMoved = true;
	}

	private void setDividerLocation(DockSplitPaneW splitPane,
			final double dividerLocation) {
		final DockSplitPaneW sp = splitPane;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				sp.setDividerLocation(dividerLocation);
				rootPane.deferredOnResize();
			}
		});
	}

	private void setDividerLocationAbs(DockSplitPaneW splitPane,
			final int dividerLocation) {
		final DockSplitPaneW sp = splitPane;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				sp.setDividerLocation(dividerLocation);
				rootPane.deferredOnResize();
			}
		});
	}

	/**
	 * Show a DockPanel identified by its ID.
	 * 
	 * @param viewId
	 *            view ID
	 */
	public void show(int viewId) {
		show(getPanel(viewId));
	}

	/**
	 * Show a DockPanel where it was displayed the last time - either in the
	 * main window or in a separate frame.
	 * 
	 * The location of the DockPanel in the main window is given by the
	 * definition string stored in DockPanelInfo.getEmbeddedDef(). A definition
	 * string can be read like a list of directions, where numbers represents
	 * the four directions we can go:
	 * 
	 * 0: Top 1: Right 2: Bottom 3: Left
	 * 
	 * A definition string like "0,3,2" is read by the program this way: - Go to
	 * the top (=0) container of the root pane. - Go to the container at the
	 * left (=3) of the current container. - Insert the DockPanel at the bottom
	 * (=2) of the current container.
	 * 
	 * Note that the program differs between the top & left and bottom & right
	 * position while the DockSplitPane just differs between a left and right
	 * component and the orientation of the split pane.
	 * 
	 * As the layout of the panels is changed frequently and may be completely
	 * different if the DockPanel is inserted again, the algorithm ignores all
	 * directions which are not existing anymore in order to get the best
	 * possible result. Using the example from above, the second direction ("3")
	 * may be ignored if the top container of the root pane isn't divided
	 * anymore or the orientation of the container was changed. The algorithm
	 * will continue with "2" and will insert the DockPanel at the bottom of the
	 * top container of the root pane.
	 * 
	 * @param panel
	 *            panel
	 */
	public void show(DockPanelW panel) {
		if (panel.isAttached() && panel.isVisible()) {
			return;
		}
		panel.setVisible(true);
		panel.setHidden(false);

		// TODO causes any problems?
		app.getGuiManager().attachView(panel.getViewId());

		app.persistWidthAndHeight();
		// Transform the definition into an array of integers
		String[] def = panel.getEmbeddedDef().split(",");
		int[] locations = new int[def.length];

		for (int i = 0; i < def.length; ++i) {
			if (def[i].length() == 0) {
				def[i] = "1";
			}

			locations[i] = Integer.parseInt(def[i]);

			if (locations[i] > 3 || locations[i] < 0) {
				locations[i] = 3; // left as default direction
			}
		}

		// We insert this panel at the left by default
		if (locations.length == 0) {
			locations = new int[] { 3 };
		}

		DockSplitPaneW currentPane = rootPane;
		int secondLastPos = -1;

		// Get the location of our new DockPanel (ignore last entry)
		for (int i = 0; i < locations.length - 1; ++i) {
			// The orientation of the current pane does not match the stored
			// orientation, skip this
			if (currentPane
					.getOrientation() == SwingConstants.HORIZONTAL_SPLIT
					&& (locations[i] == 0 || locations[i] == 2)) {
				continue;
			} else if (currentPane
					.getOrientation() == SwingConstants.VERTICAL_SPLIT
					&& (locations[i] == 1 || locations[i] == 3)) {
				continue;
			}

			Widget component;

			if (locations[i] == 0 || locations[i] == 3) {
				component = currentPane.getLeftComponent();
			} else {
				component = currentPane.getRightComponent();
			}

			if (!(component instanceof DockSplitPaneW)) {
				secondLastPos = locations[i];
				break;
			}
			// else
			currentPane = (DockSplitPaneW) component;
		}

		int size = panel.getEmbeddedSize();
		int lastPos = locations[locations.length - 1];

		DockSplitPaneW newSplitPane = new DockSplitPaneW(app);

		if (lastPos == 0 || lastPos == 2) {
			newSplitPane.setOrientation(SwingConstants.VERTICAL_SPLIT);
		} else {
			newSplitPane.setOrientation(SwingConstants.HORIZONTAL_SPLIT);
		}

		// the size (height / width depending upon lastPos) of the parent
		// element,
		// this value is necessary to prevent panels which completely hide
		// their opposite element

		// the component opposite to the current component
		int[] oppositeDim = new int[] { 0, 0 };

		Widget opposite = prepareRootPaneForInsert(oppositeDim, currentPane,
					newSplitPane, lastPos, secondLastPos);
		// save divider locations to prevent not visible views
		if (opposite != null) {
			((DockComponent) opposite).saveDividerLocation();
		}

		if (lastPos == 0 || lastPos == 3) {
			newSplitPane.setLeftComponent(panel);
			newSplitPane.setRightComponent(opposite);
		} else {
			newSplitPane.setLeftComponent(opposite);
			newSplitPane.setRightComponent(panel);
		}

		if (!app.isIniting()) {
			app.updateCenterPanel();
		}

		// check new split pane size regarding orientation
		int newSplitPaneSize;
		if (newSplitPane
				.getOrientation() == SwingConstants.HORIZONTAL_SPLIT) {
			newSplitPaneSize = oppositeDim[0];
		} else {
			newSplitPaneSize = oppositeDim[1];
		}
		// check if panel size is not too large
		if (size + DockComponent.MIN_SIZE > newSplitPaneSize) {
			size = newSplitPaneSize / 2;
			// set the divider location
		}

		// ----------------
		// TODO turned this off for now ... need to fix for web
		// ---------------
		if (lastPos == 0 || lastPos == 3) {
			newSplitPane.setDividerLocation(size);
		} else {
			newSplitPane.setDividerLocation(newSplitPaneSize - size);
		}

		// App.debug("\nnewSplitPaneSize = "+newSplitPaneSize+"\nsize =
		// "+size);
		// App.debug("\n======\n"+((DockComponent) opposite).toString(""));
		// re dispatch divider locations to prevent not visible views
		if (opposite != null) {
			((DockComponent) opposite).updateDividerLocation(
					newSplitPaneSize - size, newSplitPane.getOrientation());
		}

		updateAfterShow(panel);
	}

	private Widget prepareRootPaneForInsert(int[] oppositeDim,
			DockSplitPaneW currentPane, DockSplitPaneW newSplitPane,
			final int lastPos, final int secondLastPos) {
		Widget opposite;
		if (secondLastPos == -1) {
			opposite = rootPane;
			oppositeDim[0] = opposite.getOffsetWidth();
			oppositeDim[1] = opposite.getOffsetHeight();
			rootPane = newSplitPane;

			// in root pane, the opposite may be null
			if (lastPos == 0 || lastPos == 3) {
				if (((DockSplitPaneW) opposite).getLeftComponent() == null) {
					opposite = ((DockSplitPaneW) opposite).getRightComponent();
				}
			} else {
				if (((DockSplitPaneW) opposite).getRightComponent() == null) {
					opposite = ((DockSplitPaneW) opposite).getLeftComponent();
				}
			}
		} else {
			if (secondLastPos == 0 || secondLastPos == 3) {
				opposite = currentPane.getLeftComponent();
			} else {
				opposite = currentPane.getRightComponent();
			}

			// in root pane, the opposite may be null
			if (opposite == null) {
				opposite = currentPane.getOpposite(null);
				oppositeDim[0] = opposite.getOffsetWidth();
				oppositeDim[1] = opposite.getOffsetHeight();
				rootPane = newSplitPane;
			} else if (opposite.getParent() == rootPane
					&& rootPane.getOpposite(opposite) == null) {
				oppositeDim[0] = opposite.getOffsetWidth();
				oppositeDim[1] = opposite.getOffsetHeight();
				rootPane = newSplitPane;
			} else {
				oppositeDim[0] = opposite.getOffsetWidth();
				oppositeDim[1] = opposite.getOffsetHeight();
				currentPane.replaceComponent(opposite, newSplitPane);
			}
		}
		newSplitPane.setPreferredWidth(oppositeDim[0], oppositeDim[1]);
		return opposite;

	}

	private void updateAfterShow(DockPanelW panel) {
		panel.updatePanel(false);

		// here we need to resize both panel and opposite
		panel.getParentSplitPane().deferredOnResize();

		// update dispatching of new space
		updateSplitPanesResizeWeight();

		// add toolbar to main toolbar container if necessary, *has* to be
		// called after
		// DockPanel::updatePanel() as the toolbar is initialized there
		if (app.isShowToolbar()) {
			// original
			// ToolbarContainer mainContainer = ((GuiManagerD)
			// app.getGuiManager()).getToolbarPanel();
			// mainContainer.addToolbar(panel.getToolbar());
			// mainContainer.updateToolbarPanel();

			app.setShowToolBar(true, true);
			app.getGuiManager().setActivePanelAndToolbar(panel.getViewId());
		}

		// has to be called *after* the toolbar was added to the container
		setFocusedPanel(panel);

		unmarkAlonePanels();
		markAlonePanel();

		for (ShowDockPanelListener l : showDockPanelListener) {
			l.showDockPanel(panel);
		}

	}

	/**
	 * Hide a dock panel identified by the view ID.
	 * 
	 * @param viewId
	 *            vew ID
	 * @param isPermanent
	 *            permanent?
	 * @return true if succeeded to hide the panel
	 */
	public boolean hide(int viewId, boolean isPermanent) {
		return hide(getPanel(viewId), isPermanent, false);
	}

	/**
	 * Hide a dock panel permanently.
	 * 
	 * @param panel
	 *            panel
	 * @return true if succeeded to hide the panel
	 */
	public boolean hide(DockPanelW panel) {
		return hide(panel, true, false);
	}

	/**
	 * close the dock panel
	 * 
	 * @param viewId
	 *            id of the dock panel
	 * @param isPermanent
	 *            says if the close is permanent
	 */
	public void closePanel(int viewId, boolean isPermanent) {
		closePanel(getPanel(viewId), isPermanent);
	}

	/**
	 * close the dock panel
	 * 
	 * @param panel
	 *            dock panel
	 * @param isPermanent
	 *            says if the close is permanent
	 */
	public void closePanel(DockPanelW panel, boolean isPermanent) {
		if (hide(panel, isPermanent, false)) {
			app.updateMenubar();

			if (getFocusedPanel() == panel) {
				setFocusedPanel(null);
			}
		}
	}

	/**
	 * 
	 * @return true if the layout contains less than two panels
	 */
	private boolean containsLessThanTwoPanels() {
		return (rootPane == null) || (rootPane.getLeftComponent() == null)
				|| (rootPane.getRightComponent() == null);
	}

	/**
	 * Hide a dock panel.
	 * 
	 * @param panel
	 *            panel
	 * @param isPermanent
	 *            If this change is permanent.
	 * @param fromDrop
	 *            whether it was dropped
	 * @return true if it succeeded to hide the panel
	 */
	public boolean hide(DockPanelW panel, boolean isPermanent,
			boolean fromDrop) {
		if (!panel.isVisible() || panel.getParentSplitPane() == null) {
			// some views (especially CAS) will close so slowly that the user is
			// able
			// to issue another "close" call, therefore we quit quietly
			return false;
		}

		// check if the panel is the last one
		if (containsLessThanTwoPanels()) {
			return false;
		}

		// do in the end, because we need to calculate width/height
		// panel.setHidden(!isPermanent);
		// panel.setVisible(false);

		setFocusedPanel(null);

		if (isPermanent) {
			app.getGuiManager().detachView(panel.getViewId());
		}

		DockSplitPaneW parent = panel.getParentSplitPane();
		int parentOffsetWidth = parent.getOffsetWidth();
		int parentOffsetHeight = parent.getOffsetHeight();
		app.persistWidthAndHeight();
		// Save settings
		if (parent.getOrientation() == SwingConstants.HORIZONTAL_SPLIT) {
			panel.setEmbeddedSize(panel.getOffsetWidth());
		} else {
			panel.setEmbeddedSize(panel.getOffsetHeight());
		}

		panel.setEmbeddedDef(panel.calculateEmbeddedDef());

		Widget opposite = parent.getOpposite(panel);

		// save divider location and size (if DockSplitPane)
		if (opposite != null) {
			((DockComponent) opposite).saveDividerLocation();
		}
		int orientation = parent.getOrientation();
		int size;
		if (orientation == SwingConstants.VERTICAL_SPLIT) {
			size = parentOffsetHeight;
		} else {
			size = parentOffsetWidth;
		}

		if (parent == rootPane) {
			if (opposite instanceof DockSplitPaneW) {
				rootPane = (DockSplitPaneW) opposite;
			} else {
				parent.replaceComponent(panel, null);
			}
			app.updateCenterPanel();
		} else {
			DockSplitPaneW grandParent = (DockSplitPaneW) parent
					.getParent();
			int dividerLoc = grandParent.getDividerLocation();
			grandParent.replaceComponent(parent, opposite);
			grandParent.setDividerLocation(dividerLoc);
			grandParent.forceLayout();
		}

		// re dispatch divider location
		if (opposite != null) {
			((DockComponent) opposite).updateDividerLocation(size,
					orientation);
		}

		// TODO: resize here?
		// if(isPermanent) {
		// app.validateComponent();
		// }

		if (fromDrop) {
			if (opposite.getParent() instanceof DockSplitPaneW) {
				((DockSplitPaneW) opposite.getParent()).onResize();
			} else if (opposite instanceof DockSplitPaneW) {
				((DockSplitPaneW) opposite).onResize();
			}
		} else if (opposite != null) {
			if (opposite.getParent() instanceof DockSplitPaneW) {
				((DockSplitPaneW) opposite.getParent()).deferredOnResize();
			} else if (opposite instanceof DockSplitPaneW) {
				((DockSplitPaneW) opposite).deferredOnResize();
			}
		}

		if (panel.hasToolbar()) {
			// ToolbarContainer mainContainer = ((GuiManagerD)
			// app.getGuiManager()).getToolbarPanel();
			// mainContainer.removeToolbar(panel.getToolbar());
			// mainContainer.updateToolbarPanel();
			app.setShowToolBar(true, true);
			// active toolbar should not be the panel's any more
			if (app.getGuiManager().getActiveToolbarId() == panel
					.getViewId()) {
				setActiveToolBarDefault(null);
			}
		}
		app.getGuiManager().refreshCustomToolsInToolBar();
		app.updateToolBar();

		panel.setHidden(!isPermanent);
		panel.setVisible(false);

		markAlonePanel();

		return true;
	}

	/**
	 * set active toolbar to default
	 */
	private void setActiveToolBarDefault(DockPanelData[] dpData) {

		GuiManagerInterfaceW guiManager = app.getGuiManager();

		// default
		int toolbarID = App.VIEW_EUCLIDIAN;
		if (dpData != null) {
			viewsInPerspective.clear();
			for (int i = 0; i < dpData.length; i++) {
				if (dpData[i].isVisible() && !dpData[i].isOpenInFrame()) {
					viewsInPerspective.add(dpData[i].getViewId());
				}
			}
			if (viewsInPerspective.contains(App.VIEW_CAS)) {
				toolbarID = App.VIEW_CAS;
			} else if (viewsInPerspective.contains(App.VIEW_SPREADSHEET)) {
				toolbarID = App.VIEW_SPREADSHEET;
			} else if (viewsInPerspective.contains(App.VIEW_EUCLIDIAN)) {
				toolbarID = App.VIEW_EUCLIDIAN;
			} else if (viewsInPerspective.contains(App.VIEW_EUCLIDIAN2)) {
				toolbarID = App.VIEW_EUCLIDIAN2;
			} else if (viewsInPerspective.contains(App.VIEW_EUCLIDIAN3D)) {
				toolbarID = App.VIEW_EUCLIDIAN3D;
			} else if (viewsInPerspective
					.contains(App.VIEW_PROBABILITY_CALCULATOR)) {
				toolbarID = App.VIEW_PROBABILITY_CALCULATOR;
			}
		}
		// show CAS-toolbar in CAS-perspective (same for Spreadsheet)
		// in the other perspectives use Euclidian-toolbar (if available)
		else if (guiManager.hasCasView() && getPanel(App.VIEW_CAS) != null
				&& getPanel(App.VIEW_CAS).isVisible()) {
			toolbarID = App.VIEW_CAS;
		} else if (guiManager.hasSpreadsheetView()
				&& getPanel(App.VIEW_SPREADSHEET) != null
				&& getPanel(App.VIEW_SPREADSHEET).isVisible()) {
			toolbarID = App.VIEW_SPREADSHEET;
		} else if (app.getEuclidianView1().isShowing()) {
			toolbarID = App.VIEW_EUCLIDIAN;
		} else if (app.hasEuclidianView2(1)
				&& app.getEuclidianView2(1).isShowing()) {
			toolbarID = App.VIEW_EUCLIDIAN2;
		} else if (app.isEuclidianView3Dinited()
				&& app.showView(App.VIEW_EUCLIDIAN3D)) {
			toolbarID = App.VIEW_EUCLIDIAN3D;
			// what else can it be??
		} else if (guiManager.hasProbabilityCalculator()
				&& getPanel(App.VIEW_PROBABILITY_CALCULATOR) != null
				&& getPanel(App.VIEW_PROBABILITY_CALCULATOR).isVisible()) {
			toolbarID = App.VIEW_PROBABILITY_CALCULATOR;
		} else if (guiManager.hasAlgebraView()
				&& app.getGuiManager().getAlgebraView().isShowing()) {
			// algebra view has no toolbar!
			toolbarID = App.VIEW_ALGEBRA;
		}

		guiManager.setActivePanelAndToolbar(toolbarID);
	}

	/**
	 * Change the focused panel to "panel".
	 * 
	 * @param panel
	 *            panel
	 */
	public void setFocusedPanel(DockPanel panel) {
		setFocusedPanel(panel, true);
	}

	/**
	 * Change the focused panel to "panel". TODO: partly unimplemented
	 * 
	 * @param panel
	 *            panel
	 * @param updatePropertiesView
	 *            update the properties view
	 */
	public void setFocusedPanel(DockPanel panel, boolean updatePropertiesView) {
		if (focusedDockPanel == panel) {
			return;
		}

		// in case there is no focused panel there is also no focused euclidian
		// dock panel
		if (panel == null) {
			if (focusedEuclidianDockPanel != null) {
				focusedEuclidianDockPanel = null;
			}
		} else {
			if (panel instanceof EuclidianDockPanelWAbstract
					&& focusedEuclidianDockPanel != panel) {
				// if a panel has focus and that panel is a euclidian dock panel
				// change the focused euclidian dock panel to that panel
				focusedEuclidianDockPanel = (EuclidianDockPanelWAbstract) panel;

				// (panels which are not euclidian dock panels do not change the
				// focused
				// euclidian dock panel (ie the old is kept))
			}
		}

		focusedDockPanel = (DockPanelW) panel;

		if (focusedDockPanel != null) {
			focusedDockPanel.setFocus(updatePropertiesView);
		}

		app.getGuiManager().updateMenubarSelection();
	}

	/**
	 * Changes the focused panel to the dock panel with ID viewId. Uses
	 * {@link DockManagerW#setFocusedPanel(DockPanel)} internally but adds some
	 * validation checks.
	 * 
	 * @param viewId
	 *            view ID
	 * @return true if focus was changed, false if the requested dock panel does
	 *         not exist or is invisible at the moment
	 */
	@Override
	public boolean setFocusedPanel(int viewId) {
		DockPanelW dockPanel = getPanel(viewId);

		if (dockPanel != null && dockPanel.isVisible()) {
			setFocusedPanel(dockPanel);
			return true;
		}

		// else
		return false;
	}

	/**
	 * @return The dock panel which has focus at the moment.
	 */
	public DockPanelW getFocusedPanel() {
		return focusedDockPanel;
	}

	/**
	 * @return The viewId of the dock panel which has focus at the moment.
	 */
	@Override
	public int getFocusedViewId() {

		if (focusedDockPanel == null) {
			return -1;
		}

		return focusedDockPanel.getViewId();
	}

	/**
	 * @return The dock euclidian panel which had focus the last.
	 */
	@Override
	public EuclidianDockPanelWAbstract getFocusedEuclidianPanel() {
		return focusedEuclidianDockPanel;
	}

	/**
	 * If just one panel is visible in the main frame, mark him as 'alone'.
	 */
	private void markAlonePanel() {
		// determine if such a panel exists
		DockPanelW singlePanel = null;

		if (rootPane.getRightComponent() == null) {
			Widget leftComponent = rootPane.getLeftComponent();

			if (leftComponent instanceof DockPanel) {
				singlePanel = (DockPanelW) leftComponent;
			}
		}

		if (rootPane.getLeftComponent() == null) {
			Widget rightComponent = rootPane.getRightComponent();

			if (rightComponent instanceof DockPanel) {
				singlePanel = (DockPanelW) rightComponent;
			}
		}

		// mark the found panel as 'alone'
		if (singlePanel != null) {
			singlePanel.setAlone(true);
		}
	}

	/**
	 * Remove marks from any panel, that it might be alone.
	 */
	private void unmarkAlonePanels() {
		for (DockPanelW panel : dockPanels) {
			if (panel.isAlone()) {
				panel.setAlone(false);
			}
		}
	}

	/**
	 * Update the labels of all DockPanels.
	 */
	@Override
	public void setLabels() {
		for (DockPanelW panel : dockPanels) {
			panel.setLabels();
		}
	}

	/**
	 * Update all DockPanels.
	 * 
	 * This is required if the user changed whether the title bar should be
	 * displayed or not.
	 * 
	 * @see #setLabels()
	 */
	public void updatePanels() {
		for (DockPanelW panel : dockPanels) {
			panel.updatePanel(false);
		}
	}

	/**
	 * @return GeoGebraLayout instance
	 */
	public LayoutW getLayout() {
		return layout;
	}

	/**
	 * 
	 * @param dpData
	 *            data containing view ID and plane for euclidian view for plane
	 * @return a DockPanel
	 */
	public DockPanelW getPanel(DockPanelData dpData) {
		if (dpData.getPlane() == null) {
			return getPanel(dpData.getViewId());
		}

		// euclidian view for plane case
		DockPanelW panel = (DockPanelW) app.getCompanion()
				.createEuclidianDockPanelForPlane(dpData.getViewId(),
						dpData.getPlane());
		if (panel == null) {
			Log.error("panel==null");
			return null;
		}

		// set the view id of the dock panel data for apply perspective
		dpData.setViewId(panel.getViewId());
		return panel;

	}

	/**
	 * Returns a specific DockPanel.
	 * 
	 * Use the constants VIEW_EUCLIDIAN, VIEW_ALGEBRA etc. as viewId.
	 * 
	 * @param viewId
	 *            view ID
	 * @return The panel associated to the viewId
	 */
	@Override
	public DockPanelW getPanel(int viewId) {
		DockPanelW panel = null;
		for (DockPanelW dockPanel : dockPanels) {
			if (dockPanel.getViewId() == viewId) {
				panel = dockPanel;
				break;
			}
		}

		return panel;
	}

	/**
	 * @return All dock panels
	 */
	public DockPanelW[] getPanels() {
		return dockPanels.toArray(new DockPanelW[0]);
	}

	/**
	 * @return The root split pane which contains all other elements like
	 *         DockPanels or DockSplitPanes.
	 */
	public DockSplitPaneW getRoot() {
		return rootPane;
	}

	/**
	 * re-initializes root panel if needed
	 * 
	 * @param panel
	 *            The app frame
	 */
	public void init(Panel panel) {
		if (rootPane == null) {
			rootPane = new DockSplitPaneW(app);
		}
		if (rootPane.getParent() == null) {
			panel.add(rootPane);
		}
		panelsMoved = false;
	}

	/**
	 * @param l
	 *            listener
	 */
	public void addShowDockPanelListener(ShowDockPanelListener l) {
		showDockPanelListener.add(l);
	}

	/**
	 * @param drag
	 *            whether to enable dragging
	 */
	public void enableDragging(boolean drag) {
		for (int i = 0; i < getPanels().length; i++) {
			getPanels()[i].enableDragging(drag);
		}
	}

	@Override
	public void resizePanels() {
		for (int i = 0; i < getPanels().length; i++) {
			getPanels()[i].onResize();
		}
	}

	/**
	 * In most cases keyboard goes to focused panel, but if focus is in EV or
	 * lost, other panel must be chosen
	 * 
	 * @return panel that should get keyboard
	 */
	public DockPanelW getPanelForKeyboard() {
		DockPanelW focusedPanel = getFocusedPanel();
		List<Integer> keyboardViews = ((AppWFull) app).getKeyboardManager()
				.getKeyboardViews();
		if (focusedPanel != null && keyboardViews.contains(focusedPanel.getViewId())) {
			return focusedPanel;
		}
		for (int panelId : keyboardViews) {
			DockPanelW panel = getPanel(panelId);
			if (panel.isVisible()) {
				return panel;
			}
		}
		return null;
	}

	@Override
	public int getNumberOfOpenViews() {
		int num = 0;
		for (DockPanelW d : this.dockPanels) {
			if (d.isVisible()) {
				num++;
			}
		}
		return num;
	}

	/**
	 * @return app
	 */
	public AppW getApp() {
		return app;
	}

	@Override
	public void adjustViews(boolean force) {
		DockPanelW avPanel = getPanel(App.VIEW_ALGEBRA);
		if (avPanel != null) {
			avPanel.onResize();
		}

		if ((!app.canResize() && !force) || panelsMoved) {
			return;
		}
		calculateKeyboardHeight();
		ExtendedBoolean old = portrait;
		portrait = ExtendedBoolean.newExtendedBoolean(app.isPortrait());
		// ExtendedBoolean
		// .newExtendedBoolean(app.getWidth() < app.getHeight());
		if (force || old != portrait) {
			// run only if oreintation has changed;
			final double landscape = PerspectiveDecoder.landscapeRatio(app,
					app.getWidth());

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					adjustViews(landscape);
				}
			});
		}
	}

	/**
	 * @param landscapeRatio
	 *            preferred landscape ratio
	 */
	protected void adjustViews(double landscapeRatio) {
		DockPanelW avPanel = getPanel(App.VIEW_ALGEBRA);
		if (avPanel == null) {
			return;
		}

		DockSplitPaneW split = avPanel.getParentSplitPane();
		if (split == null || split != getRoot()) {
			return;
		}

		Widget opposite = split.getOpposite(avPanel);

		if (!(opposite instanceof EuclidianDockPanelWAbstract)) {
			return;
		}

		AlgebraViewW av = ((AlgebraViewW) app.getAlgebraView());
		double avHeight = Math.max(av.getInputTreeItem().getOffsetHeight(),
				120);
		double appHeight = app.getHeight();
		ToolbarPanel toolbar = null;
		double visibleKB = kbHeight;
		if (app.isUnbundled()) {
			toolbar = ((ToolbarDockPanelW) avPanel).getToolbar();
			avHeight = toolbar.isOpen() ? toolbar.getMinVHeight()
					: ToolbarPanel.CLOSED_HEIGHT_PORTRAIT;
			if (!app.getAppletFrame().isKeyboardShowing()) {
				visibleKB = 0;
			}
		} else {
			appHeight -= GLookAndFeel.TOOLBAR_OFFSET;
		}

		if (app.isPortrait()) {
			if (toolbar != null && toolbar.isClosed()) {
				closePortrait(split, toolbar);
			} else {
				setDividerLocationAbs(split,
						(int) Math.max(0, appHeight - visibleKB - avHeight));
			}
		} else {
			double ratio = landscapeRatio;

			if (split.getLeftComponent() == avPanel
					&& split
					.getDividerLocation() <= ToolbarPanel.CLOSED_WIDTH_LANDSCAPE) {
				toolbar.close();
			}
			if (toolbar != null && !toolbar.isOpen()) {
				ratio = ToolbarPanel.CLOSED_WIDTH_LANDSCAPE / app.getWidth();
			}

			setDividerLocationAbs(split, (int) (ratio * app.getWidth()));
		}

		int newOrientation = app.isPortrait() ? SwingConstants.VERTICAL_SPLIT
				: SwingConstants.HORIZONTAL_SPLIT;
		if (newOrientation != split.getOrientation()) {
			split.clear();
			split.setOrientation(newOrientation);
			if (app.isPortrait()) {
				split.setRightComponent(avPanel);
				split.setLeftComponent(opposite);
			} else {
				split.setLeftComponent(avPanel);
				split.setRightComponent(opposite);
			}

			avPanel.tryBuildZoomPanel();
			avPanel.setLayout(false);
			((DockPanelW) opposite).tryBuildZoomPanel();
			((DockPanelW) opposite).setLayout(false);

			if (toolbar != null) {
				toolbar.onOrientationChange();
			}
		}
	}

	/**
	 * Resize probability calculator view; can't be done by CSS because of canvas
	 */
	public void resizeProbabilityCalculator() {
		DockPanelW probPanel = getPanel(App.VIEW_PROBABILITY_CALCULATOR);
		if (probPanel != null) {
			probPanel.deferredOnResize();
		}
	}

	/**
	 * Closes toolbar in portrait mode
	 * 
	 */
	public void closePortrait() {
		DockPanelW avPanel = getPanel(App.VIEW_ALGEBRA);
		if (avPanel == null) {
			return;
		}

		DockSplitPaneW split = avPanel.getParentSplitPane();
		if (split == null || split != getRoot()) {
			return;
		}
		ToolbarPanel toolbar = ((ToolbarDockPanelW) avPanel).getToolbar();
		closePortrait(split, toolbar);
	}

	/**
	 * Closes toolbar in portrait mode
	 * 
	 * @param split
	 *            SpitPanel of the toolbar.
	 * @param toolbar
	 *            to close.
	 */
	public void closePortrait(DockSplitPaneW split, ToolbarPanel toolbar) {
		if (toolbar == null || toolbar.isOpen()) {
			return;
		}

		double height = app.getAppletFrame().computeHeight();
		setDividerLocationAbs(split,
				(int) height - ToolbarPanel.CLOSED_HEIGHT_PORTRAIT
						- ToolbarPanel.VSHADOW_OFFSET);
	}

	private void calculateKeyboardHeight() {
		double kh = app.getAppletFrame().getKeyboardHeight();
		if (kh == 0) {
			kh = DEFAULT_KEYBOARD_HEIGHT;
		}

		if (kbHeight < kh) {
			kbHeight = kh;
		}
	}

	/**
	 * Replace tabbed algebra panel with plain algebra pael or vice versa
	 */
	public void swapAlgebraPanel() {
		DockPanelW old = this.getPanel(App.VIEW_ALGEBRA);
		if (old != null) {
			layout.getDockManager().unRegisterPanel(old);
		}
		layout.registerPanel(
					((AppWFull) app).getActivity().createAVPanel());

	}

	/**
	 * Reset stylebar in all panels when changing classic <=> graphing
	 */
	public void reset() {
		for (DockPanelW dock : this.dockPanels) {
			if (dock.getViewId() != App.VIEW_ALGEBRA) {
				dock.resetStylebar();
			}
		}
	}

	/**
	 * Set active tab(s) from perspective
	 * @param p perspective
	 */
	public void setActiveTab(Perspective p) {
		for (DockPanelData dpData: p.getDockPanelData()) {
			DockPanelW panel = getPanel(dpData.getViewId());
			if (panel != null) {
				panel.setTabId(dpData.getTabId());
			}
		}
	}

	/**
	 * Paint all panels to a canvas
	 * @param c canvas
	 * @param callback consumer for the resulting base64 string (without marker)
	 */
	public void paintPanels(Canvas c, StringConsumer callback) {
		c.setCoordinateSpaceWidth(rootPane.getOffsetWidth());
		c.setCoordinateSpaceHeight(rootPane.getOffsetHeight());
		Runnable counter = new Runnable() {
			private int count = dockPanels.size();
			@Override
			public void run() {
				count--;
				if (count == 0) {
					callback.consume(c.toDataUrl().replace(StringUtil.pngMarker, ""));
				}
			}
		};
		CanvasRenderingContext2D context2d = Js.uncheckedCast(c.getContext("2d"));
		// gray color for the dividers in Classic
		context2d.fillStyle = BaseRenderingContext2D.FillStyleUnionType.of("rgb(200,200,200)");
		context2d.fillRect(0, 0, rootPane.getOffsetWidth(), rootPane.getOffsetHeight());
		for (DockPanelW panel: dockPanels) {
			if (panel.isAttached() && panel.isVisible()) {
				panel.paintToCanvas(context2d, counter,
						panel.getAbsoluteLeft() - rootPane.getAbsoluteLeft(),
						panel.getAbsoluteTop() - rootPane.getAbsoluteTop());
			} else {
				counter.run();
			}
		}
	}
}
