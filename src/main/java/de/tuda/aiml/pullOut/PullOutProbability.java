package de.tuda.aiml.pullOut;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.util.UtilityMethods;
import de.tum.in.i4.hp2sat.causality.*;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;

import de.tum.in.i4.hp2sat.util.Util;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the "Pull Out the Probability" approach
 */
public class PullOutProbability {

    public static double solve(ProbabilisticCausalModel model, Set<Literal> context, Formula phi, Set<Literal> cause, SolvingStrategy solvingStrategy) throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        double probCause = 0.0;
        double probModels = 0.0;

        // Create all contexts
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();
        Set<Literal> exogenousAssignments = new HashSet<>();
        exogenousAssignments.addAll(exogenousVariables.keySet());

        for(Literal literal: exogenousVariables.keySet()){
            exogenousAssignments.add(literal.negate());
        }
        Set<Set<Literal>> allSubsetsOfExoAssignments = (new Util<Literal>()).generatePowerSet(exogenousAssignments).stream()
                .filter(l -> UtilityMethods.noDuplicates(l) && l.containsAll(context) && l.size() == model.getExogenousVariables().size())
                .collect(Collectors.toSet());

        for(Set<Literal> c : allSubsetsOfExoAssignments){
            CausalModel causalModel = new CausalModel(model.getName(), model.getEquationsSorted().stream().collect(Collectors.toSet()), model.getExogenousVariables().keySet(), model.getFormulaFactory());
            CausalitySolverResult causalitySolverResultActual = causalModel.isCause(c, phi, cause, solvingStrategy);
            Set<Literal> evaluation = CausalitySolver.evaluateEquations(causalModel, c);

            // Effect does not happen in this model
            if(!phi.evaluate(new Assignment(evaluation))){
                continue;
            }

            // compute probability that the uncertain factors take on those values
            Set<Literal> uncertainContext = new HashSet<>();
            uncertainContext.addAll(c);
            uncertainContext.removeAll(context);

            double modelProbability = model.getProbability(uncertainContext);

            // Whether X = x is a cause in this model
            if (causalitySolverResultActual.isAc1() && causalitySolverResultActual.isAc2() && causalitySolverResultActual.isAc3()){
                probCause += modelProbability;
            }
            probModels += modelProbability;
        }

        // result as portion of cases in which X = x actually causes phi
        return probCause/probModels;
    }
}
