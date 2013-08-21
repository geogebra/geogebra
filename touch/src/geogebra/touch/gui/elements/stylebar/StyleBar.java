package geogebra.touch.gui.elements.stylebar;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.LineProperties;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.euclidian.EuclidianViewT;
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

	private static DefaultResources lafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final HorizontalPanel contentPanel, styleButtonsPanel;
	private final Map<StyleBarEntry, FastButton> buttons = new HashMap<StyleBarEntry, FastButton>();
	private FastButton showHideButton;
	private EuclidianViewT euclidianView;
	private final TouchModel touchModel;
	private final GuiModel guiModel;
	private OptionsPanel optionsPanel;

	/**
	 * Initializes the StyleBar
	 * 
	 */
	public StyleBar(final TouchModel touchModel, final EuclidianViewT view) {
		this.setStyleName("stylebar");
		this.euclidianView = view;
		this.touchModel = touchModel;
		this.guiModel = touchModel.getGuiModel();

		this.contentPanel = new HorizontalPanel();
		this.styleButtonsPanel = new HorizontalPanel();

		this.showHideButton = new StandardImageButton(lafIcons.triangle_left());
		this.showHideButton.setStyleName("arrowLeft");
		this.showHideButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				showHide();
			}
		});

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();

		if (this.guiModel.getCommand() != null
				&& this.guiModel.getCommand().getStyleBarEntries() != null) {
			this.rebuild(this.guiModel.getCommand().getStyleBarEntries());
		}

		this.contentPanel.add(this.styleButtonsPanel);
		this.contentPanel.add(this.showHideButton);
		this.add(this.contentPanel);

		// Prevent events from getting through to the canvas
		this.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		this.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(final MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, MouseDownEvent.getType());

		this.addDomHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(final TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}

		}, TouchStartEvent.getType());
	}

	private void rebuild(StyleBarDefaultSettings entry) {

		SVGResource[] resource = entry.getResources();
		String color = entry.getColor() != null ? entry.getColor().toString()
				: "";
		if (this.guiModel.getColor() != null) {
			color = this.guiModel.getColor().toString();
		}

		if (entry == StyleBarDefaultSettings.Move
				&& this.touchModel.getTotalNumber() > 0) {
			color = this.touchModel.getSelectedGeos().get(0).getObjectColor()
					.toString();
			if (this.touchModel.getSelectedGeos().get(0)
					.getGeoElementForPropertiesDialog() instanceof GeoPointND) {
				resource = StyleBarDefaultSettings.Point.getResources();
			} else if (this.touchModel.getSelectedGeos().get(0)
					.getGeoElementForPropertiesDialog() instanceof GeoAngle) {
				resource = StyleBarDefaultSettings.Angle.getResources();
			} else if (this.touchModel.getSelectedGeos().get(0)
					.getGeoElementForPropertiesDialog() instanceof LineProperties
					|| this.touchModel.getSelectedGeos().get(0)
							.getGeoElementForPropertiesDialog() instanceof GeoNumeric) {
				// GeoNumeric in case of Slider) {
				resource = StyleBarDefaultSettings.Line.getResources();
			} else if (this.touchModel.getSelectedGeos().get(0)
					.getGeoElementForPropertiesDialog() instanceof GeoPolygon) {
				resource = StyleBarDefaultSettings.Polygon.getResources();
			}
		}

		this.buttons.clear();
		FastButton b;

		for (SVGResource svg : resource) {
			if (svg.equals(lafIcons.color())) {

				b = new StandardImageButton(lafIcons.color());
				b.getElement().getStyle().setBackgroundImage("initial");
				b.getElement().setAttribute("style", "background: " + color);
				b = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(
						b, this, OptionType.Color);
				this.buttons.put(StyleBarEntry.Color, b);

			} else if (svg.equals(lafIcons.properties_default())) {

				b = new StandardImageButton(lafIcons.properties_default());
				b = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(
						b, this, OptionType.LineStyle);
				this.buttons.put(StyleBarEntry.LineStyle, b);

			} else if (svg.equals(lafIcons.label())) {
				b = new StandardImageButton(lafIcons.label());
				b = TouchEntryPoint.getLookAndFeel().setOptionalButtonHandler(
						b, this, OptionType.CaptionStyle);
				// only show "label" in special cases
				if (this.touchModel.getCommand().getMode() == EuclidianConstants.MODE_MOVE
						|| this.touchModel.getCommand().getMode() == EuclidianConstants.MODE_POINT
						|| this.touchModel.getCommand().getMode() == EuclidianConstants.MODE_INTERSECT
						|| this.touchModel.getCommand().getMode() == EuclidianConstants.MODE_MIDPOINT
						|| this.touchModel.getCommand().getMode() == EuclidianConstants.MODE_POINT_ON_OBJECT
						|| this.touchModel.getCommand().getMode() == EuclidianConstants.MODE_COMPLEX_NUMBER) {
					this.buttons.put(StyleBarEntry.CaptionStyle, b);
				}
			} else if (svg.equals(lafIcons.show_or_hide_the_axes())) {

				b = this.createStyleBarButton("showAxes",
						lafIcons.show_or_hide_the_axes());
				this.buttons.put(StyleBarEntry.Axes, b);

			} else if (svg.equals(lafIcons.show_or_hide_the_grid())) {

				b = this.createStyleBarButton("showGrid",
						lafIcons.show_or_hide_the_grid());
				this.buttons.put(StyleBarEntry.Grid, b);
			}

			else {

			}
		}

		this.styleButtonsPanel.clear();

		for (final FastButton imageButton : this.buttons.values()) {
			this.styleButtonsPanel.add(imageButton);
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
	private FastButton createStyleBarButton(final String process,
			final SVGResource svg) {
		FastButton newButton = new StandardImageButton(svg);

		newButton = TouchEntryPoint.getLookAndFeel().setStyleBarButtonHandler(
				newButton, this, process);

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

	public void onOptionalButtonEvent(final FastButton button,
			final OptionType type) {

		if (this.guiModel.getOptionTypeShown().equals(type)) {

			this.guiModel.closeOptions();

		} else {

			if (this.optionsPanel == null) {
				this.optionsPanel = new OptionsPanel(this);
			}

			this.guiModel.showOption(this.optionsPanel.getOptionsPanel(type),
					button);
		}
	}

	public void onStyleBarButtonEvent(final FastButton newButton,
			final String process) {

		this.guiModel.closeOptions();
		EuclidianStyleBarStatic.processSourceCommon(process, null,
				this.euclidianView);

		newButton.setActive(!newButton.isActive());
		this.touchModel.getKernel().getApplication().setUnsaved();
		TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
	}

	public void updateColor(final String color) {

		this.buttons.get(StyleBarEntry.Color).getElement().getStyle()
				.setBackgroundImage("initial");
		this.buttons.get(StyleBarEntry.Color).getElement().getStyle()
				.setBackgroundColor(color);

	}

	public TouchModel getTouchModel() {
		return this.touchModel;
	}
}
