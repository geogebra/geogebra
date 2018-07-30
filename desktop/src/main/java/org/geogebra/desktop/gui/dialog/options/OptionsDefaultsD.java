package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.dialog.PropertiesPanelD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;

/**
 * Options with the default settings of objects.
 */
public class OptionsDefaultsD
		implements OptionPanelD, TreeSelectionListener, SetLabels {

	/**
	 * An instance of the GeoGebra application.
	 */
	private AppD app;

	/**
	 * The panel with the tabs for the different properties.
	 */
	private PropertiesPanelD propPanel;

	/**
	 * A tree with the different available object types to which new default
	 * values can be assigned.
	 */
	private JTree tree;

	private DefaultTreeCellRenderer treeCellRenderer;

	/**
	 * The tree model.
	 */
	private DefaultTreeModel treeModel;

	/**
	 * The root node of the tree.
	 */
	private DefaultMutableTreeNode rootNode;

	/**
	 * Nodes for points.
	 */
	private DefaultMutableTreeNode pointsNode, pointsFreeNode, pointsDepNode,
			pointsPathNode, pointsInRegionNode, pointsComplexNode;

	private DefaultMutableTreeNode lineNode, segmentNode, vectorNode, conicNode,
			conicSectorNode, rayNode;

	private DefaultMutableTreeNode numberNode, angleNode;

	private DefaultMutableTreeNode functionNode, polygonNode, polylineNode,
			locusNode;

	private DefaultMutableTreeNode textNode, imageNode, booleanNode;

	private DefaultMutableTreeNode listNode, inequalitiesNode, functionNVarNode;

	// polyhedra, cylinders, cones etc
	private DefaultMutableTreeNode solidsNode;

	private JButton defaultsButton;

	/**
	 * The class which contains all default objects.
	 */
	private ConstructionDefaults defaults;

	private JPanel wrappedPanel;

	/**
	 * A dictionary which assigns a constant of the ConstructionsDefaults class
	 * to the tree nodes.
	 */
	private static Hashtable<DefaultMutableTreeNode, Integer> typeToNode;

	/**
	 * Construct an panel where the user can assign new values to the default
	 * objects.
	 * 
	 * @param app
	 *            application
	 */
	public OptionsDefaultsD(AppD app) {
		this.app = app;
		this.wrappedPanel = new JPanel();

		defaults = app.getKernel().getConstruction().getConstructionDefaults();

		initGUI();
		updateGUI();
	}

	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		// init the model of the tree
		initNodes();
		treeModel = new DefaultTreeModel(rootNode);

		// init the real JTree component
		tree = new JTree(treeModel);

		tree.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		tree.setRootVisible(false);
		tree.setScrollsOnExpand(true);

		// expand the point node and select the first point
		tree.expandRow(0);
		tree.setSelectionRow(1);

		tree.addTreeSelectionListener(this);

		// disable leaf icons
		treeCellRenderer = new DefaultTreeCellRenderer();
		treeCellRenderer.setLeafIcon(null);
		tree.setCellRenderer(treeCellRenderer);

		// create the properties panel
		GeoGebraColorChooser colorChooser = new GeoGebraColorChooser(app);
		propPanel = new PropertiesPanelD(app, colorChooser, true);
		propPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		propPanel.updateSelection(new Object[] { defaults
				.getDefaultGeo(ConstructionDefaults.DEFAULT_POINT_FREE) });

		// apply defaults button
		defaultsButton = new JButton();
		defaultsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyDefaults();
			}
		});

		// set the labels of the components
		setLabels();

		// define panel properties
		// setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		wrappedPanel.setLayout(new BorderLayout());

		// tree scroll pane
		tree.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 2));
		JScrollPane treeScroller = new JScrollPane(tree);
		treeScroller.setMinimumSize(new Dimension(120, 200));
		treeScroller.setBackground(tree.getBackground());
		treeScroller.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
				SystemColor.controlShadow));

		// split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treeScroller, propPanel);
		splitPane.setBorder(BorderFactory.createEmptyBorder());

		// add components
		wrappedPanel.add(splitPane, BorderLayout.CENTER);
		wrappedPanel.add(LayoutUtil.flowPanel(0, 0, 20, defaultsButton),
				BorderLayout.SOUTH);

		app.setComponentOrientation(wrappedPanel);
	}

	/**
	 * Initialize the nodes of the tree.
	 */
	private void initNodes() {
		// create the nodes for the tree
		rootNode = new DefaultMutableTreeNode();
		pointsNode = new DefaultMutableTreeNode();
		pointsFreeNode = new DefaultMutableTreeNode();
		pointsDepNode = new DefaultMutableTreeNode();
		pointsPathNode = new DefaultMutableTreeNode();
		pointsInRegionNode = new DefaultMutableTreeNode();
		pointsComplexNode = new DefaultMutableTreeNode();
		lineNode = new DefaultMutableTreeNode();
		segmentNode = new DefaultMutableTreeNode();
		rayNode = new DefaultMutableTreeNode();
		vectorNode = new DefaultMutableTreeNode();
		conicNode = new DefaultMutableTreeNode();
		conicSectorNode = new DefaultMutableTreeNode();
		functionNode = new DefaultMutableTreeNode();
		polygonNode = new DefaultMutableTreeNode();
		polylineNode = new DefaultMutableTreeNode();
		locusNode = new DefaultMutableTreeNode();
		textNode = new DefaultMutableTreeNode();
		imageNode = new DefaultMutableTreeNode();
		numberNode = new DefaultMutableTreeNode();
		angleNode = new DefaultMutableTreeNode();
		booleanNode = new DefaultMutableTreeNode();
		listNode = new DefaultMutableTreeNode();
		inequalitiesNode = new DefaultMutableTreeNode();
		functionNVarNode = new DefaultMutableTreeNode();
		solidsNode = new DefaultMutableTreeNode();
		rootNode.add(pointsNode);
		pointsNode.add(pointsFreeNode);
		pointsNode.add(pointsDepNode);
		pointsNode.add(pointsPathNode);
		pointsNode.add(pointsInRegionNode);
		pointsNode.add(pointsComplexNode);

		rootNode.add(lineNode);
		rootNode.add(segmentNode);
		rootNode.add(rayNode);
		rootNode.add(polylineNode);
		rootNode.add(vectorNode);
		rootNode.add(conicNode);
		rootNode.add(conicSectorNode);
		rootNode.add(functionNode);
		rootNode.add(functionNVarNode);
		rootNode.add(solidsNode);
		rootNode.add(polygonNode);
		rootNode.add(locusNode);
		rootNode.add(textNode);
		rootNode.add(imageNode);
		rootNode.add(numberNode);
		rootNode.add(angleNode);
		rootNode.add(booleanNode);
		rootNode.add(listNode);
		rootNode.add(inequalitiesNode);

		// create the dictionary which is used to assign a type constant (int)
		// to
		// every tree leaf
		createDefaultMap();
	}

	/**
	 * Creates the dictionary which is used to assign a type constant (int) to
	 * every tree leaf
	 */
	private void createDefaultMap() {

		typeToNode = new Hashtable<>(15);
		typeToNode.put(pointsFreeNode, ConstructionDefaults.DEFAULT_POINT_FREE);
		typeToNode.put(pointsDepNode,
				ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		typeToNode.put(pointsPathNode,
				ConstructionDefaults.DEFAULT_POINT_ON_PATH);
		typeToNode.put(pointsInRegionNode,
				ConstructionDefaults.DEFAULT_POINT_IN_REGION);
		typeToNode.put(pointsComplexNode,
				ConstructionDefaults.DEFAULT_POINT_COMPLEX);
		typeToNode.put(lineNode, ConstructionDefaults.DEFAULT_LINE);
		typeToNode.put(segmentNode, ConstructionDefaults.DEFAULT_SEGMENT);
		typeToNode.put(rayNode, ConstructionDefaults.DEFAULT_RAY);
		typeToNode.put(vectorNode, ConstructionDefaults.DEFAULT_VECTOR);
		typeToNode.put(conicNode, ConstructionDefaults.DEFAULT_CONIC);
		typeToNode.put(conicSectorNode,
				ConstructionDefaults.DEFAULT_CONIC_SECTOR);
		typeToNode.put(functionNode, ConstructionDefaults.DEFAULT_FUNCTION);
		typeToNode.put(functionNVarNode,
				ConstructionDefaults.DEFAULT_FUNCTION_NVAR);
		typeToNode.put(solidsNode,
				ConstructionDefaults.DEFAULT_POLYHEDRON);
		typeToNode.put(polygonNode, ConstructionDefaults.DEFAULT_POLYGON);
		typeToNode.put(polylineNode, ConstructionDefaults.DEFAULT_POLYLINE);
		typeToNode.put(locusNode, ConstructionDefaults.DEFAULT_LOCUS);
		typeToNode.put(textNode, ConstructionDefaults.DEFAULT_TEXT);
		typeToNode.put(imageNode, ConstructionDefaults.DEFAULT_IMAGE);
		typeToNode.put(numberNode, ConstructionDefaults.DEFAULT_NUMBER);
		typeToNode.put(angleNode, ConstructionDefaults.DEFAULT_ANGLE);
		typeToNode.put(booleanNode, ConstructionDefaults.DEFAULT_BOOLEAN);
		typeToNode.put(listNode, ConstructionDefaults.DEFAULT_LIST);
		typeToNode.put(inequalitiesNode,
				ConstructionDefaults.DEFAULT_INEQUALITY);

	}

	/**
	 * Restores all defaults to the current ConstructionDefault values.
	 */
	public void restoreDefaults() {
		createDefaultMap();

		// update panel selection ... this should trigger the valueChanged
		// method
		TreePath path = tree.getSelectionPath();
		tree.setSelectionPath(null);
		tree.setSelectionPath(path);

		// but if not, make sure that it is called certainly
		// defaults =
		// app.getKernel().getConstruction().getConstructionDefaults();
		// valueChanged(null);
	}

	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	@Override
	public void updateGUI() {
		SwingUtilities.updateComponentTreeUI(this.wrappedPanel);
	}

	/**
	 * Update the labels of the current panel. Should be applied if the language
	 * was changed.
	 */
	@Override
	public void setLabels() {
		// update tree labels
		Localization loc = app.getLocalization();
		pointsNode.setUserObject(loc.getMenu("Point"));
		pointsFreeNode.setUserObject(loc.getMenu("PointFree"));
		pointsDepNode.setUserObject(loc.getMenu("PointDep"));
		pointsPathNode.setUserObject(loc.getMenu("PointOnPath"));
		pointsInRegionNode.setUserObject(loc.getMenu("PointInside"));
		pointsComplexNode.setUserObject(loc.getMenu("ComplexNumber"));
		lineNode.setUserObject(loc.getMenu("Line"));
		segmentNode.setUserObject(loc.getMenu("Segment"));
		rayNode.setUserObject(loc.getMenu("Ray"));
		vectorNode.setUserObject(loc.getMenu("Vector"));
		conicNode.setUserObject(loc.getMenu("Conic"));
		conicSectorNode.setUserObject(loc.getMenu("Sector"));
		functionNode.setUserObject(loc.getMenu("Function"));
		functionNVarNode.setUserObject(loc.getMenu("MultivariableFunction"));
		solidsNode.setUserObject(loc.getMenu("Solids"));
		polygonNode.setUserObject(loc.getMenu("Polygon"));
		polylineNode.setUserObject(loc.getMenu("PolyLine"));
		locusNode.setUserObject(loc.getMenu("Locus"));
		textNode.setUserObject(loc.getMenu("Text"));
		imageNode.setUserObject(loc.getMenu("Image"));
		numberNode.setUserObject(loc.getMenu("Slider"));
		angleNode.setUserObject(loc.getMenu("Angle"));
		booleanNode.setUserObject(loc.getMenu("Boolean"));
		listNode.setUserObject(loc.getMenu("List"));
		inequalitiesNode.setUserObject(loc.getMenu("Inequality"));

		defaultsButton.setText(loc.getMenu("ApplyToSelectedObjects"));

		propPanel.setLabels();
	}

	/**
	 * The selection has changed.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null || node == rootNode) {
			propPanel.setVisible(false);
		} else {
			if (!propPanel.isVisible()) {
				propPanel.setVisible(true);
			}

			if (node == pointsNode) {
				Object[] selection = new Object[pointsNode.getChildCount()];

				for (int i = 0; i < pointsNode.getChildCount(); ++i) {
					selection[i] = defaults.getDefaultGeo(
							typeToNode.get(pointsNode.getChildAt(i)));
				}

				propPanel.updateSelection(selection);
			} else {
				if (typeToNode.containsKey(node)) {
					propPanel.updateSelection(new Object[] {
							defaults.getDefaultGeo(typeToNode.get(node)) });
				}
			}
		}
	}

	/**
	 * Save the settings of this panel.
	 */
	@Override
	public void applyModifications() {
		propPanel.applyModifications();
	}

	@Override
	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	@Override
	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	@Override
	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}

	/**
	 * Reset the visual style of the selected elements.
	 * 
	 * TODO Does not work with lists (F.S.)
	 */
	private void applyDefaults() {

		for (GeoElement geo : app.getSelectionManager().getSelectedGeos()) {
			defaults.setDefaultVisualStyles(geo, true);
			geo.updateRepaint();
		}

	}

	@Override
	public void updateFont() {
		Font font = app.getPlainFont();

		tree.setFont(font);
		treeCellRenderer.setFont(font);

		defaultsButton.setFont(font);

		// propPanel.setFont(font);
	}

	@Override
	public void setSelected(boolean flag) {
		// see OptionsEuclidianD for possible implementation
	}
}
