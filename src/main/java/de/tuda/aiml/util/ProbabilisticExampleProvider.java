package de.tuda.aiml.util;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.Equation;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;

import org.logicng.formulas.*;
import org.logicng.formulas.Formula;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;

import java.util.*;

/**
 * Example provider for probabilistic causal examples and probabilistic versions of examples mentioned in
 * {@link de.tum.in.i4.hp2sat.util.ExampleProvider} and {@link DeterministicExampleProvider}.
 */
public class ProbabilisticExampleProvider {

    /**
     * Probabilistic version of the rock-throwing example. Example taken from: Halpern, Actual Causation, pp: 47
     *
     * BT = 1: Billy throws his rock; BT = 0: Billy does not throw his rock
     * ST = 1: Suzy throws her rock; ST = 0: Suzy does not throw her rock
     * BH = 1: Billy hits the bottle; BH = 0: Billy does not hit the bottle
     * SH = 1: Suzy hits the bottle; SH = 0: Suzy does not hit the bottle
     * BS = 1: The bottle shatters; BS = 0: The bottle does not shatter
     *
     * @return The Probabilistic Causal Model of the rock-throwing example
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel prob_rock_throwing() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable BTExo = f.variable("BT_exo");
        Variable STExo = f.variable("ST_exo");
        Variable SHExo = f.variable("SH_exo");
        Variable BHExo = f.variable("BH_exo");

        Variable BT = f.variable("BT");
        Variable ST = f.variable("ST");
        Variable BH = f.variable("BH");
        Variable SH = f.variable("SH");
        Variable BS = f.variable("BS");

        Formula BTFormula = BTExo;
        Formula STFormula = STExo;
        Formula SHFormula = f.and(ST, SHExo);
        Formula BHFormula = f.and(f.and(BT, f.not(SH)), BHExo);
        Formula BSFormula = f.or(SH, BH);

        Equation BTEquation = new Equation(BT, BTFormula);
        Equation STEquation = new Equation(ST, STFormula);
        Equation SHEquation = new Equation(SH, SHFormula);
        Equation BHEquation = new Equation(BH, BHFormula);
        Equation BSEquation = new Equation(BS, BSFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(BTEquation, STEquation, SHEquation, BHEquation, BSEquation));

        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(BTExo, 1.0);
        exogenousVariables.put(STExo, 1.0);
        exogenousVariables.put(SHExo, 0.9);
        exogenousVariables.put(BHExo, 0.8);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Probabilistic rock-throwing", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Probabilistic version of the forest fire example.
     * Example taken from: Jingzhi, On probabilistic reasoning of actual causation, pp: 22
     *
     * L = 1: Lightning does strike at the forest; L = 0: Lightning does not strike at the forest
     * MD = 1: The arsonist drops a lit match; MD = 0: The arsonist does not drop a lit match
     * FF = 1: The forest catches fire; FF = 0: The forest does not catch fire
     *
     * @return Probabilistic Causal Model of the forest fire example
     */
    public static ProbabilisticCausalModel prob_forest_fire() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable LExo = f.variable("L_exo");
        Variable MDExo = f.variable("MD_exo");
        Variable LightningStartsFireExo = f.variable("LightningStartsFire_exo");
        Variable MatchStartsFireExo = f.variable("MatchStartsFire_exo");
        Variable OtherFactorsStartFireExo = f.variable("OtherFactorsStartFire_exo");

        Variable L = f.variable("L");
        Variable MD = f.variable("MD");
        Variable FF = f.variable("FF");

        Formula LFormula = LExo;
        Formula MDFormula = MDExo;
        Formula FFFormula =  f.or(f.or(f.and(L, LightningStartsFireExo), f.and(MD, MatchStartsFireExo)), OtherFactorsStartFireExo);

