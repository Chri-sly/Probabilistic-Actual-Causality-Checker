package de.tuda.aiml.util;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import org.logicng.formulas.*;
import org.logicng.formulas.Formula;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.Equation;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProbabilisticExampleProvider {
    public static ProbabilisticCausalModel donPolice() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable CIExo = f.variable("CI_exo");
        Variable BIExo = f.variable("BI_exo");

        Variable C = f.variable("C");
        Variable B = f.variable("B");
        Variable S = f.variable("S");
        Variable T = f.variable("T");
        Variable D = f.variable("D");

        Formula CFormula = CIExo;
        Formula BFormula = BIExo;
        Formula SFormula = C;
        Formula TFormula = f.and(B, f.not(S));
        Formula DFormula = f.or(S, T);

        Equation CEquation = new Equation(C, CFormula, 0.9);
        Equation BEquation = new Equation(B, BFormula, 0.9);
        Equation SEquation = new Equation(S, SFormula, 0.5);
        Equation TEquation = new Equation(T, TFormula, 0.9);
        Equation DEquation = new Equation(D, DFormula, 1);

        Set<Equation> equations = new HashSet<>(Arrays.asList(CEquation, BEquation, SEquation, TEquation,
                DEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(CIExo, BIExo));

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("donPolice", equations, exogenousVariables, f);
        return causalModel;
    }

    // Pull Out Probability Examples
    public static CausalModel billyAndSuzyHit() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable BTExo = f.variable("BT_exo");
        Variable STExo = f.variable("ST_exo");
        Variable SuzyHitsExo = f.variable("SuzyHits_exo");
        Variable BillyHitsExo = f.variable("BillyHits_exo");

        Variable BT = f.variable("BT");
        Variable ST = f.variable("ST");
        Variable BH = f.variable("BH");
        Variable SH = f.variable("SH");
        Variable BS = f.variable("BS");

        Formula BTFormula = BTExo;
        Formula STFormula = STExo;
        Formula SHFormula = f.and(ST, SuzyHitsExo);
        Formula BHFormula = f.and(f.and(BT, f.not(SH)), BillyHitsExo);
        Formula BSFormula = f.or(SH, BH);

        de.tum.in.i4.hp2sat.causality.Equation BTEquation = new de.tum.in.i4.hp2sat.causality.Equation(BT, BTFormula);
        de.tum.in.i4.hp2sat.causality.Equation STEquation = new de.tum.in.i4.hp2sat.causality.Equation(ST, STFormula);
        de.tum.in.i4.hp2sat.causality.Equation SHEquation = new de.tum.in.i4.hp2sat.causality.Equation(SH, SHFormula);
        de.tum.in.i4.hp2sat.causality.Equation BHEquation = new de.tum.in.i4.hp2sat.causality.Equation(BH, BHFormula);
        de.tum.in.i4.hp2sat.causality.Equation BSEquation = new de.tum.in.i4.hp2sat.causality.Equation(BS, BSFormula);

        Set<de.tum.in.i4.hp2sat.causality.Equation> equations = new HashSet<>(Arrays.asList(BTEquation, STEquation, SHEquation, BHEquation,
                BSEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(BTExo, STExo, SuzyHitsExo, BillyHitsExo));

        CausalModel causalModel = new CausalModel("BillyAndSuzyHit", equations, exogenousVariables, f);
        return causalModel;
    }

    public static CausalModel doctorTreatment() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable MTExo = f.variable("MT_exo");
        Variable OFExo = f.variable("OF_exo");
        Variable TreatmentWorksExo = f.variable("TreatmentWorks_exo");
        Variable OtherFactorsWorkExo = f.variable("OtherFactorsWork_exo");

        Variable MT = f.variable("MT");
        Variable OF = f.variable("OF");
        Variable BMC = f.variable("BMC");

        Formula MTFormula = MTExo;
        Formula OFFormula = OFExo;
        Formula BMCFormula = f.or(f.and(MT, TreatmentWorksExo), f.and(OF, OtherFactorsWorkExo));

        de.tum.in.i4.hp2sat.causality.Equation MTEquation = new de.tum.in.i4.hp2sat.causality.Equation(MT, MTFormula);
        de.tum.in.i4.hp2sat.causality.Equation OFEquation = new de.tum.in.i4.hp2sat.causality.Equation(OF, OFFormula);
        de.tum.in.i4.hp2sat.causality.Equation BMCEquation = new de.tum.in.i4.hp2sat.causality.Equation(BMC, BMCFormula);

        Set<de.tum.in.i4.hp2sat.causality.Equation> equations = new HashSet<>(Arrays.asList(MTEquation, OFEquation, BMCEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(MTExo, OFExo, TreatmentWorksExo, OtherFactorsWorkExo));

        CausalModel causalModel = new CausalModel("DoctorTreatment", equations, exogenousVariables, f);
        return causalModel;
    }

    public static CausalModel twoDoctorTreatment() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable D1TreatmentExo = f.variable("D1_exo");
        Variable D2TreatmentExo = f.variable("D2_exo");
        Variable D1TreatmentWorksExo = f.variable("D1TreatmentWorks_exo");
        Variable D2TreatmentWorksExo = f.variable("D2TreatmentWorks_exo");
        Variable BadInteractionExo = f.variable("BadInteraction_exo");

        Variable D1T = f.variable("D1T");
        Variable D2T = f.variable("D2T");
        Variable D1TEffective = f.variable("D1TEffective");
        Variable D2TEffective = f.variable("D2TEffective");
        Variable BMC = f.variable("BMC");

        Formula D1TFormula = D1TreatmentExo;
        Formula D2TFormula = D2TreatmentExo;
        Formula D1TEffectiveFormula = f.and(D1T, D1TreatmentWorksExo);
        Formula D2TEffectiveFormula = f.and(D2T, D2TreatmentWorksExo);
        Formula BMCFormula = f.or(f.or(f.and(D1TEffective, f.not(D2TEffective), f.and(D2TEffective, f.not(D1TEffective)))),
                f.and(f.and(f.not(BadInteractionExo), D1TEffective), D2TEffective));

        de.tum.in.i4.hp2sat.causality.Equation D1TEquation = new de.tum.in.i4.hp2sat.causality.Equation(D1T, D1TFormula);
        de.tum.in.i4.hp2sat.causality.Equation D2TEquation = new de.tum.in.i4.hp2sat.causality.Equation(D2T, D2TFormula);
        de.tum.in.i4.hp2sat.causality.Equation D1TEffectiveEquation = new de.tum.in.i4.hp2sat.causality.Equation(D1TEffective, D1TEffectiveFormula);
        de.tum.in.i4.hp2sat.causality.Equation D2TEffectiveEquation = new de.tum.in.i4.hp2sat.causality.Equation(D2TEffective, D2TEffectiveFormula);
        de.tum.in.i4.hp2sat.causality.Equation BMCEquation = new de.tum.in.i4.hp2sat.causality.Equation(BMC, BMCFormula);

        Set<de.tum.in.i4.hp2sat.causality.Equation> equations = new HashSet<>(Arrays.asList(D1TEquation, D2TEquation,
                D1TEffectiveEquation, D2TEffectiveEquation, BMCEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(D1TreatmentExo, D2TreatmentExo, D1TreatmentWorksExo, D2TreatmentWorksExo, BadInteractionExo));

        CausalModel causalModel = new CausalModel("TwoDoctorTreatment", equations, exogenousVariables, f);
        return causalModel;
    }
}