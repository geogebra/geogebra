package org.geogebra.common.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.main.App;

public class MultiVarStatTableModel extends StatTableModel {

	/**
	 * UI delegate for this model.
	 */
	public interface MultiVarStatTableListener extends StatTableListener {

		@MissingDoc
		String[] getDataTitles();

		@MissingDoc
		boolean isMinimalTable();

	}

	protected MultiVarStatTableListener getMultiVarListener() {
		return (MultiVarStatTableListener) getListener();
	}

	public MultiVarStatTableModel(App app, MultiVarStatTableListener listener) {
		super(app, listener);
	}

	@Override
	public String[] getRowNames() {
		return getMultiVarListener().getDataTitles();
	}

	@Override
	public String[] getColumnNames() {

		ArrayList<Statistic> list = getStatList();
		String[] names = new String[list.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = getStatName(list.get(i));
		}
		return names;
	}

	@Override
	public int getRowCount() {
		return getRowNames() == null ? 0 : getRowNames().length;
	}

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public ArrayList<Statistic> getStatList() {

		ArrayList<Statistic> list = new ArrayList<>();

		if (getMultiVarListener().isViewValid()) {
			return list;
		}

		if (getMultiVarListener().isMinimalTable()) {
			list.add(Statistic.LENGTH);
			list.add(Statistic.MEAN);
			list.add(Statistic.SAMPLE_SD);

		} else {

			list.add(Statistic.LENGTH);
			list.add(Statistic.MEAN);
			list.add(Statistic.SD);
			list.add(Statistic.SAMPLE_SD);
			list.add(Statistic.MIN);
			list.add(Statistic.Q1);
			list.add(Statistic.MEDIAN);
			list.add(Statistic.Q3);
			list.add(Statistic.MAX);

		}

		return list;
	}

	@Override
	public void updatePanel() {
		GeoList dataList = getMultiVarListener().getDataSelected();

		String[] titles = getMultiVarListener().getDataTitles();

		ArrayList<Statistic> list = getStatList();
		double value;

		for (int row = 0; row < titles.length; row++) {
			for (int col = 0; col < list.size(); col++) {

				Statistic stat = list.get(col);

				if (getMultiVarListener().isValidData() && stat != Statistic.NULL) {
					AlgoElement algo = getAlgo(stat,
							(GeoList) dataList.get(row), null);
					if (algo != null) {
						getConstruction().removeFromConstructionList(algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						getMultiVarListener().setValueAt(value, row, col);
					}
				}
			}
		}
	}

}
