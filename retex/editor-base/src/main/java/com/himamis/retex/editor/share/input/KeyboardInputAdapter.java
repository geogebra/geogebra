package com.himamis.retex.editor.share.input;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.controller.KeyListenerImpl;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.adapter.FunctionsAdapter;
import com.himamis.retex.editor.share.input.adapter.KeyboardAdapter;
import com.himamis.retex.editor.share.input.adapter.StringAdapter;
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

		adapters.add(new StringAdapter(times, "*"));
		adapters.add(new StringAdapter(minus, "-"));
		adapters.add(new StringAdapter("10^", "10^"));
		adapters.add(new StringAdapter("a_n", "_"));
		adapters.add(new StringAdapter(e + "^", e + "^"));

		adapters.add(new StringInput("x^(-1)") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "^-1");
				CursorController.nextCharacter(mfi.getEditorState());
			}
		});

		adapters.add(new StringInput(divide + "") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				InputController controller = mfi.getInputController();
				boolean createFrac = controller.getCreateFrac();
				controller.setCreateFrac(false);
				type(mfi, "/");
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

		adapters.add(new StringInput("log_{10}") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "log_10");
				CursorController.nextCharacter(mfi.getEditorState());
				type(mfi, "(");
			}
		});

		adapters.add(new StringInput("logb") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "log(");
				mfi.getCursorController().prevCharacter(mfi.getEditorState());
			}
		});

		adapters.add(new StringInput("d/dx") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "d/dx");
				mfi.getCursorController().prevCharacter(mfi.getEditorState());
				mfi.getCursorController().prevCharacter(mfi.getEditorState());
				mfi.getCursorController().prevCharacter(mfi.getEditorState());
			}
		});

		// Let through all one length characters!
		adapters.add(new StringInput() {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, input);
			}

			@Override
			public boolean test(String input) {
				return input.length() == 1;
			}
		});

		adapters.add(new FunctionVariableAdapter());

		commandAdapter = new KeyboardAdapter() {
			@Override
			public void commit(MathFieldInternal mfi, String commandName) {
				emulateInput(mfi, commandName);
				if (!commandName.contains("(")) {
					mfi.getInputController().newBraces(mfi.getEditorState(), '(');
					mfi.notifyAndUpdate("(");
				}
			}

			@Override
			public boolean test(String input) {
				return true;
			}
		};

		adapters.add(commandAdapter);
	}

	/**
	 * @param mfi math field
	 * @param input string to type
	 */
	public static void type(MathFieldInternal mfi, String input) {
		EditorState editorState = mfi.getEditorState();
		KeyListenerImpl keyListener = mfi.getKeyListener();
		for (int i = 0; i < input.length(); i++) {
			keyListener.onKeyTyped(input.charAt(i), editorState);
		}
		mfi.notifyAndUpdate(String.valueOf(input.charAt(input.length() - 1)));
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
