package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolver;
import de.tuda.aiml.util.UtilityMethods;

import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
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
public class InterventionalProbabilityRaising{

    public static ProbabilityRaisingResult compute(ProbabilisticCausalModel model, int numberOfCases, Formula phi, Set<Literal> cause) {
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();
        FormulaFactory f = model.getFormulaFactory();
        List<Set<Literal>> contexts = new ArrayList<>();
        int countCauseOccurs = 0;
        int countBothOccur = 0;
        int countPhiNotCause = 0;

        // Generate contexts according to the probabilities of the exogenous variables
        for(int i = 0; i < numberOfCases; i++){
            Set<Literal> context = new HashSet<>();
            for(Variable var : exogenousVariables.keySet()){
                boolean b = Math.random() < exogenousVariables.get(var) ? context.add(f.literal(var.name(), true)) : context.add(f.literal(var.name(), false));
            }
            contexts.add(context);
        }

        for(Set<Literal> context : contexts){
            Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(model, context);
            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluation, phi, cause);

            if(ac1Tuple.second()) {
                countCauseOccurs += 1;
            }
            if(ac1Tuple.first() && ac1Tuple.second()){
                countBothOccur += 1;
            }
            if(ac1Tuple.first() && !ac1Tuple.second()){
                countPhiNotCause += 1;
            }
        }

        double givenCause = (double) countBothOccur / countCauseOccurs;
        double givenNotCause = (double) countPhiNotCause / (numberOfCases - countCauseOccurs);

        return new ProbabilityRaisingResult(givenCause > givenNotCause, givenCause, givenNotCause);
    }

    public static ProbabilityRaisingResult computeActual(ProbabilisticCausalModel model, Formula phi, Set<Literal> cause, Set<Literal> context) throws InvalidCausalModelException {
        FormulaFactory f = model.getFormulaFactory();
        ProbabilisticCausalModel causalModelForCause = ProbabilisticCausalitySolver.createModifiedCausalModelForCause(model, cause, f);
        ProbabilisticCausalModel causalModelModified = ProbabilisticCausalitySolver.createModifiedCausalModelForNegatedCause(model, cause, f);
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();
        double probCAndE = 0.0;
        double probC = 0.0;
        double probNotCAndE = 0.0;
        double probNotC = 0.0;

        Set<Literal> exogenousAssignments = new HashSet<>();
        exogenousAssignments.addAll(exogenousVariables.keySet());
        for(Literal literal: exogenousVariables.keySet()){
            exogenousAssignments.add(literal.negate());
        }
        List<Set<Literal>> allSubsetsOfExoAssignments = (new Util<Literal>()).generatePowerSet(exogenousAssignments);
        for(Set<Literal> exoAssignment : allSubsetsOfExoAssignments) {
            if (exoAssignment.size() != exogenousVariables.size() || !exoAssignment.containsAll(context) || !UtilityMethods.noDuplicates(exoAssignment)) {
                continue;
            }
            // evaluate phi for the cause
            Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModelForCause, exoAssignment);
            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluation, phi, cause);
            double modelProbability = causalModelForCause.getProbability(exoAssignment);

            // evaluate phi for the negation of the cause
            Set<Literal> negatedEvaluation = ProbabilisticCausalitySolver.evaluateEquations(causalModelModified, exoAssignment);
            Pair<Boolean, Boolean> ac1TupleNegated = ProbabilisticCausalitySolver.fulfillsPC1(negatedEvaluation, phi, cause.stream().map(Literal::negate)
                    .collect(Collectors.toSet()));
            double negatedModelProbability = causalModelModified.getProbability(exoAssignment);

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

        return new ProbabilityRaisingResult((probCAndE / probC) > (probNotCAndE / probNotC), (probCAndE / probC), (probNotCAndE / probNotC));
    }
}
