/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 *
 * @author markus
 */
public class ContextMenuGraphicsWindowD extends ContextMenuGeoElementD
		implements ActionListener {

	private double px;
	private double py;
	// private JMenuItem miStandardView, miProperties;

	private ImageIcon iconZoom;

	public ContextMenuGraphicsWindowD(AppD app) {
		super(app);
	}

	/**
	 * Creates new ZoomMenu
	 * 
	 * @param app application
	 * @param px horizontal position
	 * @param py vertical position
	 */
	public ContextMenuGraphicsWindowD(AppD app, double px, double py) {
		this(app);

		iconZoom = app.getScaledIcon(GuiResourcesD.ZOOM16);

		// zoom point
		this.px = px;
		this.py = py;

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (ev.getEuclidianViewNo() == 2) {
			setTitle("<html>" + loc.getMenu("DrawingPad2") + "</html>");
		} else {
			setTitle("<html>" + loc.getMenu("DrawingPad") + "</html>");
		}

		addAxesAndGridCheckBoxes();

		addNavigationBar();

		// zoom for both axes
		addZoomMenu(ev);

		// zoom for y-axis
		JMenu yaxisMenu = new JMenu(
				loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"));
		yaxisMenu.setIcon(app.getEmptyIcon());
		yaxisMenu.setBackground(wrappedPopup.getBackground());
		addAxesRatioItems(yaxisMenu);
		wrappedPopup.add(yaxisMenu);

		JMenuItem miShowAllObjectsView = addShowAllObjectsView(app);

		JMenuItem miStandardView = addStandardViewItem();

		wrappedPopup.addSeparator();
		if (!ev.isZoomable()) {
			yaxisMenu.setEnabled(false);
			miShowAllObjectsView.setEnabled(false);
			miStandardView.setEnabled(false);
		}

		if (ev.isLockedAxesRatio()) {
			yaxisMenu.setEnabled(false);
		}

		addMiProperties();

		app.setComponentOrientation(wrappedPopup);

	}

	protected void addZoomMenu(EuclidianViewInterfaceCommon ev) {
		JMenu zoomMenu = new JMenu(loc.getMenu("Zoom"));
		zoomMenu.setIcon(iconZoom);
		zoomMenu.setBackground(wrappedPopup.getBackground());
		addZoomItems(zoomMenu);
		if (!ev.isZoomable()) {
			zoomMenu.setEnabled(false);
		}
		wrappedPopup.add(zoomMenu);

	}

	protected void addNavigationBar() {
		AbstractAction showConstructionStep = new AbstractAction(
				loc.getMenu("NavigationBar")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				toggleShowConstructionProtocolNavigation();

			}
		};
		JCheckBoxMenuItem cbShowConstructionStep = new JCheckBoxMenuItem(
				showConstructionStep);
		cbShowConstructionStep.setSelected(app.showConsProtNavigation(
				app.getActiveEuclidianView().getViewID()));
		cbShowConstructionStep.setBackground(wrappedPopup.getBackground());
		wrappedPopup.add(cbShowConstructionStep);

		wrappedPopup.addSeparator();
	}

	/**
	 * add show all objects item
	 */
	protected JMenuItem addShowAllObjectsView(AppD app) {
		JMenuItem miShowAllObjectsView = new JMenuItem(
				loc.getMenu("ShowAllObjects"));
		miShowAllObjectsView.setIcon(app.getEmptyIcon());
		miShowAllObjectsView.setActionCommand("showAllObjects");
		miShowAllObjectsView.addActionListener(this);
		miShowAllObjectsView.setBackground(bgColor);
		wrappedPopup.add(miShowAllObjectsView);

		return miShowAllObjectsView;
	}

	/**
	 * add "standard view" item
	 * 
	 * @return menu item
	 */
	protected JMenuItem addStandardViewItem() {
		JMenuItem miStandardView = new JMenuItem(loc.getMenu("StandardView"));
		setMenuShortCutAccelerator(miStandardView, 'M');
		miStandardView.setIcon(((AppD) app).getEmptyIcon());
		miStandardView.setActionCommand("standardView");
		miStandardView.addActionListener(this);
		miStandardView.setBackground(bgColor);
		wrappedPopup.add(miStandardView);
		return miStandardView;
	}

	void toggleShowConstructionProtocolNavigation() {
		((AppD) app).toggleShowConstructionProtocolNavigation(
				app.getActiveEuclidianView().getViewID());
	}

	protected void addMiProperties() {
		JMenuItem miProperties = new JMenuItem(
				loc.getMenu("DrawingPad") + " ...");
		miProperties.setIcon(
				((AppD) app).getScaledIcon(GuiResourcesD.VIEW_PROPERTIES_16));
		miProperties.setActionCommand("properties");
		miProperties.addActionListener(this);
		miProperties.setBackground(bgColor);
		wrappedPopup.add(miProperties);
	}

	protected void addAxesAndGridCheckBoxes() {

		// checkboxes for axes and grid
		JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(
				((GuiManagerD) app.getGuiManager()).getShowAxesAction());
		EuclidianViewInterfaceCommon view = app.getGuiManager().getActiveEuclidianView();
		cbShowAxes.setSelected(view.getShowXaxis() && view.getShowYaxis());
		cbShowAxes.setBackground(wrappedPopup.getBackground());
		wrappedPopup.add(cbShowAxes);

		JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(
				((GuiManagerD) app.getGuiManager()).getShowGridAction());
		cbShowGrid.setSelected(view.getShowGrid());
		cbShowGrid.setBackground(wrappedPopup.getBackground());
		wrappedPopup.add(cbShowGrid);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if ("standardView".equals(cmd)) {
			setStandardView();
		} else if ("showAllObjects".equals(cmd)) {
			app.setViewShowAllObjects(false);
		} else if ("properties".equals(cmd)) {
			showOptionsDialog();
		}
	}

	/**
	 * set standard view
	 */
	protected void setStandardView() {
		app.setStandardView();
	}

	protected void showOptionsDialog() {
		app.getGuiManager().setShowView(true, App.VIEW_PROPERTIES);
		((GuiManagerD) app.getGuiManager()).setFocusedPanel(
				app.getActiveEuclidianView().getViewID(), true);
		// app.getDialogManager().showOptionsDialog(OptionsDialog.TAB_EUCLIDIAN);
		// app.getGuiManager().showDrawingPadPropertiesDialog();
	}

	private void addZoomItems(JMenu menu) {
		int perc;

		ActionListener al = e -> {
			try {
				zoom(Double.parseDouble(e.getActionCommand()));
			} catch (Exception ex) {
			}
		};

		// ImageIcon icon;
		JMenuItem mi;
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getZoomFactorLength(); i++) {
			perc = (int) (getZoomFactor(i) * 100.0);

			// build text like "125%" or "75%"
			sb.setLength(0);
			if (perc <= 100 && !separatorAdded) {
					menu.addSeparator();
					separatorAdded = true;
			}
			sb.append(perc);
			sb.append('%');

			mi = new JMenuItem(sb.toString());
			mi.setActionCommand("" + getZoomFactor(i));
			mi.addActionListener(al);
			mi.setBackground(wrappedPopup.getBackground());
			menu.add(mi);
		}
	}

	private void addAxesRatioItems(JMenu menu) {
		ActionListener al = e -> {
			try {
				zoomYaxis(Double.parseDouble(e.getActionCommand()));
			} catch (Exception ex) {
			}
		};

		// get current axes ratio
		double scaleRatio = app.getActiveEuclidianView()
				.getScaleRatio();

		JMenuItem mi;
		// int perc;
		// ImageIcon icon;
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < axesRatios.length; i++) {
			// build text like "1 : 2"
			sb.setLength(0);
			if (axesRatios[i] > 1.0) {
				sb.append((int) axesRatios[i]);
				sb.append(" : 1");
				if (!separatorAdded) {
					menu.addSeparator();
					separatorAdded = true;
				}

			} else { // factor
				if (axesRatios[i] == 1) {
					menu.addSeparator();
				}
				sb.append("1 : ");
				sb.append((int) (1.0 / axesRatios[i]));
			}

			mi = new JCheckBoxMenuItem(sb.toString());
			mi.setSelected(DoubleUtil.isEqual(axesRatios[i], scaleRatio));
			mi.setActionCommand("" + axesRatios[i]);
			mi.addActionListener(al);
			mi.setBackground(wrappedPopup.getBackground());
			menu.add(mi);
		}
	}

	private void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
	}

	// ratio: yaxis / xaxis
	private void zoomYaxis(double axesRatio) {
		app.zoomAxesRatio(axesRatio);
	}
}
