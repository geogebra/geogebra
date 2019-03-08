package org.geogebra.common.gui.util.slider;

import org.geogebra.common.euclidian.smallscreen.AdjustSlider;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Builds a slider.
 */
public class SliderBuilder {

	private App app;
	private NumberInputHandler inputHandler;
	private ErrorHandler errorHandler;
	private Construction construction;

	private SliderData<String> input;
	private SliderData<GeoNumberValue> processedData;
	private Point2D location;

	/**
	 * @param app The app.
	 */
	public SliderBuilder(App app) {
		this.app = app;
		errorHandler = app.getDefaultErrorHandler();

		Kernel kernel = app.getKernel();
		inputHandler = new NumberInputHandler(kernel.getAlgebraProcessor());
		construction = kernel.getConstruction();

		input = new SliderData<>();
		processedData = new SliderData<>();
	}

	/**
	 * @param min The input for the minimum value.
	 * @return Itself
	 */
	public SliderBuilder withMin(String min) {
		input.setMin(min);
		return this;
	}

	/**
	 * @param max The input for the maximum value.
	 * @return Itself
	 */
	public SliderBuilder withMax(String max) {
		input.setMax(max);
		return this;
	}

	/**
	 * @param step The input for the step value.
	 * @return Itself
	 */
	public SliderBuilder withStep(String step) {
		input.setStep(step);
		return this;
	}

	/**
	 * @param x The x coordinate of the position.
	 * @param y The y coordinate of the position.
	 * @return Itself
	 */
	public SliderBuilder withLocation(double x, double y) {
		location = new Point2D(x, y);
		return this;
	}

	/**
	 * Creates the slider if the inputs are correct.
	 */
	public void create() {
		boolean wasSuppressLabelsActive = construction.isSuppressLabelsActive();
		construction.setSuppressLabelCreation(true);
		processInputs();
		construction.setSuppressLabelCreation(wasSuppressLabelsActive);
	}

	private void processInputs() {
		inputHandler.processInput(input.getMin(), errorHandler, getMaxProcessingCallback());
	}

	private AsyncOperation<Boolean> getMaxProcessingCallback() {
		return new AsyncOperation<Boolean>() {
			@Override
			public void callback(Boolean ok) {
				if (!ok) {
					return;
				}
				processedData.setMin(inputHandler.getNum());
				inputHandler.processInput(
						input.getMax(),
						errorHandler,
						getStepProcessingCallback());
			}
		};
	}

	private AsyncOperation<Boolean> getStepProcessingCallback() {
		return new AsyncOperation<Boolean>() {
			@Override
			public void callback(Boolean ok) {
				if (!ok) {
					return;
				}
				processedData.setMax(inputHandler.getNum());
				inputHandler.processInput(
						input.getStep(),
						errorHandler,
						getSliderSetupCallback());
			}
		};
	}

	private AsyncOperation<Boolean> getSliderSetupCallback() {
		return new AsyncOperation<Boolean>() {
			@Override
			public void callback(Boolean ok) {
				if (!ok) {
					return;
				}
				processedData.setStep(inputHandler.getNum());
				setupSlider(createSlider());
			}
		};
	}

	private GeoNumeric createSlider() {
		construction.setSuppressLabelCreation(false);
		final GeoNumeric slider =
				GeoNumeric.setSliderFromDefault(new GeoNumeric(construction), false);
		slider.setLabel(null);
		construction.setSuppressLabelCreation(true);
		return slider;
	}

	private void setupSlider(GeoNumeric slider) {
		slider.setIntervalMin(processedData.getMin());
		slider.setIntervalMax(processedData.getMax());
		slider.setAnimationStep(processedData.getStep());
		slider.setSliderLocation(location.getX(), location.getY(), true);

		slider.setAutoStep(false);
		slider.setEuclidianVisible(true);
		slider.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		slider.setLabelVisible(true);
		AdjustSlider.ensureOnScreen(slider, app.getActiveEuclidianView());
		slider.update();

		app.storeUndoInfo();
	}
}
