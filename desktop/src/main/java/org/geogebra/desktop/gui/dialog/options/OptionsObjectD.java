/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.OptionsObject;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.gui.color.GeoGebraColorChooser;
import org.geogebra.desktop.gui.dialog.PropertiesPanelD;
import org.geogebra.desktop.gui.view.algebra.AlgebraTree;
import org.geogebra.desktop.gui.view.algebra.AlgebraTreeController;
import org.geogebra.desktop.main.AppD;

/**
 * @author Markus Hohenwarter
 */
public class OptionsObjectD extends OptionsObject
		implements OptionPanelD, SetLabels {

	private PropertiesPanelD propPanel;
	private GeoGebraColorChooser colChooser;

	private AlgebraTree tree;

	private JSplitPane splitPane;
	private JScrollPane listScroller;
	private JPanel wrappedPanel;
	private AppD app;

	/**
	 * Creates new PropertiesDialog.
	 * 
	 * @param app
	 *            parent frame
	 */
	public OptionsObjectD(AppD app) {
		this.app = app;
		// build GUI
		initGUI();
	}

	/**
	 * inits GUI with labels of current language
	 */
	public void initGUI() {

		wrappedPanel = new JPanel();
		boolean wasShowing = wrappedPanel.isShowing();
		if (wasShowing) {
			setVisible(false);
		}

		// LIST PANEL
		Kernel kernel = app.getKernel();
		tree = new AlgebraTree(new AlgebraTreeController(kernel), false);
		listScroller = new JScrollPane(tree);
		listScroller.setMinimumSize(new Dimension(MIN_LIST_WIDTH, 200));
		listScroller.setBackground(Color.white);

		// PROPERTIES PANEL
		if (colChooser == null) {
			// init color chooser
			colChooser = new GeoGebraColorChooser(app);
		}

		// check for null added otherwise you get two listeners for the
		// colChooser
		// when a file is loaded
		if (propPanel == null) {
			propPanel = new PropertiesPanelD(app, colChooser, false);
			propPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		}

		// put it all together
		wrappedPanel.removeAll();

		splitPane = new JSplitPane();
		splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		splitPane.setLeftComponent(listScroller);
		splitPane.setRightComponent(propPanel);

		wrappedPanel.setLayout(new BorderLayout());
		// this.add(propPanel, BorderLayout.CENTER);
		wrappedPanel.add(splitPane, BorderLayout.CENTER);

		if (wasShowing) {
			setVisible(true);
		}

		setLabels();

	}

	public PropertiesPanelD getPropPanel() {
		return propPanel;
	}

	/**
	 * show slider tab
	 */
	public void showSliderTab() {
		if (propPanel != null) {
			propPanel.showSliderTab();
		}
	}

	/**
	 * Update the labels of this dialog.
	 * 
	 * TODO Create "Apply Defaults" phrase (F.S.)
	 */
	@Override
	public void setLabels() {

		propPanel.setLabels();

	}

	/**
	 * shows this dialog and select GeoElement geo at screen position location
	 */
	public void setVisibleWithGeos(ArrayList<GeoElement> geos) {
		app.getKernel().clearJustCreatedGeosInViews();

		setViewActive(true);

		if (!wrappedPanel.isShowing()) {
			// ensure min size
			Dimension dim = wrappedPanel.getSize();
			if (dim.width < MIN_WIDTH) {
				dim.width = MIN_WIDTH;
				wrappedPanel.setSize(dim);
			}
			if (dim.height < MIN_HEIGHT) {
				dim.height = MIN_HEIGHT;
				wrappedPanel.setSize(dim);
			}

			wrappedPanel.setVisible(true);
		}
	}

	/**
	 * @param visible
	 *            whether to show this
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			setVisibleWithGeos(null);
		} else {
			wrappedPanel.setVisible(false);
			setViewActive(false);
		}
	}

	private void setViewActive(boolean flag) {
		if (flag == viewActive) {
			return;
		}
		viewActive = flag;

	}

	private boolean viewActive = false;

	private int dividerLocation = MIN_LIST_WIDTH;

	/**
	 * show the geo list
	 */
	public void setGeoTreeVisible() {
		splitPane.setDividerSize(8);
		splitPane.setDividerLocation(dividerLocation);
		listScroller.setVisible(true);
		splitPane.repaint();

	}

	/**
	 * hide the geo list
	 */
	public void setGeoTreeNotVisible() {

		listScroller.setVisible(false);
		dividerLocation = splitPane.getDividerLocation();
		splitPane.setDividerSize(0);
		splitPane.repaint();

	}

	/*
	 * update selection regarding Application
	 * 
	 * public void updateSelection() { updateSelection(app.getSelectedGeos()); }
	 */

	/**
	 * update selection for properties panel
	 * 
	 * @param geos
	 *            geos
	 */
	public void updateSelection(ArrayList<GeoElement> geos) {

		setSelection(geos);
		propPanel.updateSelection(geos.toArray());
	}

	/**
	 * @return the tree
	 */
	public AlgebraTree getTree() {
		return tree;
	}

	@Override
	public void updateGUI() {
		setLabels();

	}

	/**
	 * update geo if in selection
	 * 
	 * @param geo
	 *            geo
	 */
	public void updateIfInSelection(GeoElement geo) {
		// AbstractApplication.printStacktrace("\ngeo = "+geo+"\nselected =
		// "+geo.isSelected()+"\nhighlighted = "+geo.doHighlighting());
		// AbstractApplication.debug("\ngeo = "+geo+"\nselection contains =
		// "+(selection!=null
		// && selection.contains(geo)));
		if (getSelection() != null && getSelection().size() == 1
				&& getSelection().contains(geo)) {
			// propPanel.updateSelection(selection.toArray()); //TODO update
			// only first tab, set flag to others
			propPanel.updateOneGeoDefinition(geo);
		}
	}

	/**
	 * rename geo
	 * 
	 * @param geo
	 *            element to be renamed
	 */
	public void rename(GeoElement geo) {
		if (getSelection() != null && getSelection().size() == 1
				&& getSelection().contains(geo)) {
			propPanel.updateOneGeoName(geo);
		}
	}

	@Override
	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	@Override
	public void setBorder(Border border) {
		// TODO Auto-generated method stub

	}

	public Dimension getPreferredSize() {
		return wrappedPanel.getPreferredSize();
	}

	public void setMinimumSize(Dimension preferredSize) {
		wrappedPanel.setMinimumSize(preferredSize);
	}

	@Override
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

	@Override
	public void applyModifications() {
		propPanel.applyModifications();
	}

	@Override
	public void updateFont() {

		tree.updateFonts();
		propPanel.updateFonts();

	}

	@Override
	public void setSelected(boolean flag) {
		// isSelected = flag;
	}

	/**
	 * @param geo
	 *            GeoText to be updated
	 */
	public void updateTextEditor(GeoElement geo) {
		propPanel.updateTextEditor(geo);
	}

} // PropertiesDialog