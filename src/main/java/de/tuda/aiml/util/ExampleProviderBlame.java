package de.tuda.aiml.util;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.Equation;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;

import java.util.*;


public class ExampleProviderBlame {
    public static CausalModel voters() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable ONEExo = f.variable("ONE_exo");
        Variable TWOExo = f.variable("TWO_exo");
        Variable THREEExo = f.variable("THREE_exo");
        Variable FOURExo = f.variable("FOUR_exo");
        Variable FIVEExo = f.variable("FIVE_exo");
        Variable SIXExo = f.variable("SIX_exo");
        Variable SEVENExo = f.variable("SEVEN_exo");
        Variable EIGHTExo = f.variable("EIGHT_exo");
        Variable NINEExo = f.variable("NINE_exo");
        Variable TENExo = f.variable("TEN_exo");
        Variable ELEVENExo = f.variable("ELEVEN_exo");


        Variable ONE = f.variable("ONE");
        Variable TWO = f.variable("TWO");
        Variable THREE = f.variable("THREE");
        Variable FOUR = f.variable("FOUR");
        Variable FIVE = f.variable("FIVE");
        Variable SIX = f.variable("SIX");
        Variable SEVEN = f.variable("SEVEN");
        Variable EIGHT = f.variable("EIGHT");
        Variable NINE = f.variable("NINE");
        Variable TEN = f.variable("TEN");
        Variable ELEVEN = f.variable("ELEVEN");
        Variable WON = f.variable("WON");

        Formula OneFormula = ONEExo;
        Formula TWOFormula = TWOExo;
        Formula THREEFormula = THREEExo;
        Formula FOURFormula = FOURExo;
        Formula FIVEFormula = FIVEExo;
        Formula SIXFormula = SIXExo;
        Formula SEVENFormula = SEVENExo;
        Formula EIGHTFormula = EIGHTExo;
        Formula NINEFormula = NINEExo;
        Formula TENFormula = TENExo;
        Formula ELEVENFormula = ELEVENExo;

        return null;
    }

    public static CausalModel firingSquad() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable ONEExo = f.variable("ONE_exo");
        Variable TWOExo = f.variable("TWO_exo");
        Variable THREEExo = f.variable("THREE_exo");

        Variable ONE = f.variable("ONE");
        Variable TWO = f.variable("TWO");
        Variable THREE = f.variable("THREE");
        Variable DEATH = f.variable("DEATH");

        Formula OneFormula = ONEExo;
        Formula TWOFormula = TWOExo;
        Formula THREEFormula = THREEExo;
        Formula DEATHFormula = f.or(f.and(ONE, f.not(TWO), f.not(THREE)), f.and(f.not(ONE), TWO, f.not(THREE)), f.and(f.not(ONE), f.not(TWO), THREE));

        Equation ONEEquation = new Equation(ONE, OneFormula);
        Equation TWOEquation = new Equation(TWO, TWOFormula);
        Equation THREEEquation = new Equation(THREE, THREEFormula);
        Equation DEATHEquation = new Equation(DEATH, DEATHFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(ONEEquation, TWOEquation, THREEEquation, DEATHEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(ONEExo, TWOExo, THREEExo));

        CausalModel causalModel = new CausalModel("firingSquad", equations, exogenousVariables, f);
        return causalModel;
    }
}
