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

package org.geogebra.desktop.cas.view;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.geogebra.common.cas.view.RowHeader;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.main.AppD;

/**
 * Row headers for CAS cells
 */
public class RowHeaderD extends JList implements RowHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * Width of row headers
	 */
	public static final int ROW_HEADER_WIDTH = 50;

	private RowHeaderRenderer renderer;

	/**
	 * @param table
	 *            CAS table
	 * @param multipleIntervalSelection
	 *            whether multiple intervals should be possible to select
	 * @param lsModel
	 *            selection model
	 */
	public RowHeaderD(CASTableD table, boolean multipleIntervalSelection,
			ListSelectionModel lsModel, AppD app) {
		setModel(new RowHeaderListModel(table));
		setSelectionModel(lsModel);
		if (multipleIntervalSelection) {
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		}
		setFixedCellWidth(ROW_HEADER_WIDTH);
		setFocusable(true);
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
				GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
		// renderer
		renderer = new RowHeaderRenderer(table);
		setCellRenderer(renderer);

		// listener
		RowHeaderListener rhl = new RowHeaderListener(table, this, app);
		addMouseListener(rhl);
		addMouseMotionListener(rhl);
		addKeyListener(rhl);
		// this.getSelectionModel().addListSelectionListener(rhl);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// table.getSelectionModel().addListSelectionListener(this);
		table.setRowSelectionAllowed(true);
	}

	/**
	 * Update the icons.
	 */
	public void updateIcons() {
		renderer.updateIcons();
	}
}
