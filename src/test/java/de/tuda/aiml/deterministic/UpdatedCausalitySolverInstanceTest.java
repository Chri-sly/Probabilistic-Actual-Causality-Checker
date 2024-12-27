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
    public void setUp(){
        updatedHPSolver = new UpdatedHPSolver();
    }
    // Rock Throwing
    @Test
    public void Should_FulfillAllAC_When_ST_IsCauseFor_BS() throws Exception {
        CausalModel billySuzy = ExampleProvider.billySuzy();
        FormulaFactory f = billySuzy.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BS");

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>(Arrays.asList(f.literal("BT", false))));

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(billySuzy, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    @Test
    public void Should_FulfillAC1AC3Only_When_BT_IsCauseFor_BS() throws Exception {
        CausalModel billySuzy = ExampleProvider.billySuzy();
        FormulaFactory f = billySuzy.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BT");

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, false, true, cause, null);

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(billySuzy, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    // Voting
    @Test
    public void Should_FulfillAllACs_When_Suzy_Wins_And_V1_votes_for_her()
            throws Exception {
        CausalModel voting = DeterministicExampleProvider.voting();
        FormulaFactory f = voting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("U1_exo", true), f.literal("U2_exo", true), f.literal("U3_exo", true),
                f.literal("U4_exo", true), f.literal("U5_exo", true)));
        Set<Literal> cause = new HashSet<>(Arrays.asList(f.literal("V1", true)));
        Formula phi = f.literal("SW", true);

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, true, true, cause, new HashSet<>(Arrays.asList(f.literal("V3", true))));

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(voting, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    @Test
    public void Should_FulfillAllACs_When_Suzy_Wins_And_all_and_V1_votes_for_her() throws Exception {
        CausalModel voting = DeterministicExampleProvider.voting();
        FormulaFactory f = voting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("U1_exo", true), f.literal("U2_exo", true), f.literal("U3_exo", true),
                f.literal("U4_exo", false), f.literal("U5_exo", false)));
        Set<Literal> cause = new HashSet<>(Arrays.asList(f.literal("V1", true)));
        Formula phi = f.literal("SW", true);

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, true, true, cause, new HashSet<>());

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(voting, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    // Prisoners
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

    // Prisoners Extended
    @Test
    public void Original_Should_Not_FulfillAllACs_When_A_IsCauseFor_D() throws Exception {
        CausalModel prisonersExtended = DeterministicExampleProvider.prisonersExtended();
        FormulaFactory f = prisonersExtended.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("A")));
        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, false, true, cause, null);

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(prisonersExtended, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpectedEval, causalitySolverResultActual);
    }

    // Forest Fire
    @Test
    public void Should_FulfillAllACs_When_NotLAndNotMD_IsCauseFor_NotFF_Given_NotLExoAndNotMDExo_CONJUNCTIVE()
            throws Exception {
        CausalModel forestFire = ExampleProvider.forestFire(false);
        FormulaFactory f = forestFire.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("L_exo", false), f.literal("MD_exo", false)));
        Set<Literal> cause = new HashSet<>(Arrays.asList(f.literal("L", false),
                f.literal("MD", false)));
        Formula phi = f.literal("FF", false);

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, true, false, cause, new HashSet<>());

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(forestFire, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    @Test
    public void Should_FulfillAC1AC2Only_When_NotLAndNotMD_IsCauseFor_NotFF_Given_NotLExoAndNotMDExo_DISJUNCTIVE()
            throws Exception {
        CausalModel forestFire = ExampleProvider.forestFire(true);
        FormulaFactory f = forestFire.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("L_exo", false), f.literal("MD_exo", false)));
        Set<Literal> cause = new HashSet<>(Arrays.asList(f.literal("L", false),
                f.literal("MD", false)));
        Formula phi = f.literal("FF", false);

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, true, false, cause, new HashSet<>());

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(forestFire, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    // Optical Voting
    @Test
    public void Optical_voting() throws Exception {
        CausalModel opticalVoting = DeterministicExampleProvider.opticalVoting();
        FormulaFactory f = opticalVoting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true)));
        Set<Literal> cause = new HashSet<>(Arrays.asList(f.literal("B", true), f.literal("C", true)));
        Formula phi = f.literal("WIN", true);

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, true, true, cause, new HashSet<>(Arrays.asList(f.literal("A", false))));

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(opticalVoting, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }

    @Test
    public void Optical_voting_false() throws Exception {
        CausalModel opticalVoting = DeterministicExampleProvider.opticalVoting();
        FormulaFactory f = opticalVoting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true)));
        Set<Literal> cause = new HashSet<>(Arrays.asList(f.literal("B", true)));
        Formula phi = f.literal("WIN", true);

        CausalitySolverResult causalitySolverResultExpected =
                new CausalitySolverResult(true, false, true, cause, null);

        CausalitySolverResult causalitySolverResultActual = updatedHPSolver.solve(opticalVoting, context, phi, cause, SolvingStrategy.UPDATED_HP);

        assertEquals(causalitySolverResultExpected, causalitySolverResultActual);
    }
}
