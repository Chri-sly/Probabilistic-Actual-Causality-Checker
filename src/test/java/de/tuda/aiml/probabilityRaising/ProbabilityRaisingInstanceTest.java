package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.util.ProbabilisticExampleProvider;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Various tests for the probability raising implementation, includes the example of probability lowering cause,
 * probability raising non-cause etc.
 */
public class ProbabilityRaisingInstanceTest {

    // Variant of the rock-throwing example in which the cause lowers the probability of the effect
    @Test
    public void Suzy_is_cause_but_lowers_prob() throws Exception {
        ProbabilisticCausalModel prob_rock_throwing_cause_lowers_prob = ProbabilisticExampleProvider.prob_rock_throwing_cause_lowers_prob();
        FormulaFactory f = prob_rock_throwing_cause_lowers_prob.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true)));
        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("ST"));
        Formula phi = f.variable("BS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.545, 0.81);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(prob_rock_throwing_cause_lowers_prob, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(prob_rock_throwing_cause_lowers_prob, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(Double.NaN, probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    @Test
    public void Billy_is_not_a_cause_but_raises_prob() throws Exception {
        ProbabilisticCausalModel prob_rock_throwing_cause_lowers_prob = ProbabilisticExampleProvider.prob_rock_throwing_cause_lowers_prob();
        FormulaFactory f = prob_rock_throwing_cause_lowers_prob.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true)));
        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("BT"));
        Formula phi = f.variable("BS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(true, 0.95, 0.5);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(prob_rock_throwing_cause_lowers_prob, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(prob_rock_throwing_cause_lowers_prob, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    // Don's and Police example
    @Test
    public void Corleones_Order_Cause_of_Death() throws Exception {
        ProbabilisticCausalModel Don_Corleone = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = Don_Corleone.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("C"));

        Formula phi = f.variable("D");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.531, 0.81);

        ProbabilityRaisingResult probabilityRaisingResultExpectedNaN =
                new ProbabilityRaisingResult(false, 0.531, Double.NaN);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Don_Corleone, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Don_Corleone, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpectedNaN.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpectedNaN.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(Double.NaN, probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    @Test
    public void Barzinis_Order_Cause_of_Death() throws Exception {
        ProbabilisticCausalModel Don_Corleone = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = Don_Corleone.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("B"));

        Formula phi = f.variable("D");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(true, 0.531, 0.36);

        ProbabilityRaisingResult probabilityRaisingResultExpectedNaN =
                new ProbabilityRaisingResult(false, 0.531, Double.NaN);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Don_Corleone, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Don_Corleone, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpectedNaN.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpectedNaN.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(Double.NaN, probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    @Test
    public void Barometer_Drop_causes_Rain() throws Exception {
        ProbabilisticCausalModel Barometer = ProbabilisticExampleProvider.barometer();
        FormulaFactory f = Barometer.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("BW_exo", true)));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("BD"));

        Formula phi = f.variable("RS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(true, 1.0, 0.0);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Barometer, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Barometer, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }
}
