package com.himamis.retex.editor.share.input;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.KeyListenerImpl;
import com.himamis.retex.editor.share.controller.PlaceholderController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.input.adapter.FunctionsAdapter;
import com.himamis.retex.editor.share.input.adapter.KeyboardAdapter;
import com.himamis.retex.editor.share.input.adapter.StringAdapter;
import com.himamis.retex.editor.share.input.adapter.StringInput;
import com.himamis.retex.editor.share.util.CommandParser;
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
		adapters.add(new StringAdapter(divide, "/"));
		adapters.add(new StringAdapter("10^", "10^"));
		adapters.add(new StringAdapter("a_n", "_"));
		adapters.add(new StringAdapter(e + "^", e + "^"));

		// these two are needed for text mode input, math mode input would work without them
		adapters.add(new StringInput(Unicode.LFLOOR + "") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "" + Unicode.LFLOOR);
				if (plainTextMode(mfi)) {
					type(mfi, "" + Unicode.RFLOOR);
					CursorController.prevCharacter(mfi.getEditorState());
				}
			}
		});

		adapters.add(new StringInput(Unicode.LCEIL + "") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "" + Unicode.LCEIL);
				if (plainTextMode(mfi)) {
					type(mfi, "" + Unicode.RCEIL);
					CursorController.prevCharacter(mfi.getEditorState());
				}
			}
		});

		adapters.add(new StringInput("abs") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "|");
				if (plainTextMode(mfi)) {
					type(mfi, "|");
					CursorController.prevCharacter(mfi.getEditorState());
				}
			}
		});

		adapters.add(new StringInput("x^(-1)") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "^-1");
				CursorController.nextCharacter(mfi.getEditorState());
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
				CursorController.prevCharacter(mfi.getEditorState());
			}
		});

		adapters.add(new StringInput("d/dx") {
			@Override
			public void commit(MathFieldInternal mfi, String input) {
				type(mfi, "d/dx");
				CursorController.prevCharacter(mfi.getEditorState());
				CursorController.prevCharacter(mfi.getEditorState());
				CursorController.prevCharacter(mfi.getEditorState());
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
			public void commit(MathFieldInternal mfi, String commandString) {
				List<String> splitCommand = CommandParser.parseCommand(commandString);

				EditorState editorState = mfi.getEditorState();
				type(mfi, splitCommand.get(0));
				mfi.getInputController().newBraces(editorState, '(');
				mfi.notifyAndUpdate("(");
				PlaceholderController.insertPlaceholders(editorState,
						splitCommand.subList(1, splitCommand.size()));
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
		if (input.isEmpty()) {
			return;
		}

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
