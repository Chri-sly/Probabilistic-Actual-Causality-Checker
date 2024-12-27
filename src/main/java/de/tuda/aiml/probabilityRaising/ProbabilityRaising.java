package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolver;
import de.tuda.aiml.util.UtilityMethods;
import de.tum.in.i4.hp2sat.util.Util;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.util.Pair;

import java.util.*;

/**
 * Implementation of the simple Probability Raising approach in probabilistic causal models.
 */
public class ProbabilityRaising {
    ProbabilisticCausalModel model;
    int numberOfCases;
    Formula phi;
    Set<Literal> cause;
    Set<Literal> context;

    public ProbabilityRaising(ProbabilisticCausalModel model, int numberOfCases, Formula phi, Set<Literal> cause, Set<Literal> context){
        this.model = model;
        this.numberOfCases = numberOfCases;
        this.phi = phi;
        this.cause = cause;
        this.context = context;
    }

    /**
     * Method to check probability raising in a Monte-Carlo approach by generating a number of contexts with the given
     * probability of the uncertain exogenous variables and then counting in which cases the cause and phi or not the cause
     * and phi occur.
     * @param model causal model that represents the example
     * @param numberOfCases to generate and count the occurrences of cause and effect in
     * @param phi the effect
     * @param cause the possible cause of phi
     * @return
     */
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

    /**
     * Method to check probability raising by computing the probability of each context that may occur and
     * generating a number of contexts with the given
     * probability of the uncertain exogenous variables and then counting in which cases the cause and phi or not the cause
     * and phi occur.
     * @param model causal model that represents the example
     * @param phi the effect
     * @param cause the possible cause of phi
     * @param context the actual context of the given scenario
     * @return
     */
    public static ProbabilityRaisingResult computeActual(ProbabilisticCausalModel model, Formula phi, Set<Literal> cause, Set<Literal> context) {
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
            if (exoAssignment.size() != exogenousVariables.size() || !exoAssignment.containsAll(context) || !UtilityMethods.noDuplicates(exoAssignment)) {
                continue;
            }
            
            Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(model, exoAssignment);
            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsPC1(evaluation, phi, cause);
            double modelProbability = model.getProbability(exoAssignment);

            // cause fulfilled
            if(ac1Tuple.second()) {
                probC += modelProbability;
            }
            if(!ac1Tuple.second()){
                probNotC += modelProbability;
            }
            if(ac1Tuple.first() && ac1Tuple.second()){
                probCAndE += modelProbability;
            }
            if(ac1Tuple.first() && !ac1Tuple.second()){
                probNotCAndE += modelProbability;
            }
        }

        return new ProbabilityRaisingResult((probCAndE / probC) > (probNotCAndE / probNotC), (probCAndE / probC), (probNotCAndE / probNotC));
    }
}
