package org.geogebra.keyboard.base.model.impl.factory;

public class DefaultCharProvider implements CharacterProvider {

    @Override
    public String xForButton() {
        return Characters.x;
    }

    @Override
    public String xAsInput() {
        return Characters.BASIC_X;
    }

    @Override
    public String yForButton() {
        return Characters.y;
    }

    @Override
    public String yAsInput() {
        return Characters.BASIC_Y;
    }

    @Override
    public String zForButton() {
        return Characters.z;
    }

    @Override
    public String zAsInput() {
        return Characters.BASIC_Z;
    }

    @Override
    public String eulerForButton() {
        return Characters.CURLY_EULER;
    }

    @Override
    public String eulerAsInput() {
        return Characters.EULER;
    }

    @Override
    public String piForButton() {
        return Characters.CURLY_PI;
    }

    @Override
    public String piAsInput() {
        return Characters.PI;
    }
}
