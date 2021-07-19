package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CustomScrollbar;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * HTML representation of the Table of Values View.
 *
 * @author laszlo
 *
 */
public class TableValuesPanel extends FlowPanel implements TableValuesListener {

	/** view of table values */
	TableValuesView view;
	private StickyValuesTable table;
	private TableTab parentTab;

	/**
	 * @param app
	 *            {@link AppW}.
	 */
	public TableValuesPanel(AppW app, TableTab parentTab) {
		super();
		view = (TableValuesView) app.getGuiManager().getTableValuesView();
		view.getTableValuesModel().registerListener(this);
		table = new StickyValuesTable(app, view);
		this.parentTab = parentTab;
		TestHarness.setAttr(table, "TV_table");
		add(table);
	}

	private void showTableView() {
		setStyleName("tvTable", true);
		CustomScrollbar.apply(parentTab);
	}

	/**
	 * Sets height of the view.
	 *
	 * @param height
	 *            to set.
	 */
	public void setHeight(int height) {
		table.setHeight(height);
	}

	/**
	 * Deletes the specified column from the view.
	 *
	 * @param column
	 *            column to delete.
	 * @param cb
	 *            to run on transition end.
	 */
	public void deleteColumn(int column, Runnable cb) {
		// not used.
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// not used
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// not used.
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		if (model.getColumnCount() == 2) {
			showTableView();
		}
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// not used.
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		// not used
	}

	@Override
	public void onAttach() {
		super.onAttach();
		showTableView();
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 *
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		table.scrollTo(geo);
	}

}
