package de.tuda.aiml.util;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.Equation;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Additional examples for the HP definitions. Most of the popular examples, like the rock-throwing or the forest fire,
 * can be found in {@link de.tum.in.i4.hp2sat.util.ExampleProvider}
 */
public class DeterministicExampleProvider {

    /**
     * The prisoners example that led to the modification of the original HP definition to the updated HP definition.
     * The example is taken from: Halpern, Actual Causation, pp: 58
     *
     * A = 1: Person A loads B's gun; A = 0: Person A does not load B's gun
     * B = 1: Person B shoots the gun; B = 0: Person B does not shoot the gun
     * C = 1: Person C loads and shoots their gun; C = 0: Person C neither loads nor shoots their gun
     * D = 1: The prisoner dies; D = 0: The prisoner does not die
     *
     * @return Causal Model of the prisoners example
     * @throws InvalidCausalModelException
     */
    public static CausalModel prisoners() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable AExo = f.variable("A_exo");
        Variable BExo = f.variable("B_exo");
        Variable CExo = f.variable("C_exo");

        Variable A = f.variable("A");
        Variable B = f.variable("B");
        Variable C = f.variable("C");
        Variable D = f.variable("D");

        Formula AFormula = AExo;
        Formula BFormula = BExo;
        Formula CFormula = CExo;
        Formula DFormula = f.or(f.and(A, B), C);

        Equation AEquation = new Equation(A, AFormula);
        Equation BEquation = new Equation(B, BFormula);
        Equation CEquation = new Equation(C, CFormula);
        Equation DEquation = new Equation(D, DFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(AEquation, BEquation, CEquation, DEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(AExo, BExo, CExo));

        CausalModel causalModel = new CausalModel("Prisoners", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * The extended version of the prisoners example such that the original HP definition provides the correct outcome.
     * The example is taken from: Halpern, Actual Causality
     *
     * A = 1: Person A loads B's gun; A = 0: Person A does not load B's gun
     * B = 1: Person B shoots the gun; B = 0: Person B does not shoot the gun
     * B' = 1: Person B shoots a loaded gun; B = 0: Person B does not shoot a loaded gun
     * C = 1: Person C loads and shoots their gun; C = 0: Person C neither loads nor shoots their gun
     * D = 1: The prisoner dies; D = 0: The prisoner does not die
     *
     * @return Causal Model of the extended prisoners example
     * @throws InvalidCausalModelException
     */
    public static CausalModel prisonersExtended() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable AExo = f.variable("A_exo");
        Variable BExo = f.variable("B_exo");
        Variable CExo = f.variable("C_exo");

        Variable A = f.variable("A");
        Variable B = f.variable("B");
        Variable B2 = f.variable("B'");
        Variable C = f.variable("C");
        Variable D = f.variable("D");

        Formula AFormula = AExo;
        Formula BFormula = BExo;
        Formula B2Formula = f.and(A, B);
        Formula CFormula = CExo;
        Formula DFormula = f.or(B2, C);

        Equation AEquation = new Equation(A, AFormula);
        Equation BEquation = new Equation(B, BFormula);
        Equation B2Equation = new Equation(B2, B2Formula);
        Equation CEquation = new Equation(C, CFormula);
        Equation DEquation = new Equation(D, DFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(AEquation, BEquation, B2Equation, CEquation, DEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(AExo, BExo, CExo));

        CausalModel causalModel = new CausalModel("PrisonersExtended", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * The responsibility example of the firing squad in which three excellent marksman shoot at the same time at a prisoner
     * while one has a live bullet and the rest have blanks.
     * The example is taken from: Halpern, Actual Causation, pp: 170
     *
     * M1 = 1: Marksman 1 has the live bullet; M1 = 0: Marksman 1 has a blank
     * M2 = 1: Marksman 2 has the live bullet; M2 = 0: Marksman 2 has a blank
     * M3 = 1: Marksman 3 has the live bullet; M3 = 0: Marksman 3 has a blank
     * D = 1: The prisoner dies; D = 0: The prisoner does not die
     *
     * @return Causal Model of the firing Squad example
     * @throws InvalidCausalModelException
     */
    public static CausalModel firingSquad() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable M1Exo = f.variable("M1_exo");
        Variable M2Exo = f.variable("M2_exo");
        Variable M3Exo = f.variable("M3_exo");

        Variable M1 = f.variable("M1");
        Variable M2 = f.variable("M2");
        Variable M3 = f.variable("M3");
        Variable D = f.variable("D");

        Formula M1Formula = M1Exo;
        Formula M2Formula = M2Exo;
        Formula M3Formula = M3Exo;
        Formula DFormula = f.or(f.and(M1, f.not(M2), f.not(M3)), f.and(f.not(M1), M2, f.not(M3)), f.and(f.not(M1), f.not(M2), M3));

        Equation M1Equation = new Equation(M1, M1Formula);
        Equation M2Equation = new Equation(M2, M2Formula);
        Equation M3Equation = new Equation(M3, M3Formula);
        Equation DEquation = new Equation(D, DFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(M1Equation, M2Equation, M3Equation, DEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(M1Exo, M2Exo, M3Exo));

        CausalModel causalModel = new CausalModel("firingSquad", equations, exogenousVariables, f);
        return causalModel;
    }
}
