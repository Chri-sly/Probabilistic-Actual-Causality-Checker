package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolver;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolverResult;
import de.tuda.aiml.probabilistic.ProbabilisticSolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import de.tum.in.i4.hp2sat.util.Util;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Interventional Probability Raising Approach in Probabilistic Causal models.
 */
public class InterventionalProbabilityRaising extends ProbabilisticCausalitySolver{

    @Override
    protected ProbabilisticCausalitySolverResult solve(ProbabilisticCausalModel causalModel, Set<Literal> context, Formula phi, Set<Literal> cause, ProbabilisticSolvingStrategy solvingStrategy) throws InvalidCausalModelException {
        return null;
    }

    public static ProbabilityRaisingResult computeActual(ProbabilisticCausalModel model, Formula phi, Set<Literal> cause, Set<Literal> context) throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        FormulaFactory f = model.getFormulaFactory();
        double probCAndE = 0.0;
        double probC = 0.0;
        double probNotCAndE = 0.0;
        double probNotC = 0.0;
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();

        Set<Literal> exogenousAssignments = new HashSet<>();
        exogenousAssignments.addAll(exogenousVariables.keySet());
        for(Literal literal: exogenousVariables.keySet()){
            exogenousAssignments.add(literal.negate());
        }
        List<Set<Literal>> allSubsetsOfExoAssignments = (new Util<Literal>()).generatePowerSet(exogenousAssignments);
        for(Set<Literal> exoAssignment : allSubsetsOfExoAssignments) {
            if (exoAssignment.size() != exogenousVariables.size() || !exoAssignment.containsAll(context) || !noDuplicates(exoAssignment)) {
                continue;
            }
            System.out.println(exoAssignment);
            double modelProbability = model.getProbability(exoAssignment);
            System.out.println(modelProbability);
            Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(model, exoAssignment);
            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluation, phi, cause);

            ProbabilisticCausalModel causalModelModified = createModifiedCausalModelForNegatedCause(model, cause, f);
            System.out.println(causalModelModified.getEquationsSorted());
            Set<Literal> negatedEvaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModelModified, exoAssignment);
            System.out.println(negatedEvaluation);
            Pair<Boolean, Boolean> ac1TupleNegated = ProbabilisticCausalitySolver.fulfillsPC1(negatedEvaluation, phi, cause.stream().map(Literal::negate)
                    .collect(Collectors.toSet()));
            double negatedModelProbability = causalModelModified.getProbability(exoAssignment);

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

        System.out.println(probCAndE);
        System.out.println(probNotCAndE);
        System.out.println("P(E|C): " + (probCAndE / probC));
        System.out.println("P(E|not C): " + (probNotCAndE / probNotC));

        return new ProbabilityRaisingResult((probCAndE / probC) > (probNotCAndE / probNotC), (probCAndE / probC), (probNotCAndE / probNotC));
    }

    private static boolean noDuplicates(Set<Literal> context){
        boolean flag = true;
        for(Literal literal : context){
            if(context.contains(literal.negate())){
                flag = false;
                break;
            }
        }
        return flag;
    }
}
