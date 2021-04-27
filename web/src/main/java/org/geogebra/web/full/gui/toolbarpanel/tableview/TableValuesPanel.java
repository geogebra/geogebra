package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * HTML representation of the Table of Values View.
 *
 * @author laszlo
 *
 */
public class TableValuesPanel extends FlowPanel
		implements SetLabels, TableValuesListener {

	/** view of table values */
	TableValuesView view;
	private StickyValuesTable table;
	private TableValuesEmptyPanel emptyPanel;
	private TableTab parentTab;

	/**
	 * @param app
	 *            {@link AppW}.
	 */
	public TableValuesPanel(AppW app, TableTab parentTab) {
		super();
		view = (TableValuesView) app.getGuiManager().getTableValuesView();
		view.getTableValuesModel().registerListener(this);
		emptyPanel = new TableValuesEmptyPanel(app);
		table = new StickyValuesTable(app, view);
		this.parentTab = parentTab;
		TestHarness.setAttr(table, "TV_table");
		add(emptyPanel);
		add(table);
	}

	private void showEmptyView() {
		show(emptyPanel);
		hide(table);
		setStyleForEmpty(true);
	}

	private void setStyleForEmpty(boolean empty) {
		setStyleName("tvTable", !empty);
		setStyleName("emptyTablePanel", empty);
		parentTab.setStyleName("customScrollbar", empty);
		addParentClassName("tableViewParent", empty);
	}

	private void showTableView() {
		hide(emptyPanel);
		show(table);
		setStyleForEmpty(false);
	}

	private void addParentClassName(String className, boolean add) {
		Element parent = getElement().getParentElement();
		if (parent != null) {
			if (add) {
				parent.addClassName(className);
			} else {
				parent.removeClassName(className);
			}
		}
	}

	private void removeParentClassName(String className) {
		Element parent = getElement().getParentElement();
		if (parent != null) {
			parent.removeClassName(className);
		}
	}

	@Override
	public void setLabels() {
		emptyPanel.setLabels();
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
		if (model.getColumnCount() == 1) {
			showEmptyView();
		}
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
		if (view.isEmpty()) {
			showEmptyView();
		}
	}

	@Override
	public void onAttach() {
		super.onAttach();

		if (view.isEmpty()) {
			showEmptyView();
		} else {
			showTableView();
		}
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

	private static void show(UIObject object) {
		object.removeStyleName("hidden");
	}

	private static void hide(UIObject object) {
		object.addStyleName("hidden");
	}

}
