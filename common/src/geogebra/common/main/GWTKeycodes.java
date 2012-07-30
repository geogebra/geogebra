package geogebra.common.main;

@SuppressWarnings("javadoc")
public interface GWTKeycodes {

	/**
	 * Contains the native key codes previously defined in
	 * {@link com.google.gwt.user.client.ui.KeyboardListener}. When converting
	 * keyboard listener instances, developers can use the following static import
	 * to access these constants:
	 * 
	 * <pre> import static com.google.gwt.event.dom.client.KeyCodes.*; </pre>
	 * 
	 * These constants are defined with an int data type in order to be compatible
	 * with the constants defined in
	 * {@link com.google.gwt.user.client.ui.KeyboardListener}.
	 * 
	 * @author gabor
	 * Because the original KeyCodes class has a private constructor, cant be extended,
	 * so I must create an own one.
	 */

	/**
	 * Alt key code.
	 */
	public static final int KEY_ALT = 18;

	/**
	 * Backspace key code.
	 */
	public static final int KEY_BACKSPACE = 8;
	/**
	 * Control key code.
	 */
	public static final int KEY_CTRL = 17;

	/**
	 * Delete key code.
	 */
	public static final int KEY_DELETE = 46;

	/**
	 * Down arrow code.
	 */
	public static final int KEY_DOWN = 40;

	/**
	 * End key code.
	 */
	public static final int KEY_END = 35;

	/**
	 * Enter key code.
	 */
	public static final int KEY_ENTER = 13;
	/**
	 * Escape key code.
	 */
	public static final int KEY_ESCAPE = 27;
	/**
	 * Home key code.
	 */
	public static final int KEY_HOME = 36;
	/**
	 * Left key code.
	 */
	public static final int KEY_LEFT = 37;
	/**
	 * Page down key code.
	 */
	public static final int KEY_PAGEDOWN = 34;
	/**
	 * Page up key code.
	 */
	public static final int KEY_PAGEUP = 33;
	/**
	 * Right arrow key code.
	 */
	public static final int KEY_RIGHT = 39;
	/**
	 * Shift key code.
	 */
	public static final int KEY_SHIFT = 16;
	/**
	 * Insert key code.
	 */
	public static final int KEY_INSERT = 45;

	/**
	 * Tab key code.
	 */
	public static final int KEY_TAB = 9;
	/**
	 * Up Arrow key code.
	 */
	public static final int KEY_UP = 38;	  
	/**
	 * Space key
	 */
	public static final int KEY_SPACE = 32;

	/**
	 * F1 - F12
	 */
	public static final int KEY_F1 = 112;
	public static final int KEY_F2 = 113;
	public static final int KEY_F3 = 114;
	public static final int KEY_F4 = 115;
	public static final int KEY_F5 = 116;
	public static final int KEY_F6 = 117;
	public static final int KEY_F7 = 118;
	public static final int KEY_F8 = 119;
	public static final int KEY_F9 = 120;
	public static final int KEY_F10 = 121;
	public static final int KEY_F11 = 122;
	public static final int KEY_F12 = 123;
	/**
	 * 0-9
	 */
	public static final int KEY_0 = 48;

	public static final int KEY_1 = 49;

	public static final int KEY_2 = 50;

	public static final int KEY_3 = 51;

	public static final int KEY_4 = 52;

	public static final int KEY_5 = 53;

	public static final int KEY_6 = 54;

	public static final int KEY_7 = 55;

	public static final int KEY_8 = 56;

	public static final int KEY_9 = 57;
	/**
	 * NUMPAD_X
	 */
	public static final int KEY_NUMPAD0 = 96;

	public static final int KEY_NUMPAD1 = 97;

	public static final int KEY_NUMPAD2 = 98;

	public static final int KEY_NUMPAD3 = 99;

	public static final int KEY_NUMPAD4 = 100;

	public static final int KEY_NUMPAD5 = 101;

	public static final int KEY_NUMPAD6 = 102;

	public static final int KEY_NUMPAD7 = 103;

	public static final int KEY_NUMPAD8 = 104;

	public static final int KEY_NUMPAD9 = 105;

	public static final int KEY_A = 65;
	public static final int KEY_B = 66;
	public static final int KEY_C = 67;
	public static final int KEY_D = 68;
	public static final int KEY_E = 69;
	public static final int KEY_F = 70;
	public static final int KEY_G = 71;
	public static final int KEY_H = 72;
	public static final int KEY_I = 73;
	public static final int KEY_J = 74;
	public static final int KEY_K = 75;
	public static final int KEY_L = 76;
	public static final int KEY_M = 77;
	public static final int KEY_N = 78;
	public static final int KEY_O = 79;
	public static final int KEY_P = 80;
	public static final int KEY_Q = 81;
	public static final int KEY_R = 82;
	public static final int KEY_S = 83;
	public static final int KEY_T = 84;
	public static final int KEY_U = 85;
	public static final int KEY_V = 86;
	public static final int KEY_W = 87;
	public static final int KEY_X = 88;
	public static final int KEY_Y = 89;
	public static final int KEY_Z = 90;

	public static final int KEY_NUMPADPLUS = 107;
	public static final int KEY_NUMPADMINUS = 109;

	public static final int KEY_MINUS = 189;

	public static final int KEY_EQUALS = 187;

	public static final int KEY_BACK_QUOTE = 223;

	public static final int KEY_LEFT_SQUARE_BRACKET = 219;
	public static final int KEY_RIGHT_SQUARE_BRACKET = 221;

	public static final int KEY_PERIOD = 190;
	public static final int KEY_COMMA = 188;
	public static final int KEY_SEMICOLON = 186;
	public static final int KEY_APOSTROPHE = 192;
	public static final int KEY_HASH = 222;
	public static final int KEY_SLASH = 191;
	public static final int KEY_BACKSLASH = 220;
	public static final int KEY_NUMPADASTERISK = 106;
	public static final int KEY_NUMPADSLASH = 111;
	public static final int KEY_WINDOWS = 91; // also META on Mac
	public static final int KEY_NUMLOCK = 144;

	public static final int KEY_CLEAR = 12;

	public static final int KEY_UNDEFINED = 0;







}
