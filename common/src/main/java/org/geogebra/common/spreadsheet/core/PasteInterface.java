package org.geogebra.common.spreadsheet.core;

public interface PasteInterface<T> {
void pasteInternal(TabularData<T> tabularData, TabularBuffer<T> buffer, TabularRange destination);
}
