package org.geogebra.web.html5.gui.voiceInput.questResErr;

/**
 * @author Csilla
 * 
 *         ids of the question+result+error types
 *
 */
public class QuestResErrConstants {

	/**
	 * command input
	 */
	public static final int COMMAND = 0;
	/**
	 * x coordinate of a point
	 */
	public static final int X_COORD = 1;
	/**
	 * y coordinate of a point
	 */
	public static final int Y_COORD = 2;
	/**
	 * radius of a circle
	 */
	public static final int RADIUS = 3;

	/**
	 * error message if the input was not a number
	 */
	public static final String ERR_MUST_BE_NUMBER = "The input must be a number. ";
	/**
	 * error message if the input was not positive
	 */
	public static final String ERR_MUST_BE_POSITIVE = "The input must be a positive number. ";

	/**
	 * if tool is not supported yet
	 */
	public static final int NOT_SUPPORTED = 100;
	/**
	 * id of creating a circle
	 */
	public static final int CREATE_POINT = 101;
	/**
	 * id of creating a circle
	 */
	public static final int CREATE_CIRCLE = 102;
	/**
	 * id of creating a segment
	 */
	public static final int CREATE_SEGMENT = 103;
	/**
	 * task fulfilled
	 */
	public static final int CREATED_OBJECT = 500;

}
