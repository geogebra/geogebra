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

package org.geogebra.common.exam;

import static org.geogebra.common.util.StreamUtils.filter;
import static org.geogebra.common.util.StreamUtils.streamOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.exam.restrictions.expression.ExpressionRestriction;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ExpressionRestrictionTests {
    private App app;
    private AlgebraProcessor algebraProcessor;
    private final ExpressionFilter expressionFilter = ExpressionRestriction.toFilter(Set.of(
            new RestrictBooleanExpressions(),
            new AllowBooleanCommandArguments(),
            new RestrictPlusOperation()));

    private static class RestrictPlusOperation implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getRestrictedSubExpressions(
                @Nonnull ExpressionValue expression) {
            return filter(expression, subExpression -> subExpression.isOperation(Operation.PLUS));
        }
    }

    private static final class RestrictBooleanExpressions implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getRestrictedSubExpressions(
                @Nonnull ExpressionValue expression) {
            return filter(expression, subExpression -> subExpression instanceof BooleanValue);
        }
    }

    private static class AllowBooleanCommandArguments implements ExpressionRestriction {
		@Override
		public @Nonnull Set<ExpressionValue> getAllowedSubExpressions(@Nonnull ExpressionValue expression) {
            return streamOf(expression)
                    // For commands
                    .filter(subExpression -> subExpression instanceof Command)
                    .map(command -> (Command) command)
                    // iterate through the arguments
                    .flatMap(command -> Arrays.stream(command.getArguments())
                            .map(ExpressionNode::unwrap)
                            // and allow booleans
                            .filter(argument -> argument instanceof BooleanValue)
                    ).collect(Collectors.toSet());
        }
    }

    @BeforeEach
    public void setup() {
        app = AppCommonFactory.create(new AppConfigGraphing());
        algebraProcessor = app.getKernel().getAlgebraProcessor();
        algebraProcessor.addInputExpressionFilter(expressionFilter);
        algebraProcessor.addOutputExpressionFilter(expressionFilter);
        app.getSettingsUpdater().resetSettingsOnAppStart();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1 * 2",
            "(1 * 2) / (5 ^(2 - 3))",
            "1 * BinomialDist(5, 0.5, 2, true)",
    })
    public void testNewAllowedExpressions(String expression) {
        assertNotNull(evaluate(expression));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "true",
            "false",
            "true && false",
            "5 > 2 ≟ 5 < 2",
            "(1 * 2) / (5 ^(2 - true))",
            "(1 * 2) / (5 ^(2 + 3))",
            "1 * BinomialDist(5, 0.5, 2, true && true)",
            "1 * BinomialDist(5, 0.5, true * 2, true)"
    })
    public void testNewRestrictedExpressions(String expression) {
        assertNull(evaluate(expression));
    }

    private GeoElementND[] evaluate(String expression) {
        EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
        return algebraProcessor.processAlgebraCommandNoExceptionHandling(
                expression, false, new ErrorAccumulator(), evalInfo, null);
    }
}
