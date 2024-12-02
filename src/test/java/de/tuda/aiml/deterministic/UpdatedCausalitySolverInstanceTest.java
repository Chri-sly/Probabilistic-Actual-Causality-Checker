package de.tuda.aiml.deterministic;

import de.tuda.aiml.util.DeterministicExampleProvider;
import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.util.ExampleProvider;
import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UpdatedCausalitySolverInstanceTest {
    UpdatedHPSolver updatedHPSolver;

    @Before
    public void setUp() throws Exception {
        updatedHPSolver = new UpdatedHPSolver();
    }

    @Test
    public void Updated_Should_FulfillNotAC2_When_A_IsCauseFor_D() throws Exception {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("A")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, false, true, cause, null);

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }
}
