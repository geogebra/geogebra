package geogebra.web.export;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.util.debug.Log;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.dialog.DialogBoxW;
import geogebra.web.gui.util.AnimatedGifEncoderW;
import geogebra.web.gui.util.FrameCollectorW;
import geogebra.web.gui.view.algebra.InputPanelW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author bencze
 *
 */
public class AnimationExportDialogW extends DialogBoxW implements ClickHandler {

	/**
	 * Application
	 */
	private AppW app;

	/**
	 * Vertical panel containing sub-panels.
	 */
	private VerticalPanel panel;

	/**
	 * Panel containing the combo box for selecting the slider.
	 */
	private HorizontalPanel sliderPanel;

	/**
	 * Panel containing options elements.
	 */
	private HorizontalPanel optionsPanel;

	/**
	 * Panel containing the OK and Cancel button
	 */
	private FlowPanel bottomPanel;

	/**
	 * Combo box for selecting the slider.
	 */
	private ListBox sliderComboBox;

	/**
	 * Button exports the GIF.
	 */
	private Button saveBtn;

	/**
	 * Cancels exporting and closes the window.
	 */
	private Button cancelBtn;

	/**
	 * The time in milliseconds between the frames.
	 */
	private InputPanelW timeBetweenFramesInput;

	/**
	 * Checkbox to set the GIF to infinite loop
	 */
	private CheckBox isLoop;

	/**
	 * The list of sliders.
	 */
	private List<GeoElement> geoNumerics;

	/**
	 * The index of the selected slider.
	 */
	private int selectedGeo;

	/**
	 * @param app
	 *            Application
	 */
	public AnimationExportDialogW(AppW app) {
		super();
		this.app = app;
		geoNumerics = new ArrayList<GeoElement>();
		initGUI();
		refreshGUI();
	}

	public void exportAnimatedGIF(FrameCollectorW gifEncoder, GeoNumeric num,
	        int n, double val, double min, double max, double step) {
		Log.debug("exporting animation");
		for (int i = 0; i < n; i++) {

			// avoid values like 14.399999999999968
			val = Kernel.checkDecimalFraction(val);
			num.setValue(val);
			num.updateRepaint();

			String url = ((EuclidianViewW) app.getActiveEuclidianView())
			        .getExportImageDataUrl(1, false);
			if (url == null) {
				Log.error("image null");
			} else {
				gifEncoder.addFrame(url);
			}
			val += step;

			if (val > max + 0.00000001 || val < min - 0.00000001) {
				val -= 2 * step;
				step *= -1;
			}

		}
		gifEncoder.finish();
	}

	private void initGUI() {
		addStyleName("GeoGebraPopup");
		add(panel = new VerticalPanel());
		panel.add(sliderPanel = new HorizontalPanel());
		sliderPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sliderPanel.add(new Label(app.getPlain("Slider") + ":"));
		sliderPanel.add(sliderComboBox = new ListBox());

		panel.add(optionsPanel = new HorizontalPanel());
		optionsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		optionsPanel.add(new Label(app.getPlain("TimeBetweenFrames") + ":"));
		optionsPanel.add(timeBetweenFramesInput = new InputPanelW("500", app,
		        5, false));
		optionsPanel.add(new Label("ms"));
		optionsPanel.add(isLoop = new CheckBox(app.getPlain("AnimationLoop")));

		panel.add(bottomPanel = new FlowPanel());
		bottomPanel.add(saveBtn = new Button(app.getPlain("Apply")));
		bottomPanel.add(cancelBtn = new Button(app.getPlain("Cancel")));

		// buttons
		saveBtn.addClickHandler(this);
		cancelBtn.addClickHandler(this);

		bottomPanel.setStyleName("DialogButtonPanel");

		getCaption().setText(app.getPlain("AnimatedGIFExport"));
		setGlassEnabled(true);
		isLoop.getElement().getStyle().setMarginLeft(15, Unit.PX);
	}

	/**
	 * This method should be called before reusing this dialog.
	 */
	public void refreshGUI() {
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
		        .getGeoSetNameDescriptionOrder();

		// lists for combo boxes to select input and output objects
		// fill combobox models
		Iterator<GeoElement> it = sortedSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoNumeric() && ((GeoNumeric) geo).isIntervalMinActive()
			        && ((GeoNumeric) geo).isIntervalMaxActive()) {
				sliderComboBox.addItem(geo
				        .toString(StringTemplate.defaultTemplate));
				geoNumerics.add(geo);
			}
		}
		selectedGeo = 0;
		boolean enabled = geoNumerics.size() != 0;
		timeBetweenFramesInput.getTextComponent().setText("500");

		isLoop.setEnabled(enabled);
		timeBetweenFramesInput.setEnabled(enabled);
		saveBtn.setEnabled(enabled);
		timeBetweenFramesInput.setEnabled(enabled);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == cancelBtn) { // cancel button clicked
			hide();
		} else { // save button clicked
			export();
			hide();
		}
	}

	private void export() {
		// implementation taken and modified from
		// :AnimationExportDialog.export()
		// TODO: factor out the export method to a common class
		int timeBetweenFrames = 500;

		// try to parse text field value (and check that it is > 0)
		try {
			timeBetweenFrames = Integer.parseInt(timeBetweenFramesInput
			        .getText());

			// negative values or zero are bad too
			if (timeBetweenFrames <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			app.showError("InvalidInput", timeBetweenFramesInput.getText());
			return;
		}

		app.getKernel().getAnimatonManager().stopAnimation();

		GeoNumeric num = (GeoNumeric) geoNumerics.get(selectedGeo);

		int type = num.getAnimationType();
		double min = num.getIntervalMin();
		double max = num.getIntervalMax();

		double val;

		double step;
		int n;

		switch (type) {
		case GeoElement.ANIMATION_DECREASING:
			step = -num.getAnimationStep();
			n = (int) ((max - min) / -step);
			if (Kernel.isZero(((max - min) / -step) - n))
				n++;
			if (n == 0)
				n = 1;
			val = max;
			break;
		case GeoElement.ANIMATION_OSCILLATING:
			step = num.getAnimationStep();
			n = (int) ((max - min) / step) * 2;
			if (Kernel.isZero(((max - min) / step * 2) - n))
				n++;
			if (n == 0)
				n = 1;
			val = min;
			break;
		default: // GeoElement.ANIMATION_INCREASING:
			     // GeoElement.ANIMATION_INCREASING_ONCE:
			step = num.getAnimationStep();
			n = (int) ((max - min) / step);
			if (Kernel.isZero(((max - min) / step) - n))
				n++;
			if (n == 0)
				n = 1;
			val = min;
		}

		final AnimatedGifEncoderW gifEncoder = new AnimatedGifEncoderW(
		        timeBetweenFrames, isLoop.getValue());

		FrameCollectorW collector = new FrameCollectorW() {

			public void addFrame(String url) {
				gifEncoder.addFrame(url);
			}

			public void finish() {
				gifEncoder.finish();
			}
		};
		// hide dialog
		setVisible(false);

		app.setWaitCursor();

		try {
			this.exportAnimatedGIF(collector, num, n, val, min, max, step);
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}
	}

}
