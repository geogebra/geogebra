package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Handles copy/paste of {@link TabularData} when the content of the cell
 * is a {@link GeoElement}.
 */
public class TabularDataPasteGeos implements TabularDataPasteInterface<GeoElement> {

	private CopyPasteCellOperationList operations = new CopyPasteCellOperationList();

	/**
	 * Copy and paste geos ensuring that the creation order of the new, pasted geos
	 * will be the same as the copied ones.
	 */
	@Override
	public void pasteInternal(TabularData<GeoElement> tabularData,
			TabularClipboard<GeoElement> clipboard, TabularRange destination) {
		collectOperations(clipboard, destination);
		operations.sort();
		operations.apply(clipboard, tabularData);
	}

	private void collectOperations(TabularClipboard<GeoElement> buffer, TabularRange destination) {
		operations.clear();
		TabularRange source = buffer.getSource();
		for (int col = source.fromCol; col <= source.toCol; ++col) {
			int bufferCol = col - source.fromCol;
			for (int row = source.fromRow; row <= source.toRow; ++row) {
				int bufferRow = row - source.fromRow;

				// check if we're pasting back into what we're copying from
				if (bufferCol + destination.fromCol <= destination.toCol
						&& bufferRow + destination.fromRow <= destination.toRow
						&& (!isInSource(col, row, source, destination))) {

					GeoElement geo = buffer.contentAt(bufferRow, bufferCol);
					if (geo != null) {
						operations.add(geo.getConstructionIndex(), bufferRow, bufferCol,
								destination.fromRow + bufferRow,
								destination.fromCol + bufferCol);
					}
				}
			}
		}
	}

	private static boolean isInSource(int col, int row, TabularRange source,
			TabularRange destination) {
		return col + (destination.fromCol - source.fromCol) <= source.toCol
				&& col + (destination.fromCol - source.fromCol) >= source.fromCol && row + (
				destination.fromRow - source.fromRow) <= source.toRow
				&& row + (destination.fromRow - source.fromRow) >= source.fromRow;
	}
}
