package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class FillingModel extends MultipleOptionsModel {

	private FillType fillType;
	private Kernel kernel;
	private boolean hasGeoButton;
	private boolean hasGeoTurtle;

	public interface IFillingListener extends IComboListener {
		void setSymbolsVisible(boolean isVisible);

		void setFillingImage(String imageFileName);

		void setFillInverseVisible(boolean isVisible);

		void setFillTypeVisible(boolean isVisible);

		void setFillInverseSelected(boolean value);

		void setFillValue(int value);

		void setAngleValue(int value);

		void setDistanceValue(int value);

		void setBarChart(AlgoBarChart algo);

		void setImageFillType();

		void setDottedFillType();

		void setSymbolFillType();

		void setBrickFillType();

		void setCrossHatchedFillType();

		void setHatchFillType();

		void setStandardFillType();

		int getSelectedBarIndex();

		void selectSymbol(String barSymbol);

		String getSelectedSymbolText();

		double getFillingValue();

		FillType getSelectedFillType();

		int getDistanceValue();

		int getAngleValue();
	}

	public FillingModel(App app) {
		super(app);
		kernel = app.getKernel();
	}

	public IFillingListener getFillingListener() {
		return (IFillingListener) getListener();
	}

	@Override
	public List<String> getChoices(Localization loc) {
		if (app.isExam()) {
			return Arrays.asList(loc.getMenu("Filling.Standard"), // index 0
					loc.getMenu("Filling.Hatch"), // index 1
					loc.getMenu("Filling.Crosshatch"), // index 2
					loc.getMenu("Filling.Chessboard"), // index 3
					loc.getMenu("Filling.Dotted"), // index 4
					loc.getMenu("Filling.Honeycomb"), // index 5
					loc.getMenu("Filling.Brick"), // index 6
					loc.getMenu("Filling.Weaving"), // index 6
					loc.getMenu("Filling.Symbol")// index 7
			);
		}
		return Arrays.asList(loc.getMenu("Filling.Standard"), // index 0
				loc.getMenu("Filling.Hatch"), // index 1
				loc.getMenu("Filling.Crosshatch"), // index 2
				loc.getMenu("Filling.Chessboard"), // index 3
				loc.getMenu("Filling.Dotted"), // index 4
				loc.getMenu("Filling.Honeycomb"), // index 5
				loc.getMenu("Filling.Brick"), // index 6
				loc.getMenu("Filling.Weaving"), // index 6
				loc.getMenu("Filling.Symbol"), // index 7
				loc.getMenu("Filling.Image") // index 8
		);

	}

	public void updateFillType(FillType newFillType) {

		switch (newFillType) {

		case STANDARD:
			getFillingListener().setStandardFillType();
			break;
		case HATCH:
			getFillingListener().setHatchFillType();
			break;
		case CROSSHATCHED:
		case CHESSBOARD:
		case WEAVING:
			getFillingListener().setCrossHatchedFillType();
			break;
		case BRICK:
			getFillingListener().setBrickFillType();
			break;
		case SYMBOLS:
			getFillingListener().setSymbolFillType();
			break;
		case HONEYCOMB:
		case DOTTED:
			getFillingListener().setDottedFillType();
			break;
		case IMAGE:
			getFillingListener().setImageFillType();
			break;

		}
	}

	@Override
	public void updateProperties() {
		GeoElement geo0 = getGeoAt(0);
		IFillingListener fillListener = getFillingListener();
		// set selected fill type to first geo's fill type

		if (isBarChart()) {
			setBarFillType(geo0);
		} else {
			fillListener.setSelectedIndex(geo0.getFillType().ordinal());
		}

		// set selected fill type to first geo's fill type
		fillListener.setFillInverseSelected(geo0.isInverseFill());
		AlgoBarChart algo = null;

		if (isBarChart()) {
			algo = (AlgoBarChart) geo0.getParentAlgorithm();
			updateBarFillTypePanel(algo);
		} else {
			updateFillType(geo0.getFillType());
		}

		fillListener.setBarChart(algo);

		// set value to first geo's alpha value
		double alpha = geo0.getAlphaValue();
		if (isBarChart()) {
			setAlpha(algo, alpha);
		} else {
			fillListener.setFillValue((int) Math.round(alpha * 100));
		}
		double angle = geo0.getHatchingAngle();
		if (isBarChart()) {
			setBarAngle(algo, angle);
		} else {
			fillListener.setAngleValue((int) angle);
		}

		int distance = geo0.getHatchingDistance();
		if (isBarChart()) {
			setBarDistance(algo, distance);
		} else {
			fillListener.setDistanceValue(distance);
		}

		if (isBarChart()) {
			fillListener.selectSymbol(
					algo.getBarSymbol(fillListener.getSelectedBarIndex()));
			// setSymbol((AlgoBarChart) geo0.getParentAlgorithm());
		} else {
			if (geo0.getFillSymbol() != null
					&& !geo0.getFillSymbol().trim().equals("")) {
				fillListener.selectSymbol(geo0.getFillSymbol());
			}
		}
		// set selected image to first geo image
		fillListener.setFillingImage(geo0.getImageFileName());

	}

	// Methods that set value for single bar if single bar is selected
	// and bar has tag for value

	private void setAlpha(AlgoBarChart algo, double alpha0) {
		double alpha = alpha0;
		int idx = getFillingListener().getSelectedBarIndex();
		if (idx != 0) {
			double barAlpha = algo.getBarAlpha(idx);
			if (barAlpha != -1) {
				alpha = barAlpha;
			}
		}
		getFillingListener().setFillValue((int) Math.round(alpha * 100));
	}

	private void updateBarFillTypePanel(AlgoBarChart algo) {
		int idx = getFillingListener().getSelectedBarIndex();
		FillType type = FillType.STANDARD;
		if (algo.getBarFillType(idx) != FillType.STANDARD) {
			type = FillType.values()[algo.getBarFillType(idx).ordinal()];
		}

		fillType = type;
		updateFillType(type);
	}

	private void setBarDistance(AlgoBarChart algo, int distance0) {
		int distance = distance0;
		int idx = getFillingListener().getSelectedBarIndex();
		if (idx != 0) {
			if (algo.getBarHatchDistance(idx) != -1) {
				distance = algo.getBarHatchDistance(idx);
			}
		}
		getFillingListener().setDistanceValue(distance);
	}

	private void setBarAngle(AlgoBarChart algo, double angle0) {
		double angle = angle0;
		int idx = getFillingListener().getSelectedBarIndex();
		if (idx != 0) {
			if (algo.getBarHatchAngle(idx) != -1) {
				angle = algo.getBarHatchAngle(idx);
			}
		}
		getFillingListener().setAngleValue((int) angle);
	}

	private void setBarFillType(GeoElement geo) {
		int idx = getFillingListener().getSelectedBarIndex();
		if (idx == 0) {
			getFillingListener().setSelectedIndex(geo.getFillType().ordinal());
		} else {
			AlgoBarChart algo = (AlgoBarChart) geo.getParentAlgorithm();
			if (algo != null && algo.getBarFillType(idx) != null) {
				getFillingListener()
						.setSelectedIndex(algo.getBarFillType(idx).ordinal());
			}
		}
	}

	public void applyImage(String fileName) {
		if (fileName == null) {
			return;
		}
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (isBarChart()) {
				if (!updateBarsFillType(geo, 2, fileName)) {
					geo.setImageFileName(fileName);
				}
			} else {
				geo.setImageFileName(fileName);
				Log.debug("geo.setImageFileName(" + fileName + ")");
			}
			geo.setAlphaValue(fileName.isEmpty() ? 0.0f : 1.0f);
			geo.updateRepaint();
		}

	}

	public void applyUnicode(String symbolText) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!"".equals(symbolText)) {
				if (isBarChart()) {
					if (!updateBarsFillType(geo, 3, null)) {
						geo.setFillType(fillType);
						geo.setFillSymbol(symbolText);
					}
				} else {
					geo.setFillType(fillType);
					geo.setFillSymbol(symbolText);
				}
				geo.updateRepaint();
			}

		}
	}

	public void applyOpacity(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (isBarChart()) {
				updateBarsFillType(geo, 4, null);
			} else {
				geo.setAlphaValue(value / 100.0f);
			}
			geo.updateVisualStyle(GProperty.COLOR);

		}
		kernel.notifyRepaint();
	}

	public void applyAngleAndDistance(int angle, int distance) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (isBarChart()) {
				if (!updateBarsFillType(geo, 1, null)) {
					geo.setHatchingAngle(angle);
					geo.setHatchingDistance(distance);
				}
			} else {
				geo.setHatchingAngle(angle);
				geo.setHatchingDistance(distance);
			}
			geo.updateVisualStyle(GProperty.HATCHING);
		}
		kernel.notifyRepaint();

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
		fillType = getFillTypeAt(index);
		GeoElement geo0 = getGeoAt(0);
		if (fillType == FillType.IMAGE && geo0.getFillImage() != null) {
			getFillingListener().setFillingImage(geo0.getImageFileName());
		} else {
			getFillingListener().setFillingImage(null);

		}

		getFillingListener().setSymbolsVisible(fillType == FillType.SYMBOLS);

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (isBarChart()) {
				if (!updateBarsFillType(geo, 1, null)) {
					updateGeoFillType(geo);
				}
			} else {
				updateGeoFillType(geo);
			}
		}
		kernel.notifyRepaint();

		storeUndoInfo();
		updateFillType(fillType);
	}

	private void updateGeoFillType(GeoElement geo) {
		if (fillType == FillType.SYMBOLS && geo.getFillSymbol() == null) {
			geo.setFillSymbol("$");
		}
		geo.setFillType(fillType);
		geo.updateVisualStyle(GProperty.HATCHING);
	}

	public void applyFillingInverse(boolean value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setInverseFill(value);
			geo.updateRepaint();
		}
		storeUndoInfo();
	}

	public boolean updateBarsFillType(GeoElement geo, int type,
			String fileName) {
		int selectedBarIndex = getFillingListener().getSelectedBarIndex();
		AlgoBarChart algo = (AlgoBarChart) geo.getParentAlgorithm();
		if (selectedBarIndex == 0) {
			int numBar = algo.getIntervals();
			for (int i = 1; i < numBar + 1; i++) {
				algo.setBarFillType(null, i);
				algo.setBarHatchDistance(-1, i);
				algo.setBarHatchAngle(-1, i);
				algo.setBarSymbol(null, i);
				algo.setBarImage(null, i);
			}
			geo.updateVisualStyle(GProperty.HATCHING);
			return false;
		}
		switch (type) {
		default:
		case 1:
			algo.setBarFillType(getFillType(), selectedBarIndex);
			algo.setBarHatchDistance(getDistanceValue(), selectedBarIndex);
			algo.setBarHatchAngle(getAngleValue(), selectedBarIndex);
			algo.setBarImage(null, selectedBarIndex);
			if (getSelectedFillType() == FillType.SYMBOLS) {
				if (getSelectedSymbolText() != null
						&& !"".equals(getSelectedSymbolText())) {
					algo.setBarHatchAngle(-1, selectedBarIndex);
					algo.setBarSymbol(getSelectedSymbolText(),
							selectedBarIndex);
				} else {
					algo.setBarFillType(FillType.STANDARD, selectedBarIndex);
					algo.setBarSymbol(null, selectedBarIndex);
				}
			} else {
				algo.setBarSymbol(null, selectedBarIndex);
			}
			break;
		case 4:
			algo.setBarAlpha(getFillingValue() / 100f, selectedBarIndex);
			break;
		case 2:
			algo.setBarFillType(null, selectedBarIndex);
			algo.setBarHatchDistance(-1, selectedBarIndex);
			algo.setBarHatchAngle(-1, selectedBarIndex);
			algo.setBarSymbol(null, selectedBarIndex);
			algo.setBarImage(fileName, selectedBarIndex);
			algo.setBarFillType(FillType.IMAGE, selectedBarIndex);
			break;
		case 3:
			if (getSelectedSymbolText() != null
					&& !"".equals(getSelectedSymbolText())) {
				algo.setBarFillType(FillType.SYMBOLS, selectedBarIndex);
				algo.setBarHatchAngle(-1, selectedBarIndex);
				algo.setBarImage(null, selectedBarIndex);
				algo.setBarSymbol(getSelectedSymbolText(), selectedBarIndex);
			} else {
				algo.setBarSymbol(null, selectedBarIndex);
			}
			break;
		}
		geo.updateVisualStyle(GProperty.HATCHING);
		storeUndoInfo();
		return true;
	}

	private String getSelectedSymbolText() {
		return getFillingListener().getSelectedSymbolText();
	}

	private double getFillingValue() {
		return getFillingListener().getFillingValue();
	}

	private FillType getSelectedFillType() {
		return getFillingListener().getSelectedFillType();
	}

	private int getAngleValue() {
		return getFillingListener().getAngleValue();
	}

	private int getDistanceValue() {
		return getFillingListener().getDistanceValue();
	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		hasGeoButton = false;
		hasGeoTurtle = false;
		if (getFillingListener() != null) {
			getFillingListener().setFillInverseVisible(true);
			getFillingListener().setFillTypeVisible(true);
		}
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			hasGeoButton = geo.isGeoButton();
			hasGeoTurtle = geo.isGeoTurtle();
			if (!geo.isInverseFillable()
					// transformed objects copy inverse filling from parents, so
					// users can't change this
					|| geo.getParentAlgorithm() instanceof AlgoTransformation) {
				if (getFillingListener() != null) {
					getFillingListener().setFillInverseVisible(false);
				}
			}
			if (!geo.isFillable() || geo instanceof GeoImage
					|| geo instanceof GeoInputBox || geo.isGeoQuadric()) {
				geosOK = false;
				break;
			}

			// TODO add fill type for 3D elements
			if (getFillingListener() != null) {
				if (!geo.hasFillType()) {
					getFillingListener().setFillTypeVisible(false);
				}
			}
		}

		return geosOK;
	}

	public boolean isBarChart() {
		return getGeoAt(0).getParentAlgorithm() instanceof AlgoBarChart;
	}

	public boolean hasGeoButton() {
		// its function must be clarified.
		return hasGeoButton;
	}

	public boolean hasGeoTurtle() {
		// its function must be clarified.
		return hasGeoTurtle;
	}

	public FillType getFillType() {
		return fillType;
	}

	public void setFillType(FillType fillType) {
		this.fillType = fillType;
	}

	public FillType getFillTypeAt(int index) {
		return FillType.values()[index];
	}

}
