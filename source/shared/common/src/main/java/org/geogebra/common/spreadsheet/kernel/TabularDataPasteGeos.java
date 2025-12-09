/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.gui.view.spreadsheet.CopyPasteAdapter;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.TabularClipboard;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataPasteInterface;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;

/**
 * Handles copy/paste of {@link TabularData} when the content of the cell
 * is a {@link GeoElement}.
 */
final class TabularDataPasteGeos implements TabularDataPasteInterface<GeoElement> {

	private final RelativeCopy relativeCopy;
	private final App app;
	CopyPasteAdapter adapter;

	/**
	 * @param kernel Needed for {@link RelativeCopy}
	 */
	TabularDataPasteGeos(Kernel kernel) {
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
