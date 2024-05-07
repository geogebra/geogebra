package org.geogebra.common.gui.view.table.keyboard;

import java.util.Objects;

final class CellIndex {

    final int row;
    final int column;

    CellIndex(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CellIndex)) {
            return false;
        }
        CellIndex other = (CellIndex) obj;
        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "CellIndex{" + "row=" + row + ", column=" + column + '}';
    }
}