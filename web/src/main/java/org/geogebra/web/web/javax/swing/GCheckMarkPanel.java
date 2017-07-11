package org.geogebra.web.web.javax.swing;

import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Adds a panel with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public abstract class GCheckMarkPanel {

	protected FlowPanel itemPanel;
	private boolean checked;
	private String text;
	private Image checkImg;
	private ScheduledCommand cmd;

	/**
	 * @param text
	 *            Title
	 * @param checkUrl
	 *            image of check mark
	 * @param checked
	 *            initial value.
	 * @param cmd
	 *            The command to run.
	 */
	public GCheckMarkPanel(String text, String checkUrl,
			boolean checked,
			final ScheduledCommand cmd) {
		this.setText(text);
		this.setCmd(cmd);
		checkImg = new NoDragImage(checkUrl);
		checkImg.addStyleName("checkImg");
		itemPanel = new FlowPanel();
		itemPanel.addStyleName("checkMarkMenuItem");
		createContents();
		setChecked(checked);
	}

	/**
	 * Creates the contents goes along with the check mark.
	 */
	protected abstract void createContents();

	/**
	 * Updates the contents goes along with the check mark.
	 */
	protected abstract void updateContents();

	/**
	 * Sets the item checked/unchecked.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setChecked(boolean value) {
		checked = value;
		itemPanel.clear();
		itemPanel.add(new HTML(getText()));
		if (checked) {
			itemPanel.add(checkImg);
		}
		updateContents();
	}

	/**
	 * 
	 * @return true if item is checked
	 */
	public boolean isChecked() {
		return checked;
	}

	public ScheduledCommand getCmd() {
		return cmd;
	}

	public void setCmd(ScheduledCommand cmd) {
		this.cmd = cmd;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
