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

package org.geogebra.ar;

import org.geogebra.common.main.Localization;

public class ARException extends Exception {

    public enum Type {UPDATE, RESTART, INSTALL}
    public enum Source {APP, AR, CAMERA}

    private Type type;
    private Source source;

    public ARException(String message, Type type, Source source, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.source = source;
    }

    public ARException(String message, Type type, Source source) {
        super(message);
        this.type = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Source getSource() {
        return source;
    }

    public String getLocalizedMessage(Localization localization) {
        return localization.getMenu(getMessage());
    }

}
