package geogebra.gui.layout;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.InputHandler;
import geogebra.common.gui.Layout;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.dialog.InputDialogD;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * Manage layout related stuff.
 * 
 * @author Florian Sonner
 */
public class LayoutD extends Layout implements SettingListener {	
	private boolean isInitialized = false;
	
	private AppD app;
	private DockManager dockManager;
	
	/**
	 * {@link #initialize(AppD)} has to be called once in order to use this class.
	 */
	public LayoutD() {
		initializeDefaultPerspectives(true, false);
		
		this.perspectives = new ArrayList<Perspective>(defaultPerspectives.length);
	}
	
	/**
	 * Initialize the layout component.
	 * 
	 * @param app
	 */
	public void initialize(AppD app) {
		if(isInitialized)
			return;
		
		isInitialized = true;
		
		this.app = app;
		this.settings = app.getSettings().getLayout();
		this.settings.addListener(this);
		this.dockManager = new DockManager(this);
	}
	
	/**
	 * Add a new dock panel to the list of known panels.
	 * 
	 * Attention: This method has to be called as early as possible in the application
	 * life cycle (e.g. before loading a file, before constructing the ViewMenu). 
	 * 
	 * @param dockPanel
	 */
	public void registerPanel(DockPanel dockPanel) {
		dockManager.registerPanel(dockPanel);
	}
	
	/**
	 * Apply a new perspective.
	 * 
	 * TODO consider applet parameters
	 * 
	 * @param perspective
	 */
	public void applyPerspective(Perspective perspective) {
		// ignore axes & grid settings for the document perspective
		if(!perspective.getId().equals("tmp")) {
			EuclidianViewND ev = app.getActiveEuclidianView();

			if (app.getEuclidianView1() == ev)
				app.getSettings().getEuclidian(1).setShowAxes(perspective.getShowAxes(), perspective.getShowAxes());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.setShowAxes(perspective.getShowAxes(), false);
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2).setShowAxes(perspective.getShowAxes(), perspective.getShowAxes());
			else
				ev.setShowAxes(perspective.getShowAxes(), false);

			if (app.getEuclidianView1() == ev)
				app.getSettings().getEuclidian(1).showGrid(perspective.getShowGrid());
			else if (!app.hasEuclidianView2EitherShowingOrNot())
				ev.showGrid(perspective.getShowGrid());
			else if (app.getEuclidianView2() == ev)
				app.getSettings().getEuclidian(2).showGrid(perspective.getShowGrid());
			else
				ev.showGrid(perspective.getShowGrid());

			ev.setLockedAxesRatio(perspective.isUnitAxesRatio()?1.0:null);
		}
		
		app.getGuiManagerD().setToolBarDefinition(perspective.getToolbarDefinition());
		
		app.setShowToolBarNoUpdate(perspective.getShowToolBar());
		app.setShowAlgebraInput(perspective.getShowInputPanel(), false);
		app.setShowInputTop(perspective.getShowInputPanelOnTop(), false);
		
		// change the dock panel layout
		dockManager.applyPerspective(perspective.getSplitPaneData(), perspective.getDockPanelData());
		
