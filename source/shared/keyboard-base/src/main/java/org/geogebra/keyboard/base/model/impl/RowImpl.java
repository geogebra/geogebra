package org.geogebra.keyboard.base.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;

public class RowImpl implements Row {

    private List<WeightedButton> buttons = new ArrayList<>();

    public void addButton(WeightedButton button) {
        buttons.add(button);
    }

    @Override
    public float getRowWeightSum() {
        float sumOfButtonWeights = 0;

        for (WeightedButton button : buttons) {
            sumOfButtonWeights += button.getWeight();
        }

        return sumOfButtonWeights;
    }

    @Override
    public List<WeightedButton> getButtons() {
        return buttons;
    }
}
