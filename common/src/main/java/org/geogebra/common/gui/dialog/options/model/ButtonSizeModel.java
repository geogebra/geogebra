package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.main.App;

public class ButtonSizeModel extends OptionsModel {
	private IButtonSizeListener listener;

	public interface IButtonSizeListener extends PropertyListener {
		void updateSizes(int width, int height, boolean isFixed);
	}

	public ButtonSizeModel(App app) {
		super(app);
	}

	public void setListener(IButtonSizeListener listener) {
		this.listener = listener;
	}

	private GeoButton getButtonAt(int index) {
		Object geo = getObjectAt(index);
		if (geo instanceof GeoButton) {
			return (GeoButton) geo;
		}

		return null;
	}

	@Override
	public void updateProperties() {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);

			if (geo != null) {
				listener.updateSizes(geo.getWidth(), geo.getHeight(),
						geo.isFixedSize());
			}
		}

	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isGeoButton()
				&& !getGeoAt(index).isGeoInputBox();
	}

	public void setSizesFromString(String strWidth, String strHeight,
			boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);

			if (geo != null) {
				geo.setFixedSize(isFixed);
				if (isFixed) {
					geo.setHeight(Integer.parseInt(strHeight));
					geo.setWidth(Integer.parseInt(strWidth));
				} else {
					geo.setFixedSize(false);
				}
			}
		}
	}

	public void applyChanges(boolean isFixed) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoButton geo = getButtonAt(i);

			if (geo != null) {
				geo.setFixedSize(isFixed);
				listener.updateSizes(geo.getWidth(), geo.getHeight(), isFixed);
			}
		}
		storeUndoInfo();
	}

	@Override
	public PropertyListener getListener() {
		return listener;
	}
}
