package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.algos.ChartStyleAlgo;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class FillingModel extends MultipleOptionsModel {

	private FillType fillType;
	private Kernel kernel;
	private boolean hasGeoButton;
	private boolean hasGeoTurtle;
	private List<FillType> fillTypes;
	enum FillingProperty {ALPHA, IMAGE, SYMBOL, FILL_TYPE}

	public interface IFillingListener extends IComboListener {
		void setSymbolsVisible(boolean isVisible);

		void setFillingImage(String imageFileName);

		void setFillInverseVisible(boolean isVisible);

		void setFillTypeVisible(boolean isVisible);

		void setFillInverseSelected(boolean value);

		void setFillValue(int value);

		void setAngleValue(int value);

		void setDistanceValue(int value);

		void setBarChart(int cols);

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
		setupFillTypes();
	}

	private void setupFillTypes() {
		Set<FillType> types = new HashSet<>(Arrays.asList(FillType.values()));
		Set<FillType> availableFillTypes = app.getConfig().getAvailableFillTypes();
		types.retainAll(availableFillTypes);
		fillTypes = new ArrayList<>(types);
	}

	private IFillingListener getFillingListener() {
		return (IFillingListener) getListener();
	}

	@Override
	public List<String> getChoices(Localization loc) {
		List<FillType> types = fillTypes;
		if (app.isExam()) {
			types = new ArrayList<>(fillTypes);
			types.remove(FillType.IMAGE);
		}
		List<String> choices = new ArrayList<>();
		for (FillType fillType: types) {
			String key = getFillTypeTranslationKey(fillType);
			choices.add(loc.getMenu(key));
		}
		return choices;
	}

	private String getFillTypeTranslationKey(FillType fillType) {
		switch (fillType) {
			case HATCH:
				return "Filling.Hatch";
			case CROSSHATCHED:
				return "Filling.Crosshatch";
			case CHESSBOARD:
				return "Filling.Chessboard";
			case DOTTED:
				return "Filling.Dotted";
			case HONEYCOMB:
				return "Filling.Honeycomb";
			case BRICK:
				return "Filling.Brick";
			case WEAVING:
				return "Filling.Weaving";
			case SYMBOLS:
				return "Filling.Symbol";
			case IMAGE:
				return "Filling.Image";
			case STANDARD:
			default:
				return "Filling.Standard";
		}
	}

	private void updateFillType(FillType newFillType) {
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
		ChartStyle chartStyle = isChart() ?
			((ChartStyleAlgo) geo0.getParentAlgorithm()).getStyle() : null;
		if (chartStyle != null) {
			setBarFillType(geo0);
		} else {
			fillListener.setSelectedIndex(geo0.getFillType().ordinal());
		}

		// set selected fill type to first geo's fill type
		fillListener.setFillInverseSelected(geo0.isInverseFill());


		if (chartStyle != null) {
			updateBarFillTypePanel(geo0, chartStyle);
			fillListener.setBarChart(((ChartStyleAlgo) geo0.getParentAlgorithm()).getIntervals());
		} else {
			updateFillType(geo0.getFillType());
			fillListener.setBarChart(-1);
		}

		// set value to first geo's alpha value
		double alpha = geo0.getAlphaValue();
		if (chartStyle != null) {
			setAlpha(chartStyle, alpha);
		} else {
			fillListener.setFillValue((int) Math.round(alpha * 100));
		}
		double angle = geo0.getHatchingAngle();
		if (chartStyle != null) {
			setBarAngle(chartStyle, angle);
		} else {
			fillListener.setAngleValue((int) angle);
		}

		int distance = geo0.getHatchingDistance();
		if (chartStyle != null) {
			setBarDistance(chartStyle, distance);
		} else {
			fillListener.setDistanceValue(distance);
		}

		if (chartStyle != null) {
			fillListener.selectSymbol(
					chartStyle.getBarSymbol(fillListener.getSelectedBarIndex()));
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

	private void setAlpha(ChartStyle algo, double alpha0) {
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

	private void updateBarFillTypePanel(GeoElement geo, ChartStyle style) {
		int idx = getFillingListener().getSelectedBarIndex();

		FillType type;
		if (idx == 0) {
			type = geo.getFillType();
		} else {
			type = style.getBarFillType(idx);
		}

		fillType = type;
		updateFillType(type);
	}

	private void setBarDistance(ChartStyle algo, int distance0) {
		int distance = distance0;
		int idx = getFillingListener().getSelectedBarIndex();
		if (idx != 0) {
			if (algo.getBarHatchDistance(idx) != -1) {
				distance = algo.getBarHatchDistance(idx);
			}
		}
		getFillingListener().setDistanceValue(distance);
	}

	private void setBarAngle(ChartStyle algo, double angle0) {
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
			ChartStyleAlgo algo = (ChartStyleAlgo) geo.getParentAlgorithm();
			if (algo != null && algo.getStyle().getBarFillType(idx) != null) {
				getFillingListener()
						.setSelectedIndex(algo.getStyle().getBarFillType(idx).ordinal());
			}
		}
	}

	public void applyImage(String fileName) {
		if (fileName == null) {
			return;
		}
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (isChart()) {
				if (!updateBarsFillType(geo, FillingProperty.IMAGE, fileName)) {
					setImage(geo, fileName);
				}
			} else {
				setImage(geo, fileName);
			}
			geo.updateRepaint();
		}

	}

	private void setImage(GeoElement geo, String fileName) {
		geo.setImageFileName(fileName);
		geo.setAlphaValue(fileName.isEmpty() ? 0.0f : 1.0f);
	}

	public void applyUnicode(String symbolText) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			if (!"".equals(symbolText)) {
				if (isChart()) {
					if (!updateBarsFillType(geo, FillingProperty.SYMBOL, null)) {
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
			if (isChart()) {
				updateBarsFillType(geo, FillingProperty.ALPHA, null);
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
			if (isChart()) {
				if (!updateBarsFillType(geo, FillingProperty.FILL_TYPE, null)) {
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
			if (isChart()) {
				if (!updateBarsFillType(geo, FillingProperty.FILL_TYPE, null)) {
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

	private boolean updateBarsFillType(GeoElement geo, FillingProperty type,
			String fileName) {
		int selectedBarIndex = getFillingListener().getSelectedBarIndex();
		ChartStyle algo = ((ChartStyleAlgo) geo.getParentAlgorithm()).getStyle();
		if (selectedBarIndex == 0) {
			int numBar = ((ChartStyleAlgo) geo.getParentAlgorithm()).getIntervals();
			for (int i = 1; i < numBar + 1; i++) {
				algo.setBarFillType(null, i);
				algo.setBarHatchDistance(-1, i);
				algo.setBarHatchAngle(-1, i);
				algo.setBarSymbol(null, i);
				algo.setBarImage(null, i);
			}
			return false;
		}
		switch (type) {
		default:
		case FILL_TYPE:
			algo.setBarFillType(getFillType(), selectedBarIndex);
			algo.setBarHatchDistance(getDistanceValue(), selectedBarIndex);
			algo.setBarHatchAngle(getAngleValue(), selectedBarIndex);
			algo.setBarImage(null, selectedBarIndex);
			if (getSelectedFillType() == FillType.SYMBOLS) {
				algo.setBarFillType(FillType.SYMBOLS, selectedBarIndex);
				algo.setBarHatchAngle(-1, selectedBarIndex);
				if (getSelectedSymbolText() != null
						&& !"".equals(getSelectedSymbolText())) {
					algo.setBarSymbol(getSelectedSymbolText(),
							selectedBarIndex);
				} else {
					algo.setBarSymbol("$", selectedBarIndex);
				}
			} else {
				algo.setBarSymbol(null, selectedBarIndex);
			}
			break;
		case ALPHA:
			algo.setBarAlpha(getFillingValue() / 100f, selectedBarIndex);
			break;
		case IMAGE:
			algo.setBarFillType(null, selectedBarIndex);
			algo.setBarHatchDistance(-1, selectedBarIndex);
			algo.setBarHatchAngle(-1, selectedBarIndex);
			algo.setBarSymbol(null, selectedBarIndex);
			algo.setBarImage(fileName, selectedBarIndex);
			algo.setBarFillType(FillType.IMAGE, selectedBarIndex);
			break;
		case SYMBOL:
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

	public boolean isChart() {
		return getGeoAt(0).getParentAlgorithm() instanceof ChartStyleAlgo;
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

	public FillType getFillTypeAt(int index) {
		return fillTypes.get(index);
	}
}
