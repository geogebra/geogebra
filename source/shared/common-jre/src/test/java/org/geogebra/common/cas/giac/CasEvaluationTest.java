/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.cas.giac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasEvaluation;
import org.geogebra.common.cas.CasEvaluation.Failure;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.binding.CASGiacBinding;
import org.geogebra.common.cas.giac.binding.Context;
import org.geogebra.common.cas.giac.binding.Gen;
import org.geogebra.regexp.server.JavaRegExpFactory;
import org.geogebra.regexp.shared.RegExpFactory;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Failure metadata must survive conversion, caching and independent native workers. */
@Issue("APPS-7945")
class CasEvaluationTest {
	private static final String SUM_ERROR = "GIAC_ERROR: diff of sum with boundaries or mute"
			+ " variable depending on differentiation variable Error: Bad Argument Value";

	@BeforeEach
	void initRegexp() {
		RegExpFactory.setPrototypeIfNull(new JavaRegExpFactory());
	}

	@Test
	void conversionPreservesSpecificFailure() {
		CasEvaluation evaluation = CasEvaluation.fromGiac(SUM_ERROR, "?").withValue(null);
		assertEquals(Failure.UNSUPPORTED_SUM_DERIVATIVE, evaluation.getFailure());
		assertNull(evaluation.getValue());
	}

	@Test
	void unknownErrorsAndPlainUndefinedDoNotMatchTheSpecificFailure() {
		assertEquals(
				Failure.GIAC_ERROR, CasEvaluation.fromGiac("GIAC_ERROR: unrelated", "?").getFailure());
		assertEquals(Failure.NONE, CasEvaluation.fromGiac("?", "?").getFailure());
		assertEquals(Failure.NONE, CasEvaluation.value(null).getFailure());
	}

	@Test
	void nativeCacheHitRetainsFailureAfterAnotherEvaluationSucceeds() {
		StubNativeCAS cas = new StubNativeCAS();
		cas.response = SUM_ERROR;
		CasEvaluation first = cas.evaluateCASResult("failedSum(x)");
		cas.response = "2";
		CasEvaluation success = cas.evaluateCASResult("1+1");
		CasEvaluation cached = cas.evaluateCASResult("failedSum(x)");
		assertEquals("?", first.getValue());
		assertEquals(Failure.UNSUPPORTED_SUM_DERIVATIVE, first.getFailure());
		assertEquals("2", success.getValue());
		assertEquals(Failure.NONE, success.getFailure());
		assertEquals(first.getFailure(), cached.getFailure());
		assertEquals("?", cached.getValue());
		assertEquals(2, cas.evaluations);
	}

	@Test
	void nativeTimeoutIsStillPropagatedUnchanged() {
		StubNativeCAS cas = new StubNativeCAS();
		cas.workerFailure = new TimeoutException("Thread timeout from Giac");
		assertSame(
				cas.workerFailure,
				assertThrows(TimeoutException.class, () -> cas.evaluateCASResult("slowSum(x)")));
	}

	@Test
	void nativeExceptionSentinelIsPreserved() {
		StubNativeCAS cas = new StubNativeCAS();
		cas.workerSentinel = true;
		CasEvaluation result = cas.evaluateCASResult("failedWorker(x)");
		assertEquals(CASgiac.FORCE_ERROR, result.getValue());
		assertEquals(Failure.NONE, result.getFailure());
	}

	@Test
	void nestedEvaluationCannotReplaceOuterFailure() {
		StubNativeCAS cas = new StubNativeCAS();
		cas.response = SUM_ERROR;
		cas.afterEvaluation = () -> {
			cas.response = "2";
			assertEquals("2", cas.evaluateCASResult("1+1").getValue());
		};
		CasEvaluation outer = cas.evaluateCASResult("nestedSum(x)");
		assertEquals("?", outer.getValue());
		assertEquals(Failure.UNSUPPORTED_SUM_DERIVATIVE, outer.getFailure());
	}

	@Test
	void canceledLateWorkerCannotReplaceAnotherWorkersOwnResult() {
		StubNativeCAS cas = new StubNativeCAS();
		EvalFunction late = new EvalFunction(cas, "lateSum(x)", 1000);
		late.cancel();
		CasEvaluation success = cas.evaluateCASResult("2");
		cas.response = SUM_ERROR;
		late.run();
		assertEquals("2", cas.threadResult);
		assertEquals("2", success.getValue());
		assertEquals(Failure.NONE, success.getFailure());
		assertEquals(SUM_ERROR, late.getResult());
	}

	private static final class StubNativeCAS extends CASgiacB {
		private final CASGiacBinding binding = mock(CASGiacBinding.class);
		private String response = "2";
		private int evaluations;
		private Runnable afterEvaluation;
		private RuntimeException workerFailure;
		private boolean workerSentinel;

		StubNativeCAS() {
			super(mock(CASparser.class));
			when(casParser.insertSpecialChars(anyString())).thenAnswer(call -> call.getArgument(0));
			Gen gen = mock(Gen.class);
			when(binding.createGen(anyString(), org.mockito.ArgumentMatchers.same(context)))
					.thenReturn(gen);
			when(gen.eval(1, context)).thenReturn(gen);
			when(gen.print(context)).thenAnswer(invocation -> {
				evaluations++;
				return response;
			});
		}

		@Override
		protected void createContext() {
			context = mock(Context.class);
		}

		@Override
		protected CASGiacBinding createBinding() {
			return binding;
		}

		@Override
		void init(String exp, long timeoutMilliseconds) {
			// Native initialization is outside this test's scope.
		}

		@Override
		protected void callEvaluateFunction(EvalFunction worker) {
			if (workerFailure != null) {
				throw workerFailure;
			}
			if (workerSentinel) {
				threadResult = CASgiac.FORCE_ERROR;
				return;
			}
			worker.run();
			Runnable nested = afterEvaluation;
			afterEvaluation = null;
			if (nested != null) {
				nested.run();
			}
		}
	}
}
