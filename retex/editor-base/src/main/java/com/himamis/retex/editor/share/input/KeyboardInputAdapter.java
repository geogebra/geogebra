package com.himamis.retex.editor.share.input;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.adapter.FunctionAdapter;
import com.himamis.retex.editor.share.input.adapter.FunctionsAdapter;
import com.himamis.retex.editor.share.input.adapter.KeyboardAdapter;
import com.himamis.retex.editor.share.input.adapter.StringCharAdapter;
import com.himamis.retex.editor.share.input.adapter.StringInput;
import com.himamis.retex.editor.share.util.Unicode;

public class KeyboardInputAdapter {

	protected static final char e = Unicode.EULER_CHAR;
	private static final char divide = Unicode.DIVIDE;
	private static final char times = Unicode.MULTIPLY;
	private static final char minus = Unicode.MINUS;
    private static final List<KeyboardAdapter> adapters;
    private static final KeyboardAdapter commandAdapter;
    /*private static final String[] allowedCharacterCategories = {
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
    };*/

    static {
		adapters = new ArrayList<>();
        adapters.add(new FunctionsAdapter());
        adapters.add(new StringCharAdapter(divide, '/'));
        adapters.add(new StringCharAdapter(times, '*'));
        adapters.add(new StringCharAdapter(minus, '-'));
        adapters.add(new FunctionAdapter("|", "abs"));
        adapters.add(new FunctionAdapter("log_{10}", "log10"));
        adapters.add(new FunctionAdapter("random"));
        adapters.add(new FunctionAdapter("nroot"));
		adapters.add(new StringInput(Unicode.SUPERSCRIPT_2 + "") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
				if (mfi.getInputController().getCreateFrac()) {
					typeCharacter(mfi, '^');
					typeCharacter(mfi, '2');
					CursorController.nextCharacter(mfi.getEditorState());
				} else {
					typeCharacter(mfi, Unicode.SUPERSCRIPT_2);
				}
            }
        });
		adapters.add(new StringInput(Unicode.SQUARE_ROOT + "") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                commitFunction(mfi, "sqrt");
            }
        });
        adapters.add(new StringInput(e + "^") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, e);
                typeCharacter(mfi, '^');
            }
        });
        adapters.add(new StringInput("logb") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, 'l');
                typeCharacter(mfi, 'o');
                typeCharacter(mfi, 'g');
                typeCharacter(mfi, '_');
                CursorController.nextCharacter(mfi.getEditorState());
                typeCharacter(mfi, '(');
                mfi.getCursorController().prevCharacter(mfi.getEditorState());
                mfi.getCursorController().prevCharacter(mfi.getEditorState());
            }
        });
        adapters.add(new StringInput("10^") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, '1');
                typeCharacter(mfi, '0');
                typeCharacter(mfi, '^');
            }
        });
        adapters.add(new StringInput("a_n") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, '_');
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
                return Character.areLettersOrDigits(input);
            }
        };

        adapters.add(commandAdapter);
    }

	public static void insertString(final MathFieldInternal mMathFieldInternal,
			String text) {
		boolean oldCreateFrac = mMathFieldInternal.getInputController()
				.getCreateFrac();
		mMathFieldInternal.getInputController().setCreateFrac(false);
        for (int i = 0; i < text.length(); i++) {
			KeyboardInputAdapter.onKeyboardInput(mMathFieldInternal,
					text.charAt(i) + "");
		}
		mMathFieldInternal.getInputController().setCreateFrac(oldCreateFrac);
		mMathFieldInternal.onInsertString();
	}

    public static void onKeyboardInput(MathFieldInternal mathFieldInternal, String input) {
        if (input == null) {
            return;
        }

        int i = 0;
        while (i < adapters.size() && !adapters.get(i).test(input)) {
			i++;
		}

        if (i < adapters.size()) {
            KeyboardAdapter adapter = adapters.get(i);
            adapter.commit(mathFieldInternal, input);
            mathFieldInternal.update();
		}
    }

    public static void onCommandInput(MathFieldInternal mathFieldInternal, String commandName) {
        commandAdapter.commit(mathFieldInternal, commandName);
        mathFieldInternal.update();
    }

}
