import de.tuda.aiml.blame.BlameSolver;
import de.tuda.aiml.probabilistic.*;
import de.tuda.aiml.util.ProbabilisticExampleProvider;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Testing ground for probabilistic approaches
 */
public class ProbabilisticPlayground {

    public static void main(String[] args) throws InvalidCausalModelException, InvalidContextException, InvalidCauseException, InvalidPhiException {

        // PC Approach on the police parade example
        ProbabilisticCausalModel police_parade = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = police_parade.getFormulaFactory();

        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true), f.literal("SonnyShoots", true),
                f.literal("SonnyHits", true), f.literal("TurkShoots", false), f.literal("TurkHits", false)
        ));

        /*
         * Similar as for the context, we specify f.literal("C", true) as cause and f.variable("D") as phi, as we
         * want to express C = 1 and D = 1 respectively.
         */
        Set<Literal> cause = new HashSet<>((Collections.singletonList(f.literal("C", true))));
        Formula phi = f.variable("D");

        // check whether the cause with this phi fulfills all conditions of PC in this context
        ProbabilisticCausalitySolverResult result = police_parade.isCause(context, phi, cause, ProbabilisticSolvingStrategy.PC);

        System.out.println(result.toString());
    }
}
