package org.geogebra.web.web.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.util.FrameCollectorW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;

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
	 * private String toJSON() { StringBuilder sb = new StringBuilder();
	 * sb.append("{ \"request\": {"); sb.append("    \"-api\": \"1.0.0\",");
	 * sb.append("    \"task\": {");
	 * sb.append("      \"-type\": \"convertGGBToGIF\",");
	 * sb.append("      \"file\": { \"-base64\": \"" +
	 * app.getGgbApi().getBase64() + " \" },");
	 * sb.append("      \"slidername\": \"a\""); sb.append("    }");
	 * sb.append("  }"); sb.append("		}"); return sb.toString(); } The list of
	 * sliders.
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

	private GeoPointND createExportPoint(int idx, int corner) {
		GeoElement p = app.getKernel().lookupLabel("Export_" + idx);
		if (p instanceof GeoPointND) {
			return (GeoPointND) p;
		}

		GeoPointND ret = app
		        .getKernel()
		        .getAlgebraProcessor()
		        .evaluateToPoint(
		                "Export_" + idx + "=CopyFreeObject[Corner[" + corner
		                        + "]]",
		                false, false);

		ret.setEuclidianVisible(false);

		return ret;

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

		GeoGebraTubeAPIW api = new GeoGebraTubeAPIW(app.getClientInfo(),
				app.has(Feature.TUBE_BETA));
		String sliderName = sliderComboBox.getSelectedValue().split(" ")[0];
		GeoPointND p1 = createExportPoint(1, 1);
		GeoPointND p2 = createExportPoint(2, 3);
		api.exportAnimGif(app, sliderName, timeBetweenFrames, isLoop.getValue());
		p1.remove();
		p2.remove();

	}


}
