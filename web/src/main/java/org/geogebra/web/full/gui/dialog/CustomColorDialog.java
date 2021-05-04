package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.html5.javax.swing.GSpinnerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.base.Js;

public class CustomColorDialog extends DialogBoxW implements SetLabels {
	
	private static final int PREVIEW_HEIGHT = 40;
	private static final int PREVIEW_WIDTH = 258;
	private ColorComponent red;
	private ColorComponent green;
	private ColorComponent blue;
	
	private FlowPanel mainWidget;
	private GColor origColor;
	private PreviewPanel preview;
	private Button btnOk;
	private Button btnCancel;
	private Button btnReset;
	private Localization loc;
	private ICustomColor listener;

	public interface ICustomColor {
		GColor getSelectedColor();

		void onCustomColor(GColor color);
	}

	private class ColorComponent extends FlowPanel {
		private Slider slider;
		private GSpinnerW spinner;

		public ColorComponent() {
			setStyleName("colorComponent");

			FlowPanel sp = new FlowPanel();

			Label minLabel = new Label("0");
			slider = new Slider(0, 255);
			slider.setTickSpacing(1);
			Label maxLabel = new Label("255");

			sp.setStyleName("colorSlider");
			sp.add(minLabel);
			sp.add(slider);
			sp.add(maxLabel);
	
			spinner = new GSpinnerW();
			spinner.setMinValue(0);
			spinner.setMaxValue(255);
			spinner.setStepValue(1);
			add(sp);			
			add(spinner);
					
			spinner.addChangeHandler(event -> {
				slider.setValue(Integer.parseInt(spinner.getValue()));
				preview.update();
			});
			slider.addChangeHandler(event -> {
				spinner.setValue(slider.getValue().toString());
				preview.update();
			});
		}
		
		public void setValue(Integer value) {
			slider.setValue(value);
			spinner.setValue(value.toString());
		}

		public int getValue() {
			return slider.getValue();
		}
	}
	
	private class PreviewPanel extends FlowPanel {
		private Label title;
		private Canvas canvas;
		private CanvasRenderingContext2D ctx;

		public PreviewPanel(GColor oColor) {
			setStyleName("CustomColorPreview");
			title = new Label();
			if (getApplication().isWhiteboardActive()) {
				title.addStyleName("previewLbl");
			}
			add(title);
			canvas = Canvas.createIfSupported();
			canvas.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
			canvas.setCoordinateSpaceHeight(PREVIEW_HEIGHT);
			canvas.setCoordinateSpaceWidth(PREVIEW_WIDTH * 2);
			ctx = Js.uncheckedCast(canvas.getContext2d());
			add(canvas);
			reset(oColor);
		}

		/**
		 * Reset both color rectangles to original color.
		 * 
		 * @param oColor
		 *            color for both rectangles
		 */
		public void reset(GColor oColor) {
			drawRect(0, oColor);
			drawRect(PREVIEW_WIDTH, oColor);
		}

		public void update() {
			drawRect(PREVIEW_WIDTH, getColor());
		}
		
		protected void drawRect(int x, GColor color) {
			String htmlColor = StringUtil.toHtmlColor(color);
			ctx.fillStyle = BaseRenderingContext2D.FillStyleUnionType.of(htmlColor);
			ctx.globalAlpha = 1.0;
			ctx.fillRect(x, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		}

		@Override
		public void setTitle(String text) {
			title.setText(text);
		}
	}

	/**
	 * Create new color dialog.
	 * 
	 * @param app
	 *            application
	 * @param listener
	 *            custom color listener
	 */
	public CustomColorDialog(App app, ICustomColor listener) {
		super(((AppW) app).getPanel(), app);
		this.listener = listener;
		loc = app.getLocalization();
		setWidget(mainWidget = new FlowPanel());
		addStyleName("GeoGebraPopup");
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName(app.isWhiteboardActive() ? "ColorChooser mow"
					: "ColorChooser");
		}
		this.origColor = listener.getSelectedColor();
		createGUI();
	}
	
	/**
	 * @return custom color
	 */
	public GColor getColor() {
		return GColor.newColor(red.getValue(), green.getValue(),
				blue.getValue());
	}

	protected void createGUI() {
		FlowPanel contents = new FlowPanel();
		contents.setStyleName("ColorDialog-content");
		red = new ColorComponent();
		green = new ColorComponent();
		blue = new ColorComponent();
		setOriginalValues();
		contents.add(red);
		contents.add(green);
		contents.add(blue);
		preview = new PreviewPanel(origColor);
		contents.add(preview);	
		mainWidget.add(contents);
		
		FlowPanel btnPanel = new FlowPanel();
		btnOk = new Button();
		btnCancel = new Button();
		btnCancel.addStyleName("cancelBtn");
		btnReset = new Button();
		btnReset.addStyleName("resetBtn");
		btnPanel.addStyleName("DialogButtonPanel");
		btnPanel.add(btnOk);
		btnPanel.add(btnCancel);
		btnPanel.add(btnReset);
		mainWidget.add(btnPanel);
		
		btnOk.addClickHandler(event -> {
			if (listener != null) {
				listener.onCustomColor(getColor());
			}
			hide();
		});
		btnCancel.addClickHandler(event -> hide());
		
		btnReset.addClickHandler(event -> reset());
		setLabels();
	}

	protected void reset() {
		setOriginalValues();
		preview.update();
	}

	@Override
	public void setLabels() {
		setTitle(loc.getMenu("ChooseColor"));
		this.getCaption().setText(loc.getMenu("ChooseColor"));
		red.setTitle(StringUtil.capitalize(loc.getColor("red")));
		green.setTitle(StringUtil.capitalize(loc.getColor("green")));
		blue.setTitle(StringUtil.capitalize(loc.getColor("blue")));
		preview.setTitle(loc.getMenu("Preview"));
		btnOk.setText(loc.getMenu("OK"));
		btnCancel.setText(loc.getMenu("Cancel"));
		btnReset.setText(loc.getMenu("Reset"));
	}

	/**
	 * Update textfield from original color
	 */
	protected void setOriginalValues() {
		red.setValue(origColor.getRed());
		green.setValue(origColor.getGreen());
		blue.setValue(origColor.getBlue());
	}

	/**
	 * Show and initialize with a color.
	 * 
	 * @param color
	 *            new initial color
	 */
	public void show(GColor color) {
		this.origColor = color;
		setOriginalValues();
		preview.reset(origColor);
		setLabels();
		super.center();
	}
}