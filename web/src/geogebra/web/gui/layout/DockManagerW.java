package geogebra.web.gui.layout;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.layout.DockComponent;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.io.layout.ShowDockPanelListener;
import geogebra.common.main.App;
import geogebra.html5.awt.GRectangleW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelWAbstract;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class responsible to manage the whole docking area of the window.
 * 
 * @author Florian Sonner
 */
public class DockManagerW implements  SetLabels {
	private AppW app;
	private LayoutW layout;
	
	/**
	 * False if the application is running in unsigned mode. We can only listen to 
	 * euclidian view focus changes in this case.
	 */
	private boolean hasFullFocusSystem;
	
	/**
	 * The glass panel used for drag'n'drop.
	 */
	private DockGlassPaneW glassPane;	
	
	/**
	 * The root split pane.
	 */
	private DockSplitPaneW rootPane;
	
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
	
	
	
	/**
	 * @param layout
	 */
	public DockManagerW(LayoutW layout) {
		this.layout = layout;
		this.app = layout.getApplication();
		
		dockPanels = new ArrayList<DockPanelW>();
		showDockPanelListener=new ArrayList<ShowDockPanelListener>();
		
		if (App.isFullAppGui()) {
			glassPane = app.getAppFrame().getGlassPane();
			glassPane.attach(this);
		}
		
		//if(!app.isApplet()) {
		//	app.setGlassPane(glassPane);
		//}
		
		// register focus changes
	//	try {
	//		Toolkit.getDefaultToolkit().addAWTEventListener(this , AWTEvent.MOUSE_EVENT_MASK);
	//		hasFullFocusSystem = true;
	//	} catch(Exception e) {
	//		hasFullFocusSystem = false;
	//	}
	}
	
	/**
	 * Register a new dock panel. Use Layout::registerPanel() as public interface.
	 * 
	 * @param dockPanel
	 */
	public void registerPanel(DockPanelW dockPanel) {
		dockPanels.add(dockPanel);
		dockPanel.register(this);
	}
	
	
	/**
	 * remove panel for the dock panels list
	 * @param dockPanel panel
	 */
	public void unRegisterPanel(DockPanel dockPanel) {
		dockPanels.remove(dockPanel);
	}
	
	/**
	 * Apply a certain perspective by arranging the dock panels in the requested order.
	 * 
	 * @param spData
	 * @param dpData
	 * 
	 * @see LayoutD#applyPerspective(geogebra.io.layout.Perspective)
	 */
	public void applyPerspective(DockSplitPaneData[] spData, DockPanelData[] dpData) {		
		
		if(dockPanels != null) {			
			// hide existing external windows
			for(DockPanelW panel : dockPanels) {
				if(panel.isOpenInFrame() && panel.isVisible()) {
					hide(panel);
				}
				
				panel.setAlone(false);
			}
			
			// copy dock panel info settings
			for(int i = 0; i < dpData.length; ++i) {
				DockPanelW panel = getPanel(dpData[i]);
				if(panel == null) {
					// TODO insert error panel
				}else{
					panel.setToolbarString(dpData[i].getToolbarString());
					panel.setFrameBounds((GRectangleW) dpData[i].getFrameBounds());
					panel.setEmbeddedDef(dpData[i].getEmbeddedDef());
					panel.setEmbeddedSize(dpData[i].getEmbeddedSize());
					panel.setShowStyleBar(dpData[i].showStyleBar());
					panel.setOpenInFrame(dpData[i].isOpenInFrame());
					
					// detach views which were visible, but are not in the new perspective
					if(panel.isVisible() && !dpData[i].isVisible()) {
						app.getGuiManager().detachView(panel.getViewId());
					}
					
					panel.setVisible(dpData[i].isVisible());
				}
			}
		}
		
		if(spData.length > 0) {
			DockSplitPaneW[] splitPanes = new DockSplitPaneW[spData.length];
			
			// construct the split panes
			for(int i = 0; i < spData.length; ++i) {
				splitPanes[i] = new DockSplitPaneW(spData[i].getOrientation());
			}
			
			// cascade the split panes
			rootPane = splitPanes[0];
			
			// loop through every but the first split pane
			for(int i = 1; i < spData.length; ++i) {
				DockSplitPaneW currentParent = rootPane;
				
				// a similar system as it's used to determine the position of the dock panels (see comment in DockManager::show())
				// 0: turn left/up, 1: turn right/down
				String[] directions = spData[i].getLocation().split(",");
				
				// get the parent split pane, the last position is reserved for the location
				// of the current split pane and therefore ignored here
				for(int j = 0; j < directions.length - 1; ++j) {
					if(directions[j].equals("0")) {
						currentParent = (DockSplitPaneW)currentParent.getLeftComponent();
					} else {
						currentParent = (DockSplitPaneW)currentParent.getRightComponent();
					}
				}
				
				// insert the split pane
				if(directions[directions.length - 1].equals("0")) {
					currentParent.setLeftComponentCheckEmpty(splitPanes[i]);
				} else {
					currentParent.setRightComponentCheckEmpty(splitPanes[i]);
				}
			}

			// now insert the dock panels
			for(int i = 0; i < dpData.length; ++i) {
				DockPanelW panel = getPanel(dpData[i].getViewId());
				// skip panels which will not be drawn in the main window
				if(!dpData[i].isVisible()
						// eg run "no 3D" with 3D View open in saved settings
						|| panel == null){
					continue;
				}
				
				// attach view to kernel (being attached multiple times is ignored)
				app.getGuiManager().attachView(panel.getViewId());
				
				if(dpData[i].isOpenInFrame()) {
					show(panel);
					continue;
				}
				
				DockSplitPaneW currentParent = rootPane;
				String[] directions = dpData[i].getEmbeddedDef().split(",");
				
				/* 
				 * Get the parent split pane of this dock panel and ignore the last
				 * direction as its reserved for the position of the dock panel itself.
				 * 
				 * In contrast to the algorithm used in the show() method we'll not take care
				 * of invalid positions as the data should not be corrupted.
				 */
				for(int j = 0; j < directions.length - 1; ++j) {
					if(directions[j].equals("0") || directions[j].equals("3")) {
						currentParent = (DockSplitPaneW)currentParent.getLeftComponent();
					} else {
						currentParent = (DockSplitPaneW)currentParent.getRightComponent();
					}
				}
				if(currentParent==null){
					App.error("Invalid perspective");
					
				}
				else if(directions[directions.length - 1].equals("0") || directions[directions.length - 1].equals("3")) {
					currentParent.setLeftComponentCheckEmpty((Widget) panel);
				} else {
					currentParent.setRightComponentCheckEmpty((Widget) panel);
				}
				
				panel.updatePanel();
				
				// move toolbar to main container
			//	if(panel.hasToolbar()) {
			//		ToolbarContainer mainContainer = ((GuiManagerD) app.getGuiManager()).getToolbarPanel();
			//		mainContainer.addToolbar(getPanel(dpData[i].getViewId()).getToolbar());
			//	}
			}
			
			//recursive update resize weights for giving new space to euclidian views
			updateSplitPanesResizeWeight();
			
			//int windowWidth = app.getPreferredSize().width;
			//int windowHeight = app.getPreferredSize().height;
			
			int windowWidth;
			int windowHeight;

			if (app.isApplet() || !App.isFullAppGui()) {
				windowWidth = app.getDataParamWidth();
				windowHeight = app.getDataParamHeight();
			} else {
				windowWidth = app.getAppFrame().getOffsetWidth();
				windowHeight = app.getAppFrame().getOffsetWidth();
			}
			
			// set the dividers of the split panes
			for(int i = 0; i < spData.length; ++i) {
				if(spData[i].getOrientation() == DockSplitPaneW.VERTICAL_SPLIT)
					splitPanes[i].setDividerLocation((int)(spData[i].getDividerLocation() * windowHeight));
				else 
					splitPanes[i].setDividerLocation((int)(spData[i].getDividerLocation() * windowWidth));
				
				splitPanes[i].updateUI();
			}
			
			markAlonePanel();
			
			// is focused dock panel not visible anymore => choose another one
		//	if(focusedDockPanel == null || !focusedDockPanel.isVisible()) {
		//		for(DockPanelW panel : dockPanels) {
				//	if(panel.isVisible() && !panel.isInFrame()) {
				//		setFocusedPanel(panel);
				//	}
		//		}
		//	}
			
		}
		
		// update all labels at once
		setLabels();
	}
	
