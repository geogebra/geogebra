package geogebra.gui.view.data;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.main.AppD;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanel extends BasicStatTable {
	private static final long serialVersionUID = 1L;
	
	private boolean isMinimalTable = false;

	
	/***************************************************
	 * Constructs a MultiVarStatPanel
	 * @param app
	 * @param statDialog
	 */
	public MultiVarStatPanel(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog);
	}

	public void setMinimalTable(boolean isMinimalTable) {
		this.isMinimalTable = isMinimalTable;
		initStatTable();
		
	}
	
	@Override
	public String[] getRowNames() {
		return daView.getDataTitles();
	}

	@Override
	public String[] getColumnNames() {

		ArrayList<Stat> list = getStatList();
		String[] names = new String[list.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = getStatName(list.get(i));
		}
		return names;
	}

	@Override
	public int getRowCount() {
		return getRowNames().length;
	}

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public void updatePanel() {
		GeoList dataList = daView.getController().getDataSelected();
		DefaultTableModel model = statTable.getModel();

		String[] titles = daView.getDataTitles();

		ArrayList<Stat> list = getStatList();
		double value;

		for (int row = 0; row < titles.length; row++) {
			for (int col = 0; col < list.size(); col++) {

				Stat stat = list.get(col);

				if (daView.getController().isValidData() && stat != Stat.NULL) {
					AlgoElement algo = getAlgo(stat,
							(GeoList) dataList.get(row), null);
					if (algo != null) {
						app.getKernel().getConstruction()
								.removeFromConstructionList(algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						model.setValueAt(daView.format(value), row, col);
					}
				}
			}
		}
		statTable.repaint();
	}

	private ArrayList<Stat> getStatList() {

		ArrayList<Stat> list = new ArrayList<Stat>();

		if (daView == null || daView.getDataSource() == null) {
			return list;
		}

		if (isMinimalTable) {
			list.add(Stat.LENGTH);
			list.add(Stat.MEAN);
			list.add(Stat.SAMPLE_SD);
			
		} else {
			
			list.add(Stat.LENGTH);
			list.add(Stat.MEAN);
			list.add(Stat.SD);
			list.add(Stat.SAMPLE_SD);
			list.add(Stat.MIN);
			list.add(Stat.Q1);
			list.add(Stat.MEDIAN);
			list.add(Stat.Q3);
			list.add(Stat.MAX);

		}

		return list;
	}

}
