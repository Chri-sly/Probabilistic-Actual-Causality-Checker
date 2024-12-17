package de.tuda.aiml.pullOut;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;

import org.logicng.formulas.Formula;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the "Pull Out the Probability" approach
 */
public class PullOutProbability {
    private CausalModel model;
    private List<Set<Literal>> contexts;
    private Formula phi;
    private Set<Literal> cause;
    private SolvingStrategy solvingStrategy;
    private Map<Variable, Double> map;
    private Set<Literal> know;

    public PullOutProbability(CausalModel model, List<Set<Literal>> contexts, Formula phi, Set<Literal> cause, SolvingStrategy solvingStrategy, Map<Variable, Double> map, Set<Literal> know){
        this.model = model;
        this.contexts = contexts;
        this.phi = phi;
        this.cause = cause;
        this.solvingStrategy = solvingStrategy;
        this.map = map;
        this.know = know;
    }

    public PullOutProbability(CausalModel model, List<Set<Literal>> contexts, Formula phi, Set<Literal> cause, SolvingStrategy solvingStrategy){
        this.model = model;
        this.contexts = contexts;
        this.phi = phi;
        this.cause = cause;
        this.solvingStrategy = solvingStrategy;
    }

    public double solve() throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        double probCause = 0.0;
        double probModels = 0.0;

        for(Set<Literal> context : contexts){
            CausalitySolverResult causalitySolverResultActual = model.isCause(context, phi, cause, this.solvingStrategy);

            // if phi is not fulfilled then do not check this model or context contains an assignment that is invalid
            if (!causalitySolverResultActual.isAc1() || checkKnownAssignment(know, context)) {
                continue;
            }

            if (causalitySolverResultActual.isAc1() && causalitySolverResultActual.isAc2() && causalitySolverResultActual.isAc3()){
                double prob = computeProbability(context, map);
                probCause += prob;
                probModels += prob;
            } else {
                probModels += computeProbability(context, map);
            }
        }

        return probCause/probModels;
    }

    private double computeProbability(Set<Literal> context, Map<Variable, Double> map){
        double temp = 1.0;
        for (Literal literal : context) {
            double tmp = map.get(literal.variable());
            if (!literal.phase()) {
                tmp = 1.0 - tmp;
            }
            temp *= tmp;
        }

        return temp;
    }

    /**
     * Helper method to check if the given context fulfills the assignment of observed variables
     * @param know Variables whose value was observed
     * @param context of the current causal model
     * @return true if the context
     */
    private boolean checkKnownAssignment(Set<Literal> know, Set<Literal> context){
        boolean skipModel = false;
        for(Literal knownLiteral : know){
            if (!context.contains(knownLiteral)) {
                skipModel = true;
                break;
            }
        }

        return skipModel;
    }

    public double solveProb() throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        int fulfills = 0;
        for(Set<Literal> context : contexts) {
            CausalitySolverResult causalitySolverResultActual = model.isCause(context, phi, cause, SolvingStrategy.BRUTE_FORCE);

            // if phi is not fulfilled then do not check this model
            if (!causalitySolverResultActual.isAc1()) {
                continue;
            }

            if (causalitySolverResultActual.isAc1() && causalitySolverResultActual.isAc2() && causalitySolverResultActual.isAc3()) {
                fulfills++;
            }
        }

        return (double) fulfills / (double) contexts.size();
    }
}
