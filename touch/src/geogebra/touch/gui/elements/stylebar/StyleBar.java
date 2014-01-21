package geogebra.touch.gui.elements.stylebar;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoAxis;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.ToolbarResources;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.euclidian.EuclidianViewT;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.StyleBarDefaultSettings;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.resources.client.ImageResource;
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
	private enum StyleBarEntry {
		Axes, Grid, Color, LineStyle, CaptionStyle;
	}

	private static DefaultResources lafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final HorizontalPanel contentPanel, styleButtonsPanel;
	private final Map<StyleBarEntry, FastButton> buttons = new HashMap<StyleBarEntry, FastButton>();
	private final FastButton showHideButton;
	final EuclidianViewT euclidianView;
	final TouchModel touchModel;
	final GuiModel guiModel;
	private OptionsPanel optionsPanel;
	private final TabletGUI gui;

	/**
	 * Initializes the StyleBar
	 * 
	 */
	public StyleBar(final TouchModel touchModel, final EuclidianViewT view,TabletGUI gui) {
		this.setStyleName("stylebar");
		this.gui = gui;
		this.euclidianView = view;
		this.touchModel = touchModel;
		this.guiModel = touchModel.getGuiModel();

		this.contentPanel = new HorizontalPanel();
		this.styleButtonsPanel = new HorizontalPanel();

		this.showHideButton = new StandardButton(lafIcons.triangle_left());
		this.showHideButton.setStyleName("arrowLeft");
		this.showHideButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				showHide();
			}
		});

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();
		EuclidianStyleBarStatic.pointStyleArray = EuclidianView.getLineTypes();

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

	private void rebuild(final StyleBarDefaultSettings entry) {

		OptionType[] resource = entry.getOptions();
		String color = entry.getColor() != null ? entry.getColor().toString()
				: "";

		if (this.guiModel.getDefaultGeo() != null
				&& this.guiModel.getDefaultGeo().getObjectColor() != null) {
			color = this.guiModel.getDefaultGeo().getObjectColor().toString();
		}

		if (entry == StyleBarDefaultSettings.Move
				&& this.touchModel.getTotalNumber() > 0) {
			GeoElement geo = this.touchModel.getSelectedGeos().get(0)
					.getGeoElementForPropertiesDialog();
			color = geo.getObjectColor().toString();
			if (geo instanceof GeoAxis) {
				resource = StyleBarDefaultSettings.Move.getOptions();
			} else if (geo instanceof GeoPointND) {
				resource = StyleBarDefaultSettings.Point.getOptions();
			} else if (geo instanceof GeoAngle) {
				resource = StyleBarDefaultSettings.Angle.getOptions();
			} else if (geo.showLineProperties()) {
				resource = StyleBarDefaultSettings.Line.getOptions();
			} else if (this.touchModel.getSelectedGeos().get(0)
					.getGeoElementForPropertiesDialog() instanceof GeoPolygon) {
				resource = StyleBarDefaultSettings.Polygon.getOptions();
			}
		}

		this.buttons.clear();

		for (OptionType option : resource) {
			final FastButton b;
			switch(option){
			case Color:
				b = new StandardButton(lafIcons.color());
				b.getElement().getStyle().setBackgroundImage("initial");
				b.getElement().setAttribute("style", "background: " + color);
				b.addFastClickHandler(new FastClickHandler() {

					@Override
					public void onClick() {
						StyleBar.this
								.onOptionalButtonEvent(b, OptionType.Color);
					}
				});

				this.buttons.put(StyleBarEntry.Color, b);
				break;
			case LineStyle:
				b = new StandardButton(lafIcons.properties_default());
				b.addFastClickHandler(new FastClickHandler() {

					@Override
					public void onClick() {
						StyleBar.this.onOptionalButtonEvent(b,
								OptionType.LineStyle);

					}
				});
				this.buttons.put(StyleBarEntry.LineStyle, b);
				break;
			case CaptionStyle:
				b = new StandardButton(ToolbarResources.INSTANCE.label());
				b.addFastClickHandler(new FastClickHandler() {

					@Override
					public void onClick() {
						StyleBar.this.onOptionalButtonEvent(b,
								OptionType.CaptionStyle);
					}
				});
				// only show "label" in special cases
				final int cmd = this.touchModel.getCommand().getMode();
				if (cmd == EuclidianConstants.MODE_MOVE
						|| cmd == EuclidianConstants.MODE_POINT
						|| cmd == EuclidianConstants.MODE_INTERSECT
						|| cmd == EuclidianConstants.MODE_MIDPOINT
						|| cmd == EuclidianConstants.MODE_POINT_ON_OBJECT
						|| cmd == EuclidianConstants.MODE_COMPLEX_NUMBER
						|| cmd == EuclidianConstants.MODE_SLIDER) {
					this.buttons.put(StyleBarEntry.CaptionStyle, b);
				}
				break;
			case Axes:
				b = this.createStyleBarButton("showAxes",
						lafIcons.show_or_hide_the_axes());
				b.setActive(this.euclidianView.getShowAxis(0));
				checkStyle(b);
				this.buttons.put(StyleBarEntry.Axes, b);
				break;
			case Grid:
				b = this.createStyleBarButton("showGrid",
						lafIcons.show_or_hide_the_grid());
				b.setActive(this.euclidianView.getShowGrid());
				checkStyle(b);
				this.buttons.put(StyleBarEntry.Grid, b);
				break;
			case None:
				break;
			case PointStyle:
				b = new StandardButton(lafIcons.properties_default());
				b.addFastClickHandler(new FastClickHandler() {

					@Override
					public void onClick() {
						StyleBar.this.onOptionalButtonEvent(b,
								OptionType.PointStyle);

					}
				});
				this.buttons.put(StyleBarEntry.LineStyle, b);
				break;
			case ToolBar:
				break;
			default:
				break;
		}
		}

		this.styleButtonsPanel.clear();

		for (final FastButton imageButton : this.buttons.values()) {
			this.styleButtonsPanel.add(imageButton);
		}
	}

	static void checkStyle(FastButton button) {
		if (button.isActive()) {
			button.addStyleName("active");
		} else {
			button.removeStyleName("active");
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
			final ImageResource svg) {
		final FastButton newButton = new StandardButton(svg);

		newButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				StyleBar.this.guiModel.closeOptions();
				EuclidianStyleBarStatic.processSourceCommon(process, null,
						StyleBar.this.euclidianView);

				boolean newValue = ("showAxes".equals(process) && StyleBar.this.euclidianView
						.getShowAxis(0))
						|| ("showGrid".equals(process) && StyleBar.this.euclidianView
								.getShowGrid());
				newButton.setActive(newValue);
				checkStyle(newButton);
				StyleBar.this.touchModel.getKernel().getApplication()
						.setUnsaved();
				TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
			}
		});

		return newButton;
	}

	public void rebuild() {

		if (this.guiModel.getCommand().getStyleBarEntries() != null) {
			this.rebuild(this.guiModel.getCommand().getStyleBarEntries());
			if(this.gui.isRTL()){
				this.gui.getEuclidianViewPanel().adjustRightWidget();
			}
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
			this.addStyleName("transparent");

			this.styleButtonsPanel.setVisible(false);

		} else {

			this.showHideButton.setStyleName("arrowLeft");
			// Set stylebar nontransparent, when open
			this.removeStyleName("transparent");

			this.styleButtonsPanel.setVisible(true);
		}
		if(this.gui.isRTL()){
			this.gui.getEuclidianViewPanel().adjustRightWidget();
		}
	}

	void onOptionalButtonEvent(final FastButton button, final OptionType type) {

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

	void updateColor(final String color) {

		this.buttons.get(StyleBarEntry.Color).getElement().getStyle()
				.setBackgroundImage("initial");
		this.buttons.get(StyleBarEntry.Color).getElement().getStyle()
				.setBackgroundColor(color);

	}

	public TouchModel getTouchModel() {
		return this.touchModel;
	}
}
