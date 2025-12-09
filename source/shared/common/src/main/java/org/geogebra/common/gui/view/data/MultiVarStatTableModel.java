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
