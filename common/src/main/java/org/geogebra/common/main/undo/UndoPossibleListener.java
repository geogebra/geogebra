package org.geogebra.common.main.undo;

/**
 * Listener for undo - actions
 */
public interface UndoPossibleListener {
    /**
     * @param isPossible {@code true} if undo is possible, {@code false} otherwise
     */
    void undoPossible(boolean isPossible);

    /**
     * @param isPossible {@code true} if redo is possible, {@code false} otherwise
     */
    void redoPossible(boolean isPossible);
}
