package de.tuda.aiml.pullOut;

import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolver;
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

        System.out.println(cause);
        System.out.println("Result " + probCause);

        for(Set<Literal> context : contexts){
            System.out.println("Zwischen Result " + probCause);
            CausalitySolverResult causalitySolverResultActual = model.isCause(context, phi, cause, this.solvingStrategy);

            // if phi is not fulfilled then do not check this model
            if (!causalitySolverResultActual.isAc1()) {
                continue;
            }

            boolean skipModel = false;
            for(Literal knownLiteral : know){
                if (!context.contains(knownLiteral)) {
                    System.out.println("Continue da " + knownLiteral);
                    skipModel = true;
                    break;
                }
            }

            if (skipModel) {
                continue;
            }

            if (causalitySolverResultActual.isAc1() && causalitySolverResultActual.isAc2() && causalitySolverResultActual.isAc3()){
                double temp = 1.0;
                for (Literal literal : context) {
                    double tmp = map.get(literal.variable());
                    if (!literal.phase()) {
                        tmp = 1.0 - tmp;
                    }
                    temp *= tmp;
                }
                probCause += temp;
            }

            if (causalitySolverResultActual.isAc1()){
                double temp = 1.0;
                for (Literal literal : context) {
                    double tmp = map.get(literal.variable());
                    if (!literal.phase()) {
                        tmp = 1.0 - tmp;
                    }
                    temp *= tmp;
                }
                probModels += temp;
            }
            System.out.println(causalitySolverResultActual);
        }
        double result = probCause/probModels;
        return result;
    }

    public double solveProb() throws InvalidContextException, InvalidCauseException, InvalidCausalModelException, InvalidPhiException {
        int fulfills = 0;
        for(Set<Literal> context : contexts){
            CausalitySolverResult causalitySolverResultActual = model.isCause(context, phi, cause, SolvingStrategy.BRUTE_FORCE);
            System.out.println(causalitySolverResultActual.toString());
            if (causalitySolverResultActual.isAc1() && causalitySolverResultActual.isAc2() && causalitySolverResultActual.isAc3()){
                fulfills++;
            }
        }
        double res = (double) fulfills / (double) contexts.size();

        return res;
    }
}
