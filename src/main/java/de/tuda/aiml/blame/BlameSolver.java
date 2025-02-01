package de.tuda.aiml.blame;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import org.logicng.formulas.Formula;
import org.logicng.formulas.Literal;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of computing the Blame defined by Halpern and Chockler.
 */
public class BlameSolver {
    private Formula phi;
    private Set<Literal> cause;
    private CausalModel model;
    private SolvingStrategy strategy;

    public BlameSolver(CausalModel model, Formula phi, Set<Literal> cause, SolvingStrategy strategy){
       this.cause = cause;
       this.phi = phi;
       this.model = model;
       this.strategy = strategy;
    }

    /**
     * Compute the degree of blame in which each context is same likely to occur.
     * @param contexts The contexts that may occur
     * @return the degree of blame of the cause for the effect phi
     */
    public double getBlameSameProbabilities(List<Set<Literal>> contexts) throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        double blame = 0.0;
        for (Set<Literal> context : contexts) {
            CausalitySolverResult result = model.isCause(context, phi, cause, strategy);
            for (Literal c : cause){
                blame += result.getResponsibility().get(c) * 1/contexts.size();
            }
        }

        return blame;
    }

    /**
     * Compute the degree of blame by also providing the probabilities of each context
     * @param contextsMap The contexts and their probability that may occur
     * @return the degree of blame of the cause for the effect phi
     */
    public double getBlameDifferentProbabilities(Map<Set<Literal>, Double> contextsMap) throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        double blame = 0.0;
        for (Map.Entry<Set<Literal>, Double> pair : contextsMap.entrySet()) {
            CausalitySolverResult result = model.isCause(pair.getKey(), phi, cause, strategy);
            for (Literal c : cause){
                blame += result.getResponsibility().get(c) * pair.getValue();
            }
        }

        return blame;
    }
}
