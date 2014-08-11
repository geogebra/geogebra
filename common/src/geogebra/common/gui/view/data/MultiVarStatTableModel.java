package geogebra.common.gui.view.data;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

import java.util.ArrayList;

public class MultiVarStatTableModel extends StatTableModel {
	public interface MultiVarStatTableListener extends StatTableListener {

		String[] getDataTitles();

		boolean isMinimalTable();
		
	}
	
	private MultiVarStatTableListener mvListener;
	
	public MultiVarStatTableModel(App app, MultiVarStatTableListener listener) {
		super(app, listener);
		mvListener = listener;
	}
	
	
 	@Override
	public String[] getRowNames() {
		return mvListener.getDataTitles();
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
	
	public ArrayList<Stat> getStatList() {

		ArrayList<Stat> list = new ArrayList<Stat>();

		if (mvListener.isViewValid()) {
			return list;
		}

		if (mvListener.isMinimalTable()) {
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

	@Override
	public void updatePanel() {
		GeoList dataList = mvListener.getDataSelected();
		
		String[] titles = mvListener.getDataTitles();

		ArrayList<Stat> list = getStatList();
		double value;

		for (int row = 0; row < titles.length; row++) {
			for (int col = 0; col < list.size(); col++) {

				Stat stat = list.get(col);

				if (mvListener.isValidData() && stat != Stat.NULL) {
					AlgoElement algo = getAlgo(stat,
							(GeoList) dataList.get(row), null);
					if (algo != null) {
						getConstruction()
								.removeFromConstructionList(algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						mvListener.setValueAt(value, row, col);
					}
				}
			}
		}
	}

}
