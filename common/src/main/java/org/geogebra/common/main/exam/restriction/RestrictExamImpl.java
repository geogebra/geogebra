package org.geogebra.common.main.exam.restriction;

import java.util.ArrayList;
import java.util.List;

public class RestrictExamImpl implements RestrictExam {

	private ExamRestrictionModel model;
	private List<Restrictable> restrictables;

	/**
	 *
	 * @param model with restriction rules.
	 */
	public RestrictExamImpl(ExamRestrictionModel model) {
		this.model = model;
		restrictables = new ArrayList<>();
	}

	@Override
	public void enable() {
		restrictables.forEach(restrictable -> {
			if (restrictable.isExamRestrictionModelAccepted(model)) {
				restrictable.setExamRestrictionModel(model);
				restrictable.applyExamRestrictions();
			}
		});
	}

	@Override
	public void disable() {
		restrictables.forEach(Restrictable::cancelExamRestrictions);

	}

	@Override
	public void register(Restrictable item) {
		restrictables.add(item);
	}
}
