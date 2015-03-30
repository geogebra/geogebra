/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.HasExtendedAV;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.main.SelectionManager;
import geogebra.common.util.AsyncOperation;
import geogebra.common.util.IndexHTMLBuilder;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.html5.event.PointerEvent;
import geogebra.html5.event.ZeroOffset;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.ClickStartHandler;
import geogebra.html5.gui.util.LongTouchManager;
import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;
import geogebra.html5.gui.view.algebra.GeoContainer;
import geogebra.html5.gui.view.algebra.MathKeyboardListener;
import geogebra.html5.main.AppW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.html5.util.EventUtil;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.SliderW;
import geogebra.web.util.keyboard.OnScreenKeyBoard;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * RadioButtonTreeItem for the items of the algebra view tree and also for the
 * event handling which is copied from Desktop/AlgebraController.java
 *
 * File created by Arpad Fekete
 */

public class RadioButtonTreeItem extends FlowPanel implements
        DoubleClickHandler, ClickHandler, MouseMoveHandler, MouseDownHandler,
        MouseOverHandler, MouseOutHandler, GeoContainer, MathKeyboardListener,
 TouchStartHandler,
        TouchMoveHandler, TouchEndHandler, LongTouchHandler {

	GeoElement geo;
	Kernel kernel;
	protected AppW app;
	private SelectionManager selection;
	protected AlgebraView av;
	private boolean LaTeX = false;
	private boolean thisIsEdited = false;
	boolean newCreationMode = false;
	boolean mout = false;

	protected SpanElement seMayLatex;
	private SpanElement seNoLatex;

	private Marble radio;
	InlineHTML ihtml;
	TextBox tb;
	private boolean needsUpdate;

	private LongTouchManager longTouchManager;

	/**
	 * prevents that a blur event stops the editing process
	 */
	boolean blockBlur = false;

	/**
	 * Slider to be shown as part of the extended Slider entries
	 */
	private SliderW slider;

	/**
	 * panel to correctly display an extended slider entry
	 */
	private FlowPanel sliderPanel;

	/**
	 * this panel contains the marble (radio) and the play button for extended
	 * slider entries
	 */
	private FlowPanel marblePanel;

	/**
	 * start/pause slider animations
	 */
	Image playButton;

	/**
	 * checkbox displaying boolean variables
	 */
	private CheckBox checkBox;

	/**
	 * button shown at the right side of the entry, if the entry is selected.
	 * used to delete the geo.
	 */
	private Image deleteButton;

	/**
	 * TODO this will be replaced by a check-box in the settings
	 * 
	 * allow slider (for a number) or checkBox (for a boolean) in AV as part of
	 * the RadioButtonTreeItem
	 */
	public static boolean showSliderOrTextBox = false;

	public void updateOnNextRepaint() {
		this.needsUpdate = true;
	}

	/*
	 * private class RadioButtonHandy extends RadioButton { public
	 * RadioButtonHandy() { super(DOM.createUniqueId()); }
	 * 
	 * @Override public void onBrowserEvent(Event event) {
	 * 
	 * if (av.isEditing()) return;
	 * 
	 * if (event.getTypeInt() == Event.ONCLICK) { // Part of
	 * AlgebraController.mouseClicked in Desktop if
	 * (Element.is(event.getEventTarget())) { if
	 * (Element.as(event.getEventTarget()) == getElement().getFirstChild()) {
	 * setValue(previouslyChecked = !previouslyChecked);
	 * geo.setEuclidianVisible(!geo.isSetEuclidianVisible()); geo.update();
	 * geo.getKernel().getApplication().storeUndoInfo();
	 * geo.getKernel().notifyRepaint(); return; } } } } }
	 */
	private IndexHTMLBuilder getBuilder(final SpanElement se) {
		return new IndexHTMLBuilder(false) {
			Element sub = null;

			@Override
			public void append(String s) {

				if (sub == null) {
					se.appendChild(Document.get().createTextNode(s));
				} else {
					sub.appendChild(Document.get().createTextNode(s));
				}
			}

			@Override
			public void startIndex() {
				sub = Document.get().createElement("sub");
				sub.getStyle().setFontSize((int) (app.getFontSize() * 0.8),
				        Unit.PX);
			}

			@Override
			public void endIndex() {
				if (sub != null) {
					se.appendChild(sub);
				}
				sub = null;
			}

			@Override
			public String toString() {
				if (sub != null) {
					endIndex();
				}
				return se.getInnerHTML();
			}

			@Override
			public void clear() {
				se.removeAllChildren();
				sub = null;
			}

			@Override
			public boolean canAppendRawHtml() {
				return false;
			}

			@Override
			public void appendHTML(String str) {
				append(str);
			}
		};
	}

	/**
	 * Creates a new RadioButtonTreeItem for displaying/editing an existing
	 * GeoElement
	 * 
	 * @param ge
	 *            the existing GeoElement to display/edit
	 * @param showUrl
	 *            the marble to be shown when the GeoElement is visible
	 * @param hiddenUrl
	 *            the marble to be shown when the GeoElement is invisible
	 */
	public RadioButtonTreeItem(GeoElement ge, SafeUri showUrl, SafeUri hiddenUrl) {
		super();
		getElement().setDraggable(Element.DRAGGABLE_TRUE);

		geo = ge;
		kernel = geo.getKernel();
		app = (AppW) kernel.getApplication();
		av = app.getAlgebraView();
		selection = app.getSelectionManager();
		addStyleName("elem");
		addStyleName("panelRow");

		// setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		radio = new Marble(showUrl, hiddenUrl, this);
		radio.setStyleName("marble");
		radio.setEnabled(ge.isEuclidianShowable());
		radio.setChecked(ge.isEuclidianVisible());

		marblePanel = new FlowPanel();
		marblePanel.add(radio);
		add(marblePanel);

		// Sliders
		if (showSliderOrTextBox && app.isPrerelease()
		        && geo instanceof GeoNumeric
		        && ((GeoNumeric) geo).isShowingExtendedAV()) {
			if (!geo.isEuclidianVisible()) {
				// number inserted via input bar
				// -> initialize min/max etc.
				geo.setEuclidianVisible(true);
				geo.setEuclidianVisible(false);
			}

			slider = new SliderW(((GeoNumeric) geo).getIntervalMin(),
			        (int) ((GeoNumeric) geo).getIntervalMax());
			slider.setValue(((GeoNumeric) geo).getValue());
			slider.setMinorTickSpacing(geo.getAnimationStep());

			slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
				public void onValueChange(ValueChangeEvent<Double> event) {
					((GeoNumeric) geo).setValue(event.getValue());
					geo.updateCascade();
					// updates other views (e.g. Euclidian)
					kernel.notifyRepaint();
				}
			});

			sliderPanel = new FlowPanel();
			add(sliderPanel);

			if (geo.isAnimatable()) {
				ImageResource imageresource = geo.isAnimating() ? AppResources.INSTANCE
				        .nav_pause() : AppResources.INSTANCE.nav_play();
				playButton = new Image(imageresource);
				playButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						boolean newValue = !(geo.isAnimating() && app
						        .getKernel().getAnimatonManager().isRunning());
						geo.setAnimating(newValue);
						playButton.setResource(newValue ? AppResources.INSTANCE
						        .nav_pause() : AppResources.INSTANCE.nav_play());
						geo.updateRepaint();

						if (geo.isAnimating()) {
							geo.getKernel().getAnimatonManager()
							        .startAnimation();
						}
					}
				});
				marblePanel.add(playButton);
			}
		}

		SpanElement se = DOM.createSpan().cast();
		EquationEditor.updateNewStatic(se);
		updateColor(se);
		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		ihtml.addMouseDownHandler(this);
		ihtml.addMouseOverHandler(this);
		ihtml.addMouseOutHandler(this);
		ihtml.addTouchStartHandler(this);
		ihtml.addTouchMoveHandler(this);
		ihtml.addTouchEndHandler(this);
		addSpecial(ihtml);
		ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.appendChild(Document.get().createTextNode(
		        "\u00A0\u00A0\u00A0\u00A0"));
		ihtml.getElement().appendChild(se2);
		// String text = "";

		if (showSliderOrTextBox && app.isPrerelease()
		        && geo instanceof GeoBoolean) {
			// CheckBoxes
			checkBox = new CheckBox();
			checkBox.setValue(((GeoBoolean) geo).getBoolean());
			add(checkBox);
			checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					((GeoBoolean) geo).setValue(event.getValue());
					geo.updateCascade();
					// updates other views (e.g. Euclidian)
					kernel.notifyRepaint();
				}
			});

			// use only the name of the GeoBoolean
			getBuilder(se).append(geo.getLabel(StringTemplate.defaultTemplate));
		} else if (geo.isIndependent()) {
			geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				geo.addLabelTextOrHTML(
				        geo.getDefinitionDescription(StringTemplate.defaultTemplate),
				        getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				geo.addLabelTextOrHTML(geo
				        .getCommandDescription(StringTemplate.defaultTemplate),
				        getBuilder(se));
				break;
			}
		}
		// if enabled, render with LaTeX
		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
			        StringTemplate.latexTemplateMQ);
			seNoLatex = se;
			if ((latexStr != null) && geo.isLaTeXDrawableGeo()
			        && (geo.isGeoList() ? !((GeoList) geo).isMatrix() : true)) {
				this.needsUpdate = true;
				av.repaintView();
			}
		} else {
			seNoLatex = se;
		}
		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
		setDraggable();
	}

	/**
	 * Creates a new RadioButtonTreeItem for creating a brand new GeoElement or
	 * executing a new command which might not result in any GeoElement(s) ...
	 * no marble, no input GeoElement here. But this will be called from
	 * NewRadioButtonTreeItem(kernel), for there are many extras
	 */
	public RadioButtonTreeItem(Kernel kern) {
		super();
		// this method is still not able to show an editing box!
		newCreationMode = true;

		kernel = kern;
		app = (AppW) kernel.getApplication();
		av = app.getAlgebraView();
		selection = app.getSelectionManager();
		this.setStyleName("elem");
		this.addStyleName("NewRadioButtonTreeItem");

		// setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		// add(radio);

		SpanElement se = DOM.createSpan().cast();
		EquationEditor.updateNewStatic(se);

		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		ihtml.addMouseDownHandler(this);
		ihtml.addMouseOverHandler(this);
		ihtml.addMouseOutHandler(this);
		ihtml.addTouchStartHandler(this);
		ihtml.addTouchMoveHandler(this);
		ihtml.addTouchEndHandler(this);
		add(ihtml);
		ihtml.getElement().appendChild(se);
		ihtml.getElement().addClassName("hasCursorPermanent");

		// setCellVerticalAlignment(ihtml, HasVerticalAlignment.ALIGN_MIDDLE);
		// setCellHorizontalAlignment(ihtml, HasHorizontalAlignment.ALIGN_LEFT);
		// setCellWidth(ihtml, "100%");
		getElement().getStyle().setWidth(100, Style.Unit.PCT);

		// making room for the TitleBarPanel (top right of the AV)
		SpanElement se2 = DOM.createSpan().cast();
		se2.appendChild(Document.get().createTextNode(
		        "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"));
		ihtml.getElement().appendChild(se2);

		// String text = "";
		/*
		 * if (geo.isIndependent()) {
		 * geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se)); } else {
		 * switch (kernel.getAlgebraStyle()) { case Kernel.ALGEBRA_STYLE_VALUE:
		 * geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se)); break;
		 * 
		 * case Kernel.ALGEBRA_STYLE_DEFINITION: geo.addLabelTextOrHTML(
		 * geo.getDefinitionDescription
		 * (StringTemplate.defaultTemplate),getBuilder(se)); break;
		 * 
		 * case Kernel.ALGEBRA_STYLE_COMMAND: geo.addLabelTextOrHTML(
		 * geo.getCommandDescription(StringTemplate.defaultTemplate),
		 * getBuilder(se)); break; } }
		 */
		// if enabled, render with LaTeX
		seNoLatex = se;
		if (av.isRenderLaTeX()) {
			this.needsUpdate = true;

			// here it complains that geo is undefined
			doUpdate();
			// startEditing();
		}
		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
		setDraggable();
	}

	/**
	 * Creates a new RadioButtonTreeItem for creating/editing a GeoElement of a
	 * special type (like matrix, piecewise function or parametric curve),
	 * marbles are given because it will turn to be used as a normal
	 * RadioButtonTreeItem later (just maybe edited differently)... This will be
	 * called from the constructor of SpecialRadioButtonTreeItem
	 */
	public RadioButtonTreeItem(Kernel kern, GeoElement ge, SafeUri showUrl,
	        SafeUri hiddenUrl) {
		super();
		// touch events did not work because these events were still not sunk
		sinkEvents(Event.ONTOUCHSTART | Event.ONTOUCHMOVE | Event.ONTOUCHEND);
		if (ge != null) {
			geo = ge;
			kernel = geo.getKernel();
		} else {
			geo = null;
			kernel = kern;
		}
		app = (AppW) kernel.getApplication();
		av = app.getAlgebraView();
		selection = app.getSelectionManager();
		this.setStyleName("elem");

		// setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		radio = new Marble(showUrl, hiddenUrl, this);
		radio.setStyleName("marble");
		radio.setEnabled(ge.isEuclidianShowable());
		radio.setChecked(ge.isEuclidianVisible());
		marblePanel = new FlowPanel();
		marblePanel.add(radio);
		add(marblePanel);

		// Sliders
		// could have been (ge != null) && ... but
		// geo is not GeoNumeric anyway, for matrix,
		// piecewise functions and parametric curves
		/*
		 * if ((ge != null) && showSliderOrTextBox && app.isPrerelease() && geo
		 * instanceof GeoNumeric) { if (!geo.isEuclidianVisible()) { // number
		 * inserted via input bar // -> initialize min/max etc.
		 * geo.setEuclidianVisible(true); geo.setEuclidianVisible(false); }
		 * 
		 * slider = new SliderW(((GeoNumeric) geo).getIntervalMin(), (int)
		 * ((GeoNumeric) geo).getIntervalMax()); slider.setValue(((GeoNumeric)
		 * geo).getValue()); slider.setMinorTickSpacing(geo.getAnimationStep());
		 * 
		 * slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
		 * public void onValueChange(ValueChangeEvent<Double> event) {
		 * ((GeoNumeric) geo).setValue(event.getValue()); geo.updateCascade();
		 * // updates other views (e.g. Euclidian) kernel.notifyRepaint(); } });
		 * 
		 * sliderPanel = new VerticalPanel(); add(sliderPanel);
		 * 
		 * if (geo.isAnimatable()) { ImageResource imageresource =
		 * geo.isAnimating() ? AppResources.INSTANCE .nav_pause() :
		 * AppResources.INSTANCE.nav_play(); playButton = new
		 * Image(imageresource); playButton.addClickHandler(new ClickHandler() {
		 * public void onClick(ClickEvent event) { boolean newValue =
		 * !(geo.isAnimating() && app
		 * .getKernel().getAnimatonManager().isRunning());
		 * geo.setAnimating(newValue); playButton.setResource(newValue ?
		 * AppResources.INSTANCE .nav_pause() :
		 * AppResources.INSTANCE.nav_play()); geo.updateRepaint();
		 * 
		 * if (geo.isAnimating()) { geo.getKernel().getAnimatonManager()
		 * .startAnimation(); } } }); marblePanel.add(playButton); } }
		 */

		SpanElement se = DOM.createSpan().cast();
		EquationEditor.updateNewStatic(se);
		updateColor(se);
		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		ihtml.addMouseDownHandler(this);
		ihtml.addMouseOverHandler(this);
		ihtml.addMouseOutHandler(this);
		ihtml.addTouchStartHandler(this);
		ihtml.addTouchMoveHandler(this);
		ihtml.addTouchEndHandler(this);
		addSpecial(ihtml);
		ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.appendChild(Document.get().createTextNode(
		        "\u00A0\u00A0\u00A0\u00A0"));
		ihtml.getElement().appendChild(se2);
		// String text = "";

		// could have been (ge != null) && ... but
		// geo is not GeoBoolean anyway, for matrix,
		// piecewise functions and parametric curves
		/*
		 * if ((ge != null) && showSliderOrTextBox && app.isPrerelease() && geo
		 * instanceof GeoBoolean) { // CheckBoxes checkBox = new CheckBox();
		 * checkBox.setValue(((GeoBoolean) geo).getBoolean()); add(checkBox);
		 * checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
		 * public void onValueChange(ValueChangeEvent<Boolean> event) {
		 * ((GeoBoolean) geo).setValue(event.getValue()); geo.updateCascade();
		 * // updates other views (e.g. Euclidian) kernel.notifyRepaint(); } });
		 * 
		 * // use only the name of the GeoBoolean
		 * getBuilder(se).append(geo.getLabel(StringTemplate.defaultTemplate));
		 * } else
		 */
		if (ge == null) { // addition on
			getBuilder(se).append("?");
		} else // addition off
		if (geo.isIndependent()) {
			geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				geo.addLabelTextOrHTML(
				        geo.getDefinitionDescription(StringTemplate.defaultTemplate),
				        getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				geo.addLabelTextOrHTML(geo
				        .getCommandDescription(StringTemplate.defaultTemplate),
				        getBuilder(se));
				break;
			}
		}
		// if enabled, render with LaTeX
		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String latexStr = "";
			if (ge != null) {
				geo.getLaTeXAlgebraDescription(true,
				        StringTemplate.latexTemplateMQ);
			} else {
				// latexStr = "?";
			}
			seNoLatex = se;
			if ((ge != null) && (latexStr != null) && geo.isLaTeXDrawableGeo()
			        && (geo.isGeoList() ? !((GeoList) geo).isMatrix() : true)) {
				this.needsUpdate = true;
				av.repaintView();
			} else if (ge == null) {
				// ?
			}
		} else {
			seNoLatex = se;
		}
		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public boolean popupSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public boolean hideSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public boolean shuffleSuggestions(boolean down) {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public void addToHistory(String str, String latexx) {
	}

	/**
	 * This method can be used to invoke a keydown event on MathQuillGGB, e.g.
	 * key=8,alt=false,ctrl=false,shift=false will trigger a Backspace event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keydown
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	public void keydown(int key, boolean alt, boolean ctrl, boolean shift) {
		if (av.isEditing() || isThisEdited() || newCreationMode) {
			geogebra.html5.main.DrawEquationWeb.triggerKeydown(seMayLatex, key,
			        alt, ctrl, shift);
		}
	}

	/**
	 * This method should be used to invoke a keypress on MathQuillGGB, e.g.
	 * keypress(47, false, false, false); will trigger a '/' press event... This
	 * method should be used instead of "keydown" in case we are interested in
	 * the Character meaning of the key (to be entered in a textarea) instead of
	 * the Controller meaning of the key.
	 * 
	 * @param character
	 *            charCode of the event, which is the same as "event.which",
	 *            used at keypress
	 * @param alt
	 *            boolean maybe not useful
	 * @param ctrl
	 *            boolean maybe not useful
	 * @param shift
	 *            boolean maybe not useful
	 */
	public void keypress(int character, boolean alt, boolean ctrl, boolean shift) {
		if (av.isEditing() || isThisEdited() || newCreationMode) {
			geogebra.html5.main.DrawEquationWeb.triggerKeypress(seMayLatex,
			        character, alt, ctrl, shift);
		}
	}

	/**
	 * This method can be used to invoke a keyup event on MathQuillGGB, e.g.
	 * key=13,alt=false,ctrl=false,shift=false will trigger a Enter event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keyup
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	public final void keyup(int key, boolean alt, boolean ctrl, boolean shift) {
		if (av.isEditing() || isThisEdited() || newCreationMode) {
			geogebra.html5.main.DrawEquationWeb.triggerKeyUp(seMayLatex, key,
			        alt, ctrl, shift);
		}
	}

	@Override
	public void handleLongTouch(int x, int y) {
		if (newCreationMode) {
			// maybe this fixes a bug with not focusing
			setFocus(true);
		}
		onRightClick(x, y);
	}

	public void repaint() {
		if (needsUpdate)
			doUpdate();
	}

	private void doUpdate() {
		// check for new LaTeX
		needsUpdate = false;
		boolean newLaTeX = false;

		if (this.checkBox != null
		        && ((HasExtendedAV) geo).isShowingExtendedAV()) {
			add(checkBox);
			checkBox.setValue(((GeoBoolean) geo).getBoolean());
			// reset the label text; use only the name of the GeoBoolean
			getBuilder(seNoLatex).clear();
			getBuilder(seNoLatex).append(
			        geo.getLabel(StringTemplate.defaultTemplate));
			return;
		} else if (this.checkBox != null) {
			remove(checkBox);
		}

		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String text = "";
			if (geo != null) {
				text = geo.getLaTeXAlgebraDescription(true,
				        StringTemplate.latexTemplateMQ);
				if ((text != null)
						&& text.length() < 500
				        && geo.isLaTeXDrawableGeo()
				        && (geo.isGeoList() ? !((GeoList) geo).isMatrix()
				                : true)) {
					newLaTeX = true;
				}
			} else {
				newLaTeX = true;
			}
			// now we have text and how to display it (newLaTeX/LaTeX)
			if (LaTeX && newLaTeX) {
				text = DrawEquationWeb.inputLatexCosmetics(text);
				int tl = text.length();
				text = DrawEquationWeb.stripEqnArray(text);
				updateColor(seMayLatex);
				DrawEquationWeb.updateEquationMathQuillGGB("\\mathrm{" + text
				        + "}", seMayLatex, tl == text.length());
				updateColor(seMayLatex);
			} else if (newLaTeX) {
				SpanElement se = DOM.createSpan().cast();
				EquationEditor.updateNewStatic(se);
				updateColor(se);
				ihtml.getElement().replaceChild(se, seNoLatex);
				text = DrawEquationWeb.inputLatexCosmetics(text);
				seMayLatex = se;
				if (newCreationMode) {
					// in editing mode, we shall avoid letting an invisible, but
					// harmful element!
					DrawEquationWeb.drawEquationAlgebraView(seMayLatex, "",
					        newCreationMode);
				} else {
					DrawEquationWeb.drawEquationAlgebraView(seMayLatex,
					        "\\mathrm {" + text + "}", newCreationMode);
				}
				LaTeX = true;
			}

		} else if (geo == null) {
			newLaTeX = true;
		}
		// check for new text
		if (!newLaTeX) {
			if (geo.isIndependent()) {
				geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(seNoLatex));
			} else {
				switch (kernel.getAlgebraStyle()) {
				case Kernel.ALGEBRA_STYLE_VALUE:
					geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(seNoLatex));
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					geo.addLabelTextOrHTML(
					        geo.getDefinitionDescription(StringTemplate.defaultTemplate),
					        getBuilder(seNoLatex));
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					geo.addLabelTextOrHTML(
					        geo.getCommandDescription(StringTemplate.defaultTemplate),
					        getBuilder(seNoLatex));
					break;
				}
			}
			// now we have text and how to display it (newLaTeX/LaTeX)
			if (!LaTeX) {
				updateColor(seNoLatex);
			} else {
				SpanElement se = DOM.createSpan().cast();
				EquationEditor.updateNewStatic(se);
				updateColor(se);
				ihtml.getElement().replaceChild(se, seMayLatex);
				seNoLatex = se;
				LaTeX = false;
			}
		}

		if (geo != null && radio != null) {
			radio.setChecked(geo.isEuclidianVisible());
		}

		if (geo != null && geo instanceof GeoNumeric && slider != null
		        && sliderPanel != null) {
			slider.setMinimum(((GeoNumeric) geo).getIntervalMin());
			slider.setMaximum(((GeoNumeric) geo).getIntervalMax());
			slider.setMinorTickSpacing(geo.getAnimationStep());
			slider.setValue(((GeoNumeric) geo).value);
			if (((HasExtendedAV) geo).isShowingExtendedAV()
			        && !geo.isEuclidianVisible()) {
				sliderPanel.add(slider);
				marblePanel.add(playButton);
			} else if (marblePanel != null) {
				sliderPanel.remove(slider);
				marblePanel.remove(playButton);
			}
		}
	}



	private void updateColor(SpanElement se) {
		if (geo != null) {
			se.getStyle()
			        .setColor(GColor.getColorString(geo.getAlgebraColor()));
		}
	}

	public boolean isThisEdited() {
		return thisIsEdited;
	}

	public void cancelEditing() {
		// as this method is only called from AlgebraViewWeb.update,
		// and in that context, this should not cancel editing in case of
		// newCreationMode,
		// we can put an if check here safely for the present time
		if (!newCreationMode) {
			if (LaTeX) {
				DrawEquationWeb
				        .endEditingEquationMathQuillGGB(this, seMayLatex);
			} else {
				removeSpecial(tb);
				addSpecial(ihtml);
				stopEditingSimple(tb.getText());
			}
		}
	}

	public boolean blockBlurSensible() {
		return !newCreationMode
		        && (!LaTeX || (geo.isGeoVector() && geo.isIndependent()));
	}

	public void startEditing() {
		thisIsEdited = true;
		if (newCreationMode) {
			geogebra.html5.main.DrawEquationWeb.editEquationMathQuillGGB(this,
			        seMayLatex, true);
		} else if (shouldEditLaTeX()) {
			geogebra.html5.main.DrawEquationWeb.editEquationMathQuillGGB(this,
			        seMayLatex, false);
		} else {
			removeSpecial(ihtml);
			tb = new GTextBox();
			tb.setText(geo.getAlgebraDescriptionDefault());
			addSpecial(tb);
			mout = false;
			tb.setFocus(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					tb.setFocus(true);
				}
			});
			tb.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent kevent) {
					if (kevent.getNativeKeyCode() == 13) {
						removeSpecial(tb);
						addSpecial(ihtml);
						stopEditingSimple(tb.getText());
						app.hideKeyboard();
					} else if (kevent.getNativeKeyCode() == 27) {
						removeSpecial(tb);
						addSpecial(ihtml);
						stopEditingSimple(null);
						app.hideKeyboard();
					}
				}
			});
			tb.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent bevent) {
					if (mout && !blockBlur) {
						removeSpecial(tb);
						addSpecial(ihtml);
						stopEditingSimple(null);
					}
				}
			});
			tb.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent moevent) {
					mout = false;
				}
			});
			tb.addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent moevent) {
					mout = true;
					tb.setFocus(true);
				}
			});

			ClickStartHandler.init(tb, new ClickStartHandler() {
				@Override
				public void onClickStart(int x, int y,
				        final PointerEventType type) {

					OnScreenKeyBoard.setInstanceTextField(app,
					        new MathKeyboardListener() {

						        @Override
						        public void setFocus(boolean focus) {
							        tb.setFocus(focus);

						        }

						        @Override
						        public void ensureEditing() {
							        // TODO Auto-generated method stub

						        }
					        });
					// prevent that keyboard is closed on clicks (changing
					// cursor position)
					CancelEventTimer.keyboardSetVisible();
				}
			});
		}

		scrollIntoView();
	}

	public void stopEditingSimple(String newValue) {

		thisIsEdited = false;
		av.cancelEditing();

		if (newValue != null) {
			if (geo != null) {
				boolean redefine = !geo.isPointOnPath();
				GeoElement geo2 = kernel.getAlgebraProcessor()
				        .changeGeoElement(geo, newValue, redefine, true);
				if (geo2 != null)
					geo = geo2;
			} else {
				// TODO create new GeoElement
			}
		}

		// maybe it's possible to enter something which is LaTeX
		// note: this should be OK for independent GeoVectors too
		doUpdate();
	}

	private static String stopCommon(String newValue0) {
		String newValue = newValue0;
		// newValue = newValue0.replace("space *", " ");
		// newValue = newValue.replace("* space", " ");

		// newValue = newValue.replace("space*", " ");
		// newValue = newValue.replace("*space", " ");

		newValue = newValue.replace("space ", " ");
		newValue = newValue.replace(" space", " ");
		newValue = newValue.replace("space", " ");

		// \" is the " Quotation delimiter returned by MathQuillGGB
		// now it's handy that "space" is not in newValue
		newValue = newValue.replace("\\\"", "space");

		// change \" to corresponding unicode characters
		StringBuilder sb = new StringBuilder();
		StringUtil.processQuotes(sb, newValue, Unicode.OPEN_DOUBLE_QUOTE);
		newValue = sb.toString();

		newValue = newValue.replace("space", "\"");

		// do not substitute for absolute value in these cases
		newValue = newValue.replace("||", ExpressionNodeConstants.strOR);
		return newValue;
	}

	@Override
	public boolean stopEditing(String newValue0) {

		boolean ret = false;

		if (blockBlur && blockBlurSensible()) {
			return false;
		}

		thisIsEdited = false;
		av.cancelEditing();

		if (newValue0 != null) {
			String newValue = stopCommon(newValue0);

			// not sure why it is needed... TODO: is this needed?
			newValue.replace(" ", "");

			// Formula Hacks ended.
			if (geo != null) {
				boolean redefine = !geo.isPointOnPath();
				GeoElement geo2 = kernel.getAlgebraProcessor()
				        .changeGeoElement(geo, newValue, redefine, true);
				if (geo2 != null) {
					ret = true;
					geo = geo2;
				}
			} else {
				// TODO: create new GeoElement!

			}
		}

		// maybe it's possible to enter something which is non-LaTeX
		doUpdate();

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				scrollIntoView();
			}
		});
		return ret;
	}

	/**
	 * Stop new formula creation Much of this code is copied from
	 * AlgebraInputW.onKeyUp
	 * 
	 * @param newValue0
	 * @return boolean whether it was successful
	 */
	public boolean stopNewFormulaCreation(String newValue0,
	        final String latexx, final AsyncOperation cb) {

		// TODO: move to NewRadioButtonTreeItem? Wouldn't help much...

		String newValue = newValue0;

		if (newValue0 != null) {
			newValue = stopCommon(newValue);
		}

		app.getKernel().clearJustCreatedGeosInViews();
		final String input = newValue;
		if (input == null || input.length() == 0) {
			app.getActiveEuclidianView().requestFocusInWindow(); // Michael
			                                                     // Borcherds
			                                                     // 2008-05-12
			scrollIntoView();
			return false;
		}

		app.setScrollToShow(true);

		try {
			AsyncOperation callback = new AsyncOperation() {

				@Override
				public void callback(Object obj) {

					if (!(obj instanceof GeoElement[])) {
						// inputField.getTextBox().setFocus(true);
						setFocus(true);
						return;
					}
					GeoElement[] geos = (GeoElement[]) obj;

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					// create texts in the middle of the visible view
					// we must check that size of geos is not 0 (ZoomIn,
					// ZoomOut, ...)
					if (geos.length > 0 && geos[0] != null
					        && geos[0].isGeoText()) {
						GeoText text = (GeoText) geos[0];
						if (!text.isTextCommand()
						        && text.getStartPoint() == null) {

							Construction cons = text.getConstruction();
							EuclidianViewInterfaceCommon ev = app
							        .getActiveEuclidianView();

							boolean oldSuppressLabelsStatus = cons
							        .isSuppressLabelsActive();
							cons.setSuppressLabelCreation(true);
							GeoPoint p = new GeoPoint(text.getConstruction(),
							        null, (ev.getXmin() + ev.getXmax()) / 2,
							        (ev.getYmin() + ev.getYmax()) / 2, 1.0);
							cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

							try {
								text.setStartPoint(p);
								text.update();
							} catch (CircularDefinitionException e1) {
								e1.printStackTrace();
							}
						}
					}

					app.setScrollToShow(false);

					addToHistory(input, latexx);

					Scheduler.get().scheduleDeferred(
					        new Scheduler.ScheduledCommand() {
						        public void execute() {
							        scrollIntoView();
							        if (newCreationMode) {
								        setFocus(true);
							        }
						        }
					        });

					// actually this (and only this) means return true!
					cb.callback(null);

					// inputField.setText(null); // that comes after boolean
					// return true
					// inputField.setIsSuggestionJustHappened(false); // that is
					// not relevant here
				}

			};

			GeoElement[] newGeo = app
			        .getKernel()
			        .getAlgebraProcessor()
			        .processAlgebraCommandNoExceptionHandling(input, true,
			                false, true, true, callback);

			if (newGeo != null && newGeo.length == 1
			        && newGeo[0] instanceof GeoText) {
				// texts created via the input field should be displayed in the
				// AV
				newGeo[0].setAuxiliaryObject(false);
			}

		} catch (Exception ee) {
			// TODO: better exception handling
			// GOptionPaneW.setCaller(inputField.getTextBox());// we have no
			// good FocusWidget
			// app.showError(ee, inputField);
			app.showError(ee.getMessage());// we use what we have
			return false;
		} catch (MyError ee) {
			// TODO: better error handling
			// GOptionPaneW.setCaller(inputField.getTextBox());// we have no
			// good FocusWidget
			// inputField.showError(ee);
			app.showError(ee);// we use what we have
			return false;
		}
		// there is also a timer to make sure it scrolls into view
		Timer tim = new Timer() {
			@Override
			public void run() {
				scrollIntoView();
				if (newCreationMode) {
					setFocus(true);
				}
			}
		};
		tim.schedule(500);
		return true;
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if (av.isEditing() || isThisEdited() || newCreationMode)
			return;

		onDoubleClickAction(evt.isControlKeyDown(), evt.isShiftKeyDown());
	}

	private void onDoubleClickAction(boolean ctrl, boolean shif) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selection.clearSelectedGeos();
		ev.resetMode();
		if (geo != null && !ctrl) {
			av.startEditing(geo, shif);
			if (shouldEditLaTeX()) {
				app.showKeyboard(this);
				this.setFocus(true);
			}
			// if (app.isPrerelease() && tb != null) {
			// app.showKeyboard(tb);
			// // update the keyboard, if it is already visible
			// OnScreenKeyBoard.setInstanceTextField(tb);
			// blockBlur = true;
			// OnScreenKeyBoard.setResetComponent(this);
			// } else if (app.isPrerelease()) {
			// app.showKeyboard(this);
			// // update the keyboard, if it is already visible
			// OnScreenKeyBoard.setInstanceTextField(this);
			// blockBlur = true;
			// OnScreenKeyBoard.setResetComponent(this);
			// }
		}
	}

	private boolean shouldEditLaTeX() {
		return LaTeX && !geo.isGeoVector() && geo.isIndependent();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
		        ZeroOffset.instance);
		onPointerDown(wrappedEvent);

		// This would prevent dragging, so commented out
		// event.preventDefault();
		event.stopPropagation();
	}

	@Override
	public void onClick(ClickEvent evt) {
		// this 'if' should be the first one in every 'mouse' related method
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
		        ZeroOffset.instance);
		onPointerUp(wrappedEvent);

		((AlgebraViewWeb) this.av).getStyleBar().update(this.getGeo());

		if (app.isPrerelease() && geo != null) {
			if (deleteButton == null) {
				deleteButton = new Image(
				        GuiResources.INSTANCE.algebraViewDeleteEntry());
				ClickStartHandler.init(deleteButton, new ClickStartHandler(
				        true, true) {

					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
						geo.remove();

					}
				});
			}
			add(deleteButton);
			((AlgebraViewWeb) this.av).setActiveTreeItem(this);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
		        ZeroOffset.instance);
		onPointerMove(wrappedEvent);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (geo != null) {
			ToolTipManagerW.sharedInstance().showToolTip(
			        geo.getLongDescriptionHTML(true, true));
		}
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		ToolTipManagerW.sharedInstance().showToolTip(null);
	}

	@Override
	public GeoElement getGeo() {
		return geo;
	}

	public long latestTouchEndTime = 0;

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (newCreationMode) {
			setFocus(true);
		}
		long time = System.currentTimeMillis();
		if (time - latestTouchEndTime < 500) {
			// ctrl key, shift key for TouchEndEvent? interesting...
			latestTouchEndTime = time;
			if (!newCreationMode) {
				onDoubleClickAction(false, // event.isControlKeyDown(),
				        false // event.isShiftKeyDown()
				);
			}
		} else {
			latestTouchEndTime = time;
		}
		longTouchManager.cancelTimer();
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(event,
		        ZeroOffset.instance);
		onPointerUp(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		longTouchManager.rescheduleTimerIfRunning(this, x, y);
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(targets.get(0),
		        ZeroOffset.instance);
		onPointerMove(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		longTouchManager.scheduleTimer(this, x, y);
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(event,
		        ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	private void onPointerDown(AbstractEvent event) {

		if (event.isRightClick()) {
			onRightClick(event.getX(), event.getY());
		} else if (av.isEditing() || isThisEdited() || newCreationMode) {
			if (!av.isEditing() && !isThisEdited()) {
				startEditing();
			}
			app.showKeyboard(this);
			PointerEvent pointerEvent = (PointerEvent) event;
			pointerEvent.getWrappedEvent().stopPropagation();
			if (newCreationMode) {
				// put earlier, maybe it freezes afterwards?
				setFocus(true);
			}
		}
	}

	private void onPointerUp(AbstractEvent event) {
		if (av.isEditing() || isThisEdited() || newCreationMode) {
			return;
		}
		int mode = app.getActiveEuclidianView().getMode();
		if (// !skipSelection &&
		(mode == EuclidianConstants.MODE_MOVE)) {
			// update selection
			if (geo == null) {
				selection.clearSelectedGeos();
			} else {
				// handle selecting geo
				if (event.isControlDown()) {
					selection.toggleSelectedGeo(geo);
					if (selection.getSelectedGeos().contains(geo)) {
						av.setLastSelectedGeo(geo);
					}
				} else if (event.isShiftDown()
				        && av.getLastSelectedGeo() != null) {
					boolean nowSelecting = true;
					boolean selecting = false;
					boolean aux = geo.isAuxiliaryObject();
					boolean ind = geo.isIndependent();
					boolean aux2 = av.getLastSelectedGeo().isAuxiliaryObject();
					boolean ind2 = av.getLastSelectedGeo().isIndependent();

					if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
						Iterator<GeoElement> it = kernel.getConstruction()
						        .getGeoSetLabelOrder().iterator();
						boolean direction = geo.getLabel(
						        StringTemplate.defaultTemplate).compareTo(
						        av.getLastSelectedGeo().getLabel(
						                StringTemplate.defaultTemplate)) < 0;

						while (it.hasNext()) {
							GeoElement geo2 = it.next();
							if ((geo2.isAuxiliaryObject() == aux && aux)
							        || (geo2.isAuxiliaryObject() == aux && geo2
							                .isIndependent() == ind)) {

								if (direction
								        && geo2.equals(av.getLastSelectedGeo()))
									selecting = !selecting;
								if (!direction && geo2.equals(geo))
									selecting = !selecting;

								if (selecting) {
									selection.toggleSelectedGeo(geo2);
									nowSelecting = selection.getSelectedGeos()
									        .contains(geo2);
								}
								if (!direction
								        && geo2.equals(av.getLastSelectedGeo()))
									selecting = !selecting;
								if (direction && geo2.equals(geo))
									selecting = !selecting;
							}
						}
					}

					if (nowSelecting) {
						selection.addSelectedGeo(geo);
						av.setLastSelectedGeo(geo);
					} else {
						selection.removeSelectedGeo(av.getLastSelectedGeo());
						av.setLastSelectedGeo(null);
					}
				} else {
					selection.clearSelectedGeos(false); // repaint will be done
					                                    // next step
					selection.addSelectedGeo(geo);
					av.setLastSelectedGeo(geo);
				}
			}
		} else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			if (geo != null) {
				app.getActiveEuclidianView().clickedGeo(geo,
				        event.isControlDown());
			}
			// event.release();
		} else
		// tell selection listener about click
		if (geo != null) {
			app.geoElementSelected(geo, false);
		}

		// Alt click: copy definition to input field
		if (geo != null && event.isAltDown() && app.showAlgebraInput()) {
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3,
			        geo);
		}

		app.getActiveEuclidianView().mouseMovedOver(null);

		// this should not give the focus to AV instead of the current formula!
		// except if we are not in editing mode! That's why better condition was
		// needed at the beginning of this method!
		av.setFocus(true);
	}

	private void onPointerMove(AbstractEvent event) {
		if (av.isEditing() || isThisEdited() || newCreationMode)
			return;

		// tell EuclidianView to handle mouse over
		EuclidianViewInterfaceCommon ev = kernel.getApplication()
		        .getActiveEuclidianView();
		if (geo != null) {
			ev.mouseMovedOver(geo);
		}

		// highlight the geos
		// getElement().getStyle().setBackgroundColor("rgb(200,200,245)");

		// implemented by HTML title attribute on the label
		// FIXME: geo.getLongDescription() doesn't work
		// if (geo != null) {
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
		// } else {
		// se.setTitle("");
		// }
	}

	private void onRightClick(int x, int y) {
		if (av.isEditing() || isThisEdited() || newCreationMode)
			return;

		SelectionManager selection = app.getSelectionManager();
		GPoint point = new GPoint(x + Window.getScrollLeft(), y
		        + Window.getScrollTop());
		if (geo != null) {
			if (selection.containsSelectedGeo(geo)) {// popup
				// menu for
				// current
				// selection
				// (including
				// selected
				// object)
				((GuiManagerW) app.getGuiManager()).showPopupMenu(
				        selection.getSelectedGeos(), av, point);
			} else {// select only this object and popup menu
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(geo, true, true);
				ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
				temp.add(geo);

				((GuiManagerW) app.getGuiManager()).showPopupMenu(temp, av,
				        point);
			}
		}
	}

	/**
	 * As adding focus handlers to JavaScript code would be too complex, let's
	 * do it even before they actually get focus, i.e. make a method that
	 * triggers focus, and then override it if necessary
	 * 
	 * @param b
	 *            focus (false: blur)
	 */
	public void setFocus(boolean b) {
		geogebra.html5.main.DrawEquationWeb.focusEquationMathQuillGGB(
		        seMayLatex, b);

		// as the focus operation sometimes also scrolls
		// if (b)
		// geogebra.html5.main.DrawEquationWeb.scrollCursorIntoView(this,
		// seMayLatex);
		// put to focus handler
	}

	public void resetBlockBlur() {
		this.blockBlur = false;
		if (tb != null) {
			NativeEvent event = Document.get().createBlurEvent();
			tb.onBrowserEvent(Event.as(event));
		}
	}

	public void insertString(String text) {
		// even worse
		// for (int i = 0; i < text.length(); i++)
		// geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
		// seMayLatex, "" + text.charAt(i), "", false);

		geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
		        seMayLatex, text, "", false);
	}

	public String getText() {
		return geogebra.html5.main.DrawEquationWeb
		        .getActualEditedValue(seMayLatex);
	}

	public void scrollCursorIntoView() {
		DrawEquationWeb.scrollCursorIntoView(this, seMayLatex, newCreationMode);
	}

	public void scrollIntoView() {
		this.getElement().scrollIntoView();
	}

	public void removeCloseButton() {
		if (this.deleteButton != null) {
			remove(this.deleteButton);
		}
	}

	void removeSpecial(Widget w) {
		remove(w);
		if (sliderPanel != null) {
			sliderPanel.remove(w);
		}
	}

	void addSpecial(Widget w) {
		if (geo != null && geo instanceof GeoNumeric && slider != null
		        && sliderPanel != null) {
			sliderPanel.remove(slider);
			sliderPanel.add(w);
			if (((HasExtendedAV) geo).isShowingExtendedAV()) {
				sliderPanel.add(slider);
			}
		} else if (checkBox != null) {
			remove(checkBox);
			add(w);
			if (((HasExtendedAV) geo).isShowingExtendedAV()) {
				add(checkBox);
			}
		} else {
			add(w);
		}
	}

	public void setDraggable() {

		getElement().setAttribute("position", "absolute");
		addDomHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				event.setData("text", "draggginggg");
				event.getDataTransfer().setDragImage(getElement(), 10, 10);
				event.stopPropagation();
				((AlgebraViewW) av).dragStart(event, geo);
			}
		}, DragStartEvent.getType());

	}

	public App getApplication() {
		return app;
	}

	@Override
	public void ensureEditing() {
		if (!isThisEdited()) {
			startEditing();
		}
	}
}
