package org.geogebra.common.main.exam.restriction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Deprecated // use org.geogebra.common.exam API instead
public class RestrictExamImpl implements RestrictExam {

	private final ExamRestrictionModel model;
	private final List<Restrictable> restrictables;
	private boolean enabled;

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
		enabled = true;
		applyExamRestrictions(model);
	}

	private void applyExamRestrictions(ExamRestrictionModel restrictionModel) {
		restrictables.forEach(restrictable -> {
			if (restrictionModel == null
					|| restrictable.isExamRestrictionModelAccepted(restrictionModel)) {
				restrictable.setExamRestrictionModel(restrictionModel);
				restrictable.applyExamRestrictions();
			}
		});
	}

	@Override
	public void disable() {
		enabled = false;
		applyExamRestrictions(null);
	}

	@Override
	public void register(Restrictable item) {
		restrictables.add(item);
		item.setExamRestrictionModel(enabled ? model : null);
	}

	@Override
	public Stream<Restrictable> getRestrictables() {
		return restrictables.stream();
	}

	@Override
	public ExamRestrictionModel getModel() {
		return model;
	}
}
