package org.geogebra.common.exam.restrictions;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.filter.GraphingOperationArgumentFilter;
import org.geogebra.common.kernel.arithmetic.filter.OperationExpressionFilter;
import org.geogebra.common.plugin.Operation;

final class ReaulschuleExamRestrictions extends ExamRestrictions {
	ReaulschuleExamRestrictions() {
		super(ExamType.REALSCHULE,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
						SuiteSubApp.PROBABILITY, SuiteSubApp.SCIENTIFIC),
				SuiteSubApp.GRAPHING,
				null,
				getInputExpressionFilter(),
				null,
				null,
				null,
				getFilteredOperations(),
				null,
				null,
				null);
	}

	@Override
	protected RestorableSettings createSavedSettings() {
		return new RealschuleSettings();
	}

	@Override
	public void applySettingsRestrictions(@Nonnull Settings settings) {
		super.applySettingsRestrictions(settings);
		EuclidianSettings euclidian = settings.getEuclidian(1);
		settings.getGeneral().setCoordFormat(Kernel.COORD_STYLE_AUSTRIAN);
		euclidian.beginBatch();
		euclidian.setAxisLabel(0, "x");
		euclidian.setAxisLabel(1, "y");
		euclidian.setGridType(EuclidianView.GRID_CARTESIAN);
		euclidian.setAxisNumberingDistance(0, 0.5);
		euclidian.setAxisNumberingDistance(1, 0.5);
		euclidian.endBatch();
	}

	private static Set<ExpressionFilter> getInputExpressionFilter() {
		return Set.of(new OperationExpressionFilter(getFilteredOperations()),
				GraphingOperationArgumentFilter.INSTANCE);
	}

	private static Set<Operation> getFilteredOperations() {
		return Set.of(Operation.ALT,Operation.ARG);
	}
}
