package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.MayHaveFocus;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.spreadsheet.core.ClipboardInterface;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.ActionableItem;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.Divider;
import org.geogebra.common.spreadsheet.core.ContextMenuItem.SubMenuItem;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellDataSerializer;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.MathFieldEditor;
import org.geogebra.web.full.gui.inputfield.AutoCompletePopup;
import org.geogebra.web.full.gui.view.algebra.ToastController;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.util.ClipboardW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.style.shared.TextAlign;
import org.gwtproject.dom.style.shared.Unit;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.syntax.SyntaxController;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

public class SpreadsheetControlsDelegateW implements SpreadsheetControlsDelegate, AutoCompleteW {

	private final SpreadsheetCellEditorW editor;
	private final GPopupMenuW contextMenu;
	private final Localization loc;
	private final static int CONTEXT_MENU_PADDING = 8;
	private final static int MARGIN_FROM_SCREEN_EDGE = 16;
	private final ClipboardInterface clipboard;
	private final SpreadsheetPanel parent;
	private AutoCompletePopup autocomplete;

	private static class SpreadsheetCellEditorW implements SpreadsheetCellEditor {
		private final MathFieldEditor mathField;
		private final SpreadsheetPanel parent;
		private final AppW app;
		private final ToastController toastController;
		private DefaultSpreadsheetCellProcessor cellProcessor;
		private Rectangle editorBounds;

		public SpreadsheetCellEditorW(AppW app, SpreadsheetPanel parent, MathTextFieldW mathField) {
			this.mathField = mathField;
			this.mathField.getMathField().setForegroundColor(
					GColor.getColorString(GeoGebraColorConstants.NEUTRAL_900));
			mathField.addStyleName("spreadsheetEditor");
			SyntaxController syntaxController = new SyntaxController();
			this.toastController = new ToastController(app, () -> editorBounds);
			syntaxController.setUpdater(toastController);
			getMathField().registerMathFieldInternalListener(syntaxController);
			this.parent = parent;
			this.app = app;
		}

		public SpreadsheetPanel getSpreadsheetPanel() {
			return parent;
		}

		@Override
		public void show(@Nonnull Rectangle editorBounds, @Nonnull Rectangle viewport, int textAlignment) {
			mathField.attach(parent);
			updatePosition(editorBounds, viewport);
			mathField.setRightMargin(8);
			mathField.asWidget().getElement().getStyle().setTextAlign(
					textAlignment == CellFormat.ALIGN_LEFT ? TextAlign.LEFT : TextAlign.RIGHT);
			mathField.setVisible(true);
			mathField.editorClicked();
			Scheduler.get().scheduleDeferred(mathField::requestFocus);
		}

		@Override
		public void updatePosition(@Nonnull Rectangle editorBounds, @Nonnull Rectangle viewport) {
			Rectangle bounds = editorBounds.insetBy(-2, -2);
			double dx = parent.getAbsoluteLeft() - app.getAbsLeft();
			double dy = parent.getAbsoluteTop() - app.getAbsTop();
			this.editorBounds = new Rectangle(editorBounds.getMinX() + dx,
					parent.getOffsetWidth() + dx,
					editorBounds.getMinY() + dy, editorBounds.getMaxY() + dy);
			mathField.getStyle().setLeft(bounds.getMinX(), Unit.PX);
			mathField.getStyle().setTop(bounds.getMinY(), Unit.PX);
			mathField.getStyle().setWidth(bounds.getWidth(), Unit.PX);
			mathField.getStyle().setProperty("minHeight", bounds.getHeight(), Unit.PX);
		}

		@Override
		public void hide() {
			mathField.setVisible(false);
			parent.requestFocus();
			app.hideKeyboard();
			toastController.hide();
		}

		@Override
		public @Nonnull MathFieldInternal getMathField() {
			return mathField.getMathField().getInternal();
		}

		@Override
		public @Nonnull DefaultSpreadsheetCellProcessor getCellProcessor() {
			if (cellProcessor == null) {
				cellProcessor = new DefaultSpreadsheetCellProcessor(
						app.getKernel().getAlgebraProcessor());
			}
			return cellProcessor;
		}

		@Override
		public @Nonnull SpreadsheetCellDataSerializer getCellDataSerializer() {
			return new DefaultSpreadsheetCellDataSerializer();
		}
	}

