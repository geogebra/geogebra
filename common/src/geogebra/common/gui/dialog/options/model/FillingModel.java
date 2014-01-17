package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.FillType;
import geogebra.common.main.Localization;

import java.util.Arrays;
import java.util.List;


public class FillingModel extends MultipleOptionsModel {

	public interface IFillingListener extends IComboListener {
		void setSymbolsVisible(boolean isVisible);
		void setFillingImage(String imageFileName);
		void updateFillTypePanel(FillType fillType);
		void setFillInverseVisible(boolean isVisible);
		void setFillTypeVisible(boolean isVisible);
	}
	
	public FillingModel(IFillingListener listener) {
		super(listener);
	}

	public IFillingListener getFillingListener() {
		return (IFillingListener)getListener();
	}
	@Override
	public List<String> getChoiches(Localization loc) {
		return Arrays.asList(loc.getMenu("Filling.Standard"), // index 0
				loc.getMenu("Filling.Hatch"), // index 1
				loc.getMenu("Filling.Crosshatch"), // index 2
				loc.getMenu("Filling.Chessboard"), // index 3
				loc.getMenu("Filling.Dotted"), // index 4
				loc.getMenu("Filling.Honeycomb"),// index 5
				loc.getMenu("Filling.Brick"),// index 6
				loc.getMenu("Filling.Symbol"),// index 7
				loc.getMenu("Filling.Image") // index 8
				);
	}

	@Override
	protected void apply(int index, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getValueAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void applyFillType(int index) {
		FillType fillType = FillType.values()[index];
		GeoElement geo0 = getGeoAt(0);
		if (fillType == FillType.IMAGE
				&& geo0.getFillImage() != null) {
			getFillingListener().setFillingImage(geo0.getImageFileName());
		} else {
			getFillingListener().setFillingImage(null);
			
		}
		
		if (fillType == fillType.SYMBOLS) {
			getFillingListener().setSymbolsVisible(true);
		} else {
			getFillingListener().setSymbolsVisible(false);

			for (int i = 0; i < getGeosLength(); i++) {
				GeoElement geo = getGeoAt(i);						
				if(false){
//					isBarChart){
//				}
//					if (!updateBarsFillType(geo,1,null)){
//						geo.setFillType(fillType);
//					}
				} else {
					geo.setFillType(fillType);
				}
				geo.updateRepaint();
			}
		}

		getFillingListener().updateFillTypePanel(fillType);
	}

	public void applyFillingInverse(boolean value){
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setInverseFill(value);
			geo.updateRepaint();
		}
	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		boolean hasGeoButton = false;
		getFillingListener().setFillInverseVisible(true);
		getFillingListener().setFillTypeVisible(true);
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			hasGeoButton = geo.isGeoButton();
			if (!geo.isInverseFillable()
					// transformed objects copy inverse filling from parents, so
					// users can't change this
					|| geo.getParentAlgorithm() instanceof AlgoTransformation) {

				getFillingListener().setFillInverseVisible(false);
			}
			if (!geo.isFillable()) {
				geosOK = false;
				break;
			}

			// TODO add fill type for 3D elements
			if (geo.isGeoElement3D()
					|| geo.isGeoImage()) {
				getFillingListener().setFillTypeVisible(false);
			}
		}
		return geosOK;
	}


}
