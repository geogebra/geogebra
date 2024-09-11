package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.view.spreadsheet.CopyPasteAdapter;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Handles copy/paste of {@link TabularData} when the content of the cell
 * is a {@link GeoElement}.
 */
public final class TabularDataPasteGeos implements TabularDataPasteInterface<GeoElement> {

	private final RelativeCopy relativeCopy;
	private final App app;
	CopyPasteAdapter adapter;

	/**
	 * @param kernel Needed for {@link RelativeCopy}
	 */
	public TabularDataPasteGeos(Kernel kernel) {
		this.relativeCopy = new RelativeCopy(kernel);
		this.app = kernel.getApplication();
		adapter = new CopyPasteAdapter(app, null);
	}

	/**
	 * Copy and paste geos ensuring that the creation order of the new, pasted geos
	 * will be the same as the copied ones.
	 */
	@Override
	public void pasteInternal(TabularData<GeoElement> tabularData,
			TabularClipboard<GeoElement> clipboard, TabularRange destination) {
		CopyPasteCellOperationList operations = collectOperations(clipboard, destination);
		operations.sort();
		operations.apply(tabularData);
	}

	@Override
	public void pasteExternal(TabularData<GeoElement> tabularData, String[][] data,
			TabularRange destination) {
		adapter.pasteExternalMultiple(data, destination);
	}

	private CopyPasteCellOperationList collectOperations(TabularClipboard<GeoElement> buffer,
			TabularRange destination) {
		CopyPasteCellOperationList operations = new CopyPasteCellOperationList();
		TabularRange source = buffer.getSourceRange();
		for (int col = source.getFromColumn(); col <= source.getToColumn(); ++col) {
			int bufferCol = col - source.getFromColumn();
			for (int row = source.getFromRow(); row <= source.getToRow(); ++row) {
				int bufferRow = row - source.getFromRow();

				int destinationRow = destination.getFromRow() + bufferRow;
				int destinationColumn = destination.getFromColumn() + bufferCol;

				if (bufferCol + destination.getFromColumn() <= destination.getToColumn()
						&& bufferRow + destination.getFromRow() <= destination.getToRow()) {

					try {
						GeoElement geo = (GeoElement) relativeCopy.doCopyNoStoringUndoInfo0(
								buffer.contentAt(bufferRow, bufferCol),
								RelativeCopy.getValue(app, destinationColumn, destinationRow),
								destination.getFromColumn() - source.getFromColumn(),
								destination.getFromRow() - source.getFromRow());
						if (geo != null) {
							operations.add(geo, destinationRow, destinationColumn);
						}
					} catch (CircularDefinitionException | ParseException e) {
						Log.error(e);
					}
				}
			}
		}
		return operations;
	}
}
