/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.euclidian;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Use this modal dialog to let the user choose one string from a list.
 */
// TODO class is unused; will it be used?
public class ListDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int MIN_WIDTH = 120;
	private static final int MIN_HEIGHT = 100;

	private GeoElement value = null;
	JList list;
	DefaultListModel listModel;

	/**
	 * 
	 * @param comp
	 * @param data
	 *            list of GeoElement objects
	 * @param title
	 */
	public ListDialog(JComponent comp, ArrayList<GeoElement> data,
			String title) {
		super(JOptionPane.getFrameForComponent(comp), title, true);

		// list
		listModel = new DefaultListModel();
		list = new JList(listModel);
		for (int i = 0; i < data.size(); i++) {
			GeoLabel label = new GeoLabel(data.get(i));
			listModel.addElement(label);
		}

		final int lineHeight = list.getPreferredSize().height / data.size();

		// listen when mouse is moved over an item
		MouseMotionListener mml = new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int size = listModel.getSize();
				list.setSelectedIndex(
						Math.min(e.getY() / lineHeight, size - 1));
			}
		};
		list.addMouseMotionListener(mml);

		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				GeoLabel gl = (GeoLabel) list.getSelectedValue();
				if (gl == null) {
					return;
				}
				setValue(gl.geo);
				setVisible(false);
			}
		});

		list.setVisibleRowCount(4);
		JScrollPane listScroller = new JScrollPane(list);
		Dimension dim = new Dimension(MIN_WIDTH, MIN_HEIGHT);
		listScroller.setPreferredSize(dim);
		// Must do the following, too, or else the scroller thinks
		// it's taller than it is:
		listScroller.setMinimumSize(dim);

		// Put everything together, using the content pane's BorderLayout.
		Container contentPane = getContentPane();
		contentPane.add(listScroller, BorderLayout.CENTER);
		// setUndecorated(false);
		pack();
	}

	/**
	 * Show the initialized dialog. The first argument should be null if you
	 * want the dialog to come up in the center of the screen. Otherwise, the
	 * argument should be the component on top of which the dialog should
	 * appear.
	 */
	public GeoElement showDialog(Component comp, Point location) {
		Point p = comp.getLocationOnScreen();
		setLocation(location.x + p.x, location.y + p.y);
		setVisible(true);
		return value;
	}

	void setValue(GeoElement newValue) {
		value = newValue;
		list.setSelectedValue(value, true);
	}

	private static class GeoLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		GeoElement geo;

		public GeoLabel(GeoElement geo) {
			this.geo = geo;
		}

		@Override
		public String toString() {
			return geo.getNameDescriptionHTML(true, true);
		}
	}
}