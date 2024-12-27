package de.tuda.aiml.probabilistic;

import de.tuda.aiml.util.UtilityMethods;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.util.Util;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PC'
 */
public class PCPrimeSolver extends ProbabilisticCausalitySolver {
    /**
     * Overrides {@link ProbabilisticCausalitySolver#solve(ProbabilisticCausalModel, Set, Formula, Set, ProbabilisticSolvingStrategy)}.
     *
     * @param causalModel     the underlying probabilistic causal model
     * @param context         the context
     * @param phi             the phi
     * @param cause           the cause
     * @param solvingStrategy the applied solving strategy
     * @return for each PC', true if fulfilled, false else
     * @throws InvalidCausalModelException thrown if internally generated causal models are invalid
     */
    public ProbabilisticCausalitySolverResult solve(ProbabilisticCausalModel causalModel, Set<Literal> context, Formula phi,
                                                    Set<Literal> cause, ProbabilisticSolvingStrategy solvingStrategy)
            throws InvalidCausalModelException {
        FormulaFactory f = causalModel.getFormulaFactory();
        Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModel, context);
        Pair<Boolean, Boolean> pcPrime1Tuple = fulfillsPC1(evaluation, phi, cause);
        boolean pcPrime1 = pcPrime1Tuple.first() && pcPrime1Tuple.second();
        Set<Literal> w = fulfillsPCPrime2(causalModel, phi, cause, context, evaluation, f);
        boolean pcPrime2 = w != null;
        boolean pcPrime3 = fulfillsPCPrime3(causalModel, phi, cause, context, evaluation, pcPrime1Tuple.first(), f);
        ProbabilisticCausalitySolverResult causalitySolverResult = new ProbabilisticCausalitySolverResult(pcPrime1, pcPrime2, pcPrime3, cause, w);
        return causalitySolverResult;
    }

    /**
     * Checks if PC'2 is fulfilled.
     *
     * @param causalModel     the underlying causal model
     * @param phi             the phi
     * @param cause           the cause for which we check PC'2
     * @param context         the context
     * @param evaluation      the original evaluation of variables
     * @param f               a formula factory
     * @return W if PC'2 fulfilled, else null
     * @throws InvalidCausalModelException thrown if internally generated causal models are invalid
     */
    private Set<Literal> fulfillsPCPrime2(ProbabilisticCausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                     Set<Literal> evaluation, FormulaFactory f)
            throws InvalidCausalModelException {

        // all exogenous variables
        Map<Variable, Double> exogenousVariables = causalModel.getExogenousVariables();

        Set<Literal> evaluationEndogenousVars = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().keySet().contains(l.variable())).collect(Collectors.toSet());

        // create copy of original causal model
        ProbabilisticCausalModel causalModelForCause = createModifiedCausalModelForCause(causalModel, cause, f);
        ProbabilisticCausalModel causalModelForNegatedCause = createModifiedCausalModelForNegatedCause(causalModel, cause, f);

        // get the cause as set of variables
        Set<Variable> causeVariables = cause.stream().map(Literal::variable).collect(Collectors.toSet());

        /*
         * remove exogenous variables from evaluation as they are not needed for computing the Ws. Furthermore,
         * all variables in the cause also must not be in W. */
        Set<Literal> wVariables = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().keySet().contains(l.variable()) &&
                        !(causeVariables.contains(l.variable())))
                .collect(Collectors.toSet());

        // get all possible Ws, i.e. create power set of the evaluation
        List<Set<Literal>> allW = (new Util<Literal>()).generatePowerSet(wVariables);

        Set<Literal> exogenousAssignments = new HashSet<>();
        exogenousAssignments.addAll(exogenousVariables.keySet());
        for(Literal literal: exogenousVariables.keySet()){
            exogenousAssignments.add(literal.negate());
        }
        List<Set<Literal>> allSubsetsOfExoAssignments = (new Util<Literal>()).generatePowerSet(exogenousAssignments);

        // Iterate over all W and Z
        for (Set<Literal> w : allW) {
            Set<Literal> zVariables = evaluation.stream().filter(l -> !causalModel.getExogenousVariables().keySet().contains(l.variable())).collect(Collectors.toSet());
            zVariables.removeAll(w);

            // Set of all possible assignments to the Ws
            Set<Literal> wAssignments = new HashSet<>();
            wAssignments.addAll(w);
            for(Literal lit: w){
                wAssignments.add(lit.negate());
            }
            List<Set<Literal>> allSubsetsOfWAssignments = (new Util<Literal>()).generatePowerSet(wAssignments);
            for(Set<Literal> wAssignment : allSubsetsOfWAssignments){
                if(wAssignment.size() != w.size()  || !UtilityMethods.noDuplicates(wAssignment)){
                    continue;
                }
                ProbabilisticCausalModel causalModelModifiedW = createModifiedCausalModelForW(causalModelForCause, wAssignment, f);
                ProbabilisticCausalModel causalModelNegatedModifiedW = createModifiedCausalModelForW(causalModelForNegatedCause, wAssignment, f);

                double probCAndE = 0.0;
                double probC = 0.0;
                double probNotCAndE = 0.0;
                double probNotC = 0.0;
                boolean zFulfills = true;
                double probCause = 0.0;
                double notCause = 0.0;
                List<Double> probList = new ArrayList<>();

                zVariables.removeAll(cause);
                List<Set<Literal>> allSubsetsOfZPrime = (new Util<Literal>()).generatePowerSet(zVariables);
                for(Set<Literal> zStar : allSubsetsOfZPrime) {
                    ProbabilisticCausalModel causalModelModWModZStar = createModifiedCausalModelForW(causalModelModifiedW, zStar, f);
                    for(Set<Literal> exoAssignment : allSubsetsOfExoAssignments) {
                        if (exoAssignment.size() != exogenousVariables.size() || !UtilityMethods.noDuplicates(exoAssignment)) {
                            continue;
                        }
                        if (ProbabilisticCausalitySolver.evaluateEquations(causalModel, exoAssignment).containsAll(evaluationEndogenousVars)) {
                            // evaluate all variables
                            Set<Literal> evaluationModified = ProbabilisticCausalitySolver.evaluateEquations(causalModelModWModZStar, exoAssignment);
                            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluationModified, phi, cause);
                            double modelProbability = causalModelModWModZStar.getProbability(exoAssignment);

                            Set<Literal> negatedEvaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModelNegatedModifiedW, exoAssignment);
                            Pair<Boolean, Boolean> ac1TupleNegated = ProbabilisticCausalitySolver.fulfillsPC1(negatedEvaluation, phi, cause.stream().map(Literal::negate)
                                    .collect(Collectors.toSet()));
                            double negatedModelProbability = causalModelNegatedModifiedW.getProbability(exoAssignment);

                            // cause fulfilled
                            if (ac1Tuple.second()) {
                                probC += modelProbability;
                            }
                            if (ac1TupleNegated.second()) {
                                probNotC += negatedModelProbability;
                            }
                            if (ac1Tuple.first() && ac1Tuple.second()) {
                                probCAndE += modelProbability;
                            }
                            if (ac1TupleNegated.first() && ac1TupleNegated.second()) {
                                probNotCAndE += negatedModelProbability;
                            }
                        }
                    }

                    probCause = (probCAndE / probC);
                    notCause = (probNotCAndE / probNotC);
                    probList.add(probCause);
                    if (probCause <= notCause) {
                        zFulfills = false;
                        break;
                    }
                }
                if(zFulfills){
                    System.out.println("Prob 1. cond: " + probCause);
                    System.out.println("W assignment " + wAssignment);
                    System.out.println("Z: " + zVariables);
                    System.out.println("P(not(C)): " + notCause);
                    System.out.println("fulfill PC2 (a)");
                    System.out.println("---------");
                    return wAssignment;
                }
            }
        }

        return null;
    }

    /**
     * Checks if PC'3 is fulfilled
     *
     * @param causalModel     the underlying probabilistic causal model
     * @param phi             the phi
     * @param cause           the cause for which we check PC'2
     * @param context         the context
     * @param evaluation      the original evaluation of variables
     * @param f               a formula factory
     * @return true if PC'3 fulfilled, else false
     */
    private boolean fulfillsPCPrime3(ProbabilisticCausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                Set<Literal> evaluation, boolean phiOccurred, FormulaFactory f) throws InvalidCausalModelException {
        if (cause.size() > 1 && phiOccurred) {

            // get all subsets of cause
            Set<Set<Literal>> allSubsetsOfCause = new UnifiedSet<>(cause).powerSet().stream()
                    .map(s -> s.toImmutable().castToSet())
                    .filter(s -> s.size() > 0 && s.size() < cause.size()) // remove empty set and full cause
                    .collect(Collectors.toSet());
            /*
             * no sub-cause must fulfill AC1 and AC2
             * for AC1, we only need to check if the current cause subset, as we checked for phi before */
            for (Set<Literal> c : allSubsetsOfCause) {
                if (evaluation.containsAll(c) &&
                        fulfillsPCPrime2(causalModel, phi, c, context, evaluation, f) != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
