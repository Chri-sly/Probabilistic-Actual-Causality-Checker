package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.util.ProbabilisticExampleProvider;
import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.util.ExampleProvider;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProbabilityRaisingInstanceTest {

    @Test
    public void Original_Should_FulfillAllACs_When_A_IsCauseFor_D() throws Exception {
        ProbabilisticCausalModel rock_throwing = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = rock_throwing.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true), f.literal("BT_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("BH")));
        Formula phi = f.variable("BS");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>(Arrays.asList(f.literal("B", true), f.literal("C", false))));

        Boolean b  = ProbabilityRaising.compute(rock_throwing, 1000, phi, cause);

        assertEquals(causalitySolverResultExpectedEval, b);
    }

    @Test
    public void Suzy_is_cause_but_lowers_prob() throws Exception {
        ProbabilisticCausalModel prob_rock_throwing_cause_lowers_prob = ProbabilisticExampleProvider.prob_rock_throwing_cause_lowers_prob();
        FormulaFactory f = prob_rock_throwing_cause_lowers_prob.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true), f.literal("BNotFollowsPlan_exo", true)));
        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("ST"));
        //cause.add(f.variable("SH"));
        Formula phi = f.variable("BS");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>(Arrays.asList(f.literal("B", true), f.literal("C", false))));

        Boolean b  = ProbabilityRaising.compute(prob_rock_throwing_cause_lowers_prob, 1000, phi, cause);

        assertEquals(causalitySolverResultExpectedEval, b);
    }

    @Test
    public void Corleones_Order_Cause_of_Death() throws Exception {
        ProbabilisticCausalModel Don_Corleone = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = Don_Corleone.getFormulaFactory();

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("C"));

        Formula phi = f.variable("D");

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>());

        Boolean b  = ProbabilityRaising.compute(Don_Corleone, 1000, phi, cause);

        assertEquals(causalitySolverResultExpectedEval, b);
    }

    @Test
    public void AssassinPoison() throws Exception {
        ProbabilisticCausalModel assassin = ProbabilisticExampleProvider.assassin();
        FormulaFactory f = assassin.getFormulaFactory();

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("A"));

        Formula phi = f.not(f.variable("D"));

        CausalitySolverResult causalitySolverResultExpectedEval =
                new CausalitySolverResult(true, true, true, cause,
                        new HashSet<>());

        Boolean b  = ProbabilityRaising.compute(assassin, 1000, phi, cause);

        assertEquals(causalitySolverResultExpectedEval, b);
    }
}
