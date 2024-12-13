package de.tuda.aiml.probabilityRaising;

import de.tuda.aiml.probabilistic.ProbabilisticCausalModel;
import de.tuda.aiml.util.ProbabilisticExampleProvider;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class InterventionalProbabilityRaisingInstanceTest {

    @Test
    public void Corleones_Order_Cause_of_Death() throws Exception {
        ProbabilisticCausalModel Don_Corleone = ProbabilisticExampleProvider.donPolice();
        FormulaFactory f = Don_Corleone.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("CI_exo", true), f.literal("BI_exo", true)
        ));

        Set<Literal> cause = new HashSet<>();
        cause.add(f.variable("C"));

        Formula phi = f.variable("D");

        ProbabilityRaisingResult probabilityRaisingResultExpected =
                new ProbabilityRaisingResult(false, 0.531, 0.81);

        ProbabilityRaisingResult result = InterventionalProbabilityRaising.computeActual(Don_Corleone, phi, cause, context);

        assertEquals(probabilityRaisingResultExpected, result);
    }
}
