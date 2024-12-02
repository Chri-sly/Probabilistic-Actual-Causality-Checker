package de.tuda.aiml.deterministic;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolver;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.util.Util;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Original variant of the HP definition.
 */
public class OriginalHPSolver extends CausalitySolver {
    @Override
    public CausalitySolverResult solve(CausalModel causalModel, Set<Literal> context, Formula phi, Set<Literal> cause, SolvingStrategy solvingStrategy) throws InvalidCausalModelException {
        FormulaFactory f = causalModel.getFormulaFactory();
        Set<Literal> evaluation = CausalitySolver.evaluateEquations(causalModel, context);
        Pair<Boolean, Boolean> ac1Tuple = fulfillsAC1(evaluation, phi, cause);
        boolean ac1 = ac1Tuple.first() && ac1Tuple.second();
        Set<Literal> w = fulfillsAC2(causalModel, phi, cause, context, evaluation, f);
        boolean ac2 = w != null;
        boolean ac3 = fulfillsAC3(causalModel, phi, cause, context, evaluation, ac1Tuple.first(), f);
        CausalitySolverResult causalitySolverResult = new CausalitySolverResult(ac1, ac2, ac3, cause, w);
        return causalitySolverResult;
    }

    /**
     * Method to check if the second clause of the original HP variant is fulfilled.
     * @param causalModel of the current example
     * @param phi the effect
     * @param cause the cause to check if it fulfills the condition
     * @param context actual setting of the exogenous variables
     * @param evaluation of the variables in the actual setting
     * @param f the formula factory of the example
     * @return a set of literals. That is the set of endogenous variables vector(W) and their assignment vector(w) that
     * fulfills the second clause. Else null
     * @throws InvalidCausalModelException
     */
    private Set<Literal> fulfillsAC2(CausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                     Set<Literal> evaluation, FormulaFactory f)
            throws InvalidCausalModelException {

        // firstly, check the trivial case of empty W. Therefore, negate phi.
        Formula phiFormula = f.not(phi);

        // create modified causal model by replacing the cause x with x'.
        CausalModel causalModelModified = createModifiedCausalModelForCause(causalModel, cause, f);

        // evaluate causal model with setting x' for cause
        Set<Literal> evaluationModified = CausalitySolver.evaluateEquations(causalModelModified, context);

        // check if not(phi) evaluates to true for empty W -> if yes, no further investigation necessary. AC2 is fulfilled.
        if (phiFormula.evaluate(new Assignment(evaluationModified))) {
            return new HashSet<>();
        }

        // get the cause as set of variables
        Set<Variable> causeVariables = cause.stream().map(Literal::variable).collect(Collectors.toSet());
        /*
         * remove exogenous variables from evaluation as they are not needed for computing the Ws. Furthermore,
         * all variables in the cause also must not be in W. */
        Set<Literal> wVariables = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().contains(l.variable()) &&
                        !(causeVariables.contains(l.variable())))
                .collect(Collectors.toSet());

        // get all possible Ws, i.e. create power set of the evaluation
        List<Set<Literal>> allSubsetsOfW = (new Util<Literal>()).generatePowerSet(wVariables);

        // Remove all exogenous variables for Z
        Set<Literal> zVariables = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().contains(l.variable()))
                .collect(Collectors.toSet());

        List<Set<Literal>> allSubsetsOfZ = (new Util<Literal>()).generatePowerSet(zVariables);

        for (Iterator<Set<Literal>> i = allSubsetsOfZ.iterator(); i.hasNext();) {
            Set<Literal> element = i.next();
            if (!element.containsAll(cause)) {
                i.remove();
            }
        }

        // Iterate over all subsets of W and Z
        for (Set<Literal> w : allSubsetsOfW) {
            for(Set<Literal> z : allSubsetsOfZ){

                // Copy W to intersect with Z
                Set<Literal> intersection = new HashSet<>(w);
                intersection.retainAll(z);

                // W and Z have to be disjoint
                if(!intersection.isEmpty()){
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
                        if(wAssignment.size() != w.size()){
                            continue;
                        }
                        // create a modified causal of the model in which we previously set X = x', by intervening
                        // on the values of the current variables in W using wAssignment
                        CausalModel causalModelModifiedW = createModifiedCausalModelForW(causalModelModified, wAssignment, f);

                        // evaluate all values of variables in this causal model
                        evaluationModified = CausalitySolver.evaluateEquations(causalModelModifiedW, context);

                        // Check AC2 (a): Not Phi should hold in model that has X = x' and W = w
                        if (phiFormula.evaluate(new Assignment(evaluationModified))) {

                            // Create Z' as Z - X
                            Set<Literal> zPrimeVariables = new HashSet<>();
                            zPrimeVariables.addAll(z);
                            zPrimeVariables.removeAll(cause);
                            List<Set<Literal>> allSubsetsOfZPrime = (new Util<Literal>()).generatePowerSet(zPrimeVariables);

                            // create causal model with the W = w that fulfilled AC2 (a) in original model with X = x
                            CausalModel causalModelModW = createModifiedCausalModelForW(causalModel, wAssignment, f);

                            for(Set<Literal> zStar : allSubsetsOfZPrime){

                                // create causal model with X = x, W = w and Z' = z*
                                CausalModel causalModelModWModZStar = createModifiedCausalModelForW(causalModelModW, zStar, f);
                                evaluationModified = CausalitySolver.evaluateEquations(causalModelModWModZStar, context);

                                // Check AC2 (b): Phi fulfilled in model that has X = x, W = w, Z' = z*
                                if(phi.evaluate(new Assignment(evaluationModified))){
                                    return wAssignment;
                                }
                                else{
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Method to check if the third clause of the original HP definition is fulfilled.
     * @return true if AC3 fulfilled, else false
     * @throws InvalidCausalModelException
     */
    private boolean fulfillsAC3(CausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                Set<Literal> evaluation, boolean phiOccurred, FormulaFactory f) throws InvalidCausalModelException {

        // Only if the cause is not a singleton, we have to check if a strict subset X' of X exists.
        if (cause.size() > 1 && phiOccurred) {

            // get all subsets of the cause, and remove the empty set and the identical set X
            Set<Set<Literal>> allSubsetsOfCause = new UnifiedSet<>(cause).powerSet().stream()
                    .map(s -> s.toImmutable().castToSet())
                    .filter(s -> s.size() > 0 && s.size() < cause.size())
                    .collect(Collectors.toSet());
            /*
             * No subset X' of the cause must fulfill AC1 and AC2.
             * For AC1, we only need to check if the current subset X' is fulfilled in the actual setting,
             * as we checked for phi before */
            for (Set<Literal> c : allSubsetsOfCause) {
                if (evaluation.containsAll(c) &&
                        fulfillsAC2(causalModel, phi, c, context, evaluation, f) != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
