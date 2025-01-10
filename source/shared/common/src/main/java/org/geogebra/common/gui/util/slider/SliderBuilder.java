package org.geogebra.common.gui.util.slider;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Builds a slider.
 */
public class SliderBuilder {

	private GeoNumeric slider;

	private NumberInputHandler inputHandler;
	private ErrorHandler errorHandler;
	private Construction construction;

	private SliderData<String> input;
	private SliderData<GeoNumberValue> processedData;
	private GPoint2D location;

	/**
	 * @param algebraProcessor algebra processor
	 * @param errorHandler error handler
	 */
	public SliderBuilder(AlgebraProcessor algebraProcessor, ErrorHandler errorHandler) {

		this.construction = algebraProcessor.getConstruction();
		inputHandler = new NumberInputHandler(algebraProcessor);
		this.errorHandler = errorHandler;

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
		location = new GPoint2D(x, y);
		return this;
	}

	/**
	 * Creates the slider if the inputs are correct.
	 * @return The created slider.
	 */
	public GeoNumeric create() {
		boolean wasSuppressLabelsActive = construction.isSuppressLabelsActive();
		construction.setSuppressLabelCreation(true);
		processInputs();
		construction.setSuppressLabelCreation(wasSuppressLabelsActive);
		return slider;
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
		boolean wasSuppressLabelsActive = construction.isSuppressLabelsActive();
		construction.setSuppressLabelCreation(false);
		slider = GeoNumeric.setSliderFromDefault(new GeoNumeric(construction), false);
		slider.setLabel(null);
		construction.setSuppressLabelCreation(wasSuppressLabelsActive);
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
		slider.setAVSliderOrCheckboxVisible(true);
		slider.update();
	}
}
