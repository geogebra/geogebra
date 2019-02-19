package org.geogebra.web.html5.gui.voiceInput;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.voiceInput.command.VoiceInputCommandInterface;
import org.geogebra.web.html5.gui.voiceInput.command.VoiceInputPoint;
import org.geogebra.web.html5.gui.voiceInput.command.VoiceInputSegment;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrConstants;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrInterface;

/**
 * @author Csilla
 * 
 *         handle different tasks
 *
 */
public class VoiceInputDispatcher {
	private VoiceInputOutputController controller;
	private ArrayList<QuestResErrInterface> questList;
	private VoiceInputCommandInterface currentCommand;

	/**
	 * @param controller
	 *            input output controller
	 */
	public VoiceInputDispatcher(VoiceInputOutputController controller) {
		this.controller = controller;
		questList = new ArrayList<>();
	}

	/**
	 * @return current task
	 */
	public VoiceInputCommandInterface getCurrentCommand() {
		return currentCommand;
	}

	/**
	 * @param currentCommand
	 *            current task
	 */
	public void setCurrentCommand(VoiceInputCommandInterface currentCommand) {
		this.currentCommand = currentCommand;
	}

	/**
	 * @param commandID
	 *            id of the task (e.g. create point)
	 */
	public void processCommand(int commandID) {
		questList.clear();
		switch (commandID) {
		case QuestResErrConstants.CREATE_POINT:
			setCurrentCommand(new VoiceInputPoint());
			break;
		case QuestResErrConstants.CREATE_SEGMENT:
			setCurrentCommand(new VoiceInputSegment());
			break;
		default:
			break;
		}
		questList = currentCommand.getQuestResList();
		controller.collectInput();
	}

	/**
	 * @return list of questions for current task
	 * 
	 *         e.g. for point we need an x coordinate and y coordinate
	 */
	public ArrayList<QuestResErrInterface> getQuestList() {
		return questList;
	}

}
