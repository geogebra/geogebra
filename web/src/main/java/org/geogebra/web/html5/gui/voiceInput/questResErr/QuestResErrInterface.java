package org.geogebra.web.html5.gui.voiceInput.questResErr;

/**
 * @author Csilla
 *
 *         e.g.: C: please give radius H: -1 C: radius must be pos. Please give
 *         radius H: 5
 *
 *         interface for a round of question + result + error handling
 */
public interface QuestResErrInterface {

	/**
	 * @return id of the question+result, e.g. radius
	 */
	int getID();

	/**
	 * @return question read by computer
	 */
	String getQuestion();

	/**
	 * @return users response
	 */
	String getResponse();

	/**
	 * @return the response in double
	 */
	Double getResponseAsNumber();

	/**
	 * @param response
	 *            given by user for the question
	 */
	void setResponse(String response);

	/**
	 * check if users input (see getResponse()) is valid e.g. radius must be a
	 * positive number
	 * 
	 * @return "OK" if input valid, error message otherwise
	 */
	String checkValidity();

}
