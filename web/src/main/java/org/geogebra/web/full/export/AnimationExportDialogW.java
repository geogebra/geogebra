package org.geogebra.web.full.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

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
	 * @param app
	 *            Application
	 */
	public AnimationExportDialogW(AppW app) {
		super(app.getPanel(), app);
		this.app = app;
		geoNumerics = new ArrayList<>();
		initGUI();
		refreshGUI();
	}

	private void initGUI() {
		Localization loc = app.getLocalization();
		addStyleName("GeoGebraPopup");
		add(panel = new VerticalPanel());
		panel.add(sliderPanel = new HorizontalPanel());
		sliderPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sliderPanel.add(new Label(loc.getMenu("Slider") + ":"));
		sliderPanel.add(sliderComboBox = new ListBox());

		panel.add(optionsPanel = new HorizontalPanel());
		optionsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		optionsPanel.add(new Label(loc.getMenu("TimeBetweenFrames") + ":"));
		timeBetweenFramesInput = new InputPanelW(app, 5, false);
		optionsPanel.add(timeBetweenFramesInput);
		optionsPanel.add(new Label("ms"));
		optionsPanel.add(isLoop = new CheckBox(loc.getMenu("AnimationLoop")));

		panel.add(bottomPanel = new FlowPanel());
		bottomPanel.add(saveBtn = new Button(loc.getMenu("Apply")));
		bottomPanel.add(cancelBtn = new Button(loc.getMenu("Cancel")));

		// buttons
		saveBtn.addClickHandler(this);
		cancelBtn.addClickHandler(this);
		cancelBtn.addStyleName("cancelBtn");

		bottomPanel.setStyleName("DialogButtonPanel");

		getCaption().setText(loc.getMenu("AnimatedGIFExport"));
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
		boolean enabled = geoNumerics.size() != 0;
		timeBetweenFramesInput.getTextComponent().setText("500");

		isLoop.setEnabled(enabled);
		timeBetweenFramesInput.setEnabled(enabled);
		saveBtn.setEnabled(enabled);
		timeBetweenFramesInput.setEnabled(enabled);
	}

	@Override
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
						ErrorHelper.silent(), false);

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
			app.showError(Errors.InvalidInput, timeBetweenFramesInput.getText());
			return;
		}

		GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation()
				.getGeoGebraTubeAPI();
		String sliderName = sliderComboBox.getSelectedValue().split(" ")[0];
		GeoPointND p1 = createExportPoint(1, 1);
		GeoPointND p2 = createExportPoint(2, 3);
		api.exportAnimGif((AppW) app, sliderName, timeBetweenFrames,
				isLoop.getValue());
		p1.remove();
		p2.remove();
		app.dispatchEvent(new Event(EventType.EXPORT, null, "[\"gif\"]"));
	}

}
