package org.geogebra.common.main.exam.restriction;

import java.util.stream.Stream;

/**
 * Class to manage restrictions at exam start and clear them at exam end..
 */
public interface RestrictExam {

	/**
	 * Enable restrictions on exam start.
	 */
	void enable();

	/**
	 * Clear all restrictions when exam ends.
	 */
	void disable();

	/**
	 * Register program components that can be restricted during exam.
	 *
	 * @param item to register as a Restrictable-
	 */
	void register(Restrictable item);

	/**
	 * @return all restrictable objects
	 */
	Stream<Restrictable> getRestrictables();
}
