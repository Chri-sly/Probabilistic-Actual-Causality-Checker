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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OriginalHPSolver extends CausalitySolver {
    @Override
    public CausalitySolverResult solve(CausalModel causalModel, Set<Literal> context, Formula phi, Set<Literal> cause, SolvingStrategy solvingStrategy) throws InvalidCausalModelException {
        FormulaFactory f = causalModel.getFormulaFactory();
        Set<Literal> evaluation = CausalitySolver.evaluateEquations(causalModel, context);
        Pair<Boolean, Boolean> ac1Tuple = fulfillsAC1(evaluation, phi, cause);
        boolean ac1 = ac1Tuple.first() && ac1Tuple.second();
        Set<Literal> w = fulfillsAC2(causalModel, phi, cause, context, evaluation, solvingStrategy, f);
        boolean ac2 = w != null;
        boolean ac3 = fulfillsAC3(causalModel, phi, cause, context, evaluation, ac1Tuple.first(), solvingStrategy, f);
        CausalitySolverResult causalitySolverResult = new CausalitySolverResult(ac1, ac2, ac3, cause, w);
        return causalitySolverResult;
    }

    private Set<Literal> fulfillsAC2(CausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                     Set<Literal> evaluation, SolvingStrategy solvingStrategy, FormulaFactory f)
            throws InvalidCausalModelException {
        System.out.println("Checking original AC2");

        // negate phi
        Formula phiFormula = f.not(phi);

        // create copy of original causal model
        CausalModel causalModelModified = createModifiedCausalModelForCause(causalModel, cause, f);

        // evaluate causal model with setting x' for cause
        Set<Literal> evaluationModified = CausalitySolver.evaluateEquations(causalModelModified, context);
        // check if not(phi) evaluates to true for empty W -> if yes, no further investigation necessary
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
        // get all possible Ws, i.e create power set of the evaluation
        List<Set<Literal>> allW = (new Util<Literal>()).generatePowerSet(wVariables);

        // Remove all exogenous variables for Z
        Set<Literal> zVariables = evaluation.stream()
                .filter(l -> !causalModel.getExogenousVariables().contains(l.variable()))
                .collect(Collectors.toSet());

        List<Set<Literal>> allZ = (new Util<Literal>()).generatePowerSet(zVariables);
        List<Set<Literal>> allZs = new ArrayList<Set<Literal>>();

        // Only keep the powersets if they are a subset of the cause
        for(Set<Literal> z : allZ){
            if(z.containsAll(cause)){
                allZs.add(z);
            }
        }

        // Iterate over all W and Z
        for (Set<Literal> w : allW) {
            for(Set<Literal> z : allZs){
                Set<Literal> intersection = new HashSet<Literal>(w);
                intersection.retainAll(z);
                // make sure W and Z are disjoint
                if(!intersection.isEmpty()){
                    continue;
                }
                else{
                    int wSize = w.size();
                    // Set of all possible assignments to the Ws
                    Set<Literal> wAssign = new HashSet<>();
                    wAssign.addAll(w);
                    for(Literal lit: w){
                        wAssign.add(lit.negate());
                    }
                    List<Set<Literal>> allWAssing = (new Util<Literal>()).generatePowerSet(wAssign);
                    for(Set<Literal> wAssig : allWAssing){
                        if(wAssig.size() != wSize){
                            continue;
                        }
                        // create copy of modified causal model
                        CausalModel causalModelModifiedW = createModifiedCausalModelForW(causalModelModified, wAssig, f);
                        // evaluate all variables
                        evaluationModified = CausalitySolver.evaluateEquations(causalModelModifiedW, context);

                        // Check AC2 a)
                        if (phiFormula.evaluate(new Assignment(evaluationModified))) {
                            Set<Literal> zWithoutX = new HashSet<>();
                            zWithoutX.addAll(z);
                            zWithoutX.removeAll(cause);
                            List<Set<Literal>> ZStar = (new Util<Literal>()).generatePowerSet(zWithoutX);
                            // check for same W and cause if phi holds
                            CausalModel causalModelModW = createModifiedCausalModelForW(causalModel, wAssig, f);
                            boolean checkZSubsets = true;

                            for(Set<Literal> zStar : ZStar){
                                CausalModel causalModelModWModZStar = createModifiedCausalModelForW(causalModelModW, zStar, f);
                                evaluationModified = CausalitySolver.evaluateEquations(causalModelModWModZStar, context);

                                // Check AC2 b)
                                if(phi.evaluate(new Assignment(evaluationModified))){
                                    System.out.println("AC2 b) fulfilled");
                                    System.out.println(z);
                                    return w;
                                }
                                else{
                                    checkZSubsets = false;
                                    break;
                                }
                            }
                            if(checkZSubsets){
                                return w;
                            }
                            continue;
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean fulfillsAC3(CausalModel causalModel, Formula phi, Set<Literal> cause, Set<Literal> context,
                                Set<Literal> evaluation, boolean phiOccurred, SolvingStrategy solvingStrategy,
                                FormulaFactory f)
            throws InvalidCausalModelException {
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
                        fulfillsAC2(causalModel, phi, c, context, evaluation, solvingStrategy, f) != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