	/** 
	 * update dispatching of new space with split panes
	 */
	private void updateSplitPanesResizeWeight(){
		
		rootPane.updateResizeWeight();
	}

	/**
	 * Start the drag'n'drop process of a DockPanel.
	 * 
	 * @param panel
	 */
	public void drag(DockPanelW panel) {
		// Do not allow docking in case this is the last view
		if(panel.getParentSplitPane() == rootPane) {
			if(rootPane.getOpposite(panel) == null) {
				return;
			}
		}

		if (App.isFullAppGui())
			glassPane.startDrag(new DnDState(panel));
	}
	
	/**
	 * Stop the drag'n'drop procedure and drop the component to the the defined
	 * location.
	 * 
	 * @param dndState
	 */
	public void drop(DnDState dndState) {
	
		DockPanelW source = dndState.getSource();		
		DockSplitPaneW sourceParent = source.getParentSplitPane();
		DockPanelW target = dndState.getTarget();
		Widget opposite = sourceParent.getOpposite(source);
		
		// No action required
		if(target == null || target == source && !dndState.isRegionOut()) {
			return;
		}
		
		// Hide the source first
		hide(source, false);
		
		source.setVisible(true);
		
		// Add the source panel at the new position
		DockSplitPaneW newSplitPane = new DockSplitPaneW();
		int dndRegion = dndState.getRegion();
		
		// Determine the orientation of the new split pane
		if(dndRegion == DnDState.LEFT || dndRegion == DnDState.LEFT_OUT ||
			dndRegion == DnDState.RIGHT || dndRegion == DnDState.RIGHT_OUT)
		{
			newSplitPane.setOrientation(DockSplitPaneW.HORIZONTAL_SPLIT);
		} else {
			newSplitPane.setOrientation(DockSplitPaneW.VERTICAL_SPLIT);
		}
		
		if(dndState.isRegionOut() && (target.getParent() == sourceParent || target == source)) {
			dndRegion >>= 4;
			dndState.setRegion(dndRegion);
		}
		
		boolean updatedRootPane = false;
		
		if(dndState.isRegionOut()) {
			DockSplitPaneW targetParent = target.getParentSplitPane();
			
			if(targetParent == rootPane) {
				rootPane = newSplitPane;
			} else {
				((DockSplitPaneW)targetParent.getParent()).replaceComponent(targetParent, newSplitPane);
			}
			
			if(dndRegion == DnDState.LEFT_OUT || dndRegion == DnDState.TOP_OUT) {
				newSplitPane.setRightComponent(targetParent);
				newSplitPane.setLeftComponent(source);
			} else {
				newSplitPane.setRightComponent(source);
				newSplitPane.setLeftComponent(targetParent);
			}
		} else {
			if(source == target) {
				if(opposite instanceof DockPanel) {
					if(((DockPanelW) opposite).getParentSplitPane().getOpposite(opposite) == null)
						rootPane = newSplitPane;
					else
						((DockPanelW) opposite).getParentSplitPane().replaceComponent(opposite, newSplitPane);
				} else {
					if(opposite == rootPane)
						rootPane = newSplitPane;
					else
						((DockSplitPaneW)opposite.getParent()).replaceComponent(opposite, newSplitPane);
				}
				
				if(dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					newSplitPane.setRightComponent(opposite);
					newSplitPane.setLeftComponent(source);
				} else {
					newSplitPane.setRightComponent(source);
					newSplitPane.setLeftComponent(opposite);
				}
			} else if(target.getParentSplitPane().getOpposite(target) == null && target.getParentSplitPane() == rootPane) {
				rootPane.clear();
				
				if(dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) { 
					rootPane.setLeftComponent(source);
					rootPane.setRightComponent(target);
				} else {
					rootPane.setLeftComponent(target);
					rootPane.setRightComponent(source);
				}
				
				updatedRootPane = true;
				rootPane.setOrientation(newSplitPane.getOrientation());
			}  else {
				target.getParentSplitPane().replaceComponent(target, newSplitPane);
				if(dndRegion == DnDState.LEFT || dndRegion == DnDState.TOP) {
					newSplitPane.setRightComponent(target);
					newSplitPane.setLeftComponent(source);
				} else {
					newSplitPane.setRightComponent(source);
					newSplitPane.setLeftComponent(target);
				}
			}
		}
		
		app.updateCenterPanel(true);
		//updatePanels();
		
		
		
		double dividerLocation = 0;
		
		if(dndRegion == DnDState.LEFT || dndRegion == DnDState.LEFT_OUT
			|| dndRegion == DnDState.TOP || dndRegion == DnDState.TOP_OUT)
		{
			dividerLocation = 0.4;
		} else {
			dividerLocation = 0.6;
		}
		
		
		if(updatedRootPane) {
			setDividerLocation(rootPane, dividerLocation);
		} else {
			setDividerLocation(newSplitPane,dividerLocation);
		}

		
		
		//update new space dispatching
		updateSplitPanesResizeWeight();
		
		// add toolbar to main toolbar container if necessary
		if(source.hasToolbar()) {
			//ToolbarContainer mainContainer = ((GuiManagerW) app.getGuiManager()).getToolbarPanel();
		//	mainContainer.addToolbar(source.getToolbar());
		//	mainContainer.updateToolbarPanel();
		}

		// has to be called *after* the toolbar was added to the container
		setFocusedPanel(source);
		
		unmarkAlonePanels();
		markAlonePanel();

		// Manually dispatch a resize event as the size of the 
		// euclidian view isn't updated all the time.
		// TODO What does the resize do which will update the component ?!
		//app.repaintEuclidianViews(rootPane);
		rootPane.onResize();
		
	 }
	
