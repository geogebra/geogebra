package org.geogebra.common.spreadsheet.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyle;
import org.geogebra.common.util.MulticastEvent;

import com.google.j2objc.annotations.Property;

/**
 * An observable model for the spreadsheet style bar.
 *
 * @apiNote Depending on the type of client (mobile, Web) and screen orientation (portrait,
 * landscape), clients may hide the style bar entirely (see APPS-6439 for details).
 * This functionality (hiding the style bar) is not provided here and needs to be handled
 * on the client side. Everything else should be available from here.
 */
@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public final class SpreadsheetStyleBarModel {

	/**
	 * The style bar's current state.
	 */
	public static class State {
		static final State DISABLED = new State(false, null,
				SpreadsheetStyle.TextAlignment.DEFAULT, null, null, null, null);

		/** @return true if at least one UI element in the style bar is enabled */
		@Property("readonly")
		public final boolean isEnabled;
		/** Font traits of the selected cell. An empty set means default font style. */
		@Property("readonly")
		public final @Nonnull Set<SpreadsheetStyle.FontTrait> fontTraits;
		/** Text alignment of the selected cell. */
		@Property("readonly")
		public final @Nonnull SpreadsheetStyle.TextAlignment textAlignment;
		/** Background color of the selected cell. */
		@Property("readonly")
		public final @Nullable GColor backgroundColor;
		@Property("readonly")
		public final @Nullable GColor textColor;
		final @Nullable List<Selection> selections;
		final @Nullable SpreadsheetCoords firstCell;

		State(boolean isEnabled,
				@Nullable Set<SpreadsheetStyle.FontTrait> fontTraits,
				@Nullable SpreadsheetStyle.TextAlignment textAlignment,
				@Nullable GColor backgroundColor,
				@Nullable GColor textColor,
				@Nullable List<Selection> selections,
				@Nullable SpreadsheetCoords firstCell) {
			this.isEnabled = isEnabled;
			this.fontTraits = fontTraits != null
					? fontTraits : Set.of();
			this.textAlignment = textAlignment != null
					? textAlignment : SpreadsheetStyle.TextAlignment.DEFAULT;
			this.backgroundColor = backgroundColor;
			this.textColor = textColor;
			this.selections = selections != null ? new ArrayList<>(selections) : null;
			this.firstCell = firstCell;
		}

		/**
		 * Modify the current font traits.
		 * @param adding Pass {@code true} to add {@code fontTrait}, {@code false} to remove.
		 * @param fontTrait The trait to add or remove.
		 * @return A copy of the current font traits, modified by adding or removing the given
		 * trait.
		 */
		private Set<SpreadsheetStyle.FontTrait> modifyingFontTraits(boolean adding,
				SpreadsheetStyle.FontTrait fontTrait) {
			Set<SpreadsheetStyle.FontTrait> traits = new HashSet<>(fontTraits);
			if (adding) {
				traits.add(fontTrait);
			} else {
				traits.remove(fontTrait);
			}
			return traits;
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof State)) {
				return false;
			}
			State other = (State) object;
			return isEnabled == other.isEnabled
					&& Objects.equals(fontTraits, other.fontTraits)
					&& textAlignment == other.textAlignment
					&& Objects.equals(backgroundColor, other.backgroundColor)
					&& Objects.equals(textColor, other.textColor)
					&& Objects.equals(selections, other.selections)
					&& Objects.equals(firstCell, other.firstCell);
		}

		@Override
		public int hashCode() {
			return Objects.hash(isEnabled, fontTraits, textAlignment, backgroundColor, textColor,
					selections, firstCell);
		}
	}

	/**
	 * Observability (notifications about state changes).
	 * The event payload is the current state of the style bar.
	 */
	@Property("readonly")
	public final MulticastEvent<SpreadsheetStyleBarModel.State> stateChanged;

	/** Needed only for {@link SpreadsheetController#getVisibleSelections()}. */
	private final SpreadsheetController spreadsheetController;
	/** Get the current selection, and listen to selection changes. */
	private final SpreadsheetSelectionController selectionController;
	/** Spreadsheet (cell) styling API and backing store. */
	private final SpreadsheetStyle style;
	/** The current state. */
	private State state;

	SpreadsheetStyleBarModel(
			@Nonnull SpreadsheetController spreadsheetController,
			@Nonnull SpreadsheetSelectionController selectionController,
			@Nonnull SpreadsheetStyle style) {
		this.spreadsheetController = spreadsheetController;
		this.selectionController = selectionController;
		this.style = style;
		style.stylingApplied.addListener(this::stylingApplied);
		state = State.DISABLED;
		stateChanged = new MulticastEvent<>();
		selectionController.selectionsChanged.addListener(this::selectionsChanged);
	}

	/**
	 * @return The current state.
	 */
	public @Nonnull State getState() {
		return state;
	}

	// Font traits

	/**
	 * Add or remove the BOLD font trait to the selected range.
	 * @param bold Pass {@code true} to add the font trait, {@code false} to remove.
	 */
	public void setBold(boolean bold) {
		Set<SpreadsheetStyle.FontTrait> newTraits = state.modifyingFontTraits(bold,
				SpreadsheetStyle.FontTrait.BOLD);
		style.setFontTraits(newTraits, getSelectedRanges());
	}

	/**
	 * Add or remove the ITALIC font trait to the selected range.
	 * @param italic Pass {@code true} to add the font trait, {@code false} to remove.
	 */
	public void setItalic(boolean italic) {
		Set<SpreadsheetStyle.FontTrait> newTraits = state.modifyingFontTraits(italic,
				SpreadsheetStyle.FontTrait.ITALIC);
		style.setFontTraits(newTraits, getSelectedRanges());
	}

	// Text alignment

	/**
	 * Set the text alignment for the selected range.
	 * @param alignment Text alignment.
	 */
	public void setTextAlignment(@Nonnull SpreadsheetStyle.TextAlignment alignment) {
		style.setTextAlignment(alignment, getSelectedRanges());
	}

	// Text & cell colors

	/**
	 * Set the cell text (foreground) color.
	 * @param textColor Cell text color. Pass {@code null} to clear use the default text color.
	 */
	public void setTextColor(GColor textColor) {
		style.setTextColor(textColor, getSelectedRanges());
	}

	/**
	 * Set the cell background color.
	 * @param backgroundColor Cell background color. Pass {@code null} to clear any active
	 * background color.
	 */
	public void setBackgroundColor(@Nullable GColor backgroundColor) {
		style.setBackgroundColor(backgroundColor, getSelectedRanges());
	}

	// Change notification

	private void stylingApplied(List<TabularRange> unused) {
		updateStateAndNotifyChanged();
	}

	private void selectionsChanged(List<Selection> unused) {
		updateStateAndNotifyChanged();
	}

	private void updateStateAndNotifyChanged() {
		State previousState = state;
		state = getSelectionState();
		if (state.equals(previousState)) {
			return;
		}
		spreadsheetController.storeUndoInfo(); // only create undo point if there's a state change
		stateChanged.notifyListeners(state);
	}

	private State getSelectionState() {
		SpreadsheetCoords firstCell = spreadsheetController.getLastSelectionUpperLeftCell();
		if (firstCell == null) {
			return State.DISABLED;
		}
		int row = firstCell.row;
		int column = firstCell.column;
		Set<SpreadsheetStyle.FontTrait> fontTraits = style.getFontTraits(row, column);
		SpreadsheetStyle.TextAlignment textAlignment = style.getTextAlignment(row, column);
		GColor backgroundColor = style.getBackgroundColor(row, column, null);
		GColor textColor = style.getTextColor(row, column, null);
		return new State(true, fontTraits, textAlignment, backgroundColor, textColor,
				selectionController.getSelectionsCopy(), firstCell);
	}

	// Utils

	private List<TabularRange> getSelectedRanges() {
		return spreadsheetController.getVisibleSelections();
	}
}
