package de.tuda.aiml;

import de.tuda.aiml.blame.BlameSolver;
import de.tuda.aiml.util.DeterministicExampleProvider;
import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.*;

public class Playground {
    public static void main(String[] args) throws InvalidCausalModelException, InvalidContextException, InvalidCauseException, InvalidPhiException {
        CausalModel firingSquad = DeterministicExampleProvider.firingSquad();
        FormulaFactory f = firingSquad.getFormulaFactory();
        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("ONE_exo", true), f.literal("TWO_exo", false), f.literal("THREE_exo", false)
        ));
        Set<Literal> cause1 = new HashSet<>(Collections.singletonList(f.variable("TWO")));
        Formula phi1 = f.variable("DEATH");

        CausalitySolverResult BSResult = firingSquad.isCause(context1, phi1, cause1, SolvingStrategy.ORIGINAL_HP);

        List<Set<Literal>> contexts = new ArrayList<>();
        contexts.add(context1);
        Set<Literal> context2 = new HashSet<>(Arrays.asList(
                f.literal("ONE_exo", false), f.literal("TWO_exo", true), f.literal("THREE_exo", false)
        ));
        contexts.add(context2);

        Set<Literal> context3 = new HashSet<>(Arrays.asList(
                f.literal("ONE_exo", false), f.literal("TWO_exo", false), f.literal("THREE_exo", true)
        ));
        contexts.add(context3);

        BlameSolver BS = new BlameSolver(firingSquad, phi1, cause1, SolvingStrategy.ORIGINAL_HP);

        System.out.println("firingSquad: " + BSResult.toString());
        System.out.println("Resp. : " + BSResult.getResponsibility());
        System.out.println("Blame: " + BS.getBlameSameProbabilities(contexts));

        HashMap<Set<Literal>, Double> contextsMap = new HashMap<>();
        contextsMap.put(context1, 0.25);
        contextsMap.put(context2, 0.5);
        contextsMap.put(context3, 0.25);

        System.out.println("Blame different probs: " + BS.getBlameDifferentProbabilities(contextsMap));
    }
}
