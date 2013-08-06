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
import geogebra.touch.utils.StyleBarEntries;
import geogebra.touch.utils.ToolBarCommand;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StyleBar extends PopupPanel {
    private static DefaultResources lafIcons = TouchEntryPoint.getLookAndFeel().getIcons();

    private HorizontalPanel contentPanel, styleButtonsPanel;

    private StandardImageButton[] buttons = new StandardImageButton[0];
    private StandardImageButton showHideButton, colorButton;

    EuclidianViewM euclidianView;
    private TouchModel touchModel;
    private GuiModel guiModel;

    private ToolBarCommand lastCommand;

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

	this.lastCommand = this.guiModel.getCommand();

	this.contentPanel.add(this.styleButtonsPanel);
	this.contentPanel.add(this.showHideButton);
	this.add(this.contentPanel);
    }

    private boolean rebuild(StyleBarEntries entry) {

	SVGResource[] resource = entry.getResources();
	String color = entry.getColor() != null ? entry.getColor().toString() : "";

	if (entry == StyleBarEntries.Move && this.touchModel.getTotalNumber() > 0) {
	    color = this.touchModel.getSelectedGeos().get(0).getObjectColor().toString();
	    if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoPointND) {
		resource = StyleBarEntries.Point.getResources();
	    } else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof LineProperties) {
		resource = StyleBarEntries.Line.getResources();
	    } else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoPolygon) {
		resource = StyleBarEntries.Polygon.getResources();
	    } else if (this.touchModel.getSelectedGeos().get(0).getGeoElementForPropertiesDialog() instanceof GeoAngle) {
		resource = StyleBarEntries.Angle.getResources();
	    }
	}

	final StandardImageButton[] b = new StandardImageButton[resource.length];

	for (int i = 0; i < resource.length; i++) {
	    if (resource[i].equals(lafIcons.label())) {

		b[i] = new StandardImageButton(lafIcons.label());
		b[i] = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(b[i], this, OptionType.CaptionStyle);

	    } else if (resource[i].equals(lafIcons.properties_default())) {

		b[i] = new StandardImageButton(lafIcons.properties_default());
		b[i] = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(b[i], this, OptionType.LineStyle);
	    } else if (resource[i].equals(lafIcons.color())) {

		b[i] = new StandardImageButton(lafIcons.color());
		b[i].getElement().getStyle().setBackgroundImage("initial");
		b[i].getElement().setAttribute("style", "background: " + color);
		b[i] = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(b[i], this, OptionType.Color);

	    } else if (resource[i].equals(lafIcons.show_or_hide_the_axes())) {
		b[i] = this.createStyleBarButton("showAxes", lafIcons.show_or_hide_the_axes());
	    } else if (resource[i].equals(lafIcons.show_or_hide_the_grid())) {
		b[i] = this.createStyleBarButton("showGrid", lafIcons.show_or_hide_the_grid());
	    }

	    else {
		return false;
	    }
	}

	this.styleButtonsPanel.clear();
	this.buttons = b;

	for (final StandardImageButton imageButton : this.buttons) {
	    this.styleButtonsPanel.add(imageButton);
	}

	return true;
    }

    /**
     * 
     * @param String
     *            process
     * @param SVGResourcce
     *            svg
     * @return a new StandardImageButton for the StyleBar with OS specific
     *         EventHandler
     */
    private StandardImageButton createStyleBarButton(final String process, SVGResource svg) {
	StandardImageButton newButton = new StandardImageButton(svg);

	newButton = TouchEntryPoint.getLookAndFeel().setStyleBarButtonHandler(newButton, this, process);

	return newButton;
    }

    public void rebuild() {
	if (this.guiModel.getCommand().getStyleBarEntries() != null) {
	    this.rebuild(this.guiModel.getCommand().getStyleBarEntries());
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
	    StyleBar.this.guiModel.showOption(new OptionsPanel(new ColorBarPanel(this, this.touchModel)), OptionType.Color, this.colorButton);
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

	this.colorButton.getElement().getStyle().setBackgroundImage("initial");
	this.colorButton.getElement().getStyle().setBackgroundColor(color);

    }
}
