package org.geogebra.common.spreadsheet.style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.MulticastEvent;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public final class SpreadsheetStyle {

	/** Fallback alignment when {@link CellFormat} has no information regarding alignment. */
	public static final Integer DEFAULT_CELL_ALIGNMENT = CellFormat.ALIGN_RIGHT;

	public enum FontTrait {
		BOLD, ITALIC
	}

	public enum TextAlignment {
		LEFT, CENTERED, RIGHT
	}

	public static final GColor SPREADSHEET_ERROR_BORDER = GColor.newColorRGB(0xB00020);

	/**
	 * Observability (notifications about style changes).
	 * The event payload is the list of affected ranges.
	 */
	public final MulticastEvent<List<TabularRange>> stylingChanged = new MulticastEvent<>();

	private final CellFormat format;
	private boolean showGrid = true;

	public SpreadsheetStyle() {
		this.format = new CellFormat(null);
	}

	CellFormat getFormat() {
		return format;
	}

	private void notifyStylingChanged(@Nonnull List<TabularRange> ranges) {
		stylingChanged.notifyListeners(ranges);
	}

	// Grid & borders

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public GColor getGridColor() {
		return GeoGebraColorConstants.NEUTRAL_300;
	}

	public GColor getErrorGridColor() {
		return SPREADSHEET_ERROR_BORDER;
	}

	/**
	 * @param row cell row
	 * @param column cell column
	 * @return whether to show border for given cell
	 */
	public boolean showBorder(int row, int column) {
		Byte border = (Byte) format.getCellFormat(column, row, CellFormat.FORMAT_BORDER);
		return border != null && border != 0;
	}

	// Font style (traits)

	/**
	 * @param row cell row
	 * @param column cell column
	 * @return font style of given cell (see {@link GFont#getStyle()})
	 */
	public Integer getFontStyle(int row, int column) {
		return (Integer) format.getCellFormat(column, row, CellFormat.FORMAT_FONTSTYLE);
	}

	/**
	 * @return font traits of the cell at (row, column)
	 */
	public @Nonnull Set<FontTrait> getFontTraits(int row, int column) {
		Integer fontStyle = getFontStyle(row, column);
		return fontTraitsFromCellFormat(fontStyle);
	}

	/**
	 * Set the font traits for a list of ranges.
	 */
	public void setFontTraits(@Nonnull Set<FontTrait> traits,
			@Nonnull List<TabularRange> ranges) {
		Integer cellFormat = cellFormatFromFontTraits(traits);
		boolean changed = format.setFormat(ranges, CellFormat.FORMAT_FONTSTYLE, cellFormat);
		if (changed) {
			notifyStylingChanged(ranges);
		}
	}

	// Text alignment

	/**
	 * Returns text alignment for the cell at (row, column).
	 * @return one of the CellFormat.ALIGN_* constants
	 */
	public Integer getAlignment(int row, int column) {
		return (Integer) format.getCellFormat(column, row, CellFormat.FORMAT_ALIGN);
	}

	/**
	 * @return the text alignment for the cell at (row, column)
	 */
	public @Nonnull TextAlignment getTextAlignment(int row, int column) {
		Integer alignment = getAlignment(row, column);
		return textAlignmentFromCellFormat(alignment);
	}

	/**
	 * Set the text alignment for a list of ranges.
	 */
	public void setTextAlignment(@Nonnull TextAlignment textAlignment,
			@Nonnull List<TabularRange> ranges) {
		Integer cellFormat = cellFormatFromTextAlignment(textAlignment);
		boolean changed = format.setFormat(ranges, CellFormat.FORMAT_ALIGN, cellFormat);
		if (changed) {
			notifyStylingChanged(ranges);
		}
	}

	// Text color

	public GColor getDefaultTextColor() {
		return GeoGebraColorConstants.NEUTRAL_900;
	}

	/**
	 * Get the text color of a cell.
	 * @param row row of cell
	 * @param column column of cell
	 * @param fallback fallback value to return (can be null)
	 * @return The cell's text color if non-null, or the fallback color otherwise.
	 */
	public GColor getTextColor(int row, int column, @CheckForNull GColor fallback) {
		GColor textColor = (GColor) format.getCellFormat(column, row, CellFormat.FORMAT_FGCOLOR);
		return textColor == null ? fallback : textColor;
	}

	/**
	 * Set the text color for a range of cells.
	 * @param color text color
	 * @param ranges list of ranges
	 */
	public void setTextColor(GColor color, List<TabularRange> ranges) {
		boolean changed = format.setFormat(ranges, CellFormat.FORMAT_FGCOLOR, color);
		if (changed) {
			notifyStylingChanged(ranges);
		}
	}

	// Cell & header colors

	/**
	 * @param row cell row
	 * @param column cell column
	 * @param fallback color to use if no background set
	 * @return background color of given cell
	 */
	public GColor getBackgroundColor(int row, int column, GColor fallback) {
		GColor bgColor = (GColor) format.getCellFormat(column, row, CellFormat.FORMAT_BGCOLOR);
		return bgColor == null ? fallback : bgColor;
	}

	/**
	 * Set the background color for a range of cells.
	 * @param color background color
	 * @param ranges list of ranges
	 */
	public void setBackgroundColor(GColor color, List<TabularRange> ranges) {
		boolean changed = format.setFormat(ranges, CellFormat.FORMAT_BGCOLOR, color);
		if (changed) {
			notifyStylingChanged(ranges);
		}
	}

	public GColor getHeaderBackgroundColor() {
		return GeoGebraColorConstants.NEUTRAL_200;
	}

	// Selection colors

	public GColor getSelectionColor() {
		return GeoGebraColorConstants.PURPLE_100;
	}

	public GColor getSelectionBorderColor() {
		return GeoGebraColorConstants.PURPLE_600;
	}

	public GColor getDashedSelectionBorderColor() {
		return GeoGebraColorConstants.NEUTRAL_700;
	}

	public GColor getSelectionHeaderColor() {
		return GeoGebraColorConstants.NEUTRAL_700;
	}

	public GColor getSelectedTextColor() {
		return GColor.WHITE;
	}

	// Format conversion utils

	/**
	 * Converts {@link CellFormat} {@code ALIGN_*} fields to {@link TextAlignment} values.
	 * @param cellFormat one of {@link CellFormat} alignment fields
	 * @return TextAlignment
	 */
	@Nonnull
	public static TextAlignment textAlignmentFromCellFormat(@CheckForNull Integer cellFormat) {
		switch (cellFormat != null ? cellFormat : DEFAULT_CELL_ALIGNMENT) {
		case CellFormat.ALIGN_LEFT:
			return TextAlignment.LEFT;
		case CellFormat.ALIGN_CENTER:
			return TextAlignment.CENTERED;
		case CellFormat.ALIGN_RIGHT:
		default:
			return TextAlignment.RIGHT;
		}
	}

	private static Integer cellFormatFromTextAlignment(@Nonnull TextAlignment textAlignment) {
		switch (textAlignment) {
		case LEFT:
			return CellFormat.ALIGN_LEFT;
		case CENTERED:
			return CellFormat.ALIGN_CENTER;
		case RIGHT:
			return CellFormat.ALIGN_RIGHT;
		}
		return null;
	}

	private static Set<FontTrait> fontTraitsFromCellFormat(@CheckForNull Integer cellFormat) {
		Set<FontTrait> traits = new HashSet<>();
		if (cellFormat != null) {
			switch (cellFormat) {
			case CellFormat.STYLE_BOLD:
				traits.add(FontTrait.BOLD);
				break;
			case CellFormat.STYLE_ITALIC:
				traits.add(FontTrait.ITALIC);
				break;
			case CellFormat.STYLE_BOLD_ITALIC:
				traits.add(FontTrait.BOLD);
				traits.add(FontTrait.ITALIC);
				break;
			default:
				break;
			}
		}
		return traits;
	}

	private static Integer cellFormatFromFontTraits(Set<FontTrait> traits) {
		boolean bold = traits.contains(FontTrait.BOLD);
		boolean italic = traits.contains(FontTrait.ITALIC);
		int cellFormat = CellFormat.STYLE_PLAIN;
		if (bold && italic) {
			cellFormat = CellFormat.STYLE_BOLD_ITALIC;
		} else if (bold) {
			cellFormat = CellFormat.STYLE_BOLD;
		} else if (italic) {
			cellFormat = CellFormat.STYLE_ITALIC;
		}
		return cellFormat;
	}
}