        Equation LEquation = new Equation(L, LFormula);
        Equation MDEquation = new Equation(MD, MDFormula);
        Equation FFEquation = new Equation(FF, FFFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(LEquation, MDEquation, FFEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(LExo, 0.3);
        exogenousVariables.put(MDExo, 0.2);
        exogenousVariables.put(LightningStartsFireExo, 0.4);
        exogenousVariables.put(MatchStartsFireExo, 0.5);
        exogenousVariables.put(OtherFactorsStartFireExo, 0.1);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Probabilistic ForestFire", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Preemption example of Don Corleone and Don Barzini trying to assassinate Police Chief McClusky.
     * Example taken from: Luke Fenton-Glynn, A proposed probabilistic extension of the Halpern and Pearl Definition
     * of Actual Cause, pp: 28
     *
     * C = 1: Don Corleone orders Sonny to shoot; C = 0: Don Corleone does not order Sonny to shoot
     * B = 1: Don Barzini orders Turk to shoot; B = 0: Don Barzini does not order Turk to shoot
     * S = 1: Sonny shoots at McClusky; S = 0: Sonny does not shoot at McClusky
     * T = 1: Turk shoots at McClusky; T = 0: Turk does not shoot at McClusky
     * D = 1: McClusky dies; D = 0: McClusky does not die
     *
     * @return The probabilistic Causal Model of the Don and Police example
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel donPolice() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable CIExo = f.variable("CI_exo");
        Variable BIExo = f.variable("BI_exo");
        Variable U1 = f.variable("SonnyShoots");
        Variable U2 = f.variable("TurkShoots");
        Variable U3 = f.variable("SonnyHits");
        Variable U4 = f.variable("TurkHits");

        Variable C = f.variable("C");
        Variable B = f.variable("B");
        Variable S = f.variable("S");
        Variable T = f.variable("T");
        Variable D = f.variable("D");

        Formula CFormula = CIExo;
        Formula BFormula = BIExo;
        Formula SFormula = f.and(C, U1);
        Formula TFormula = f.and(f.and(B, f.not(S)), U2);
        Formula DFormula = f.or(f.and(S, U3), f.and(T, U4));

        Equation CEquation = new Equation(C, CFormula);
        Equation BEquation = new Equation(B, BFormula);
        Equation SEquation = new Equation(S, SFormula);
        Equation TEquation = new Equation(T, TFormula);
        Equation DEquation = new Equation(D, DFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(CEquation, BEquation, SEquation, TEquation,
                DEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(CIExo, 0.9);
        exogenousVariables.put(BIExo, 0.9);
        exogenousVariables.put(U1, 0.9);
        exogenousVariables.put(U2, 0.9);
        exogenousVariables.put(U3, 0.5);
        exogenousVariables.put(U4, 0.9);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("donPolice", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Probabilistic version of the voting example. Here five Voters either vote for Suzy or Billy.
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
    public static ProbabilisticCausalModel voting() throws InvalidCausalModelException, ParserException {
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

        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(U1Exo, 0.5);
        exogenousVariables.put(U2Exo, 0.5);
        exogenousVariables.put(U3Exo, 0.5);
        exogenousVariables.put(U4Exo, 0.5);
        exogenousVariables.put(U5Exo, 0.5);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Voting", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Bogus Prevention example of an assassin poisoning a victim's coffee, while a bodyguard may provide an antidote.
     * Example taken from: Jingzhi Fang, On probabilistic actual causation, pp: 43
     *
     * A = 1: Assassin puts the poison in the coffee; P = 0: Assassin does not put the poison in the coffee
     * B = 1: Bodyguard puts the antidote in the coffee; A = 0: Bodyguard does not put the antidote in the coffee
     * D = 1: Victim dies; D = 1: Victim does not die
     *
     * @return
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel prob_assassin() throws InvalidCausalModelException, ParserException {
        FormulaFactory f = new FormulaFactory();
        Variable AExo = f.variable("A_exo");
        Variable BExo = f.variable("B_exo");
        Variable DeathByPoison = f.variable("DeathByPoison");
        Variable DeathElse = f.variable("DeathElse");

        Variable A = f.variable("A");
        Variable B = f.variable("B");
        Variable D = f.variable("D");

        Formula AFormula = AExo;
        Formula BFormula = BExo;

        PropositionalParser p = new PropositionalParser(f);
        Formula DFormula = p.parse("(A & ~B & DeathByPoison) | (A & B & DeathElse) | (~A & B & DeathElse) | (~A & ~B & DeathElse)");

        Equation PEquation = new Equation(A, AFormula);
        Equation AEquation = new Equation(B, BFormula);
        Equation DEquation = new Equation(D, DFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(PEquation, AEquation, DEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(AExo, 0.2);
        exogenousVariables.put(BExo, 0.2);
        exogenousVariables.put(DeathByPoison, 0.9);
        exogenousVariables.put(DeathElse, 0.1);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Assassin", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Overlapping example of atoms U-238 and Ra-226 that possibly decay into another atom. U-238 could decay into Th-234 and an alpha particle
     * and Ra-226 could decay into Rn-222 and an alpha particle. Thus, both atoms U-238 and Ra-226 overlap on the possibility to produce an alpha particle.
     * Example taken from: Jingzhi Fang, On probabilistic actual causation, pp: 39-40
     *
     * A = 1: U-238 appears in the box; A = 0: U-238 does not appear in the box
     * B = 1: Ra-226 appears in the box; B = 0: Ra-226 does not appear in the box
     * C = 1: Th-234 appears in the box; C = 0: Th-234 does not appear in the box
     * D = 1: Rn-222 appears in the box; D = 0: Rn-222 does not appear in the box
     * E = 1: Alpha particle appears in the box; E = 0: Alpha particle does not appear in the box
     *
     * @return
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel prob_overlapping() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable U1 = f.variable("U1");
        Variable U2 = f.variable("U2");
        Variable U3 = f.variable("U3");
        Variable U4 = f.variable("U4");

        Variable A = f.variable("A");
        Variable B = f.variable("B");
        Variable C = f.variable("C");
        Variable D = f.variable("D");
        Variable E = f.variable("E");

        Formula AFormula = U1;
        Formula BFormula = U2;
        Formula CFormula = f.and(A, U3);
        Formula DFormula = f.and(B, U4);
        Formula EFormula = f.or(f.and(A, U3), f.and(B, U4));

        Equation AEquation = new Equation(A, AFormula);
        Equation BEquation = new Equation(B, BFormula);
        Equation CEquation = new Equation(C, CFormula);
        Equation DEquation = new Equation(D, DFormula);
        Equation EEquation = new Equation(E, EFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(AEquation, BEquation, CEquation, DEquation, EEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(U1, 0.5);
        exogenousVariables.put(U2, 0.5);
        exogenousVariables.put(U3, 0.8);
        exogenousVariables.put(U4, 0.7);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Overlapping", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Probabilistic version of the track switching example.
     * Example taken from: Jingzhi Fang, On probabilistic actual causation, pp: 43;
     * The original deterministic example is from: J. Y. Halpern. Actual Causality, pp: 38-39
     *
     * F = 1: The engineer flips the switch from left track to right track; F = 0: The engineer does not flip the switch
     * LB = 1: The left track is blocked; LB = 0: The left track is not blocked
     * RB = 1: The right track is blocked; RB = 0: The right track is not blocked
     * A = 1: The train arrives at the destination; A = 0: The train does not arrive at the destination
     *
     * @return Probabilistic Causal Model of the Track Switching example
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel prob_switching_tracks() throws InvalidCausalModelException, ParserException {
        FormulaFactory f = new FormulaFactory();
        Variable FExo = f.variable("F_exo");
        Variable LBExo = f.variable("LB_exo");
        Variable RBExo = f.variable("RB_exo");
        Variable AExo = f.variable("A_exo");

        Variable F = f.variable("F");
        Variable LB = f.variable("LB");
        Variable RB = f.variable("RB");
        Variable A = f.variable("A");

        Formula FFormula = FExo;
        Formula LBFormula = LBExo;
        Formula RBFormula = RBExo;

        PropositionalParser p = new PropositionalParser(f);
        Formula AFormula = p.parse("(F & LB & ~RB & A) | (F & ~LB & ~RB & A) | (~F & ~LB & RB & A) | (~F & ~LB & ~RB & A)");

        Equation FEquation = new Equation(F, FFormula);
        Equation LBEquation = new Equation(LB, LBFormula);
        Equation RBEquation = new Equation(RB, RBFormula);
        Equation AEquation = new Equation(A, AFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(FEquation, LBEquation, RBEquation, AEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(FExo, 0.6);
        exogenousVariables.put(LBExo, 0.6);
        exogenousVariables.put(RBExo, 0.3);
        exogenousVariables.put(AExo, 0.9);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Switching Tracks", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Probabilistic version of the late preemption example of Alice and Bob shooting a deer.
     * Example taken from: L. Fenton-Glynn. Causation. Cambridge University Press, pp: 73-75
     *
     * BI = 1: Bob intends to shoot the deer; BI = 0: Bob does not intent to shoot the deer
     * A = 1: Alice shoots at the deer by 1:00; A = 0: Alice does not shoot at the deer
     * B = 1: Bob shoots at the deer by 1:01; B = 0: Bob does not shoot at the deer
     * D100 = 1: Deer is dead by 1:00; D100 = 0: Deer is alive by 1:00
     * D101 = 1: Deer is dead by 1:01; D101 = 0: Deer is alive by 1:01
     *
     * @return Probabilistic causal model of the Deer shooting example
     * @throws InvalidCausalModelException
     * @throws ParserException
     */
    public static ProbabilisticCausalModel prob_shooting_deer() throws InvalidCausalModelException, ParserException {
        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);
        Variable BIExo = f.variable("BI_exo");
        Variable AExo = f.variable("A_exo");
        Variable BExo = f.variable("B_exo");
        Variable D0Exo = f.variable("D0_exo");
        Variable D1Exo = f.variable("D1_exo");

        Variable BI = f.variable("BI");
        Variable A = f.variable("A");
        Variable B = f.variable("B");
        Variable D100 = f.variable("D100");
        Variable D101 = f.variable("D101");

        Formula BIFormula = BIExo;
        Formula AFormula = AExo;
        Formula D100Formula = f.and(A, D0Exo);
        Formula BFormula = f.and(f.and(BI, f.not(D100)), BExo);
        Formula D101Formula = p.parse("(B & D100) | (B & ~D100 & D1Exo) | (~B & D100)");

        Equation BIEquation = new Equation(BI, BIFormula);
        Equation AEquation = new Equation(A, AFormula);
        Equation D100Equation = new Equation(D100, D100Formula);
        Equation BEquation = new Equation(B, BFormula);
        Equation D101Equation = new Equation(D101, D101Formula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(BIEquation, AEquation, D100Equation, BEquation, D101Equation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(BIExo, 1.0);
        exogenousVariables.put(AExo, 0.9);
        exogenousVariables.put(D0Exo, 0.8);
        exogenousVariables.put(D1Exo, 0.8);
        exogenousVariables.put(BExo, 0.9);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Deer Shooting", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Probabilistic version of the boulder example in which a boulder might fall on a hiker that can duck and survive or die.
     * Example taken from: Jingzhi Fang, On probabilistic actual causation, pp: 50 - 51;
     *
     * F = 1: The boulder falls down; F = 0: The boulder does not fall down
     * D = 1: The hiker ducks; D = 0: The hiker does not duck
     * S = 1: The hiker survives; S = 0: The hiker does not survive
     *
     * @return Probabilistic Causal Model of the boulder example
     * @throws InvalidCausalModelException
     * @throws ParserException
     */
    public static ProbabilisticCausalModel prob_boulder() throws InvalidCausalModelException, ParserException {
        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);
        Variable FExo = f.variable("F_exo");
        Variable DExo = f.variable("D_exo");
        Variable SBExo = f.variable("SB_exo");
        Variable SNBExo = f.variable("SNB_exo");

        Variable F = f.variable("F");
        Variable D = f.variable("D");
        Variable S = f.variable("S");

        Formula FFormula = FExo;
        Formula DFormula = f.and(F, DExo);
        Formula SFormula = p.parse("(F & D & SBExo) | (~F & D & SNBExo) | (~F & ~F & SNBExo)");

        Equation FEquation = new Equation(F, FFormula);
        Equation DEquation = new Equation(D, DFormula);
        Equation SEquation = new Equation(S, SFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(FEquation, DEquation, SEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(FExo, 0.1);
        exogenousVariables.put(DExo, 0.7);
        exogenousVariables.put(SBExo, 0.7);
        exogenousVariables.put(SNBExo, 0.9);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Boulder falling", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * Variant of the rock throwing example in which Suzy throwing the rock lowers the probability of the bottle shattering
     * even though she actually hits the bottle.
     * Example taken from: Hitchcock, Christopher, "Probabilistic Causation" (4.2 Problem cases)
     *
     * BT = 1: Billy throws his rock; BT = 0: Billy does not throw his rock
     * ST = 1: Suzy throws her rock; ST = 0: Suzy does not throw her rock
     * BH = 1: Billy hits the bottle; BH = 0: Billy does not hit the bottle
     * SH = 1: Suzy hits the bottle; SH = 0: Suzy does not hit the bottle
     * BS = 1: The bottle shatters; BS = 0: The bottle does not shatter
     *
     * @return Probabilistic Causal Model of a probability lowering variant of the rock-throwing example
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel prob_rock_throwing_cause_lowers_prob() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable BNFPxo = f.variable("BNotFollowsPlan_exo");
        Variable STExo = f.variable("ST_exo");
        Variable SHExo = f.variable("SH_exo");
        Variable BHExo = f.variable("BH_exo");

        Variable BT = f.variable("BT");
        Variable ST = f.variable("ST");
        Variable BH = f.variable("BH");
        Variable SH = f.variable("SH");
        Variable BS = f.variable("BS");

        Formula BTFormula = f.or(f.and(f.not(STExo), f.not(BNFPxo)), f.and(STExo, BNFPxo));
        Formula STFormula = STExo;
        Formula SHFormula = f.and(ST, SHExo);
        Formula BHFormula = f.and(f.and(BT, f.not(SH)), BHExo);
        Formula BSFormula = f.or(SH, BH);

        Equation BTEquation = new Equation(BT, BTFormula);
        Equation STEquation = new Equation(ST, STFormula);
        Equation SHEquation = new Equation(SH, SHFormula);
        Equation BHEquation = new Equation(BH, BHFormula);
        Equation BSEquation = new Equation(BS, BSFormula);

        Set<de.tum.in.i4.hp2sat.causality.Equation> equations = new HashSet<>(Arrays.asList(BTEquation, STEquation, SHEquation, BHEquation,
                BSEquation));
        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(BNFPxo, 0.1);
        exogenousVariables.put(STExo, 0.9);
        exogenousVariables.put(SHExo, 0.5);
        exogenousVariables.put(BHExo, 0.9);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Rock throwing, cause lowers probability", equations, exogenousVariables, f);
        return causalModel;
    }

    /**
     * The barometer example in which a man reads off from a barometer (which might have a defect) and then it starts
     * to rain.
     * Example taken from: Hitchcock, Christopher, "Probabilistic Causation" (1.1 Problems for Regularity Theories)
     *
     * AP = 1: The air pressure drops; AP = 0: The air pressure does not drop
     * BD = 1: The barometer shows a low value; BD = 0: The barometer shows a high value
     * RS = 1: Rain starts to fall; RS = 0: Rain does not fall
     *
     * @return
     * @throws InvalidCausalModelException
     */
    public static ProbabilisticCausalModel barometer() throws InvalidCausalModelException{
        FormulaFactory f = new FormulaFactory();
        Variable APExo = f.variable("AP_exo");
        Variable BWExo = f.variable("BW_exo");

        Variable AP = f.variable("AP");
        Variable BD = f.variable("BD");
        Variable RS = f.variable("RS");

        Formula APFormula = APExo;
        Formula BDFormula = f.and(AP, BWExo);
        Formula RSFormula = AP;

        Equation APEquation = new Equation(AP, APFormula);
        Equation BDEquation = new Equation(BD, BDFormula);
        Equation RSEquation = new Equation(RS, RSFormula);

        Set<Equation> equations = new HashSet<>(Arrays.asList(APEquation, BDEquation, RSEquation));

        Map<Variable, Double> exogenousVariables = new HashMap<>();
        exogenousVariables.put(APExo, 0.5);
        exogenousVariables.put(BWExo, 0.9);

        ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Barometer", equations, exogenousVariables, f);
        return causalModel;
    }

    // #################################################################################################################
    // ################################################ PULL OUT PROBABILITY ###########################################
    // #################################################################################################################
    // Examples from Halpern's chapter 2.5 Probability and Causality

    // Example mentioned during chapter 2.5
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

    // Example 2.5.1
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

    // Example 2.5.2
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

    // Example 2.5.4
    public static CausalModel billyAndSuzyTopple() throws InvalidCausalModelException {
        FormulaFactory f = new FormulaFactory();
        Variable BTExo = f.variable("BT_exo");
        Variable STExo = f.variable("ST_exo");
        Variable ToppleBothHitExo = f.variable("ToppleBothHit_exo");
        Variable ToppleBillyHitExo = f.variable("ToppleBillyHit_exo");
        Variable ToppleSuzyHitExo = f.variable("ToppleSuzyHit_exo");

        Variable BT = f.variable("BT");
        Variable ST = f.variable("ST");
        Variable BH = f.variable("BH");
        Variable SH = f.variable("SH");
        Variable BTO = f.variable("BTO");

        Formula BTFormula = BTExo;
        Formula STFormula = STExo;
        Formula SHFormula = ST;
        Formula BHFormula = BT;
        Formula BTOFormula = f.or(f.or(f.and(BH, SH), ToppleBothHitExo), f.and(BH, f.not(SH), ToppleBillyHitExo),
                f.and(SH, f.not(BH), ToppleSuzyHitExo));

        de.tum.in.i4.hp2sat.causality.Equation BTEquation = new de.tum.in.i4.hp2sat.causality.Equation(BT, BTFormula);
        de.tum.in.i4.hp2sat.causality.Equation STEquation = new de.tum.in.i4.hp2sat.causality.Equation(ST, STFormula);
        de.tum.in.i4.hp2sat.causality.Equation SHEquation = new de.tum.in.i4.hp2sat.causality.Equation(SH, SHFormula);
        de.tum.in.i4.hp2sat.causality.Equation BHEquation = new de.tum.in.i4.hp2sat.causality.Equation(BH, BHFormula);
        de.tum.in.i4.hp2sat.causality.Equation BTOEquation = new de.tum.in.i4.hp2sat.causality.Equation(BTO, BTOFormula);

        Set<de.tum.in.i4.hp2sat.causality.Equation> equations = new HashSet<>(Arrays.asList(BTEquation, STEquation, SHEquation, BHEquation,
                BTOEquation));
        Set<Variable> exogenousVariables = new HashSet<>(Arrays.asList(BTExo, STExo, ToppleBothHitExo, ToppleBillyHitExo, ToppleSuzyHitExo));

        CausalModel causalModel = new CausalModel("BillyAndSuzyTopple", equations, exogenousVariables, f);
        return causalModel;
    }
}