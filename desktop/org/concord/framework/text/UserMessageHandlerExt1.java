package org.concord.framework.text;

public interface UserMessageHandlerExt1 extends UserMessageHandler {
	/**
	 * Show a message to the user.  This will have a single "ok" button
	 * or whatever is appropriate for the platform or presentation.
	 * 
	 * @param message
	 * @param title
	 */
	public void showMessage(String message, String title, String details);
}
