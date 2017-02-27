package org.geogebra.keyboard.base.linear.impl;

import org.geogebra.keyboard.base.linear.Row;
import org.geogebra.keyboard.base.linear.WeightedButton;

import java.util.ArrayList;
import java.util.List;

public class RowImpl implements Row {

    private float rowWeightSum;

    private List<WeightedButton> buttons = new ArrayList<>();

    public RowImpl(float rowWeightSum) {
        this.rowWeightSum = rowWeightSum;
    }

    public void addButton(WeightedButton button) {
        buttons.add(button);
    }

    @Override
    public float getRowWeightSum() {
        return rowWeightSum;
    }

    @Override
    public List<WeightedButton> getButtons() {
        return buttons;
    }
}
