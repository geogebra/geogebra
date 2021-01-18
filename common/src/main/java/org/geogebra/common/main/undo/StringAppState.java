package org.geogebra.common.main.undo;

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
    public boolean equalsTo(AppState state) {
        return state != null && xml.equals(state.getXml());
    }
}
