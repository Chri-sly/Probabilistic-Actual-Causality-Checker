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

    // Rock Throwing
    @Test
    public void Suzy_causes_bottle_shatter() throws Exception {
        ProbabilisticCausalModel prob_rock_throwing = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = prob_rock_throwing.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true), f.literal("BT_exo", true)));
        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("ST"));
        Formula phi = f.variable("BS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.98, Double.NaN);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(prob_rock_throwing, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(prob_rock_throwing, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
    }

    @Test
    public void Billy_not_causes_bottle_shatter() throws Exception {
        ProbabilisticCausalModel prob_rock_throwing = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = prob_rock_throwing.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true), f.literal("BT_exo", true)));
        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("BT"));
        Formula phi = f.variable("BS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.98, Double.NaN);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(prob_rock_throwing, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(prob_rock_throwing, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
    }

    // Extended Rock Throwing
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

    // Forest fire
    @Test
    public void Match_drop_Causes_Forest_fire() throws Exception {
        ProbabilisticCausalModel Forest_Fire = ProbabilisticExampleProvider.prob_forest_fire();
        FormulaFactory f = Forest_Fire.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("L_exo", true), f.literal("MD_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("MD"));

        Formula phi = f.variable("FF");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.73, Double.NaN);

        ProbabilityRaisingResult probabilityRaisingTrueResultExpected =
                new ProbabilityRaisingResult(true, 0.61, 0.21);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Forest_Fire, 5000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Forest_Fire, phi, cause, context);

        assertEquals(probabilityRaisingTrueResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingTrueResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingTrueResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
    }

    @Test
    public void Lightning_Causes_Forest_fire() throws Exception {
        ProbabilisticCausalModel Forest_Fire = ProbabilisticExampleProvider.prob_forest_fire();
        FormulaFactory f = Forest_Fire.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("L_exo", true), f.literal("MD_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("L"));

        Formula phi = f.variable("FF");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.73, Double.NaN);

        ProbabilityRaisingResult probabilityRaisingResultExpectedGeneral =
                new ProbabilityRaisingResult(true, 0.53, 0.23);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Forest_Fire, 3000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Forest_Fire, phi, cause, context);

        assertEquals(probabilityRaisingResultExpectedGeneral.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpectedGeneral.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpectedGeneral.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
    }

    // Police parade
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

    // Barometer
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

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Barometer, 5000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Barometer, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    @Test
    public void Rain_causes_drop_Barometer() throws Exception {
        ProbabilisticCausalModel Barometer = ProbabilisticExampleProvider.barometer();
        FormulaFactory f = Barometer.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("BW_exo", true)));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("RS"));

        Formula phi = f.variable("BD");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(true, 1.0, 0.0);

        ProbabilityRaisingResult probabilityRaisingResultGeneral  = ProbabilityRaising.compute(Barometer, 4000, phi, cause);
        ProbabilityRaisingResult probabilityRaisingResultActual = ProbabilityRaising.computeActual(Barometer, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultGeneral.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultGeneral.getPC(), 1e-1);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultGeneral.getNotPC(), 1e-1);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }
}
