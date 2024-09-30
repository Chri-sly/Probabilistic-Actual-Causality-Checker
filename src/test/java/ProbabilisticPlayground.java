import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.probabilistic.ProbabilisticCausalitySolverResult;
import de.tuda.aiml.probabilistic.ProbabilisticSolvingStrategy;
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

public class ProbabilisticPlayground {
    public static void main(String[] args) throws InvalidCausalModelException, InvalidContextException, InvalidCauseException, InvalidPhiException {
        ProbabilisticCausalModel dons = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = dons.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("C")));
        Formula phi = f.variable("D");

        ProbabilisticCausalitySolverResult result = dons.isCause(context, phi, cause, ProbabilisticSolvingStrategy.PROBABILITY_RAISING);

        System.out.println("Probability raising: " + result.toString());
    }
}
