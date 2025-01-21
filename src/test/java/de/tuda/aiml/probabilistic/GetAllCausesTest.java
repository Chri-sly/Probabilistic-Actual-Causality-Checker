package de.tuda.aiml.probabilistic;

import de.tuda.aiml.util.ProbabilisticExampleProvider;
import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class GetAllCausesTest {
    PACSolver pacSolver;
    PCSolver pcSolver;
    PCPrimeSolver pcPrimeSolver;

    List<ProbabilisticSolvingStrategy> solvingStrategies = Arrays.asList(ProbabilisticSolvingStrategy.PC, ProbabilisticSolvingStrategy.PCPrime,
            ProbabilisticSolvingStrategy.PAC);

    @Before
    public void setUp() {
        pacSolver = new PACSolver();
        pcSolver = new PCSolver();
        pcPrimeSolver = new PCPrimeSolver();
    }

    /**
     * Compute the result for each strategy and assert the identical outcome. The order of the results may differ between
     * the strategies.
     */
    private void testGetAllCauses(ProbabilisticCausalModel causalModel, Set<Literal> context, Formula phi,
                                  Map<ProbabilisticSolvingStrategy, Set<ProbabilisticCausalitySolverResult>> causalitySolverResultsExpected)
            throws Exception {
        for (ProbabilisticSolvingStrategy solvingStrategy : solvingStrategies) {
            Set<ProbabilisticCausalitySolverResult> causalitySolverResultsActual = null;
            if (solvingStrategy == ProbabilisticSolvingStrategy.PAC) {
                causalitySolverResultsActual =
                        pacSolver.getAllCauses(causalModel, context, phi, solvingStrategy,
                                causalModel.getFormulaFactory());
            } else if (solvingStrategy == ProbabilisticSolvingStrategy.PCPrime) {
                causalitySolverResultsActual = pcPrimeSolver.getAllCauses(causalModel, context, phi, solvingStrategy,
                        causalModel.getFormulaFactory());
            } else if (solvingStrategy == ProbabilisticSolvingStrategy.PC) {
                causalitySolverResultsActual = pcSolver.getAllCauses(causalModel, context, phi, solvingStrategy,
                        causalModel.getFormulaFactory());
            }

            assertEquals("Error for " + solvingStrategy, causalitySolverResultsExpected.get(solvingStrategy),
                    causalitySolverResultsActual);
        }
    }

    // Rock throwing
    @Test
    public void Rock_Throwing () throws Exception {
        ProbabilisticCausalModel RockThrowing = ProbabilisticExampleProvider.prob_rock_throwing();
        FormulaFactory f = RockThrowing.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true), f.literal("BH_exo", false),
                f.literal("SH_exo", true)
        ));

        Formula phi = f.variable("BS");

        Set<ProbabilisticCausalitySolverResult> allCausesExpectedEvalPC = new HashSet<>(Arrays.asList(
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("SH"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("BS"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("BT"))),
                        new HashSet<>(Arrays.asList(f.literal("ST", false)))),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("ST"))),
                        new HashSet<>(Arrays.asList(f.literal("BT", false))))
                ));

        Set<ProbabilisticCausalitySolverResult> allCausesExpectedEvalPCPrime = new HashSet<>(Arrays.asList(
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("SH"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("BS"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("ST"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("BT"))),
                        new HashSet<>(Arrays.asList(f.literal("ST", false))))
        ));

        // PAC is the strictest and considers the actual context, thus, BT can not be a cause.
        Set<ProbabilisticCausalitySolverResult> allCausesExpectedEvalPAC = new HashSet<>(Arrays.asList(
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("SH"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("BS"))),
                        new HashSet<>()),
                new ProbabilisticCausalitySolverResult(true, true, true,
                        new HashSet<>(Collections.singletonList(f.variable("ST"))),
                        new HashSet<>())
        ));


        Map<ProbabilisticSolvingStrategy, Set<ProbabilisticCausalitySolverResult>> allCausesExpected =
                new HashMap<ProbabilisticSolvingStrategy, Set<ProbabilisticCausalitySolverResult>>() {
                    {
                        put(ProbabilisticSolvingStrategy.PC, allCausesExpectedEvalPC);
                        put(ProbabilisticSolvingStrategy.PCPrime, allCausesExpectedEvalPCPrime);
                        put(ProbabilisticSolvingStrategy.PAC, allCausesExpectedEvalPAC);
                    }
                };

        testGetAllCauses(RockThrowing, context, phi, allCausesExpected);
    }
}
