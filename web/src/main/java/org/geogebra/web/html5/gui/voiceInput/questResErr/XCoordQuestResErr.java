package org.geogebra.web.html5.gui.voiceInput.questResErr;

import org.geogebra.common.util.StringUtil;

/**
 * @author Csilla
 * 
 *         question + result + error for x coordinate of a point
 */
public class XCoordQuestResErr implements QuestResErrInterface {

	private String response = "";

	public int getID() {
		return QuestResErrConstants.X_COORD;
	}

	public String getQuestion() {
		return "Please give the x coordinate.";
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Double getResponseAsNumber() {
		return Double.valueOf(getResponse());
	}

	public String checkValidity() {
		if (StringUtil.isNumber(getResponse())) {
			return "OK";
		}
		return QuestResErrConstants.ERR_MUST_BE_NUMBER;
	}

}
