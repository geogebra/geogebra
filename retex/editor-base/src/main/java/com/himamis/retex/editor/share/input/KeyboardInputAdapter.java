package com.himamis.retex.editor.share.input;

import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.adapter.FunctionAdapter;
import com.himamis.retex.editor.share.input.adapter.FunctionsAdapter;
import com.himamis.retex.editor.share.input.adapter.KeyboardAdapter;
import com.himamis.retex.editor.share.input.adapter.StringCharAdapter;
import com.himamis.retex.editor.share.input.adapter.StringInput;

import java.util.ArrayList;
import java.util.List;

public class KeyboardInputAdapter {

    private static final char e = '\u212f';
    private static final char divide = '\u00F7';
    private static final char times = '\u00D7';
    private static final char minus = '\u2212';
    private static final List<KeyboardAdapter> adapters;
    private static final KeyboardAdapter commandAdapter;
    private static final String[] allowedCharacterCategories = {
            Character.DECIMAL_DIGIT_NUMBER,
            Character.OTHER_NUMBER,
            Character.LOWERCASE_LETTER,
            Character.UPPERCASE_LETTER,
            Character.OTHER_LETTER,
            Character.OTHER_PUNCTUATION,
            Character.START_PUNCTUATION,
            Character.END_PUNCTUATION,
            Character.MATH_SYMBOL,
            Character.CONNECTOR_PUNCTUATION,
            Character.SPACE_SEPARATOR,
            Character.LETTER_NUMBER,
            Character.DASH_PUNCTUATION
    };

    static {
        adapters = new ArrayList();
        adapters.add(new FunctionsAdapter());
        adapters.add(new StringCharAdapter(divide, '/'));
        adapters.add(new StringCharAdapter(times, '*'));
        adapters.add(new StringCharAdapter(minus, '-'));
        adapters.add(new FunctionAdapter("|", "abs"));
        adapters.add(new FunctionAdapter("log_{10}", "log10"));
        adapters.add(new FunctionAdapter("random"));
        adapters.add(new FunctionAdapter("nroot"));
        adapters.add(new StringInput() {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, '^');
                typeCharacter(mfi, '2');
                mfi.getCursorController().nextCharacter(mfi.getEditorState());
            }

            @Override
            public boolean test(String input) {
                return input.equals("\u00B2");
            }
        });
        adapters.add(new StringInput() {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                commitFunction(mfi, "sqrt");
            }

            @Override
            public boolean test(String input) {
                return input.equals("\u221A");
            }
        });
        adapters.add(new StringInput() {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, e);
                typeCharacter(mfi, '^');
            }

            @Override
            public boolean test(String input) {
                return (e + "^").equals(input);
            }
        });

        // Let through all one length characters!
        adapters.add(new StringInput() {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, input.charAt(0));
            }

            @Override
            public boolean test(String input) {
                return input.length() == 1;
            }
        });

        commandAdapter = new KeyboardAdapter() {
            @Override
            public void commit(MathFieldInternal mfi, String commandName) {

                EditorState editorState = mfi.getEditorState();
                InputController inputController = mfi.getInputController();
                for (int i = 0; i < commandName.length(); i++) {
                    inputController.newCharacter(editorState, commandName.charAt(i));
                }
                inputController.newBraces(editorState, '(');

            }

            @Override
            public boolean test(String input) {
                return true;
            }
        };

    }

    public static void onKeyboardInput(MathFieldInternal mathFieldInternal, String input) {
        if (input == null) {
            return;
        }

        int i = 0;
        while (i < adapters.size() && !adapters.get(i).test(input)) i++;

        if (i < adapters.size()) {
            KeyboardAdapter adapter = adapters.get(i);
            adapter.commit(mathFieldInternal, input);
            mathFieldInternal.update();
        } else {
            // No adapter found for input
        }
    }

    public static void onCommandInput(MathFieldInternal mathFieldInternal, String commandName) {
        commandAdapter.commit(mathFieldInternal, commandName);
        mathFieldInternal.update();
    }

    /**
     * @param c
     * @return true if this char can be handled
     */
    static final public boolean isValidChar(char c) {
        for (int i = 0; i < allowedCharacterCategories.length; i++) {
            if (Character.charIsTypeOf(c, allowedCharacterCategories[i])) {
                return true;
            }
        }

        // needs check:
        // ENCLOSING_MARK, CURRENCY_SYMBOL, OTHER_SYMBOL, INITIAL_QUOTE_PUNCTUATION, FINAL_QUOTE_PUNCTUATION

        return false;
    }

}