	/**
	 * Spreadsheet controls delegate
	 * @param app - application
	 * @param parent - parent panel
	 * @param mathTextField - math text field
	 */
	public SpreadsheetControlsDelegateW(AppW app, SpreadsheetPanel parent,
			MathTextFieldW mathTextField) {
		editor = new SpreadsheetCellEditorW(app, parent, mathTextField);
		mathTextField.addBlurHandler(blur -> {
			if (!isSuggesting()) {
				editor.getSpreadsheetPanel().saveContentAndHideCellEditor();
			}
		});
		this.parent = parent;
		contextMenu = new GPopupMenuW(app) {
			@Override
			public void returnFocus(MayHaveFocus anchor) {
				parent.requestFocus();
			}
		};
		loc = app.getLocalization();
		clipboard = new ClipboardW();
	}

	@Override
	public @Nonnull SpreadsheetCellEditor getCellEditor() {
		return editor;
	}

	// CONTEXT MENU

	@Override
	public void showContextMenu(@Nonnull List<ContextMenuItem> items, @Nonnull Point location) {
		contextMenu.clearItems();
		parent.cancelFocus();
		contextMenu.getApp().getAsyncManager().prefetch(null, "scripting", "stats");
		addItems(contextMenu.getPopupMenu(), items);
		positionContextMenu((int) Math.round(location.x), (int) Math.round(location.y));
		contextMenu.getPopupMenu().focus();
	}

	private void addItems(AriaMenuBar popupMenu, List<ContextMenuItem> items) {
		for (ContextMenuItem item : items) {
			if (item instanceof Divider) {
				popupMenu.addSeparator();
			} else if (item instanceof SubMenuItem) {
				popupMenu.addItem(createSubMenuItem((SubMenuItem) item));
			} else if (item instanceof ActionableItem) {
				popupMenu.addItem(createActionableItem((ActionableItem) item));
			}
		}
	}

	private AriaMenuItem createActionableItem(ActionableItem actionableItem) {
		String text = loc.getMenu(actionableItem.getLocalizationKey());
		SVGResource image = getActionIcon(actionableItem.getIdentifier());
		ScheduledCommand scheduledCommand = () -> performAndHideMenu(actionableItem);
		return new AriaMenuItem(text, image, scheduledCommand);
	}

	private AriaMenuItem createSubMenuItem(SubMenuItem subMenuItem) {
		String text = loc.getMenu(subMenuItem.getLocalizationKey());
		SVGResource image = getActionIcon(subMenuItem.getIdentifier());
		AriaMenuBar ariaMenuBar = new AriaMenuBar();
		addItems(ariaMenuBar, subMenuItem.getItems());
		return new AriaMenuItem(text, image, ariaMenuBar);
	}

	private void performAndHideMenu(ActionableItem item) {
		item.performAction();
		hideContextMenu();
	}