	private void setDividerLocation(DockSplitPaneW splitPane,
	        double dividerLocation) {
		final double dividerLoc = dividerLocation;
		final DockSplitPaneW sp = splitPane;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				sp.setDividerLocation(dividerLoc);
			}
		});
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				app.getAppFrame().onResize();
			}
		});
	}
	
	/**
	 * Show a DockPanel identified by its ID.
	 * 
	 * @param viewId
	 */
	public void show(int viewId) {
		show(getPanel(viewId));
	}
	
	/**
	 * Show a DockPanel where it was displayed the last time - either in the main window
	 * or in a separate frame. 
	 * 
	 * The location of the DockPanel in the main window is given by the definition string
	 * stored in DockPanelInfo.getEmbeddedDef().
	 * A definition string can be read like a list of directions, where numbers
	 * represents the four directions we can go:
	 * 
	 * 0: Top
	 * 1: Right
	 * 2: Bottom
	 * 3: Left 
	 * 
	 * A definition string like "0,3,2" is read by the program this way:
	 * - Go to the top (=0) container of the root pane.
	 * - Go to the container at the left (=3) of the current container.
	 * - Insert the DockPanel at the bottom (=2) of the current container.
	 * 
	 * Note that the program differs between the top & left and bottom & right
	 * position while the DockSplitPane just differs between a left and right
	 * component and the orientation of the split pane.
	 * 
	 * As the layout of the panels is changed frequently and may be completely
	 * different if the DockPanel is inserted again, the algorithm ignores all
	 * directions which are not existing anymore in order to get the best possible
	 * result.
	 * Using the example from above, the second direction ("3") may be
	 * ignored if the top container of the root pane isn't divided anymore or the
	 * orientation of the container was changed. The algorithm will continue with 
	 * "2" and will insert the DockPanel at the bottom of the top container 
	 * of the root pane.
	 * 
	 * @param panel 
	 */
	public void show(DockPanelW panel) {
		
		panel.setVisible(true);
		panel.setHidden(false);
		// undo maximized state if another dock panel is to be shown
		if(isMaximized)
			undoMaximize(false);
		
		// TODO causes any problems?
		app.getGuiManager().attachView(panel.getViewId());
		
		if(panel.isOpenInFrame()) {
			//panel.createFrame();
		} else {
			// Transform the definition into an array of integers
			String[] def = panel.getEmbeddedDef().split(",");
			int[] locations = new int[def.length];
			
			for(int i = 0; i < def.length; ++i) {
				if(def[i].length() == 0) {
					def[i] = "1";
				}
				
				locations[i] = Integer.parseInt(def[i]);
				
				if(locations[i] > 3 || locations[i] < 0)
					locations[i] = 3; // left as default direction
			}
			
			// We insert this panel at the left by default
			if(locations.length == 0) 
				locations = new int[] { 3 };
			
			DockSplitPaneW currentPane = rootPane;
			int secondLastPos = -1;
			
			// Get the location of our new DockPanel (ignore last entry)
			for(int i = 0; i < locations.length - 1; ++i) {			
				// The orientation of the current pane does not match the stored orientation, skip this
				if(currentPane.getOrientation() == DockSplitPaneW.HORIZONTAL_SPLIT && (locations[i] == 0 || locations[i] == 2)) {
					continue;
				} else if(currentPane.getOrientation() == DockSplitPaneW.VERTICAL_SPLIT && (locations[i] == 1 || locations[i] == 3)) {
					continue;
				}
				
				Widget component;
				
				if(locations[i] == 0 || locations[i] == 3)
					component = currentPane.getLeftComponent();
				else
					component = currentPane.getRightComponent();
				
				if(!(component instanceof DockSplitPaneW)) {
					secondLastPos = locations[i];
					break;
				} else {
					currentPane = (DockSplitPaneW)component;
				}
			}
	
			int size = panel.getEmbeddedSize();
			int lastPos = locations[locations.length - 1];
			
			DockSplitPaneW newSplitPane = new DockSplitPaneW();
			
			
			if(lastPos == 0 || lastPos == 2) {
				newSplitPane.setOrientation(DockSplitPaneW.VERTICAL_SPLIT);
			} else {
				newSplitPane.setOrientation(DockSplitPaneW.HORIZONTAL_SPLIT);
			}
			
			// the size (height / width depending upon lastPos) of the parent element,
			// this value is necessary to prevent panels which completely hide
			// their opposite element
			
			// the component opposite to the current component
			Widget opposite;
			int oppositeWidth = 0;
			int oppositeHeight = 0;

			//====================
			// TODO temporary fix:
			//newSplitPane.setDividerLocation(size);
			
			
			if(secondLastPos == -1) {
				opposite = rootPane;
				oppositeWidth = opposite.getOffsetWidth();
				oppositeHeight = opposite.getOffsetHeight();
				rootPane = newSplitPane;

				// in root pane, the opposite may be null
				if(lastPos == 0 || lastPos == 3) {
					if(((DockSplitPaneW)opposite).getLeftComponent() == null) {
						opposite = ((DockSplitPaneW)opposite).getRightComponent();
					}
				} else {
					if(((DockSplitPaneW)opposite).getRightComponent() == null) {
						opposite = ((DockSplitPaneW)opposite).getLeftComponent();
					}
				}
			} else {
				if(secondLastPos == 0 || secondLastPos == 3) {
					opposite = currentPane.getLeftComponent();
				} else {
					opposite = currentPane.getRightComponent();
				}

				// in root pane, the opposite may be null
				if(opposite == null) {
					opposite = currentPane.getOpposite(opposite);
					oppositeWidth = opposite.getOffsetWidth();
					oppositeHeight = opposite.getOffsetHeight();
					rootPane = newSplitPane;
				} else if(opposite.getParent() == rootPane && rootPane.getOpposite(opposite) == null) {
					oppositeWidth = opposite.getOffsetWidth();
					oppositeHeight = opposite.getOffsetHeight();
					rootPane = newSplitPane;
				} else {
					oppositeWidth = opposite.getOffsetWidth();
					oppositeHeight = opposite.getOffsetHeight();
					currentPane.replaceComponent(opposite, newSplitPane);
				}
			}



			//App.debug("\n"+((DockComponent) opposite).toString("opposite"));
			//save divider locations to prevent not visible views
			if (opposite != null) {
				((DockComponent) opposite).saveDividerLocation();
			}
			
			if(lastPos == 0 || lastPos == 3) {
				newSplitPane.setLeftComponent((Widget) panel);
				newSplitPane.setRightComponent(opposite);
			} else {
				newSplitPane.setLeftComponent(opposite);
				newSplitPane.setRightComponent((Widget) panel);
			}
			
			if(!app.isIniting())
				app.updateCenterPanel(true);
			
			//check new split pane size regarding orientation
			int newSplitPaneSize;
			if(newSplitPane.getOrientation() == DockSplitPaneW.HORIZONTAL_SPLIT) {
				newSplitPaneSize=//newSplitPane.getOffsetWidth();
					oppositeWidth;
			} else {
				newSplitPaneSize=//newSplitPane.getOffsetHeight();
					oppositeHeight;
			}
			//check if panel size is not too large
			if (size+DockComponent.MIN_SIZE>newSplitPaneSize)
				size = newSplitPaneSize/2;
			//set the divider location
			
			//----------------
			// TODO turned this off for now ... need to fix for web
			//---------------
			if(lastPos == 0 || lastPos == 3) {
				newSplitPane.setDividerLocation(size);
			} else {
				newSplitPane.setDividerLocation(newSplitPaneSize - size);
			}
			

			//App.debug("\nnewSplitPaneSize = "+newSplitPaneSize+"\nsize = "+size);
			//App.debug("\n======\n"+((DockComponent) opposite).toString(""));
			//re dispatch divider locations to prevent not visible views
			if (opposite != null) {
				((DockComponent) opposite).updateDividerLocation(newSplitPaneSize-size,newSplitPane.getOrientation());
			}
		}
		
		panel.updatePanel();
		
		//update dispatching of new space
		updateSplitPanesResizeWeight();
		
		
		// add toolbar to main toolbar container if necessary, *has* to be called after
		// DockPanel::updatePanel() as the toolbar is initialized there
		if(!panel.isOpenInFrame()) {
		// original
		//	ToolbarContainer mainContainer = ((GuiManagerD) app.getGuiManager()).getToolbarPanel();
		//	mainContainer.addToolbar(panel.getToolbar());
		//	mainContainer.updateToolbarPanel();

			app.setShowToolBar(true, true);
			app.getGuiManager().setActiveToolbarId(panel.getViewId());
		}

		// has to be called *after* the toolbar was added to the container
		setFocusedPanel(panel);
		
		unmarkAlonePanels();
		markAlonePanel();
		
		for (ShowDockPanelListener l:showDockPanelListener){
			l.showDockPanel(panel);
		}
	}
	
	/**
	 * Hide a dock panel identified by the view ID.
	 * 
	 * @param viewId
	 * @return true if succeeded to hide the panel
	 */
	public boolean hide(int viewId, boolean isPermanent) {
		return hide(getPanel(viewId), isPermanent);
	}
	
	/**
	 * Hide a dock panel permanently.
	 * 
	 * @param panel
	 * @return true if succeeded to hide the panel
	 */
	public boolean hide(DockPanelW panel) {
		return hide(panel, true);
	}

	/**
	 * close the dock panel
	 * @param viewId id of the dock panel
	 * @param isPermanent says if the close is permanent
	 */
	public void closePanel(int viewId, boolean isPermanent){
		closePanel(getPanel(viewId), isPermanent);
	}

	/**
	 * close the dock panel
	 * @param panel dock panel
	 * @param isPermanent says if the close is permanent
	 */
	public void closePanel(DockPanelW panel, boolean isPermanent){
		if (hide(panel, isPermanent)){
			app.updateMenubar();

			if(getFocusedPanel() == panel) {
				setFocusedPanel(null);
			}
		}
	}
	

	/**
	 * 
	 * @return true if the layout contains less than two panels
	 */
	private boolean containsLessThanTwoPanels(){
		return (rootPane==null) || (rootPane.getLeftComponent()==null) || (rootPane.getRightComponent()==null);
	}

	
	/**
	 * Hide a dock panel.
	 * 
	 * @param panel
	 * @param isPermanent If this change is permanent.
	 * @return true if it succeeded to hide the panel
	 */
	public boolean hide(DockPanelW panel, boolean isPermanent) {
		if(!panel.isVisible()) {
			// some views (especially CAS) will close so slowly that the user is able
			// to issue another "close" call, therefore we quit quietly
			return false;
		}
		
		//if panel is open in frame, check if it's not the last one
		if (!panel.isOpenInFrame() && containsLessThanTwoPanels())
			return false;
		
		panel.setHidden(!isPermanent);
		
		panel.setVisible(false);
		setFocusedPanel(null);
		
		if(isPermanent) {
			app.getGuiManager().detachView(panel.getViewId());
		}
		
		if(panel.isOpenInFrame()) {
			// TODO: deal with remove
		//	panel.removeFrame();
			panel.setOpenInFrame(true); // open in frame the next time
		} else {
			DockSplitPaneW parent = panel.getParentSplitPane();
			
			// Save settings
			if(parent.getOrientation() == DockSplitPaneW.HORIZONTAL_SPLIT) {
				panel.setEmbeddedSize(panel.getOffsetWidth());
			} else {
				panel.setEmbeddedSize(panel.getOffsetHeight());
			}
			
			panel.setEmbeddedDef(panel.calculateEmbeddedDef());
			panel.setOpenInFrame(false);
			
			Widget opposite = parent.getOpposite(panel);

			//save divider location and size (if DockSplitPane)
			if (opposite!=null){
				((DockComponent) opposite).saveDividerLocation();
			}
			int orientation = parent.getOrientation();
			int size = 0;
			if (orientation==DockSplitPaneW.VERTICAL_SPLIT)
				size = parent.getOffsetHeight();
			else
				size = parent.getOffsetWidth();


			if(parent == rootPane) {
				if(opposite instanceof DockSplitPaneW) {
					rootPane = (DockSplitPaneW) opposite;
				} else {
					parent.replaceComponent((Widget) panel, null);
				}
				app.updateCenterPanel(true);
			} else {
				DockSplitPaneW grandParent = (DockSplitPaneW)parent.getParent();
				int dividerLoc = grandParent.getDividerLocation();
				grandParent.replaceComponent(parent, opposite);
				grandParent.setDividerLocation(dividerLoc);
			}
			
			//re dispatch divider location
			if (opposite!=null)
				((DockComponent) opposite).updateDividerLocation(size,orientation);
			
		// TODO: resize here?	
		//	if(isPermanent) {
		//		app.validateComponent();
		//	}
			
			markAlonePanel();

			if(panel.hasToolbar()) {
			//	ToolbarContainer mainContainer = ((GuiManagerD) app.getGuiManager()).getToolbarPanel();
			//	mainContainer.removeToolbar(panel.getToolbar());
			//	mainContainer.updateToolbarPanel();
			}
			
			app.updateToolBar();
		}
		
		return true;
	}
	
	/**
	 * Listen to mouse clicks and determine if the view focus changed. Just
	 * used in case the full focus system is active. 
	 * 
	 * Euclidian views always inform the dock manager about focus changes using
	 * their own mouse click events (see EuclidianController:mouseClicked()) because
	 *   1) This AWT event cannot be used for unsigned applets but we need euclidian
	 *      view focus changes for new object placement
	 *   2) An own mouse listener may be called *after* the euclidian controller
	 *      mouse listener was called, therefore new objects may be created in the wrong
	 *      euclidian view as the focus was not changed at the time of object creation. 
	 */
	/*
	public void eventDispatched(AWTEvent event) {
		// we also get notified about other mouse events, but we want to ignore them
		if(event.getID() != MouseEvent.MOUSE_CLICKED) {
//			System.out.println(event);
			return;
		}
		
		// determine ancestor element of the event source which is of type
		// dock panel
		Component source = (Component)event.getSource();
		//System.out.println("    source: " + source);
		DockPanel dp = (DockPanel)SwingUtilities.getAncestorOfClass(DockPanel.class, source);
		
		// ignore this if we didn't hit a dock panel at all or if we hit the euclidian
		// view, they are always handled by their own mouse event (see doc comment above)
		if(dp != null && !(dp.getComponent() instanceof EuclidianViewJPanel)) {
			//updates the properties view only if source is not the euclidian style bar
			boolean updatePropertiesView = true;
			if (source instanceof EuclidianStyleBar)
				updatePropertiesView=false;
			else if (SwingUtilities.getAncestorOfClass(EuclidianStyleBar.class, source)!=null)
				updatePropertiesView=false;
			setFocusedPanel(dp, updatePropertiesView);
		}
	}
	*/
	
	
	/**
	 * Change the focused panel to "panel".
	 * 
	 * @param panel panel
	 */
	public void setFocusedPanel(DockPanel panel) {
		setFocusedPanel(panel, true);
	}
	
	/**
	 * Change the focused panel to "panel". TODO: partly unimplemented
	 * 
	 * @param panel panel
	 * @param updatePropertiesView update the properties view
	 */
	public void setFocusedPanel(DockPanel panel, boolean updatePropertiesView) {
		if(focusedDockPanel == panel) {
			return;
		} 
		
		// euclidian focus	
		
		// in case there is no focused panel there is also no focused euclidian
		// dock panel
		if(panel == null) {
			if(focusedEuclidianDockPanel != null) {
				focusedEuclidianDockPanel.setEuclidianFocus(false);
				// if (focusedEuclidianDockPanel != focusedDockPanel)
				//	focusedEuclidianDockPanel.setTitleLabelFocus();
				focusedEuclidianDockPanel = null;
			}
		}else{
			if(panel instanceof EuclidianDockPanelWAbstract && focusedEuclidianDockPanel != panel) {
				// remove focus from previously focused dock panel
				if(focusedEuclidianDockPanel != null) {
					focusedEuclidianDockPanel.setEuclidianFocus(false);
					// if (focusedEuclidianDockPanel != focusedDockPanel)
					//	focusedEuclidianDockPanel.setTitleLabelFocus();
				}


				// if a panel has focus and that panel is a euclidian dock panel
				// change the focused euclidian dock panel to that panel				
				focusedEuclidianDockPanel = (EuclidianDockPanelWAbstract) panel;
				focusedEuclidianDockPanel.setEuclidianFocus(true);


				// (panels which are not euclidian dock panels do not change the focused
				// euclidian dock panel (ie the old is kept))

			}
		}
		

		// remove focus from previously focused dock panel
		if(focusedDockPanel != null) {
			focusedDockPanel.setFocus(false, false);
		}
		
		focusedDockPanel = (DockPanelW)panel;
		
		if(focusedDockPanel != null) {
			focusedDockPanel.setFocus(true, updatePropertiesView);
		}
		
		app.getGuiManager().updateMenubarSelection();
		
		//if(focusedDockPanel != null && panel.isInFrame()){
		//	panel.getFrame().toFront();
		//}
		
	}


	/**
	 * Changes the focused panel to the dock panel with ID viewId. 
	 * Uses {@link DockManagerW#setFocusedPanel(DockPanel)} internally 
	 * but adds some validation checks. 
	 * 
	 * @param viewId 
	 * @return true if focus was changed, false if the requested dock panel does 
	 * 			not exist or is invisible at the moment  
	 */
	public boolean setFocusedPanel(int viewId) {
		DockPanelW dockPanel = getPanel(viewId);
		
		if(dockPanel != null && dockPanel.isVisible()) {
			setFocusedPanel(dockPanel);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return The dock panel which has focus at the moment.
	 */
	public DockPanel getFocusedPanel() {
		return focusedDockPanel;
	}
	
	/**
	 * @return The viewId of the dock panel which has focus at the moment.
	 */
	public int getFocusedViewId() {
		
		if (focusedDockPanel == null)
			return -1;

		return focusedDockPanel.getViewId();
	}
	
	
	
	/**
	 * @return The dock euclidian panel which had focus the last.
	 */
	public EuclidianDockPanelWAbstract getFocusedEuclidianPanel() {
		return focusedEuclidianDockPanel;
	}	
	
	/**
	 * Moves the focus between visible panels. Just the register order is taken
	 * into consideration here, so the focus changing order does not depend upon
	 * the visual order.
	 * 
	 * @param forward If the next or previous panel should be focused,
	 * 		calling this method once with both possibilities will cancel out 
	 * 		the effect so to say.
	 */
	public void moveFocus(boolean forward) {
		if(focusedDockPanel == null)
			return;
		
		// to follow the DRY principle we'll use a single iterator for both
		// forward and backward iteration
		Iterator<DockPanelW> it = getForwardBackwardIterator(forward);
		
		// if the focused dock panel was found already
		boolean foundFocused = false;
		boolean changedFocus = false;
		
		while(it.hasNext()) {
			DockPanelW panel = it.next();
			
			// all we do for now on just takes visible dock panels
			// into consideration
			if(panel.isVisible()) {
				// we already found the focused dock panel, so this is the
				// new panel to focus
				if(foundFocused) {
					setFocusedPanel(panel);
					changedFocus = true;
					break;
				}
				
				// is this the focused dock panel?
				else if(panel == focusedDockPanel) {
					foundFocused = true;
				} 
				
				else {
					// we have not reached the focused dock panel, therefore
					// we do nothing
				}
			}
		}
		
		// if just invisible dock panels (or none) followed the focused dock panel we have
		// not changed the focus so far, so we go through our list from the beginning
		if(!changedFocus) {
			// recreate our iterator as we can't reset it
			it = getForwardBackwardIterator(forward);
			
			
			while(it.hasNext()) {
				DockPanelW panel = it.next();
				
				if(panel.isVisible()) {
					// quit if we reached the focused panel until we found another
					// visible panel
					if(panel == focusedDockPanel) {
						break;
					}
					
					// change panel
					else {
						setFocusedPanel(panel);
						changedFocus = true;
						break;
					}
				}
			}
		}
	}
	
	/**
	 * If just one panel is visible in the main frame, mark him as 'alone'.
	 */
	private void markAlonePanel() {
		// determine if such a panel exists
		DockPanelW singlePanel = null;
		
		if(rootPane.getRightComponent() == null) {
			Widget leftComponent = rootPane.getLeftComponent();
			
			if(leftComponent != null && leftComponent instanceof DockPanel) {
				singlePanel = (DockPanelW)leftComponent;
			}
		}
		
		if(rootPane.getLeftComponent() == null) {
			Widget rightComponent = rootPane.getRightComponent();
			
			if(rightComponent != null && rightComponent instanceof DockPanel) {
				singlePanel = (DockPanelW)rightComponent;
			}
		}
		
		// mark the found panel as 'alone'
		if(singlePanel != null) {
			singlePanel.setAlone(true);
		}
	}
	
	/**
	 * Remove marks from any panel, that it might be alone.
	 */
	private void unmarkAlonePanels() {
		for(DockPanelW panel : dockPanels) {
			if(panel.isAlone()) {
				panel.setAlone(false);
			}
		}
	}
	
	/**
	 * Helper method to create an iterator which either iterates forward or
	 * backward through the dock panel list.
	 * 
	 * @param forward	If the returned iterator should return forward or backward
	 * @return 			The iterator
	 */
	private Iterator<DockPanelW> getForwardBackwardIterator(boolean forward) {
		if(forward) {	
			return dockPanels.iterator();
		} else {
			final ListIterator<DockPanelW> original = dockPanels.listIterator(dockPanels.size());
			
			// we create our own iterator which iterates through our list in
			// reversed order
			return new Iterator<DockPanelW>() {
				public void remove() {
					original.remove();
				}
				
				public DockPanelW next() {
					return original.previous();
				}
				
				public boolean hasNext() {
					return original.hasPrevious();
				}
			};
		}
	}
	
	/**
	 * Update the labels of all DockPanels.
	 */
	public void setLabels() {
		for(DockPanelW panel : dockPanels) {
			panel.updateLabels();
		}
		
		for(DockPanelW panel : dockPanels) {
			panel.buildToolbarGui();
		}
	}
	
	/**
	 * Update the glass pane
	 */
	public void updateGlassPane() {
	//	if(!app.isApplet() && glassPane.getParent() != null) {
	//		app.setGlassPane(glassPane);
	//	}
	}
	
	/**
	 * Update the titles of the frames as they contain the file name of the current
	 * document.
	 */
	public void updateTitles() {
		for(DockPanel panel : dockPanels) {
			//panel.updateTitle();
		}
	}
	
	/**
	 * Update all DockPanels.
	 * 
	 * This is required if the user changed whether the title bar should be displayed or not.
	 * 
	 * @see #setLabels()
	 */
	public void updatePanels() {
		for(DockPanelW panel : dockPanels) {
			panel.updatePanel();
		}
	}
	
	/**
	 * Update the toolbars of all dock panels.
	 */
	public void updateToolbars() {
		for(DockPanelW panel : dockPanels) {
			panel.updateToolbar();
		}
	}
	
	/**
	 * Change the toolbar mode for all toolbars in external frames.
	 * 
	 * @param mode
	 */
	public void setToolbarMode(int mode) {
		for(DockPanelW panel : dockPanels) {
		//	panel.setToolbarMode(mode);
		}
	}
	
	/**
	 * Update the fonts in all dock panels.
	 */
	public void updateFonts() {
		for(DockPanelW panel : dockPanels) {
			panel.updateFonts();
		}
		
		for(DockPanelW panel : dockPanels) {
			panel.buildToolbarGui();
		}
	}
	
	/**
	 * Scale the split panes based upon the given X and Y scale. This is used to keep relative
	 * dimensions of the split panes if the user is switching between applet and frame mode. 
	 * 
	 * @param scaleX
	 * @param scaleY
	 */
	public void scale(float scaleX, float scaleY) {
		scale(scaleX, scaleY, rootPane);
	}
	
	private void scale(float scaleX, float scaleY, DockSplitPaneW splitPane) {
		splitPane.setDividerLocation((int)(splitPane.getDividerLocation() * (splitPane.getOrientation() == DockSplitPaneW.VERTICAL_SPLIT ? scaleX : scaleY)));
		
		if(splitPane.getLeftComponent() != null && splitPane.getLeftComponent() instanceof DockSplitPaneW) {
			scale(scaleX, scaleY, (DockSplitPaneW)splitPane.getLeftComponent());
		}
		
		if(splitPane.getRightComponent() != null && splitPane.getRightComponent() instanceof DockSplitPaneW) {
			scale(scaleX, scaleY, (DockSplitPaneW)splitPane.getRightComponent());
		}
	}
	
	/**
	 * @return GeoGebraLayout instance
	 */
	public LayoutW getLayout() {
		return layout;
	}
	
	/**
	 * @return The glass pane which is used to draw the preview rectangle if the user dragged
	 * a DockPanel.
	 */
	//public DockGlassPane getGlassPane() {
	//	return glassPane;
	// }
	
	/**
	 * 
	 * @param viewId constant VIEW_EUCLIDIAN, VIEW_ALGEBRA, or VIEW_FOR_PLANE
	 * @param plane plane when for euclidian view for plane
	 * @return a DockPanel
	 */
	public DockPanelW getPanel(DockPanelData dpData)
	{
		if (dpData.getPlane()==null) //standard case
			return getPanel(dpData.getViewId());
		
		//euclidian view for plane case	
		//DockPanelW panel = app.createEuclidianDockPanelForPlane(dpData.getViewId(), dpData.getPlane());
		//if (panel==null){
		//	App.error("panel==null");
		//	return null;
		//}
		
		//set the view id of the dock panel data for apply perspective
		//dpData.setViewId(panel.getViewId());
		//return panel;
		return null;
		
	}
	
	/**
	 * Returns a specific DockPanel.
	 * 
	 * Use the constants VIEW_EUCLIDIAN, VIEW_ALGEBRA etc. as viewId.
	 * 
	 * @param viewId
	 * @return The panel associated to the viewId
	 */
	public DockPanelW getPanel(int viewId)
	{
		DockPanelW panel = null;
		for(DockPanelW dockPanel : dockPanels) {
			if(dockPanel.getViewId() == viewId) {
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
		return (DockPanelW[])dockPanels.toArray(new DockPanelW[0]);
	}
	
	/**
	 * @return The root split pane which contains all other elements like DockPanels or
	 * DockSplitPanes.
	 */
	public DockSplitPaneW getRoot() {
		return rootPane;
	}
	
	/**
	 * @return True if all focus may change between all views, false if just
	 * the euclidian views are affected by this. 
	 */
	public boolean hasFullFocusSystem() {
		return hasFullFocusSystem;
	}
	
	/**
	 * Return a string which can be used to debug the view tree.
	 * @param depth
	 * @param pane
	 * @return
	 */
	private String getDebugTree(int depth, DockSplitPaneW pane) {
		StringBuilder strBuffer = new StringBuilder();
		
		Widget leftComponent = pane.getLeftComponent();
		Widget rightComponent = pane.getRightComponent();
		
		strBuffer.append(strRepeat("-", depth) + "[left]");
		
		if(leftComponent == null)
			strBuffer.append("null" + "\n");
		else if(leftComponent instanceof DockSplitPaneW)
			strBuffer.append("\n" + getDebugTree(depth+1, (DockSplitPaneW)leftComponent));
		else
			strBuffer.append(leftComponent.toString() + "\n");
		
		strBuffer.append(strRepeat("-", depth) + "[right]");
		
		if(rightComponent == null)
			strBuffer.append("null" + "\n");
		else if(rightComponent instanceof DockSplitPaneW)
			strBuffer.append("\n" + getDebugTree(depth+1, (DockSplitPaneW)rightComponent));
		else
			strBuffer.append(rightComponent.toString() + "\n");
		
		return strBuffer.toString();
	}
	
	private static String strRepeat(String str, int times)	{
		StringBuilder strBuffer = new StringBuilder();
		for(int i = 0; i < times; ++i)
			strBuffer.append(str);
		return strBuffer.toString();
	}
	
	
	// ===================================================================
	//
	// Temporary code for developing a maximize feature
	// G. Sturr 4/4/2012
	//
	// ===================================================================
	
	/**
	 * Perspective that stores the configuration just before a dock panel is
	 * maximized. 
	 */
	private Perspective restorePerspective;
	
	/**
	 * Flag to determine if the layout has been maximized, i.e. the layout
	 * temporarily displays a single dock panel
	 */
	private boolean isMaximized = false;
	
	
	/**
	 * @return true if the dock panel layout has been maximized
	 */
	public boolean isMaximized(){
		return isMaximized;
	}
	
	/**
	 * Undo a maximized layout.
	 * 
	 * @param doRestore
	 *            if true, then attempt to restore the previous state
	 */
	public void undoMaximize(boolean doRestore) {
		if (!isMaximized)
			return;
		
		isMaximized = false;
		if (doRestore) {
			
			if (restorePerspective != null){
				layout.applyPerspective(restorePerspective);
			}
			restorePerspective = null;
		}
	}


	/**
	 * Maximizes the layout.
	 * 
	 * @param dp
	 *            the dock panel to maximize
	 */
	public void maximize(DockPanelW dp) {
		/*
		restorePerspective = layout.createPerspective("tmp");
		for (int i = 0; i < getPanels().length; i++) {
			if (getPanels()[i] != dp) {
				hide(getPanels()[i], false);
			}
		}
		isMaximized = true;
		//app.updateCenterPanel(true);
		app.updateMenubar();
		*/
	}
	
	public void addShowDockPanelListener(ShowDockPanelListener l){
		showDockPanelListener.add(l);
	}
	
	
}
