package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.gui.view.algebra.AlgebraController;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LayerView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.inputbar.WarningErrorHandler;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.full.gui.layout.panels.AlgebraPanelInterface;
import org.geogebra.web.full.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.PrintableW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.TimerSystemW;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * HTML5 version of AV
 *
 */
public class AlgebraViewW extends Tree implements LayerView, AlgebraView,
		OpenHandler<TreeItem>, SettingListener, ProvidesResize, PrintableW {

	/** app */
	private final AppW app;
	/** Localization */
	protected final Localization loc;
	/** Kernel */
	protected final Kernel kernel;
	private AnimationScheduler repaintScheduler = AnimationScheduler.get();
	// protected AlgebraInputW inputPanel;
	/** Input item */
	RadioTreeItem inputPanelLatex;
	private AlgebraStyleBarW styleBar;
	private boolean editItem = false;
	private GeoElement draggedGeo;
	// to store width if original was thiner than needed.
	private Integer originalWidth = null;
	private int mqFontSize = -1;
	private int maxItemWidth = 0;
	private boolean latexLoaded;
	private int userWidth;
	private TreeItem inputPanelTreeItem;
	private ArrayList<Integer> collapsedNodes;
	private boolean isShowingAuxiliaryObjects;
	/** whether it's attached to kernel */
	protected boolean attached = false;

	private AnimationCallback repaintCallback = new AnimationCallback() {
		@Override
		public void execute(double ts) {
			doRepaint();
		}
	};

	private AnimationCallback repaintSlidersCallback = new AnimationCallback() {
		@Override
		public void execute(double ts) {
			doRepaintSliders();
		}
	};

	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	protected SortMode treeMode = SortMode.ORDER;

	private boolean showAuxiliaryObjectsSettings = false;

	private boolean settingsChanged = false;

	/**
	 * Nodes for tree mode MODE_DEPENDENCY
	 */
	private TreeItem depNode;
	private TreeItem indNode;
	private TreeItem auxiliaryNode;

	/**
	 * Root node for tree mode MODE_TYPE.
	 */
	private TreeItem rootType;

	/**
	 * Nodes for tree mode MODE_TYPE
	 */
	private HashMap<String, TreeItem> typeNodesMap;

	/* for SortMode.ORDER */
	private TreeItem rootOrder;

	/* for SortMode.LAYER */
	private TreeItem rootLayer;
	private HashMap<Integer, TreeItem> layerNodesMap;

	private HashMap<GeoElement, RadioTreeItem> nodeTable = new HashMap<>(500);

	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private StringBuilder sbXML;

	private RadioTreeItem activeItem;
	// private AlgebraHelperBar helperBar;

	private AlgebraController algebraController;
	private AVSelectionController selectionCtrl;
	private ItemFactory itemFactory;

	/**
	 * @return algebra controller
	 */
	public AlgebraController getAlgebraController() {
		return algebraController;
	}

	/**
	 * Creates new AV
	 *
	 * @param algCtrl
	 *            controller
	 */
	public AlgebraViewW(AlgebraController algCtrl) {
		super(new TreeImages());
		Log.debug("creating Algebra View");
		this.algebraController = algCtrl;
		this.app = (AppW) algCtrl.getApplication();
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.itemFactory = new ItemFactory();
		itemFactory.setSlidersEnabled(app.getConfig().hasSlidersInAV());
		this.addOpenHandler(this);
		selectionCtrl = new AVSelectionController(app);
		algCtrl.setView(this);
		if (algCtrl instanceof AlgebraControllerW) {
			initGUI((AlgebraControllerW) algCtrl);
		}

		app.getSelectionManager()
				.addSelectionListener(new GeoElementSelectionListener() {
					@Override
					public void geoElementSelected(GeoElement geo,
							boolean addToSelection) {
						updateSelection();
					}
				});
		app.getGgbApi().setEditor(new AlgebraMathEditorAPI(this));
	}

	/**
	 * Scroll handler
	 */
	void onAlgebraScroll() {
		if (activeItem != null) {
			activeItem.reposition();
		}

		if (getInputTreeItem() != null) {
			getInputTreeItem().setItemWidth(getFullWidth());
		}
	}

	private void initGUI(AlgebraControllerW algCtrl) {
		// add listener
		addDomHandler(algCtrl, MouseDownEvent.getType());
		addDomHandler(algCtrl, MouseMoveEvent.getType());
		addDomHandler(algCtrl, TouchStartEvent.getType());
		addDomHandler(algCtrl, TouchEndEvent.getType());
		addDomHandler(algCtrl, TouchMoveEvent.getType());

		// initializes the tree model, important to set tree mode first to avoid
		// inf. loop #3651
		treeMode = app.getSettings().getAlgebra().getTreeMode();
		initModel();

		setLabels();

		getElement().addClassName("algebraView");

		// needed to have an element with tabindex > 0 with focus to catch
		// keyboard events
		getElement().setTabIndex(GUITabs.AV_TREE);
		addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		addKeyPressHandler(this.app.getGlobalKeyDispatcher());
		if (!app.getArticleElement().preventFocus()) {
			setFocus(true);
		}

		// Initialize settings and register listener
		app.getSettings().getAlgebra().addListener(this);
		settingsChanged(app.getSettings().getAlgebra());
	}

	@Override
	public void onBrowserEvent(Event event) {
		// as arrow keys are prevented in super.onBrowserEvent,
		// we need to handle arrow key events before that
		switch (DOM.eventGetType(event)) {
		default:
			// do nothing
			break;
		case Event.ONKEYUP:
			switch (event.getKeyCode()) {
			default:
				// do nothing
				break;
			case KeyCodes.KEY_UP:
			case KeyCodes.KEY_DOWN:
			case KeyCodes.KEY_LEFT:
			case KeyCodes.KEY_RIGHT:
				// this may be enough for Safari too, because it is not
				// onkeypress
				if (!(editItem || Browser.isTabletBrowser())) {
					app.getGlobalKeyDispatcher()
							.handleSelectedGeosKeysNative(event);
					event.stopPropagation();
					event.preventDefault();
					return;
				}

				// TODO: check this ----
				break;
			case KeyCodes.KEY_TAB:
				event.preventDefault();
				return;
			}
			break;
		case Event.ONMOUSEDOWN:
		case Event.ONTOUCHSTART:
			app.closePopups();
			// see this.setFocus(true) and this.addKeyDownHandler...
			app.focusGained(this, this.getElement());
		}

		if (Browser.isTabletBrowser()) {
			handleTabletKeyboard(event);
			return;
		}
		if (!editItem) {
			// background click
			if (event.getTypeInt() == Event.ONCLICK
					&& !CancelEventTimer.cancelKeyboardHide()
					&& !CancelEventTimer.cancelMouseEvent()) {
				// maybe another focusScheduled is called, but
				// that should not be a problem, the problem should
				// collect blur events all along the way
				app.getGuiManager().focusScheduled(true, true, true);
				app.hideKeyboard();
			}
			super.onBrowserEvent(event);
		}
	}

	/**
	 * handles input of keyboard attached to tablet
	 *
	 * @param event
	 *            keyboard events
	 */
	private void handleTabletKeyboard(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYPRESS:
			handleKeyPressed(event);
			break;
		case Event.ONKEYDOWN:
			handleKeyDown(event);
			break;
		default:
			break;
		}
	}

	private void handleKeyPressed(Event event) {
		int keyCode = event.getKeyCode();

		switch (keyCode) {
		case GWTKeycodes.KEY_ENTER:
		case GWTKeycodes.KEY_ESCAPE:
		case GWTKeycodes.KEY_BACKSPACE:
			getActiveTreeItem().getMathField().getKeyListener().onKeyPressed(
					new KeyEvent(keyCode, 0, (char) event.getCharCode()));
			break;
		default:
			getActiveTreeItem().getMathField().getKeyListener().onKeyTyped(
					new KeyEvent(keyCode, 0, (char) event.getCharCode()));
			break;
		}
	}

	private void handleKeyDown(Event event) {
		int keyCode = event.getKeyCode();

		if (keyCode == 0 && Browser.isIPad()) {
			int arrowType = Browser.getIOSArrowKeys(event);
			if (arrowType != -1) {
				keyCode = arrowType;
			}
		}
		switch (keyCode) {
		case GWTKeycodes.KEY_BACKSPACE:
			if (Browser.isAndroid()) {
				getActiveTreeItem().getMathField().getKeyListener()
						.onKeyPressed(new KeyEvent(keyCode, 0,
								(char) event.getCharCode()));
			}
			break;
		case GWTKeycodes.KEY_LEFT:
		case GWTKeycodes.KEY_RIGHT:
		case GWTKeycodes.KEY_UP:
		case GWTKeycodes.KEY_DOWN:
		case GWTKeycodes.KEY_TAB:
			getActiveTreeItem().getMathField().getKeyListener()
					.onKeyPressed(new KeyEvent(keyCode, 0,
							(char) event.getCharCode()));
			event.stopPropagation();
			break;
		default:
			break;
		}
	}

	/**
	 * schedule a repaint
	 */
	public void deferredRepaint() {
		repaintScheduler.requestAnimationFrame(repaintCallback);
	}

	/**
	 * Repaint sliders in next animation frame
	 */
	public void deferredRepaintSliders() {
		repaintScheduler.requestAnimationFrame(repaintSlidersCallback);
	}

	/**
	 * timer system suggests a repaint
	 */
	@Override
	public boolean suggestRepaint() {
		// repaint sliders as fast as possible

		if (isShowing()) {
			deferredRepaintSliders();
		}

		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			return false;
		}

		if (waitForRepaint == TimerSystemW.REPAINT_FLAG) {
			if (isShowing()) {
				deferredRepaint();
				waitForRepaint = TimerSystemW.SLEEPING_FLAG;
			}
			return true;
		}

		waitForRepaint--;
		return true;
	}

	@Override
	public final void repaintView() {
		app.ensureTimerRunning();
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG) {
			waitForRepaint = TimerSystemW.ALGEBRA_LOOPS;
		}
	}

	/**
	 * Make sure we repaint all updated objects in nodes that were collapsed
	 * before
	 */
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		this.deferredRepaint();
	}

	@Override
	public final int getViewID() {
		return App.VIEW_ALGEBRA;
	}

	// TODO EuclidianView#setHighlighted() doesn't exist
	/**
	 * updates node of GeoElement geo (needed for highlighting)
	 *
	 */
	@Override
	public void update(GeoElement geo) {
		long start = System.currentTimeMillis();
		TreeItem node = nodeTable.get(geo);

		if (node != null) {

			RadioTreeItem item = RadioTreeItem.as(node);

			item.updateOnNextRepaint();
			repaintView();
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151).
			 */
			if (isEditItem() && item.getController().isEditing()) {
				item.cancelEditing();
			}

		}
		GeoGebraProfiler.addAlgebra(System.currentTimeMillis() - start);
	}

	/**
	 * Repaints the whole AV tree, item by item.
	 */
	public void doRepaint() {
		Object geo;
		// suppose that the add operations have been already done elsewhere
		for (int i = 0; i < getItemCount(); i++) {
			TreeItem ti = getItem(i);
			if (ti instanceof CheckboxTreeItem) {
				CheckboxTreeItem.as(ti).repaint();

			} else {
				geo = getItem(i).getUserObject();
				if (geo instanceof GeoElement) {
					RadioTreeItem.as(ti).repaint();
				} else if (ti.getWidget() instanceof GroupHeader) {
					((GroupHeader) ti.getWidget())
							.setText(ti.getUserObject().toString());
					if (ti.getState()) {
						repaintChildren(ti);
					}

				}
			}
		}
		if (getInputTreeItem() != null) {
			getInputTreeItem().setPixelRatio(app.getPixelRatio());
		}
	}

	/**
	 * updates only GeoNumerics; used for animated
	 */
	protected void doRepaintSliders() {
		switch (treeMode) {

		case ORDER:
			repaintSlidersOrder();
			break;
		case TYPE:
		case LAYER:
			for (int i = 0; i < getItemCount(); i++) {
				repaintSlidersDependent(getItem(i));
			}
			break;
		case DEPENDENCY:
			repaintSlidersDependent(this.indNode);
			break;
		default:
			break;
		}
	}

	private static void repaintSlidersDependent(TreeItem ti) {
		if (ti != null) {
			for (int j = 0; j < ti.getChildCount(); j++) {
				repaintSliderNode(ti.getChild(j));
			}
		}
	}

	private void repaintSlidersOrder() {
		for (int j = 0; j < getItemCount(); j++) {
			repaintSliderNode(getItem(j));
		}
	}

	private static void repaintSliderNode(TreeItem ti) {
		if (ti instanceof SliderTreeItemRetex) {
			((SliderTreeItemRetex) ti).repaint();
		}
	}

	private static void repaintChildren(TreeItem item) {
		for (int j = 0; j < item.getChildCount(); j++) {
			if (item.getChild(j) instanceof RadioTreeItem) {
				RadioTreeItem.as(item.getChild(j)).repaint();
			}
		}
	}

	private void updateCollapsedNodesIndices() {

		// no collapsed nodes
		if (getTreeMode() == SortMode.ORDER) {
			collapsedNodes = null;
			return;
		}

		if (collapsedNodes == null) {
			collapsedNodes = new ArrayList<>();
		} else {
			collapsedNodes.clear();
		}

		for (int i = 0; i < getItemCount(); i++) {
			TreeItem node = getItem(i);
			if (!node.getState()) {
				collapsedNodes.add(i);
			}
		}
	}

	/**
	 * @return The display mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	@Override
	public SortMode getTreeMode() {
		return treeMode;
	}

	/**
	 * @param value
	 *            Either AlgebraView.MODE_DEPDENCY or AlgebraView.MODE_TYPE
	 */
	@Override
	public void setTreeMode(SortMode value) {
		if (getTreeMode().equals(value)) {
			return;
		}

		clearView();

		this.treeMode = value;
		initModel();

		kernel.notifyAddAll(this);
		setLabels();
	}

	/**
	 * returns settings in XML format
	 *
	 * @param sb
	 *            string builder
	 */
	public final void getXML(StringBuilder sb) {

		if (sbXML == null) {
			sbXML = new StringBuilder();
		} else {
			sbXML.setLength(0);
		}

		sbXML.append("\t<mode ");
		sbXML.append("val=\"");
		sbXML.append(getTreeMode().toInt());
		sbXML.append("\"");
		sbXML.append("/>\n");

		// auxiliary objects
		boolean flag = showAuxiliaryObjects();
		if (flag) {
			sbXML.append("\t<auxiliary ");
			sbXML.append("show=\"");
			sbXML.append(flag);
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

		// collapsed nodes
		updateCollapsedNodesIndices();
		if (collapsedNodes != null && collapsedNodes.size() > 0) {
			sbXML.append("\t<collapsed ");
			sbXML.append("val=\"");
			sbXML.append(collapsedNodes.get(0));
			for (int i = 1; i < collapsedNodes.size(); i++) {
				sbXML.append(",");
				sbXML.append(collapsedNodes.get(i));
			}
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

		if (sbXML.length() > 0) {
			sb.append("<algebraView>\n");
			sb.append(sbXML);
			sb.append("</algebraView>\n");
		}
	}

	private void setCollapsedNodes(int[] collapsedNodes) {
		if (collapsedNodes == null) {
			return;
		}

		if (this.collapsedNodes == null) {
			this.collapsedNodes = new ArrayList<>();
		} else {
			this.collapsedNodes.clear();
		}

		for (int collapsedNode : collapsedNodes) {
			this.collapsedNodes.add(collapsedNode);
		}
	}

	/**
	 * @return whether auxiliary objects are showing
	 */
	private boolean showAuxiliaryObjects() {
		return app.showAuxiliaryObjects;
	}

	/**
	 * @param flag
	 *            whether to show auxiliary
	 */
	public void setShowAuxiliaryObjects(boolean flag) {
		if (this.isShowingAuxiliaryObjects == flag) {
			return;
		}
		isShowingAuxiliaryObjects = flag;
		app.showAuxiliaryObjects = flag;

		cancelEditItem();

		if (flag) {
			clearView();

			if (getTreeMode() == SortMode.DEPENDENCY) {
				addItem(auxiliaryNode);
			}
			kernel.notifyAddAll(this);
		} else {
			// if we're listing the auxiliary objects in a single leaf we can
			// just remove that leaf, but for type-based categorization those
			// auxiliary nodes might be scattered across the whole tree,
			// therefore we just rebuild the tree
			if (getTreeMode() == SortMode.DEPENDENCY) {
				if (auxiliaryNode.getParentItem() != null) {
					removeItem(auxiliaryNode);
				}
			} else {
				clearView();
				kernel.notifyAddAll(this);
			}
		}
	}

	/**
	 * apply the settings
	 */
	public void applySettings() {

		if (!settingsChanged) {
			// that means that no settings were stored in the file: reset
			// settings to have default
			AlgebraSettings settings = app.getSettings().getAlgebra();
			settings.reset();
			settingsChanged(settings);
		}

		settingsChanged = false;

		// auxilliary objects
		setShowAuxiliaryObjects(showAuxiliaryObjectsSettings);

		// collapsed nodes
		if (collapsedNodes == null) {
			return;
		}

		for (int i : collapsedNodes) {
			TreeItem node = getItem(i);
			if (node != null) {
				node.setState(false);
				if (node.getWidget() instanceof GroupHeader) {
					((GroupHeader) node.getWidget()).setChecked(false);
				}
			}
		}
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {

		AlgebraSettings algebraSettings = (AlgebraSettings) settings;
		setTreeMode(algebraSettings.getTreeMode());
		showAuxiliaryObjectsSettings = algebraSettings
				.getShowAuxiliaryObjects();
		setCollapsedNodes(algebraSettings.getCollapsedNodes());

		settingsChanged = true;
		resetItems(false);
	}

	/**
	 * Fill this view and attach it to kernel
	 */
	public void attachView() {

		if (attached) {
			return;
		}
		attached = true;
		clearView();
		kernel.notifyAddAll(this);
		applySettings();
		kernel.attach(this);

		/*
		 * if (treeMode == SortMode.DEPENDENCY) { indNode.setState(true);
		 * depNode.setState(true); if (auxiliaryNode.getParentItem() != null) {
		 * auxiliaryNode.setState(true); } }
		 */
	}

	/**
	 * Detach this from kernel
	 */
	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;
	}

	/**
	 * Method to initialize the tree model of the current tree mode. This method
	 * should be called whenever the tree mode is changed, it won't initialize
	 * anything if not necessary.
	 *
	 * This method will also actually change the model of the tree.
	 */
	protected void initModel() {
		// build default tree structure
		switch (treeMode) {
		default:
		case DEPENDENCY:
			initDependencyOrder();
			break;
		case ORDER:
			initConstructionOrder();
			break;
		case TYPE:
			initTypeOrder();
			break;
		case LAYER:
			initLayer();
			break;
		}
	}

	private void initDependencyOrder() {
		// don't re-init anything
		if (depNode == null || indNode == null || auxiliaryNode == null) {
			// rootDependency = new TreeItem();
			depNode = new AVTreeItem(); // dependent objects
			indNode = new AVTreeItem();
			auxiliaryNode = new AVTreeItem();
		}

		// set the root
		clear();
		addItem(indNode);
		addItem(depNode);

		// add auxiliary node if neccessary
		if (app.showAuxiliaryObjects) {
			if (auxiliaryNode.getTree() != this) {
				addItem(auxiliaryNode);
			}
		}
	}

	private void initConstructionOrder() {
		if (rootOrder == null) {
			// both rootOrder and AlgebraView will have the Tree items
			rootOrder = new AVTreeItem();
		}
		setUserObject(rootOrder, "", "");

		// always try to remove the auxiliary node
		if (app.showAuxiliaryObjects && auxiliaryNode != null) {
			removeAuxiliaryNode();
		}

		// set the root
		clear();
		if (isAlgebraInputVisible()) {
			// why is this here and not in case of DEPENDENCY, LAYER
			super.addItem(inputPanelTreeItem);
		}
	}

	private void initTypeOrder() {
		// don't re-init anything
		if (rootType == null) {
			rootType = new AVTreeItem();
			// setUserObject(rootType, "");
			typeNodesMap = new HashMap<>(5);
		}

		// always try to remove the auxiliary node
		if (app.showAuxiliaryObjects && auxiliaryNode != null) {
			removeAuxiliaryNode();
		}

		// set the root
		clear();
		if (isAlgebraInputVisible()) {
			// why is this here and not in case of DEPENDENCY, LAYER
			super.addItem(inputPanelTreeItem);
		}
	}

	private void initLayer() {
		// don't re-init anything
		if (rootLayer == null) {
			rootLayer = new AVTreeItem();
			layerNodesMap = new HashMap<>(10);
		}

		// always try to remove the auxiliary node
		if (app.showAuxiliaryObjects && auxiliaryNode != null) {
			removeAuxiliaryNode();
		}

		// set the root
		clear();
		if (isAlgebraInputVisible()) {
			// why is this here and not in case of DEPENDENCY, LAYER
			super.addItem(inputPanelTreeItem);
		} // addItem(rootLayer);
	}

	/**
	 * remove all from the tree
	 */
	protected void clearTree() {
		switch (getTreeMode()) {
		default:
		case DEPENDENCY:
			indNode.removeItems();
			depNode.removeItems();
			auxiliaryNode.removeItems();
			break;
		case TYPE:
			removeItems();
			typeNodesMap.clear();
			break;
		case LAYER:
			removeItems();
			layerNodesMap.clear();
			break;
		case ORDER:
			rootOrder.removeItems();
			removeItems();
		}
	}

	/**
	 * set labels on the tree
	 */
	protected void setTreeLabels() {
		TreeItem node;
		switch (getTreeMode()) {
		case DEPENDENCY:
			setUserObject(indNode, loc.getMenu("FreeObjects"), "1");
			setUserObject(depNode, loc.getMenu("DependentObjects"), "2");
			setUserObject(auxiliaryNode, loc.getMenu("AuxiliaryObjects"), "3");
			break;
		case TYPE:
			for (Entry<String, TreeItem> entry : typeNodesMap.entrySet()) {
				String key = entry.getKey();
				node = entry.getValue();
				setUserObject(node, loc.getMenu(key), key);
			}
			break;
		case LAYER:
			for (Entry<Integer, TreeItem> entry : layerNodesMap.entrySet()) {
				Integer key = entry.getKey();
				node = entry.getValue();
				setUserObject(node, loc.getPlain("LayerA", key.toString()),
						key.toString());
			}
			break;
		case ORDER:
			break;
		}
		// if (lastLang == null || !lastLang.equals(loc.getLocaleStr())) {
		rebuildItems();
		// }
	}

	/**
	 *
	 * @param geo
	 *            element
	 * @param forceLayer
	 *            override layer stored in Geo
	 * @return parent node of this geo
	 */
	protected TreeItem getParentNode(GeoElement geo, int forceLayer) {
		TreeItem parent;

		switch (treeMode) {
		case DEPENDENCY:
			if (geo.isAuxiliaryObject()) {
				parent = auxiliaryNode;
			} else if (geo.isIndependent()) {
				parent = indNode;
			} else {
				parent = depNode;
			}
			break;
		case TYPE:
			// get type node
			String typeString = geo.getTypeStringForAlgebraView();
			parent = typeNodesMap.get(typeString);

			// do we have to create the parent node?
			if (parent == null) {
				String transTypeString = geo
						.translatedTypeStringForAlgebraView();
				parent = new AVTreeItem(new InlineLabel(transTypeString));
				setUserObject(parent, transTypeString, typeString);
				typeNodesMap.put(typeString, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++) {
					TreeItem child = getItem(i);
					String groupName = getGroupName(child);
					if (typeString.compareTo(groupName) < 0
							|| (child.getWidget() != null
									&& this.inputPanelTreeItem != null
									&& this.inputPanelTreeItem
											.getWidget() != null
									&& child.getWidget()
											.equals(this.inputPanelTreeItem
													.getWidget()))) {
						pos = i;
						break;
					}
				}

				insertItem(pos, parent);
			}
			break;
		case LAYER:
			// get type node
			int layer = forceLayer > -1 ? forceLayer : geo.getLayer();
			parent = layerNodesMap.get(layer);

			// do we have to create the parent node?
			if (parent == null) {
				String layerStr = loc.getPlain("LayerA", layer + "");
				parent = new AVTreeItem(new InlineLabel(layerStr));

				setUserObject(parent, layerStr, layer + "");

				layerNodesMap.put(layer, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++) {
					TreeItem child = getItem(i);
					if (layerStr.compareTo(getGroupName(child)) < 0) {
						pos = i;
						break;
					}
				}

				insertItem(pos, parent);
			}
			break;
		case ORDER:
			parent = rootOrder;

			break;
		default:
			parent = null;
		}

		return parent;
	}

	private static String getGroupName(TreeItem child) {
		return child.getUserObject() instanceof String
				? ((String) child.getUserObject()) : "_";
	}

	/**
	 * Assign element or element group to a given tree node
	 *
	 * @param ti
	 *            tree item
	 * @param ob
	 *            object
	 * @param key
	 *            sorting key
	 */
	public final void setUserObject(TreeItem ti, final String ob, String key) {
		ti.setUserObject(ob);
		GroupHeader group = new GroupHeader(this.app.getSelectionManager(), ti,
				ob, key,
				SharedResources.INSTANCE.algebra_tree_open().getSafeUri(),
				SharedResources.INSTANCE.algebra_tree_closed().getSafeUri());
		group.getElement().getStyle().setFontSize(getFontSizeWeb(),
				Unit.PX);
		ti.setWidget(group);
	}

	/**
	 * Remove this node and prune empty branches
	 *
	 * @param node
	 *            node to be removed
	 */
	private void removeFromModel(TreeItem node) {
		node.remove();
		nodeTable.remove(node.getUserObject());

		// remove the type branch if there are no more children
		switch (treeMode) {
		case DEPENDENCY:
		default:
			// do nothing
			break;
		case TYPE:
			String typeString = ((GeoElement) node.getUserObject())
					.getTypeStringForAlgebraView();
			TreeItem parent = typeNodesMap.get(typeString);

			// this has been the last node
			if (parent != null && parent.getChildCount() == 0) {
				typeNodesMap.remove(typeString);
				parent.remove();
			}
			break;
		case LAYER:
			removeFromLayer(((GeoElement) node.getUserObject()).getLayer());

			break;
		case ORDER:
			rootOrder.removeItem(node);
			if (getItemCount() > 0 && getItem(0) instanceof RadioTreeItem) {
				((RadioTreeItem) getItem(0)).setFirst(true);
			}
		}
	}

	private void removeFromLayer(int i) {
		TreeItem parent = layerNodesMap.get(i);

		// this has been the last node
		if ((parent != null) && parent.getChildCount() == 0) {
			layerNodesMap.remove(i);
			parent.remove();
		}
	}

	private void addRadioTreeItem(TreeItem parent, RadioTreeItem node) {
		// add node to model (alphabetically ordered)
		int pos = getInsertPosition(parent, node.geo, treeMode);

		if (pos == 0 && parent == rootOrder) {
			node.setFirst(true);
		}

		if (pos >= count(parent)) {
			if (treeMode == SortMode.LAYER) {
				if (isAlgebraInputVisible()) {
					removeItem(inputPanelTreeItem);
				}
				parent.addItem(node);
				if (isAlgebraInputVisible()) {
					super.addItem(inputPanelTreeItem);
				}

			} else {

				if (parent.equals(rootOrder)) {
					addItem(node);
				} else {
					parent.addItem(node);
				}
			}

		} else {
			try {

				if (parent.equals(rootOrder)) {
					insertItem(pos, node);
					updateIndices(pos);
				} else {
					parent.insertItem(pos, node);
				}
			} catch (IndexOutOfBoundsException e) {

				if (parent.equals(rootOrder)) {
					addItem(node);
				} else {
					parent.addItem(node);
				}
			}
		}
	}

	private int count(TreeItem parent) {
		return parent == this.rootOrder ? getItemCount()
				: parent.getChildCount();
	}

	/**
	 * adds a new node to the tree
	 */
	@Override
	public void add(GeoElement geo) {
		add(geo, -1, true);
	}

	/**
	 * @param geo
	 *            element
	 * @param forceLayer
	 *            layer or -1 to use layer from geo
	 * @param scroll
	 *            whether we may scroll down
	 */
	protected void add(GeoElement geo, int forceLayer, boolean scroll) {
		if (!this.isAttachedToKernel()) {
			return;
		}
		cancelEditItem();

		this.isShowingAuxiliaryObjects = showAuxiliaryObjects();

		if (geo.isLabelSet() && geo.showInAlgebraView()
				&& geo.isSetAlgebraVisible()) {

			// don't add auxiliary objects if the tree is categorized by type
			if (!getTreeMode().equals(SortMode.DEPENDENCY)
					&& !showAuxiliaryObjects() && geo.isAuxiliaryObject()) {
				return;
			}

			TreeItem parent = getParentNode(geo, forceLayer);
			RadioTreeItem node = itemFactory.createAVItem(geo);

			addRadioTreeItem(parent, node);

			// if (node != null && !node.isInputTreeItem()) {
			// setActiveTreeItem(node);
			// }

			RadioTreeItem.as(node).setItemWidth(getFullWidth());

			boolean wasEmpty = isNodeTableEmpty();
			nodeTable.put(geo, node);
			if (wasEmpty) {
				// if adding new elements the first time,
				// let's show the X signs in the input bar!
				if (this.inputPanelLatex != null) {
					this.inputPanelLatex.updateGUIfocus(false);
				}

			}

			// ensure that the leaf with the new object is visible
			parent.setState(true);
		}

		if (inputPanelLatex != null && scroll) {
			inputPanelLatex.updateUIforInput();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {

					inputPanelLatex.updateButtonPanelPosition();
					getAlgebraDockPanel().scrollAVToBottom();

				}
			});
		}
		updateSelection();
	}

	@Override
	public void changeLayer(GeoElement g, int oldLayer, int newLayer) {
		if (this.treeMode.equals(SortMode.LAYER)) {
			TreeItem node = nodeTable.get(g);

			if (node != null) {
				node.remove();
				nodeTable.remove(node.getUserObject());
				removeFromLayer(oldLayer);
			}

			this.add(g, newLayer, false);
		}
	}

	@Override
	public void remove(GeoElement geo) {
		doRemove(geo);
	}

	@Override
	public void doRemove(GeoElement geo) {
		cancelEditItem();
		TreeItem node = nodeTable.get(geo);
		if (node != null) {
			int firstUpdateIndex = indexOf(node);
			removeFromModel(node);
			if (firstUpdateIndex >= 0) {
				updateIndices(firstUpdateIndex);
			}
		}

		if (inputPanelLatex != null) {
			inputPanelLatex.updateButtonPanelPosition();
			inputPanelLatex.setIndexLast();
		}
	}

	private int indexOf(TreeItem node) {
		for (int i = 0; i < getItemCount(); i++) {
			if (node == getItem(i)) {
				return i;
			}
		}
		return -1;
	}

	private void updateIndices(int removeIndex) {
		for (int i = removeIndex; i < this.getItemCount(); i++) {
			TreeItem row = this.getItem(i);
			if (row instanceof RadioTreeItem) {
				((RadioTreeItem) row).getHelpToggle().setIndex(i + 1);
			}
		}
	}

	@Override
	public void clearView() {
		nodeTable.clear();
		latexLoaded = false;
		clearTree();
		activeItem = null;
		if (inputPanelLatex != null) {
			inputPanelLatex.setText("");
		}
		showAlgebraInput(false);
		maxItemWidth = 0;
	}

	/**
	 * renames an element and sorts list
	 */
	@Override
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	private void removeAuxiliaryNode() {
		removeItem(auxiliaryNode);
	}

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of
	 * GeoElement as user objects.
	 *
	 * @param parent
	 *            parent node
	 * @param newGeo
	 *            geo to be inserted
	 *
	 * @param mode
	 *            sort mode
	 * @return position
	 */
	final public int getInsertPosition(TreeItem parent, GeoElement newGeo,
			SortMode mode) {

		// standard case: binary search
		int left = 0;
		int right = count(parent);
		if (right == 0) {
			return right;
		}
		// bigger then last?
		TreeItem node = getItem(parent, right - 1);
		if (node.getUserObject() == null) {
			if (right == 1) {
				return 0;
			}
			node = getItem(parent, right - 2);
			right--;
		}
		GeoElement geo2 = ((GeoElement) node.getUserObject());

		if (compare(newGeo, geo2, mode)) {
			return right;
		}
		// binary search
		while (right > left) {
			int middle = (left + right) / 2;
			node = getItem(parent, middle);
			geo2 = ((GeoElement) node.getUserObject());

			if (!compare(newGeo, geo2, mode)) {
				right = middle;
			} else {
				left = middle + 1;
			}
		}

		// insert at correct position
		return right;
	}

	private TreeItem getItem(TreeItem parent, int i) {
		return parent == rootOrder ? getItem(i) : parent.getChild(i);
	}

	/**
	 * Performs a binary search for geo among the children of parent. All
	 * children of parent have to be instances of GeoElement sorted
	 * alphabetically by their names.
	 *
	 * @param parent
	 *            parent node
	 * @param geoLabel
	 *            label of geo
	 *
	 * @return -1 when not found
	 */
	public static int binarySearchGeo(TreeItem parent, String geoLabel) {
		int left = 0;
		int right = parent.getChildCount() - 1;
		if (right == -1 || geoLabel == null) {
			return -1;
		}

		// binary search for geo's label
		while (left <= right) {
			int middle = (left + right) / 2;
			TreeItem node = parent.getChild(middle);
			String nodeLabel = ((GeoElement) node.getUserObject())
					.getLabelSimple();

			int compare = GeoElement.compareLabels(geoLabel, nodeLabel);
			if (compare < 0) {
				right = middle - 1;
			} else if (compare > 0) {
				left = middle + 1;
			} else {
				return middle;
			}
		}

		return -1;
	}

	/**
	 * Performs a linear search for geo among the children of parent.
	 *
	 * @param parent
	 *            parent node
	 * @param geoLabel
	 *            label of geo
	 * @return -1 when not found
	 */
	public static int linearSearchGeo(TreeItem parent, String geoLabel) {
		if (geoLabel == null) {
			return -1;
		}
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TreeItem node = parent.getChild(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel(StringTemplate.defaultTemplate))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Reset the algebra view if the mode changed.
	 */
	@Override
	public void setMode(int mode, ModeSetter m) {
		reset();
	}

	private static boolean compare(GeoElement geo1, GeoElement geo2,
			SortMode mode) {
		if (mode == SortMode.ORDER) {
			return geo1.getConstructionIndex() > geo2.getConstructionIndex()
					|| (geo1.getConstructionIndex() == geo2
							.getConstructionIndex()
							&& geo1.getParentAlgorithm() != null
							&& geo1.getParentAlgorithm().isBefore(geo1, geo2));
		}
		// alphabetical
		return GeoElement.compareLabels(
				geo1.getLabel(StringTemplate.defaultTemplate),
				geo2.getLabel(StringTemplate.defaultTemplate)) > 0;
	}

	@Override
	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	@Override
	public void reset() {
		cancelEditItem();
		repaintView();
		ensureSelectedItemVisible();
	}

	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	@Override
	public void setLabels() {
		/*
		 * if (inputPanel != null) { inputPanel.setLabels(); }
		 */
		// TODO add button ?
		setTreeLabels();
		if (this.inputPanelLatex != null) {
			this.inputPanelLatex.setLabels();
		}
		if (this.styleBar != null) {
			this.styleBar.setLabels();
		}

		if (inputPanelLatex != null && inputPanelLatex.hasHelpPopup()) {
			app.getGuiManager().getInputHelpPanel().setLabels();
		}
		AriaHelper.setLabel(this, app.getLocalization().getMenu("Algebra"));
	}

	@Override
	public final GeoElement getLastSelectedGeo() {
		return getSelectionCtrl().getLastSelectedGeo();
	}

	@Override
	public final void setLastSelectedGeo(GeoElement geo) {
		getSelectionCtrl().setLastSelectedGeo(geo);
	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub
	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the RadioButtonTreeItem containing the input-box
	 */
	public RadioTreeItem getInputTreeItem() {
		return inputPanelLatex;
	}

	/**
	 * @return currently edited tree item
	 */
	public RadioTreeItem getActiveTreeItem() {
		if (activeItem == null) {
			return this.inputPanelLatex;
		}
		return this.activeItem;
	}

	/**
	 * Create new input panel and add it
	 */
	public void setInputPanel() {

		// usually, inputPanel is here, but not in use (not attached)
		boolean forceKeyboard = false;
		boolean inputJustCreated = false;
		if (inputPanelLatex == null) {
			inputPanelLatex = createInputPanel();
			forceKeyboard = GuiManagerW.mayForceKeyboard(app);

			inputJustCreated = true;
		} else {
			inputPanelLatex.removeFromParent();
		}
		hideAlgebraInput();
		this.inputPanelTreeItem = new TreeItem(inputPanelLatex.getWidget());
		inputPanelTreeItem.addStyleName("avInputItem");
		inputPanelLatex.getWidget().getElement().getParentElement()
				.addClassName("NewRadioButtonTreeItemParent");

		unselect(getSelectionCtrl().getLastSelectedGeo());

		if (inputJustCreated) {
			if (isNodeTableEmpty()) {
				inputPanelLatex.updateGUIfocus(false);
			}
		}
		showAlgebraInput(forceKeyboard);
	}

	private RadioTreeItem createInputPanel() {
		return new RadioTreeItem(kernel).initInput();
	}

	@Override
	public void setShowAlgebraInput(boolean show) {
		if (show) {
			showAlgebraInput(false);
		} else {
			hideAlgebraInput();
		}
	}

	private void hideAlgebraInput() {
		if (!isAlgebraInputVisible()) {
			return;
		}
		super.removeItem(inputPanelTreeItem);
		inputPanelTreeItem = null;
	}

	private void showAlgebraInput(boolean forceKeyboard0) {
		if (!app.showAlgebraInput()
				|| app.getInputPosition() != InputPosition.algebraView) {
			hideAlgebraInput();
			return;
		}
		Integer inputWidth = null;
		if (isAlgebraInputVisible()) {
			// note that hideAlgebraInput just does this
			// hideAlgebraInput();
			// except it also makes this null, no problem
			// ... or? still preferring to be safe
			if (inputPanelLatex != null) {
				inputWidth = inputPanelLatex.getWidget().getElement()
						.getParentElement().getClientWidth();
			}
			super.removeItem(inputPanelTreeItem);

			// inputPanel.removeFromParent();//?
		}
		boolean inputJustCreated = false;
		boolean forceKeyboard = false;
		boolean suggestKeyboard = false;
		if (inputPanelLatex == null) {
			suggestKeyboard = true;
			forceKeyboard = forceKeyboard0 || GuiManagerW.mayForceKeyboard(app);
			inputPanelLatex = createInputPanel();
			// open the keyboard (or show the keyboard-open-button) at
			// when the application is started

			inputJustCreated = true;
		} else {
			inputPanelLatex.removeFromParent();
		}

		inputPanelTreeItem = super.addItem(inputPanelLatex.getWidget());
		inputPanelLatex.setIndexLast();
		inputPanelTreeItem.addStyleName("avInputItem");
		// inputPanelTreeItem.addStyleName("NewRadioButtonTreeItemParent");
		inputPanelLatex.getWidget().getElement().getParentElement()
				.addClassName("NewRadioButtonTreeItemParent");

		if (inputJustCreated) {
			if (isNodeTableEmpty()) {
				inputPanelLatex.updateGUIfocus(false);
			}
		}

		if (app.showView(App.VIEW_ALGEBRA) && !isToolMode()) {
			if (forceKeyboard) {
				doShowKeyboard();
			} else if (suggestKeyboard) {
				app.getAppletFrame().showKeyboardOnFocus();
			}
		}
		if (inputWidth != null) {
			inputPanelLatex.setItemWidth(inputWidth);
		}
		updateFonts();
	}

	private boolean isToolMode() {
		return app.isUnbundled() && getAlgebraDockPanel().isToolMode();
	}

	private void doShowKeyboard() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				setActiveTreeItem(null);
				getApp().showKeyboard(inputPanelLatex, true);
			}
		});
	}

	private boolean isAlgebraInputVisible() {
		return inputPanelTreeItem != null;
	}

	@Override
	public void addItem(TreeItem item) {
		// make sure the item is inserted before the inputPanel
		if (isAlgebraInputVisible()) {
			removeItem(inputPanelTreeItem);
		}
		super.addItem(item);
		if (isAlgebraInputVisible()) {
			super.addItem(inputPanelTreeItem);
		}
	}

	@Override
	protected boolean isKeyboardNavigationEnabled(TreeItem ti) {
		// keys should move the geos in the EV
		// if (isEditing())
		return false;
		// return super.isKeyboardNavigationEnabled(ti);
	}

	/**
	 * @return true if nodeTable is empty
	 */
	public boolean isNodeTableEmpty() {
		return this.nodeTable.isEmpty();
	}

	/**
	 * @return number of elements in the view
	 */
	public int getNodeTableSize() {
		return this.nodeTable.size();
	}

	/**
	 * @param item
	 *            new active item
	 */
	public void setActiveTreeItem(RadioTreeItem item) {

		if (item == null) {
			getSelectionCtrl().clear();
		}

		boolean sameItem = activeItem == item;
		if (sameItem) {
			return;
		}

		stopCurrentEditor();

		if (activeItem != null && !selectionCtrl.isMultiSelect()) {
			selectRow(activeItem.getGeo(), false);
		}

		this.activeItem = item;
		//
		if (activeItem != null) {
			selectRow(activeItem.getGeo(), true);

		}
	}

	private void stopCurrentEditor() {
		if (getActiveTreeItem() != null) {
			getActiveTreeItem().onEnter(false);
		}
	}

	/**
	 * Remove close button from active item
	 */
	public void removeCloseButton() {
		if (activeItem != null) {
			activeItem.removeCloseButton();
			stopCurrentEditor();
			// use setter to make sure current item is reset correctly
			activeItem = null;
		}
	}

	/**
	 * @param geo
	 *            geo to be (un)selected
	 * @param select
	 *            whether to select or unselect
	 */
	public void selectRow(GeoElement geo, boolean select) {
		TreeItem node = nodeTable.get(geo);
		if (node == null) {
			return;
		}

		RadioTreeItem.as(node).selectItem(select);
	}

	/**
	 * @param mayCreate
	 *            true if this may call constructor (otherwise it may return
	 *            null)
	 * @return {@link AlgebraStyleBarW}
	 */
	public AlgebraStyleBarW getStyleBar(boolean mayCreate) {
		if (mayCreate && this.styleBar == null) {
			this.styleBar = new AlgebraStyleBarW(app);
		}
		return this.styleBar;
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
		if (styleBar != null && geo.isLabelSet()) {
			styleBar.update(geo);
		}
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		if (geos != null) {
			if (geos.length > 0) {
				inputPanelLatex.previewValue(geos[0]);
			}
		} else {
			if (WarningErrorHandler.getUndefinedValiables(kernel) != null) {
				inputPanelLatex.clearUndefinedVariables();
			} else {
				inputPanelLatex.clearPreviewAndSuggestions();
			}
		}
	}

	@Override
	public boolean isShowing() {
		return isVisible() && isAttached();
	}

	@Override
	public void cancelEditItem() {
		editItem = false;
		setAnimationEnabled(true);
	}

	@Override
	public boolean isEditItem() {
		return editItem;
	}

	/**
	 * Starts the corresponding editor for geo.
	 */
	@Override
	public void startEditItem(GeoElement geo) {
		if (geo == null) {
			editItem = true;
			return;
		}
		geo.setAnimating(false);

		// open Object Properties for eg GeoImages
		// also for GeoPenStroke
		if (!geo.isAlgebraViewEditable()) {
			ArrayList<GeoElement> geos = new ArrayList<>();
			geos.add(geo);
			app.getDialogManager().showPropertiesDialog(geos);
			return;
		}

		getAlgebraDockPanel().saveAVScrollPosition();

		if (!geo.isPointOnPath() && !geo.isPointInRegion()) {
			// check for attached needed for F2 when Algebra View closed
			if ((!geo.isIndependent() && !(geo
					.getParentAlgorithm() instanceof AlgoCurveCartesian))
					|| !attached) {
				if (geo.isRedefineable()) {
					redefine(geo);
				}
				return;
			}

			if (!geo.isChangeable()) {
				if (geo.isProtected(EventType.UPDATE)) {
					app.showError(Errors.AssignmentToFixed);
					return;
				} else if (geo.isRedefineable() && !(geo
						.getParentAlgorithm() instanceof AlgoCurveCartesian)) {
					redefine(geo);
					return;
				} else if (!(geo
						.getParentAlgorithm() instanceof AlgoCurveCartesian)) {
					return;
				}
			}
		}

		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			cancelEditItem();
			editItem = true;
			setAnimationEnabled(false);
			if (node instanceof RadioTreeItem) {
				RadioTreeItem ri = RadioTreeItem.as(node);
				expandAVToItem(ri);
				ri.enterEditMode(geo.isPointOnPath() || geo.isPointInRegion());
			}
		}
	}

	/**
	 * Expand AV to make space for editing a specific item
	 *
	 * @param ri
	 *            edited item
	 */
	void expandAVToItem(RadioTreeItem ri) {
		if (app.isUnbundled()) {
			return;
		}
		int editedWidth = ri.getWidthForEdit();
		Log.debug("EDIT:" + editedWidth);

		int expanded = editedWidth;
		if (editedWidth < userWidth) {
			expanded = userWidth;
		}

		expandWidth(expanded);
		setWidths(expanded);
	}

	private void redefine(GeoElement geo) {
		if (geo.getParentAlgorithm() instanceof AlgoDependentText) {
			app.getDialogManager().showRedefineDialog(geo, true);
			return;
		}
		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			cancelEditItem();
			// FIXMEWEB select and show node
			editItem = true;

			setAnimationEnabled(false);
			if (node instanceof RadioTreeItem) {
				RadioTreeItem ri = RadioTreeItem.as(node);
				expandAVToItem(ri);
				if (!ri.enterEditMode(false)) {
					cancelEditItem();
					app.getDialogManager().showRedefineDialog(geo, true);
				}
			}
		}
	}

	/**
	 * Clear selection
	 */
	public void clearSelection() {

		// deselecting this causes a bug; it maybe fixed
		// by changing the repaintView method too,
		// adding setSelectedItem( some TreeItem ),
		// but which TreeItem should be that if more are selected?
		// that's why Arpad choosed to comment this out instead
		// super.setSelectedItem(null);

		for (int i = 0; i < getItemCount(); i++) {
			if (!(getItem(i).getUserObject() instanceof GeoElement)) {
				for (int j = 0; j < getItem(i).getChildCount(); j++) {
					TreeItem item = getItem(i).getChild(j);
					item.setSelected(false);
					if (item instanceof RadioTreeItem) {
						unselect(RadioTreeItem.as(item).getGeo());
					}
				}
			}
		}
	}

	/**
	 *
	 * @param event
	 *            drag event
	 * @param geo
	 *            element
	 */
	public void dragStart(DragStartEvent event, GeoElement geo) {
		setDraggedGeo(geo);
	}

	@Override
	public GeoElement getDraggedGeo() {
		return draggedGeo;
	}

	private void setDraggedGeo(GeoElement draggedGeo) {
		this.draggedGeo = draggedGeo;
	}

	@Override
	public boolean hasFocus() {
		Log.debug("unimplemented");
		return false;
	}

	@Override
	protected void onLoad() {
		// this may be important if the view is added/removed from the DOM
		super.onLoad();
		repaintView();
	}

	/*
	 * private int resizedWidth;* Not used in Web so far. Will not work with
	 * JLM.
	 */
	private void updateFonts() {
		if (mqFontSize != getFontSizeWeb()) {
			mqFontSize = getFontSizeWeb();
			if (getInputTreeItem() != null) {
				getInputTreeItem().updateFonts();
			}
		}
	}

	/**
	 * @param ratio
	 *            pixel ratio
	 */
	public void setPixelRatio(double ratio) {
		updateFonts();
		rebuildItems();
	}

	private void rebuildItems() {
		for (int i = 0; i < getItemCount(); i++) {
			TreeItem ti = getItem(i);
			if (!updateAndSetLabels(ti)) {
				if (ti.getWidget() instanceof GroupHeader) {
					ti.getWidget().getElement().getStyle()
							.setFontSize(getFontSizeWeb(), Unit.PX);
					for (int j = 0; j < ti.getChildCount(); j++) {
						updateAndSetLabels(ti.getChild(j));
					}
				}
			}
		}
		this.repaintView();
	}

	private int getFontSizeWeb() {
		return app.getSettings().getFontSettings().getAppFontSize();
	}

	private static boolean updateAndSetLabels(TreeItem ti) {
		if (ti instanceof RadioTreeItem) {
			RadioTreeItem.as(ti).updateOnNextRepaint();
			RadioTreeItem.as(ti).setLabels();
			return true;
		}
		return false;
	}

	/**
	 * Update items for new window size / pixel ratio
	 *
	 * @param minWidth
	 *            minimal width
	 */
	public void resize(int minWidth) {
		int resizedWidth = getOffsetWidth();
		setWidths(Math.max(minWidth, resizedWidth));
		if (activeItem != null) {
			activeItem.updateButtonPanelPosition();
		}
	}

	/*
	 * AV DockPanel resizing rules:
	 *
	 * maxItemWith: the longest item width.
	 *
	 * avWidth: the current width of AV dock panel.
	 *
	 * userWidth: the width that user sets using the splitter.
	 *
	 * editedWidth: width of the current edited item.
	 *
	 *
	 * width of InputTreeItem must be maxItemWidth when it is not on focus, to
	 * preserve its 'blue line' border. On focus its width must be avWidth
	 * userWidth should change only if user changes it.
	 *
	 * Expanding AV dock panel
	 *
	 * When user starts to edit an item, AV dock panel should be resized to:
	 *
	 * - avWidth: if the avWidth is less than the edited item's width
	 *
	 * - userWidth: if userWidth is greater than both editedWidth and avWidth
	 *
	 */

	/**
	 * Sets each tree item to a specific width
	 *
	 * @param width
	 *            to set.
	 */
	public void setItemWidth(int width) {
		if (width > maxItemWidth) {
			maxItemWidth = width;
		}
		setWidths(getFullWidth());
	}

	private void setWidths(int width) {

		if (this.getInputTreeItem() != null) {
			getInputTreeItem().setItemWidth(width);
			getInputTreeItem().reposition();
		}

		for (int i = 0; i < getItemCount(); i++) {
			TreeItem ti = getItem(i);
			if (ti instanceof RadioTreeItem) {
				RadioTreeItem ri = RadioTreeItem.as(ti);
				ri.setItemWidth(width);

			} else if (ti.getWidget() instanceof GroupHeader) {

				for (int j = 0; j < ti.getChildCount(); j++) {
					if (ti.getChild(j) instanceof RadioTreeItem) {
						RadioTreeItem.as(ti.getChild(j)).setItemWidth(width);

					}
				}

			}
		}
	}

	/**
	 * Update highlighting of rows
	 */
	public void updateSelection() {
		// if (selectionCtrl.isMultiSelect()) {
		// return;
		// }

		if (selectionCtrl.isEmpty()) {
			removeCloseButton();
		}

		for (int i = 0; i < getItemCount(); i++) {
			TreeItem ti = getItem(i);
			if (ti instanceof RadioTreeItem) {
				GeoElement geo = RadioTreeItem.as(ti).getGeo();
				if (geo != null) {
					selectRow(geo, geo.doHighlighting());
				}

			} else if (ti.getWidget() instanceof GroupHeader) {

				for (int j = 0; j < ti.getChildCount(); j++) {
					if (ti.getChild(j) instanceof RadioTreeItem) {
						GeoElement geo = RadioTreeItem.as(ti.getChild(j))
								.getGeo();
						if (geo != null) {
							selectRow(geo, geo.doHighlighting());
						}

					}
				}

			}
		}
	}

	/**
	 * Clears the selection of the last selected item, it also stops editing if
	 * it is currently edited.
	 */
	public void unselectActiveItem() {
		if (activeItem != null) {
			activeItem.getController().stopEdit();
			unselect(activeItem.getGeo());
		}
		// repaintView();
	}

	/**
	 * Reset active item to null
	 */
	public void clearActiveItem() {
		activeItem = null;
		// repaintView();
	}

	private void unselect(GeoElement geo) {
		if (geo == null) {
			return;
		}
		TreeItem node = nodeTable.get(geo);
		RadioTreeItem.as(node).selectItem(false);
		selectRow(geo, false);
	}

	@Override
	public void resetItems(boolean unselectAll) {
		MinMaxPanel.closeMinMaxPanel();

		cancelEditItem();
		stopCurrentEditor();
		restoreWidth(false);
	}

	/**
	 * @return selection controller
	 */
	public AVSelectionController getSelectionCtrl() {
		return selectionCtrl;
	}

	/**
	 * Gets the original width before AV expansion to restore original width
	 * after.
	 *
	 * @return original width in pixels
	 */
	public Integer getOriginalWidth() {
		return originalWidth;
	}

	/**
	 * @param oldWidth
	 *            original width in pixels
	 */
	public void setOriginalWidth(Integer oldWidth) {
		this.originalWidth = oldWidth;
	}

	@Override
	public boolean isAttachedToKernel() {
		return attached;
	}

	private static void addLeaf(HasTreeItems printItem, RadioTreeItem leaf) {
		RadioTreeItem printLeaf = leaf.copy();
		printItem.addItem(printLeaf);

		printLeaf.repaint();
	}

	@Override
	public void getPrintable(FlowPanel pPanel, final Button btPrint) {
		Tree printTree = new Tree();

		pPanel.clear();

		DrawEquationW.setPrintScale(10);

		for (int i = 0; i < this.getItemCount(); i++) {
			TreeItem item = this.getItem(i);

			if (item instanceof RadioTreeItem) {
				addLeaf(printTree, (RadioTreeItem) item);
			} else if (item != inputPanelTreeItem) {

				TreeItem printItem = new TreeItem();
				printItem.setText(item.getText());
				for (int j = 0; j < item.getChildCount(); j++) {
					TreeItem leaf = item.getChild(j);
					if (leaf instanceof RadioTreeItem) {
						addLeaf(printItem, (RadioTreeItem) leaf);
					}
				}

				printItem.setState(true);
				printTree.addItem(printItem);
			}
		}

		DrawEquationW.setPrintScale(1);

		pPanel.add(printTree);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				btPrint.setEnabled(true);
			}
		});
	}

	/**
	 * The full width of this view: either the widest item or current offset
	 * width
	 *
	 * @return the max item width.
	 */
	public int getFullWidth() {
		int avWidth = getAlgebraDockPanel().getInnerWidth();
		if (app.isUnbundled()) {
			return avWidth;
		}
		return maxItemWidth < avWidth ? avWidth : maxItemWidth;
	}

	/**
	 * Algebra View is expanded to the item's width when editing.
	 *
	 * @param width
	 *            The width to expand.
	 */
	public void expandWidth(int width) {
		if (app.isUnbundled()) {
			return;
		}

		AlgebraPanelInterface avDockPanel = getAlgebraDockPanel();
		DockSplitPaneW splitPane = avDockPanel.getParentSplitPane();
		if (splitPane == null
				|| splitPane.getOrientation() == SwingConstants.VERTICAL_SPLIT
				|| splitPane.isCenter(avDockPanel)) {
			return;
		}

		splitPane.setWidgetSize(avDockPanel.asWidget(), width);
		avDockPanel.deferredOnResize();
	}

	/**
	 * Restores AV original size before editing, if it has been expanded.
	 *
	 * @param force
	 *            whether to force this over the canceling timer
	 */
	public void restoreWidth(boolean force) {
		if (!force && CancelEventTimer.cancelAVRestoreWidth()) {
			return;
		}

		if (app.isUnbundled()) {
			return;
		}
		int w = userWidth;
		AlgebraPanelInterface avDockPanel = getAlgebraDockPanel();
		DockSplitPaneW avParent = getAlgebraDockPanel().getParentSplitPane();
		if (avParent == null || userWidth == 0
				|| avParent.getOrientation() == SwingConstants.VERTICAL_SPLIT) {
			return;
		}

		// normally the "center" orientation should be handled by the
		// VERTICAL_SPLIT check above
		if (!avParent.isCenter(avDockPanel)) {
			avParent.setWidgetSize(avDockPanel.asWidget(), w);
			avDockPanel.deferredOnResize();
		}
	}

	/**
	 * @return algebra dock panel
	 */
	AlgebraPanelInterface getAlgebraDockPanel() {
		return (AlgebraPanelInterface) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);
	}

	/**
	 * @return whether active element is the input row
	 */
	public boolean isInputActive() {
		return activeItem == inputPanelLatex;
	}

	/**
	 * Flag to determine whether we can render elements as LaTeX synchronously.
	 * When false, render as text temporarily.
	 *
	 * @return whether LaTeX is already loaded
	 */
	public boolean isLaTeXLoaded() {
		return latexLoaded;
	}

	/**
	 * Notify that LaTeX was loaded
	 */
	public void setLaTeXLoaded() {
		latexLoaded = true;
	}

	/**
	 * Gets the TreeItem associated with the geo.
	 *
	 * @param geo
	 *            the element to look for.
	 * @return the TreeItem with the geo.
	 */
	public RadioTreeItem getNode(GeoElement geo) {
		return nodeTable.get(geo);
	}

	/**
	 * @return width determined by user resizing
	 */
	public int getUserWidth() {
		return userWidth;
	}

	/**
	 * @param userWidth
	 *            width determined by user resizing
	 */
	public void setUserWidth(int userWidth) {
		MinMaxPanel.closeMinMaxPanel();
		this.userWidth = userWidth;
	}

	/**
	 * Set AV width to current offset width or default if not attached
	 */
	public void setDefaultUserWidth() {
		int w = getOffsetWidth();
		setUserWidth(w > 0 ? w : getDefaultAVWidth());
	}

	private int getDefaultAVWidth() {
		return (int) (app.getWidth()
				* PerspectiveDecoder.landscapeRatio(app, app.getWidth()));
	}

	/**
	 * Open settings menu for geo.
	 *
	 * @param geo
	 *            to open settings for.
	 */
	public void openMenuFor(GeoElement geo) {
		RadioTreeItem ti = nodeTable.get(geo);
		if (ti != null) {
			ti.openMoreMenu();
		}
	}

	/**
	 * @return factory that creates slider/checkbox/text/default elements as
	 *         needed
	 */
	public ItemFactory getItemFactory() {
		return itemFactory;
	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}
}