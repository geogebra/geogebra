package org.geogebra.web.html5.gui.voiceInput.questResErr;

import org.geogebra.common.util.StringUtil;

/**
 * @author Csilla
 * 
 *         question + result + error for radius of a circle
 */
public class RadiusQuestResErr implements QuestResErrInterface {

	private String response = "";

	@Override
	public int getID() {
		return QuestResErrConstants.RADIUS;
	}

	@Override
	public String getQuestion() {
		return "Please give the radius.";
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
	 * check if input is valid (pos nr)
	 * 
	 * @return OK, error message otherwise
	 */
	@Override
	public String checkValidity() {
		if (StringUtil.isNumber(getResponse()) && getResponseAsNumber() > 0) {
			return "OK";
		} else if (!StringUtil.isNumber(getResponse())) {
			return QuestResErrConstants.ERR_MUST_BE_NUMBER;
		} else {
			return QuestResErrConstants.ERR_MUST_BE_POSITIVE;
		}
	}
}
