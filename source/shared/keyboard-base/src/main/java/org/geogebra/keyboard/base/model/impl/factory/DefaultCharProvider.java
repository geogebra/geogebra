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
