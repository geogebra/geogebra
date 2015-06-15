package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.algebra.AlgebraController;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LayerView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TimerSystemW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.inputbar.AlgebraInputW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AlgebraViewW extends Tree implements LayerView,
		AlgebraView, OpenHandler<TreeItem>, SettingListener {
	/**
	 * Flag for LaTeX rendering
	 */
	final private static boolean renderLaTeX = true;
	protected final AppW app; // parent appame
	protected final Localization loc;
	protected final Kernel kernel;
	private AnimationScheduler repaintScheduler = AnimationScheduler.get();
	protected AlgebraInputW inputPanel;
	NewRadioButtonTreeItem inputPanelLatex;
	private AlgebraStyleBarW styleBar;
	public boolean editing = false;
	private GeoElement draggedGeo;

	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler.AnimationCallback specialRepaintCallback = new AnimationScheduler.AnimationCallback() {
		public void execute(double ts) {
			doRepaint3();
		}
	};

	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	protected SortMode treeMode = SortMode.TYPE;

	private boolean showAuxiliaryObjectsSettings = false;

	private boolean settingsChanged = false;

	/**
	 * Nodes for tree mode MODE_DEPENDENCY
	 */
	private TreeItem depNode, indNode;
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
	private GeoElement lastSelectedGeo = null;
	protected HashMap<GeoElement, TreeItem> nodeTable = new HashMap<GeoElement, TreeItem>(500);
	private int waitForRepaint = TimerSystemW.SLEEPING_FLAG;
	private StringBuilder sbXML;

	private RadioButtonTreeItem activeItem;

	private GeoElement selectedGeoElement;
	private TreeItem selectedNode;

	// private AlgebraHelperBar helperBar;

	private AlgebraController algebraController;

	public AlgebraController getAlgebraController() {
		return algebraController;
	}
	 
	public AlgebraViewW(AlgebraController algCtrl) {
		super(new TreeImages());
		App.debug("creating Algebra View");
		this.algebraController = algCtrl;
		this.app = (AppW) algCtrl.getApplication();
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.addOpenHandler(this);
		algCtrl.setView(this);
		initGUI((AlgebraControllerW) algCtrl);
	}

	private void initGUI(AlgebraControllerW algCtrl) {
		// add listener
		this.addDomHandler(algCtrl, MouseDownEvent.getType());
		this.addDomHandler(algCtrl,
				TouchStartEvent.getType());
		this.addDomHandler(algCtrl, TouchEndEvent.getType());
		this.addDomHandler(algCtrl, TouchMoveEvent.getType());

		// initializes the tree model, important to set tree mode first to avoid
		// inf. loop #3651
		this.treeMode = intToMode(app.getSettings().getAlgebra().getTreeMode());
		initModel();

		setLabels();

		getElement().setId("View_" + App.VIEW_ALGEBRA);

		this.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		this.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		this.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

		this.setFocus(true);

		// Initialize settings and register listener
		app.getSettings().getAlgebra().addListener(this);

		settingsChanged(app.getSettings().getAlgebra());

	}

	@Override
	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() == Event.ONBLUR) {
			setActiveTreeItem(null);
		}

		// as arrow keys are prevented in super.onBrowserEvent,
		// we need to handle arrow key events before that
		switch (DOM.eventGetType(event)) {
		case Event.ONKEYUP:
			switch (DOM.eventGetKeyCode(event)) {
			case KeyCodes.KEY_UP:
			case KeyCodes.KEY_DOWN:
			case KeyCodes.KEY_LEFT:
			case KeyCodes.KEY_RIGHT:
				// this may be enough for Safari too, because it is not
				// onkeypress
				if (!editing) {
					app.getGlobalKeyDispatcher().handleSelectedGeosKeysNative(
							event);
					event.stopPropagation();
					event.preventDefault();
					return;
				}
			}
		case Event.ONMOUSEDOWN:
		case Event.ONTOUCHSTART:
			app.closePopups();
			app.focusGained(AlgebraViewW.this);
		}
		if (!editing
				&& (this.inputPanel == null || !this.inputPanel.hasFocus())) {
			if (event.getTypeInt() == Event.ONCLICK) {
				// background click
				if (!CancelEventTimer.cancelKeyboardHide()
						&& !CancelEventTimer.cancelMouseEvent()) {
					app.hideKeyboard();
				}
			}
			super.onBrowserEvent(event);
		}
	}

    /**
	 * schedule a repaint
	 */
	public void doRepaint() {		
		repaintScheduler.requestAnimationFrame(repaintCallback);
	}
	
	public void doSpecialRepaint() {
		repaintScheduler.requestAnimationFrame(specialRepaintCallback);
	}

	/**
	 * timer system suggests a repaint
	 */
	public boolean suggestRepaint(){

		if (app.has(Feature.AV_EXTENSIONS)
				&& RadioButtonTreeItem.showSliderOrTextBox
				&& waitForRepaint != TimerSystemW.REPAINT_FLAG) {
			doSpecialRepaint();
		}

		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG){
			return false;
		}

		if (waitForRepaint == TimerSystemW.REPAINT_FLAG){
			if (isShowing()){
				doRepaint();	
				waitForRepaint = TimerSystemW.SLEEPING_FLAG;	
			}
			return true;
		}

		waitForRepaint--;
		return true;
	}
	
	

	public final void repaintView() {
		app.ensureTimerRunning();
		if (waitForRepaint == TimerSystemW.SLEEPING_FLAG){
    		waitForRepaint = TimerSystemW.ALGEBRA_LOOPS;
    	}
	}
	
	/**
	 * Make sure we repaint all updated objects in nodes that were collapsed before
	 */
	public void onOpen(OpenEvent<TreeItem> event){
		this.doRepaint();
	}

	public final int getViewID() {
		return App.VIEW_ALGEBRA;
	}

	// TODO EuclidianView#setHighlighted() doesn't exist
	/**
	 * updates node of GeoElement geo (needed for highlighting)
	 * 
	 * @see EuclidianView#setHighlighted()
	 */
	public void update(GeoElement geo) {
		long start = System.currentTimeMillis();
		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			
			
							
				((RadioButtonTreeItem) node.getWidget()).updateOnNextRepaint();
				repaintView();
			
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151).
			 */
			if (isEditing()) {
				if (((RadioButtonTreeItem) node.getWidget()).isThisEdited()) {
					((RadioButtonTreeItem) node.getWidget()).cancelEditing();
				}
			}
		}
		GeoGebraProfiler.addAlgebra(System.currentTimeMillis()-start);
	}

	/**
	 * Only call this method if you really know what you're doing. Otherwise
	 * call repaint() instead.
	 */
	public void doRepaint2() {
		Object geo;
		// suppose that the add operations have been already done elsewhere
		for (int i = 0; i < getItemCount(); i++) {
			TreeItem ti = getItem(i);
			geo = getItem(i).getUserObject();
			if (geo instanceof GeoElement) {
				((RadioButtonTreeItem) ti.getWidget()).repaint();
				ti.setSelected(((GeoElement) geo).doHighlighting());
			} else if (ti.getWidget() instanceof GroupHeader) {				
				((GroupHeader) ti.getWidget()).setText(ti.getUserObject().toString());
				if (ti.getState()) {
					repaintChildren(ti);
				}

			}
		}
	}

	/**
	 * updates only GeoNumerics; used for animated
	 */
	protected void doRepaint3() {
		Object geo;
		for (int i = 0; i < getItemCount(); i++) {
			TreeItem ti = getItem(i);
			if (ti != null) {
				for (int j = 0; j < ti.getChildCount(); j++) {
					geo = ti.getChild(j).getUserObject();
					if (geo instanceof GeoNumeric) {
						((RadioButtonTreeItem) ti.getChild(j).getWidget())
								.repaint();

						// TODO needed?
						ti.setSelected(((GeoElement) geo).doHighlighting());
					}
				}
			}
		}
	}

	private static void repaintChildren(TreeItem item) {
		for (int j = 0; j < item.getChildCount(); j++) {
			if (item.getChild(j).getWidget() instanceof RadioButtonTreeItem) {
				((RadioButtonTreeItem) item.getChild(j).getWidget()).repaint();
			}
			Object geo = item.getChild(j).getUserObject();
			if (geo instanceof GeoElement)
				item.getChild(j).setSelected(
				        ((GeoElement) geo).doHighlighting());
		}
	}

	private void updateCollapsedNodesIndices() {

		// no collapsed nodes
		if (getTreeMode() == SortMode.ORDER) {
			collapsedNodes = null;
			return;
		}

		if (collapsedNodes == null)
			collapsedNodes = new ArrayList<Integer>();
		else
			collapsedNodes.clear();

		for (int i = 0; i < getItemCount(); i++) {
			TreeItem node = getItem(i);
			if (!node.getState())
				collapsedNodes.add(i);
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
	 * @return int value for tree mode (used in XML)
	 */
	public int getTreeModeValue() {
		switch (getTreeMode()) {
		case DEPENDENCY:
			return 0;
		case TYPE:
		default:
			return 1;
		case LAYER:
			return 2;
		case ORDER:
			return 3;
		}
	}

	public void setTreeMode(int mode) {
		setTreeMode(intToMode(mode));
	}
	
	public static SortMode intToMode(int mode){
		switch (mode) {
		case 0:
			return SortMode.DEPENDENCY;
		case 1:
			return SortMode.TYPE;
		case 2:
			return SortMode.LAYER;
		case 3:
			return SortMode.ORDER;
		}
		return SortMode.TYPE;
	}

	/**
	 * returns settings in XML format
	 */
	public final void getXML(StringBuilder sb) {

		if (sbXML == null)
			sbXML = new StringBuilder();
		else
			sbXML.setLength(0);

		// tree mode
		if (getTreeMode() != SortMode.TYPE) {
			sbXML.append("\t<mode ");
			sbXML.append("val=\"");
			sbXML.append(getTreeModeValue());
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

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

	private ArrayList<Integer> collapsedNodes;

	private void setCollapsedNodes(int[] collapsedNodes) {
		if (collapsedNodes == null)
			return;

		if (this.collapsedNodes == null)
			this.collapsedNodes = new ArrayList<Integer>();
		else
			this.collapsedNodes.clear();

		for (int i = 0; i < collapsedNodes.length; i++)
			this.collapsedNodes.add(collapsedNodes[i]);
	}

	public boolean showAuxiliaryObjects() {
		return app.showAuxiliaryObjects;
	}

	public void setShowAuxiliaryObjects(boolean flag) {

		app.showAuxiliaryObjects = flag;

		cancelEditing();

		if (flag) {
			clearView();

			switch (getTreeMode()) {
			case DEPENDENCY:
				addItem(auxiliaryNode);
				break;
			}
			kernel.notifyAddAll(this);
		} else {
			// if we're listing the auxiliary objects in a single leaf we can
			// just remove that leaf, but for type-based categorization those
			// auxiliary nodes might be scattered across the whole tree,
			// therefore we just rebuild the tree
			switch (getTreeMode()) {
			case DEPENDENCY:
				if (auxiliaryNode.getParentItem() != null) {
					removeItem(auxiliaryNode);
				}
				break;
			default:

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

		attachView(); // TODO implement applyPerspective correctly

		// collapsed nodes
		if (collapsedNodes == null)
			return;

		for (int i : collapsedNodes) {
			TreeItem node = getItem(i);
			if(node != null){
				node.setState(false);
			}
		}

	}

	public void settingsChanged(AbstractSettings settings) {

		AlgebraSettings algebraSettings = (AlgebraSettings) settings;
		setTreeMode(algebraSettings.getTreeMode());
		showAuxiliaryObjectsSettings = algebraSettings
		        .getShowAuxiliaryObjects();
		setCollapsedNodes(algebraSettings.getCollapsedNodes());

		settingsChanged = true;

	}

	protected boolean attached = false;
	private TreeItem dummy;

	public void attachView() {

		if (attached)
			return;
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
		case DEPENDENCY:
			// don't re-init anything
			if (depNode == null || indNode == null || auxiliaryNode == null) {
				// rootDependency = new TreeItem();
				depNode = new TreeItem(); // dependent objects
				indNode = new TreeItem();
				auxiliaryNode = new TreeItem();

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
			break;
		case ORDER:
			if (rootOrder == null) {
				// both rootOrder and AlgebraView will have the Tree items
				rootOrder = new TreeItem();
			}
			setUserObject(rootOrder, "");

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
			break;

		case TYPE:
			// don't re-init anything
			if (rootType == null) {
				rootType = new TreeItem();
				// setUserObject(rootType, "");
				typeNodesMap = new HashMap<String, TreeItem>(5);
				
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
			break;
		case LAYER:
			// don't re-init anything
			if (rootLayer == null) {
				rootLayer = new TreeItem();
				layerNodesMap = new HashMap<Integer, TreeItem>(10);
			}

			// always try to remove the auxiliary node
			if (app.showAuxiliaryObjects && auxiliaryNode != null) {
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			// addItem(rootLayer);
			break;
		}

	}

	/**
	 * remove all from the tree
	 */
	protected void clearTree() {
		switch (getTreeMode()) {
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
			setUserObject(indNode, loc.getPlain("FreeObjects"));
			setUserObject(depNode, loc.getPlain("DependentObjects"));
			setUserObject(auxiliaryNode, loc.getPlain("AuxiliaryObjects"));
			break;
		case TYPE:
			for (String key : typeNodesMap.keySet()) {
				node = typeNodesMap.get(key);
				setUserObject(node, loc.getPlain(key));
			}
			break;
		case LAYER:
			for (Integer key : layerNodesMap.keySet()) {
				node = layerNodesMap.get(key);
				setUserObject(node, loc.getPlain("LayerA", key.toString())
				        + "TODO" + key);
			}
			break;
		case ORDER:
			break;
		}
	}

	/**
	 * 
	 * @param geo
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
				parent = new TreeItem(new InlineLabel(transTypeString));
				setUserObject(parent, transTypeString);
				typeNodesMap.put(typeString, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++) {
					TreeItem child = getItem(i);
					if (transTypeString.compareTo(child.toString()) < 0 || 
									(child.getWidget() != null
					                && this.inputPanel != null
					                && this.inputPanelTreeItem != null
					                && this.inputPanelTreeItem.getWidget() != null
					                && child.getWidget().equals(this.inputPanelTreeItem.getWidget()))) {
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
				parent = new TreeItem(SafeHtmlUtils.fromString(layerStr));

				setUserObject(parent, layerStr);

				layerNodesMap.put(layer, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++) {
					TreeItem child = getItem(i);
					if (layerStr.compareTo(child.toString()) < 0) {
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
	
	/**
	 * Assign element or element group to a given tree node
	 * 
	 * @param ti
	 *            tree item
	 * @param ob
	 *            object
	 */
	public final void setUserObject(TreeItem ti, final Object ob) {
		ti.setUserObject(ob);
		if (ob instanceof GeoElement) {
			ti.setWidget(RadioButtonTreeItem.create((GeoElement) ob,
					AppResources.INSTANCE.shown().getSafeUri(),
					AppResources.INSTANCE.hidden().getSafeUri()));
			ti.addStyleName("avItem");
			// ti.getElement().getStyle().setPadding(0, Unit.PX);

			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
					.getElement().getFirstChildElement(), "display",
					"-moz-inline-box");
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
					.getElement().getFirstChildElement(), "display",
					"inline-block");
		} else {
			ti.setWidget(new GroupHeader(this.app.getSelectionManager(), ti, ob
					.toString(), GuiResources.INSTANCE.algebra_tree_open()
					.getSafeUri(), GuiResources.INSTANCE.algebra_tree_closed()
					.getSafeUri()));
		}
	}
	
	/**
	 * Remove this node from the model.
	 * 
	 * @param node
	 * @param model
	 */
	private void removeFromModel(TreeItem node) {
		node.remove();
		nodeTable.remove(node.getUserObject());

		// remove the type branch if there are no more children
		switch (treeMode) {
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

	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {
		add(geo, -1);
	}

	private void add(GeoElement geo, int forceLayer) {
		cancelEditing();
		

		if (geo.isLabelSet() && geo.showInAlgebraView()
		        && geo.isSetAlgebraVisible()) {
			if(this.dummy != null){
				removeItem(this.dummy);
				this.dummy = null;
			}
			// don't add auxiliary objects if the tree is categorized by type
			if (!getTreeMode().equals(SortMode.DEPENDENCY)
			        && !showAuxiliaryObjects() && geo.isAuxiliaryObject()) {
				return;
			}

			TreeItem parent, node;
			node = new TreeItem();

			parent = getParentNode(geo, forceLayer);

			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo, treeMode);

			if (pos == parent.getChildCount()) {
				parent.addItem(node);
				if (parent.equals(rootOrder))
					addItem(node);
			} else
				try {
					parent.insertItem(pos, node);
					if (parent.equals(rootOrder))
						insertItem(pos, node);
				} catch (IndexOutOfBoundsException e) {
					parent.addItem(node);
					if (parent.equals(rootOrder))
						addItem(node);
				}

			setUserObject(node, geo);

			// item is already added
			if (node.getWidget() instanceof RadioButtonTreeItem
					&& !(node.getWidget() instanceof NewRadioButtonTreeItem)) {
				((RadioButtonTreeItem) node.getWidget())
						.replaceXButtonDOM(node);
			}

			if (isNodeTableEmpty()) {
				// this is for the case "add" is called after
				// the input panel exists; the other case
				// is done elsewhere, when it is created...

				// if adding new elements the first time,
				// let's show the X signs in the input bar!
				if (this.inputPanelLatex != null) {
					this.inputPanelLatex.updateGUIfocus(inputPanelLatex, false);
				}
				else if (this.inputPanel != null
						&& this.inputPanel.getElement() != null) {
					this.inputPanel.onFocus(null);
				}
			}
			nodeTable.put(geo, node);
			
			// ensure that the leaf with the new object is visible
			parent.setState(true);
		}
	}

	public void changeLayer(GeoElement g, int oldLayer, int newLayer) {
		if (this.treeMode.equals(SortMode.LAYER)) {
			TreeItem node = nodeTable.get(g);

			if (node != null) {
				node.remove();
				nodeTable.remove(node.getUserObject());
				removeFromLayer(oldLayer);
			}

			this.add(g, newLayer);

		}
	}

	/**
	 * removes a node from the tree
	 */
	public void remove(GeoElement geo) {
		cancelEditing();
		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			removeFromModel(node);
		}
	}


	public void clearView() {
		nodeTable.clear();
		clearTree();
		showAlgebraInput(false);
	}

	/**
	 * renames an element and sorts list
	 */
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	protected void removeAuxiliaryNode() {
		removeItem(auxiliaryNode);
	}

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of
	 * GeoElement as user objects.
	 * 
	 * @param mode
	 */
	final public static int getInsertPosition(TreeItem parent,
	        GeoElement newGeo, SortMode mode) {
		// label of inserted geo
		// String newLabel = newGeo.getLabel();

		// standard case: binary search
		int left = 0;
		int right = parent.getChildCount();
		if (right == 0)
			return right;

		// bigger then last?
		TreeItem node = parent.getChild(parent.getChildCount() - 1);
		// String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
		GeoElement geo2 = ((GeoElement) node.getUserObject());
		if (compare(newGeo, geo2, mode))
			return right;

		// binary search
		while (right > left) {
			int middle = (left + right) / 2;
			node = parent.getChild(middle);
			// nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
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

	/**
	 * Performs a binary search for geo among the children of parent. All
	 * children of parent have to be instances of GeoElement sorted
	 * alphabetically by their names.
	 * 
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(TreeItem parent, String geoLabel) {
		int left = 0;
		int right = parent.getChildCount() - 1;
		if (right == -1 || geoLabel == null)
			return -1;

		// binary search for geo's label
		while (left <= right) {
			int middle = (left + right) / 2;
			TreeItem node = parent.getChild(middle);
			String nodeLabel = ((GeoElement) node.getUserObject())
			        .getLabelSimple();

			int compare = GeoElement.compareLabels(geoLabel, nodeLabel);
			if (compare < 0)
				right = middle - 1;
			else if (compare > 0)
				left = middle + 1;
			else
				return middle;
		}

		return -1;
	}

	/**
	 * Performs a linear search for geo among the children of parent.
	 * 
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(TreeItem parent, String geoLabel) {
		if (geoLabel == null)
			return -1;
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TreeItem node = parent.getChild(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel(StringTemplate.defaultTemplate)))
				return i;
		}
		return -1;
	}

	/**
	 * Reset the algebra view if the mode changed.
	 */
	public void setMode(int mode, ModeSetter m) {
		reset();
	}

	private static boolean compare(GeoElement geo1, GeoElement geo2,
	        SortMode mode) {
		switch (mode) {

		case ORDER:

			return geo1.getConstructionIndex() > geo2.getConstructionIndex();

		default: // alphabetical

			return GeoElement.compareLabels(
			        geo1.getLabel(StringTemplate.defaultTemplate),
			        geo2.getLabel(StringTemplate.defaultTemplate)) > 0;

		}

	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	public void reset() {
		cancelEditing();
		repaintView();
		ensureSelectedItemVisible();
	}

	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	public void setLabels() {
		if (inputPanel != null) {
			inputPanel.setLabels();
		}
		setTreeLabels();

		if (this.styleBar != null) {
			this.styleBar.setLabels();
		}
	}
	
	public final GeoElement getLastSelectedGeo() {
	    return lastSelectedGeo;
    }

	public final void setLastSelectedGeo(GeoElement geo) {
	    lastSelectedGeo = geo;	    
    }
	
	public void startBatchUpdate() {
		// TODO Auto-generated method stub
		
	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub
		
	}

	protected TreeItem inputPanelTreeItem;

	/**
	 * @return the RadioButtonTreeItem containing the input-box
	 */
	public RadioButtonTreeItem getInputTreeItem() {
		return inputPanelLatex;
	}

	public void setInputPanel(final AlgebraInputW inputPanel){
		if (this.inputPanel != null) {
			this.inputPanel.removeFromParent();
		}

		// usually, inputPanel is here, but not in use (not attached)
		this.inputPanel = inputPanel;
		boolean forceKeyboard = false;
		if ((!app.getLocalization().getLanguage().equals("ko")
 || app
				.has(Feature.KOREAN_KEYBOARD)) && !app.getLAF().isSmart()) {
			if (inputPanelLatex == null) {
				inputPanelLatex = new NewRadioButtonTreeItem(kernel);
				forceKeyboard = app.getArticleElement()
						.getDataParamBase64String().length() == 0;

				if (!isNodeTableEmpty() && !App.isFullAppGui()) {
					AutoCompleteTextFieldW.showSymbolButtonIfExists(
							inputPanelLatex, true);
				}
			} else {
				inputPanelLatex.removeFromParent();
			}
			hideAlgebraInput();
			this.inputPanelTreeItem = new TreeItem(inputPanelLatex);
			inputPanelLatex.getElement().getParentElement()
			        .addClassName("NewRadioButtonTreeItemParent");
			inputPanelLatex.replaceXButtonDOM();
		}
		if(inputPanel != null){
			//make sure we do not trigger long touch here
			inputPanel.getTextField().addDomHandler(new TouchStartHandler(){
				// TODO: maybe use CancelEvents.instance?
				@Override
                public void onTouchStart(TouchStartEvent event) {
	               event.stopPropagation();
	                
                }}, TouchStartEvent.getType());
			inputPanel.getTextField().addDomHandler(new MouseDownHandler(){

				@Override
                public void onMouseDown(MouseDownEvent event) {
					// event.stopPropagation();
				}
			}, MouseDownEvent.getType());
		}
		showAlgebraInput(forceKeyboard);
	}

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

	private void showAlgebraInput(boolean forceKeyboard) {
		if (inputPanel == null || !app.showAlgebraInput()) {
			hideAlgebraInput();
			return;
		}
		if (isAlgebraInputVisible()) {
			// note that hideAlgebraInput just does this
			// hideAlgebraInput();
			// except it also makes this null, no problem
			// ... or? still preferring to be safe
			super.removeItem(inputPanelTreeItem);

			// inputPanel.removeFromParent();//?
		}

		if(this.app.getInputPosition() == InputPositon.algebraView){
			if ((!app.getLocalization().getLanguage().equals("ko")
 || app
					.has(Feature.KOREAN_KEYBOARD)) && !app.getLAF().isSmart()) {
				if (inputPanelLatex == null) {
					inputPanelLatex = new NewRadioButtonTreeItem(kernel);

					// open the keyboard (or show the keyboard-open-button) at
					// when the application is started

					if (!isNodeTableEmpty() && !App.isFullAppGui()) {
						AutoCompleteTextFieldW.showSymbolButtonIfExists(
								inputPanelLatex, true);
					}
				} else {
					inputPanelLatex.removeFromParent();
				}
				if (forceKeyboard) {
					Scheduler.get().scheduleDeferred(
							new Scheduler.ScheduledCommand() {
								public void execute() {
									app.showKeyboard(inputPanelLatex, true);
								}
							});
				}
				inputPanelTreeItem = super.addItem(inputPanelLatex);
				// inputPanelTreeItem.addStyleName("NewRadioButtonTreeItemParent");
				inputPanelLatex.getElement().getParentElement()
				        .addClassName("NewRadioButtonTreeItemParent");
				inputPanelLatex.replaceXButtonDOM();
			} else {
				inputPanelTreeItem = super.addItem(inputPanel.getTextField());
			}
		}
	}
	
	private boolean isAlgebraInputVisible() {
		return inputPanelTreeItem != null;
	}

	@Override
	public void addItem(TreeItem item) {
		// make sure the item is inserted before the inputPanel
		if(isAlgebraInputVisible()){
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
	 * @return true if {@link #nodeTable} is empty
	 */
	public boolean isNodeTableEmpty() {
		 return this.nodeTable.isEmpty();
	}

	public void setActiveTreeItem(RadioButtonTreeItem radioButtonTreeItem) {
		if (!app.has(Feature.AV_EXTENSIONS)) {
			return;
		}

		if ((this.activeItem != null)
				&& (this.activeItem != radioButtonTreeItem)
				&& (!this.activeItem.isThisEdited())) {
			// e.g. if setting it null, if this is edited,
			// then the close button should still not be removed!
			this.activeItem.removeCloseButton();
		}
		this.activeItem = radioButtonTreeItem;
	}

	/**
	 * @return {@link AlgebraStyleBarW}
	 */
	public AlgebraStyleBarW getStyleBar(boolean mayCreate) {
		if (mayCreate && this.styleBar == null) {
			this.styleBar = new AlgebraStyleBarW(app);
		}
		return this.styleBar;
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		update(geo);
		if (styleBar != null) {
			styleBar.update(geo);
		}
	}

	public boolean isShowing() {
		return isVisible() && isAttached();
	}

	public void cancelEditing() {
		editing = false;
		setAnimationEnabled(true);
	}

	public boolean isEditing() {
		return editing;
	}

	/**
	 * Open Editor textfield for geo.
	 */
	public void startEditing(GeoElement geo) {
		if (geo == null)
			return;

		// open Object Properties for eg GeoImages
		if (!geo.isAlgebraViewEditable()) {
			// FIXMEWEB ArrayList<GeoElement> geos = new
			// ArrayList<GeoElement>();
			// FIXMEWEB geos.add(geo);
			// FIXMEWEB app.getDialogManager().showPropertiesDialog(geos);
			return;
		}

		if (!geo.isPointOnPath() && !geo.isPointInRegion()) {
			if ((!geo.isIndependent() && !(geo.getParentAlgorithm() instanceof AlgoCurveCartesian))
					|| !attached) // needed for F2 when Algebra
			// View closed
			{
				if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}

			if (!geo.isChangeable()) {
				if (geo.isFixed()) {
					app.showMessage(loc.getError("AssignmentToFixed"));
					return;
				} else if (geo.isRedefineable()
						&& !(geo.getParentAlgorithm() instanceof AlgoCurveCartesian)) {
					app.getDialogManager().showRedefineDialog(geo, true);
					return;
				} else if (!(geo.getParentAlgorithm() instanceof AlgoCurveCartesian)) {
					return;
				}
			}
		}

		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			cancelEditing();
			// FIXMEWEB select and show node
			Widget wi = node.getWidget();
			editing = true;
			setAnimationEnabled(false);
			if (wi instanceof RadioButtonTreeItem)
				((RadioButtonTreeItem) wi).startEditing();
		}
	}

	public void clearSelection() {

		// deselecting this causes a bug; it maybe fixed
		// by changing the repaintView method too,
		// adding setSelectedItem( some TreeItem ),
		// but which TreeItem should be that if more are selected?
		// that's why Arpad choosed to comment this out instead
		// super.setSelectedItem(null);

		for (int i = 0; i < getItemCount(); i++) {
			if (!(getItem(i).getUserObject() instanceof GeoElement))
				for (int j = 0; j < getItem(i).getChildCount(); j++) {
					getItem(i).getChild(j).setSelected(false);
				}
		}
		selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}

	/**
	 * Returns true if rendering is done with LaTeX
	 * 
	 * @return
	 */
	public boolean isRenderLaTeX() {
		return renderLaTeX;
	}

	public void dragStart(DragStartEvent event, GeoElement geo) {
		setDraggedGeo(geo);
	}

	public GeoElement getDraggedGeo() {
		return draggedGeo;
	}

	public void setDraggedGeo(GeoElement draggedGeo) {
		this.draggedGeo = draggedGeo;
	}

	public boolean hasFocus() {
		App.debug("unimplemented");
		return false;
	}

	@Override
	protected void onLoad() {
		// this may be important if the view is added/removed from the DOM
		super.onLoad();
		repaintView();
	}

	/**
	 * Not used in Web so far. Will not work with JLM.
	 */
	public void updateFonts() {
		GFont font = app.getPlainFontCommon();
		getStyleElement().getStyle().setFontSize(font.getSize(), Style.Unit.PX);
	}
}
