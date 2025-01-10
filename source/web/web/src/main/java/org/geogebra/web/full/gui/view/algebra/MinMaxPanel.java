package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.util.DataTest;
import org.geogebra.web.html5.util.HasDataTest;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseEvent;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.user.client.ui.Label;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Min/max setting for slider
 *
 */
public class MinMaxPanel extends AdvancedFlowPanel implements SetLabels,
		KeyHandler, MouseDownHandler, MouseUpHandler, HasDataTest {
	private static volatile MinMaxPanel openedMinMaxPanel = null;
	private SliderTreeItemRetex sliderTreeItem;
	/** min width of the panel */
	public static final int MINMAX_MIN_WIDTH = 326;
	private MinMaxAVField tfMin;
	private MinMaxAVField tfMax;
	private MinMaxAVField tfStep;
	private Label lblValue;
	private Label lblStep;
	private GeoNumeric num;
	private boolean keepOpen = false;
	private boolean focusRequested = false;
	private Kernel kernel;
	private App app;

	/**
	 * @return whether the given panel is the currently open one
	 */
	public static boolean isOpenedPanel(MinMaxPanel panel) {
		return openedMinMaxPanel == panel;
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

	private static void setOpenedPanel(MinMaxPanel panel) {
		openedMinMaxPanel = panel;
	}

	/**
	 * @param item
	 *            parent tree item
	 */
	public MinMaxPanel(SliderTreeItemRetex item) {
		this.sliderTreeItem = item;
		num = (GeoNumeric) this.sliderTreeItem.getGeo();
		kernel = num.getKernel();
		app = kernel.getApplication();

		tfMin = new MinMaxAVField(this, 4, app);
		tfMax = new MinMaxAVField(this, 4, app);
		tfStep = new MinMaxAVField(this, 4, app);
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

		tfMin.addKeyHandler(this);
		tfMax.addKeyHandler(this);
		tfStep.addKeyHandler(this);

		tfStep.addFocusHandler(event -> stepFocused(event));

		addMouseDownHandler(this);
		addMouseUpHandler(this);
		addBlurHandler(event -> hide());
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
		setAlt(tfMin, "Minimum");
		setAlt(tfMax, "Maximum");
		setAlt(tfStep, "Step");
	}

	private void setAlt(MinMaxAVField fld, String key) {
		AriaHelper.setLabel(fld, app.getLocalization().getMenu(key));
	}

	/**
	 * Show the panel
	 */
	public void show() {
		num.setAnimating(false);
		this.sliderTreeItem.expandSize(MINMAX_MIN_WIDTH);
		this.sliderTreeItem.setSliderVisible(false);
		setVisible(true);
		setKeepOpen(true);
		setOpenedPanel(this);
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
		NumberValue value = null; // new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor()
					.evaluateToNumeric(
					inputText, false);
		}

		return value;
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

		event.stopPropagation();

		if (!(handleInputFieldMouseEvent(tfMin, event)
				|| handleInputFieldMouseEvent(tfMax, event)
				|| handleInputFieldMouseEvent(tfStep, event))) {
			apply();
		}

	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			return;
		}

		event.stopPropagation();

		handleInputFieldMouseEvent(tfMin, event);
		handleInputFieldMouseEvent(tfMax, event);
		handleInputFieldMouseEvent(tfStep, event);

	}

	private static boolean handleInputFieldMouseEvent(MinMaxAVField avField,
			MouseEvent<?> event) {
		if (RadioTreeItemController.isWidgetHit(avField, event)) {
			avField.updateCursorOverlay();
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

	@Override
	public void updateDataTest(int index) {
		DataTest.ALGEBRA_ITEM_SLIDER_MIN.applyWithIndex(tfMin, index);
		DataTest.ALGEBRA_ITEM_SLIDER_MAX.applyWithIndex(tfMax, index);
		DataTest.ALGEBRA_ITEM_SLIDER_STEP.applyWithIndex(tfStep, index);
	}
}
