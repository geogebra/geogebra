package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;

import org.geogebra.common.kernel.geos.GeoElement;

public class PasteGeos implements PasteInterface<GeoElement> {

	private CellRecord[] constructionIndexes = null;

	@Override
	public void pasteInternal(TabularData<GeoElement> tabularData, TabularBuffer<GeoElement> buffer,
			TabularRange destination) {
		TabularRange source = buffer.getSource();
		createConstructionIndexBuffer(source);
		int count = storeConstuctionIndexes(buffer, destination, source);
		pasteInConstructionIndexOrder(tabularData, buffer, count);
	}

	private void createConstructionIndexBuffer(TabularRange source) {
		int size = (source.toCol - source.fromCol + 1) * (source.toRow - source.fromRow + 1);
		if (constructionIndexes == null || constructionIndexes.length < size) {
			constructionIndexes = new CellRecord[size];
		}
	}

	private int storeConstuctionIndexes(TabularBuffer<GeoElement> buffer, TabularRange destination,
			TabularRange source) {
		int count = 0;
		for (int col = source.fromCol; col <= source.toCol; ++col) {
			int bufferCol = col - source.fromCol;
			for (int row = source.fromRow; row <= source.toRow; ++row) {
				int bufferRow = row - source.fromRow;

				// check if we're pasting back into what we're copying from

				if (bufferCol + destination.fromCol <= destination.toCol
						&& bufferRow + destination.fromRow <= destination.toRow
						&& (!isInSource(col, row, source, destination))) {
					// check we're not pasting over
					// what we're copying

					GeoElement value = buffer.contentAt(bufferRow, bufferCol);
					if (value != null) {
						constructionIndexes[count] = new CellRecord(
								value.getConstructionIndex(), bufferRow,
								bufferCol, destination.fromRow + bufferRow,
								destination.fromCol + bufferCol);
						count++;
					}
				}
			}
		}
		Arrays.sort(constructionIndexes, 0, count, CellRecord.getComparator());
		return count;
	}

	private static boolean isInSource(int col, int row, TabularRange source,
			TabularRange destination) {
		return col + (destination.fromCol - source.fromCol) <= source.toCol
				&& col + (destination.fromCol - source.fromCol) >= source.fromCol && row + (
				destination.fromRow - source.fromRow) <= source.toRow
				&& row + (destination.fromRow - source.fromRow) >= source.fromRow;
	}

	private void pasteInConstructionIndexOrder(TabularData<GeoElement> tabularData,
			TabularBuffer<GeoElement> buffer, int count) {
		for (int i = 0; i < count; i++) {
			CellRecord r = constructionIndexes[i];
			int row = r.getSourceRow();
			int column = r.getSourceCol();
			GeoElement copy = copyGeo(buffer.contentAt(row, column));
			tabularData.setContent(r.getDestRow(), r.getDestCol(), copy);
		}
	}

	private GeoElement copyGeo(GeoElement value) {
		return value.copy();
	}
}
