package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.web.gui.view.algebra.SliderTreeItemInterface.CancelListener;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * Min/max setting for slider
 *
 */
public class MinMaxPanel extends AdvancedFlowPanel implements SetLabels,
		KeyHandler, MouseDownHandler, MouseUpHandler, CancelListener {
	private static volatile MinMaxPanel openedMinMaxPanel = null;

	/**
	 * @return current panel
	 */
	public static MinMaxPanel getOpenedPanel() {
		return openedMinMaxPanel;
	}
	/**
	 * Closes min/max/step settings panel of the slider and restores its size if
	 * needed.
	 */
	public static void closeMinMaxPanel() {
		closeMinMaxPanel(true);
	}

	/**
	 * Closes min/max/step settings panel of the slider.
	 * 
	 * @param restore
	 *            Decides if the item size should be restored (AV was too arrow
	 *            to fit min/max panel) or not.
	 */
	public static void closeMinMaxPanel(boolean restore) {
		if (openedMinMaxPanel == null) {
			return;
		}

		openedMinMaxPanel.hide(restore);
		openedMinMaxPanel = null;

	}

	/**
	 * Sets the currently open min/max panel of AV.
	 * 
	 * @param panel
	 *            current panel
	 */
	public void setOpenedMinMaxPanel(MinMaxPanel panel) {
		openedMinMaxPanel = panel;
	}


	/**
	 * Input field for MinMaxPanel
	 */
	static class AVField extends AutoCompleteTextFieldW {
		private CancelListener listener;

		/**
		 * @param columns
		 *            field width
		 * @param app
		 *            application
		 * @param listener
		 *            called on edit cancel
		 */
		public AVField(int columns, App app, CancelListener listener) {
			super(columns, app);
			this.listener = listener;
			setDeferredFocus(true);
			enableGGBKeyboard();

		}

		@Override
		public void onKeyPress(KeyPressEvent e) {
			e.stopPropagation();
		}

		@Override
		public void onKeyDown(KeyDownEvent e) {
			e.stopPropagation();
			if (e.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
				listener.cancel();
			}

		}

		@Override
		public void onKeyUp(KeyUpEvent e) {
			e.stopPropagation();
		}

		@Override
		protected void fieldFocus() {
			super.fieldFocus();
			selectAll();

		}

	}

	private SliderTreeItemInterface sliderTreeItem;
	private static final int MINMAX_MIN_WIDHT = 326;
	private AVField tfMin;
	private AVField tfMax;
	private AVField tfStep;
	private Label lblValue;
	private Label lblStep;
	private GeoNumeric num;
	private boolean keepOpen = false;
	private boolean focusRequested = false;
	private Kernel kernel;
	private App app;

	/**
	 * @param item
	 *            parent tree item
	 */
	public MinMaxPanel(SliderTreeItemInterface item) {
		this.sliderTreeItem = item;
		num = (GeoNumeric) this.sliderTreeItem.getGeo();
		kernel = num.getKernel();
		app = kernel.getApplication();

		tfMin = new AVField(4, app,
				this);
		tfMax = new AVField(4, app,
				this);
		tfStep = new AVField(4, app,
				this);
		lblValue = new Label(Unicode.LESS_EQUAL + " "
				+ num
						.getCaption(StringTemplate.defaultTemplate)
				+ " "
				+ Unicode.LESS_EQUAL);
		// content set in update()->setLabels()
		lblStep = new Label();
		addStyleName("minMaxPanel");
		add(tfMin);
		add(lblValue);
		add(tfMax);
		add(lblStep);
		add(tfStep);

		tfMin.setDeferredFocus(true);
		tfMax.setDeferredFocus(true);
		tfStep.setDeferredFocus(true);

		tfMin.addKeyHandler(this);
		tfMax.addKeyHandler(this);
		tfStep.addKeyHandler(this);


		tfStep.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				stepFocused(event);
			}
		});

		addMouseDownHandler(this);
		addMouseUpHandler(this);
		addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {

				hide();
			}
		});

		update();

	}

	/**
	 * @param event
	 *            focus event in step field
	 */
	protected void stepFocused(FocusEvent event) {
		if (focusRequested) {
			event.preventDefault();
			event.stopPropagation();
			return;
		}
		tfStep.selectAll();

	}

	/**
	 * Update UIfrom geo
	 */
	public void update() {
		tfMin.setText(kernel.format(num.getIntervalMin(),
				StringTemplate.editTemplate));
		tfMax.setText(kernel.format(num.getIntervalMax(),
				StringTemplate.editTemplate));
		tfStep.setText(
				num.isAutoStep() ? "" : kernel.format(
				num.getAnimationStep(), StringTemplate.editTemplate));
		setLabels();
	}

	@Override
	public void setLabels() {
		lblStep.setText(app.getLocalization().getMenu("Step"));
	}

	/**
	 * Show the panel
	 */
	public void show() {
		num.setAnimating(false);
		this.sliderTreeItem.expandSize(MINMAX_MIN_WIDHT);
		this.sliderTreeItem.setSliderVisible(false);
		setVisible(true);
		setKeepOpen(true);
		setOpenedMinMaxPanel(this);
		this.sliderTreeItem.setAnimPanelVisible(false);
	}

	/**
	 * @param restore
	 *            whether to restore size
	 */
	private void hide(boolean restore) {
		if (restore) {
			this.sliderTreeItem.restoreSize();
		}
		hide();
	}

	/**
	 * Hide the panel
	 */
	public void hide() {
		this.sliderTreeItem.setSliderVisible(true);
		this.sliderTreeItem.deferredResize();
		setVisible(false);
		this.sliderTreeItem.setAnimPanelVisible(true);

	}


	@Override
	public void keyReleased(KeyEvent e) {
		if (e.isEnterKey()) {
			apply();
		}
	}

	private void apply() {
		NumberValue min = getNumberFromInput(tfMin.getText().trim());
		NumberValue max = getNumberFromInput(tfMax.getText().trim());
		String stepText = tfStep.getText().trim();

		if (min != null && max != null
				&& min.getDouble() <= max.getDouble()) {
			num.setIntervalMin(min);
			num.setIntervalMax(max);
			if (stepText.isEmpty()) {
				num.setAutoStep(true);
			} else {
				num.setAutoStep(false);
				num.setAnimationStep(getNumberFromInput(stepText));
			}
			num.update();
			hide(true);
		}
	}

	// TODO: refactor needed: copied from SliderPanelW;
	private NumberValue getNumberFromInput(final String inputText) {
		boolean emptyString = "".equals(inputText);
		NumberValue value = null;// new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor()
					.evaluateToNumeric(
					inputText, false);
		}

		return value;
	}

	@Override
	public void cancel() {
		hide();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (isKeepOpen()) {
			setKeepOpen(false);
			return;
		}

		if (focusRequested) {
			focusRequested = false;
			return;
		}

		event.preventDefault();
		event.stopPropagation();

		if (!(selectAllOnFocus(tfMin, event)
				|| selectAllOnFocus(tfMax, event)
				|| selectAllOnFocus(tfStep, event))) {
			apply();
		}

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			return;
		}

		event.preventDefault();
		event.stopPropagation();

		selectAllOnFocus(tfMin, event);
		selectAllOnFocus(tfMax, event);
		selectAllOnFocus(tfStep, event);

	}

	private static boolean selectAllOnFocus(AVField avField,
			MouseEvent<?> event) {
		if (RadioTreeItemController.isWidgetHit(avField, event)) {
			avField.selectAll();
			return true;
		}
		return false;
	}

	private boolean isKeepOpen() {
		return keepOpen;
	}

	private void setKeepOpen(boolean keepOpen) {
		this.keepOpen = keepOpen;
	}

	/**
	 * Focus min field
	 */
	public void setMinFocus() {
		tfMin.requestFocus();
		focusRequested = true;
	}

	/**
	 * Focus max field
	 */
	public void setMaxFocus() {
		tfMax.requestFocus();
		focusRequested = true;
	}
}