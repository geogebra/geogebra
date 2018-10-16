package org.geogebra.common.kernel;

/**
 * App State that saves the state in a String.
 */
public class StringAppState implements AppState {

    private String xml;

    /**
     * Construct an App State based on a string.
     *
     * @param xml string App State
     */
    public StringAppState(String xml) {
        this.xml = xml;
    }

    @Override
    public String getXml() {
        return xml;
    }

    @Override
    public void delete() {
        xml = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringAppState that = (StringAppState) o;

        return xml != null ? xml.equals(that.xml) : that.xml == null;
    }

    @Override
    public int hashCode() {
        return xml != null ? xml.hashCode() : 0;
    }
}