		if(!app.isIniting()) {
			app.updateToolBar();
			app.updateMenubar();
			app.updateContentPane();
		}
	}
	
	/**
	 * Apply a new perspective using its id. 
	 * 
	 * This is a wrapper for #applyPerspective(Perspective) to simplify the loading of default
	 * perspectives by name. 
	 * 
	 * @param id The ID of the perspective. For default perspectives the hard-coded ID is used, ie
	 * 			 the translation key, for all other perspectives the ID chosen by the user is
	 * 			 used.
	 * @throws IllegalArgumentException If no perspective with the given name could be found.
	 */
	public void applyPerspective(String id) throws IllegalArgumentException {
		Perspective perspective = getPerspective(id);
		
		if(perspective != null) {
			applyPerspective(perspective);
		} else {
			throw new IllegalArgumentException("Could not find perspective with the given name.");
		}		
	}
	
	/**
	 * Create a perspective for the current layout.
	 * 
	 * @param id
	 * @return a perspective for the current layout.
	 */
	public Perspective createPerspective(String id) {
		if(app == null || dockManager.getRoot() == null)
			return null;
		
		// return the default perspective in case we're creating new preferences of
		// a virgin application.		
		EuclidianView ev = app.getEuclidianView1();
		Perspective perspective = new Perspective(id);

		// get the information about the split panes
		DockSplitPane.TreeReader spTreeReader = new DockSplitPane.TreeReader(app);
		perspective.setSplitPaneData(spTreeReader.getInfo(dockManager.getRoot()));

		// get the information about the dock panels
		DockPanel[] panels = dockManager.getPanels();
		DockPanelData[] dockPanelInfo = new DockPanelData[panels.length];

		for (int i = 0; i < panels.length; ++i) {
			// just the width of the panels isn't updated every time the panel
			// is updated, so we have to take care of this by ourself
			if (!panels[i].isOpenInFrame() && panels[i].isVisible()) {
				DockSplitPane parent = panels[i].getParentSplitPane();
				if (parent.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
					panels[i].setEmbeddedSize(panels[i].getWidth());
				} else {
					panels[i].setEmbeddedSize(panels[i].getHeight());
				}
				panels[i].setEmbeddedDef(panels[i].calculateEmbeddedDef());
			}
			dockPanelInfo[i] = panels[i].createInfo();
		}

		// Sort the dock panels as the entries with the smallest amount of
		// definition should
		// be read first by the loading algorithm.
		Arrays.sort(dockPanelInfo, new Comparator<DockPanelData>() {
			public int compare(DockPanelData o1, DockPanelData o2) {
				int diff = o2.getEmbeddedDef().length()
						- o1.getEmbeddedDef().length();
				return diff;
			}
		});
		
		perspective.setDockPanelData(dockPanelInfo);

		perspective.setToolbarDefinition(app.getGuiManagerD().getToolbarDefinition());
		perspective.setShowToolBar(app.showToolBar());
		perspective.setShowAxes(ev.getShowXaxis() && ev.getShowYaxis());
		perspective.setShowGrid(ev.getShowGrid());
		perspective.setShowInputPanel(app.showAlgebraInput());
		perspective.setShowInputPanelCommands(app.showInputHelpToggle());
		perspective.setShowInputPanelOnTop(app.showInputTop());

		return perspective;
	}
	
	/**
	 * Get all current perspectives as array.
	 * 
	 * @return all current perspectives as array.
	 */
	public Perspective[] getPerspectives() {
		Perspective[] array = new Perspective[perspectives.size()];
		return perspectives.toArray(array);
	}

	/**
	 * @param index
	 * @return perspective at given index
	 */
	public Perspective getPerspective(int index) {
		if(index >= perspectives.size())
			throw new IndexOutOfBoundsException();
		
		return perspectives.get(index);
	}
	
	/**
	 * @param id name of the perspective
	 * @return perspective with 'id' as name or null 
	 */
	public Perspective getPerspective(String id) {
		for(int i = 0; i < defaultPerspectives.length; ++i) {
			if(id.equals(defaultPerspectives[i].getId())) {
				return defaultPerspectives[i];
			}
		}
		
		for(Perspective perspective : perspectives) {
			if(id.equals(perspective.getId())) {
				return perspective;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a new perspective to the list of available perspectives.
	 * 
	 * @param perspective
	 */
	public void addPerspective(Perspective perspective) {
		perspectives.add(perspective);
	}
	
	/**
	 * Remove a perspective identified by the object.
	 * 
	 * @param perspective
	 */
	public void removePerspective(Perspective perspective) {
		if(perspectives.contains(perspective)) {
			perspectives.remove(perspective);
		}
	}
	
	/**
	 * Remove a perspective identified by the index.
	 * 
	 * @param index
	 */
	public void removePerspective(int index) {
		if(index >= 0 && index < perspectives.size()) {
			perspectives.remove(index);
		} else {
			App.debug("Invalid perspective index: " + index);
		}
	}
	
	/**
	 * Return the layout as XML.
	 * @param sb 
	 * 
	 * @param asPreference If the collected data is used for the preferences
	 */
	public void getXml(StringBuilder sb, boolean asPreference) {
		/**
		 * Create a temporary perspective which is used to store the layout
		 * of the document at the moment. This perspective isn't accessible
		 * through the menu and will be removed as soon as the document was 
		 * saved with another perspective. 
		 */ 
		Perspective tmpPerspective = createPerspective("tmp");

		sb.append("\t<perspectives>\n");
		
		// save the current perspective
		if(tmpPerspective != null)
			sb.append(tmpPerspective.getXml());
		
		// save all custom perspectives as well
		for(Perspective perspective : perspectives) {
			// skip old temporary perspectives
			if(perspective.getId().equals("tmp")) {
				continue;
			}
			
			sb.append(perspective.getXml());
		}
		
		sb.append("\t</perspectives>\n");

		/**
		 * Certain user elements should be just saved as preferences and not
		 * if a document is saved normally as they just depend on the
		 * preferences of the user.
		 */
		if(asPreference) {
			sb.append("\t<settings ignoreDocument=\"");
			sb.append(settings.isIgnoringDocumentLayout());
			sb.append("\" showTitleBar=\"");
			sb.append(settings.showTitleBar());
			sb.append("\" allowStyleBar=\"");
			sb.append(settings.isAllowingStyleBar());
			sb.append("\" />\n");
		}

	}

	/**
	 * Checks if the given component is in an external window. Used for key dispatching.
	 * 
	 * @param component
	 * @return whether the given component is in an external window. Used for key dispatching.
	 */
	public boolean inExternalWindow(Component component) {
		DockPanel[] panels = dockManager.getPanels();
		
		for(int i = 0; i < panels.length; ++i) {
			if(panels[i].isOpenInFrame()) {
				if(component == SwingUtilities.getRootPane(panels[i])) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @param viewId
	 * @return If just the view associated to viewId is visible
	 */
	public boolean isOnlyVisible(int viewId) {
		DockPanel[] panels = dockManager.getPanels();
		boolean foundView = false;
		
		for(int i = 0; i < panels.length; ++i) {
			// check if the view is visible at all
			if(panels[i].getViewId() == viewId) {
				foundView = true;
				
				if(!panels[i].isVisible()) {
					return false;
				}
			}
			
			// abort if any other view is visible
			else {
				if(panels[i].isVisible()) {
					return false;
				}
			}
		}
		
		// if we reach this point each other view is invisible, but
		// if the view wasn't found at all we return false as well
		return foundView;
	}
	
	/**
	 * Layout settings changed.
	 */
	public void settingsChanged(AbstractSettings abstractSettings) {
		dockManager.updatePanels();
	}
	
	/**
	 * @return The application object.
	 */
	public AppD getApplication() {
		return app;
	}
	
	/**
	 * @return The management class for the docking behavior.
	 */
	public DockManager getDockManager() {
		return dockManager;
	}

	public JComponent getRootComponent() {
		if(dockManager == null) {
			return null;
		}
		
		return dockManager.getRoot();
	}
	
	/**
	 * Show the prompt which is used to save the current perspective.
	 */
	public void showSaveDialog() {
		InputDialogD inputDialog = new InputDialogD(app, app.getPlain("PerspectiveName"), app.getMenu("SaveCurrentPerspective"), "", false, new SaveInputHandler(this));
		inputDialog.showSymbolTablePopup(false);
        inputDialog.setVisible(true);    
	}
	
	private class SaveInputHandler implements InputHandler {
		private LayoutD layout;
		
		public SaveInputHandler(LayoutD layout) {
			this.layout = layout;
		}
		
		public boolean processInput(String inputString) {
			// tmp is reserved for the default perspective 
			if(inputString.equals("tmp")) {
				return false;
			}
			
			// such a perspective already exists
			if(layout.getPerspective(inputString) != null) {
				return false;
			}
			
			layout.addPerspective(layout.createPerspective(inputString));
			layout.getApplication().updateMenubar();
			GeoGebraPreferencesD.getPref().saveXMLPreferences(app);
			
			return true;
		}
	}
	
	/**
	 * Show the dialog which is used to manage the custom defined perspectives.
	 */
	public void showManageDialog() {
		ManagePerspectivesDialog dialog = new ManagePerspectivesDialog(app, this);
		dialog.setVisible(true);
	}

	/**
	 * Dialog which is used to manage (delete) the custom perspectives.
	 * 
	 * @author Florian Sonner
	 * @version 2008-09-14
	 * 
	 * TODO More advanced functions (rename, etc.)
	 */
	private class ManagePerspectivesDialog extends JDialog implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private AppD app;
		private LayoutD layout;
		private JList list;
		private DefaultListModel listModel;
		private JButton cancelButton, removeButton;
		
		public ManagePerspectivesDialog(AppD app, LayoutD layout) {
			super(app.getFrame());
			
			this.app = app;
			this.layout = layout;
			
			setModal(true);
			setTitle(app.getMenu("ManagePerspectives"));
			buildGUI();
			pack();
			setLocationRelativeTo(app.getMainComponent());
		}
		
		/**
		 * Build the GUI which includes a list with perspectives and some buttons.
		 */
		private void buildGUI() {
			// build list with perspectives
			listModel = new DefaultListModel();
			Perspective[] perspectives = layout.getPerspectives();
			for(int i = 0; i < perspectives.length; ++i) {
				listModel.addElement(perspectives[i].getId());
			}
			
			list = new JList(listModel);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setLayoutOrientation(JList.VERTICAL);
			list.setVisibleRowCount(6);
			
			JScrollPane listSP = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			listSP.setPreferredSize(new Dimension(150, 200));
			
			// build button panel to remove perspectives and to close this dialog
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			removeButton = new JButton(app.getPlain("Remove"));
			removeButton.addActionListener(this);
			updateRemoveButton();
			buttonPanel.add(removeButton);
			
			cancelButton = new JButton(app.getPlain("Cancel"));
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);
			
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			cp.add(listSP, BorderLayout.CENTER);
			cp.add(buttonPanel, BorderLayout.SOUTH);
		}

		/**
		 * One of the buttons was pressed.
		 */
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == cancelButton) {
				setVisible(false);
				dispose();
			} else if(e.getSource() == removeButton) {
				int index = list.getSelectedIndex();
				
				if(index != -1) { // -1 = no entry selected
					listModel.remove(index);
					layout.removePerspective(index);
					GeoGebraPreferencesD.getPref().saveXMLPreferences(app);
					
					app.updateMenubar();
					updateRemoveButton();
					app.updateDockBar();
				}
			}
		}
		
		/**
		 * Don't enable the remove button if there are no items.
		 */
		private void updateRemoveButton() {
			removeButton.setEnabled(listModel.size() != 0);
		}
	}
}
