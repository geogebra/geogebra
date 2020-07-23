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

    static {
		adapters = new ArrayList<>();
        adapters.add(new FunctionsAdapter());
        adapters.add(new StringCharAdapter(times, '*'));
        adapters.add(new StringCharAdapter(minus, '-'));
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
		adapters.add(new StringInput("x^(-1)") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, '^');
                typeCharacter(mfi, '-');
                typeCharacter(mfi, '1');
                CursorController.nextCharacter(mfi.getEditorState());
            }
        });
		adapters.add(new StringInput(divide + "") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                InputController controller = mfi.getInputController();
                boolean createFrac = controller.getCreateFrac();
                controller.setCreateFrac(false);
                typeCharacter(mfi, '/');
                if (createFrac) {
                    mfi.onDivisionInserted();
                }
                controller.setCreateFrac(createFrac);
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
        adapters.add(new StringInput("log_{10}") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, 'l');
                typeCharacter(mfi, 'o');
                typeCharacter(mfi, 'g');
                typeCharacter(mfi, '_');
                typeCharacter(mfi, '1');
                typeCharacter(mfi, '0');
                CursorController.nextCharacter(mfi.getEditorState());
                typeCharacter(mfi, '(');
                CursorController.nextCharacter(mfi.getEditorState());
                mfi.getCursorController().prevCharacter(mfi.getEditorState());
            }
        });
        adapters.add(new StringInput("logb") {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                typeCharacter(mfi, 'l');
                typeCharacter(mfi, 'o');
                typeCharacter(mfi, 'g');
                typeCharacter(mfi, '(');
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

        adapters.add(new StringInput() {
            @Override
            public void commit(MathFieldInternal mfi, String input) {
                String command = input.substring(0, input.length() - Unicode
                        .SUPERSCRIPT_MINUS_ONE_STRING.length());
				getCommandAdapter().commit(mfi, command);
				mfi.getCursorController().prevCharacter(mfi.getEditorState());
                typeCharacter(mfi, '^');
                typeCharacter(mfi, '-');
                typeCharacter(mfi, '1');
                CursorController.nextCharacter(mfi.getEditorState());
                CursorController.nextCharacter(mfi.getEditorState());
            }

            @Override
            public boolean test(String keyboard) {
				return keyboard.endsWith(Unicode.SUPERSCRIPT_MINUS_ONE_STRING)
						&& getCommandAdapter().test(keyboard.substring(0,
								keyboard.length() - Unicode.SUPERSCRIPT_MINUS_ONE_STRING.length()));
            }
        });

        adapters.add(commandAdapter);
    }

	/**
	 * @param mathFieldInternal
	 *            editor
	 * @param text
	 *            text to be inserted
	 */
	public static void insertString(final MathFieldInternal mathFieldInternal,
			String text) {
		boolean oldCreateFrac = mathFieldInternal.getInputController()
				.getCreateFrac();
		mathFieldInternal.getInputController().setCreateFrac(false);
		mathFieldInternal.getInputController().setCreateNroot(false);
		emulateInput(mathFieldInternal, text);
		mathFieldInternal.getInputController().setCreateFrac(oldCreateFrac);
		mathFieldInternal.getInputController().setCreateNroot(true);
		mathFieldInternal.onInsertString();
	}

	/**
	 * Type text character by character. This way 1/2+3 becomes 1/(2+3). To
	 * insert string properly please use
	 * {@link #insertString(MathFieldInternal, String)}
	 * 
	 * @param mathFieldInternal
	 *            input field
	 * @param text
	 *            text to write
	 */
	public static void emulateInput(MathFieldInternal mathFieldInternal,
			String text) {
		for (int i = 0; i < text.length(); i++) {
			KeyboardInputAdapter.onKeyboardInput(mathFieldInternal,
					text.charAt(i) + "");
		}
	}

	/**
	 * @return command adapter
	 */
	protected static KeyboardAdapter getCommandAdapter() {
		return commandAdapter;
	}

	/**
	 * @param mathFieldInternal
	 *            editor
	 * @param input
	 *            input
	 */
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

	/**
	 * @param mathFieldInternal
	 *            editor
	 * @param commandName
	 *            command name
	 */
    public static void onCommandInput(MathFieldInternal mathFieldInternal, String commandName) {
        commandAdapter.commit(mathFieldInternal, commandName);
        mathFieldInternal.update();
    }

}
