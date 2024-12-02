package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolver;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolverResult;
import de.tuda.aiml.probabilistic.ProbabilisticSolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.util.Pair;

import java.util.*;

public class ProbabilityRaising {

    ProbabilisticCausalModel model;
    int numberOfCases;
    Formula phi;
    Set<Literal> cause;

    public ProbabilityRaising(ProbabilisticCausalModel model, int numberOfCases, Formula phi, Set<Literal> cause){
        this.model = model;
        this.numberOfCases = numberOfCases;
        this.phi = phi;
        this.cause = cause;
    }

    public static boolean compute(ProbabilisticCausalModel model, int numberOfCases, Formula phi, Set<Literal> cause) throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();
        FormulaFactory f = model.getFormulaFactory();
        List<Set<Literal>> contexts = new ArrayList<>();
        int countPhiOccurs = 0;
        int countCauseOccurs = 0;
        int countBothOccur = 0;
        int countPhiNotCause = 0;

        for(int i = 0; i < numberOfCases; i++){
            Set<Literal> context = new HashSet<>();
            for(Variable var : exogenousVariables.keySet()){
                boolean b = (Math.random() < exogenousVariables.get(var)) ? context.add(f.literal(var.name(), true)) : context.add(f.literal(var.name(), false));
            }
            contexts.add(context);
        }

        for(Set<Literal> context : contexts){
            ProbabilisticCausalitySolverResult causalitySolverResultActual = model.isCause(context, phi, cause, ProbabilisticSolvingStrategy.BRUTE_FORCE);
            Set<Literal> evaluation = ProbabilisticCausalitySolver.evaluateEquations(model, context);
            Pair<Boolean, Boolean> ac1Tuple = ProbabilisticCausalitySolver.fulfillsAC1(evaluation, phi, cause);

            if(ac1Tuple.first()) {
                countPhiOccurs += 1;
            }
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

        double givenCause = (double) countBothOccur / (double) countCauseOccurs;
        double independent = (double) countPhiOccurs/ (double) numberOfCases;
        double givenNotCause = (double) countPhiNotCause / (double) (numberOfCases - countCauseOccurs);

        System.out.println("Both: " + countBothOccur);
        System.out.println("Cause: " + countCauseOccurs);
        System.out.println("Phi: " + countPhiOccurs);
        System.out.println("P(E|C): " + givenCause);
        System.out.println("P(E): " + independent);
        System.out.println("P(E|not C): " + givenNotCause);

        return false;
    }
}
