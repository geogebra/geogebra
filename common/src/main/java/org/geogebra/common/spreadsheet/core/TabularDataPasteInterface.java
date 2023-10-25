package org.geogebra.common.spreadsheet.core;

public interface TabularDataPasteInterface<T> {
void pasteInternal(TabularData<T> tabularData, TabularBuffer<T> buffer, TabularRange destination);
}
