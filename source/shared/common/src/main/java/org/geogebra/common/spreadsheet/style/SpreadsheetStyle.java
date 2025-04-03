package org.geogebra.common.spreadsheet.style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.MulticastEvent;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public final class SpreadsheetStyle {

	public enum FontTrait {
		BOLD, ITALIC
	}

	public enum TextAlignment {
		DEFAULT, LEFT, CENTERED, RIGHT
	}

	public static final GColor SPREADSHEET_ERROR_BORDER = GColor.newColorRGB(0xB00020);

	/**
	 * Observability (notifications about style changes).
	 * The event payload is the list of affected ranges.
	 */
	public final MulticastEvent<List<TabularRange>> stylingApplied = new MulticastEvent<>();

	private final CellFormat format;
	private boolean showGrid = true;

	public SpreadsheetStyle(CellFormat format) {
		this.format = format;
	}

	private void notifyStylingApplied(@Nonnull List<TabularRange> ranges) {
		stylingApplied.notifyListeners(ranges);
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
		format.setFormat(ranges, CellFormat.FORMAT_FONTSTYLE, cellFormat);
		notifyStylingApplied(ranges);
	}

	// Text alignment

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
	 * Set the text alignemnt for a list of ranges.
	 */
	public void setTextAlignment(@Nonnull TextAlignment textAlignment,
			@Nonnull  List<TabularRange> ranges) {
		Integer cellFormat = cellFormatFromTextAlignment(textAlignment);
		format.setFormat(ranges, CellFormat.FORMAT_ALIGN, cellFormat);
		notifyStylingApplied(ranges);
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
	public GColor getTextColor(int row, int column, @Nullable GColor fallback) {
		GColor textColor = (GColor) format.getCellFormat(column, row, CellFormat.FORMAT_FGCOLOR);
		return textColor == null ? fallback : textColor;
	}

	/**
	 * Set the text color for a range of cells.
	 * @param color text color
	 * @param ranges list of ranges
	 */
	public void setTextColor(GColor color, List<TabularRange> ranges) {
		format.setFormat(ranges, CellFormat.FORMAT_FGCOLOR, color);
		notifyStylingApplied(ranges);
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
		format.setFormat(ranges, CellFormat.FORMAT_BGCOLOR, color);
		notifyStylingApplied(ranges);
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

	private TextAlignment textAlignmentFromCellFormat(@Nullable Integer cellFormat) {
		if (cellFormat == null) {
			return TextAlignment.DEFAULT;
		}
		switch (cellFormat) {
		case CellFormat.ALIGN_LEFT:
			return TextAlignment.LEFT;
		case CellFormat.ALIGN_CENTER:
			return TextAlignment.CENTERED;
		case CellFormat.ALIGN_RIGHT:
			return TextAlignment.RIGHT;
		default:
			return TextAlignment.DEFAULT;
		}
	}

	private Integer cellFormatFromTextAlignment(@Nonnull TextAlignment textAlignment) {
		switch (textAlignment) {
		case LEFT:
			return CellFormat.ALIGN_LEFT;
		case CENTERED:
			return CellFormat.ALIGN_CENTER;
		case RIGHT:
			return CellFormat.ALIGN_RIGHT;
		case DEFAULT:
			break;
		}
		return null;
	}

	private Set<FontTrait> fontTraitsFromCellFormat(@Nullable Integer cellFormat) {
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

	private Integer cellFormatFromFontTraits(Set<FontTrait> traits) {
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
