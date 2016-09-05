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
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.HasExtendedAV;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GColorW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.sliderPanel.SliderWJquery;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputbar.HasHelpButton;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPopup;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.util.MyToggleButton2;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * main -> marblePanel content controls
 * 
 * content -> plainTextitem | latexItem | c | (definitionPanel outputPanel)
 * 
 * sliderContent -> [sliderPanel minmaxPanel]
 * 
 * plaintextitem -> STRING | (definitionPanel, outputPanel)
 * 
 * outputPanel -> valuePanel
 * 
 * definitionPanel -> c | STRING
 */
@SuppressWarnings("javadoc")
public abstract class RadioTreeItem extends AVTreeItem
		implements DoubleClickHandler, ClickHandler, MouseDownHandler,
		MouseUpHandler, MouseMoveHandler, MouseOverHandler, MouseOutHandler,
		MathKeyboardListener, TouchStartHandler, TouchMoveHandler,
		TouchEndHandler,
		LongTouchHandler,
		AutoCompleteW, RequiresResize, HasHelpButton {

	private static final int BROWSER_SCROLLBAR_WIDTH = 17;

	private static final int LATEX_MAX_EDIT_LENGHT = 1500;

	static final String CLEAR_COLOR_STR = GColor
			.getColorString(new GColorW(255, 255, 255, 0));
	static final String CLEAR_COLOR_STR_BORDER = GColor
			.getColorString(new GColorW(220, 220, 220));
	Boolean stylebarShown;
	/** Help button */
	ToggleButton btnHelpToggle;
	/** Help popup */
	protected InputBarHelpPopup helpPopup;
	/** label "Input..." */
	protected Label dummyLabel;

	/**
	 * The main widget of the tree item containing all others
	 */
	protected FlowPanel main;

	/** Item content after marble */
	protected FlowPanel content;

	/** Item controls like delete, play, etc */
	protected ItemControls controls;


	public class MarblePanel extends FlowPanel {
		private Marble marble;
		private boolean selected = false;

		public MarblePanel() {

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
		}

		public void update() {
			if (marble != null) {
				marble.setChecked(geo.isEuclidianVisible());
			}
			setHighlighted(selected);
		}

		public boolean isHit(int x, int y) {
			return x > getAbsoluteLeft()
					&& x < getAbsoluteLeft() + getOffsetWidth()
					&& y < getAbsoluteTop() + getOffsetHeight();
		}
	}

	public class ItemControls extends FlowPanel {
		/** Deletes the whole item */
		protected PushButton btnDelete;


		/** animation controls */
		protected AnimPanel animPanel;

		public ItemControls() {
			addStyleName("AlgebraViewObjectStylebar");
			addStyleName("smallStylebar");
			setVisible(false);
		}

		/**
		 * Gets (and creates if there is not yet) the delete button which geo
		 * item can be removed with from AV.
		 * 
		 * @return The "X" button.
		 */
		public PushButton getDeleteButton() {
			if (btnDelete == null) {
				btnDelete = new PushButton(
						new Image(GuiResources.INSTANCE.algebra_delete()));
				btnDelete.getUpHoveringFace().setImage(new Image(
						GuiResources.INSTANCE.algebra_delete_hover()));
				btnDelete.addStyleName("XButton");
				btnDelete.addStyleName("shown");
				btnDelete.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						if (event
								.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
							return;
						}
						event.stopPropagation();
						geo.remove();
					}
				});
			}
			return btnDelete;

		}

		public void showAnimPanel(boolean value) {
			if (hasAnimPanel()) {
				animPanel.setVisible(value);
			}
		}

		public void showAnimPanel() {
			showAnimPanel(true);
		}

		public void hideAnimPanel() {
			showAnimPanel(false);
		}

		protected void createAnimPanel() {
			animPanel = geo.isAnimatable() ? new AnimPanel(RadioTreeItem.this)
					: null;

		}

		public void updateAnimPanel() {
			if (hasAnimPanel()) {
				animPanel.update();
			}
		}

		public AnimPanel getAnimPanel() {
			return animPanel;
		}

		public boolean update(boolean showX) {
			setFirst(first);

			if (geo == null) {
				return false;
			}
			boolean ret = false;
			if (selectionCtrl.isSingleGeo() || selectionCtrl.isEmpty()) {
				setFirst(first);
				clear();
				if (geo.isAnimatable()) {
					if (animPanel == null) {
						createAnimPanel();
					}

					add(animPanel);
				}

				if (getPButton() != null) {
					add(getPButton());
				}
				if (showX) {
					add(getDeleteButton());
				}

				setVisible(true);

				if (!isEditing()) {
					maybeSetPButtonVisibility(false);
				}

				getAV().setActiveTreeItem(RadioTreeItem.this);
				ret = true;
			} else {
				getAV().removeCloseButton();
			}

			updateAnimPanel();
			return ret;
		}

		public void removeAnimPanel() {
			if (hasAnimPanel()) {
				remove(animPanel);
			}
		}

		public void reset() {
			Log.debug("[CONTROLS] reset");
			if (hasAnimPanel()) {
				animPanel.reset();
			}
		}

		public boolean hasAnimPanel() {
			return animPanel != null;
		}
	}

	protected GeoElement geo;
	protected Kernel kernel;
	protected AppW app;
	protected AlgebraView av;
	protected boolean latex = false;
	protected boolean editing = false;

	public FlowPanel latexItem;
	private FlowPanel plainTextItem;

	GTextBox tb;
	private boolean needsUpdate;
	protected Label errorLabel;

	/** Clears input only when editing */
	protected PushButton btnClearInput;

	private LongTouchManager longTouchManager;

	/**
	 * this panel contains the marble (radio) button
	 */
	protected MarblePanel marblePanel;

	protected FlowPanel sliderContent;



	protected boolean definitionAndValue;

	protected FlowPanel valuePanel;

	protected FlowPanel definitionPanel;

	protected FlowPanel outputPanel;


	public void updateOnNextRepaint() {
		needsUpdate = true;
	}

	protected IndexHTMLBuilder getBuilder(final Widget w) {
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
	 * Minimal constructor
	 *
	 * @param kernel
	 */
	public RadioTreeItem(Kernel kernel) {
		super();
		this.kernel = kernel;
		app = (AppW) kernel.getApplication();
		av = app.getAlgebraView();
		definitionAndValue = app.has(Feature.AV_DEFINITION_AND_VALUE);

		main = new FlowPanel();
		content = new FlowPanel();
		plainTextItem = new FlowPanel();
		setWidget(main);

		selectionCtrl = getAV().getSelectionCtrl();
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

		addMarble();

		if (geo.isSimple()) {
			getPlainTextItem().addStyleName("avDefinitionSimple");
		}

		getPlainTextItem().addStyleName("sqrtFontFix");
		getPlainTextItem().addStyleName("avPlainTextItem");

		updateColor(getPlainTextItem());
		updateFont(getPlainTextItem());

		styleContent();
		
		addDomHandlers(main);

		addControls();

		content.add(getPlainTextItem());
		buildPlainTextItem();
		// if enabled, render with LaTeX

		if (getLatexString(true, null) != null) {
			setNeedsUpdate(true);
			av.repaintView();

		}
		createAvexWidget();
		addAVEXWidget(content);

	}

	protected void addMarble() {
		main.addStyleName("elem");
		main.addStyleName("panelRow");

		marblePanel = new MarblePanel();
		main.add(marblePanel);

	}

	protected void styleContent() {
		content.addStyleName("elemText");


	}

	protected void createControls() {
		controls = new ItemControls();
	}

	protected void addControls() {
		createControls();
		main.add(controls);
	}

	/**
	 *
	 */
	protected void createAvexWidget() {
		// only for checkboxes
	}

	private String getLatexString(boolean MathQuill, Integer limit) {
		return getLatexString(geo, MathQuill, limit);
	}

	private String getLatexString(GeoElement geo, boolean MathQuill,
			Integer limit) {
		if ((kernel.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_VALUE
						&& !isDefinitionAndValue())
				|| !geo.isDefined() || !geo.isLaTeXDrawableGeo()) {
			return null;
		}

		String text = geo.getLaTeXAlgebraDescription(true,
				MathQuill ? StringTemplate.latexTemplateMQ
						: StringTemplate.latexTemplate);

		if ((text != null) && (limit == null || (text.length() < limit))) {
			return text;
		}

		return null;
	}



	protected abstract void clearInput();

	private void buildPlainTextItem() {
		if (geo.isIndependent() && geo.getDefinition() == null) {
			geo.getAlgebraDescriptionTextOrHTMLDefault(
					getBuilder(getPlainTextItem()));
		} else {
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

	protected void createDVPanels() {
		if (definitionPanel == null) {
			definitionPanel = new FlowPanel();
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

	private boolean isGeoFraction() {
		return geo instanceof GeoNumeric && geo.getDefinition() != null
				&& geo.getDefinition().isFraction();
	}

	private boolean isLatexTrivial() {
		if (!latex) {
			return false;
		}

		String text = getTextForEditing(false, StringTemplate.latexTemplate);
		String[] eq = text.split("=");

		String leftSide = eq[0].trim();
		String rightSide = eq[1].replaceFirst("\\\\left", "")
				.replaceFirst("\\\\right", "").replaceAll(" ", "");

		return leftSide.equals(rightSide);
	}
	private boolean updateDefinitionPanel() {
		if (latex || isGeoFraction()) {
			String text = getTextForEditing(false,
					StringTemplate.latexTemplate);
			definitionPanel.clear();

			canvas = latexToCanvas(text);
			canvas.addStyleName("canvasDef");
			if (geo == null) {
				Log.debug("CANVAS to DEF");
			}
			definitionPanel.add(canvas);
		} else if (geo != null) {

			IndexHTMLBuilder sb = getBuilder(definitionPanel);
			geo.addLabelTextOrHTML(
					geo.getDefinition(StringTemplate.defaultTemplate), sb);

		}
		return true;
	}

	private boolean isSymbolicDiffers() {
		if (!(geo instanceof HasSymbolicMode)) {
			return false;
		}

		HasSymbolicMode sm = (HasSymbolicMode) geo;
		boolean orig = sm.isSymbolicMode();
		String text1 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);
		sm.setSymbolicMode(!orig, false);
		String text2 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);

		sm.setSymbolicMode(orig, false);
		if (text1 == null) {
			return true;
		}

		return !text1.equals(text2);

	}

	protected boolean updateValuePanel() {
		String text = getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
		boolean fraction = isGeoFraction() && isSymbolicGeo();
		latex = text != null || fraction;
		return updateValuePanel(fraction
 ? geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplateMQ) : text);
	}

	private void addPrefixLabel(String text) {
		final Label label = new Label(text);
		if (!latex) {
			label.addStyleName("prefix");
		} else {
			label.addStyleName("prefixLatex");
		}
		outputPanel.add(label);
	}

	private boolean updateValuePanel(String text) {
		return updateValuePanel(geo, text);
	}

	private boolean updateValuePanel(GeoElement geo1, String text) {
		if (geo1 == null || !geo1.needToShowBothRowsInAV()) {
			return false;
		}

		outputPanel.clear();
		if (app.has(Feature.FRACTIONS)) {
			if (isSymbolicDiffers()) {
				final MyToggleButton2 btnSymbolic = new MyToggleButton2(
						GuiResourcesSimple.INSTANCE.modeToggleSymbolic(),
						GuiResourcesSimple.INSTANCE.modeToggleNumeric());
				btnSymbolic.addStyleName("symbolicButton");
				if (getOutputPrefix() == Unicode.CAS_OUTPUT_NUMERIC) {
					btnSymbolic.setSelected(true);
				}
				if (getOutputPrefix() == Unicode.CAS_OUTPUT_PREFIX) {
					btnSymbolic.setSelected(false);
					btnSymbolic.addStyleName("btn-prefix");
				}
			btnSymbolic.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
						toggleSymbolic(btnSymbolic);
				}
			});
			outputPanel.add(btnSymbolic);
			} else {
				addPrefixLabel(kernel.getLocalization().rightToLeftReadingOrder
						? Unicode.CAS_OUTPUT_PREFIX_RTL
						: Unicode.CAS_OUTPUT_PREFIX);
			}

		} else {
			addPrefixLabel(getOutputPrefix());
		}

		valuePanel.clear();
		IndexHTMLBuilder sb = new IndexHTMLBuilder(false);
		geo1.getAlgebraDescriptionTextOrHTMLDefault(sb);
		valuePanel.add(new HTML(sb.toString()));
		if (latex) {
			valCanvas = DrawEquationW.paintOnCanvas(geo1, text, valCanvas,
					getFontSize());
			valCanvas.addStyleName("canvasVal");
			if (valCanvas != null) {
				valuePanel.clear();
				valuePanel.add(valCanvas);
			}
		}

		return true;
	}

	private boolean isSymbolicGeo() {
		return (geo instanceof HasSymbolicMode
				&& ((HasSymbolicMode) geo).isSymbolicMode());
	}

	private void buildItemContent() {
		if (isDefinitionAndValue()) {
			if (isEditing() || geo == null) {
				return;
			}

			if (geo.needToShowBothRowsInAV() && !isLatexTrivial()) {
				buildItemWithTwoRows();
				updateItemColor();
			} else {
				buildItemWithSingleRow();
			}
		} else {
			buildPlainTextItem();
		}

		adjustToPanel(content);

	}


	private void buildItemWithTwoRows() {
		createDVPanels();
		String text = isGeoFraction()
				? getTextForEditing(false, StringTemplate.latexTemplate)
				: getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
		latex = text != null;
		if (latex) {
			definitionPanel.addStyleName("avDefinition");
		} else {
			definitionPanel.addStyleName("avDefinitionPlain");
		}

		content.clear();
		if (updateDefinitionPanel()) {
			plainTextItem.clear();
			plainTextItem.add(definitionPanel);
		}

		if (updateValuePanel(geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate))) {
			outputPanel.add(valuePanel);
			;
			plainTextItem.add(outputPanel);
		}

		// updateFont(plainTextItem);
		content.add(plainTextItem);
	}

	public void clearPreview() {
		if (!app.has(Feature.AV_PREVIEW)) {
			return;
		}

		if (valuePanel == null) {
			return;
		}
		valuePanel.clear();
	}

	public void previewValue(GeoElement previewGeo) {
		if (!app.has(Feature.AV_PREVIEW)) {
			return;
		}
		String text = "";
		if (previewGeo.isGeoFunction()) {
			Log.debug("FUNCTIOOOOOOOOOOOOOON");
			text += previewGeo.getLabel(StringTemplate.defaultTemplate);
		}
		createDVPanels();
		content.addStyleName("avPreview");
		plainTextItem.clear();
		plainTextItem.add(outputPanel);
		outputPanel.clear();

		valuePanel.clear();
		IndexHTMLBuilder sb = new IndexHTMLBuilder(false);
		previewGeo.getAlgebraDescriptionTextOrHTMLDefault(sb);
		String plain = sb.toString();
		text = previewGeo
				.getAlgebraDescription(StringTemplate.latexTemplate);
		Log.debug("PREV-- " + text);
		if (!plain.equals(text)) {
			// LaTeX
			valCanvas = DrawEquationW.paintOnCanvas(previewGeo, text, valCanvas,
					getFontSize());
			valCanvas.addStyleName("canvasVal");
			if (valCanvas != null) {
				valuePanel.clear();
				valuePanel.add(valCanvas);
			}
		} else {
			valuePanel.add(new HTML(plain.replace("undefined", "")));

		}
		if (outputPanel.getWidgetIndex(valuePanel) == -1) {
			outputPanel.add(valuePanel);
		}

		if (content.getWidgetIndex(plainTextItem) == -1) {
			content.add(plainTextItem);
		}

		// if (previewGeo == null) {
		// return;
		// }
		Log.debug("previewValue " + previewGeo);
		
		
	}

	protected void buildItemWithSingleRow() {

		// LaTeX
		String text = getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
		latex = text != null;


		if (latex) {
			if (isInputTreeItem()) {
				text = geo.getLaTeXAlgebraDescription(true,
						StringTemplate.latexTemplateMQ);
			}

			canvas = DrawEquationW.paintOnCanvas(geo, text, canvas,
					getFontSize());
			content.clear();
			if (geo == null) {
				Log.debug("CANVAS to IHTML");
			}
			content.add(canvas);
		}

		else {
			geo.getAlgebraDescriptionTextOrHTMLDefault(
						getBuilder(getPlainTextItem()));
			updateItemColor();
			// updateFont(getPlainTextItem());
			content.clear();
			content.add(getPlainTextItem());
			adjustToPanel(plainTextItem);
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

	/**
	 * 
	 * @param button
	 * @return true if output is numeric, false otherwise
	 */
	void toggleSymbolic(MyToggleButton2 button) {
		if (geo instanceof HasSymbolicMode) {
			((HasSymbolicMode) geo)
					.setSymbolicMode(!((HasSymbolicMode) geo).isSymbolicMode(),
							true);

			if (getOutputPrefix() == Unicode.CAS_OUTPUT_NUMERIC) {
				button.setSelected(true);
			} else {
				button.setSelected(false);
			}
			geo.updateRepaint();
		}

	}
	private void updateFont(Widget w) {
		w.getElement().getStyle().setFontSize(app.getFontSizeWeb(), Unit.PX);

	}


	protected void createSliderContent() {
		if (sliderContent == null) {
			sliderContent = new FlowPanel();
		} else {
			sliderContent.clear();
		}

	}

	protected void styleContentPanel() {
		sliderContent.addStyleName("elemPanel");
		sliderContent.removeStyleName("avItemContent");
		controls.updateAnimPanel();

	}

	protected boolean first = false;
	public void setFirst(boolean first) {
		this.first = first;
		updateButtonPanelPosition();
	}

	/**
	 * Method to be overridden in InputTreeItem
	 */
	public boolean popupSuggestions() {
		return false;
	}



	/**
	 * Method to be overridden in InputTreeItem
	 * 
	 * @param str
	 *            GeoGebra input
	 * @param latexx
	 *            LaTeX value
	 */
	public void addToHistory(String str, String latexx) {
		// override
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
		// highlight only
		setSelected(geo.doHighlighting());

		// select only if it is in selection really.
		selectItem(geo.doHighlighting());
	}

	public boolean commonEditingCheck() {
		return av.isEditing() || isEditing() || isInputTreeItem()
				|| geo == null;
	}



	/**
	 * Updates all the contents of the AV Item
	 */
	protected void doUpdate() {
		setNeedsUpdate(false);
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		if (controls != null) {
			controls.updateAnimPanel();
		}

		if (!isInputTreeItem() && isDefinitionAndValue()) {
			if (geo == null) {
				Log.debug(Feature.RETEX_EDITOR, "Build item");
			}
			buildItemContent();
		} else {
			updateTextItems();
		}


		if (plainTextItem != null) {
			updateFont(plainTextItem);
		}



	}

	protected void updateTextItems() {

		// check for new LaTeX
		boolean latexAfterEdit = false;

		if (!isDefinitionAndValue() && outputPanel != null) {
			content.remove(outputPanel);

		}
		if (kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE
				|| isDefinitionAndValue()) {
			String text = "";
			if (geo != null) {
				text = getLatexString(isInputTreeItem(), LATEX_MAX_EDIT_LENGHT);
				latexAfterEdit = (text != null);
			} else {
				latexAfterEdit = true;
			}

			if (latex && latexAfterEdit) {
				// Both original and edited text is LaTeX
				if (isInputTreeItem() && geo != null) {
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
			} else {
				// original text was latex.

				updateItemColor();
				content.clear();
				content.add(getPlainTextItem());
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

	protected Canvas canvas;
	private Canvas valCanvas;

	protected abstract void renderLatex(String text0, Widget w,
			boolean forceMQ);

	protected void renderLatexCanvas(String text0, Element old) {

			canvas = DrawEquationW.paintOnCanvas(geo, text0, canvas,
					getFontSize());

			if (canvas != null && content.getElement().isOrHasChild(old)) {
				content.getElement().replaceChild(canvas.getCanvasElement(), old);
			}

	}



	private Canvas latexToCanvas(String text) {
		return DrawEquationW.paintOnCanvas(geo, text, canvas, getFontSize());
	}

	private void updateLaTeX(String text) {
		if (!isDefinitionAndValue()) {
			content.clear();
			canvas = DrawEquationW.paintOnCanvas(geo, text, canvas,
					getFontSize());

			content.add(canvas);
			return;
		}
	}

	protected void adjustToPanel(final FlowPanel panel) {
		if (!app.has(Feature.AV_SCROLL)) {
			return;
		}


		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				int width = panel.getOffsetWidth()
						+ marblePanel.getOffsetWidth();
				Log.debug("[AD] adjustToPanel: " + width);
				setAVItemWidths(width);
			}
		});
	}

	private void setAVItemWidths(int width) {
		if (getOffsetWidth() < width) {
			getAV().setItemWidth(width);
		}

	}
	public void setItemWidth(int width) {
		if (isInputTreeItem()) {
			getWidget().getElement().getParentElement().getStyle()
					.setWidth(width, Unit.PX);
		} else {
			setWidth(width + "px");
		}
		onResize();
	}

	protected void replaceToCanvas(String text, Widget old) {

		updateLaTeX(text);
		LayoutUtilW.replace(content, canvas, old);
	}





	/**
	 * @return size for JLM texts. Due to different fonts we need a bit more
	 *         than app.getFontSize(), but +3 looked a bit too big
	 */
	protected int getFontSize() {
		return app.getFontSizeWeb() + 1;
	}




	protected void updateColor(Widget w) {
		if (geo != null) {
			w.getElement().getStyle()
					.setColor(GColor.getColorString(geo.getAlgebraColor()));
		}
	}

	public boolean isEditing() {
		return editing;
	}

	public abstract void cancelEditing();

	/**
	 * Switches to edit mode
	 * 
	 * @param substituteNumbers
	 *            whether value should be used
	 * @return whether it was successful
	 */
	public boolean enterEditMode(boolean substituteNumbers) {
		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			content.addStyleName("scrollableTextBox");
			if (isInputTreeItem()) {
				setItemWidth(getAV().getOffsetWidth());
			}
		}

		if (isEditing()) {
			return true;
		}



		if (controls != null) {
			controls.update(true);
		}

		editing = true;
		if (startEditing(substituteNumbers) == false) {
			return false;
		}
		if (controls != null) {
			controls.setVisible(true);
			updateButtonPanelPosition();
		}
		maybeSetPButtonVisibility(true);
		return true;
	}

	protected String getTextForEditing(boolean substituteNumbers,
			StringTemplate tpl) {
		return geo.getLaTeXAlgebraDescriptionWithFallback(
						substituteNumbers
								|| (geo instanceof GeoNumeric
										&& geo.isSimple()),
						tpl, true);

	}

	/**
	 * Starts the equation editor for the item.
	 * 
	 * @param substituteNumbers
	 *            Sets that variables must be substituted or not
	 * @return
	 */
	protected abstract boolean startEditing(boolean substituteNumbers);
	protected boolean isDefinitionAndValue() {
		return definitionAndValue && kernel
				.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE;
	}

	private static boolean isMoveablePoint(GeoElement point) {
		return (point.isPointInRegion() || point.isPointOnPath())
				&& point.isChangeable();
	}

	public void styleEditor() {
		if (!app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			return;
		}
		content.removeStyleName("scrollableTextBox");
		if (isInputTreeItem()) {
			setItemWidth(getAV().getMaxItemWidth());
		}

	}

	public void styleScrollBox() {
		if (!app.has(Feature.AV_SCROLL)) {
			return;
		}
		content.addStyleName("scrollableTextBox");

	}

	public void stopEditing(String newValue0,
			final AsyncOperation<GeoElement> callback) {

		styleEditor();

		restoreSize();
		if (stylebarShown != null) {
			getAlgebraDockPanel().showStyleBarPanel(stylebarShown);
			stylebarShown = null;
		}

		removeCloseButton();

		editing = false;
		av.cancelEditing();
		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			if (btnClearInput != null){
				content.remove(btnClearInput);
				btnClearInput = null;
			}
			if (controls != null) {
				controls.setVisible(true);
			}
		}

		if (newValue0 != null) {
			String newValue = stopCommon(newValue0);
			// // not sure why it is needed... TODO: is this needed?
			newValue.replace(" ", "");

			// Formula Hacks ended.
			if (geo != null) {
				boolean redefine = !isMoveablePoint(geo);
				kernel.getAlgebraProcessor().changeGeoElement(geo, newValue,
						redefine, true, getErrorHandler(true),
						new AsyncOperation<GeoElement>() {

							@Override
							public void callback(GeoElement geo2) {
								if (geo2 != null) {
									geo = geo2;
								}
								updateAfterRedefine(geo != null);
								if (callback != null) {
									callback.callback(geo2);
								}

							}
						});
				return;
			}
		} else {
			if (isDefinitionAndValue()) {
				cancelDV();
			}
		}
		updateAfterRedefine(false);
	}

	/**
	 * @param success
	 *            whether redefinition was successful
	 */
	protected void updateAfterRedefine(boolean success) {
		if (latexItem == null) {
			return;
		}
		if (!this.isInputTreeItem() && canvas != null
				&& content.getElement().isOrHasChild(latexItem.getElement())) {
			if (geo != null) {
				LayoutUtilW.replace(content, canvas, latexItem);
			} else {
				Log.debug(Feature.RETEX_EDITOR, "update after redefine");
			}

		}
		if (!latex && !this.isInputTreeItem() && getPlainTextItem() != null
				&& content.getElement().isOrHasChild(latexItem.getElement())) {
			LayoutUtilW.replace(content, getPlainTextItem(), latexItem);

		}
		// maybe it's possible to enter something which is non-LaTeX
		if (success) {
			doUpdate();
		}

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				scrollIntoView();
			}
		});

	}

	private void cancelDV() {
		// LayoutUtilW.replace(ihtml, definitionPanel, latexItem);
		doUpdate();
	}


	protected String stopCommon(String newValue) {
		return newValue;
	}

	protected abstract void blurEditor();

	private static final int HORIZONTAL_BORDER_HEIGHT = 2;

	/**
	 * Make sure the line height of the help icon fits the line height of the
	 * rest
	 */
	protected void updateLineHeight() {
		if (helpButtonPanel != null) {
			this.helpButtonPanel.getElement().getStyle().setLineHeight(
					content.getOffsetHeight() - HORIZONTAL_BORDER_HEIGHT,
					Unit.PX);
		}

	}

	protected void createErrorLabel() {
		this.errorLabel = new Label();
		errorLabel.setStyleName("algebraError");
		main.add(errorLabel);
	}
	/**
	 * @param valid
	 *            whether this is for valid string (false = last valid substring
	 *            used)
	 * @return error handler
	 */
	protected ErrorHandler getErrorHandler(final boolean valid) {
		if (errorLabel != null) {
			errorLabel.setText("");
		} else {
			createErrorLabel();
		}
		return new ErrorHandler(){

			public void showError(String msg) {
				if (errorLabel != null) {
					errorLabel.setText(msg);
				} else {
					app.getDefaultErrorHandler().showError(msg);
				}
				
			}

			public boolean onUndefinedVariables(String string,
					AsyncOperation<String[]> callback) {
				if (valid) {
					return app.getGuiManager().checkAutoCreateSliders(string,
							callback);
				} else if (app.getLocalization()
						.getReverseCommand(getCurrentCommand()) != null) {
					setShowInputHelpPanel(true);
					((InputBarHelpPanelW) app.getGuiManager()
							.getInputHelpPanel()).focusCommand(
									getCommand());

					return false;
				}
				callback.callback(new String[] { "7" });
				return false;
			}



			public void showCommandError(String command, String message) {
				if (errorLabel != null) {
					errorLabel.setText(message);
				} else {
					app.getDefaultErrorHandler().showCommandError(command,
							message);
				}
			}

			public String getCurrentCommand() {
				return RadioTreeItem.this.getCommand();
			}
			
		};
	}

	/**
	 * @param show
	 *            whether to show input help
	 */
	/**
	 * @param show
	 *            true to show input help
	 */
	public void setShowInputHelpPanel(boolean show) {

		if (show) {
			if (!av.isEditing()) {
				ensureEditing();
			}

			removeDummy();

			if (isInputTreeItem()) {
				setFocus(true);
			}
			InputBarHelpPanelW helpPanel = (InputBarHelpPanelW) app
					.getGuiManager().getInputHelpPanel();

			if (helpPopup == null && app != null) {
				helpPopup = new InputBarHelpPopup(this.app, this,
						"helpPopupAV");
				helpPopup.addAutoHidePartner(this.getElement());
				helpPopup.addCloseHandler(new CloseHandler<GPopupPanel>() {

					@Override
					public void onClose(CloseEvent<GPopupPanel> event) {
						focusAfterHelpClosed();
					}

				});

				if (btnHelpToggle != null) {
					helpPopup.setBtnHelpToggle(btnHelpToggle);
				}
			} else if (app != null && helpPopup.getWidget() == null) {
				helpPanel = (InputBarHelpPanelW) app.getGuiManager()
						.getInputHelpPanel();
				helpPopup.add(helpPanel);
			}

			updateHelpPosition(helpPanel);

		} else if (helpPopup != null) {
			helpPopup.hide();
		}
	}

	protected void focusAfterHelpClosed() {
		content.getElement().getElementsByTagName("textarea").getItem(0).focus();
	}



	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		evt.stopPropagation();

		if (marblePanel != null
				&& marblePanel.isHit(evt.getClientX(), evt.getClientY())) {
			return;
		}

		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if (commonEditingCheck())
			return;

		onDoubleClickAction(evt.isControlKeyDown());
	}

	protected void onDoubleClickAction(boolean ctrl) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selectionCtrl.clear();
		ev.resetMode();
		if (geo != null && !ctrl) {
			if (!isEditing()) {
				geo.setAnimating(false);
				av.startEditing(geo);
				if (app.has(Feature.AV_INPUT_BUTTON_COVER)
						&& controls != null) {
					controls.setVisible(false);
				}

				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {

							public void execute() {
								expandSize(getWidthForEdit());

								if (styleBarCanHide()
										&& (!getAlgebraDockPanel()
												.isStyleBarVisible())) {
									stylebarShown = getAlgebraDockPanel()
											.isStyleBarPanelShown();
									getAlgebraDockPanel()
											.showStyleBarPanel(false);
									if (controls != null) {
										controls.getElement().getStyle()
												.setRight(0, Unit.PX);
									}
								}

								if (controls != null) {
									controls.removeAnimPanel();
								}
							}

						});


			}
			app.showKeyboard(this);
			this.setFocus(true);
		}
	}

	boolean styleBarCanHide() {
		if (!getAlgebraDockPanel().isStyleBarPanelShown()) {
			return false;
		}
		int itemTop = this.isInputTreeItem()
				? main.getElement()
				.getAbsoluteTop() : getElement()
				.getAbsoluteTop();
		Log.debug(Feature.AV_INPUT_BUTTON_COVER, "itemTop: " + itemTop);
		Log.debug(Feature.AV_INPUT_BUTTON_COVER,
				"dockpaneltop: " + getAlgebraDockPanel().getAbsoluteTop());
		return (itemTop - getAlgebraDockPanel().getAbsoluteTop() < 35);
	}

	protected int getWidthForEdit() {
		int appWidth = (int) app.getWidth();
		if (appWidth < 1) {// for case app is not part of DOM
			appWidth = 600;
		}
		return Math.min(content.getOffsetWidth() + 70, appWidth);
	}

	static boolean isWidgetHit(Widget w, MouseEvent<?> evt) {
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
		if (geo == null) {
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
		app.closePopups();
		handleAVItem(event);
		event.stopPropagation();
		if (commonEditingCheck()) {
			// keep focus in editor
			event.preventDefault();
		}
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
				ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		this.updateButtonPanelPosition();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		SliderWJquery.stopSliders();
		event.stopPropagation();
	}


	public void update() {
		// marblePanel.setBackground();
	}

	public void adjustControlsPosition() {
		if (!app.has(Feature.AV_SCROLL)) {
			return;
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				ScrollPanel algebraPanel = ((AlgebraDockPanelW) app
						.getGuiManager().getLayout().getDockManager()
						.getPanel(App.VIEW_ALGEBRA)).getAbsolutePanel();
				int scrollPos = algebraPanel.getHorizontalScrollPosition();

				// extra margin if vertical scrollbar is visible.
				int margin = getAV().getOffsetHeight()
						+ getOffsetHeight() > algebraPanel
						.getOffsetHeight() ? BROWSER_SCROLLBAR_WIDTH : 0;

				int value = margin + getOffsetWidth()
						- (algebraPanel.getOffsetWidth() + scrollPos);
					 
				if (controls != null) {
					Log.debug("[AD] controls right: " + value);
					controls.getElement().getStyle().setRight(value, Unit.PX);
				}

			}
		});
	}

	protected void updateButtonPanelPosition() {
		if (controls == null) {
			return;
		}
		
		boolean accurate = true; // used for testing the new code
		if (styleBarCanHide()) {
			Log.debug(Feature.AV_INPUT_BUTTON_COVER, "canhide");
			ScrollPanel algebraPanel = ((AlgebraDockPanelW) app.getGuiManager()
					.getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA))
					.getAbsolutePanel();
			
			if (accurate) { // new code
				if (app.has(Feature.AV_SCROLL)) {
					adjustControlsPosition();
				} else {
					Log.debug(Feature.AV_INPUT_BUTTON_COVER,
							"algebra panel null: " + (algebraPanel == null));
					int scrollbarWidth = algebraPanel == null ? 0
							: algebraPanel.getOffsetWidth() - algebraPanel
									.getElement().getClientWidth();
					controls.getElement().getStyle()
							.setRight(46 - scrollbarWidth, Unit.PX);
				}
			} else { // old code
			
				if (algebraPanel != null
						&& algebraPanel.getOffsetWidth() > algebraPanel.getElement()
								.getClientWidth()) {
					controls.addStyleName(
							"positionedObjectStyleBar_scrollbarVisible");
					controls.removeStyleName("positionedObjectStyleBar");
				} else { 
					controls.addStyleName("positionedObjectStyleBar");
					controls
					.removeStyleName("positionedObjectStyleBar_scrollbarVisible"); 
				}
			
			}
		} else {
			Log.debug("canNOT hide");
			if (accurate) {
				adjustControlsPosition();
				// controls.getElement().getStyle().setRight(visibleRight,
				// Unit.PX);
			} else {
				controls.removeStyleName("positionedObjectStyleBar");
				controls
						.removeStyleName("positionedObjectStyleBar_scrollbarVisible");
			}
		}
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

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param rightClick
	 *            wheher rght click was used
	 */
	protected boolean handleAVItem(int x, int y, boolean rightClick) {

		if (!selectionCtrl.isSelectHandled()) {
			selectItem(true);
		}

		return false;

	}
	protected AlgebraViewW getAV() {
		return (AlgebraViewW) av;
	}


	public GeoElement getGeo() {
		return geo;
	}

	public long latestTouchEndTime = 0;
	private boolean selectedItem = false;
	protected AVSelectionController selectionCtrl;

	// controls must appear on select or not
	private boolean forceControls = false;


	@Override
	public void onTouchEnd(TouchEndEvent event) {
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

	protected void onPointerDown(AbstractEvent event) {
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
			removeDummy();
			((PointerEvent) event).getWrappedEvent().stopPropagation();
			if (isInputTreeItem()) {
				// put earlier, maybe it freezes afterwards?
				setFocus(true);
			}

		}
		if (app.getActiveEuclidianView()
				.getMode() == EuclidianConstants.MODE_MOVE
				|| app.getActiveEuclidianView()
						.getMode() == EuclidianConstants.MODE_SELECTION_LISTENER) {
			updateSelection(event.isControlDown(), event.isShiftDown());
		}

	}


	protected void onPointerUp(AbstractEvent event) {
		selectionCtrl.setSelectHandled(false);

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

	public void updateSelection(boolean separated, boolean continous) {
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

	protected PushButton getPButton() {
		return null;
	}

	/**
	 * @param bool
	 *            whether pbutton should be visible
	 */
	protected void maybeSetPButtonVisibility(boolean bool) {
		// only show the delete button, but not the extras
	}

	/**
	 * 
	 * @param event
	 *            mouse move event
	 */
	protected void onPointerMove(AbstractEvent event) {
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





	public void scrollIntoView() {
		this.getElement().scrollIntoView();
	}



	public void removeCloseButton() {
		this.maybeSetPButtonVisibility(true);
		if (controls != null) {
			controls.setVisible(false);
		}
	}


	void addAVEXWidget(Widget w) {
		main.add(w);
	}

	protected boolean hasGeoExtendedAV() {
		return (geo instanceof HasExtendedAV && ((HasExtendedAV) geo)
				.isShowingExtendedAV());
	}

	public void setDraggable() {
		Widget draggableContent = main;
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

	// @Override
	public App getApplication() {
		return app;
	}

	@Override
	public void ensureEditing() {
		if (!isEditing()) {
			enterEditMode(geo == null || isMoveablePoint(geo));

			if (av != null && ((AlgebraViewW) av).isNodeTableEmpty()) {
				updateGUIfocus(this, false);
			}

		}
	}

	/**
	 * Update GUI after focus
	 * 
	 * @param source
	 *            event source
	 * @param blurtrue
	 *            blur
	 */
	protected void updateGUIfocus(Object source, boolean blurtrue) {
		// only in input element
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
	public void onResize() {
		if (!app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			if (first && isSelected()) {
				controls.update(true);
			}
		}
		updateButtonPanelPosition();
	}



	public boolean isItemSelected() {
		return selectedItem;
	}

	private void toggleControls() {
		// GGB-986 Don't show controls if geo is only highlighted
		// but no selected.

		if (controls == null) {
			return;
		}

		if (isForceControls()) {
			setForceControls(false);
			controls.setVisible(controls.isVisible());

		}

		boolean geoInSelection = app.getSelectionManager()

				.containsSelectedGeo(geo);

		if (!controls.isVisible() && geoInSelection) {
			if (controls.update(true)) {
				// don't show controls for multiselect
				controls.setVisible(true);
			}
		} else if (controls.isVisible() && !geoInSelection) {
			controls.setVisible(false);
		}
	}

	public void selectItem(boolean selected) {
		toggleControls();

		if (selectedItem == selected) {
			return;
		}

		setForceControls(false);

		selectedItem = selected;

		if (selected) {
			addStyleName("avSelectedRow");



		} else {
			removeStyleName("avSelectedRow");
		}
		if (marblePanel != null) {
			marblePanel.setHighlighted(selected);
		}
		if (selected == false
				// && geo != AVSelectionController.get(app).getLastSelectedGeo()
		) {
			controls.reset();
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

	public Element getScrollElement() {
		return getWidget().getElement();
	}

	protected AlgebraDockPanelW getAlgebraDockPanel() {
		return (AlgebraDockPanelW) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);

	}



	public void autocomplete(String s) {
		// TODO implement autocomplete in RTI
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

	protected boolean hasAnimPanel() {
		return controls.animPanel != null;
	}

	protected boolean hasMarblePanel() {
		return marblePanel != null;
	}

	private String getOutputPrefix() {
		if (geo instanceof HasSymbolicMode
				&& !((HasSymbolicMode) geo).isSymbolicMode()) {
			return Unicode.CAS_OUTPUT_NUMERIC;
		}
		if (kernel.getLocalization().rightToLeftReadingOrder) {
			return Unicode.CAS_OUTPUT_PREFIX_RTL;
		}
		return Unicode.CAS_OUTPUT_PREFIX;
	}

	public void setLabels() {
		// TODO Auto-generated method stub

	}

	/**
	 * Remove the main panel from parent
	 */
	public void removeFromParent() {
		main.removeFromParent();
	}

	public final boolean hasHelpPopup() {
		return this.helpPopup != null;
	}

	public void replaceXButtonDOM() {
		// TODO Auto-generated method stub

	}

	public abstract void handleFKey(int key, GeoElement geo1);

	@Override
	public ToggleButton getHelpToggle() {
		return this.btnHelpToggle;
	}



	@Override
	public void updateIcons(boolean warning) {
		if (btnHelpToggle == null) {
			btnHelpToggle = new ToggleButton();
		}
		if (!warning && errorLabel != null) {
			errorLabel.setText("");
		}
		btnHelpToggle.getUpFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));
		// new
		// Image(AppResources.INSTANCE.inputhelp_left_20x20().getSafeUri().asString()),
		btnHelpToggle.getDownFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));

	}

	protected void updateHelpPosition(final InputBarHelpPanelW helpPanel) {
		helpPopup.setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				double scale = app.getArticleElement().getScaleX();
				double renderScale = app.getArticleElement().getDataParamApp()
						? scale : 1;
				helpPopup.getElement().getStyle()
						.setProperty("left",
								(btnHelpToggle.getAbsoluteLeft()
										- app.getAbsLeft()
										+ btnHelpToggle.getOffsetWidth())
										* renderScale
										+ "px");
				int maxOffsetHeight;
				int totalHeight = (int) app.getHeight();
				int toggleButtonTop = (int) ((btnHelpToggle.getParent()
						.getAbsoluteTop() - (int) app.getAbsTop()) / scale);
				if (toggleButtonTop < totalHeight / 2) {
					int top = (toggleButtonTop
							+ btnHelpToggle.getParent().getOffsetHeight());
					maxOffsetHeight = totalHeight - top;
					helpPopup.getElement().getStyle().setProperty("top",
							top * renderScale + "px");
					helpPopup.getElement().getStyle().setProperty("bottom",
							"auto");
					helpPopup.removeStyleName("helpPopupAVBottom");
					helpPopup.addStyleName("helpPopupAV");
				} else {
					int minBottom = app.isApplet() ? 0 : 10;
					int bottom = (totalHeight
							- toggleButtonTop);
					maxOffsetHeight = bottom > 0 ? totalHeight - bottom
							: totalHeight - minBottom;
					helpPopup.getElement().getStyle().setProperty("bottom",
							(bottom > 0 ? bottom : minBottom) * renderScale
									+ "px");
					helpPopup.getElement().getStyle().setProperty("top",
							"auto");
					helpPopup.removeStyleName("helpPopupAV");
					helpPopup.addStyleName("helpPopupAVBottom");
				}
				helpPanel.updateGUI(maxOffsetHeight, 1);
				helpPopup.show();
			}
		});

	}

	private SimplePanel helpButtonPanel;
	protected final void insertHelpToggle() {
		if (app.has(Feature.INPUTHELP_SHOWN_IN_AV)) {
			helpButtonPanel = new SimplePanel();
			updateIcons(false);
			btnHelpToggle.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (btnHelpToggle.isDown()) {
						app.hideKeyboard();
						Scheduler.get().scheduleDeferred(
								new Scheduler.ScheduledCommand() {
									@Override
									public void execute() {
										setShowInputHelpPanel(true);
										((InputBarHelpPanelW) app
												.getGuiManager()
												.getInputHelpPanel())
														.focusCommand(
																getCommand());
									}
								});
					} else {
						setShowInputHelpPanel(false);
					}

				}

			});
			helpButtonPanel.setStyleName("avHelpButtonParent");
			helpButtonPanel.setWidget(btnHelpToggle);
			btnHelpToggle.addStyleName("algebraHelpButton");
			main.insert(helpButtonPanel, 0);
		}

	}

	/**
	 * @return whether input text is empty
	 */
	protected boolean isEmpty() {
		return "".equals(getText().trim());
	}

	protected void addDummyLabel() {
		if (dummyLabel == null) {
			dummyLabel = new Label(
					app.getPlain("InputLabel") + Unicode.ellipsis);
			dummyLabel.addStyleName("avDummyLabel");
		}
		if (canvas != null) {
			canvas.setVisible(false);
		}
		// if (dummyLabel.getElement() != null) {
		// if (dummyLabel.getElement().hasParentElement()) {
		// in theory, this is done in insertFirst,
		// just making sure here as well
		// dummyLabel.getElement().removeFromParent();
		// }
		content.insert(dummyLabel, 0);
		// }

		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			if (btnClearInput != null) {
				btnClearInput.removeFromParent();
				btnClearInput = null;
			}
			if (controls != null) {
				controls.setVisible(true);
			}
			setLatexItemVisible(false);
		}
	}

	private void setLatexItemVisible(boolean b) {
		if (this.latexItem != null) {
			this.latexItem.setVisible(b);
		}

	}

	protected void removeDummy() {
		if (this.dummyLabel != null) {
			dummyLabel.removeFromParent();
		}
		if (canvas != null) {
			canvas.setVisible(true);
		}

		if (app.has(Feature.AV_INPUT_BUTTON_COVER)) {
			// content.add(getClearInputButton());
			if (app.has(Feature.AV_PREVIEW)) {
				content.insert(getClearInputButton(), 0);
			} else {
				content.add(getClearInputButton());
			}
			if (controls != null) {
				controls.setVisible(false);
			}
			setLatexItemVisible(true);
		}

	}

	protected void updateEditorFocus(Object source, boolean blurtrue) {
		// deselects current selection
		((AlgebraViewW) av).setActiveTreeItem(null);

		boolean emptyCase = ((AlgebraViewW) av).isNodeTableEmpty()
				&& !this.getAlgebraDockPanel().hasLongStyleBar();

		// update style bar icon look
		if (emptyCase) {
			getAlgebraDockPanel().showStyleBarPanel(blurtrue);
		} else {
			getAlgebraDockPanel().showStyleBarPanel(true);
		}

		// After changing the stylebar visibility, maybe the small stylebar's
		// position will be changed too
		if (controls != null) {
			updateButtonPanelPosition();
		}

		// always show popup, except (blurtrue && emptyCase) == true

		// this basically calls the showPopup method, like:
		// showPopup(!blurtrue || !emptyCase);
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source,
				!blurtrue || !emptyCase);

		// afterwards, if the popup shall be showing,
		// then all of our three icons are visible in theory
		// except pButton, if it is null...
		if (!blurtrue || !emptyCase) {
			typing(false);
		}

	}

	/**
	 * @param heuristic
	 *            true = user is typing
	 */
	public void typing(boolean heuristic) {
		// to be overridden in InputTreeItem,
		// to know whether it's empty, whether to show Xbutton
	}

	public abstract RadioTreeItem copy();

	@Override
	public void updatePosition(DefaultSuggestionDisplay sug) {
		sug.setPositionRelativeTo(content);
	}

	@Override
	public boolean isForCAS() {
		return false;
	}

	public boolean needsAutofocus() {
		return true;
	}

	public void expandSize(int newWidth) {
		if (getAV().getOriginalWidth() != null) {
			return;
		}
		AlgebraDockPanelW avDockPanel = getAlgebraDockPanel();
		int w = avDockPanel.asWidget().getOffsetWidth();
		if (w < newWidth) {
			DockSplitPaneW splitPane = avDockPanel.getParentSplitPane();
			if (splitPane == null || splitPane
					.getOrientation() == DockSplitPaneW.VERTICAL_SPLIT) {
				return;
			}
			getAV().setOriginalWidth(w);
			splitPane.setWidgetSize(avDockPanel, newWidth);
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

	protected PushButton getClearInputButton() {
		if (btnClearInput == null) {
			btnClearInput = new PushButton(
					new Image(GuiResources.INSTANCE.algebra_delete()));
			btnClearInput.addMouseDownHandler(new MouseDownHandler() {
				public void onMouseDown(MouseDownEvent event) {
					clearInput();
					RadioTreeItem.this.setFocus(true);
					event.stopPropagation();
				}
			});
			btnClearInput.addStyleName("ggb-btnClearAVInput");
		}
		return btnClearInput;
	}

	/**
	 * Adds the needed event handlers to FlowPanel
	 * 
	 * @param panel
	 *            add events to.
	 */
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

	public boolean isForceControls() {
		return forceControls;
	}

	public void setForceControls(boolean forceControls) {
		this.forceControls = forceControls;
	}

	/**
	 * @param pixelRatio
	 *            pixel ratio for input panel
	 */
	public void setPixelRatio(float pixelRatio) {
		// only for LaTeX tree item

	}

	public boolean isLatex() {
		return latex;
	}
}

