package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.style.SpreadsheetStyling.DEFAULT_CELL_ALIGNMENT;
import static org.geogebra.common.spreadsheet.style.SpreadsheetStyling.textAlignmentFromCellFormat;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.MulticastEvent;

import com.google.j2objc.annotations.Property;

/**
 * An observable model for the spreadsheet style bar.
 * @apiNote Depending on the type of client (mobile, Web) and screen orientation (portrait,
 * landscape), clients may hide the style bar entirely (see APPS-6439 for details).
 * This functionality (hiding the style bar) is not provided here and needs to be handled
 * on the client side. Everything else should be available from here.
 */
@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public final class SpreadsheetStyleBarModel {

	private static final SpreadsheetStyling.TextAlignment DEFAULT_TEXT_ALIGNMENT =
			textAlignmentFromCellFormat(DEFAULT_CELL_ALIGNMENT);

	/**
	 * The style bar's current state.
	 */
	public static class State {
		static final State DISABLED = new State(false, null,
				DEFAULT_TEXT_ALIGNMENT, null, null);

		/** Is true if at least one UI element in the style bar is enabled */
		@Property("readonly")
		public final boolean isEnabled;
		/** Font traits of the selected cell. An empty set means default font style. */
		@Property("readonly")
		public final @Nonnull Set<SpreadsheetStyling.FontTrait> fontTraits;
		/** Text alignment of the selected cell. */
		@Property("readonly")
		public final @Nonnull SpreadsheetStyling.TextAlignment textAlignment;
		/** Background color of the selected cell. */
		@Property("readonly")
		public final @CheckForNull GColor backgroundColor;
		@Property("readonly")
		public final @CheckForNull GColor textColor;

		State(boolean isEnabled,
				@CheckForNull Set<SpreadsheetStyling.FontTrait> fontTraits,
				@CheckForNull SpreadsheetStyling.TextAlignment textAlignment,
				@CheckForNull GColor backgroundColor,
				@CheckForNull GColor textColor) {
			this.isEnabled = isEnabled;
			this.fontTraits = fontTraits != null
					? fontTraits : Set.of();
			this.textAlignment = textAlignment != null ? textAlignment : DEFAULT_TEXT_ALIGNMENT;
			this.backgroundColor = backgroundColor;
			this.textColor = textColor;
		}

		/**
		 * Modify the current font traits.
		 * @param adding Pass {@code true} to add {@code fontTrait}, {@code false} to remove.
		 * @param fontTrait The trait to add or remove.
		 * @return A copy of the current font traits, modified by adding or removing the given
		 * trait.
		 */
		private Set<SpreadsheetStyling.FontTrait> modifyingFontTraits(boolean adding,
				SpreadsheetStyling.FontTrait fontTrait) {
			Set<SpreadsheetStyling.FontTrait> traits = new HashSet<>(fontTraits);
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
					&& Objects.equals(textColor, other.textColor);
		}

		@Override
		public int hashCode() {
			return Objects.hash(isEnabled, fontTraits, textAlignment, backgroundColor, textColor);
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
	/** Spreadsheet (cell) styling API and backing store. */
	private final SpreadsheetStyling styling;
	/** The current state. */
	private State state;

	SpreadsheetStyleBarModel(
			@Nonnull SpreadsheetController spreadsheetController,
			@Nonnull SpreadsheetSelectionController selectionController,
			@Nonnull SpreadsheetStyling styling) {
		this.spreadsheetController = spreadsheetController;
		this.styling = styling;
		styling.stylingChanged.addListener(this::stylingChanged);
		styling.stylingXmlChanged.addListener(unused -> updateStateAndNotifyChanged());
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
		Set<SpreadsheetStyling.FontTrait> newTraits = state.modifyingFontTraits(bold,
				SpreadsheetStyling.FontTrait.BOLD);
		styling.setFontTraits(newTraits, getSelectedRanges());
	}

	/**
	 * Add or remove the ITALIC font trait to the selected range.
	 * @param italic Pass {@code true} to add the font trait, {@code false} to remove.
	 */
	public void setItalic(boolean italic) {
		Set<SpreadsheetStyling.FontTrait> newTraits = state.modifyingFontTraits(italic,
				SpreadsheetStyling.FontTrait.ITALIC);
		styling.setFontTraits(newTraits, getSelectedRanges());
	}

	// Text alignment

	/**
	 * Set the text alignment for the selected range.
	 * @param alignment Text alignment.
	 */
	public void setTextAlignment(@Nonnull SpreadsheetStyling.TextAlignment alignment) {
		styling.setTextAlignment(alignment, getSelectedRanges());
	}

	// Text & cell colors

	/**
	 * Set the cell text (foreground) color.
	 * @param textColor Cell text color. Pass {@code null} to clear use the default text color.
	 */
	public void setTextColor(GColor textColor) {
		styling.setTextColor(textColor, getSelectedRanges());
	}

	/**
	 * Set the cell background color.
	 * @param backgroundColor Cell background color. Pass {@code null} to clear any active
	 * background color.
	 */
	public void setBackgroundColor(@CheckForNull GColor backgroundColor) {
		styling.setBackgroundColor(backgroundColor, getSelectedRanges());
	}

	// Change notification

	private void stylingChanged(List<TabularRange> unused) {
		updateStateAndNotifyChanged();
	}

	private void selectionsChanged(MulticastEvent.Void unused) {
		updateStateAndNotifyChanged();
	}

	private void updateStateAndNotifyChanged() {
		State previousState = state;
		state = getSelectionState();
		if (state.equals(previousState)) {
			return;
		}
		stateChanged.notifyListeners(state);
	}

	private State getSelectionState() {
		SpreadsheetCoords firstCell = spreadsheetController.getLastSelectionUpperLeftCell();
		if (firstCell == null) {
			return State.DISABLED;
		}
		int row = firstCell.row;
		int column = firstCell.column;
		Set<SpreadsheetStyling.FontTrait> fontTraits = styling.getFontTraits(row, column);
		SpreadsheetStyling.TextAlignment textAlignment = styling.getTextAlignment(row, column);
		if (textAlignment == null) {
			Object content = spreadsheetController.contentAt(row, column);
			textAlignment = SpreadsheetStyling.getDefaultTextAlignment(content);
		}
		GColor backgroundColor = styling.getBackgroundColor(row, column,
				styling.getDefaultBackgroundColor());
		GColor textColor = styling.getTextColor(row, column, styling.getDefaultTextColor());
		return new State(true, fontTraits, textAlignment, backgroundColor, textColor);
	}

	// Utils

	private List<TabularRange> getSelectedRanges() {
		return spreadsheetController.getVisibleSelections();
	}
}
