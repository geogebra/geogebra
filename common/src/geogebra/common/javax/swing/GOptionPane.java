package geogebra.common.javax.swing;

import geogebra.common.main.App;

public interface GOptionPane {

    //
    // Option types - the same as in JOptionPane
    //

    /** 
     * Type meaning Look and Feel should not supply any options -- only
     * use the options from the <code>GOptionPane</code>.
     */
    public static final int         DEFAULT_OPTION = -1;
//    /** Type used for <code>showConfirmDialog</code>. */
//    public static final int         YES_NO_OPTION = 0;
//    /** Type used for <code>showConfirmDialog</code>. */
//    public static final int         YES_NO_CANCEL_OPTION = 1;
//    /** Type used for <code>showConfirmDialog</code>. */
//    public static final int         OK_CANCEL_OPTION = 2;

    //
    // Message types. Used by the UI to determine what icon to display,
    // and possibly what behavior to give based on the type.
    //
//    /** Used for error messages. */
//    public static final int  ERROR_MESSAGE = 0;
    /** Used for information messages. */
    public static final int  INFORMATION_MESSAGE = 1;
    /** Used for warning messages. */
//    public static final int  WARNING_MESSAGE = 2;
//    /** Used for questions. */
//    public static final int  QUESTION_MESSAGE = 3;
//    /** No icon is used. */
//    public static final int   PLAIN_MESSAGE = -1;

	
	public abstract int showConfirmDialog(App app,
			String message, String title, int optionType, int messageType);

}
