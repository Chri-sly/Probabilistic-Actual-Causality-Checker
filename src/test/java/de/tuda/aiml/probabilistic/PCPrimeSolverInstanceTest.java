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

public class PCPrimeSolverInstanceTest {
    PCPrimeSolver pcPrimeSolver;

    @Before
    public void setUp() throws Exception {
        pcPrimeSolver = new PCPrimeSolver();
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

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Don_Corleone, context, phi, cause, ProbabilisticSolvingStrategy.PC);

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
                new ProbabilisticCausalitySolverResult(true, true, true, cause, new HashSet<>(Arrays.asList(f.literal("C", false))));

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Don_Corleone, context, phi, cause, ProbabilisticSolvingStrategy.PC);

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
                new ProbabilisticCausalitySolverResult(true, true, true, cause,
                        new HashSet<>(Arrays.asList(f.literal("A", true))));

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Assassin, context, phi, cause, ProbabilisticSolvingStrategy.PC);

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
                new ProbabilisticCausalitySolverResult(true, true, true, cause,  new HashSet<>(Arrays.asList(f.literal("B", false))));

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Assassin, context, phi, cause, ProbabilisticSolvingStrategy.PC);

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
                new ProbabilisticCausalitySolverResult(true, true, true, cause,  new HashSet<>(Arrays.asList(f.literal("V3", false))));

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Voting, context, phi, cause, ProbabilisticSolvingStrategy.PC);

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

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Voting, context, phi, cause, ProbabilisticSolvingStrategy.PC);

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

        ProbabilisticCausalitySolverResult result = pcPrimeSolver.solve(Voting, context, phi, cause, ProbabilisticSolvingStrategy.PC);

        assertEquals(causalitySolverResultExpectedEval, result);
    }
}
