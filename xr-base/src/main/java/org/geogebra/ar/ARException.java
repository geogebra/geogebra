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
