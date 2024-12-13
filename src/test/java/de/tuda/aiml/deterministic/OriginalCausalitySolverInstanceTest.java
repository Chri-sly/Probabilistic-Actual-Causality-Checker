package de.tuda.aiml.deterministic;

import de.tuda.aiml.util.DeterministicExampleProvider;
import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class OriginalCausalitySolverInstanceTest {
    OriginalHPSolver originalHPSolver;
    UpdatedHPSolver updatedHPSolver;
    List<SolvingStrategy> solvingStrategies = Arrays.asList(SolvingStrategy.ORIGINAL_HP, SolvingStrategy.UPDATED_HP);

    @Before
    public void setUp() throws Exception {
        originalHPSolver = new OriginalHPSolver();
        updatedHPSolver = new UpdatedHPSolver();
    }

    private void testSolve(CausalModel causalModel, Set<Literal> context, Formula phi, Set<Literal> cause,
                           Map<SolvingStrategy, Set<CausalitySolverResult>> causalitySolverResultsExpected,
                           SolvingStrategy... excludedStrategies) throws
            Exception {
        for (SolvingStrategy solvingStrategy : solvingStrategies) {
            if (Arrays.asList(excludedStrategies).contains(solvingStrategy)) {
                continue;
            }
            CausalitySolverResult causalitySolverResultActual = null;
            if (solvingStrategy == SolvingStrategy.ORIGINAL_HP) {
                causalitySolverResultActual =
                        originalHPSolver.solve(causalModel, context, phi, cause, solvingStrategy);
            } else {
                causalitySolverResultActual =
                        updatedHPSolver.solve(causalModel, context, phi, cause, solvingStrategy);
            }
            Matcher[] matchers = causalitySolverResultsExpected.get
                    (solvingStrategy).stream().map(CoreMatchers::is).toArray(Matcher[]::new);
            assertThat("Error for " + solvingStrategy, causalitySolverResultActual,
                    CoreMatchers.anyOf(matchers));
        }
    }

    // #################################################################################################################
    // ################################################ PRISONERS ##################################################
    // #################################################################################################################
    //region PRISONERS
    //region [PRISONERS] A_exo = 1; B_exo = 0; C_exo = 1
    // The example that Halpern mentions in chapter 2.8 AC2 original vs AC2 updated

    // Wrong case of Original Variant
    @Test
    public void Original_Should_FulfillAllACs_When_A_IsCauseFor_D() throws Exception {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("A")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>(Arrays.asList(f.literal("B", true), f.literal("C", false))));

        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    @Test
    public void Original_Should_FulfillAllACs_When_C_IsCauseFor_D() throws Exception {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("C")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause, new HashSet<>());


        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    @Test
    public void Original_Should_Not_FulfillAC1_When_B_IsCauseFor_D() throws InvalidCausalModelException {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("B")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(false, false, true, cause, null);

        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    //region [PRISONERS] A_exo = 1; B_exo = 1; C_exo = 0
    @Test
    public void Original_Should_FulfillAllACs_When_A_IsCauseFor_D_B_Shoots_C_Not() throws Exception {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", true),
                f.literal("C_exo", false)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("A")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>());

        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    @Test
    public void Original_Should_FulfillAllAC_When_B_IsCauseFor_D_B_Shoots_C_Not() throws InvalidCausalModelException {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", true),
                f.literal("C_exo", false)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("B")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause, new HashSet<>());

        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    //region [PRISONERS] A_exo = 0; B_exo = 1; C_exo = 1

    @Test
    public void Original_Should_Not_FulfillAC1_When_A_IsCauseFor_D_B_Shoots_A_Not_Load() throws Exception {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", false), f.literal("B_exo", true),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("A")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(false, false, true, cause, null);

        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    // Wrong case of Original Variant
    @Test
    public void Original_Should_FulfillAllAC_When_B_IsCauseFor_D_B_Shoots_A_Not_Load() throws InvalidCausalModelException {
        CausalModel prisoners = DeterministicExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", false), f.literal("B_exo", true),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("B")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>(Arrays.asList(f.literal("A", true), f.literal("C", false))));

        CausalitySolverResult causalitySolverResultActual = originalHPSolver.solve(prisoners, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }
}
