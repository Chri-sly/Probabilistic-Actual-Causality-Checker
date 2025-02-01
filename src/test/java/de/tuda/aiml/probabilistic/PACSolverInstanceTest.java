package de.tuda.aiml.probabilistic;

import de.tuda.aiml.util.ProbabilisticExampleProvider;
import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PACSolverInstanceTest {
    PACSolver pacSolver;

    @Before
    public void setUp() {
        pacSolver = new PACSolver();
    }

    @Test
    public void Corleones_Order_Cause_of_Death() throws Exception {
        ProbabilisticCausalModel Don_Corleone = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = Don_Corleone.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true), f.literal("SonnyShoots", true),
                f.literal("TurkShoots", false), f.literal("SonnyHits", true), f.literal("TurkHits", false)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("C"));

        Formula phi = f.variable("D");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, true, true, cause,
                        new HashSet<>());

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Don_Corleone, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Barzinis_Order_Not_Cause_of_Death() throws Exception {
        ProbabilisticCausalModel Don_Corleone = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = Don_Corleone.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true), f.literal("SonnyShoots", true),
                f.literal("TurkShoots", false), f.literal("SonnyHits", true), f.literal("TurkHits", false)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("B"));

        Formula phi = f.variable("D");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause, null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Don_Corleone, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    // Assassin (Bogus Prevention)
    @Test
    public void Bodyguard_putting_antidote() throws Exception {
        ProbabilisticCausalModel Assassin = ProbabilisticExampleProvider.prob_assassin();
        FormulaFactory f = Assassin.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", false), f.literal("B_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("B"));

        Formula phi = f.not(f.variable("D"));

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause,  null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Assassin, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Assassin_not_putting_poison() throws Exception {
        ProbabilisticCausalModel Assassin = ProbabilisticExampleProvider.prob_assassin();
        FormulaFactory f = Assassin.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", false), f.literal("B_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.literal("A", false));

        Formula phi = f.not(f.variable("D"));

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause,  null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Assassin, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    // Voting
    @Test
    public void All_vote_for_Suzy() throws Exception {
        ProbabilisticCausalModel Voting = ProbabilisticExampleProvider.voting();
        FormulaFactory f = Voting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("U1_exo", true), f.literal("U2_exo", true), f.literal("U3_exo", true),
                f.literal("U4_exo", true), f.literal("U5_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("V1"));

        Formula phi = f.variable("SW");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause,  null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Voting, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Three_vote_for_Suzy() throws Exception {
        ProbabilisticCausalModel Voting = ProbabilisticExampleProvider.voting();
        FormulaFactory f = Voting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("U1_exo", true), f.literal("U2_exo", true), f.literal("U3_exo", true),
                f.literal("U4_exo", false), f.literal("U5_exo", false)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("V1"));

        Formula phi = f.variable("SW");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, true, true, cause,  new HashSet<>(Arrays.asList()));

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Voting, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Ra226_cause_of_alpha_particle() throws Exception {
        ProbabilisticCausalModel Voting = ProbabilisticExampleProvider.prob_overlapping();
        FormulaFactory f = Voting.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("U1", true), f.literal("U2", true), f.literal("U3", true),
                f.literal("U4", false)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("B"));

        Formula phi = f.variable("E");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause,  null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Voting, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    // Switching Tracks
    @Test
    public void Engineer_flipping_switch_not_cause_of_train_arrival() throws Exception {
        ProbabilisticCausalModel Switching = ProbabilisticExampleProvider.prob_switching_tracks();
        FormulaFactory f = Switching.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("F_exo", true), f.literal("LB_exo", false), f.literal("RB_exo", false),
                f.literal("A_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("F"));

        Formula phi = f.variable("A");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause,  null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Switching, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    // Shooting Deer (Late Preemption)
    @Test
    public void Alice_shooting_cause_of_deers_death() throws Exception {
        ProbabilisticCausalModel Deer = ProbabilisticExampleProvider.prob_shooting_deer();
        FormulaFactory f = Deer.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BI_exo", true), f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("D0_exo", true), f.literal("D1_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("A"));

        Formula phi = f.variable("D101");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, true, true, cause,new HashSet<>(Arrays.asList()));

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Deer, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Bobs_intention_not_cause_of_deers_death() throws Exception {
        ProbabilisticCausalModel Deer = ProbabilisticExampleProvider.prob_shooting_deer();
        FormulaFactory f = Deer.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BI_exo", true), f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("D0_exo", true), f.literal("D1_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("BI"));

        Formula phi = f.variable("D101");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause, null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Deer, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    // Boulder
    @Test
    public void Boulder_fall_not_cause_of_hiker_surviving() throws Exception {
        ProbabilisticCausalModel Boulder = ProbabilisticExampleProvider.prob_boulder();
        FormulaFactory f = Boulder.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("F_exo", true), f.literal("D_exo", true), f.literal("SB_exo", true),
                f.literal("SNB_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("F"));

        Formula phi = f.variable("S");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, false, true, cause, null);

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Boulder, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Boulder_fall_cause_hiker_ducking() throws Exception {
        ProbabilisticCausalModel Boulder = ProbabilisticExampleProvider.prob_boulder();
        FormulaFactory f = Boulder.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("F_exo", true), f.literal("D_exo", true), f.literal("SB_exo", true),
                f.literal("SNB_exo", false)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("F"));

        Formula phi = f.variable("D");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, true, true, cause,  new HashSet<>(Arrays.asList()));

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Boulder, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }

    @Test
    public void Hiker_ducking_causes_surviving() throws Exception {
        ProbabilisticCausalModel Boulder = ProbabilisticExampleProvider.prob_boulder();
        FormulaFactory f = Boulder.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("F_exo", true), f.literal("D_exo", true), f.literal("SB_exo", true),
                f.literal("SNB_exo", false)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("F"));

        Formula phi = f.variable("D");

        ProbabilisticCausalitySolverResult causalitySolverResultExpectedEval =
                new ProbabilisticCausalitySolverResult(true, true, true, cause,  new HashSet<>(Arrays.asList()));

        ProbabilisticCausalitySolverResult result = pacSolver.solve(Boulder, context, phi, cause, ProbabilisticSolvingStrategy.PAC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }
}
