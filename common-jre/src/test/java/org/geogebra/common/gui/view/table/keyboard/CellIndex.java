package org.geogebra.common.gui.view.table.keyboard;

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
}