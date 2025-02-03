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

public class InterventionalProbabilityRaisingInstanceTest {

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
                new ProbabilityRaisingResult(true, 0.98, 0.8);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(prob_rock_throwing, phi, cause, context);

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
                new ProbabilityRaisingResult(true, 0.98, 0.9);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(prob_rock_throwing, phi, cause, context);

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
                new ProbabilityRaisingResult(true, 0.545, 0.09);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(prob_rock_throwing_cause_lowers_prob, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
    }

    @Test
    public void Billy_is_not_a_cause_but_raises_prob() throws Exception {
        ProbabilisticCausalModel prob_rock_throwing_cause_lowers_prob = ProbabilisticExampleProvider.prob_rock_throwing_cause_lowers_prob();
        FormulaFactory f = prob_rock_throwing_cause_lowers_prob.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("ST_exo", true), f.literal("BNotFollowsPlan_exo", false)));
        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("BT"));
        Formula phi = f.variable("BS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(true, 0.95, 0.5);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(prob_rock_throwing_cause_lowers_prob, phi, cause, context);

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
                new ProbabilityRaisingResult(true, 0.73, 0.46);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(Forest_Fire, phi, cause, context);

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
                new ProbabilityRaisingResult(true, 0.73, 0.55);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(Forest_Fire, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
    }

    // Police parade
    @Test
    public void Corleones_Order_not_Cause_of_Death() throws Exception {
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

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(Don_Corleone, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 1e-10);
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
                new ProbabilityRaisingResult(true, 0.531, 0.45);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(Don_Corleone, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    // Barometer
    @Test
    public void Barometer_Drop_not_causes_Rain() throws Exception {
        ProbabilisticCausalModel Barometer = ProbabilisticExampleProvider.barometer();
        FormulaFactory f = Barometer.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("BW_exo", true)));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("BD"));

        Formula phi = f.variable("RS");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.5, 0.5);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(Barometer, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }

    @Test
    public void Rain_not_causes_drop_Barometer() throws Exception {
        ProbabilisticCausalModel Barometer = ProbabilisticExampleProvider.barometer();
        FormulaFactory f = Barometer.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("BW_exo", true)));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("RS"));

        Formula phi = f.variable("BD");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.5, 0.5);

        ProbabilityRaisingResult probabilityRaisingResultActual = InterventionalProbabilityRaising.computeActual(Barometer, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected.isCause(), probabilityRaisingResultActual.isCause());
        assertEquals(probabilityRaisingResultExpected.getPC(), probabilityRaisingResultActual.getPC(), 1e-10);
        assertEquals(probabilityRaisingResultExpected.getNotPC(), probabilityRaisingResultActual.getNotPC(), 0.0);
    }
}
