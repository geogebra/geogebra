/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.TimerListener;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasExtendedAV;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationWeb;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * RadioButtonTreeItem for the items of the algebra view tree and also for the
 * event handling which is copied from Desktop/AlgebraController.java
 *
 * File created by Arpad Fekete
 */

public class RadioButtonTreeItem extends FlowPanel implements
        DoubleClickHandler, ClickHandler, MouseMoveHandler, MouseDownHandler,
        MouseUpHandler,
        MouseOverHandler, MouseOutHandler, GeoContainer, MathKeyboardListener,
		TouchStartHandler, TouchMoveHandler, TouchEndHandler, LongTouchHandler,
 EquationEditorListener {

	public class PlayButton extends Image implements TimerListener {

		public PlayButton(ImageResource imageresource) {
			super(imageresource);
			geo.getKernel().getAnimatonManager().addListener(this);
		}

		@Override
		public void onTimerStarted() {
			playButton.setResource(AppResources.INSTANCE.nav_pause());
			geo.setAnimating(true);
		}

		@Override
		public void onTimerStopped() {
			playButton.setResource(AppResources.INSTANCE.nav_play());
			geo.setAnimating(false);
		}

	}

	protected FlowPanel buttonPanel;
	protected PushButton xButton;

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
	private final SpanElement seNoLatex;

	private Marble radio;
	InlineHTML ihtml;
	GTextBox tb;
	private boolean needsUpdate;

	private int speedIndex = 6;
	private final static double animSpeeds[] = { 0.1, 0.15, 0.2, 0.35, 0.5,
			0.75, 1, 1.5, 2, 3.5, 4, 5, 6, 7, 10, 15, 20 };
	private LongTouchManager longTouchManager;

	/**
	 * Slider to be shown as part of the extended Slider entries
	 */
	private SliderPanelW slider;

	/**
	 * panel to correctly display an extended slider entry
	 */
	private FlowPanel sliderPanel;

	/**
	 * panel to display animation speed control
	 */
	private FlowPanel speedPanel;

	/**
	 * this panel contains the marble (radio) and the play button for extended
	 * slider entries
	 */
	private FlowPanel marblePanel;

	/**
	 * start/pause slider animations
	 */
	PlayButton playButton;

	/**
	 * checkbox displaying boolean variables
	 */
	private CheckBox checkBox;

	/**
	 * whether the playButton currently shows a play or a pause icon
	 */
	private boolean playButtonValue;
	private Button btnSpeedDown;
	private Label lblSpeed;
	private Button btnSpeedUp;

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

	public static RadioButtonTreeItem create(GeoElement ge, SafeUri showUrl, SafeUri hiddenUrl) {
		if (ge.isMatrix()) {
			return new MatrixRadioButtonTreeItem(ge, showUrl, hiddenUrl);
		} else if (ge.isGeoCurveCartesian()) {
			return new ParCurveRadioButtonTreeItem(ge, showUrl, hiddenUrl);
		} else if (ge.isGeoFunctionConditional()) {
			return new CondFunRadioButtonTreeItem(ge, showUrl, hiddenUrl);
		}
		return new RadioButtonTreeItem(ge, showUrl, hiddenUrl);
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
	public RadioButtonTreeItem(final GeoElement ge, SafeUri showUrl,
			SafeUri hiddenUrl) {
		super();


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
		if (sliderNeeded()) {
			initSlider();
		}
		if (app.has(Feature.AV_EXTENSIONS)) {

			// createSpeedPanel();

			if (geo instanceof GeoBoolean && geo.isIndependent()) {
				// CheckBoxes
				checkBox = new CheckBox();
				checkBox.setValue(((GeoBoolean) geo).getBoolean());
				add(checkBox);
				checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						((GeoBoolean) geo).setValue(event.getValue());
						geo.updateCascade();
						// updates other views (e.g. Euclidian)
						kernel.notifyRepaint();
					}
				});
			}
		}
		seNoLatex = DOM.createSpan().cast();
		seNoLatex.addClassName("sqrtFontFix");
		EquationEditor.updateNewStatic(seNoLatex);
		updateColor(seNoLatex);
		ihtml = new InlineHTML();

		this.addDomHandler(this, DoubleClickEvent.getType());
		this.addDomHandler(this, ClickEvent.getType());
		this.addDomHandler(this, MouseMoveEvent.getType());
		this.addDomHandler(this, MouseDownEvent.getType());
		this.addDomHandler(this, MouseUpEvent.getType());
		this.addDomHandler(this, MouseOverEvent.getType());
		this.addDomHandler(this, MouseOutEvent.getType());
		this.addDomHandler(this, TouchStartEvent.getType());
		this.addDomHandler(this, TouchMoveEvent.getType());
		this.addDomHandler(this, TouchEndEvent.getType());

		addSpecial(ihtml);
		ihtml.getElement().appendChild(seNoLatex);

		SpanElement se2 = DOM.createSpan().cast();
		se2.appendChild(Document.get().createTextNode(
		        "\u00A0\u00A0\u00A0\u00A0"));
		ihtml.getElement().appendChild(se2);
		// String text = "";
		if (geo.isIndependent()
				|| (app.has(Feature.AV_EXTENSIONS) && geo instanceof GeoBoolean)) {
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
				geo.addLabelTextOrHTML(geo
				        .getCommandDescription(StringTemplate.defaultTemplate),
						getBuilder(seNoLatex));
				break;
			}
		}
		// if enabled, render with LaTeX
		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			if (geo.isDefined()) {
				String latexStr = geo.getLaTeXAlgebraDescription(true,
						StringTemplate.latexTemplateMQ);
				if ((latexStr != null) && geo.isLaTeXDrawableGeo()) {
					this.needsUpdate = true;
					av.repaintView();
				}
			}
		} else {
			// nothing to do, senolatex already up to date
		}
		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
		setDraggable();

		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("AlgebraViewObjectStylebar");
		buttonPanel.setVisible(false);

		xButton = new PushButton(new Image(
				GuiResources.INSTANCE.algebra_delete()));
		xButton.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.algebra_delete_hover()));
		xButton.addStyleName("XButton");
		xButton.addStyleName("shown");
		xButton.addMouseDownHandler(new MouseDownHandler() {
			// ClickHandler changed to MouseDownHandler
			// in order to fix a bug in Internet Explorer,
			// where the button disappeared earlier than
			// this method (onClick) could execute
			@Override
			public void onMouseDown(MouseDownEvent event) {

				event.stopPropagation();
				ge.remove();
			}
		});


		add(buttonPanel);// dirty hack of adding it two times!

		// pButton should be added before xButton is added!
		// also, the place of buttonPanel should also be changed!
		// so these things are moved to replaceXbuttonDOM!
		// buttonPanel.add(xButton);
	}

	private boolean sliderNeeded() {
		return app.has(Feature.AV_EXTENSIONS) && geo instanceof GeoNumeric
				&& ((GeoNumeric) geo).isShowingExtendedAV();
	}

	private void initSlider() {
		if (!geo.isEuclidianVisible()) {
		// // number inserted via input bar
		// // -> initialize min/max etc.
			geo.setEuclidianVisible(true);
			geo.setEuclidianVisible(false);
		}

		// if the geo still has no min/max, it should not be displayed with
		// a slider (e.g. boxplots)
		if (((GeoNumeric) geo).getIntervalMinObject() != null
				&& ((GeoNumeric) geo).getIntervalMaxObject() != null) {

			slider = new SliderPanelW(((GeoNumeric) geo).getIntervalMin(),
					((GeoNumeric) geo).getIntervalMax());
			slider.setValue(((GeoNumeric) geo).getValue());
			slider.setStep(geo.getAnimationStep());

			slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					((GeoNumeric) geo).setValue(event.getValue());
					geo.updateCascade();
					// updates other views (e.g. Euclidian)
					kernel.notifyRepaint();
				}
			});

			sliderPanel = new FlowPanel();

			add(sliderPanel);

			if (app.has(Feature.AV_EXTENSIONS)) {
				createSpeedButtons();
			}

			if (geo.isAnimatable()) {
				ImageResource imageresource = geo.isAnimating() ? AppResources.INSTANCE
						.nav_pause() : AppResources.INSTANCE.nav_play();
				playButton = new PlayButton(imageresource);

				ClickStartHandler.init(playButton, new ClickStartHandler() {
					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
						boolean newValue = !(geo.isAnimating() && app
								.getKernel().getAnimatonManager().isRunning());
						geo.setAnimating(newValue);
						playButton.setResource(newValue ? AppResources.INSTANCE
								.nav_pause() : AppResources.INSTANCE.nav_play());
						geo.updateRepaint();

						if (geo.isAnimating()) {
							geo.getKernel().getAnimatonManager()
									.startAnimation();
							showSpeedButtons(true);
						} else {
							showSpeedButtons(false);

						}
					}
				});
				FlowPanel p = new FlowPanel();
				p.addStyleName("panelRow");
				p.add(playButton);
				p.add(btnSpeedDown);
				p.add(btnSpeedUp);
				marblePanel.add(p);
			}
		}

	}

	public void replaceXButtonDOM(TreeItem item) {
		// in subclasses pButton will be added first!
		// also, this method should be overridden in NewRadioButtonTreeItem
		// buttonPanel.add(pButton);

		buttonPanel.add(xButton);
		item.getElement().addClassName("XButtonPanelParent");
		item.getElement().appendChild(buttonPanel.getElement());
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
		seNoLatex.addClassName("sqrtFontFix");
		if (av.isRenderLaTeX()) {
			this.needsUpdate = true;

			// here it complains that geo is undefined
			doUpdate();
		}



		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
		setDraggable();
	}

	private void setAnimationSpeed() {
		double speed = animSpeeds[speedIndex];
		geo.setAnimationSpeed(speed);
		lblSpeed.setText(speed + "x");

	}

	private void showSpeedButtons(boolean value) {
		btnSpeedUp.setVisible(value);
		btnSpeedDown.setVisible(value);

	}

	private void createSpeedButtons() {

		if (!geo.isAnimatable()) {
			return;
		}
		btnSpeedDown = new Button("<<");
		btnSpeedDown.addStyleName("avSpeedButton");
		btnSpeedUp = new Button(">>");
		btnSpeedUp.addStyleName("avSpeedButton");
		showSpeedButtons(false);
		btnSpeedDown.addClickHandler(this);
		btnSpeedUp.addClickHandler(this);

	}
	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	@Override
	public boolean popupSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	@Override
	public boolean hideSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 * 
	 * @return
	 */
	@Override
	public void showOrHideSuggestions() {
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	@Override
	public void shuffleSuggestions(boolean down) {
		//
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
	@Override
	public void keydown(int key, boolean alt, boolean ctrl, boolean shift) {
		if (commonEditingCheck()) {
			DrawEquationWeb.triggerKeydown(this, seMayLatex, key,
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
	@Override
	public void keypress(char character, boolean alt, boolean ctrl,
			boolean shift, boolean more) {
		if (commonEditingCheck()) {
			DrawEquationWeb.triggerKeypress(this, seMayLatex,
			        character, alt, ctrl, shift, more);
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
	@Override
	public final void keyup(int key, boolean alt, boolean ctrl, boolean shift) {
		if (commonEditingCheck()) {
			DrawEquationWeb.triggerKeyUp(seMayLatex, key,
			        alt, ctrl, shift);
		}
	}

	@Override
	public void handleLongTouch(int x, int y) {
		//if (newCreationMode) {
			// maybe this fixes a bug with not focusing
			// but in this case, focus is called about
			// three times, which might cause problems
			// TouchStart, TouchEnd, long touch so disabled
		//	setFocus(true);
		//}
		onRightClick(x, y);
	}

	public void repaint() {
		if (needsUpdate
				|| playButtonValue != (geo.isAnimating() && app.getKernel()
						.getAnimatonManager().isRunning())) {
			doUpdate();
		}
	}

	public boolean commonEditingCheck() {
		return av.isEditing() || isThisEdited() || newCreationMode;
	}

	private void doUpdate() {
		// check for new LaTeX
		needsUpdate = false;
		boolean newLaTeX = false;

		if (this.checkBox != null
		        && ((HasExtendedAV) geo).isShowingExtendedAV()) {
			// adds the checkBox at the right side
			addSpecial(ihtml);

			// reset the value of the checkBox
			checkBox.setValue(((GeoBoolean) geo).getBoolean());

			// reset the label text
			geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(seNoLatex));
			return;
		} else if (this.checkBox != null) {
			remove(checkBox);
		}
		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String text = "";
			if (geo != null) {
				if (app.has(Feature.JLM_IN_WEB) && !newCreationMode) {
					text = geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplate);
				} else {
					text = geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplateMQ);
				}

				if ((text != null) && text.length() < 1500
						&& geo.isLaTeXDrawableGeo() && geo.isDefined()) {
					newLaTeX = true;
				}
			} else {
				newLaTeX = true;
			}
			// now we have text and how to display it (newLaTeX/LaTeX)
			if (LaTeX && newLaTeX) {
				if (newCreationMode) {
					text = geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplateMQ);
					// or false? well, in theory, it should not matter
				}
				updateLaTeX(text);

			} else if (newLaTeX) {
				renderLatex(text, seNoLatex, newCreationMode);
				LaTeX = true;
			}

		} else if (geo == null) {
			return;
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
				updateColor(seNoLatex);
				if (!newCreationMode && app.has(Feature.JLM_IN_WEB)
						&& c != null) {
					ihtml.getElement().replaceChild(seNoLatex,
							c.getCanvasElement());
				} else {
					ihtml.getElement().replaceChild(seNoLatex, seMayLatex);
				}

				LaTeX = false;
			}
		}

		if (radio != null) {
			radio.setChecked(geo.isEuclidianVisible());
		}

		updatePlayButton();

		if (geo instanceof GeoNumeric
				&& (slider != null && sliderPanel != null) || sliderNeeded()) {
			if (slider == null) {
				initSlider();
				addSpecial(ihtml);
				getElement().setDraggable(Element.DRAGGABLE_FALSE);
			}
			slider.setMinimum(((GeoNumeric) geo).getIntervalMin());
			slider.setMaximum(((GeoNumeric) geo).getIntervalMax());
			slider.setStep(geo.getAnimationStep());
			slider.setValue(((GeoNumeric) geo).value);
			if (((HasExtendedAV) geo).isShowingExtendedAV()) {
				sliderPanel.add(slider);
				// sliderPanel.add(speedPanel);
				marblePanel.add(playButton);
			} else if (marblePanel != null) {
				sliderPanel.remove(slider);
				marblePanel.remove(playButton);
			}
		}
	}

	private Canvas c;

	private void renderLatex(String text0, Element old,
			boolean forceMQ) {
		if (app.has(Feature.JLM_IN_WEB) && !forceMQ) {
			c = DrawEquationWeb.paintOnCanvas(geo, text0, c, getFontSize());
			if (c != null && ihtml.getElement().isOrHasChild(old)) {
				ihtml.getElement().replaceChild(c.getCanvasElement(), old);
			}
		} else {
			SpanElement se = DOM.createSpan().cast();
			EquationEditor.updateNewStatic(se);
			updateColor(se);
			ihtml.getElement().replaceChild(se, old);
			String text = text0;
			if (text0 == null) {
				text = "";
			}
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
		}

	}

	private void updateLaTeX(String text0) {
		if (app.has(Feature.JLM_IN_WEB)) {
			c = DrawEquationWeb.paintOnCanvas(geo, text0, c, getFontSize());
		} else {
			if ("".equals(text0) || (text0 == null)) {
				DrawEquationWeb
						.updateEquationMathQuillGGB("", seMayLatex, true);
			} else {
				String text = DrawEquationWeb.inputLatexCosmetics(text0);
				int tl = text.length();
				text = DrawEquationWeb.stripEqnArray(text);
				updateColor(seMayLatex);
				DrawEquationWeb.updateEquationMathQuillGGB("\\mathrm{" + text
						+ "}", seMayLatex, tl == text.length());
				updateColor(seMayLatex);
			}

		}

	}

	/**
	 * @return size for JLM texts. Due to different fonts we need a bit more
	 *         than app.getFontSize(), but +3 looked a bit too big
	 */
	private int getFontSize() {
		return app.getFontSize() + 1;
	}
	void updatePlayButton() {
		 if (playButton != null) {
			 // update the icon of the playButton (if animation is started/paused
			 // from another place)
			 // TODO store actual icon and check before replacing it
			 playButtonValue = geo.isAnimating()
			 && app.getKernel().getAnimatonManager().isRunning();
			 ImageResource newIcon = playButtonValue ? AppResources.INSTANCE
			 .nav_pause() : AppResources.INSTANCE.nav_play();
			 playButton.setResource(newIcon);
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
			// if (LaTeX) {
				DrawEquationWeb
				        .endEditingEquationMathQuillGGB(this, seMayLatex);
				if (!this.newCreationMode && app.has(Feature.JLM_IN_WEB)
						&& c != null) {
					this.ihtml.getElement().replaceChild(c.getCanvasElement(),
							seMayLatex);
				}
				if (!this.newCreationMode && !LaTeX
						&& seNoLatex != null
						&& ihtml.getElement().isOrHasChild(seMayLatex)) {
					this.ihtml.getElement().replaceChild(seNoLatex, seMayLatex);
				}
			// } else {
			// removeSpecial(tb);
			// addSpecial(ihtml);
			// stopEditingSimple(tb.getText());
			// }
		}
	}

	public void startEditing() {
		// buttonPanel.setVisible(true);

		if (isThisEdited()) {
			return;
		}

		thisIsEdited = true;
		if (newCreationMode) {
			DrawEquationWeb.editEquationMathQuillGGB(this,
			        seMayLatex, true);

			app.getGuiManager().setOnScreenKeyboardTextField(this);
			CancelEventTimer.keyboardSetVisible();
			ClickStartHandler.init(this, new ClickStartHandler(false, false) {
				@Override
				public void onClickStart(int x, int y,
						final PointerEventType type) {
					app.getGuiManager().setOnScreenKeyboardTextField(
							RadioButtonTreeItem.this);
					// prevent that keyboard is closed on clicks (changing
					// cursor position)
					CancelEventTimer.keyboardSetVisible();
				}
			});
		} else {
			if (app.has(Feature.JLM_IN_WEB) && c != null) {
				renderLatex(geo.getLaTeXAlgebraDescriptionWithFallback(true,
						StringTemplate.latexTemplateMQ, true),
						c.getCanvasElement(), true);
			} else if (!LaTeX) {
				renderLatex(geo.getLaTeXAlgebraDescriptionWithFallback(true,
						StringTemplate.latexTemplateMQ, true), seNoLatex, true);
			}
			DrawEquationWeb.editEquationMathQuillGGB(this,
			        seMayLatex, false);

			app.getGuiManager().setOnScreenKeyboardTextField(this);
			CancelEventTimer.keyboardSetVisible();
			ClickStartHandler.init(this, new ClickStartHandler(false, false) {
				@Override
				public void onClickStart(int x, int y,
						final PointerEventType type) {
					app.getGuiManager().setOnScreenKeyboardTextField(
							RadioButtonTreeItem.this);
					// prevent that keyboard is closed on clicks (changing
					// cursor position)
					CancelEventTimer.keyboardSetVisible();
				}
			});
		} /*
		 * else { removeSpecial(ihtml); tb = new GTextBox();
		 * tb.setText(geo.getAlgebraDescriptionDefault()); addSpecial(tb); mout
		 * = false; tb.setFocus(true); Scheduler.get().scheduleDeferred(new
		 * Scheduler.ScheduledCommand() {
		 * 
		 * @Override public void execute() { tb.setFocus(true); } });
		 * tb.addKeyDownHandler(new KeyDownHandler() {
		 * 
		 * @Override public void onKeyDown(KeyDownEvent kevent) { if
		 * (kevent.getNativeKeyCode() == 13) { removeSpecial(tb);
		 * addSpecial(ihtml); stopEditingSimple(tb.getText());
		 * app.hideKeyboard(); } else if (kevent.getNativeKeyCode() == 27) {
		 * removeSpecial(tb); addSpecial(ihtml); stopEditingSimple(null);
		 * app.hideKeyboard(); } } }); tb.addBlurHandler(new BlurHandler() {
		 * 
		 * @Override public void onBlur(BlurEvent bevent) { if (mout &&
		 * !blockBlur) { removeSpecial(tb); addSpecial(ihtml);
		 * stopEditingSimple(null); } } }); tb.addMouseOverHandler(new
		 * MouseOverHandler() {
		 * 
		 * @Override public void onMouseOver(MouseOverEvent moevent) { mout =
		 * false; } }); tb.addMouseOutHandler(new MouseOutHandler() {
		 * 
		 * @Override public void onMouseOut(MouseOutEvent moevent) { mout =
		 * true; tb.setFocus(true); } });
		 * 
		 * ClickStartHandler.init(tb, new ClickStartHandler(false, true) {
		 * 
		 * @Override public void onClickStart(int x, int y, final
		 * PointerEventType type) {
		 * app.getGuiManager().setOnScreenKeyboardTextField(tb); // prevent that
		 * keyboard is closed on clicks (changing // cursor position)
		 * CancelEventTimer.keyboardSetVisible(); } }); }
		 */

		scrollIntoView();

		buttonPanel.setVisible(true);
		maybeSetPButtonVisibility(true);
	}

	/*
	public void stopEditingSimple(String newValue) {

		removeCloseButton();

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
	}*/

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

		removeCloseButton();

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
		if (!this.newCreationMode && app.has(Feature.JLM_IN_WEB) && c != null
				&& ihtml.getElement().isOrHasChild(seMayLatex)) {
			this.ihtml.getElement().replaceChild(c.getCanvasElement(),
					seMayLatex);
		}
		if (!LaTeX && !this.newCreationMode
				&& seNoLatex != null
				&& ihtml.getElement().isOrHasChild(seMayLatex)) {
			this.ihtml.getElement().replaceChild(seNoLatex, seMayLatex);
		}
		// maybe it's possible to enter something which is non-LaTeX
		if (ret)
			doUpdate();

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
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
	@Override
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
								@Override
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
		evt.stopPropagation();
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if (commonEditingCheck())
			return;

		onDoubleClickAction(evt.isControlKeyDown());
	}

	private void onDoubleClickAction(boolean ctrl) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selection.clearSelectedGeos();
		ev.resetMode();
		if (geo != null && !ctrl) {
			if (!isThisEdited()) {
				av.startEditing(geo);
			}
			app.showKeyboard(this);
			this.setFocus(true);
		}
	}

	protected boolean shouldEditLaTeX() {
		return (LaTeX || geo.isGeoPoint() || geo.isGeoNumeric())
		// && !(geo.isGeoVector() && geo.isGeoElement3D())
				&& (geo.isIndependent()
						|| (geo.getParentAlgorithm() instanceof AlgoCurveCartesian) || geo
							.isPointOnPath());
		// AlgoCurveCartesian3D is an instance of AlgoCurveCartesian too
	}

	private boolean isWidgetHit(Widget w, int x, int y) {
		if (w == null) {
			return false;
		}
		int left = w.getAbsoluteLeft();
		int top = w.getAbsoluteTop();
		int right = left + w.getOffsetWidth();
		int bottom = top + w.getOffsetHeight();

		return (x > left && x < right && y > top && y < bottom);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {

		event.stopPropagation();
		// int x = event.getClientX();
		// int y = event.getClientY();
		//
		// if (isWidgetHit(btnSpeedDown, x, y)) {
		// animSpeedDown();
		// return;
		// } else if (isWidgetHit(btnSpeedUp, x, y)) {
		// animSpeedUp();
		// return;
		// }

		if (commonEditingCheck()) {
			// in newCreationMode, this is necessary after the
			// MathQuillGGB gets its focusMathQuillGGB method...
			// and setFocus will be called in onPointerDown anyway.
			// about other default actions of this event,
			// I don't care (MathQuillGGB formula is over this,
			// and accepts its events before this, e.g. the
			// selection highlighting event, which looks good)

			// as for an Internet Explorer bug in editing mode
			// (not newCreationMode) the condition is extended
			// to all cases of editing
			event.preventDefault();
		}
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
		        ZeroOffset.instance);
		onPointerDown(wrappedEvent);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
	}

	private void animSpeedUp() {
		if (speedIndex < animSpeeds.length - 1) {
			speedIndex++;
			setAnimationSpeed();
		}
	}

	private void animSpeedDown() {
		if (speedIndex > 0) {
			speedIndex--;
			setAnimationSpeed();

		}
	}

	@Override
	public void onClick(ClickEvent evt) {
		evt.stopPropagation();
		Object source = evt.getSource();
		if (source == btnSpeedDown) {
			animSpeedDown();
			return;
		} else if (source == btnSpeedUp) {
			animSpeedUp();
			return;
		}
		// this 'if' should be the first one in every 'mouse' related method
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
		        ZeroOffset.instance);
		onPointerUp(wrappedEvent);

	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		if (sliderPanel == null) {
			evt.stopPropagation();
		}
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
		if (sliderPanel != null) {
			return;
		}
		event.stopPropagation();
		// event.preventDefault();
		if (newCreationMode) {
			// this might cause strange behaviour
			setFocus(true);
		}
		long time = System.currentTimeMillis();
		if (time - latestTouchEndTime < 500) {
			// ctrl key, shift key for TouchEndEvent? interesting...
			latestTouchEndTime = time;
			if (!commonEditingCheck()) {
				onDoubleClickAction(false // event.isControlKeyDown(),
				// event.isShiftKeyDown()
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
		if (sliderPanel != null) {
			return;
		}
		event.stopPropagation();
		// event.preventDefault();
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
		if (sliderPanel != null) {
			return;
		}
		// this would propagate the event to
		// AlgebraView.onBrowserEvent... is this we want?
		// probably no, as there is a stopPropagation
		// in the onMouseDown method as well...
		event.stopPropagation();
		event.preventDefault();
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
		} else if (commonEditingCheck()) {
			if (!av.isEditing()) {
				// e.g. Web.html might not be in editing mode
				// initially (temporary fix)
				ensureEditing();
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

	private void selectGeo(AbstractEvent event) {
		// handle selecting geo
		if (event.isControlDown()) {
			selection.toggleSelectedGeo(geo);
			if (selection.getSelectedGeos().contains(geo)) {
				av.setLastSelectedGeo(geo);
			}
		} else if (event.isShiftDown() && av.getLastSelectedGeo() != null) {
			boolean nowSelecting = true;
			boolean selecting = false;
			boolean aux = geo.isAuxiliaryObject();
			boolean ind = geo.isIndependent();
			boolean aux2 = av.getLastSelectedGeo().isAuxiliaryObject();
			boolean ind2 = av.getLastSelectedGeo().isIndependent();

			if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				boolean direction = geo
						.getLabel(StringTemplate.defaultTemplate).compareTo(
								av.getLastSelectedGeo().getLabel(
										StringTemplate.defaultTemplate)) < 0;

				while (it.hasNext()) {
					GeoElement geo2 = it.next();
					if ((geo2.isAuxiliaryObject() == aux && aux)
							|| (geo2.isAuxiliaryObject() == aux && geo2
									.isIndependent() == ind)) {

						if (direction && geo2.equals(av.getLastSelectedGeo()))
							selecting = !selecting;
						if (!direction && geo2.equals(geo))
							selecting = !selecting;

						if (selecting) {
							selection.toggleSelectedGeo(geo2);
							nowSelecting = selection.getSelectedGeos()
									.contains(geo2);
						}
						if (!direction && geo2.equals(av.getLastSelectedGeo()))
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
	private void onPointerUp(AbstractEvent event) {
		if (commonEditingCheck()) {
			if (newCreationMode) {
				AlgebraStyleBarW styleBar = ((AlgebraViewW) av)
						.getStyleBar(false);
				if (styleBar != null) {
					styleBar.update(null);
				}
			}
			return;
		}
		int mode = app.getActiveEuclidianView().getMode();
		if (// !skipSelection &&
		(mode == EuclidianConstants.MODE_MOVE)) {
			// update selection
			if (geo == null) {
				selection.clearSelectedGeos();
			} else {
				selectGeo(event);

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

		if (((AlgebraViewW) this.av).getStyleBar(false) != null) {
			((AlgebraViewW) this.av).getStyleBar(false).update(this.getGeo());
		}

		// note that this is only called when we are not doing editing!
		addDeleteButton();
	}

	/**
	 * This method shall only be called when we are not doing editing, so this
	 * is for the delete button at selection
	 */
	private void addDeleteButton() {

		if (app.has(Feature.DELETE_IN_ALGEBRA) && geo != null) {
			buttonPanel.setVisible(true);
			if (!isThisEdited()) {
				maybeSetPButtonVisibility(false);
			}
			((AlgebraViewW) this.av).setActiveTreeItem(this);
		}

	}

	protected void maybeSetPButtonVisibility(boolean bool) {
		// only show the delete button, but not the extras
	}

	private void onPointerMove(AbstractEvent event) {
		if (commonEditingCheck())
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
		if (commonEditingCheck())
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
		setFocus(b, false);
	}

	@Override
	public void setFocus(boolean b, boolean sv) {
		DrawEquationWeb.focusEquationMathQuillGGB(
		        seMayLatex, b);
	}

	@Override
	public void insertString(String text) {
		// even worse
		// for (int i = 0; i < text.length(); i++)
		// geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
		// seMayLatex, "" + text.charAt(i), "", false);

		DrawEquationWeb.writeLatexInPlaceOfCurrentWord(this, seMayLatex, text,
				"", false);
	}

	@Override
	public String getText() {
		if (seMayLatex == null)
			return "";

		String ret = DrawEquationWeb.getActualEditedValue(seMayLatex, false);

		if (ret == null)
			return "";

		return ret;
	}

	@Override
	public void scrollCursorIntoView() {
		if (seMayLatex != null) {
			DrawEquationWeb.scrollCursorIntoView(this, seMayLatex,
					newCreationMode);
		}
	}

	@Override
	public void scrollIntoView() {
		this.getElement().scrollIntoView();
	}

	@Override
	public void typing(boolean heuristic) {
		// to be overridden in NewRadioButtonTreeItem,
		// to know whether it's empty, whether to show Xbutton
	}

	public void removeCloseButton() {
		this.maybeSetPButtonVisibility(true);
		buttonPanel.setVisible(false);
	}

	/*
	void removeSpecial(Widget w) {
		remove(w);
		if (sliderPanel != null) {
			sliderPanel.remove(w);
		}
	}
	*/

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
			if (((HasExtendedAV) geo).isShowingExtendedAV()) {
				add(checkBox);
			}
			add(w);
		} else {
			add(w);
		}
	}

	public void setDraggable() {
		Widget draggableContent = this;
		if (app.has(Feature.AV_EXTENSIONS) && geo instanceof GeoNumeric
				&& slider != null) {
			return;
			// draggableContent = ihtml;
			// getElement().setDraggable(Element.DRAGGABLE_FALSE);
		}
		getElement().setAttribute("position", "absolute");
		draggableContent.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		draggableContent.addDomHandler(new DragStartHandler() {

			@Override
			public void onDragStart(DragStartEvent event) {
				event.setData("text", "draggginggg");
				event.getDataTransfer().setDragImage(getElement(), 10, 10);
				event.stopPropagation();
				((AlgebraViewW) av).dragStart(event, geo);
			}
		}, DragStartEvent.getType());

	}

	@Override
	public App getApplication() {
		return app;
	}

	@Override
	public void ensureEditing() {
		if (!isThisEdited()) {
			startEditing();
		}
	}

	@Override
	public boolean getAutoComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> resetCompletions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getCompletions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final void toggleSymbolButton(boolean toggled) {
		// Just for compatibility with AutoCompleteTextFieldW
	}

	@Override
	public ArrayList<String> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSuggesting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public final Widget toWidget() {
		return this;
	}

	@Override
	public final SpanElement getLaTeXSpan() {
		return seMayLatex;
	}

	@Override
	public void updatePosition(ScrollableSuggestionDisplay sug) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean resetAfterEnter() {
		return true;
	}

	@Override
	public void onBlur(BlurEvent be) {
		// to be overridden in NewRadioButtonTreeItem
	}

	@Override
	public void onFocus(FocusEvent be) {
		// to be overridden in NewRadioButtonTreeItem
		// AppW.anyAppHasFocus = true;
	}

	@Override
	public String getLaTeX() {
		// TODO atm needed for CAS only
		return null;
	}

	@Override
	public boolean isForCAS() {
		return false;
	}

}
