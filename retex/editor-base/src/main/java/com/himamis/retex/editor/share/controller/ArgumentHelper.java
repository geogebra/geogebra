package com.himamis.retex.editor.share.controller;

import com.himamis.retex.editor.share.model.MathArray;
import com.himamis.retex.editor.share.model.MathCharacter;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFunction;
import com.himamis.retex.editor.share.model.MathSequence;

public class ArgumentHelper {

    public void passArgument(EditorState editorState, MathContainer container) {
        MathSequence currentField = editorState.getCurrentField();
        int currentOffset = editorState.getCurrentOffset();
        // get pass to argument
        MathSequence field = (MathSequence) container.getArgument(container.getInsertIndex());

        // pass scripts first
        while (currentOffset > 0 && currentField.isScript(currentOffset - 1)) {
            MathFunction character = (MathFunction) currentField.getArgument(currentOffset - 1);
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
            field.addArgument(0, character);
        }
        editorState.setCurrentOffset(currentOffset);

        if (currentOffset > 0) {
            // if previous sequence argument are braces pass their content
            if (currentField.getArgument(currentOffset - 1) instanceof MathArray) {

                MathArray array = (MathArray) currentField.getArgument(currentOffset - 1);
                currentField.delArgument(currentOffset - 1);
                currentOffset--;
                if (field.size() == 0) {
                    // here we already have sequence, just set it
                    container.setArgument(container.getInsertIndex(), array.getArgument(0));
                } else {
                    field.addArgument(0, array);
                }

                // if previous sequence argument is, function pass it
            } else if (currentField.getArgument(currentOffset - 1) instanceof MathFunction) {

                MathFunction function = (MathFunction) currentField.getArgument(currentOffset - 1);
                currentField.delArgument(currentOffset - 1);
                currentOffset--;
                field.addArgument(0, function);

                // otherwise pass character sequence
            } else {

                passCharacters(editorState, container);
                currentOffset = editorState.getCurrentOffset();
            }
        }
        editorState.setCurrentOffset(currentOffset);
    }

    private void passCharacters(EditorState editorState, MathContainer container) {
        int currentOffset = editorState.getCurrentOffset();
        MathSequence currentField = editorState.getCurrentField();
        // get pass to argument
        MathSequence field = (MathSequence) container.getArgument(container.getInsertIndex());

        while (currentOffset > 0 && currentField.getArgument(currentOffset - 1) instanceof MathCharacter) {

            MathCharacter character = (MathCharacter) currentField.getArgument(currentOffset - 1);
            if (character.isOperator()) {
                break;
            }
            currentField.delArgument(currentOffset - 1);
            currentOffset--;
            field.addArgument(0, character);
        }
        editorState.setCurrentOffset(currentOffset);
    }

    public String readCharacters(EditorState editorState) {
        StringBuilder stringBuilder = new StringBuilder();
        int offset = editorState.getCurrentOffset();
        MathSequence currentField = editorState.getCurrentField();
        while (offset > 0 && currentField.getArgument(offset - 1) instanceof MathCharacter) {

            MathCharacter character = (MathCharacter) currentField.getArgument(offset - 1);
            if (character.isOperator() || character.isSymbol()) {
                break;
            }
            offset--;
            stringBuilder.insert(0, character.getName());
        }
        return stringBuilder.toString();
    }
}
