package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem.AVField;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem.CancelListener;

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
		KeyHandler, MouseDownHandler, MouseUpHandler, RadioTreeItem.CancelListener {
	/**
	 * 
	 */
	private final RadioTreeItem radioTreeItem;
	private static final int MINMAX_MIN_WIDHT = 326;
	private AVField tfMin;
	private AVField tfMax;
	private AVField tfStep;
	private Label lblValue;
	private Label lblStep;
	private GeoNumeric num;
	private boolean keepOpen = false;
	private boolean focusRequested = false;
	public MinMaxPanel(RadioTreeItem radioTreeItem) {
		this.radioTreeItem = radioTreeItem;
		if (this.radioTreeItem.geo instanceof GeoNumeric) {
			num = (GeoNumeric) this.radioTreeItem.geo;
		}
		tfMin = this.radioTreeItem.new AVField(4, this.radioTreeItem.app, this);
		tfMax = this.radioTreeItem.new AVField(4, this.radioTreeItem.app, this);
		tfStep = this.radioTreeItem.new AVField(4, this.radioTreeItem.app, this);
		lblValue = new Label(RadioTreeItem.GTE_SIGN + " "
				+ this.radioTreeItem.geo.getCaption(StringTemplate.defaultTemplate) + " "
				+ RadioTreeItem.GTE_SIGN);
		lblStep = new Label(this.radioTreeItem.app.getPlain("Step"));
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
		tfMin.setText(this.radioTreeItem.kernel.format(num.getIntervalMin(),
				StringTemplate.editTemplate));
		tfMax.setText(this.radioTreeItem.kernel.format(num.getIntervalMax(),
				StringTemplate.editTemplate));
		tfStep.setText(num.isAutoStep() ? "" : this.radioTreeItem.kernel.format(
				num.getAnimationStep(), StringTemplate.editTemplate));
		setLabels();
	}

	public void setLabels() {
		lblStep.setText(this.radioTreeItem.app.getPlain("Step"));
	}



	public void show() {
		this.radioTreeItem.geo.setAnimating(false);
		this.radioTreeItem.expandSize(MINMAX_MIN_WIDHT);
		this.radioTreeItem.sliderPanel.setVisible(false);
		setVisible(true);
		setKeepOpen(true);
		RadioTreeItem.setOpenedMinMaxPanel(this);
		this.radioTreeItem.animPanel.setVisible(false);
	}

	public void hide(boolean restore) {
		if (restore) {
			this.radioTreeItem.restoreSize();
		}
		hide();
	}

	public void hide() {
		this.radioTreeItem.sliderPanel.setVisible(true);
		this.radioTreeItem.deferredResizeSlider();
		setVisible(false);
		if (this.radioTreeItem.animPanel != null) {
			this.radioTreeItem.animPanel.setVisible(true);
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
			value = this.radioTreeItem.kernel.getAlgebraProcessor().evaluateToNumeric(
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