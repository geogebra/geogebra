package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.web.gui.view.algebra.SliderTreeItem.AVField;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Label;

class MinMaxPanel extends AdvancedFlowPanel implements SetLabels,
		KeyHandler, MouseDownHandler, MouseUpHandler,

		SliderTreeItem.CancelListener {
	/**
	 * 
	 */
	private final SliderTreeItem sliderTreeItem;
	private static final int MINMAX_MIN_WIDHT = 326;
	private AVField tfMin;
	private AVField tfMax;
	private AVField tfStep;
	private Label lblValue;
	private Label lblStep;
	private GeoNumeric num;
	private boolean keepOpen = false;
	private boolean focusRequested = false;

	public MinMaxPanel(SliderTreeItem item) {
		this.sliderTreeItem = item;
		if (this.sliderTreeItem.geo instanceof GeoNumeric) {
			num = (GeoNumeric) this.sliderTreeItem.geo;
		}
		tfMin = this.sliderTreeItem.new AVField(4, this.sliderTreeItem.app,
				this);
		tfMax = this.sliderTreeItem.new AVField(4, this.sliderTreeItem.app,
				this);
		tfStep = this.sliderTreeItem.new AVField(4, this.sliderTreeItem.app,
				this);
		lblValue = new Label(Unicode.LESS_EQUAL + " "
				+ this.sliderTreeItem.geo
						.getCaption(StringTemplate.defaultTemplate)
				+ " "
				+ Unicode.LESS_EQUAL);
		lblStep = new Label(this.sliderTreeItem.app.getPlain("Step"));
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

		tfMin.addFocusHandler(new FocusHandler() {

			public void onFocus(FocusEvent event) {
				tfMin.selectAll();
			}
		});
		tfMax.addFocusHandler(new FocusHandler() {

			public void onFocus(FocusEvent event) {
				tfMax.selectAll();
			}
		});
		tfStep.addFocusHandler(new FocusHandler() {

			public void onFocus(FocusEvent event) {
				if (focusRequested) {
					event.preventDefault();
					event.stopPropagation();
					return;
				}
				tfStep.selectAll();
			}
		});

		addMouseDownHandler(this);
		addMouseUpHandler(this);
		addBlurHandler(new BlurHandler() {

			public void onBlur(BlurEvent event) {

				hide();
			}
		});

		update();

	}

	public void update() {
		tfMin.setText(this.sliderTreeItem.kernel.format(num.getIntervalMin(),
				StringTemplate.editTemplate));
		tfMax.setText(this.sliderTreeItem.kernel.format(num.getIntervalMax(),
				StringTemplate.editTemplate));
		tfStep.setText(
				num.isAutoStep() ? "" : this.sliderTreeItem.kernel.format(
				num.getAnimationStep(), StringTemplate.editTemplate));
		setLabels();
	}

	public void setLabels() {
		lblStep.setText(this.sliderTreeItem.app.getPlain("Step"));
	}



	public void show() {
		this.sliderTreeItem.geo.setAnimating(false);
		this.sliderTreeItem.expandSize(MINMAX_MIN_WIDHT);
		this.sliderTreeItem.sliderPanel.setVisible(false);
		setVisible(true);
		setKeepOpen(true);
		sliderTreeItem.setOpenedMinMaxPanel(this);
		this.sliderTreeItem.animPanel.setVisible(false);
	}

	public void hide(boolean restore) {
		if (restore) {
			this.sliderTreeItem.restoreSize();
		}
		hide();
	}

	public void hide() {
		this.sliderTreeItem.sliderPanel.setVisible(true);
		this.sliderTreeItem.deferredResize();
		setVisible(false);
		if (this.sliderTreeItem.animPanel != null) {
			this.sliderTreeItem.animPanel.setVisible(true);
		}
	}


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
		boolean emptyString = inputText.equals("");
		NumberValue value = null;// new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = this.sliderTreeItem.kernel.getAlgebraProcessor()
					.evaluateToNumeric(
					inputText, false);
		}

		return value;
	}

	public void cancel() {
		hide();
	}

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

	private boolean selectAllOnFocus(AVField avField, MouseEvent event) {
		if (RadioTreeItem.isWidgetHit(avField, event)) {
			avField.selectAll();
			return true;
		}
		return false;
	}

	public boolean isKeepOpen() {
		return keepOpen;
	}

	public void setKeepOpen(boolean keepOpen) {
		this.keepOpen = keepOpen;
	}

	public void setMinFocus() {
		tfMin.requestFocus();
		focusRequested = true;
	}

	public void setMaxFocus() {
		tfMax.requestFocus();
		focusRequested = true;
	}
}