	private SVGResource getActionIcon(ContextMenuItem.Identifier action) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		switch (action) {
		case CUT:
			return res.cut_black();
		case COPY:
			return res.copy_black();
		case PASTE:
			return res.paste_black();
		case DELETE:
			return res.delete_black();
		case CALCULATE:
			return res.calculate();
		case CREATE_CHART:
			return res.insert_chart();
		case LINE_CHART:
			return res.table_line_chart();
		case BAR_CHART:
			return res.table_bar_chart();
		case HISTOGRAM:
			return res.table_histogram();
		case PIE_CHART:
			return res.table_pie_chart();
		case INSERT_ROW_ABOVE:
		case INSERT_ROW_BELOW:
		case DELETE_ROW:
		case INSERT_COLUMN_LEFT:
		case INSERT_COLUMN_RIGHT:
		case DELETE_COLUMN:
		default:
			return null;
		}
	}

	private void positionContextMenu(int x, int y) {
		int left = x + getAbsoluteSpreadsheetLeft() - getAbsoluteAppLeft();
		int top = y + getAbsoluteSpreadsheetTop() - getAbsoluteAppTop();

		contextMenu.showAtPoint(0, 0);

		if (!popupFitsHorizontally(left)) {
			left = (int) (contextMenu.getApp().getWidth() - contextMenu.getPopupMenu()
					.getElement().getClientWidth() - MARGIN_FROM_SCREEN_EDGE);
		}

		if (!popupFitsVertically(top)) {
			top = (int) (contextMenu.getApp().getHeight() - contextMenu.getPopupMenu().getElement()
					.getClientHeight() - 2 * CONTEXT_MENU_PADDING - MARGIN_FROM_SCREEN_EDGE);
		}

		contextMenu.showAtPoint(left, top);
	}

	private boolean popupFitsHorizontally(int originalPopupLeft) {
		return contextMenu.getApp().getWidth()
				> originalPopupLeft + contextMenu.getPopupMenu().getElement().getOffsetWidth();
	}

	private boolean popupFitsVertically(int originalPopupTop) {
		return contextMenu.getApp().getHeight()
				> originalPopupTop + contextMenu.getPopupMenu().getElement().getOffsetHeight();
	}

	private int getAbsoluteAppLeft() {
		return (int) getApplication().getAbsLeft();
	}

	private int getAbsoluteSpreadsheetLeft() {
		return editor.getSpreadsheetPanel().getAbsoluteLeft();
	}

	private int getAbsoluteAppTop() {
		return (int) getApplication().getAbsTop();
	}

	private int getAbsoluteSpreadsheetTop() {
		return editor.getSpreadsheetPanel().getAbsoluteTop();
	}

	@Override
	public void hideContextMenu() {
		boolean wasFocused = contextMenu.isMenuShown();
		contextMenu.hideMenu();
		if (wasFocused) {
			parent.requestFocus();
		}
	}

	@Override
	public ClipboardInterface getClipboard() {
		return clipboard;
	}

	// AUTOCOMPLETE

	@Override
	public void showAutoCompleteSuggestions(@Nonnull String input, @Nonnull Rectangle editorBounds) {
		int left = (int) editorBounds.getMinX() + getAbsoluteSpreadsheetLeft()
				- getAbsoluteAppLeft();
		int top = (int) editorBounds.getMinY() + getAbsoluteSpreadsheetTop() - getAbsoluteAppTop();
		int height = (int) editorBounds.getHeight();

		getAutocompletePopup().popupSuggestions(input, left, top, height);
	}

	@Override
	public void hideAutoCompleteSuggestions() {
		getAutocompletePopup().hide();
	}

	@Override
	public boolean isAutoCompleteSuggestionsVisible() {
		return getAutocompletePopup().isSuggesting();
	}

	@Override
	public boolean handleKeyPressForAutoComplete(int keyCode) {
		if (!isSuggesting()) {
			return false;
		}

		switch (keyCode) {
		case JavaKeyCodes.VK_DOWN:
		case JavaKeyCodes.VK_UP:
		case JavaKeyCodes.VK_LEFT:
		case JavaKeyCodes.VK_RIGHT:
			autocomplete.onArrowKeyPressed(keyCode);
			return true;
		case JavaKeyCodes.VK_ENTER:
			autocomplete.handleEnter();
			return true;
		case JavaKeyCodes.VK_ESCAPE:
			hideAutoCompleteSuggestions();
			return true;
		}

		return false;
	}

	/**
	 * @return autocomplete popup (lazy load)
	 */
	private AutoCompletePopup getAutocompletePopup() {
		if (autocomplete == null) {
			autocomplete = new AutoCompletePopup(getApplication(),
					new AutocompleteProvider(getApplication(), false), this);
		}
		return autocomplete;
	}

	@Override
	public boolean getAutoComplete() {
		return true;
	}

	@Override
	public void setFocus(boolean focus) {
		// nothing to do here
	}

	@Override
	public void insertString(String text) {
		editor.getMathField().deleteCurrentWord();
		KeyboardInputAdapter.onKeyboardInput(editor.getMathField(), text);
		editor.mathField.focus();
		autocomplete.hide();
	}

	@Override
	public String getText() {
		return editor.getMathField().getText();
	}

	@Override
	public void setText(String s) {
		editor.getMathField().parse(s);
	}

	@Override
	public void requestFocus() {
		// nothing to do here
	}

	@Override
	public boolean isSuggesting() {
		return autocomplete != null && autocomplete.isSuggesting();
	}

	@Override
	public void autocomplete(String s) {
		//  nothing to do here
	}

	@Override
	public String getCommand() {
		return null; // not needed
	}

	@Override
	public AppW getApplication() {
		return editor.app;
	}

	@Override
	public void showSnackbar(@Nonnull String messageKey) {
		String translatedMessage = loc.getMenu(messageKey);
		getApplication().getToolTipManager().showBottomMessage(translatedMessage,
				getApplication());
	}
}
