package de.tuda.aiml.pullOut;

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
import org.logicng.formulas.Variable;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class CausalityPullOutInstanceTest {


    // #################################################################################################################
    // ################################################ ROCK-THROWING ##################################################
    // #################################################################################################################
    //region ROCK-THROWING
    //region [ROCK-THROWING] BT_exo = 1; ST_exo = 1
    @Test
    public void Prob_Rock_Throwing_PullOutProbability_Susy_is_cause_prob() throws Exception {
        CausalModel billySuzy1 = ProbabilisticExampleProvider.billyAndSuzyHit();
        FormulaFactory f = billySuzy1.getFormulaFactory();

        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", true), f.literal("BillyHits_exo", true)));
        Set<Literal> context2 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", true), f.literal("BillyHits_exo", false)));
        Set<Literal> context3 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", false), f.literal("BillyHits_exo", true)));
        Set<Literal> context4 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", false), f.literal("BillyHits_exo", false)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BS");

        List<Set<Literal>> contexts = new ArrayList<>();
        contexts.add(context1);
        contexts.add(context2);
        contexts.add(context3);
        contexts.add(context4);

        Map<Variable, Double> map = new HashMap<>();
        map.put(f.variable("BT_exo"), 1.0);
        map.put(f.variable("ST_exo"), 1.0);
        map.put(f.variable("SuzyHits_exo"), 0.9);
        map.put(f.variable("BillyHits_exo"), 0.8);

        Set<Literal> know = new HashSet<>();
        know.add(f.literal("BT_exo", true));
        know.add(f.literal("ST_exo", true));

        PullOutProbability pullOutProbability = new PullOutProbability(billySuzy1, contexts, phi, cause, SolvingStrategy.BRUTE_FORCE, map, know);
        double result = pullOutProbability.solve();

        assertEquals(0.9/0.98, result, 1e-7);
    }

    @Test
    public void Prob_Rock_Throwing_PullOutProbability_Susy_is_cause_prob_know_Billy_missed() throws Exception {
        CausalModel billySuzy1 = ProbabilisticExampleProvider.billyAndSuzyHit();
        FormulaFactory f = billySuzy1.getFormulaFactory();

        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", true), f.literal("BillyHits_exo", true)));
        Set<Literal> context2 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", true), f.literal("BillyHits_exo", false)));
        Set<Literal> context3 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", false), f.literal("BillyHits_exo", true)));
        Set<Literal> context4 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("SuzyHits_exo", false), f.literal("BillyHits_exo", false)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BS");

        List<Set<Literal>> contexts = new ArrayList<>();
        contexts.add(context1);
        contexts.add(context2);
        contexts.add(context3);
        contexts.add(context4);

        Map<Variable, Double> map = new HashMap<>();
        map.put(f.variable("BT_exo"), 1.0);
        map.put(f.variable("ST_exo"), 1.0);
        map.put(f.variable("SuzyHits_exo"), 0.9);
        map.put(f.variable("BillyHits_exo"), 0.8);

        Set<Literal> know = new HashSet<>();
        know.add(f.literal("BT_exo", true));
        know.add(f.literal("ST_exo", true));
        know.add(f.literal("BillyHits_exo", false));

        PullOutProbability pullOutProbability = new PullOutProbability(billySuzy1, contexts, phi, cause, SolvingStrategy.BRUTE_FORCE, map, know);
        double result = pullOutProbability.solve();

        assertEquals(1.0, result, 1e-7);
    }

    @Test
    public void Rock_Throwing_PullOutProbability_Prob() throws Exception {
        CausalModel billySuzy = ProbabilisticExampleProvider.billyAndSuzyHit();
        FormulaFactory f = billySuzy.getFormulaFactory();

        List<Set<Literal>> contexts = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            double random = Math.random();
            double random2 = Math.random();
            Set<Literal> context = new HashSet<>();
            context.add(f.literal("BT_exo", true));
            context.add(f.literal("ST_exo", true));
            boolean b = (random < 0.9) ? context.add(f.literal("SuzyHits_exo", true)) : context.add(f.literal("SuzyHits_exo", false));
            boolean c = (random2 < 0.8) ? context.add(f.literal("BillyHits_exo", true)) : context.add(f.literal("BillyHits_exo", false));
            contexts.add(context);
        }
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi = f.variable("BS");

        PullOutProbability pullOutProbability = new PullOutProbability(billySuzy, contexts, phi, cause, SolvingStrategy.BRUTE_FORCE);
        double result = pullOutProbability.solveProb();
        System.out.println(result);
    }

    // #################################################################################################################
    // ################################################ Doctor-Treatment ##################################################
    // #################################################################################################################
    //region Doctor-Treatment
    //region [Doctor-Treatment] MT_exo = 1; OF_exo = 1
    @Test
    public void Doctor_Treatment_PullOutProbability() throws Exception {
        CausalModel doctorTreatment = ProbabilisticExampleProvider.doctorTreatment();
        FormulaFactory f = doctorTreatment.getFormulaFactory();

        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context2 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context3 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context4 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context5 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context6 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context7 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context8 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", false), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context9 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context10 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context11 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context12 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", false), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context13 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context14 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",false), f.literal("OtherFactorsWork_exo", true)));
        Set<Literal> context15 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", false)));
        Set<Literal> context16 = new HashSet<>(Arrays.asList(
                f.literal("MT_exo", true), f.literal("OF_exo", true), f.literal("TreatmentWorks_exo",true), f.literal("OtherFactorsWork_exo", true)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("MT")));
        Formula phi = f.variable("BMC");

        List<Set<Literal>> contexts = new ArrayList<>();
        contexts.add(context1);
        contexts.add(context2);
        contexts.add(context3);
        contexts.add(context4);
        contexts.add(context5);
        contexts.add(context6);
        contexts.add(context7);
        contexts.add(context8);
        contexts.add(context9);
        contexts.add(context10);
        contexts.add(context11);
        contexts.add(context12);
        contexts.add(context13);
        contexts.add(context14);
        contexts.add(context15);
        contexts.add(context16);

        Map<Variable, Double> map = new HashMap<>();
        map.put(f.variable("MT_exo"), 0.8);
        map.put(f.variable("OF_exo"), 0.9);
        map.put(f.variable("TreatmentWorks_exo"), 0.9);
        map.put(f.variable("OtherFactorsWork_exo"), 0.1);

        Set<Literal> know = new HashSet<>();
        know.add(f.literal("MT_exo", true));

        PullOutProbability pullOutProbability = new PullOutProbability(doctorTreatment, contexts, phi, cause, SolvingStrategy.BRUTE_FORCE, map, know);
        double result = pullOutProbability.solve();

        System.out.println(result);
        assertEquals(0.9/0.91, result, 0.1);
    }


    @Test
    public void Doctor_Treatment_PullOutProbability_Prob() throws Exception {
        CausalModel doctorTreatment = ProbabilisticExampleProvider.doctorTreatment();
        FormulaFactory f = doctorTreatment.getFormulaFactory();

        List<Set<Literal>> contexts = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            double random = Math.random();
            double random2 = Math.random();
            double random3 = Math.random();
            double random4 = Math.random();
            Set<Literal> context = new HashSet<>();
            context.add(f.literal("MT_exo", true));
            context.add(f.literal("OF_exo", false));
            boolean b = (random < 0.8) ? context.add(f.literal("DoctorHeals_exo", true)) : context.add(f.literal("DoctorHeals_exo", false));
            boolean c = (random2 < 0.9) ? context.add(f.literal("OtherFactorsHeals_exo", true)) : context.add(f.literal("OtherFactorsHeals_exo", false));
            boolean d = (random3 < 0.9) ? context.add(f.literal("TreatmentRecovers_exo", true)) : context.add(f.literal("TreatmentRecovers_exo", false));
            boolean e = (random4 < 0.1) ? context.add(f.literal("OtherFactorsRecovers_exo", true)) : context.add(f.literal("OtherFactorsRecovers_exo", false));
            contexts.add(context);
        }
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("MT")));
        Formula phi = f.variable("BMC");

        PullOutProbability pullOutProbability = new PullOutProbability(doctorTreatment, contexts, phi, cause, SolvingStrategy.BRUTE_FORCE);
        double result = pullOutProbability.solveProb();
        System.out.println(result);
    }

    // #################################################################################################################
    // ################################################ Two Doctor-Treatment ##################################################
    // #################################################################################################################
    //region Two Doctor-Treatment
    //region [Two Doctor-Treatment] MT_exo = 1; OF_exo = 1
    @Test
    public void Two_Doctor_Treatment_PullOutProbability() throws Exception {
        CausalModel twoDoctorTreatment = ProbabilisticExampleProvider.twoDoctorTreatment();
        FormulaFactory f = twoDoctorTreatment.getFormulaFactory();

        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",false), f.literal("D2TreatmentWorks_exo", false), f.literal("BadInteraction_exo", false)));
        Set<Literal> context2 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",false), f.literal("D2TreatmentWorks_exo", false), f.literal("BadInteraction_exo", true)));
        Set<Literal> context3 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",false), f.literal("D2TreatmentWorks_exo", true), f.literal("BadInteraction_exo", false)));
        Set<Literal> context4 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",false), f.literal("D2TreatmentWorks_exo", true), f.literal("BadInteraction_exo", true)));
        Set<Literal> context5 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",true), f.literal("D2TreatmentWorks_exo", false), f.literal("BadInteraction_exo", false)));
        Set<Literal> context6 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",true), f.literal("D2TreatmentWorks_exo", false), f.literal("BadInteraction_exo", true)));
        Set<Literal> context7 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",true), f.literal("D2TreatmentWorks_exo", true), f.literal("BadInteraction_exo", false)));
        Set<Literal> context8 = new HashSet<>(Arrays.asList(
                f.literal("D1_exo", true), f.literal("D2_exo", true), f.literal("D1TreatmentWorks_exo",true), f.literal("D2TreatmentWorks_exo", true), f.literal("BadInteraction_exo", true)));

        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("D1T")));
        Formula phi = f.variable("BMC");

        List<Set<Literal>> contexts = new ArrayList<>();
        contexts.add(context1);
        contexts.add(context2);
        contexts.add(context3);
        contexts.add(context4);
        contexts.add(context5);
        contexts.add(context6);
        contexts.add(context7);
        contexts.add(context8);

        Map<Variable, Double> map = new HashMap<>();
        map.put(f.variable("D1_exo"), 1.0);
        map.put(f.variable("D2_exo"), 1.0);
        map.put(f.variable("D1TreatmentWorks_exo"), 0.9);
        map.put(f.variable("D2TreatmentWorks_exo"), 0.9);
        map.put(f.variable("BadInteraction_exo"), 0.8);

        Set<Literal> know = new HashSet<>();
        know.add(f.literal("D1TreatmentWorks_exo", true));
        know.add(f.literal("D2TreatmentWorks_exo", true));

        PullOutProbability pullOutProbability = new PullOutProbability(twoDoctorTreatment, contexts, phi, cause, SolvingStrategy.UPDATED_HP, map, know);
        double result = pullOutProbability.solve();

        System.out.println(result);
        assertEquals(1.0, result, 1e-7);
    }
}
