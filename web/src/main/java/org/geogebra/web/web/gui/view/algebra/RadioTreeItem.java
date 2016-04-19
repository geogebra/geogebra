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
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasExtendedAV;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.html5.util.sliderPanel.SliderWJquery;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class RadioTreeItem extends AVTreeItem
 implements DoubleClickHandler,
		ClickHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler,
		MouseOverHandler, MouseOutHandler, GeoContainer, MathKeyboardListener,
		TouchStartHandler, TouchMoveHandler, TouchEndHandler, LongTouchHandler,
		EquationEditorListener, RequiresResize {

	private static final int LATEX_MAX_EDIT_LENGHT = 1500;

	private static final String REFX = "[AVR]";

	private static final int SLIDER_EXT = 15;
	private static final int DEFAULT_SLIDER_WIDTH = 100;
	private static final int EDIT_WIDTH = 350;
	static final String CLEAR_COLOR_STR = GColor
			.getColorString(new GColorW(255, 255, 255, 0));
	static final String CLEAR_COLOR_STR_BORDER = GColor
			.getColorString(new GColorW(220, 220, 220));
	Boolean stylebarShown;

	interface CancelListener {
		void cancel();
	}

	class AVField extends AutoCompleteTextFieldW {
		private CancelListener listener;

		public AVField(int columns, App app, CancelListener listener) {
			super(columns, app);
			this.listener = listener;
			setDeferredFocus(true);
		}

		@Override
		public void onKeyPress(KeyPressEvent e) {
			e.stopPropagation();
		}

		@Override
		public void onKeyDown(KeyDownEvent e) {
			e.stopPropagation();
			if (e.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
				listener.cancel();
			}

		}

		@Override
		public void onKeyUp(KeyUpEvent e) {
			e.stopPropagation();
		}

	}

	public void expandSize(int newWidth) {
		if (getAV().getOriginalWidth() != null) {
			return;
		}
		AlgebraDockPanelW avDockPanel = getAlgebraDockPanel();
		int w = avDockPanel.asWidget().getOffsetWidth();
		if (w < newWidth) {
			getAV().setOriginalWidth(w);
			avDockPanel.getParentSplitPane().setWidgetSize(avDockPanel,
					newWidth);
			avDockPanel.deferredOnResize();
		} else {
			getAV().setOriginalWidth(null);
		}
	}

	public void restoreSize() {
		Integer w = getAV().getOriginalWidth();
		if (w != null) {
			AlgebraDockPanelW avDockPanel = getAlgebraDockPanel();
			avDockPanel.getParentSplitPane().setWidgetSize(avDockPanel, w);
			avDockPanel.deferredOnResize();
			getAV().setOriginalWidth(null);
		}
	}

	private class MinMaxPanel extends AdvancedFlowPanel implements SetLabels,
			KeyHandler, MouseDownHandler, MouseUpHandler, CancelListener {
		private static final int MINMAX_MIN_WIDHT = 326;
		private AVField tfMin;
		private AVField tfMax;
		private AVField tfStep;
		private Label lblValue;
		private Label lblStep;
		private GeoNumeric num;
		private boolean keepOpen = false;
		private boolean focusRequested = false;
		public MinMaxPanel() {
			if (geo instanceof GeoNumeric) {
				num = (GeoNumeric) geo;
			}
			tfMin = new AVField(4, app, this);
			tfMax = new AVField(4, app, this);
			tfStep = new AVField(4, app, this);
			lblValue = new Label(GTE_SIGN + " "
					+ geo.getCaption(StringTemplate.defaultTemplate) + " "
					+ GTE_SIGN);
			lblStep = new Label(app.getPlain("Step"));
			addStyleName("minMaxPanel");
			add(tfMin);
			add(lblValue);
			add(tfMax);
			add(lblStep);
			add(tfStep);

			tfMin.setDeferredFocus(true);
			tfMax.setDeferredFocus(true);
			tfStep.setDeferredFocus(true);

			tfMin.addKeyHandler(this);
			tfMax.addKeyHandler(this);
			tfStep.addKeyHandler(this);

			tfMin.addFocusHandler(new FocusHandler() {

				public void onFocus(FocusEvent event) {
					tfMin.selectAll();
				}
			});
			tfMax.addFocusHandler(new FocusHandler() {

				public void onFocus(FocusEvent event) {
					tfMax.selectAll();
				}
			});
			tfStep.addFocusHandler(new FocusHandler() {

				public void onFocus(FocusEvent event) {
					if (focusRequested) {
						event.preventDefault();
						event.stopPropagation();
						return;
					}
					tfStep.selectAll();
				}
			});

			addMouseDownHandler(this);
			addMouseUpHandler(this);
			addBlurHandler(new BlurHandler() {

				public void onBlur(BlurEvent event) {

					hide();
				}
			});

			update();

		}

		public void update() {
			tfMin.setText(kernel.format(num.getIntervalMin(),
					StringTemplate.editTemplate));
			tfMax.setText(kernel.format(num.getIntervalMax(),
					StringTemplate.editTemplate));
			tfStep.setText(num.isAutoStep() ? "" : kernel.format(
					num.getAnimationStep(), StringTemplate.editTemplate));
			setLabels();
		}

		public void setLabels() {
			lblStep.setText(app.getPlain("Step"));
		}



		public void show() {
			geo.setAnimating(false);
			expandSize(MINMAX_MIN_WIDHT);
			sliderPanel.setVisible(false);
			setVisible(true);
			setKeepOpen(true);
			setOpenedMinMaxPanel(this);
			animPanel.setVisible(false);
		}

		public void hide(boolean restore) {
			if (restore) {
				restoreSize();
			}
			hide();
		}

		public void hide() {
			sliderPanel.setVisible(true);
			deferredResizeSlider();
			setVisible(false);
			if (animPanel != null) {
				animPanel.setVisible(true);
			}
		}


		public void keyReleased(KeyEvent e) {
			if (e.isEnterKey()) {
				apply();
			}
		}

		private void apply() {
			NumberValue min = getNumberFromInput(tfMin.getText().trim());
			NumberValue max = getNumberFromInput(tfMax.getText().trim());
			String stepText = tfStep.getText().trim();

			if (min != null && max != null
					&& min.getDouble() <= max.getDouble()) {
				num.setIntervalMin(min);
				num.setIntervalMax(max);
				if (stepText.isEmpty()) {
					num.setAutoStep(true);
				} else {
					num.setAutoStep(false);
					num.setAnimationStep(getNumberFromInput(stepText));
				}
				num.update();
				hide(true);
			}
		}

		// TODO: refactor needed: copied from SliderPanelW;
		private NumberValue getNumberFromInput(final String inputText) {
			boolean emptyString = inputText.equals("");
			NumberValue value = null;// new MyDouble(kernel, Double.NaN);
			if (!emptyString) {
				value = kernel.getAlgebraProcessor().evaluateToNumeric(
						inputText, false);
			}

			return value;
		}

		public void cancel() {
			hide();
		}

		public void onMouseUp(MouseUpEvent event) {
			if (isKeepOpen()) {
				setKeepOpen(false);
				return;
			}

			if (focusRequested) {
				focusRequested = false;
				return;
			}

			event.preventDefault();
			event.stopPropagation();

			if (!(selectAllOnFocus(tfMin, event)
					|| selectAllOnFocus(tfMax, event)
					|| selectAllOnFocus(tfStep, event))) {
				apply();
			}

		}

		public void onMouseDown(MouseDownEvent event) {
			if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
				return;
			}

			event.preventDefault();
			event.stopPropagation();

			selectAllOnFocus(tfMin, event);
			selectAllOnFocus(tfMax, event);
			selectAllOnFocus(tfStep, event);

		}

		private boolean selectAllOnFocus(AVField avField, MouseEvent event) {
			if (isWidgetHit(avField, event)) {
				avField.selectAll();
				return true;
			}
			return false;
		}

		public boolean isKeepOpen() {
			return keepOpen;
		}

		public void setKeepOpen(boolean keepOpen) {
			this.keepOpen = keepOpen;
		}

		public void setMinFocus() {
			tfMin.requestFocus();
			focusRequested = true;
		}

		public void setMaxFocus() {
			tfMax.requestFocus();
			focusRequested = true;
		}
	}

	private class MarblePanel extends FlowPanel {
		private static final int BACKGROUND_ALPHA = 60;
		private Marble marble;
		private boolean selected = false;

		public MarblePanel(final GeoElement geo) {

			marble = new Marble(RadioTreeItem.this);
			marble.setStyleName("marble");
			marble.setEnabled(geo.isEuclidianShowable());
			marble.setChecked(geo.isEuclidianVisible());

			addStyleName("marblePanel");
			add(marble);
			update();
		}

		public void setHighlighted(boolean selected) {
			this.selected = selected;
			// getElement().getStyle().setBackgroundColor(
			// selected ? getBgColorString() : CLEAR_COLOR_STR);
			// getElement().getStyle().setBorderColor(
			// selected ? getColorString() : CLEAR_COLOR_STR_BORDER);

		}

		public void update() {
			if (marble != null) {
				marble.setChecked(geo.isEuclidianVisible());
			}

			setHighlighted(selected);
		}

		private String getColorString() {
			GColor gc = geo.getAlgebraColor();
			GColorW color = new GColorW(gc.getRGB());
			return GColor.getColorString(color);

		}

		private String getBgColorString() {
			GColor gc = geo.getAlgebraColor();
			GColorW color = new GColorW(gc.getRed(), gc.getGreen(),
					gc.getBlue(), BACKGROUND_ALPHA);
			return GColor.getColorString(color);

		}
	}

	private static MinMaxPanel openedMinMaxPanel = null;

	protected FlowPanel main;

	protected FlowPanel buttonPanel;
	protected PushButton btnDelete;

	GeoElement geo;
	Kernel kernel;
	protected AppW app;
	protected AlgebraView av;
	private boolean latex = false;
	private boolean editing = false;

	protected FlowPanel latexItem;
	private FlowPanel plainTextItem;

	FlowPanel ihtml;
	GTextBox tb;
	private boolean needsUpdate;



	private static final String GTE_SIGN = "\u2264";

	private static final String DV = "[DV]";
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
	 * this panel contains the marble (radio) button
	 */
	private MarblePanel marblePanel;

	FlowPanel contentPanel;


	/**
	 * checkbox displaying boolean variables
	 */
	private CheckBox checkBox;

	/**
	 * panel to display animation related controls
	 */

	AnimPanel animPanel;
	private ScheduledCommand resizeSliderCmd = new ScheduledCommand() {

		public void execute() {
			resizeSlider();
		}
	};
	private MinMaxPanel minMaxPanel;
	private boolean definitionAndValue;

	private FlowPanel valuePanel;

	private FlowPanel definitionPanel;

	private FlowPanel outputPanel;


	public void updateOnNextRepaint() {
		needsUpdate = true;
	}

	private IndexHTMLBuilder getBuilder(final Widget w) {
		return new IndexHTMLBuilder(false) {
			Element sub = null;

			@Override
			public void append(String s) {

				if (sub == null) {
					w.getElement()
							.appendChild(Document.get().createTextNode(s));
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
					w.getElement().appendChild(sub);
				}
				sub = null;
			}

			@Override
			public String toString() {
				if (sub != null) {
					endIndex();
				}
				return w.getElement().getInnerHTML();
			}

			@Override
			public void clear() {
				w.getElement().removeAllChildren();
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
	 * Creates the specific tree item due to the type of the geo element.
	 * 
	 * @param geo0
	 *            the geo element which is the item for.
	 * @return The appropriate RadioTreeItem descendant.
	 */
	public static RadioTreeItem create(GeoElement geo0) {
		if (geo0.isMatrix()) {
			return new MatrixTreeItem(geo0);
		} else if (geo0.isGeoCurveCartesian()) {
			return new ParCurveTreeItem(geo0);
		} else if (geo0.isGeoFunctionConditional()) {
			return new CondFunctionTreeItem(geo0);
		}
		return new RadioTreeItem(geo0);
	}

	protected void addDomHandlers(FlowPanel panel) {
		panel.addDomHandler(this, DoubleClickEvent.getType());
		panel.addDomHandler(this, ClickEvent.getType());
		panel.addDomHandler(this, MouseOverEvent.getType());
		panel.addDomHandler(this, MouseOutEvent.getType());
		panel.addDomHandler(this, MouseMoveEvent.getType());
		panel.addDomHandler(this, MouseDownEvent.getType());
		panel.addDomHandler(this, MouseUpEvent.getType());
		panel.addDomHandler(this, TouchStartEvent.getType());
		panel.addDomHandler(this, TouchMoveEvent.getType());
		panel.addDomHandler(this, TouchEndEvent.getType());

	}
	

	/**
	 * Minimal constructor
	 *
	 * @param kernel
	 */
	public RadioTreeItem(Kernel kernel) {
		super();
		this.kernel = kernel;
		app = (AppW) kernel.getApplication();
		av = app.getAlgebraView();

		main = new FlowPanel();
		setWidget(main);
		border = Dom.querySelectorForElement(getElement(), "gwt-TreeItem")
				.getStyle();
		definitionAndValue = app.has(Feature.AV_DEFINITION_AND_VALUE);
		selectionCtrl = getAV().getSelectionCtrl();
		setPlainTextItem(new FlowPanel());
		ihtml = new FlowPanel();
		setLongTouchManager(LongTouchManager.getInstance());
		setDraggable();

	}

	/**
	 * Creates a new RadioTreeItem for displaying/editing an existing GeoElement
	 * 
	 * @param geo0
	 *            the existing GeoElement to display/edit
	 */
	public RadioTreeItem(final GeoElement geo0) {
		this(geo0.getKernel());

		geo = geo0;
		main.addStyleName("elem");
		main.addStyleName("panelRow");

		marblePanel = new MarblePanel(geo0);

		main.add(marblePanel);


		if (geo instanceof GeoBoolean && geo.isSimple()) {
			createCheckbox();
		}


		getPlainTextItem().addStyleName("sqrtFontFix");
		getPlainTextItem().addStyleName("avPlainTextItem");
		updateColor(getPlainTextItem());
		updateFont(getPlainTextItem());

		ihtml.addStyleName("elemText");

		addDomHandlers(main);

		// Sliders
		if (sliderNeeded()) {
			initSlider();
		} else {
			addAVEXWidget(ihtml);
		}

		ihtml.add(getPlainTextItem());
		buildPlainTextItem();
		// if enabled, render with LaTeX

		if (getLatexString(true, null) != null) {
			setNeedsUpdate(true);
			av.repaintView();

		}

		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("AlgebraViewObjectStylebar");

		buttonPanel.addStyleName("smallStylebar");

		buttonPanel.setVisible(false);

		main.add(buttonPanel);

		deferredResizeSlider();

	}

	private void createCheckbox() {
		checkBox = new CheckBox();
		checkBox.setValue(((GeoBoolean) geo).getBoolean());
		main.add(checkBox);
		checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				((GeoBoolean) geo).setValue(event.getValue());
				geo.updateCascade();
				kernel.notifyRepaint();

			}
		});

	}

	private String getLatexString(boolean mathquill, Integer limit) {
		if (!av.isRenderLaTeX()
				|| (kernel.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_VALUE
						&& !isDefinitionAndValue())
				|| !geo.isDefined() || !geo.isLaTeXDrawableGeo()) {
			return null;
		}

		String text = geo.getLaTeXAlgebraDescription(true,
				mathquill ? StringTemplate.latexTemplateMQ
						: StringTemplate.latexTemplate);

		if ((text != null) && (limit == null || (text.length() < limit))) {
			return text;
		}

		return null;
	}

	/**
	 * Gets (and creates if there is not yet) the delete button which geo item
	 * can be removed with from AV.
	 * 
	 * @return The "X" button.
	 */
	protected PushButton getDeleteButton() {
		if (btnDelete == null) {
			btnDelete = new PushButton(
					new Image(
					GuiResources.INSTANCE.algebra_delete()));
			btnDelete.getUpHoveringFace().setImage(
					new Image(GuiResources.INSTANCE.algebra_delete_hover()));
			btnDelete.addStyleName("XButton");
			btnDelete.addStyleName("shown");
			btnDelete.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
						return;
					}
					event.stopPropagation();
					geo.remove();
				}
			});
		}
		return btnDelete;

	}

	private void buildPlainTextItem() {
		if (geo.isIndependent() && geo.getDefinition() == null) {
			geo.getAlgebraDescriptionTextOrHTMLDefault(
					getBuilder(getPlainTextItem()));
		} else {
			IndexHTMLBuilder sb;
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				geo.getAlgebraDescriptionTextOrHTMLDefault(
						getBuilder(getPlainTextItem()));
				break;

			case Kernel.ALGEBRA_STYLE_DESCRIPTION:
				geo.addLabelTextOrHTML(
						geo.getDefinitionDescription(StringTemplate.defaultTemplate),
						getBuilder(getPlainTextItem()));
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				geo.addLabelTextOrHTML(
						geo.getDefinition(StringTemplate.defaultTemplate),
						getBuilder(getPlainTextItem()));
				break;
			case Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE:
				buildItemContent();
				break;
			}
		}
	}

	private void createDVPanels() {
		if (definitionPanel == null) {
			definitionPanel = new FlowPanel();
			definitionPanel.addStyleName("avDefinition");
		}

		if (outputPanel == null) {
			outputPanel = new FlowPanel();
			outputPanel.addStyleName("avOutput");
		}

		if (valuePanel == null) {
			valuePanel = new FlowPanel();
			valuePanel.addStyleName("avValue");
		}


	}

	private String lastDefinition = "";

	private boolean updateDefinitionPanel() {

		if (latex) {
			String text = getTextForEditing(false,
					StringTemplate.latexTemplate);
			if (lastDefinition.equals(text)) {
				return false;
			}

			definitionPanel.clear();
			c = latexToCanvas(text);
			definitionPanel.add(c);
			lastDefinition = text;
		} else if (geo != null) {

			IndexHTMLBuilder sb = getBuilder(definitionPanel);
			geo.addLabelTextOrHTML(
					geo.getDefinition(StringTemplate.defaultTemplate), sb);

		}

		return true;
	}

	private boolean updateValuePanel() {
		String text = getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
		latex = text != null;
		return updateValuePanel(text);
		// getTextForEditing(false, StringTemplate.latexTemplate));
	}

	private boolean updateValuePanel(String text) {
		if (geo == null || !geo.needToShowBothRowsInAV()) {
			return false;
		}

		final Label lblDefinition = new Label(getOutputPrefix());

		if (app.has(Feature.FRACTIONS)) {
			ClickStartHandler.init(lblDefinition, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					toggleSymbolic(lblDefinition);

				}
			});
		}

		updateColor(lblDefinition);
		outputPanel.clear();
		outputPanel.add(lblDefinition);
		valuePanel.clear();
		IndexHTMLBuilder sb = new IndexHTMLBuilder(false);
		geo.getAlgebraDescriptionTextOrHTMLDefault(sb);
		valuePanel.add(new Label(sb.toString()));
		if (latex) {
			valC = DrawEquationW.paintOnCanvas(geo, text, valC, getFontSize());
			if (valC != null) {
				valuePanel.clear();
				valuePanel.add(valC);
			}
		}

		return true;
	}

	private void buildItemContent() {
		if (isDefinitionAndValue()) {
			if (isEditing() || geo == null) {
				return;
			}

			if (geo.needToShowBothRowsInAV()) {
				buildItemWithTwoRows();
			} else {
				buildItemWithSingleRow();
			}
		} else {
			buildPlainTextItem();
		}
	}

	private void buildItemWithTwoRows() {
		createDVPanels();
		String text = getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
		latex = text != null;

		if (updateDefinitionPanel()) {
			plainTextItem.clear();
			plainTextItem.add(definitionPanel);
			// Log.debug("[AVR] Definition panel is updated");
		}

		if (updateValuePanel(text)) {
			outputPanel.add(valuePanel);
			plainTextItem.add(outputPanel);

			Log.debug("[AVR] Value panel is updated");
		}
	}

	private void buildItemWithSingleRow() {

		// LaTeX
		if (av.isRenderLaTeX()) {
			String text = getLatexString(isInputTreeItem(),
					LATEX_MAX_EDIT_LENGHT);
			latex = text != null;

			if (latex) {
				if (isInputTreeItem()) {
					text = geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplateMQ);
				}

				c = DrawEquationW.paintOnCanvas(geo, text, c, getFontSize());
				ihtml.clear();
				ihtml.add(c);
			}

		}
	}

	//
	// private Label getDefinitionPrefixLabel() {
	// final Label lblDefinition = new Label(getOutputPrefix());
	// if (app.has(Feature.FRACTIONS)) {
	// ClickStartHandler.init(lblDefinition, new ClickStartHandler() {
	//
	// @Override
	// public void onClickStart(int x, int y, PointerEventType type) {
	// toggleSymbolic(lblDefinition);
	//
	// }
	// });
	// }
	// updateColor(lblDefinition);
	// return lblDefinition;
	// }

	void toggleSymbolic(Label lbl) {
		if (geo instanceof HasSymbolicMode) {
			((HasSymbolicMode) geo)
					.setSymbolicMode(!((HasSymbolicMode) geo).isSymboicMode());
			geo.updateRepaint();
			lbl.setText(getOutputPrefix());
		}
	}
	private void updateFont(Widget w) {
		w.getElement().getStyle().setFontSize(app.getFontSizeWeb(), Unit.PX);

	}

	private boolean sliderNeeded() {
		return geo instanceof GeoNumeric
				&& ((GeoNumeric) geo).isShowingExtendedAV() && geo.isSimple()
				&& MyDouble.isFinite(((GeoNumeric) geo).value);
	}



	private void initSlider() {

		final GeoNumeric num = (GeoNumeric) geo;

		if (!geo.isEuclidianVisible()) {
			num.initAlgebraSlider();
		}

		// if the geo still has no min/max, it should not be displayed with
		// a slider (e.g. boxplots)
		if (num.getIntervalMinObject() != null
				&& num.getIntervalMaxObject() != null) {
			boolean degree = geo.isGeoAngle()
					&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
			slider = new SliderPanelW(num.getIntervalMin(),
					num.getIntervalMax(), app.getKernel(), degree);
			updateSliderColor();

			slider.setValue(num.getValue());

			slider.setStep(num.getAnimationStep());

			slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					num.setValue(event.getValue());
					geo.updateCascade();


					if (!geo.isAnimating()) {
						if (isAnotherMinMaxOpen()) {
							closeMinMaxPanel();
						}

						selectItem(true);
						updateSelection(false, false);
					}
					// updates other views (e.g. Euclidian)
					kernel.notifyRepaint();
				}
			});


			sliderPanel = new FlowPanel();

			createAnimPanel();
			createMinMaxPanel();

			createContentPanel();
			styleContentPanel(true);

			addAVEXWidget(ihtml);
			contentPanel.add(LayoutUtilW.panelRow(sliderPanel, minMaxPanel));
			main.add(contentPanel);
		}

	}

	private void createContentPanel() {
		if (contentPanel == null) {
			contentPanel = new FlowPanel();
		} else {
			contentPanel.clear();
		}

	}

	private void styleContentPanel(boolean hasSlider) {
		if (hasSlider) {
			contentPanel.removeStyleName("elemPanel");
			contentPanel.addStyleName("avItemContent");
		} else {
			contentPanel.addStyleName("elemPanel");
			contentPanel.removeStyleName("avItemContent");
		}
		if (sliderPanel != null) {
			sliderPanel.setVisible(hasSlider);
		}
		if (animPanel != null) {
			animPanel.setVisible(hasSlider);
		}

	}

	private void deferredResizeSlider() {
		if (slider == null) {
			return;
		}
		Scheduler.get().scheduleDeferred(resizeSliderCmd);
	}

	private void resizeSlider() {
		if (slider == null) {
			return;
		}

		int width = getAV().getOffsetWidth() - 2
 * marblePanel.getOffsetWidth()
				+ SLIDER_EXT;
		slider.setWidth(width < DEFAULT_SLIDER_WIDTH ? DEFAULT_SLIDER_WIDTH
				: width);
	}

	/**
	 * Adds the "X" button to the AV item
	 * 
	 * @param item
	 *            the AV item delete button must be added.
	 */
	public final void addDeleteButton(TreeItem item) {
		addStyleName("XButtonPanelParent");
		main.add(buttonPanel);


	}

	@Override
	public void setFirst(boolean first) {
		super.setFirst(first);
		if (buttonPanel != null) {
			if (first && app.allowStylebar()
					&& !getAlgebraDockPanel().hasLongStyleBar()) {
				buttonPanel.addStyleName("positionedObjectStyleBar");
			} else {
				buttonPanel.removeStyleName("positionedObjectStyleBar");
			}
		}
	}

	// AV EXTENSIONS
	//
	// methods for AV Slider

	private void createAnimPanel() {
		animPanel = geo.isAnimatable() ? new AnimPanel(this) : null;

	}

	private void createMinMaxPanel() {
		minMaxPanel = new MinMaxPanel();
		minMaxPanel.setVisible(false);
	}

	private boolean isMinMaxPanelVisible() {
		return (minMaxPanel != null && minMaxPanel.isVisible());
	}

	private boolean isAnotherMinMaxOpen() {
		return (openedMinMaxPanel != null && openedMinMaxPanel != minMaxPanel);
	}

	private boolean isClickedOutMinMax(int x, int y) {
		return (openedMinMaxPanel == minMaxPanel
				&& !isWidgetHit(minMaxPanel, x, y));
	}

	private void updateSliderColor() {
		if (slider == null) {
			return;
		}
		slider.updateColor(geo.getAlgebraColor());
	}

	// END OF AV Slider methods
	/**
	 * Method to be overridden in InputTreeItem
	 */
	@Override
	public boolean popupSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in InputTreeItem
	 */
	@Override
	public boolean hideSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in InputTreeItem
	 * 
	 * @return
	 */
	@Override
	public void showOrHideSuggestions() {
	}

	/**
	 * Method to be overridden in InputTreeItem
	 */
	@Override
	public void shuffleSuggestions(boolean down) {
		//
	}

	/**
	 * Method to be overridden in InputTreeItem
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
		if (isMinMaxPanelVisible()) {
			return;
		}
		if (commonEditingCheck()) {
			DrawEquationW.triggerKeydown(this, latexItem.getElement(), key,
					alt, ctrl,
					shift);
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
		if (isMinMaxPanelVisible()) {
			return;
		}

		if (commonEditingCheck()) {
			DrawEquationW.triggerKeypress(this, latexItem.getElement(),
					character, alt,
					ctrl, shift, more);
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
		if (isMinMaxPanelVisible()) {
			return;
		}

		if (commonEditingCheck()) {
			DrawEquationW.triggerKeyUp(latexItem.getElement(), key, alt, ctrl,
					shift);
		}
	}

	@Override
	public void handleLongTouch(int x, int y) {
		// if (newCreationMode) {
		// maybe this fixes a bug with not focusing
		// but in this case, focus is called about
		// three times, which might cause problems
		// TouchStart, TouchEnd, long touch so disabled
		// setFocus(true);
		// }
		onRightClick(x, y);
	}

	public void repaint() {
		if (isNeedsUpdate()) {
			doUpdate();
		}
		setSelected(geo.doHighlighting());
		selectItem(geo.doHighlighting());
	}

	public boolean commonEditingCheck() {
		return av.isEditing() || isEditing() || isInputTreeItem();
	}

	private void updateCheckBox() {

		if (((HasExtendedAV) geo).isShowingExtendedAV()) {
			// adds the checkBox at the right side
			addAVEXWidget(ihtml);

			// reset the value of the checkBox
			checkBox.setValue(((GeoBoolean) geo).getBoolean());

			// reset the label text
			geo.getAlgebraDescriptionTextOrHTMLDefault(
					getBuilder(getPlainTextItem()));
			updateFont(getPlainTextItem());
			updateColor(getPlainTextItem());
		} else {
			main.remove(checkBox);
		}

	}

	/**
	 * Updates all the contents of the AV Item
	 */
	protected void doUpdate() {

		if (hasMarblePanel()) {
			marblePanel.update();
		}

		if (hasAnimPanel()) {
			animPanel.update();

		}

		if (isItemChecBox()) {
			updateCheckBox();
		} else if (isItemNumeric()) {
			updateNumerics();
		} else {
			if (!isInputTreeItem() && isDefinitionAndValue()) {
				buildItemContent();
			} else {
				updateTextItems();
			}

		}




	}

	private void updateTextItems() {

		// check for new LaTeX
		setNeedsUpdate(false);
		boolean latexAfterEdit = false;

		if (!isDefinitionAndValue() && outputPanel != null) {
			ihtml.remove(outputPanel);

		}
		if (av.isRenderLaTeX()
				&& (kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE
						|| isDefinitionAndValue())) {
			String text = "";
			if (geo != null) {
				text = getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
				latexAfterEdit = (text != null);
			} else {
				latexAfterEdit = true;
			}

			if (latex && latexAfterEdit) {
				// Both original and edited text is LaTeX
				if (isInputTreeItem()) {
					text = geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplateMQ);
				}
				updateLaTeX(text);

			} else if (latexAfterEdit) {
				// Edited text is latex now, but the original is not.
				renderLatex(text, getPlainTextItem(), isInputTreeItem());
				latex = true;
			}

		} else if (geo == null) {
			return;
		}
		// edited text is plain
		if (!latexAfterEdit) {

			buildPlainTextItem();
			if (!latex) {
				// original text was plain

				updateItemColor();
				updateFont(getPlainTextItem());
			} else {
				// original text was latex.

				updateItemColor();
				if (!isInputTreeItem() && c != null) {
					ihtml.clear();
					ihtml.add(c);
				} else {
					ihtml.clear();
					ihtml.add(getPlainTextItem());
					// ihtml.getElement().replaceChild(getPlainTextItem().getElement(),
					// latexItem.getElement());
				}

				latex = false;
			}
		}

	}

	private void updateItemColor() {
		updateColor(getPlainTextItem());
		if (isDefinitionAndValue() && definitionPanel != null) {
			updateColor(definitionPanel);
		}
	}

	private void updateNumerics() {

		if (slider == null) {
			createContentPanel();

			addAVEXWidget(ihtml);
			initSlider();
			styleContentPanel(true);
			getElement().setDraggable(Element.DRAGGABLE_FALSE);
		} else {
			slider.setScale(app.getArticleElement().getScaleX());
		}

		boolean hasMinMax = false;
		if (((GeoNumeric) geo).getIntervalMaxObject() != null
				&& ((GeoNumeric) geo).getIntervalMinObject() != null) {
			double min = ((GeoNumeric) geo).getIntervalMin();
			double max = ((GeoNumeric) geo).getIntervalMax();
			hasMinMax = MyDouble.isFinite(min) && MyDouble.isFinite(max);
			if (hasMinMax) {
				boolean degree = geo.isGeoAngle()
						&& kernel.getAngleUnit() == Kernel.ANGLE_DEGREE;
				slider.setMinimum(min, degree);
				slider.setMaximum(max, degree);

				slider.setStep(geo.getAnimationStep());
				slider.setValue(((GeoNumeric) geo).value);
				if (minMaxPanel != null) {
					minMaxPanel.update();
				}
			}
		}

		if (hasMinMax && ((HasExtendedAV) geo).isShowingExtendedAV()) {
			if (!slider.isAttached()) {
				sliderPanel.add(slider);
				styleContentPanel(true);
			}

			updateSliderColor();
		} else if (sliderPanel != null) {
			sliderPanel.remove(slider);
			styleContentPanel(false);
		}

	}


	private Canvas c;
	private Canvas valC;

	private void renderLatex(String text0, Widget w, boolean forceMQ) {
		if (definitionAndValue) {
			renderLatexDV(text0, w, forceMQ);

		} else {
			renderLatex(text0, w.getElement(), forceMQ);
		}
	}

	private void renderLatex(String text0, Element old, boolean forceMQ) {
		if (!forceMQ) {
			c = DrawEquationW.paintOnCanvas(geo, text0, c, getFontSize());
			if (c != null && ihtml.getElement().isOrHasChild(old)) {
				ihtml.getElement().replaceChild(c.getCanvasElement(), old);
			}

		} 
 else {
			// // // Log.debug(REFX + "renderLatex 2");
			if (latexItem == null) {
				latexItem = new FlowPanel();
			}
			latexItem.clear();
			latexItem.addStyleName("avTextItem");
			updateColor(latexItem);

			ihtml.clear();





			String text = text0;
			if (text0 == null) {
				text = "";
			}
			text = DrawEquationW.inputLatexCosmetics(text);

			String latexString = "";
			if (!isInputTreeItem()) {
				latexString = isDefinitionAndValue() ? "\\mathrm {"
						: " \\mathbf {" + text + "}";
			}


			ihtml.add(latexItem);
			DrawEquationW.drawEquationAlgebraView(latexItem, latexString,
					isInputTreeItem());


		}

	}

	private void renderLatexDV(String text0, Widget old, boolean forceMQ) {
		if (forceMQ) {
			editLatexMQ(text0);
		} else {
			replaceToCanvas(text0, old);
		}

	}

	private Canvas latexToCanvas(String text) {
		return DrawEquationW.paintOnCanvas(geo, text, c, getFontSize());
	}

	private void updateLaTeX(String text) {
		if (!isDefinitionAndValue()) {
			c = DrawEquationW.paintOnCanvas(geo, text, c, getFontSize());
			return;
		}

		// String eqn = text;
		// boolean twoRows = false;
		// if (geo.needToShowBothRowsInAV()) {
		// buildDefinitionAndValue();
		// eqn = getTextForEditing(false, StringTemplate.latexTemplate);
		// twoRows = true;
		// }
		//
		// c = DrawEquationW.paintOnCanvas(geo, eqn, c, getFontSize());
		//
		// if (twoRows) {
		// valC = DrawEquationW.paintOnCanvas(geo, text, valC, getFontSize());
		// }
	}

	private void replaceToCanvas(String text, Widget old) {
		// Log.debug(DV + "replace to canvas " + text);

		updateLaTeX(text);

		// if (true) {// {
		// if (isDefinitionAndValue() && geo.needToShowBothRowsInAV()) {
		// buildLatexOutput(text);
		// // // Log.debug("ihtml.add(outputPanel) in replaceCanvas");
		// ihtml.add(outputPanel);
		// } else {
		// LayoutUtil.replace(ihtml, c, old);
		// }
		// }
	}

	private void editLatexMQ(String text0) {
		// Log.debug(DV + "editLatexMQ: " + text0);
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		latexItem.clear();
		latexItem.addStyleName("avTextItem");
		updateColor(latexItem);

		ihtml.clear();

		String text = text0;
		if (text0 == null) {
			text = "";
		}
		text = DrawEquationW.inputLatexCosmetics(text);

		String latexString = "";
		if (!isInputTreeItem()) {
			latexString = (isDefinitionAndValue() ? "\\mathrm {"
					: " \\mathbf {") + text + "}";
		}

		if (!isInputTreeItem() && geo.needToShowBothRowsInAV()) {
			createDVPanels();
			updateValuePanel();
			outputPanel.add(valuePanel);
			ihtml.add(latexItem);
			ihtml.add(outputPanel);

			latexItem.addStyleName("avDefinition");

			DrawEquationW.drawEquationAlgebraView(latexItem, latexString,
					isInputTreeItem());

		} else {
			latexItem.removeStyleName("avDefinition");
			ihtml.add(latexItem);
			DrawEquationW.drawEquationAlgebraView(latexItem, latexString,
					isInputTreeItem());
		}
	}



	/**
	 * @return size for JLM texts. Due to different fonts we need a bit more
	 *         than app.getFontSize(), but +3 looked a bit too big
	 */
	private int getFontSize() {
		return app.getFontSizeWeb() + 1;
	}




	private void updateColor(Widget w) {
		if (geo != null) {
			w.getElement().getStyle()
					.setColor(GColor.getColorString(geo.getAlgebraColor()));
		}
	}

	public boolean isEditing() {
		return editing;
	}

	public void cancelEditing() {
		if (isInputTreeItem()) {
			return;
		}
			// if (LaTeX) {
		DrawEquationW.endEditingEquationMathQuillGGB(this, latexItem);
		// if (c != null) {
		// LayoutUtil.replace(ihtml, c, latexItem);
		// // ihtml.getElement().replaceChild(c.getCanvasElement(),
		// // latexItem.getElement());
		// }
		//
		// if (!latex && getPlainTextItem() != null) {
		// LayoutUtil.replace(ihtml, getPlainTextItem(), latexItem);
		// //
		// this.ihtml.getElement().replaceChild(getPlainTextItem().getElement(),
		// // latexItem.getElement());
		// }
		//
		doUpdate();
	}

	/**
	 * Switches to edit mode
	 * 
	 * @param substituteNumbers
	 *            whether value should be used
	 * @return whether it was successful
	 */
	public boolean enterEditMode(boolean substituteNumbers) {
		if (isEditing()) {
			return true;
		}

		updateButtonPanel(true);

		editing = true;
		if (startEditing(substituteNumbers) == false) {
			return false;
		}

		buttonPanel.setVisible(true);
		maybeSetPButtonVisibility(true);
		return true;
	}

	private String getTextForEditing(boolean substituteNumbers,
			StringTemplate tpl) {
		return geo.isMatrix() && geo.isIndependent()
				? geo.toEditableLaTeXString(substituteNumbers,
						tpl)
				: geo.getLaTeXAlgebraDescriptionWithFallback(
						substituteNumbers || sliderNeeded(),
						tpl, true);

	}

	/**
	 * Starts the equation editor for the item.
	 * 
	 * @param substituteNumbers
	 *            Sets that variables must be substituted or not
	 * @return
	 */
	protected boolean startEditing(boolean substituteNumbers) {
		String text = getTextForEditing(substituteNumbers,
				StringTemplate.latexTemplateMQedit);
		if (text == null) {
			return false;
		}

		if (isDefinitionAndValue()) {
			Widget old = latex ? (c != null ? c : latexItem)
					: getPlainTextItem();

			renderLatex(text, old, true);

		} else {
			Element old = latex
					? (c != null ? c.getCanvasElement()
							: latexItem.getElement())
					: getPlainTextItem().getElement();
			renderLatex(text, old, true);
		}

		DrawEquationW.editEquationMathQuillGGB(this, latexItem, false);

		app.getGuiManager().setOnScreenKeyboardTextField(this);
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(main, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				app.getGuiManager()
						.setOnScreenKeyboardTextField(RadioTreeItem.this);
				// prevent that keyboard is closed on clicks (changing
				// cursor position)
				CancelEventTimer.keyboardSetVisible();
			}
		});

		return true;
	}

	private boolean isDefinitionAndValue() {
		return definitionAndValue && kernel
				.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE;
	}



	@Override
	public boolean stopEditing(String newValue0) {

		if (app.has(Feature.EXPAND_AV_FOR_LONG_EQUATIONS)) {
			restoreSize();
			if (stylebarShown != null) {
				getAlgebraDockPanel().showStyleBarPanel(stylebarShown);
				stylebarShown = null;
			}
		}

		boolean ret = false;

		removeCloseButton();

		editing = false;
		av.cancelEditing();

		if (newValue0 != null) {
			String newValue = EquationEditor.stopCommon(newValue0);
			// // not sure why it is needed... TODO: is this needed?
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
		if (!this.isInputTreeItem() && c != null
				&& ihtml.getElement().isOrHasChild(latexItem.getElement())) {
			this.ihtml.getElement().replaceChild(c.getCanvasElement(),
					latexItem.getElement());
		}
		if (!latex && !this.isInputTreeItem() && getPlainTextItem() != null
				&& ihtml.getElement().isOrHasChild(latexItem.getElement())) {
			this.ihtml.getElement().replaceChild(getPlainTextItem().getElement(),
					latexItem.getElement());
		}
		// maybe it's possible to enter something which is non-LaTeX
		// if (ret)
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

		// TODO: move to InputTreeItem? Wouldn't help much...

		String newValue = newValue0;

		if (newValue0 != null) {
			newValue = EquationEditor.stopCommon(newValue);
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
			AsyncOperation<GeoElement[]> callback = new AsyncOperation<GeoElement[]>() {

				@Override
				public void callback(GeoElement[] geos) {

					if (geos == null) {
						// inputField.getTextBox().setFocus(true);
						setFocus(true);
						return;
					}

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
							if (isInputTreeItem()) {
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
				if (isInputTreeItem()) {
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

		if ((isWidgetHit(animPanel, evt)
						|| (minMaxPanel != null && minMaxPanel.isVisible()) || isWidgetHit(
marblePanel, evt))) {
			return;
		}

		// if (isWidgetHit(sliderPanel, evt.getClientX(), evt.getClientY())) {
		// minMaxPanel.show();
		// return;
		// }

		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if (commonEditingCheck())
			return;

		onDoubleClickAction(evt.isControlKeyDown());
	}

	private void onDoubleClickAction(boolean ctrl) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selectionCtrl.clear();
		ev.resetMode();
		if (geo != null && !ctrl) {
			if (!isEditing()) {
				geo.setAnimating(false);
				av.startEditing(geo);
				if (app.has(Feature.EXPAND_AV_FOR_LONG_EQUATIONS)) {
					Scheduler.get().scheduleDeferred(
							new Scheduler.ScheduledCommand() {

								public void execute() {
									// TODO: change 40 for left padding of
									// contentPanel
									expandSize(Math.max(EDIT_WIDTH,
											ihtml.getOffsetWidth()) + 40);
									if ((RadioTreeItem.this.first)
											&& (!getAlgebraDockPanel()
													.isStyleBarVisible())) {
										stylebarShown = getAlgebraDockPanel()
												.isStyleBarPanelShown();
										getAlgebraDockPanel()
												.showStyleBarPanel(false);
										if (buttonPanel != null) {
											buttonPanel
													.removeStyleName("positionedObjectStyleBar");
										}
									}

									if (animPanel != null
											&& buttonPanel != null) {
										buttonPanel.remove(animPanel);
									}
								}

							});

				}
			}
			app.showKeyboard(this);
			this.setFocus(true);
		}
	}

	private static boolean isWidgetHit(Widget w, MouseEvent<?> evt) {
		return isWidgetHit(w, evt.getClientX(), evt.getClientY());

	}

	private static boolean isWidgetHit(Widget w, int x, int y) {
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
	public void onMouseOver(MouseOverEvent event) {
		if (geo == null || (isGeoASlider())) {
			return;
		}

		ToolTipManagerW.sharedInstance()
				.showToolTip(geo.getLongDescriptionHTML(true, true));

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		ToolTipManagerW.sharedInstance().showToolTip(null);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		handleAVItem(event);
		event.stopPropagation();
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
		SliderWJquery.stopSliders();
		event.stopPropagation();
	}


	public void update() {
		// marblePanel.setBackground();
	}



	@Override
	public void onClick(ClickEvent evt) {
		evt.stopPropagation();
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
				ZeroOffset.instance);
		onPointerUp(wrappedEvent);
	}

	private void handleAVItem(MouseEvent<?> evt) {
		handleAVItem(evt.getClientX(), evt.getClientY(),
				evt.getNativeButton() == NativeEvent.BUTTON_RIGHT);
	}

	private void handleAVItem(TouchStartEvent evt) {
		if (evt.getTouches().length() == 0) {
			return;
		}

		Touch t = evt.getTouches().get(0);
		if (handleAVItem(t.getClientX(), t.getClientY(), false)) {
			evt.preventDefault();
		}
	}

	private boolean handleAVItem(int x, int y, boolean rightClick) {

		boolean minHit = sliderPanel != null
				&& isWidgetHit(slider.getWidget(0), x, y);
		boolean maxHit = sliderPanel != null
				&& isWidgetHit(slider.getWidget(2), x, y);
		// Min max panel should be closed
		if (isAnotherMinMaxOpen() || isClickedOutMinMax(x, y)) {
			closeMinMaxPanel(!(minHit || maxHit));
		}

		if (isAnotherMinMaxOpen()) {
			selectItem(false);

		}

		if (minMaxPanel != null && minMaxPanel.isVisible()) {
			selectItem(true);
			return false;
		}

		if (sliderPanel != null && sliderPanel.isVisible() && !rightClick) {

			if (minHit || maxHit) {
				minMaxPanel.show();
				if (minHit) {
					minMaxPanel.setMinFocus();
				} else if (maxHit) {
					minMaxPanel.setMaxFocus();
				}

				return true;
			}
		}

		if (!selectionCtrl.isSelectHandled()) {
			selectItem(true);
		}

		return false;

	}
	protected AlgebraViewW getAV() {
		return (AlgebraViewW) av;
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt) {
		if (sliderPanel == null) {
			evt.stopPropagation();
			return;
		}
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
				ZeroOffset.instance);
		onPointerMove(wrappedEvent);
	}

	@Override
	public GeoElement getGeo() {
		return geo;
	}

	public long latestTouchEndTime = 0;
	private Style border;
	private boolean selectedItem = false;
	private AVSelectionController selectionCtrl;


	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (sliderPanel != null) {
			return;
		}
		event.stopPropagation();
		// event.preventDefault();
		if (isInputTreeItem()) {
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
		getLongTouchManager().cancelTimer();
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(event,
				ZeroOffset.instance);
		onPointerUp(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.stopPropagation();
		if (sliderPanel != null) {
			return;
		}
		// event.preventDefault();
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		getLongTouchManager().rescheduleTimerIfRunning(this, x, y);
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
		handleAVItem(event);
		// this would propagate the event to
		// AlgebraView.onBrowserEvent... is this we want?
		// probably no, as there is a stopPropagation
		// in the onMouseDown method as well...
		event.stopPropagation();
		// Do NOT prevent default, kills scrolling on touch
		// event.preventDefault();
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		getLongTouchManager().scheduleTimer(this, x, y);
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(event,
				ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	private void onPointerDown(AbstractEvent event) {

		if (event.isRightClick()) {
			onRightClick(event.getX(), event.getY());
			return;
		} else if (commonEditingCheck()) {
			if (!av.isEditing()) {
				// e.g. Web.html might not be in editing mode
				// initially (temporary fix)
				ensureEditing();
			}
			app.showKeyboard(this);
			PointerEvent pointerEvent = (PointerEvent) event;
			pointerEvent.getWrappedEvent().stopPropagation();
			if (isInputTreeItem()) {
				// put earlier, maybe it freezes afterwards?
				setFocus(true);
			}

		}
		updateSelection(event.isControlDown(), event.isShiftDown());

	}


	private void onPointerUp(AbstractEvent event) {
		selectionCtrl.setSelectHandled(false);
		if (isMinMaxPanelVisible()) {
			return;
		}
		if (commonEditingCheck()) {
			if (isInputTreeItem()) {
				AlgebraStyleBarW styleBar = getAV().getStyleBar(false);
				if (styleBar != null) {
					styleBar.update(null);
				}
			}
			return;
		}

		// Alt click: copy definition to input field
		if (geo != null && event.isAltDown() && app.showAlgebraInput()) {
			// F3 key: copy definition to input bar
			if (!commonEditingCheck()) {
				onDoubleClickAction(event.isControlDown());
				return;
			}
		}
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		int mode = ev.getMode();
		if (mode != EuclidianConstants.MODE_MOVE
				&& mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			ev.clickedGeo(geo, app.isControlDown(event));
		}
		ev.mouseMovedOver(null);

		// previously av.setFocus, but that scrolls AV and seems not to be
		// necessary
		getElement().focus();

		AlgebraStyleBarW styleBar = getAV().getStyleBar(false);

		if (styleBar != null) {
			styleBar.update(this.getGeo());
		}

	}

	private void updateSelection(boolean separated, boolean continous) {
		if (geo == null) {
			selectionCtrl.clear();
			getAV().updateSelection();
		} else {
			selectionCtrl.select(geo, separated, continous);
			if (separated && !selectionCtrl.contains(geo)) {
				selectionCtrl.setSelectHandled(true);
				getAV().selectRow(geo, false);
			} else if (continous) {
				getAV().updateSelection();
			}

		}
	}

	/**
	 * This method shall only be called when we are not doing editing, so this
	 * is for the delete button at selection
	 */
	private void updateButtonPanel(boolean showX) {
		setFirst(first);

		if (geo == null) {
			return;
		}

		if (selectionCtrl.isSingleGeo() || selectionCtrl.isEmpty()) {
			setFirst(first);
			buttonPanel.clear();
			if (animPanel != null && geo.isAnimatable()) {
				buttonPanel.add(animPanel);
			}
			if (getPButton() != null) {
				buttonPanel.add(getPButton());
			}
			if (showX) {
				buttonPanel.add(getDeleteButton());
			}
			buttonPanel.setVisible(true);

			if (!isEditing()) {
				maybeSetPButtonVisibility(false);
			}

			getAV().setActiveTreeItem(this);

		} else {
			getAV().removeCloseButton();
		}
	}

	protected PushButton getPButton() {
		return null;
	}

	protected void maybeSetPButtonVisibility(boolean bool) {
		// only show the delete button, but not the extras
	}

	private void onPointerMove(AbstractEvent event) {
		/*
		 * // tell EuclidianView to handle mouse over
		 * EuclidianViewInterfaceCommon ev = kernel.getApplication()
		 * .getActiveEuclidianView(); if (geo != null) { ev.mouseMovedOver(geo);
		 * }
		 */
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
		DrawEquationW.focusEquationMathQuillGGB(latexItem, b);
	}

	@Override
	public void insertString(String text) {
		// even worse
		// for (int i = 0; i < text.length(); i++)
		// geogebra.html5.main.DrawEquationWeb.writeLatexInPlaceOfCurrentWord(
		// seMayLatex, "" + text.charAt(i), "", false);

		DrawEquationW.writeLatexInPlaceOfCurrentWord(this,
				latexItem.getElement(), text,
				"", false);
	}

	@Override
	public String getText() {
		if (latexItem == null)
			return "";

		String ret = DrawEquationW.getActualEditedValue(latexItem.getElement(),
				false);

		if (ret == null)
			return "";

		return ret;
	}

	@Override
	public void scrollCursorIntoView() {
		if (latexItem != null) {
			DrawEquationW.scrollCursorIntoView(this, latexItem.getElement(),
					isInputTreeItem());
		}
	}

	@Override
	public void scrollIntoView() {
		this.getElement().scrollIntoView();
	}

	@Override
	public void typing(boolean heuristic) {
		// to be overridden in InputTreeItem,
		// to know whether it's empty, whether to show Xbutton
	}

	public void removeCloseButton() {
		this.maybeSetPButtonVisibility(true);
		buttonPanel.setVisible(false);
	}


	void addAVEXWidget(Widget w) {
		if (geo != null && geo instanceof GeoNumeric && slider != null
				&& sliderPanel != null) {
			sliderPanel.remove(slider);
			contentPanel.add(w);
			if (hasGeoExtendedAV()) {
				sliderPanel.add(slider);
			}
		} else if (checkBox != null) {
			main.remove(checkBox);
			if (hasGeoExtendedAV()) {
				main.add(checkBox);
			}
			main.add(w);
		} else {
			main.add(w);
		}
	}

	private boolean hasGeoExtendedAV() {
		return (geo instanceof HasExtendedAV && ((HasExtendedAV) geo)
				.isShowingExtendedAV());
	}

	public void setDraggable() {
		Widget draggableContent = main;
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
				getAV().dragStart(event, geo);
			}
		}, DragStartEvent.getType());

	}

	@Override
	public App getApplication() {
		return app;
	}

	@Override
	public void ensureEditing() {
		if (!isEditing()) {
			enterEditMode(true);

			if (av != null && ((AlgebraViewW) av).isNodeTableEmpty()) {
				updateGUIfocus(this, false);
			}

		}
	}

	protected void updateGUIfocus(Object source, boolean blurtrue) {

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
		return main;
	}

	@Override
	public final Element getLaTeXElement() {
		return latexItem.getElement();
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
		// to be overridden in InputTreeItem
	}

	@Override
	public void onFocus(FocusEvent be) {
		// to be overridden in InputTreeItem
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

	@Override
	public void onResize() {

		deferredResizeSlider();
		if (first && isSelected()) {
			updateButtonPanel(true);
		}

	}



	public static void closeMinMaxPanel() {
		closeMinMaxPanel(true);
	}

	public static void closeMinMaxPanel(boolean restore) {
		if (openedMinMaxPanel == null) {
			return;
		}

		openedMinMaxPanel.hide(restore);
		openedMinMaxPanel = null;

	}

	public static void setOpenedMinMaxPanel(MinMaxPanel panel) {
		openedMinMaxPanel = panel;
	}

	public boolean isItemSelected() {
		return selectedItem;
	}

	public void selectItem(boolean selected) {
		if (selectedItem == selected) {
			return;
		}

		selectedItem = selected;

		if (selected) {
			addStyleName("avSelectedRow");
			// border.setBorderColor(
			// GColor.getColorString(geo.getAlgebraColor()));

			updateButtonPanel(true);

		} else {
			// border.setBorderColor(CLEAR_COLOR_STR);
			removeStyleName("avSelectedRow");
		}
		if (marblePanel != null) {
			marblePanel.setHighlighted(selected);
		}
		if (selected == false
				// && geo != AVSelectionController.get(app).getLastSelectedGeo()
				&& animPanel != null) {
			animPanel.reset();
		}
	}


	public UIObject asWidget() {
		return main.asWidget();
	}

	/**
	 * cast method with no 'instanceof' check.
	 * 
	 * @param item
	 *            TreeItem to be casted
	 * @return Casted item to RadioTreeItem
	 */
	public static RadioTreeItem as(TreeItem item) {
		return (RadioTreeItem) item;
	}

	private boolean isGeoASlider() {
		return slider != null;
	}

	public Element getScrollElement() {
		return getWidget().getElement();
	}

	protected AlgebraDockPanelW getAlgebraDockPanel() {
		return (AlgebraDockPanelW) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);

	}

	public void onKeyPress(String s) {
		// TODO Auto-generated method stub

	}

	public void autocomplete(String s) {

	}

	public FlowPanel getPlainTextItem() {
		return plainTextItem;
	}

	public void setPlainTextItem(FlowPanel plainTextItem) {
		this.plainTextItem = plainTextItem;
	}

	public boolean isNeedsUpdate() {
		return needsUpdate;
	}

	public void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}

	public LongTouchManager getLongTouchManager() {
		return longTouchManager;
	}

	public void setLongTouchManager(LongTouchManager longTouchManager) {
		this.longTouchManager = longTouchManager;
	}

	/**
	 * 
	 * @return if the item is the input or not.
	 */
	public boolean isInputTreeItem() {
		return false;
	}

	private boolean hasAnimPanel() {
		return animPanel != null;
	}

	private boolean hasMarblePanel() {
		return marblePanel != null;
	}

	private boolean isItemNumeric() {
		return (geo instanceof GeoNumeric
				&& (slider != null && sliderPanel != null) || sliderNeeded());
	}

	private boolean isItemChecBox() {
		return checkBox != null;
	}

	private String getOutputPrefix() {
		if (geo instanceof HasSymbolicMode
				&& !((HasSymbolicMode) geo).isSymboicMode()) {
			return Unicode.CAS_OUTPUT_NUMERIC;
		}
		if (kernel.getLocalization().rightToLeftReadingOrder) {
			return Unicode.CAS_OUTPUT_PREFIX_RTL;
		}
		return Unicode.CAS_OUTPUT_PREFIX;
	}
}

