package org.geogebra.common.main.provider;

import org.geogebra.common.main.exam.ExamEnvironment;

@Deprecated // use org.geogebra.common.exam API instead
public interface ExamProvider {
    ExamEnvironment getExam();
}
