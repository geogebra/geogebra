package org.geogebra.web.html5.gui.voiceInput.questResErr;

import org.geogebra.common.util.StringUtil;

/**
 * @author Csilla
 * 
 *         question + result + error for x coordinate of a point
 */
public class XCoordQuestResErr implements QuestResErrInterface {

	private String response = "";

	@Override
	public int getID() {
		return QuestResErrConstants.X_COORD;
	}

	@Override
	public String getQuestion() {
		return "Please give the x coordinate.";
	}

	@Override
	public String getResponse() {
		return response;
	}

	@Override
	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public Double getResponseAsNumber() {
		return Double.valueOf(getResponse());
	}

	/**
	 * check if input is valid (must be nr)
	 * 
	 * @return OK, error message otherwise
	 */
	@Override
	public String checkValidity() {
		if (StringUtil.isNumber(getResponse())) {
			return "OK";
		}
		return QuestResErrConstants.ERR_MUST_BE_NUMBER;
	}
}
