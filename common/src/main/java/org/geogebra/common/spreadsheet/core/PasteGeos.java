package org.geogebra.common.spreadsheet.core;

import java.util.Arrays;

import org.geogebra.common.kernel.geos.GeoElement;

public class PasteGeos implements PasteInterface<GeoElement> {


	private CellRecord[] constructionIndexes = null;

	@Override
	public void pasteInternal(TabularData<GeoElement> tabularData, TabularBuffer<GeoElement> buffer,
			TabularRange destination) {
		TabularRange source = buffer.getSource();
		int x4 = destination.toCol;
		int y4 = destination.toRow;

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
		for (int x = source.fromCol; x <= source.toCol; ++x) {
			int ix = x - source.fromCol;
			for (int y = source.fromRow; y <= source.toRow; ++y) {
				int iy = y - source.fromRow;

				// check if we're pasting back into what we're copying from

				if (ix + destination.fromCol <= destination.toCol
						&& iy + destination.fromRow <= destination.toRow
						&& (!isInSource(x, y, source, destination))) {
					// check we're not pasting over
					// what we're copying

					GeoElement value = buffer.contentAt(ix, iy);
					if (value != null) {
						constructionIndexes[count] = new CellRecord(
								value.getConstructionIndex(), ix,
								iy, destination.fromCol - source.fromCol, destination.fromRow - source.fromRow);
						count++;
					}
				}
			}
		}
		Arrays.sort(constructionIndexes, 0, count, CellRecord.getComparator());
		return count;
	}

	private static boolean isInSource(int x, int y, TabularRange source, TabularRange destination) {
		return x + (destination.fromCol - source.fromCol) <= source.toCol
				&& x + (destination.fromCol - source.fromCol) >= source.fromCol && y + (
				destination.fromRow - source.fromRow) <= source.toRow
				&& y + (destination.fromRow - source.fromRow) >= source.fromRow;
	}

	private void pasteInConstructionIndexOrder(TabularData<GeoElement> tabularData, TabularBuffer<GeoElement> buffer,
			int count) {
		for (int i = 0; i < count; i++) {
			CellRecord r = constructionIndexes[i];
			int ix = r.getx1();
			int iy = r.gety1();
			GeoElement copy = copyGeo(buffer.contentAt(ix, iy));
			tabularData.setContent(r.getx2(), r.getx2(), copy);
		}
	}

	private GeoElement copyGeo(GeoElement geoElement) {
		// TODO
		return geoElement;
	}
}
