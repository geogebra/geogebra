package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.gui.util.Slider;
import org.geogebra.web.html5.javax.swing.GSpinnerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class CustomColorDialog extends DialogBoxW {
	
	public interface ICustomColor {
		GColor getSelectedColor();
		void onCustomColor(GColor color);
    };
    
	private static final int PREVIEW_HEIGHT = 40;
	private static final int PREVIEW_WIDTH = 258;
	private ColorComponent red;
	private ColorComponent green;
	private ColorComponent blue;
	
	private FlowPanel mainWidget;
	private GColor origColor;
	private GColorW color;
	private PreviewPanel preview;
	private Button btnOk;
	private Button btnCancel;
	private Button btnReset;
	private App app;
	private Localization loc;
	private ICustomColor listener;
    private class ColorComponent extends FlowPanel {
		private Slider slider;
		private GSpinnerW spinner;
		public ColorComponent() {
			setStyleName("colorComponent");
			
			FlowPanel sp = new FlowPanel();
			
			Label minLabel = new Label("0");
			slider = new Slider(0, 255);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			
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
					
			spinner.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
	                slider.setValue(Integer.parseInt(spinner.getValue()));
	                preview.update();
                }});
			slider.addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
	                spinner.setValue(slider.getValue().toString());
	                preview.update();
                }});
			
			
	
		}
		
		public void setValue(Integer value) {
			slider.setValue(value);
			spinner.setValue(value.toString());
		}

		public int getValue() {
	        // TODO Auto-generated method stub
	        return slider.getValue();
        }
	}
	
	private class PreviewPanel extends FlowPanel {
		private Label title;
		private Canvas canvas;
		private Context2d ctx;

		public PreviewPanel() {
			setStyleName("CustomColorPreview");
			title = new Label();
			add(title);
			canvas = Canvas.createIfSupported();
			canvas.setSize(PREVIEW_WIDTH + "px", PREVIEW_HEIGHT + "px");
			canvas.setCoordinateSpaceHeight(PREVIEW_HEIGHT);
			canvas.setCoordinateSpaceWidth(PREVIEW_WIDTH * 2);
			ctx = canvas.getContext2d();
			add(canvas);
			drawRect(0, origColor);
			drawRect(PREVIEW_WIDTH, origColor);
		}

		public void reset() {
			drawRect(0, origColor);
			drawRect(PREVIEW_WIDTH, origColor);
		}

		public void update(){
			drawRect(PREVIEW_WIDTH, getColor());
		}
		
		protected void drawRect(int x, GColor color) {
			
			String htmlColor = StringUtil.toHtmlColor(color);
			
			ctx.setFillStyle(htmlColor);

			ctx.setGlobalAlpha(1.0);
			ctx.fillRect(x, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
			
		}
		
		public void setTitle(String text) {
			title.setText(text);
		}
		
	}

	public CustomColorDialog(App app, ICustomColor listener) {
		super(((AppW) app).getPanel());
		this.app = app;
		this.listener = listener;
		loc = app.getLocalization();
		setWidget(mainWidget = new FlowPanel());
		addStyleName("GeoGebraPopup");
		this.origColor = listener.getSelectedColor();
		createGUI();
	}
	
	public GColor getColor() {
		color.setRed(red.getValue());
		color.setGreen(green.getValue());
		color.setBlue(blue.getValue());
	    return color;
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
		preview = new PreviewPanel();
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
		
		btnOk.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (listener != null) {
					listener.onCustomColor(getColor());
				}
		            hide();	            
    
			}});
		btnCancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				hide();
            }});
		
		btnReset.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				reset();
            }});
		setLabels();

	}
	
	protected void reset() {
		setOriginalValues();
		preview.update();
    }

	public void setLabels() {
		setTitle(loc.getPlain("ChooseColor"));
		this.getCaption().setText(loc.getPlain("ChooseColor"));
		red.setTitle(loc.getMenu("Red"));
		green.setTitle(loc.getMenu("Green"));
		blue.setTitle(loc.getMenu("Blue"));
		preview.setTitle(loc.getMenu("Preview"));
		btnOk.setText(loc.getPlain("OK"));
		btnCancel.setText(loc.getMenu("Cancel"));
		btnReset.setText(loc.getMenu("Reset"));
	}
	
	protected void setOriginalValues(){
		red.setValue(origColor.getRed());
		green.setValue(origColor.getGreen());
		blue.setValue(origColor.getBlue());
		color = new GColorW(origColor.getRed(), origColor.getGreen(), origColor.getBlue());
	}

	public void show(GColor color) {
		this.origColor = color;
		setOriginalValues();
		preview.reset();
		setLabels();
		super.center();
	}
}
