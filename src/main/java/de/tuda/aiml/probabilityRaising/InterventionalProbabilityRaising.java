package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolver;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolverResult;
import de.tuda.aiml.probabilistic.ProbabilisticSolvingStrategy;
import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolver;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.util.Pair;

import java.util.*;

public class InterventionalProbabilityRaising extends ProbabilisticCausalitySolver{

    public boolean compute(ProbabilisticCausalModel model, Formula phi, Set<Literal> cause, Set<Literal> context) throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();
        FormulaFactory f = model.getFormulaFactory();

        // create modified causal model by replacing the cause x with x'.
        ProbabilisticCausalModel causalModelModified = createModifiedCausalModelForCause(model, cause, f);

        return false;
    }

    private double checkProbability(ProbabilisticCausalModel model,  Formula phi, Set<Literal> cause, Set<Literal> context){
        Map<Variable, Double> exogenousVariables = model.getExogenousVariables();
        FormulaFactory f = model.getFormulaFactory();
        double prob = 0.0;


        Set<Literal> evaluationModified = ProbabilisticCausalitySolver.evaluateEquations(model, context);

        // If Phi is fulfilled
        if (phi.evaluate(new Assignment(evaluationModified))) {
            prob += 0.0;
        }

        return prob;
    }

    @Override
    protected ProbabilisticCausalitySolverResult solve(ProbabilisticCausalModel causalModel, Set<Literal> context, Formula phi, Set<Literal> cause, ProbabilisticSolvingStrategy solvingStrategy) throws InvalidCausalModelException {
        return null;
    }
}
