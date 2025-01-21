package de.tuda.aiml.util;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.Equation;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

import java.util.*;

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

    /**
     * The majority voting example. Here five Voters either vote for Suzy or Billy.
     * The one with the majority of votes wins the election. Example taken from: Jingzhi Fang, On probabilistic actual causation, pp: 33
     *
     * V1 = 1: Voter 1 votes for Suzy; V1 = 0: Voter 1 votes vor Billy
     * V2 = 1: Voter 2 votes for Suzy; V2 = 0: Voter 2 votes vor Billy
     * V3 = 1: Voter 3 votes for Suzy; V3 = 0: Voter 3 votes vor Billy
     * V4 = 1: Voter 4 votes for Suzy; V4 = 0: Voter 4 votes vor Billy
     * V5 = 1: Voter 5 votes for Suzy; V5 = 0: Voter 5 votes vor Billy
     * SW = 1: Suzy wins the election; SW = 0: Suzy loses the election, thus, Billy wins
     *
     * @return Probabilistic Causal Model of the Voting example
     * @throws InvalidCausalModelException
     */
    public static CausalModel voting() throws InvalidCausalModelException, ParserException {
        FormulaFactory f = new FormulaFactory();
        Variable U1Exo = f.variable("U1_exo");
        Variable U2Exo = f.variable("U2_exo");
        Variable U3Exo = f.variable("U3_exo");
        Variable U4Exo = f.variable("U4_exo");
        Variable U5Exo = f.variable("U5_exo");

        Variable V1 = f.variable("V1");
        Variable V2 = f.variable("V2");
        Variable V3 = f.variable("V3");
        Variable V4 = f.variable("V4");
        Variable V5 = f.variable("V5");
        Variable SW = f.variable("SW");

        Formula V1Formula = U1Exo;
        Formula V2Formula = U2Exo;
        Formula V3Formula = U3Exo;
        Formula V4Formula = U4Exo;
        Formula V5Formula = U5Exo;

        PropositionalParser p = new PropositionalParser(f);
        Formula SWFormula = p.parse("(V1 & V2 & V3) | (V1 & V2 & V4) | (V1 & V2 & V5) | (V1 & V3 & V4) | (V1 & V3 & V5) | " +
                "(V1 & V4 & V5) | (V2 & V3 & V4) | (V2 & V3 & V5) | (V3 & V4 & V5) | " +
                "(V1 & V2 & V3 & V4) | (V1 & V2 & V3 & V5) | (V2 & V3 & V4 & V5) | (V1 & V2 & V3 & V4 & V5)");

        Equation V1Equation = new Equation(V1, V1Formula);
        Equation V2Equation = new Equation(V2, V2Formula);
        Equation V3Equation = new Equation(V3, V3Formula);
        Equation V4Equation = new Equation(V4, V4Formula);
        Equation V5Equation = new Equation(V5, V5Formula);
        Equation SWEquation = new Equation(SW, SWFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(V1Equation, V2Equation, V3Equation, V4Equation, V5Equation, SWEquation));

        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(U1Exo, U2Exo, U3Exo, U4Exo, U5Exo));

        CausalModel causalModel = new CausalModel("Voting", equations, exogenousVariables, f);
        return causalModel;
    }

    public static CausalModel opticalVoting() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable AExo = f.variable("A_exo");

        Variable B = f.variable("B");
        Variable A = f.variable("A");
        Variable C = f.variable("C");
        Variable D = f.variable("D");
        Variable DPrime = f.variable("DPrime");
        Variable WIN = f.variable("WIN");

        Formula AFormula = AExo;
        Formula BFormula = A;
        Formula CFormula = A;
        Formula DFormula = f.and(B, C);
        Formula DPrimeFormula = f.and(B, f.not(A));
        Formula WINFormula = f.or(f.or(A, D), DPrime);

        Equation AEquation = new Equation(A, AFormula);
        Equation BEquation = new Equation(B, BFormula);
        Equation CEquation = new Equation(C, CFormula);
        Equation DEquation = new Equation(D, DFormula);
        Equation DPrimeEquation = new Equation(DPrime, DPrimeFormula);
        Equation WINEquation = new Equation(WIN, WINFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(AEquation, BEquation, CEquation, DEquation, DPrimeEquation,
                WINEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(AExo));

        CausalModel causalModel = new CausalModel("optical Voting", equations, exogenousVariables, f);
        return causalModel;
    }
}
