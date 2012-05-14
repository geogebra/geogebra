/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.dialog.options;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.GeoTreeCellRenderer;
import geogebra.gui.color.GeoGebraColorChooser;
import geogebra.gui.dialog.PropertiesPanel;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Markus Hohenwarter
 */
public class OptionsObject extends JPanel implements /*TreeSelectionListener,*/ KeyListener,
		GeoElementSelectionListener, SetLabels {

	// private static final int MAX_OBJECTS_IN_TREE = 500;
	private static final int MAX_GEOS_FOR_EXPAND_ALL = 15;
	private static final int MAX_COMBOBOX_ENTRIES = 200;

	private static final long serialVersionUID = 1L;
	private Application app;
	private Kernel kernel;
	private GeoTree geoTree;
	private JButton closeButton, defaultsButton, delButton;
	private PropertiesPanel propPanel;
	private GeoGebraColorChooser colChooser;

	// stop slider increment being less than 0.00000001
	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;
	public final static int SLIDER_MAX_WIDTH = 170;

	final private static int MIN_WIDTH = 500;
	final private static int MIN_HEIGHT = 300;

	/**
	 * Creates new PropertiesDialog.
	 * 
	 * @param app
	 *            parent frame
	 */
	public OptionsObject(Application app) {
		
		this.app = app;
		kernel = app.getKernel();

		
		geoTree = new GeoTree(app);
		geoTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// some textfields are updated when they lose focus
				// give them a chance to do that before we change the selection
				// TODO 
				//requestFocusInWindow();
			}
		});
		//geoTree.addTreeSelectionListener(this);
		geoTree.addKeyListener(this);

		// build GUI
		initGUI();
	}

	/**
	 * inits GUI with labels of current language
	 */
	public void initGUI() {
		geoTree.setFont(app.getPlainFont());

		boolean wasShowing = isShowing();
		if (wasShowing) {
			setVisible(false);
		}

		// LIST PANEL
		JScrollPane listScroller = new JScrollPane(geoTree);
		listScroller.setMinimumSize(new Dimension(120, 200));
		listScroller.setBackground(geoTree.getBackground());
		listScroller.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		// delete button
		delButton = new JButton(app.getImageIcon("delete_small.gif"));
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedGeos();
			}
		});

		// apply defaults button
		defaultsButton = new JButton();
		defaultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyDefaults();
			}
		});

		closeButton = new JButton();
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		// build button panel with some buttons on the left
		// and some on the right
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
		buttonPanel.add(leftButtonPanel, BorderLayout.WEST);

		// left buttons
		if (app.letDelete())
			leftButtonPanel.add(delButton);

		leftButtonPanel.add(defaultsButton);

		// right buttons
		rightButtonPanel.add(closeButton);

		// PROPERTIES PANEL
		if (colChooser == null) {
			// init color chooser
			colChooser = new GeoGebraColorChooser(app);
		}

		// check for null added otherwise you get two listeners for the
		// colChooser
		// when a file is loaded
		if (propPanel == null) {
			propPanel = new PropertiesPanel(app, colChooser, false);
			propPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		}
		selectionChanged(); // init propPanel

		// put it all together
		this.removeAll();
		// contentPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		/*
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(listScroller);
		splitPane.setRightComponent(propPanel);
		*/

		this.setLayout(new BorderLayout());
		//this.add(splitPane, BorderLayout.CENTER);
		this.add(propPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		

		

		if (wasShowing) {
			setVisible(true);
		}

		setLabels();
	}



	public PropertiesPanel getPropertiesPanel() {
		return propPanel;
	}

	public void showSliderTab() {
		if (propPanel != null)
			propPanel.showSliderTab();
	}

	/**
	 * Update the labels of this dialog.
	 * 
	 * TODO Create "Apply Defaults" phrase (F.S.)
	 */
	public void setLabels() {
		
		delButton.setText(app.getPlain("Delete"));
		closeButton.setText(app.getMenu("Close"));
		defaultsButton.setText(app.getMenu("ApplyDefaults"));

		geoTree.setLabels();
		propPanel.setLabels();
	}

	


	public void closeDialog() {
		app.getGuiManager().getPropertiesView().closeDialog();
	}

	/**
	 * Reset the visual style of the selected elements.
	 * 
	 * TODO Does not work with lists (F.S.)
	 */
	private void applyDefaults() {
		GeoElement geo;
		ConstructionDefaults defaults = kernel.getConstruction()
				.getConstructionDefaults();

		for (int i = 0; i < selectionList.size(); ++i) {
			geo = (GeoElement) selectionList.get(i);
			defaults.setDefaultVisualStyles(geo, true);
			geo.updateRepaint();
		}

		propPanel.updateSelection(selectionList.toArray());
	}

	/**
	 * shows this dialog and select GeoElement geo at screen position location
	 */
	public void setVisibleWithGeos(ArrayList<GeoElement> geos) {
		kernel.clearJustCreatedGeosInViews();

		setViewActive(true);

		if (kernel.getConstruction().getGeoSetConstructionOrder().size() < MAX_GEOS_FOR_EXPAND_ALL)
			geoTree.expandAll();
		else
			geoTree.collapseAll();

		geoTree.setSelected(geos, false);
		if (!isShowing()) {
			// pack and center on first showing
			if (firstTime) {
				// TODO ---- is this needed?
				//pack();
				//setLocationRelativeTo(app.getMainComponent());
				firstTime = false;
			}

			// ensure min size
			Dimension dim = getSize();
			if (dim.width < MIN_WIDTH) {
				dim.width = MIN_WIDTH;
				setSize(dim);
			}
			if (dim.height < MIN_HEIGHT) {
				dim.height = MIN_HEIGHT;
				setSize(dim);
			}

			super.setVisible(true);
		}
	}

	private boolean firstTime = true;

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			setVisibleWithGeos(null);
		} else {
			super.setVisible(false);
			setViewActive(false);
		}
	}

	private void setViewActive(boolean flag) {
		if (flag == viewActive)
			return;
		viewActive = flag;

		if (flag) {
			geoTree.clear();
			kernel.attach(geoTree);

			// // only add objects if there are less than 200
			// int geoSize =
			// kernel.getConstruction().getGeoSetConstructionOrder().size();
			// if (geoSize < MAX_OBJECTS_IN_TREE)
			kernel.notifyAddAll(geoTree);

			//app.setSelectionListenerMode(this);
			
			//TODO
		//	addWindowFocusListener(this);
		} else {
			kernel.detach(geoTree);

			//TODO
		//	removeWindowFocusListener(this);
		//	app.setSelectionListenerMode(null);
		}
	}

	private boolean viewActive = false;

	/**
	 * handles selection change
	 */
	public void selectionChanged() {
		updateSelectedGeos(geoTree.getSelectionPaths());

		Object[] geos = selectionList.toArray();
		propPanel.updateSelection(geos);
		// Util.addKeyListenerToAll(propPanel, this);

		// update selection of application too
		if (app.getMode() == EuclidianConstants.MODE_SELECTION_LISTENER)
			app.setSelectedGeos(selectionList);
	}

	private ArrayList<?> updateSelectedGeos(TreePath[] selPath) {
		selectionList.clear();

		if (selPath != null) {
			// add all selected paths
			for (int i = 0; i < selPath.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath[i]
						.getLastPathComponent();

				if (node == node.getRoot()) {
					// root: add all objects
					selectionList.clear();
					selectionList.addAll(kernel.getConstruction()
							.getGeoSetLabelOrder());
					i = selPath.length;
				} else if (node.getParent() == node.getRoot()) {
					// type node: select all children
					for (int k = 0; k < node.getChildCount(); k++) {
						DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
								.getChildAt(k);
						selectionList.add(child.getUserObject());
					}
				} else {
					// GeoElement
					selectionList.add(node.getUserObject());
				}
			}
		}

		return selectionList;
	}

	private ArrayList selectionList = new ArrayList();

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (geo == null)
			return;
		tempArrayList.clear();
		tempArrayList.add(geo);
		geoTree.setSelected(tempArrayList, addToSelection);
		// requestFocus();
	}

	private ArrayList<GeoElement> tempArrayList = new ArrayList<GeoElement>();

	/**
	 * deletes all selected GeoElements from Kernel
	 */
	private void deleteSelectedGeos() {
		ArrayList selGeos = selectionList;

		if (selGeos.size() > 0) {
			Object[] geos = selGeos.toArray();
			for (int i = 0; i < geos.length - 1; i++) {
				((GeoElement) geos[i])
						.removeOrSetUndefinedIfHasFixedDescendent();
			}

			// select element above last to delete
			GeoElement geo = (GeoElement) geos[geos.length - 1];
			TreePath tp = geoTree.getTreePath(geo);
			if (tp != null) {
				int row = geoTree.getRowForPath(tp);
				tp = geoTree.getPathForRow(row - 1);
				geo.removeOrSetUndefinedIfHasFixedDescendent();
				if (tp != null)
					geoTree.setSelectionPath(tp);
			}
		}
	}

	
	
	// Tree selection listener
	public void valueChanged(TreeSelectionEvent e) {
		selectionChanged();
	}

	/*
	 * KeyListener
	 */
	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();

		if (src instanceof GeoTree) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteSelectedGeos();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	// ignore if the view is dragged around (can't be dragged at all)
	public void beginDrag() {
	}

	public void endDrag() {
	}

	public GeoTree getGeoTree() {
		return geoTree;
	}
	
	public void updateSelection(Object[] geos) {
		// if (geos == oldSelGeos) return;
		// oldSelGeos = geos;

		propPanel.updateSelection(geos);
	}
	
	public void updateOneGeoDefinition(GeoElement geo) {
		propPanel.updateOneGeoDefinition(geo);
	}
	

} // PropertiesDialog