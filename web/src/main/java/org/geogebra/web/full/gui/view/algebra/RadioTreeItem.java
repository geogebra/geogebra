/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface.Help;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.StringUtil;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.editor.MathFieldProcessing;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentToast;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.full.gui.inputbar.HasHelpButton;
import org.geogebra.web.full.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.full.gui.inputbar.InputBarHelpPopup;
import org.geogebra.web.full.gui.inputbar.WarningErrorHandler;
import org.geogebra.web.full.gui.inputfield.AutoCompletePopup;
import org.geogebra.web.full.gui.layout.panels.AlgebraPanelInterface;
import org.geogebra.web.full.gui.util.Resizer;
import org.geogebra.web.full.gui.util.SyntaxAdapterImplWithPaste;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.activity.GeoGebraActivity;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.inputfield.AbstractSuggestionDisplay;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.tooltip.ComponentSnackbar;
import org.geogebra.web.html5.gui.tooltip.ToolTip;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.util.DataTest;
import org.geogebra.web.html5.util.HasDataTest;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.DragStartEvent;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.TreeItem;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.syntax.SyntaxController;
import com.himamis.retex.editor.share.syntax.SyntaxHint;
import com.himamis.retex.editor.share.syntax.SyntaxTooltipUpdater;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;

/**
 * main -&gt; marblePanel content controls
 *
 * <p>content -&gt; (definitionValuePanel | latexItem | canvas) ariaLabel
 *
 * <p>definitionValuePanel -&gt; STRING | (definitionPanel outputPanel)
 *
 * <p>outputPanel -&gt; valuePanel
 *
 * <p>definitionPanel -&gt; canvas | STRING
 */
