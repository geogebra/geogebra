package org.geogebra.common.spreadsheet.style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.spreadsheet.core.Direction;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.MulticastEvent;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
public final class SpreadsheetStyling {

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
	/**
	 * XML change notification triggered when dimensions change, but styles do not.
	 */
	public final MulticastEvent<String> stylingXmlChanged = new MulticastEvent<>();

	private final CellFormat cellFormat = new CellFormat(null);
	private boolean showGrid = true;

	/**
	 * @param cellFormatXml cell format XML
	 */
	public void setCellFormatXml(@CheckForNull String cellFormatXml) {
		cellFormat.processXMLString(cellFormatXml);
	}

	/**
	 * @return An XML string containing cell format information.
	 */
	public String getCellFormatXml() {
		return cellFormat.encodeFormats();
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
		Byte border = (Byte) cellFormat.getCellFormat(column, row, CellFormat.FORMAT_BORDER);
		return border != null && border != 0;
	}

	// Font style (traits)

	/**
	 * @param row cell row
	 * @param column cell column
	 * @return font style of given cell (see {@link GFont#getStyle()})
	 */
	public Integer getFontStyle(int row, int column) {
		return (Integer) cellFormat.getCellFormat(column, row, CellFormat.FORMAT_FONTSTYLE);
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
		boolean changed = cellFormat.setFormat(ranges, CellFormat.FORMAT_FONTSTYLE,
				cellFormatFromFontTraits(traits));
		if (changed) {
			stylingChanged.notifyListeners(ranges);
		}
	}

	// Text alignment

	/**
	 * Returns text alignment for the cell at (row, column).
	 * @return one of the CellFormat.ALIGN_* constants
	 */
	public Integer getAlignment(int row, int column) {
		return (Integer) cellFormat.getCellFormat(column, row, CellFormat.FORMAT_ALIGN);
	}

	/**
	 * @return the user-defined text alignment for the cell at (row, column), or {@code null}
	 * if no user-defined text alignment has been set for this cell.
	 */
	public @CheckForNull TextAlignment getTextAlignment(int row, int column) {
		Integer alignment = getAlignment(row, column);
		if (alignment == null) {
			return null;
		}
		return textAlignmentFromCellFormat(alignment);
	}

	/**
	 * @param cellContent The content of some spreadsheet cell.
	 * @return The default text alignment to use for the kind of object.
	 */
	public static @Nonnull TextAlignment getDefaultTextAlignment(@CheckForNull Object cellContent) {
		if (cellContent instanceof GeoText || cellContent instanceof String) {
			return TextAlignment.LEFT;
		}
		return TextAlignment.RIGHT;
	}

	/**
	 * Set the text alignment for a list of ranges.
	 */
	public void setTextAlignment(@Nonnull TextAlignment textAlignment,
			@Nonnull List<TabularRange> ranges) {
		boolean changed = cellFormat.setFormat(ranges, CellFormat.FORMAT_ALIGN,
				cellFormatFromTextAlignment(textAlignment));
		if (changed) {
			stylingChanged.notifyListeners(ranges);
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
		GColor textColor = (GColor) cellFormat.getCellFormat(column, row,
				CellFormat.FORMAT_FGCOLOR);
		return textColor == null ? fallback : textColor;
	}

	/**
	 * Set the text color for a range of cells.
	 * @param color text color
	 * @param ranges list of ranges
	 */
	public void setTextColor(GColor color, List<TabularRange> ranges) {
		boolean changed = cellFormat.setFormat(ranges, CellFormat.FORMAT_FGCOLOR, color);
		if (changed) {
			stylingChanged.notifyListeners(ranges);
		}
	}

	// Cell & header colors

	/**
	 * @return default background color
	 */
	public GColor getDefaultBackgroundColor() {
		return GColor.WHITE;
	}

	/**
	 * @param row cell row
	 * @param column cell column
	 * @param fallback color to use if no background set
	 * @return background color of given cell
	 */
	public GColor getBackgroundColor(int row, int column, GColor fallback) {
		GColor bgColor = (GColor) cellFormat.getCellFormat(column, row, CellFormat.FORMAT_BGCOLOR);
		return bgColor == null ? fallback : bgColor;
	}

	/**
	 * Set the background color for a range of cells.
	 * @param color background color
	 * @param ranges list of ranges
	 */
	public void setBackgroundColor(GColor color, List<TabularRange> ranges) {
		boolean changed = cellFormat.setFormat(ranges, CellFormat.FORMAT_BGCOLOR, color);
		if (changed) {
			stylingChanged.notifyListeners(ranges);
		}
	}

	/**
	 * Shift a portion of format properties in the given direction.
	 * The set of rows or columns to be shifted is a block that begins at a
	 * specified start index and includes all larger indices.
	 *
	 * @param startIndex
	 *            Index of the first row or column to shift.
	 * @param shiftAmount
	 *            Number of indices to increment each row or column
	 * @param direction
	 *            the shift direction
	 */
	public void shiftFormat(int startIndex, int shiftAmount,
			Direction direction) {
		cellFormat.shiftFormats(startIndex, shiftAmount, direction);
		stylingXmlChanged.notifyListeners(cellFormat.encodeFormats());
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
	public static @Nonnull TextAlignment textAlignmentFromCellFormat(@Nonnull Integer cellFormat) {
		switch (cellFormat) {
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
