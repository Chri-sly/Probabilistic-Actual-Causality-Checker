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
 * Implementation of the PC definition
 */
class PCSolver extends ProbabilisticCausalitySolver {
    /**
     * Overrides {@link ProbabilisticCausalitySolver#solve(ProbabilisticCausalModel, Set, Formula, Set, ProbabilisticSolvingStrategy)}.
     *
     * @param causalModel     the underlying probabilistic causal model
     * @param context         the context
     * @param phi             the phi
     * @param cause           the cause
     * @param solvingStrategy the applied solving strategy
     * @return for each PC, true if fulfilled, false else
     * @throws InvalidCausalModelException thrown if internally generated causal models are invalid
     */
     public ProbabilisticCausalitySolverResult solve(ProbabilisticCausalModel causalModel, Set<Literal> context, Formula phi,
                                Set<Literal> cause, ProbabilisticSolvingStrategy solvingStrategy)
            throws InvalidCausalModelException {
        FormulaFactory f = causalModel.getFormulaFactory();
        Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModel, context);
        Pair<Boolean, Boolean> pc1Tuple = fulfillsPC1(evaluation, phi, cause);
        boolean pc1 = pc1Tuple.first() && pc1Tuple.second();
        Set<Literal> w = fulfillsPC2(causalModel, phi, cause, context, evaluation, f);
        boolean pc2 = w != null;
        boolean pc3 = fulfillsPC3(causalModel, phi, cause, context, evaluation, pc1Tuple.first(), f);
        ProbabilisticCausalitySolverResult causalitySolverResult = new ProbabilisticCausalitySolverResult(pc1, pc2, pc3, cause, w);
        return causalitySolverResult;
    }

    /**
     * Checks if PC2 is fulfilled.
     *
     * @param causalModel     the underlying causal model
     * @param phi             the phi
     * @param cause           the cause for which we check PC2
     * @param context         the context
     * @param evaluation      the original evaluation of variables
     * @param f               a formula factory
     * @return returns W if PC2 fulfilled, else null
     * @throws InvalidCausalModelException thrown if internally generated causal models are invalid
     */
    private Set<Literal> fulfillsPC2(ProbabilisticCausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                     Set<Literal> evaluation, FormulaFactory f)
            throws InvalidCausalModelException {
        // all exogenous variables
        Map<Variable, Double> exogenousVariables = causalModel.getExogenousVariables();
        System.out.println(context);
        // negate phi
        Formula negatedPhi = f.not(phi);

        // create copy of original causal model
        ProbabilisticCausalModel causalModelForCause = createModifiedCausalModelForCause(causalModel, cause, f);
        ProbabilisticCausalModel causalModelForNegatedCause = createModifiedCausalModelForNegatedCause(causalModel, cause, f);

        // evaluate causal model with setting x' for cause
        Set<Literal> evaluationModified = ProbabilisticCausalitySolver.evaluateEquations(causalModelForCause, context);

        // get the cause as set of variables
        Set<Variable> causeVariables = cause.stream().map(Literal::variable).collect(Collectors.toSet());

        // get the negated cause
        Set<Literal> negatedCause = cause.stream().map(Literal::negate).collect(Collectors.toSet());
        /*
         * remove exogenous variables from evaluation as they are not needed for computing the Ws. Furthermore,
         * all variables in the cause also must not be in W. */
        Set<Literal> wVariables = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().keySet().contains(l.variable()) &&
                        !(causeVariables.contains(l.variable())))
                .collect(Collectors.toSet());

        // get all possible Ws, i.e. create power set of the evaluation
        List<Set<Literal>> allW = (new Util<Literal>()).generatePowerSet(wVariables);

        // Remove all exogenous variables for Z
        Set<Literal> zVariables = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().keySet().contains(l.variable()))
                .collect(Collectors.toSet());

        List<Set<Literal>> allSubsetsOfZ = (new Util<Literal>()).generatePowerSet(zVariables);

        for (Iterator<Set<Literal>> i = allSubsetsOfZ.iterator(); i.hasNext();) {
            Set<Literal> element = i.next();
            if (!element.containsAll(cause)) {
                i.remove();
            }
        }

        Set<Literal> exogenousAssignments = new HashSet<>();
        exogenousAssignments.addAll(exogenousVariables.keySet());
        for(Literal literal: exogenousVariables.keySet()){
            exogenousAssignments.add(literal.negate());
        }
        List<Set<Literal>> allSubsetsOfExoAssignments = (new Util<Literal>()).generatePowerSet(exogenousAssignments);

        // Iterate over all W and Z
        for (Set<Literal> w : allW) {
            for(Set<Literal> z : allSubsetsOfZ){
                Set<Literal> intersection = new HashSet<Literal>(w);
                intersection.retainAll(z);
                // make sure W and Z are disjoint
                if(!intersection.isEmpty() || z.size() + w.size() < causalModel.getEquationsSorted().size()){
                    continue;
                }
                else{
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
                        for(Set<Literal> exoAssignment : allSubsetsOfExoAssignments) {
                            if (exoAssignment.size() != exogenousVariables.size() || !UtilityMethods.noDuplicates(exoAssignment)) {
                                continue;
                            }
                            // evaluate all variables
                            evaluationModified = ProbabilisticCausalitySolver.evaluateEquations(causalModelModifiedW, exoAssignment);
                            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluationModified, phi, cause);
                            double modelProbability = causalModelModifiedW.getProbability(exoAssignment);

                            Set<Literal> negatedEvaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModelNegatedModifiedW, exoAssignment);
                            Pair<Boolean, Boolean> ac1TupleNegated = ProbabilisticCausalitySolver.fulfillsPC1(negatedEvaluation, phi, cause.stream().map(Literal::negate)
                                    .collect(Collectors.toSet()));
                            double negatedModelProbability = causalModelNegatedModifiedW.getProbability(exoAssignment);

                            // cause fulfilled
                            if(ac1Tuple.second()) {
                                probC += modelProbability;
                            }
                            if(ac1TupleNegated.second()){
                                probNotC += negatedModelProbability;
                            }
                            if(ac1Tuple.first() && ac1Tuple.second()){
                                probCAndE += modelProbability;
                            }
                            if(ac1TupleNegated.first() && ac1TupleNegated.second()){
                                probNotCAndE += negatedModelProbability;
                            }

                        }
                        double probCause = (probCAndE / probC);
                        double notCause = (probNotCAndE / probNotC);

                        // If PC2(a) is fulfilled
                        if(probCause > notCause){
                            Set<Literal> zPrimeVariables = new HashSet<>();
                            zPrimeVariables.addAll(z);
                            zPrimeVariables.removeAll(cause);
                            List<Set<Literal>> allSubsetsOfZPrime = (new Util<Literal>()).generatePowerSet(zPrimeVariables);
                            double probCAndE2 = 0.0;
                            double probC2 = 0.0;
                            double probCause2;
                            boolean zFulfills = true;
                            List<Double> probList = new ArrayList<>();

                            for(Set<Literal> zStar : allSubsetsOfZPrime) {
                                ProbabilisticCausalModel causalModelModWModZStar = createModifiedCausalModelForW(causalModelModifiedW, zStar, f);
                                for (Set<Literal> exoAssignment : allSubsetsOfExoAssignments) {
                                    if (exoAssignment.size() != exogenousVariables.size() || !exoAssignment.containsAll(context) || !UtilityMethods.noDuplicates(exoAssignment)) {
                                        evaluationModified = ProbabilisticCausalitySolver.evaluateEquations(causalModelModWModZStar, exoAssignment);
                                        Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluationModified, phi, cause);
                                        double modelProbability = causalModelModWModZStar.getProbability(exoAssignment);

                                        if (ac1Tuple.second()) {
                                            probC2 += modelProbability;
                                        }
                                        if (ac1Tuple.first() && ac1Tuple.second()) {
                                            probCAndE2 += modelProbability;
                                        }
                                    }
                                }

                                probCause2 = (probCAndE2 / probC2);
                                probList.add(probCause2);
                                // Check PC2 (b)
                                if (probCause2 <= notCause) {
                                    zFulfills = false;
                                    break;
                                }
                            }
                            if(zFulfills){
                                System.out.println("Prob 1. cond: " + probCause);
                                System.out.println(probCAndE2 + "  " + probC2);
                                System.out.println("W assignment " + wAssignment);
                                System.out.println("Z: " + z);
                                for(double prob: probList){
                                    System.out.println("Prob: " + prob);
                                }
                                System.out.println("P(not(C)): " + notCause);
                                System.out.println("fulfill PC2 (a)");
                                System.out.println("---------");
                                return wAssignment;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
    /**
     * Checks if PC3 is fulfilled
     *
     * @param causalModel     the underlying causal model
     * @param phi             the phi
     * @param cause           the cause for which we check PC2
     * @param context         the context
     * @param evaluation      the original evaluation of variables
     * @param f               a formula factory
     * @return true if PC3 fulfilled, else false
     */
    private boolean fulfillsPC3(ProbabilisticCausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
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
                        fulfillsPC2(causalModel, phi, c, context, evaluation, f) != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