public class RadioTreeItem extends AVTreeItem implements MathKeyboardListener,
		AutoCompleteW, RequiresResize, HasHelpButton, SetLabels, SyntaxTooltipUpdater,
		HasDataTest {

	private static final int DEFINITION_ROW_EDIT_MARGIN = 5;
	private static final int MARGIN_RESIZE = 50;

	protected static final int LATEX_MAX_EDIT_LENGHT = 1500;

	Boolean stylebarShown;
	/** Help popup */
	protected InputBarHelpPopup helpPopup;
	/** label "Input..." */
	protected Label dummyLabel;

	/**
	 * The main widget of the tree item containing all others
	 */
	protected FlowPanel main;

	/** Item content after marble */
	protected final FlowPanel content;

	/** Item controls like delete, play, etc */
	protected ItemControls controls;

	protected Canvas canvas;
	private Graphics2DW canvasGraphics;

	String commandError;

	protected String errorMessage;

	protected String lastInput;

	protected GeoElement geo = null;
	protected Kernel kernel;
	protected AppWFull app;
	private final AlgebraViewW av;
	protected boolean latex = false;

	private FlowPanel latexItem;
	private final FlowPanel definitionValuePanel;

	private boolean needsUpdate;

	/**
	 * this panel contains the marble (radio) button
	 */
	protected AlgebraItemHeader marblePanel;

	protected FlowPanel definitionPanel;

	protected AlgebraOutputPanel outputPanel;

	protected Localization loc;

	private LatexTreeItemController controller;

	String lastTeX;
	private MathFieldW mf;
	private boolean selectedItem = false;

	protected boolean first = false;
	private String ariaPreview;
	private Label ariaLabel = null;
	InputItemControl inputControl;
	private ComponentToast toast;
	private final SyntaxController syntaxController;
	private int index;
	private ToggleButton symbolicButton;

	public void updateOnNextRepaint() {
		needsUpdate = true;
	}

	/**
	 * Minimal constructor
	 *
	 * @param kernel
	 *            kernel
	 */
	public RadioTreeItem(Kernel kernel) {
		super();
		this.kernel = kernel;
		app = (AppWFull) kernel.getApplication();
		loc = app.getLocalization();
		av = app.getAlgebraView();
		main = new FlowPanel();
		content = new FlowPanel();
		definitionValuePanel = new FlowPanel();
		inputControl = createInputControl();
		syntaxController = new SyntaxController();
		syntaxController.setUpdater(this);
		setWidget(main);
		setController(createController());

		getController().setLongTouchManager(LongTouchManager.getInstance());
		setDraggable();
	}

	private InputItemControl createInputControl() {
		return new InputMoreControl(this);
	}

	/**
	 * Handle context menu's delete action
	 */
	public void onClear() {
		setText("");
		addDummyLabel();
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

		getDefinitionValuePanel().addStyleName("avPlainText");
		getElement().getStyle().setColor("black");

		updateFont(getDefinitionValuePanel());

		styleContent();

		addControls();

		content.add(getDefinitionValuePanel());
		doUpdate();
		// if enabled, render with LaTeX
		String ltx = getLatexString(null, true);
		if (ltx != null) {
			if (getAV().isLaTeXLoaded()) {
				doUpdate();
			} else {
				setNeedsUpdate(true);
				av.repaintView();
			}

		}
		createAvexWidget();
		addAVEXWidget(content);
		if (app.isUnbundled() && geo0.getParentAlgorithm() != null
				&& geo0.getParentAlgorithm() instanceof AlgoPointOnPath) {
			getWidget().getElement().getStyle().setProperty("minHeight", 72,
					Unit.PX);
		}
		updateDataTest(getIndex());
	}

	protected void addMarble() {
		main.addStyleName("elem");

		marblePanel = app.getCurrentActivity().createAVItemHeader(this,
				getAV().isInputActive() && getGeo() == null);
		setIndexLast();
		updateDataTest();
		main.add(marblePanel);
	}

	protected void resetItemHeader() {
		if (marblePanel != null) {
			marblePanel.asWidget().removeFromParent();
		}
		marblePanel = app.getCurrentActivity().createAVItemHeader(this, true);
		main.insert(marblePanel, 0);
	}

	/**
	 * Update index in the header for the last item of AV
	 */
	protected void setIndexLast() {
		index = getAV().getItemCount();
		if (marblePanel != null) {
			marblePanel.setIndex(index);
		}
	}

	public int getIndex() {
		return index;
	}

	protected void styleContent() {
		content.addStyleName("elemText");
	}

	protected void createControls() {
		controls = new ItemControls(this);
	}

	protected void addControls() {
		if (controls != null) {
			return;
		}
		createControls();

		main.add(controls);
		controls.setVisible(true);
	}

	/**
	 *
	 */
	protected void createAvexWidget() {
		// only for checkboxes
	}

	protected final String getLatexString(Integer limit, boolean output) {
		return AlgebraItem.getLatexString(geo, limit, output);
	}

	private void rebuildContent() {
		if (!buildPlainTextSimple()) {
			buildItemContent();
		} else if (content.getWidgetCount() != 1) {
			// extra content shown (eg. arrow) => rebuild
			rebuildPlaintextContent();
		}
	}

	private boolean buildPlainTextSimple() {
		return AlgebraItem.buildPlainTextItemSimple(geo,
				new DOMIndexHTMLBuilder(getDefinitionValuePanel(), app));
	}

	protected void createDVPanels() {
		if (definitionPanel == null) {
			definitionPanel = new FlowPanel();
		}

		if (outputPanel == null) {
			outputPanel = new AlgebraOutputPanel();
			outputPanel.addStyleName("avOutput");
		}
	}

	private boolean isLatexTrivial() {
		if (!latex) {
			return false;
		}

		String text = getTextForEditing(false, StringTemplate.latexTemplate);
		String[] eq = text.split("=");
		if (eq.length < 2) {
			return false;
		}
		String leftSide = eq[0].trim();
		String rightSide = eq[1].replaceFirst("\\\\left", "")
				.replaceFirst("\\\\right", "").replaceAll(" ", "");

		return leftSide.equals(rightSide);
	}

	private boolean updateDefinitionPanel() {
		if (lastInput != null) {
			definitionFromTeX(lastTeX);
		} else if (latex || AlgebraItem.isGeoFraction(geo)) {
			String text = getTextForEditing(false,
					StringTemplate.numericLatex);
			definitionFromTeX(text);
		} else if (geo != null) {
			IndexHTMLBuilder sb = new DOMIndexHTMLBuilder(definitionPanel, app);
			if (kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DESCRIPTION) {
				if (AlgebraItem.needsPacking(geo)) {
					IndexHTMLBuilder
							.convertIndicesToHTML(
									geo.getDefinitionDescription(
											StringTemplate.defaultTemplate),
									sb);
				} else {
					geo.addLabelTextOrHTML(geo.getDefinitionDescription(
						StringTemplate.defaultTemplate), sb);
				}
			} else {
				AlgebraItem.buildDefinitionString(geo, sb, StringTemplate.defaultTemplate);
			}
		}
		return true;
	}

	private void definitionFromTeX(String text) {
		definitionPanel.clear();

		latexToCanvas(text);
		if (canvas != null) {
			canvas.addStyleName("canvasDef");
			definitionPanel.add(canvas);
		}
	}

	protected boolean updateValuePanel(String text) {
		boolean ret = outputPanel.updateValuePanel(geo, text, latex,
				getFontSize());
		if (geo != null && AlgebraItem.shouldShowSymbolicOutputButton(geo)) {
			addControls();
			symbolicButton = AlgebraOutputPanel.createSymbolicButton(controls, geo);
		} else if (controls != null) {
			AlgebraOutputPanel.removeSymbolicButton(controls);
		}
		return ret;
	}

	private void buildItemContent() {
		if (mayNeedOutput()) {
			if (controller.isEditing() || geo == null) {
				return;
			}
			if (shouldBuildItemWithTwoRows()) {
				buildItemWithTwoRows();
				updateItemColor();
			} else {
				buildItemWithSingleRow();
			}
		} else {
			buildItemWithSingleRow();
		}
	}

	public boolean shouldBuildItemWithTwoRows() {
		return (AlgebraItem.shouldShowBothRows(geo) && !isLatexTrivial()) || lastTeX != null;
	}

	private void buildItemWithTwoRows() {
		createDVPanels();
		String text = getLatexString(null, false);
		latex = text != null;
		if (latex) {
			definitionPanel.addStyleName("avDefinition");
		} else {
			definitionPanel.addStyleName("avDefinitionPlain");
		}

		content.clear();
		if (updateDefinitionPanel()) {
			definitionValuePanel.clear();
			definitionValuePanel.add(definitionPanel);
		}

		if (updateOutputValuePanel()) {
			outputPanel.addValuePanel();
			definitionValuePanel.add(outputPanel);
		}

		content.add(definitionValuePanel);
	}

	private void clearPreview() {
		content.removeStyleName("avPreview");
		content.addStyleName("noPreview");
		if (outputPanel == null) {
			return;
		}
		ariaPreview = null;
		outputPanel.reset();
	}

	/**
	 * Clear preview and sugggestions
	 */
	public void clearPreviewAndSuggestions() {
		clearPreview();
		clearUndefinedVariables();
	}

	/**
	 * @param previewGeo
	 *            preview from input bar
	 */
	public void previewValue(GeoElement previewGeo) {

		if (previewGeo
				.getDescriptionMode() != DescriptionMode.DEFINITION_VALUE
				|| getController().isInputAsText()) {
			clearPreview();

		} else if (isInputTreeItem()) {
			content.removeStyleName("noPreview");
			content.addStyleName("avPreview");

			updateSymbolicMode(previewGeo);
			ariaPreview = previewGeo.getAuralExpression();

			createDVPanels();
			content.addStyleName("avPreview");
			definitionValuePanel.clear();
			definitionValuePanel.add(outputPanel);
			outputPanel.reset();

			String text = previewGeo
					.getAlgebraDescriptionForPreviewOutput();
			outputPanel.showLaTeXPreview(text, previewGeo, getFontSize());
			outputPanel.addArrowPrefix();
			outputPanel.addValuePanel();

			if (content.getWidgetIndex(definitionValuePanel) == -1) {
				content.add(definitionValuePanel);
			}
		} else {
			if (updateOutputValuePanel()) {
				outputPanel.addValuePanel();
			}
		}
		clearUndefinedVariables();
	}

	private boolean updateOutputValuePanel() {
		return updateValuePanel(geo.getLaTeXDescriptionRHS(true,
				app.getConfig().getOutputStringTemplate()));
	}

	private void updateSymbolicMode(GeoElement geoElement) {
		if (geoElement instanceof HasSymbolicMode) {
			((HasSymbolicMode) geoElement).initSymbolicMode();
		}
		geoElement.updateRepaint();
	}

	protected void buildItemWithSingleRow() {
		if (outputPanel != null) {
			outputPanel.reset();
		}
		// LaTeX
		String text = getLatexString(LATEX_MAX_EDIT_LENGHT,
				geo.getDescriptionMode() != DescriptionMode.DEFINITION);
		latex = text != null;

		if (latex) {
			if (isInputTreeItem()) {
				text = geo.getLaTeXAlgebraDescription(true,
						StringTemplate.latexTemplate);
			}

			latexToCanvas(text);
			content.clear();
			content.add(canvas);
		} else {
			if (!buildPlainTextSimple()) {
				geo.getAlgebraDescriptionTextOrHTMLDefault(
						new DOMIndexHTMLBuilder(getDefinitionValuePanel(), app));
			}
			updateItemColor();
			rebuildPlaintextContent();
		}
		AlgebraOutputPanel.removeSymbolicButton(controls);
	}

	private void rebuildPlaintextContent() {
		content.clear();
		content.add(definitionValuePanel);
		if (geo != null && geo.getParentAlgorithm() != null
				&& geo.getParentAlgorithm().getOutput(0) != geo
				&& mayNeedOutput()) {
			content.addStyleName("additionalRow");
			updateFont(content);

			Image arrow = new NoDragImage(
					MaterialDesignResources.INSTANCE.equal_sign_white(), 24, 24);
			arrow.setStyleName("arrowOutputImg");
			content.insert(arrow, 0);
		}
	}

	protected void updateFont(Widget w) {
		w.getElement().getStyle().setFontSize(app.getFontSize(), Unit.PX);
	}

	protected void styleContentPanel() {
		controls.updateAnimPanel();
	}

	/**
	 * @param first
	 *            whether this item is first in AV
	 */
	public void setFirst(boolean first) {
		this.first = first;
		updateButtonPanelPosition();
	}

	/**
	 * Update this item if it was invalidated; update selection
	 */
	public void repaint() {
		if (isNeedsUpdate()) {
			doUpdate();
		}
		// highlight only
		boolean selected = geo.doHighlighting();

		setSelected(selected);

		// select only if it is in selection really.
		selectItem(selected);
	}

	/**
	 * @return whether an item is being edited
	 */
	public boolean commonEditingCheck() {
		return av.isEditItem() || controller.isEditing() || isInputTreeItem()
				|| geo == null;
	}

	protected void doUpdateEnsureNoEditor() {
		doUpdate();
		if (!latex) {
			rebuildPlaintextContent();
		}
	}

	protected void updateTextItems() {
		// check for new LaTeX
		boolean latexAfterEdit = false;

		if (!mayNeedOutput() && outputPanel != null) {
			content.remove(outputPanel);
		}
		if (kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE
				|| isAlgebraStyleDefAndValue()
				|| (geo != null && geo.getParentAlgorithm() instanceof AlgoFractionText)) {
			String text = "";
			if (geo != null) {
				text = getLatexString(LATEX_MAX_EDIT_LENGHT, true);
				latexAfterEdit = (text != null);
			} else {
				latexAfterEdit = true;
			}

			if (latex && latexAfterEdit) {
				// Both original and edited text is LaTeX
				if (isInputTreeItem() && geo != null) {
					text = geo.getLaTeXAlgebraDescription(true,
							StringTemplate.latexTemplate);
				}
				updateLaTeX(text);

			} else if (latexAfterEdit) {
				// Edited text is latex now, but the original is not.
				renderLatex(text, getDefinitionValuePanel(), isInputTreeItem());
				latex = true;
			}

		} else if (geo == null) {
			return;
		}
		// edited text is plain
		if (!latexAfterEdit) {

			rebuildContent();
			if (latex) {
				// original text was latex.
				updateItemColor();
				content.clear();
				content.add(definitionValuePanel);
				latex = false;
			}
		}
	}

	private void updateItemColor() {
		if (isAlgebraStyleDefAndValue() && definitionPanel != null) {
			definitionPanel.getElement().getStyle().setColor("black");
		}
	}

	private void latexToCanvas(String text) {
		if (canvasGraphics != null) {
			canvasGraphics.cancelCallbacks();
		}
		canvas = DrawEquationW.makeCleanCanvas(canvas);
		canvasGraphics = ((DrawEquationW) app.getDrawEquation()).paintOnCleanCanvas(text, canvas,
				getFontSize(), GColor.BLACK, DrawEquationW.needsSerif(geo));
	}

	private void updateLaTeX(String text) {
		if (!isAlgebraStyleDefAndValue()) {
			content.clear();
			latexToCanvas(text);
			content.add(canvas);
		}
	}

	/**
	 * @param width
	 *            item width
	 */
	public void setItemWidth(int width) {
		if (getOffsetWidth() != width && width >= 0) {
			if (isInputTreeItem()) {
				Element inputParent = getWidget().getElement()
						.getParentElement();
				Resizer.setPixelWidth(inputParent, width);
			} else if (!isTextItem()) {
				setWidth(width + "px");
			}
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
		return app.getFontSize() + 1;
	}

	/**
	 * Switches to edit mode
	 *
	 * @param substituteNumbers
	 *            whether value should be used
	 * @return whether it was successful
	 */
	public boolean enterEditMode(boolean substituteNumbers) {
		content.addStyleName("scrollableTextBox");
		if (isInputTreeItem()) {
			setItemWidth(getAV().getOffsetWidth());
		}

		if (controller.isEditing()) {
			return true;
		}

		if (controls != null) {
			controls.update();
		}

		controller.setEditing(true);
		insertHelpToggle();

		if (!onEditStart()) {
			return false;
		}
		getLatexController().dispatchEditEvent(EventType.EDITOR_START);
		if (controls != null) {
			controls.setVisible(true);
			updateButtonPanelPosition();
		}

		return true;
	}

	private boolean useValidInput() {
		return app.getCurrentActivity().useValidInput();
	}

	protected String getTextForEditing(boolean substituteNumbers,
			StringTemplate tpl) {
		if (AlgebraItem.needsPacking(geo)) {
			return geo.getLaTeXDescriptionRHS(substituteNumbers, tpl);
		} else if (!geo.isAlgebraLabelVisible()) {
			return geo.getDefinition(tpl);
		}
		return geo.getLaTeXAlgebraDescriptionWithFallback(
				substituteNumbers
						|| (geo instanceof GeoNumeric && geo.isSimple()),
				tpl, true);

	}

	private boolean isAlgebraStyleDefAndValue() {
        int algebraStyle = app.getSettings().getAlgebra().getStyle();
		return algebraStyle == AlgebraStyle.DEFINITION_AND_VALUE;
	}

	protected boolean mayNeedOutput() {
		return kernel
				.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE
				|| kernel
						.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DESCRIPTION;
	}

	private static boolean isMoveablePoint(GeoElement point) {
		return (point.isPointInRegion() || point.isPointOnPath())
				&& point.isPointerChangeable();
	}

	/**
	 * Remove style for scrollable item
	 */
	public void styleEditor() {
		if (!isInputTreeItem()) {
			content.removeStyleName("scrollableTextBox");
		}
	}

	/**
	 * Update the UI for empty input element
	 */
	public void updateUIforInput() {
		content.addStyleName("scrollableTextBox");
		setIndexLast();
	}

	/**
	 * @param rawInput
	 *            value after edit
	 * @param callback
	 *            callback
	 * @param allowSliderDialog
	 *            whether to allow slider dialog
	 */
	public final void stopEditing(final String rawInput,
			final AsyncOperation<GeoElementND> callback,
			boolean allowSliderDialog) {
		lastTeX = null;
		lastInput = null;
		onStopEdit();
		styleEditor();

		if (!app.isUnbundled() && stylebarShown != null) {
			getAlgebraDockPanel().showStyleBarPanel(stylebarShown);
			stylebarShown = null;
		}
		kernel.notifyUpdatePreviewFromInputBar(null);
		removeCloseButton();
		controller.setEditing(false);

		av.cancelEditItem();
		getAV().setLaTeXLoaded();

		inputControl.ensureControlVisibility();

		if (!StringUtil.empty(rawInput)) {
			String v = app.getKernel().getInputPreviewHelper()
					.getInput(rawInput);
			String value = useValidInput() ? v : rawInput;
			String newValue = isTextItem() ? "\"" + value + "\"" : value;
			final boolean wasLaTeX = geo instanceof GeoText
					&& ((GeoText) geo).isLaTeX();
			if (geo != null) {
				boolean redefine = !isMoveablePoint(geo);
				this.lastInput = newValue;
				this.lastTeX = getEditorLatex();
				if (this.lastTeX == null) {
					this.lastInput = null;
				}

				EvalInfo info = EvalInfoFactory.getEvalInfoForRedefinition(kernel, geo, redefine);
				kernel.getAlgebraProcessor()
						.changeGeoElementNoExceptionHandling(geo, newValue, info, true,
								geo2 -> {
									if (geo2 != null) {
										geo = geo2.toGeoElement();
										lastTeX = null;
										lastInput = null;
									}
									if (geo instanceof GeoText && wasLaTeX
											&& geo.isIndependent()) {
										((GeoText) geo).setLaTeX(true, false);
									}
									if (marblePanel != null) {
										marblePanel.updateIcons(false);
									}
									updateAfterRedefine(geo2 != null);
									if (callback != null) {
										callback.callback(geo2);
									}

								}, AlgebraInputW.getWarningHandler(this, app));
				// make sure edting ends: run callback even if not successful
				// TODO maybe prevent running this twice?
				if (!geo.isIndependent()) {
					if (callback != null) {
						callback.callback(geo);
					}
				}

				return;
			}
		} else {
			if (isAlgebraStyleDefAndValue()) {
				cancelDV();
			}
		}

		// empty new value -- consider success to make sure focus goes away
		updateAfterRedefine(rawInput == null);
	}

	/**
	 * @param success
	 *            whether redefinition was successful
	 */
	protected void doUpdateAfterRedefine(boolean success) {
		if (latexItem == null) {
			return;
		}
		if (!this.isInputTreeItem() && canvas != null
				&& content.getElement().isOrHasChild(latexItem.getElement())) {
			if (geo != null) {
				LayoutUtilW.replace(content, canvas, latexItem);
			}
		}

		if (!latex && !this.isInputTreeItem() && getDefinitionValuePanel() != null
				&& content.getElement().isOrHasChild(latexItem.getElement())) {
			LayoutUtilW.replace(content, getDefinitionValuePanel(), latexItem);

		}
		// maybe it's possible to enter something which is non-LaTeX
		if (success) {
			doUpdateEnsureNoEditor();
		}
	}

	protected boolean typeChanged() {
		return (isSliderItem() != getItemFactory().matchSlider(geo))
				|| (isCheckBoxItem() != ItemFactory.matchCheckbox(geo));
	}

	/**
	 * @return AV item factory
	 */
	protected ItemFactory getItemFactory() {
		return getAV().getItemFactory();
	}

	private void cancelDV() {
		// LayoutUtilW.replace(ihtml, definitionPanel, latexItem);
		doUpdateEnsureNoEditor();
	}

	protected void clearErrorLabel() {
		commandError = null;
		errorMessage = null;
	}

	/**
	 * @param valid
	 *            whether this is for valid string (false = last valid substring
	 *            used)
	 * @param allowSliders
	 *            whether to allow sliders at all
	 * @param withSliders
	 *            whether to allow slider creation without asking
	 * @return error handler
	 */
	protected final ErrorHandler getErrorHandler(final boolean valid,
			final boolean allowSliders, final boolean withSliders) {
		clearErrorLabel();
		return app.getCurrentActivity()
						.createAVErrorHandler(this, valid, allowSliders, withSliders);
	}

	/**
	 * Mark geo as erroneous; show warning icon
	 */
	protected void saveError() {
		if (geo != null) {
			geo.setUndefined();
			geo.updateRepaint();
			updateAfterRedefine(true);
			if (marblePanel != null) {
				marblePanel.updateIcons(true);
			}
		}
	}

	/**
	 * Show error in a popup
	 *
	 * @return whether there is error to be shown
	 */
	boolean showCurrentError() {
		if (commandError != null) {
			Element snackbar = DOM.getElementById("snackbarID");
			if (snackbar == null) {
				app.getToolTipManager().setBlockToolTip(false);
				ToolTip toolTip = new ToolTip(errorMessage, null, "Help",
						app.getGuiManager().getHelpURL(Help.COMMAND, commandError),
						ToolTip.Role.ALERT);
				app.getToolTipManager().showBottomInfoToolTip(toolTip, app,
						ComponentSnackbar.DEFAULT_TOOLTIP_DURATION);
				app.getToolTipManager().setBlockToolTip(true);
			}
			return true;
		}

		if (errorMessage != null) {
			if (app.isUnbundled() && app.getCurrentActivity().useValidInput()) {
				return false;
			}
			app.getToolTipManager().showBottomMessage(errorMessage, app, ToolTip.Role.ALERT);
			return true;

		}
		return false;
	}

	void hideCurrentError() {
		app.getToolTipManager().hideTooltip();
	}

	/**
	 * @param show
	 *            whether to show input help
	 */
	public void setShowInputHelpPanel(boolean show) {
		if (show) {
			if (!av.isEditItem()) {
				ensureEditing();
			}

			removeDummy();

			if (isInputTreeItem()) {
				getController().setFocus(true);
			}
			InputBarHelpPanelW helpPanel = app
					.getGuiManager().getInputHelpPanel();

			if (helpPopup == null) {
				helpPopup = new InputBarHelpPopup(this.app, this,
						"helpPopupAV");
				helpPopup.addAutoHidePartner(this.getElement());
				helpPopup.addCloseHandler(event -> focusAfterHelpClosed());
			} else if (helpPopup.getWidget() == null) {
				helpPanel = app.getGuiManager()
						.getInputHelpPanel();
				helpPopup.add(helpPanel);
			}

			updateHelpPosition(helpPanel);

		} else if (helpPopup != null) {
			helpPopup.hide();
		}
	}

	boolean styleBarCanHide() {
		if (app.isUnbundled()
				|| !getAlgebraDockPanel().isStyleBarPanelShown()) {
			return false;
		}
		int itemTop = this.isInputTreeItem()
				? main.getElement().getAbsoluteTop()
				: getElement().getAbsoluteTop();
		return itemTop - getAlgebraDockPanel().getAbsoluteTop() < 35;
	}

	/**
	 * @return width in case editor is open
	 */
	protected int getWidthForEdit() {
		if (definitionPanel != null && definitionPanel.getOffsetWidth() > 0) {
			return marblePanel.getOffsetWidth()
					+ definitionPanel.getOffsetWidth()
					+ controls.getOffsetWidth() + DEFINITION_ROW_EDIT_MARGIN
					+ MARGIN_RESIZE;
		} else if (geo != null) {
			return content.getOffsetWidth();
		} else {
			return 0;
		}
	}

	public FlowPanel getContent() {
		return this.content;
	}

	/**
	 * Update position of buttons
	 */
	protected void updateButtonPanelPosition() {
		if (controls == null) {
			return;
		}

		controls.reposition();
	}

	/**
	 * @return algebra view
	 */
	protected AlgebraViewW getAV() {
		return av;
	}

	/**
	 * @return corresponding construction element
	 */
	public GeoElement getGeo() {
		return geo;
	}

	/**
	 * Scroll into view.
	 */
	public void scrollIntoView() {
		this.getElement().scrollIntoView();
	}

	/**
	 * Remove the controls.
	 */
	public void removeCloseButton() {
		if (controls != null) {
			controls.setVisible(false);
		}
	}

	protected void addAVEXWidget(Widget w) {
		main.add(w);
	}

	/**
	 * Make this draggable. Works with mouse only.
	 */
	public void setDraggable() {
		Widget draggableContent = main;
		getElement().setAttribute("position", "absolute");
		draggableContent.getElement().setDraggable(Element.DRAGGABLE_TRUE);

		draggableContent.addDomHandler(event -> {
			event.setData("text", geo.getLabelSimple());
			event.getDataTransfer().setDragImage(getElement(), 10, 10);
			getAV().dragStart(geo);
		}, DragStartEvent.getType());
	}

	@Override
	public AppWFull getApplication() {
		return app;
	}

	@Override
	public void ensureEditing() {
		setFocusedStyle(true);
		if (!controller.isEditing()) {
			enterEditMode(geo == null || isMoveablePoint(geo));

			if (av != null && getAV().isNodeTableEmpty()) {
				updateGUIfocus(false);
			}
		}
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
		updateButtonPanelPosition();
	}

	/**
	 * @param selected
	 *            whether this ite is selected
	 */
	public void selectItem(boolean selected) {
		if (controls != null) {
			controls.show(!controller.hasMultiGeosSelected() && selected);
		}

		if (selectedItem == selected) {
			return;
		}
		selectedItem = selected;
		setStyleName("avSelectedRow", selected);
	}

	@Override
	public Widget asWidget() {
		return main.asWidget();
	}

	/**
	 * cast method with no 'instanceof' check.
	 *
	 * @param item
	 *            TreeItem to be cast
	 * @return Cast item to RadioTreeItem
	 */
	public static RadioTreeItem as(TreeItem item) {
		return (RadioTreeItem) item;
	}

	protected AlgebraPanelInterface getAlgebraDockPanel() {
		return (AlgebraPanelInterface) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);

	}

	protected AlgebraPanelInterface getToolbarDockPanel() {
		return (AlgebraPanelInterface) app.getGuiManager().getLayout()
				.getDockManager().getPanel(App.VIEW_ALGEBRA);

	}

	/**
	 * @return whether this is a plaintext item (eg A=(1,1))
	 */
	public FlowPanel getDefinitionValuePanel() {
		return definitionValuePanel;
	}

	private boolean isNeedsUpdate() {
		return needsUpdate;
	}

	/**
	 * Mark the item as (in)valid.
	 *
	 * @param needsUpdate
	 *            whether update is needed
	 */
	public void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}

	protected boolean hasMarblePanel() {
		return marblePanel != null;
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

	@Override
	public AlgebraItemHeader getHelpToggle() {
		return this.marblePanel;
	}

	private void updateIcons(boolean warning) {
		if (this.marblePanel != null) {
			marblePanel.updateIcons(warning);
		}
	}

	protected void updateHelpPosition(final InputBarHelpPanelW helpPanel) {
		helpPopup.setPopupPositionAndShow((offsetWidth, offsetHeight) -> {
			double scale = app.getGeoGebraElement().getScaleX();
			double renderScale = app.getAppletParameters().getDataParamApp()
					? scale : 1;
			helpPopup.getElement().getStyle()
					.setProperty("left",
							(marblePanel.getAbsoluteLeft() - app.getAbsLeft()
									+ marblePanel.getOffsetWidth()) * renderScale
									+ "px");
			int maxOffsetHeight;
			int totalHeight = (int) app.getHeight();
			int toggleButtonTop = (int) ((marblePanel.getAbsoluteTop()
					- (int) app.getAbsTop()) / scale);
			if (toggleButtonTop < totalHeight / 2) {
				int top = toggleButtonTop
						+ marblePanel.getOffsetHeight();
				maxOffsetHeight = totalHeight - top;
				helpPopup.getElement().getStyle().setProperty("top",
						top * renderScale + "px");
				helpPopup.getElement().getStyle().setProperty("bottom",
						"auto");
				helpPopup.removeStyleName("helpPopupAVBottom");
				helpPopup.addStyleName("helpPopupAV");
			} else {
				int minBottom = app.isApplet() ? 0 : 10;
				int bottom = totalHeight - toggleButtonTop;
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
			helpPanel.updateGUI(maxOffsetHeight);
			helpPopup.show();
		});
	}

	protected final void insertHelpToggle() {
		updateIcons(errorMessage != null);
		if (marblePanel == null) {
			this.addMarble();
		}
		main.insert(marblePanel, 0);
	}

	/**
	 * @return whether input text is empty
	 */
	protected boolean isEmpty() {
		return StringUtil.emptyTrim(getText());
	}

	protected void addDummyLabel() {
		if (dummyLabel == null) {
			dummyLabel = BaseWidgetFactory.INSTANCE.newSecondaryText(loc.getMenu("InputLabel")
					+ Unicode.ELLIPSIS, "avDummyLabel");
			ariaLabel = new Label();
			ariaLabel.addStyleName("hidden");
			content.add(ariaLabel);
		}
		clearUndefinedVariables();
		updateFont(dummyLabel);
		if (canvas != null) {
			canvas.setVisible(false);
		}

		content.insert(dummyLabel, 0);
		removeOutput();
		inputControl.hideInputMoreButton();

		if (controls != null) {
			controls.setVisible(true);
		}
		setLatexItemVisible(false);
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

		if (isInputTreeItem()) {
			inputControl.addClearButtonIfSupported();
			if (controls != null) {
				controls.setVisible(true);
			}
			adjustStyleBar();
		}
		setLatexItemVisible(true);
	}

	protected void updateEditorFocus(boolean blurtrue) {
		// deselects current selection
		if (app.isUnbundled()) {
			if (controls != null) {
				updateButtonPanelPosition();
			}

			return;
		}

		getAV().setActiveTreeItem(null);

		boolean emptyCase = getAV().isNodeTableEmpty()
				&& !this.getAlgebraDockPanel().hasLongStyleBar();

		// update style bar icon look
		if (!app.isUnbundled()) {
			if (emptyCase) {
				getAlgebraDockPanel().showStyleBarPanel(blurtrue);
			} else {
				getAlgebraDockPanel().showStyleBarPanel(true);
			}
		}

		// After changing the stylebar visibility, maybe the small stylebar's
		// position will be changed too
		if (controls != null) {
			updateButtonPanelPosition();
		}
	}

	@Override
	public void updatePosition(AbstractSuggestionDisplay sug) {
		sug.setPositionRelativeTo(content);
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	@Override
	public boolean hasFocus() {
		return getMathField() != null && getMathField().hasFocus();
	}

	@Override
	public boolean acceptsCommandInserts() {
		return true;
	}

	/**
	 * @return whether this is a slider item
	 */
	public boolean isSliderItem() {
		return false;
	}

	/**
	 * @return whether this is a checkbox item
	 */
	public boolean isCheckBoxItem() {
		return false;
	}

	/**
	 * @return whether this is a text item
	 */
	public boolean isTextItem() {
		return false;
	}

	/**
	 * @return controller
	 */
	public LatexTreeItemController getController() {
		return controller;
	}

	/**
	 * @param controller
	 *            controller
	 */
	public void setController(LatexTreeItemController controller) {
		this.controller = controller;
	}

	/**
	 * Move the controls to fit new size.
	 */
	public void reposition() {
		if (controls != null) {
			controls.reposition();
		}
	}

	/**
	 * @return whether this belongs to an existing geo
	 */
	public boolean hasGeo() {
		return geo != null;
	}

	void adjustStyleBar() {
		if (app.isUnbundled()) {
			return;
		}

		if (styleBarCanHide() && (!getAlgebraDockPanel().isStyleBarVisible())) {
			stylebarShown = getAlgebraDockPanel().isStyleBarPanelShown();
			getAlgebraDockPanel().showStyleBarPanel(false);
			if (controls != null) {
				controls.reposition();
			}
		}
	}

	@Override
	public void setError(String error) {
		this.errorMessage = error;

		this.commandError = null;

		if (marblePanel != null) {
			marblePanel.updateIcons(error != null);
			if (!NavigatorUtil.isMobile()) {
				marblePanel.setError(error == null ? "" : error);
			}
		}
	}

	@Override
	public void setCommandError(String command) {
		this.commandError = command;
		if (marblePanel != null) {
			marblePanel.updateIcons(true);
		}
	}

	/**
	 * Remove output panel
	 */
	public void removeOutput() {
		if (outputPanel != null) {
			outputPanel.removeFromParent();
		}
	}

	protected LatexTreeItemController createController() {
		return new LatexTreeItemController(this);
	}

	/**
	 *
	 * @return The controller as LatexTreeItemController.
	 */
	public LatexTreeItemController getLatexController() {
		return getController();
	}

	/**
	 * @param text0
	 *            new text
	 * @param showKeyboard
	 *            whether to show keyboard
	 */
	protected void renderLatex(String text0, boolean showKeyboard) {
		content.clear();

		if (!(latexItem == null || isInputTreeItem() || isSliderItem())) {
			latexItem.getElement().getStyle().setProperty("minHeight",
					getController().getEditHeight() + "px");
		}
		ensureCanvas();
		appendCanvas();

		if (!content.isAttached()) {
			main.add(content);
		}

		setText(text0);
		getLatexController().initAndShowKeyboard(showKeyboard);
	}

	private void appendCanvas() {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}
		latexItem.clear();
		if (canvas != null) {
			latexItem.add(canvas);
		}
		content.add(latexItem);
	}

	/**
	 * @return whether canvas was created
	 */
	protected boolean ensureCanvas() {
		if (canvas == null) {
			canvas = Canvas.createIfSupported();
			initMathField();
			return true;
		}
		if (mf == null) {
			initMathField();
		}

		return false;
	}

	private void initMathField() {
		if (latexItem == null) {
			latexItem = new FlowPanel();
		}

		FactoryProviderGWT.ensureLoaded();
		mf = new MathFieldW(new SyntaxAdapterImplWithPaste(kernel), latexItem, canvas,
				getLatexController());
		DataTest.ALGEBRA_INPUT.apply(mf.getInputTextArea());
		mf.setExpressionReader(ScreenReader.getExpressionReader(app));
		updateEditorAriaLabel("");
		mf.setFontSize(getFontSize());
		mf.getInternal().registerMathFieldInternalListener(syntaxController);
		mf.setPixelRatio(app.getPixelRatio());
		mf.setScale(app.getGeoGebraElement().getScaleX());
		mf.setOnBlur((blurEvent) -> {
			if (toast != null) {
				toast.hide();
			}
			controller.onBlur(blurEvent);
		});
		mf.setOnFocus(focusEvent -> setFocusedStyle(true));
	}

	private void updateEditorAriaLabel(String text) {
		if (mf != null) {
			if (!StringUtil.emptyTrim(text)) {
				String label = ScreenReader.getAriaExpression(app, mf.getFormula(),
						ariaPreview);
				if (StringUtil.empty(label)) {
					label = mf.getDescription();
				}
				mf.setAriaLabel(label);
			} else {
				mf.setAriaLabel(loc.getMenu("EnterExpression"));
			}
		}
	}

	@Override
	public void setFocus(boolean focus) {
		if (focus) {
			if (app.isUnbundled()) {
				app.hideMenu();
			}
			removeDummy();

			content.addStyleName("scrollableTextBox");
			if (isInputTreeItem()) {
				MinMaxPanel.closeMinMaxPanel();
				getAV().restoreWidth(true);
				setFocusedStyle(true);
			}
		} else {
			if (isInputTreeItem()) {
				setItemWidth(getAV().getFullWidth());
			} else {
				content.removeStyleName("scrollableTextBox");
			}
		}

		if (ensureCanvas()) {
			main.clear();
			main.add(this.marblePanel);

			if (isInputTreeItem()) {
				appendCanvas();
			}
			main.add(content);
			if (controls != null) {
				main.add(controls);
				updateButtonPanelPosition();
			}
		}

		if (focus) {
			preventBlur();
			canvas.setVisible(true);
		} else {
			if (geo == null && errorMessage == null && getText().isEmpty()) {
				addDummyLabel();
			}
		}
		mf.setFocus(focus);

		int kH = (int) (app.getAppletFrame().getKeyboardHeight());
		int h = getToolbarDockPanel().getOffsetHeight();
		if (h < kH) {
			app.adjustViews(true, false);
		}
	}

	@Override
	public String getText() {
		if (mf == null) {
			return "";
		}
		return mf.getText();
	}

	@Override
	public void setText(String text) {
		if (!"".equals(text)) {
			removeDummy();
		}
		if (mf != null) {
			if (isTextItem()) {
				mf.getInternal().setPlainText(text);
			} else {
				mf.parse(text);
			}
		}
		inputControl.ensureInputMoreMenu();
		updateEditorAriaLabel(text);
		updatePreview();
	}

	@Override
	public void setLabels() {
		if (dummyLabel != null) {
			dummyLabel.setText(loc.getMenu("InputLabel") + Unicode.ELLIPSIS);
		}
		if (hasMarblePanel()) {
			marblePanel.setLabels();
			marblePanel.updateIcons(errorMessage != null);
		}
		if (controls != null) {
			controls.setLabels();
		}
		updateEditorAriaLabel(getText());
	}

	@Override
	public String getCommand() {
		return controller.getCommand(mf);
	}

	@Override
	public void autocomplete(String text) {
		getLatexController().autocomplete(text);
	}

	protected void focusAfterHelpClosed() {
		getController().setFocus(true);
	}

	/**
	 * Update after key was typed
	 */
	public void onKeyTyped() {
		this.removeDummy();
		inputControl.ensureInputMoreMenu();
		updatePreview();
		popupSuggestions();
		onCursorMove();
		if (mf != null) {
			updateEditorAriaLabel(getText());
		}
	}

	/**
	 * Cursor listener
	 */
	public void onCursorMove() {
		getMathField().scrollParentHorizontally(latexItem);
	}

	/**
	 * Show suggestions.
	 */
	public void popupSuggestions() {
		int left = getPopupSuggestionLeft();
		int top = (int) (marblePanel.getAbsoluteTop() - app.getAbsTop());
		getInputSuggestions().popupSuggestions(left, top, marblePanel.getOffsetHeight());
	}

	private int getPopupSuggestionLeft() {
		double scaleX = app.getGeoGebraElement().getScaleX();
		return (int) ((marblePanel.getAbsoluteLeft() - app.getAbsLeft()) / scaleX)
				+ marblePanel.getOffsetWidth();
	}

	/**
	 * @return suggestions model
	 */
	AutoCompletePopup getInputSuggestions() {
		return getLatexController().getAutocompletePopup();
	}

	private void updatePreview() {
		if (getController().isInputAsText()) {
			return;
		}
		String text = getText();
		app.getKernel().getInputPreviewHelper().updatePreviewFromInputBar(text,
				AlgebraInputW.getWarningHandler(this, app));
	}

	/**
	 * @return copy of this item
	 */
	public RadioTreeItem copy() {
		return new RadioTreeItem(geo);
	}

	@Override
	public void insertString(String text) {
		new MathFieldProcessing(mf).autocomplete(
				app.getParserFunctions().toEditorAutocomplete(text, loc));
	}

	@Override
	public void updateSyntaxTooltip(SyntaxHint sh) {
		if (!sh.isEmpty()) {
			String hintHtml = sh.getPrefix() + "<strong>"
					+ sh.getActivePlaceholder() + "</strong>" + sh.getSuffix();

			int leftAVCell = (int) (marblePanel.getAbsoluteLeft() - app.getAbsLeft()
					+ marblePanel.getOffsetWidth());
			int topAVCell = (int) (marblePanel.getAbsoluteTop() - app.getAbsTop());
			int bottomAVCell = topAVCell + marblePanel.getOffsetHeight();
			if (toast == null) {
				toast = new ComponentToast(app, hintHtml);
				toast.show(leftAVCell, topAVCell, bottomAVCell,
						getItemWidth() - marblePanel.getOffsetWidth());
			} else {
				toast.updateContent(hintHtml);
				if (!toast.isShowing()) {
					toast.show(leftAVCell, topAVCell, bottomAVCell,
							getItemWidth() - marblePanel.getOffsetWidth());
				}
			}
		} else {
			if (toast != null) {
				toast.hide();
			}
		}
	}

	/**
	 * Cancel editing
	 */
	public void cancelEditing() {
		GeoGebraActivity activity = app.getActivity();
		stopEditing(activity.useValidInput() ? null : getText(), null, true);
		updateIcons(this.errorMessage != null);
		app.getActiveEuclidianView().requestFocus();
	}

	protected void renderLatex(String text0, Widget w, boolean isInput) {
		if (!isInput) {
			replaceToCanvas(text0, w);
		}
	}

	protected void updateGUIfocus(boolean blurtrue) {
		if (geo == null) {
			updateEditorFocus(blurtrue);
		}
	}

	@Override
	public boolean getAutoComplete() {
		return true;
	}

	@Override
	public boolean isSuggesting() {
		return getLatexController().isSuggesting();
	}

	/**
	 * Update pixel ratio of the editor.
	 *
	 * @param pixelRatio
	 *            pixel ratio
	 */
	public void setPixelRatio(double pixelRatio) {
		if (mf != null) {
			mf.setPixelRatio(pixelRatio);
			mf.setScale(app.getGeoGebraElement().getScaleX());
			mf.repaint();
		}
	}

	protected void updateAfterRedefine(boolean success) {
		if (mf != null) {
			mf.setEnabled(false);
		}
		doUpdateAfterRedefine(success);
		if (ariaLabel != null) {
			ariaLabel.getElement().focus();
		}
	}

	/**
	 *
	 * @return if the item is the input or not.
	 */
	public boolean isInputTreeItem() {
		return getAV().getInputTreeItem() == this;
	}

	/**
	 * @return math field
	 */
	public MathFieldW getMathField() {
		return mf;
	}

	/**
	 * Start editing.
	 *
	 * @return whether editng is possible
	 */
	public boolean onEditStart() {
		String text;
		if (geo == null) {
			text = getText();
		} else if (AlgebraItem.needsPacking(geo)) {
			text = geo.getLaTeXDescriptionRHS(false,
					StringTemplate.editorTemplate);
		} else {
			text = geo.getDefinitionForEditor();
		}

		if (geo != null && (!geo.isDefined() || !useValidInput()) && lastInput != null) {
			text = lastInput;
		}
		if (text == null) {
			return false;
		}

		inputControl.hideInputMoreButton();
		prepareEdit(text);
		getMathField().requestViewFocus();
		// canvas.addBlurHandler(getLatexController());
		CancelEventTimer.keyboardSetVisible();
		ClickStartHandler.init(main, new ClickStartHandler(false, false) {
			@Override
			public void onClickStart(int x, int y,
					final PointerEventType type) {
				getLatexController().setOnScreenKeyboardTextField();
			}
		});

		return true;
	}

	void prepareEdit(String text) {
		clearErrorLabel();
		removeDummy();
		renderLatex(text, true);
		if (outputPanel != null && !isInputTreeItem()
				&& app.getSettings().getAlgebra().getStyle() == AlgebraStyle.DEFINITION_AND_VALUE) {
			definitionValuePanel.clear();
			definitionValuePanel.add(outputPanel);
			content.add(definitionValuePanel);
		}
	}

	/**
	 * Move caret to click position.
	 *
	 * @param x
	 *            caret x in canvas
	 * @param y
	 *            caret y in canvas
	 */
	public void adjustCaret(int x, int y) {
		if (mf != null) {
			mf.adjustCaret(x, y);
		}
	}

	/**
	 * Update font size of editor
	 */
	public final void updateFonts() {
		if (mf != null) {
			mf.setFontSize(getFontSize());
		}

		if (dummyLabel != null) {
			updateFont(dummyLabel);
		}
	}

	/**
	 * @return LaTeX content of editor
	 */
	protected String getEditorLatex() {
		return mf == null ? null
				: TeXSerializer.serialize(mf.getFormula().getRootComponent());
	}

	protected void doUpdate() {
		if (mf != null) {
			mf.setEnabled(false);
		}
		setNeedsUpdate(false);
		if (typeChanged()) {
			av.remove(geo);
			getAV().add(geo, -1, false);
			return;
		}
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		if (controls != null) {
			controls.updateAnimPanel();
		}

		if (!isInputTreeItem() && mayNeedOutput()) {
			buildItemContent();
		} else {
			updateTextItems();
		}

		if (definitionValuePanel != null) {
			updateFont(definitionValuePanel);
		}
		updateDataTest(getIndex());

	}

	public void preventBlur() {
		getController().preventBlur();
	}

	/**
	 * Switches editor to text mode
	 *
	 * @param plainTextMode
	 *            switches editor to text mode
	 */
	protected void onInputModeChange(boolean plainTextMode) {
		mf.setPlainTextMode(plainTextMode);
	}

	/**
	 * Initialize LaTeX input
	 *
	 * @return this
	 */
	public RadioTreeItem initInput() {
		insertHelpToggle();

		content.addStyleName("scrollableTextBox");
		content.addStyleName("noPreview");
		renderLatex("", false);
		new FocusableWidget(AccessibilityGroup.ALGEBRA_ITEM, null, content) {
			@Override
			public void focus(Widget widget) {
				setFocus(true);
			}
		}.attachTo(app);
		getHelpToggle().setIndex(1);
		inputControl.addInputControls();
		return this;
	}

	/**
	 * @return width of the whole item
	 */
	public int getItemWidth() {
		return geo == null ? main.getOffsetWidth() : getOffsetWidth();
	}

	@Override
	public void setUndefinedVariables(String vars) {
		WarningErrorHandler.setUndefinedValiables(vars);
	}

	public void clearUndefinedVariables() {
		WarningErrorHandler.setUndefinedValiables(null);
	}

	public void onStopEdit() {
		// for slider only
	}

	/**
	 * Open settings context menu for this item.
	 */
	public void openMoreMenu() {
		controls.openMoreMenu();
	}

	/**
	 * Focuses the first element of the settings context menu
	 */
	public void focusFirstMoreMenuElement() {
		if (!hasMoreMenu()) {
			return;
		}
		controls.focusFirstMoreMenuElement();
	}

	public boolean hasMoreMenu() {
		return inputControl.hasMoreMenu();
	}

	/**
	 * @return Height of the editor
	 */
	public int getEditHeight() {
		int outputHeight = outputPanel == null ? 0 : outputPanel.getOffsetHeight();
		return getOffsetHeight() - outputHeight;
	}

	/**
	 * set the focused style for inputbar
	 * @param focused - true if editing started
	 */
	public void setFocusedStyle(boolean focused) {
		if (isInputTreeItem()) {
			if (focused) {
				getWidget().getElement().getParentElement().addClassName("focused");
			} else {
				getWidget().getElement().getParentElement().removeClassName("focused");
			}
		}
	}

	/**
	 * if empty input bar remove cursor, put help text,
	 * and disable focus
	 */
	public void resetInputBarOnBlur() {
		if (isEmpty() && isInputTreeItem()) {
			addDummyLabel();
		}
		setFocusedStyle(false);
	}

	/**
	 * Insert string (like autocomplete) but with math support
	 * @param string math content
	 */
	public void insertMath(String string) {
		if (mf != null) {
			mf.getInternal().convertAndInsert(string);
		}
	}

	protected void updateDataTest() {
		updateDataTest(index);
	}

	@Override
	public void updateDataTest(int index) {
		marblePanel.setIndex(index);
		DataTest.ALGEBRA_ITEM_SYMBOLIC_BUTTON.applyWithIndex(symbolicButton, index);
		DataTest.ALGEBRA_OUTPUT_ROW.applyWithIndex(outputPanel, index);

		if (isInputTreeItem()) {
			DataTest.ALGEBRA_INPUT.apply(content);
		}

		if (controls != null) {
			controls.updateDataTest(index);
		}

	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public AlgebraOutputPanel getOutputPanel() {
		return this.outputPanel;
	}

	public boolean isLatex() {
		return this.latex;
	}
}
