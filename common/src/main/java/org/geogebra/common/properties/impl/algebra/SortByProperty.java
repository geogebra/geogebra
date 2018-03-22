package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class SortByProperty extends AbstractEnumerableProperty {

    private AlgebraView.SortMode[] sortModes = {
            AlgebraView.SortMode.DEPENDENCY,
            AlgebraView.SortMode.TYPE,
            AlgebraView.SortMode.ORDER,
            AlgebraView.SortMode.LAYER
    };

    private AlgebraView algebraView;

    public SortByProperty(AlgebraView algebraView, Localization localization) {
        super(localization, "SortBy");
        this.algebraView = algebraView;
        setupValues();
    }

    private void setupValues() {
        String[] values = new String[sortModes.length];
        for (int i = 0; i < sortModes.length; i++) {
            values[i] = sortModes[i].toString();
        }
        setValuesAndLocalize(values);
    }

    @Override
    public int getCurrent() {
        AlgebraView.SortMode sortMode = algebraView.getTreeMode();
        for (int i = 0; i < sortModes.length; i++) {
            if (sortModes[i] == sortMode) {
                return i;
            }
        }
        return NONE;
    }

    @Override
    protected void setValueSafe(String value, int index) {
        algebraView.setTreeMode(sortModes[index]);
    }
}
