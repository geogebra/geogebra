package geogebra.web.export;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gawt.BufferedImage;
import geogebra.html5.js.JavaScriptInjector;
import geogebra.web.gui.util.AnimatedGifEncoderW;
import geogebra.web.gui.util.FrameCollectorW;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author bencze
 *
 */
public class AnimationExportDialogW extends DialogBox implements ClickHandler {

	private AppW app;
	private VerticalPanel panel;
	private HorizontalPanel sliderPanel;
	private FlowPanel optionsPanel;
	private FlowPanel bottomPanel;
	private ListBox comboBox;
	
	private Button saveBtn;
	private Button cancelBtn;
	private InputPanelW frames;
	private CheckBox isLoop;
	
	private List<GeoElement> geoNumerics;
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

	private void initGUI() {
		addStyleName("GeoGebraPopup");
		add(panel = new VerticalPanel());
		panel.add(sliderPanel = new HorizontalPanel()); 
		sliderPanel.add(new Label(app.getPlain("Slider") + ":"));
		sliderPanel.add(comboBox = new ListBox());

		panel.add(optionsPanel = new FlowPanel());
		optionsPanel.add(new Label(app.getPlain("TimeBetweenFrames") + ":"));
		optionsPanel.add(frames = new InputPanelW("500", app, 5, false));
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
	}
	
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
				comboBox.addItem(geo.toString(StringTemplate.defaultTemplate));
				geoNumerics.add(geo);
			}
		}
		selectedGeo = 0;
		if (geoNumerics.size() == 0) {
			isLoop.setEnabled(false);
			frames.setEnabled(false);
			saveBtn.setEnabled(false);
		}
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == cancelBtn) { // cancel button clicked
	    	hide();
	    } else { // save button clicked
	    	export();
	    }
    }
	
	private void export() {
		int timeBetweenFrames = 500;

		// try to parse textfield value (and check that it is > 0)
		try {
			timeBetweenFrames = Integer.parseInt(frames.getText());

			// negative values or zero are bad too
			if (timeBetweenFrames <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			app.showError("InvalidInput", frames.getText());
			return;
		}

		app.getKernel().getAnimatonManager().stopAnimation();
		JavaScriptInjector.inject(GuiResources.INSTANCE.gifJs());
		
		//File file = null;
				/*((GuiManagerD) app.getGuiManager()).showSaveDialog(
				"gif", // change to Application.FILE_EXT_GIF
				null, app.getPlain("gif") + " " + app.getMenu("Files"), true,
				false);*/

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
		
		

		final AnimatedGifEncoderW gifEncoder = new AnimatedGifEncoderW(timeBetweenFrames, isLoop.getValue(), "");
		
		FrameCollectorW collector = new FrameCollectorW() {

			public void addFrame(BufferedImage img) {
				gifEncoder.addFrame(img);

			}

			public void finish() {
				gifEncoder.finish();

			}
		};
		// hide dialog
		setVisible(false);

		app.setWaitCursor();

		try {
			
			app.exportAnimatedGIF(collector, num, n, val, min, max, step);

		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}
	}

}
