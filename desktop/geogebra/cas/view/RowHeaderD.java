package geogebra.cas.view;

import geogebra.common.cas.view.RowHeader;
import geogebra.common.main.GeoGebraColorConstants;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * Row headers for CAS cells
 */
public class RowHeaderD extends JList implements RowHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * Width of row headers
	 */
	public static final int ROW_HEADER_WIDTH = 30;

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
			ListSelectionModel lsModel) {
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
				geogebra.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
		// renderer
		renderer = new RowHeaderRenderer(table);
		setCellRenderer(renderer);

		// listener
		RowHeaderListener rhl = new RowHeaderListener(table, this);
		addMouseListener(rhl);
		addMouseMotionListener(rhl);
		addKeyListener(rhl);
		// this.getSelectionModel().addListSelectionListener(rhl);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// table.getSelectionModel().addListSelectionListener(this);
		table.setRowSelectionAllowed(true);
	}

	public void updateIcons() {
		renderer.updateIcons();
	}
}
