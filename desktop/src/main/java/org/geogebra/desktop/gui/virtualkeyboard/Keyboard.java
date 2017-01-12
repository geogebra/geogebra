package org.geogebra.desktop.gui.virtualkeyboard;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.geogebra.common.util.debug.Log;

//http://stackoverflow.com/questions/1248510/convert-string-to-keyevents

/**
 * adapted from
 * http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 */
public class Keyboard {

	protected Robot robot;

	public static void mainx(String... args) throws Exception {
		Keyboard keyboard = new Keyboard();
		keyboard.type("Hello there, how are you?");
	}

	public Keyboard() throws AWTException {
		this.robot = new Robot();
	}

	public Keyboard(Robot robot) {
		this.robot = robot;
	}

	public void type(CharSequence characters) {
		int length = characters.length();
		for (int i = 0; i < length; i++) {
			char character = characters.charAt(i);
			type(character);
		}
	}

	public void type(boolean altPressed, boolean ctrlPressed,
			boolean shiftPressed, CharSequence characters) {

		if (altPressed)
		 {
			robot.keyPress(KeyEvent.VK_ALT);// */
		}
		if (ctrlPressed) {
			robot.keyPress(KeyEvent.VK_CONTROL);
		}
		if (shiftPressed)
		 {
			robot.keyPress(KeyEvent.VK_SHIFT);// */
		}

		int length = characters.length();
		for (int i = 0; i < length; i++) {
			char character = characters.charAt(i);
			type(character);
		}

		robot.keyRelease(KeyEvent.VK_ALT);// */
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_SHIFT);// */

	}

	public void type(char character) {
		switch (character) {
		case 'a':
			doType(KeyEvent.VK_A);
			break;
		case 'b':
			doType(KeyEvent.VK_B);
			break;
		case 'c':
			doType(KeyEvent.VK_C);
			break;
		case 'd':
			doType(KeyEvent.VK_D);
			break;
		case 'e':
			doType(KeyEvent.VK_E);
			break;
		case 'f':
			doType(KeyEvent.VK_F);
			break;
		case 'g':
			doType(KeyEvent.VK_G);
			break;
		case 'h':
			doType(KeyEvent.VK_H);
			break;
		case 'i':
			doType(KeyEvent.VK_I);
			break;
		case 'j':
			doType(KeyEvent.VK_J);
			break;
		case 'k':
			doType(KeyEvent.VK_K);
			break;
		case 'l':
			doType(KeyEvent.VK_L);
			break;
		case 'm':
			doType(KeyEvent.VK_M);
			break;
		case 'n':
			doType(KeyEvent.VK_N);
			break;
		case 'o':
			doType(KeyEvent.VK_O);
			break;
		case 'p':
			doType(KeyEvent.VK_P);
			break;
		case 'q':
			doType(KeyEvent.VK_Q);
			break;
		case 'r':
			doType(KeyEvent.VK_R);
			break;
		case 's':
			doType(KeyEvent.VK_S);
			break;
		case 't':
			doType(KeyEvent.VK_T);
			break;
		case 'u':
			doType(KeyEvent.VK_U);
			break;
		case 'v':
			doType(KeyEvent.VK_V);
			break;
		case 'w':
			doType(KeyEvent.VK_W);
			break;
		case 'x':
			doType(KeyEvent.VK_X);
			break;
		case 'y':
			doType(KeyEvent.VK_Y);
			break;
		case 'z':
			doType(KeyEvent.VK_Z);
			break;
		case 'A':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A);
			break;
		case 'B':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B);
			break;
		case 'C':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C);
			break;
		case 'D':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D);
			break;
		case 'E':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E);
			break;
		case 'F':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F);
			break;
		case 'G':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G);
			break;
		case 'H':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H);
			break;
		case 'I':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I);
			break;
		case 'J':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J);
			break;
		case 'K':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K);
			break;
		case 'L':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L);
			break;
		case 'M':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M);
			break;
		case 'N':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N);
			break;
		case 'O':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O);
			break;
		case 'P':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P);
			break;
		case 'Q':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q);
			break;
		case 'R':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R);
			break;
		case 'S':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S);
			break;
		case 'T':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T);
			break;
		case 'U':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U);
			break;
		case 'V':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V);
			break;
		case 'W':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W);
			break;
		case 'X':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X);
			break;
		case 'Y':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y);
			break;
		case 'Z':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z);
			break;
		case '`':
			doType(KeyEvent.VK_BACK_QUOTE);
			break;
		/*
		 * case '0': doType(KeyEvent.VK_0); break; case '1':
		 * doType(KeyEvent.VK_1); break; case '2': doType(KeyEvent.VK_2); break;
		 * case '3': doType(KeyEvent.VK_3); break; case '4':
		 * doType(KeyEvent.VK_4); break; case '5': doType(KeyEvent.VK_5); break;
		 * case '6': doType(KeyEvent.VK_6); break; case '7':
		 * doType(KeyEvent.VK_7); break; case '8': doType(KeyEvent.VK_8); break;
		 * case '9': doType(KeyEvent.VK_9); break;
		 */
		case '0':
			doType(KeyEvent.VK_NUMPAD0);
			break;
		case '1':
			doType(KeyEvent.VK_NUMPAD1);
			break;
		case '2':
			doType(KeyEvent.VK_NUMPAD2);
			break;
		case '3':
			doType(KeyEvent.VK_NUMPAD3);
			break;
		case '4':
			doType(KeyEvent.VK_NUMPAD4);
			break;
		case '5':
			doType(KeyEvent.VK_NUMPAD5);
			break;
		case '6':
			doType(KeyEvent.VK_NUMPAD6);
			break;
		case '7':
			doType(KeyEvent.VK_NUMPAD7);
			break;
		case '8':
			doType(KeyEvent.VK_NUMPAD8);
			break;
		case '9':
			doType(KeyEvent.VK_NUMPAD9);
			break;
		case '-':
			doType(KeyEvent.VK_MINUS);
			break;
		case '=':
			doType(KeyEvent.VK_EQUALS);
			break;
		case '~':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE);
			break;
		case '!':
			doType(KeyEvent.VK_EXCLAMATION_MARK);
			break;
		case '@':
			doType(KeyEvent.VK_AT);
			break;
		case '#':
			doType(KeyEvent.VK_NUMBER_SIGN);
			break;
		case '$':
			doType(KeyEvent.VK_DOLLAR);
			break;
		case '%':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5);
			break;
		case '^':
			doType(KeyEvent.VK_CIRCUMFLEX);
			break;
		case '&':
			doType(KeyEvent.VK_AMPERSAND);
			break;
		case '*':
			doType(KeyEvent.VK_ASTERISK);
			break;
		case '(':
			doType(KeyEvent.VK_LEFT_PARENTHESIS);
			break;
		case ')':
			doType(KeyEvent.VK_RIGHT_PARENTHESIS);
			break;
		case '_':
			doType(KeyEvent.VK_UNDERSCORE);
			break;
		case '+':
			doType(KeyEvent.VK_PLUS);
			break;
		case '\t':
			doType(KeyEvent.VK_TAB);
			break;
		case '\n':
			doType(KeyEvent.VK_ENTER);
			break;
		case '[':
			doType(KeyEvent.VK_OPEN_BRACKET);
			break;
		case ']':
			doType(KeyEvent.VK_CLOSE_BRACKET);
			break;
		case '\\':
			doType(KeyEvent.VK_BACK_SLASH);
			break;
		case '{':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET);
			break;
		case '}':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET);
			break;
		case '|':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH);
			break;
		case ';':
			doType(KeyEvent.VK_SEMICOLON);
			break;
		case ':':
			doType(KeyEvent.VK_COLON);
			break;
		case '\'':
			doType(KeyEvent.VK_QUOTE);
			break;
		case '"':
			doType(KeyEvent.VK_QUOTEDBL);
			break;
		case ',':
			doType(KeyEvent.VK_COMMA);
			break;
		case '<':
			doType(KeyEvent.VK_LESS);
			break;
		case '.':
			doType(KeyEvent.VK_PERIOD);
			break;
		case '>':
			doType(KeyEvent.VK_GREATER);
			break;
		case '/':
			doType(KeyEvent.VK_SLASH);
			break;
		case '?':
			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH);
			break;
		case ' ':
			doType(KeyEvent.VK_SPACE);
			break;
		default:
			throw new IllegalArgumentException(
					"Cannot type character " + character);
		}
	}

	/*
	 * process strings such as <enter>
	 */
	public void doType(boolean altPressed, boolean ctrlPressed,
			boolean shiftPressed, String text) {
		if (!text.startsWith("<") || !text.endsWith(">")) {
			type(altPressed, ctrlPressed, shiftPressed, text);
		} else {
			if ("<escape>".equals(text)) {
				doType(altPressed, ctrlPressed, shiftPressed,
						KeyEvent.VK_ESCAPE);
			} else if ("<left>".equals(text)) {
				doType(altPressed, ctrlPressed, shiftPressed, KeyEvent.VK_LEFT);
			} else if ("<right>".equals(text)) {
				doType(altPressed, ctrlPressed, shiftPressed,
						KeyEvent.VK_RIGHT);
			} else if ("<up>".equals(text)) {
				doType(altPressed, ctrlPressed, shiftPressed, KeyEvent.VK_UP);
			} else if ("<down>".equals(text)) {
				doType(altPressed, ctrlPressed, shiftPressed, KeyEvent.VK_DOWN);
			} else if ("<backspace>".equals(text)) {
				doType(altPressed, ctrlPressed, shiftPressed,
						KeyEvent.VK_BACK_SPACE);
			} else {
				Log.debug("unknown keycode:" + text);
			}
		}

	}

	public void doType(boolean altPressed, boolean ctrlPressed,
			boolean shiftPressed, int... keyCodes) {

		if (altPressed)
		 {
			robot.keyPress(KeyEvent.VK_ALT); // */
		}
		if (ctrlPressed) {
			robot.keyPress(KeyEvent.VK_CONTROL);
		}
		if (shiftPressed)
		 {
			robot.keyPress(KeyEvent.VK_SHIFT);// */
		}

		doType(keyCodes, 0, keyCodes.length);

		robot.keyRelease(KeyEvent.VK_ALT);// */
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_SHIFT);// */
	}

	public void doType(int... keyCodes) {
		doType(keyCodes, 0, keyCodes.length);
	}

	public void altPressed(boolean press) {
		if (press) {
			robot.keyPress(KeyEvent.VK_ALT);
		} else {
			robot.keyRelease(KeyEvent.VK_ALT);
		}
	}

	private void doType(int[] keyCodes, int offset, int length) {
		if (length == 0) {
			return;
		}

		robot.keyPress(keyCodes[offset]);
		doType(keyCodes, offset + 1, length - 1);
		robot.keyRelease(keyCodes[offset]);
	}

}
