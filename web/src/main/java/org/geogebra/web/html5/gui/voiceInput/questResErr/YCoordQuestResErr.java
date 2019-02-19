package org.geogebra.web.html5.gui.voiceInput.questResErr;

import org.geogebra.common.util.StringUtil;

/**
 * @author Csilla
 * 
 *         question + result + error for y coordinate of a point
 */
public class YCoordQuestResErr implements QuestResErrInterface {

	private String response = "";

	public int getID() {
		return QuestResErrConstants.Y_COORD;
	}

	public String getQuestion() {
		return "Please give the y coordinate.";
	}

	public String getResponse() {
		return response;
	}

	public Double getResponseAsNumber() {
		return Double.valueOf(getResponse());
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String checkValidity() {
		if (StringUtil.isNumber(getResponse())) {
			return "OK";
		}
		return QuestResErrConstants.ERR_MUST_BE_NUMBER;
	}
}
