package de.tuda.aiml.pullOut;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.util.ProbabilisticExampleProvider;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class CausalityPullOutInstanceTest {


    // Rock-Throwing
    @Test
    public void Prob_Rock_Throwing_PullOutProbability_Prob_Susy_is_cause() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BS");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        assertEquals(0.9/0.98, result, 1e-7);
    }

    @Test
    public void Prob_Rock_Throwing_PullOutProbability_Prob_Susy_is_cause_know_Billy_missed() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("BH_exo", false)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BS");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        assertEquals(1.0, result, 1e-7);
    }

    @Test
    public void Prob_Rock_Throwing_PullOutProbability_Prob_Billy_is_cause() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("BT")));
        Formula phi = f.variable("BS");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        assertEquals(0.08 / 0.98, result, 1e-7);
    }

    @Test
    public void Prob_Rock_Throwing_PullOutProbability_Prob_Billy_is_cause_know_Suzy_missed() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SH_exo", false)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("BT")));
        Formula phi = f.variable("BS");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        assertEquals(1.0, result, 1e-7);
    }

    // Doctor-Treatment
    @Test
    public void Prob_Doctor_Treatment_PullOutProbability() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.doctorTreatment();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("MT")));
        Formula phi = f.variable("BMC");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        assertEquals(0.9/0.91, result, 0.1);
    }

    @Test
    public void Prob_Doctor_Treatment_PullOutProbability_OF() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.doctorTreatment();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("MT_exo", true)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("OF")));
        Formula phi = f.variable("BMC");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        assertEquals(0.1/0.91, result, 0.1);
    }

    @Test
    public void Prob_Doctor_Treatment_PullOutProbability_OF_AND_MT() throws Exception {
        ProbabilisticCausalModel billySuzy = ProbabilisticExampleProvider.doctorTreatment();
        FormulaFactory f = billySuzy.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("MT_exo", true)));

        Set<Literal> cause = new HashSet<>(Arrays.asList(f.variable("OF"), f.variable("MT")));
        Formula phi = f.variable("BMC");

        double result = PullOutProbability.solve(billySuzy, context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        assertEquals(0.09/0.91, result, 0.1);
    }
}
