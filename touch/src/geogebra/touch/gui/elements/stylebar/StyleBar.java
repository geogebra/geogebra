package geogebra.touch.gui.elements.stylebar;

import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.LineProperties;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.euclidian.EuclidianViewM;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.StyleBarDefaultSettings;

import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StyleBar extends FlowPanel {

    /**
     * Enum of allowed Entries to the StyleBar
     * 
     * @author Matthias Meisinger
     * 
     */
    enum StyleBarEntry {
	Axes, Grid, Color, LineStyle, CaptionStyle;
    }

    private static DefaultResources lafIcons = TouchEntryPoint.getLookAndFeel().getIcons();

    private HorizontalPanel contentPanel, styleButtonsPanel;

    private Map<StyleBarEntry, StandardImageButton> buttons = new HashMap<StyleBarEntry, StandardImageButton>();
    private StandardImageButton showHideButton;

    EuclidianViewM euclidianView;
    private TouchModel touchModel;
    private GuiModel guiModel;

    /**
     * Initializes the StyleBar
     * 
     * @param TouchModel
     *            touchModel
     * 
     * @param EuclidianViewM
     *            view
     * @param EuclidianViewPanel
     *            euclidianViewPanel
     */
    public StyleBar(TouchModel touchModel, EuclidianViewM view) {
	this.setStyleName("stylebar");
	this.euclidianView = view;
	this.touchModel = touchModel;
	this.guiModel = touchModel.getGuiModel();

	this.contentPanel = new HorizontalPanel();
	this.styleButtonsPanel = new HorizontalPanel();

	this.showHideButton = new StandardImageButton(lafIcons.triangle_left());
	this.showHideButton.setStyleName("arrowLeft");

	this.showHideButton.addClickHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		StyleBar.this.showHide();
	    }
	});

	EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();

	if (this.guiModel.getCommand() != null && this.guiModel.getCommand().getStyleBarEntries() != null) {
	    this.rebuild(this.guiModel.getCommand().getStyleBarEntries());
	}

	this.contentPanel.add(this.styleButtonsPanel);
	this.contentPanel.add(this.showHideButton);
	this.add(this.contentPanel);

	// Prevent events from getting through to the canvas
	this.addDomHandler(new ClickHandler() {

	    @Override
	    public void onClick(ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
	    }
	}, ClickEvent.getType());

	this.addDomHandler(new MouseDownHandler() {

	    @Override
	    public void onMouseDown(MouseDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
	    }

	}, MouseDownEvent.getType());

	this.addDomHandler(new TouchStartHandler() {

	    @Override
	    public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		event.stopPropagation();
	    }

	}, TouchStartEvent.getType());
    }

    private void rebuild(StyleBarDefaultSettings entry) {

	SVGResource[] resource = entry.getResources();
	String color = entry.getColor() != null ? entry.getColor().toString() : "";

	if (entry == StyleBarDefaultSettings.Move && this.touchModel.getTotalNumber() > 0) {
	    color = this.touchModel.getSelectedGeos().get(0).getObjectColor().toString();
	    if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoPointND) {
		resource = StyleBarDefaultSettings.Point.getResources();
	    } else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof LineProperties) {
		resource = StyleBarDefaultSettings.Line.getResources();
	    } else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoPolygon) {
		resource = StyleBarDefaultSettings.Polygon.getResources();
	    } else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoAngle) {
		resource = StyleBarDefaultSettings.Angle.getResources();
	    }
	}

	this.buttons.clear();
	StandardImageButton b;

	for (SVGResource svg : resource) {
	    if (svg.equals(lafIcons.color())) {

		b = new StandardImageButton(lafIcons.color());
		b.getElement().getStyle().setBackgroundImage("initial");
		b.getElement().setAttribute("style", "background: " + color);
		b = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(b, this, OptionType.Color);
		this.buttons.put(StyleBarEntry.Color, b);

	    } else if (svg.equals(lafIcons.properties_default())) {

		b = new StandardImageButton(lafIcons.properties_default());
		b = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(b, this, OptionType.LineStyle);
		this.buttons.put(StyleBarEntry.LineStyle, b);

	    } else if (svg.equals(lafIcons.label())) {

		b = new StandardImageButton(lafIcons.label());
		b = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(b, this, OptionType.CaptionStyle);
		this.buttons.put(StyleBarEntry.CaptionStyle, b);

	    } else if (svg.equals(lafIcons.show_or_hide_the_axes())) {

		b = this.createStyleBarButton("showAxes", lafIcons.show_or_hide_the_axes());
		this.buttons.put(StyleBarEntry.Axes, b);

	    } else if (svg.equals(lafIcons.show_or_hide_the_grid())) {

		b = this.createStyleBarButton("showGrid", lafIcons.show_or_hide_the_grid());
		this.buttons.put(StyleBarEntry.Grid, b);
	    }

	    else {

	    }
	}

	this.styleButtonsPanel.clear();

	if (!this.buttons.isEmpty()) {

	    for (final StandardImageButton imageButton : this.buttons.values()) {
		this.styleButtonsPanel.add(imageButton);
	    }
	}
    }

    /**
     * 
     * @param String
     *            process
     * @param SVGResource
     *            svg
     * @return a new StandardImageButton for the StyleBar with OS specific
     *         EventHandler
     */
    private StandardImageButton createStyleBarButton(String process, SVGResource svg) {
	StandardImageButton newButton = new StandardImageButton(svg);

	newButton = TouchEntryPoint.getLookAndFeel().setStyleBarButtonHandler(newButton, this, process);

	return newButton;
    }

    public void rebuild() {

	if (this.guiModel.getCommand().getStyleBarEntries() != null) {
	    this.rebuild(this.guiModel.getCommand().getStyleBarEntries());
	    this.setVisible(true);
	} else {
	    // hide the whole StyleBar if no StyleBar is needed
	    this.setVisible(false);
	}
    }

    public void showHide() {

	if (this.styleButtonsPanel.isVisible()) {
	    // close all opened options before hiding the stylingbar
	    this.guiModel.closeOptions();

	    this.showHideButton.setStyleName("arrowRight");
	    // Set stylebar transparent, when closed
	    this.showHideButton.addStyleName("transparent");

	    this.styleButtonsPanel.setVisible(false);

	} else {

	    this.showHideButton.setStyleName("arrowLeft");
	    // Set stylebar nontransparent, when open
	    this.showHideButton.removeStyleName("transparent");

	    this.styleButtonsPanel.setVisible(true);
	}
    }

    public void onOptionalButtonEvent(StandardImageButton eventSource, OptionType type) {

	if (StyleBar.this.guiModel.getOptionTypeShown().equals(type)) {
	    StyleBar.this.guiModel.closeOptions();
	} else if (type.equals(OptionType.Color)) {
	    StyleBar.this.guiModel.showOption(new OptionsPanel(new ColorBarPanel(this, this.touchModel)), OptionType.Color,
		    this.buttons.get(OptionType.Color));
	} else if (type.equals(OptionType.LineStyle)) {
	    StyleBar.this.guiModel.showOption(new OptionsPanel(new LineStyleBar(this.touchModel, this)), OptionType.LineStyle, eventSource);
	} else if (type.equals(OptionType.CaptionStyle)) {
	    StyleBar.this.guiModel.showOption(new OptionsPanel(new CaptionBar(this.touchModel)), OptionType.CaptionStyle, eventSource);
	}
    }

    public void onStyleBarButtonEvent(StandardImageButton newButton, String process) {

	StyleBar.this.guiModel.closeOptions();
	EuclidianStyleBarStatic.processSourceCommon(process, null, StyleBar.this.euclidianView);

	newButton.setActive(!newButton.isActive());
    }

    public void updateColor(String color) {

	this.buttons.get(StyleBarEntry.Color).getElement().getStyle().setBackgroundImage("initial");
	this.buttons.get(StyleBarEntry.Color).getElement().getStyle().setBackgroundColor(color);

    }
}
