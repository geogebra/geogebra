package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Handles copy/paste of {@link TabularData} when the content of the cell
 * is a {@link GeoElement}.
 */
public final class TabularDataPasteGeos implements TabularDataPasteInterface<GeoElement> {

	/**
	 * Copy and paste geos ensuring that the creation order of the new, pasted geos
	 * will be the same as the copied ones.
	 */
	@Override
	public void pasteInternal(TabularData<GeoElement> tabularData,
			TabularClipboard<GeoElement> clipboard, TabularRange destination) {
		CopyPasteCellOperationList operations = collectOperations(clipboard, destination);
		operations.sort();
		operations.apply(clipboard, tabularData);
	}

	@Override
	public void pasteExternal(TabularData<GeoElement> tabularData, ClipboardInterface clipboard,
			TabularRange destination) {
		// TODO
	}

	private static CopyPasteCellOperationList collectOperations(TabularClipboard<GeoElement> buffer,
			TabularRange destination) {
		CopyPasteCellOperationList operations = new CopyPasteCellOperationList();
		TabularRange source = buffer.getSourceRange();
		for (int col = source.getFromColumn(); col <= source.getToColumn(); ++col) {
			int bufferCol = col - source.getFromColumn();
			for (int row = source.getFromRow(); row <= source.getToRow(); ++row) {
				int bufferRow = row - source.getFromRow();

				if (bufferCol + destination.getFromColumn() <= destination.getToColumn()
						&& bufferRow + destination.getFromRow() <= destination.getToRow()) {

					GeoElement geo = buffer.contentAt(bufferRow, bufferCol);
					if (geo != null) {
						operations.add(geo.getConstructionIndex(), bufferRow, bufferCol,
								destination.getFromRow() + bufferRow,
								destination.getFromColumn() + bufferCol);
					}
				}
			}
		}
		return operations;
	}
}
