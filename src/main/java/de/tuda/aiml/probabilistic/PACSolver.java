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
 * Implementation of the PAC definition
 */
public class PACSolver extends ProbabilisticCausalitySolver{
    /**
     * Overrides {@link ProbabilisticCausalitySolver#solve(ProbabilisticCausalModel, Set, Formula, Set, ProbabilisticSolvingStrategy)}.
     *
     * @param causalModel     the underlying probabilistic causal model
     * @param context         the context
     * @param phi             the phi
     * @param cause           the cause
     * @param solvingStrategy the applied solving strategy
     * @return for each PAC, true if fulfilled, false else
     * @throws InvalidCausalModelException thrown if internally generated causal models are invalid
     */
    public ProbabilisticCausalitySolverResult solve(ProbabilisticCausalModel causalModel, Set<Literal> context, Formula phi,
                                                    Set<Literal> cause, ProbabilisticSolvingStrategy solvingStrategy)
            throws InvalidCausalModelException {
        FormulaFactory f = causalModel.getFormulaFactory();
        Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModel, context);
        Pair<Boolean, Boolean> pc1Tuple = fulfillsPC1(evaluation, phi, cause);
        boolean pac1 = pc1Tuple.first() && pc1Tuple.second();
        Set<Literal> w = fulfillsPAC2(causalModel, phi, cause, context, evaluation, f);
        boolean pac2 = w != null;
        boolean pac3 = fulfillsPAC3(causalModel, phi, cause, context, evaluation, pc1Tuple.first(), f);
        ProbabilisticCausalitySolverResult causalitySolverResult = new ProbabilisticCausalitySolverResult(pac1, pac2, pac3, cause, w);
        return causalitySolverResult;
    }

    /**
     * Checks if PAC2 is fulfilled.
     *
     * @param causalModel     the underlying causal model
     * @param phi             the phi
     * @param cause           the cause for which we check PAC2
     * @param context         the context
     * @param evaluation      the original evaluation of variables
     * @param f               a formula factory
     * @return the W that fulfills PAC2, else null
     * @throws InvalidCausalModelException thrown if internally generated causal models are invalid
     */
    private Set<Literal> fulfillsPAC2(ProbabilisticCausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                     Set<Literal> evaluation, FormulaFactory f)
            throws InvalidCausalModelException {

        // all exogenous variables
        Map<Variable, Double> exogenousVariables = causalModel.getExogenousVariables();

        Set<Literal> evaluationEndogenousVars = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().keySet().contains(l.variable())).collect(Collectors.toSet());

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

        // Iterate over all W
        for (Set<Literal> w : allW) {
            ProbabilisticCausalModel causalModelModifiedW = createModifiedCausalModelForW(causalModelForNegatedCause, w, f);

            double probCAndE = 0.0;
            double probC = 0.0;
            for (Set<Literal> exoAssignment : allSubsetsOfExoAssignments) {
                if (exoAssignment.size() != exogenousVariables.size() || !UtilityMethods.noDuplicates(exoAssignment)) {
                    continue;
                }
                if(ProbabilisticCausalitySolver.evaluateEquations(causalModel, exoAssignment).containsAll(evaluationEndogenousVars)){
                    Set<Literal> negatedEvaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModelModifiedW, exoAssignment);
                    Pair<Boolean, Boolean> ac1TupleNegated = ProbabilisticCausalitySolver.fulfillsPC1(negatedEvaluation, phi, cause.stream().map(Literal::negate)
                            .collect(Collectors.toSet()));
                    double negatedModelProbability = causalModelModifiedW.getProbability(exoAssignment);

                    if(ac1TupleNegated.second()) {
                        probC += negatedModelProbability;
                    }
                    if(ac1TupleNegated.first() && ac1TupleNegated.second()){
                        probCAndE += negatedModelProbability;
                    }
                }
            }
            double probCause = (probCAndE / probC);
            if(probCause < 1){
                return w;
            }
        }

        return null;
    }

    /**
     * Checks if PAC3 is fulfilled
     *
     * @param causalModel     the underlying probabilistic causal model
     * @param phi             the phi
     * @param cause           the cause for which we check PAC2
     * @param context         the context
     * @param evaluation      the original evaluation of variables
     * @param f               a formula factory
     * @return true if PAC3 fulfilled, else false
     */
    private boolean fulfillsPAC3(ProbabilisticCausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                Set<Literal> evaluation, boolean phiOccurred, FormulaFactory f) throws InvalidCausalModelException {
        if (cause.size() > 1 && phiOccurred) {

            // get all subsets of cause
            Set<Set<Literal>> allSubsetsOfCause = new UnifiedSet<>(cause).powerSet().stream()
                    .map(s -> s.toImmutable().castToSet())
                    .filter(s -> s.size() > 0 && s.size() < cause.size()) // remove empty set and full cause
                    .collect(Collectors.toSet());
            /*
             * no sub-cause must fulfill PAC1 and PAC2
             * for PAC1, we only need to check if the current cause subset, as we checked for phi before */
            for (Set<Literal> c : allSubsetsOfCause) {
                if (evaluation.containsAll(c) &&
                        fulfillsPAC2(causalModel, phi, c, context, evaluation, f) != null) {
                    return false;
                }
            }
        }
        return true;
    }
}

