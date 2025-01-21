package de.tuda.aiml.blame;

import de.tuda.aiml.util.DeterministicExampleProvider;
import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class CausalitySolverBlameInstanceTest {

    @Test
    public void firing_squad_sharing_blame() throws Exception {
        CausalModel firingSquad = DeterministicExampleProvider.firingSquad();
        FormulaFactory f = firingSquad.getFormulaFactory();

        List<Set<Literal>> possibleContexts = new ArrayList<>();
        possibleContexts.add(new HashSet<>(Arrays.asList(
                f.literal("M1_exo", true), f.literal("M2_exo", false), f.literal("M3_exo", false)
        )));
        possibleContexts.add(new HashSet<>(Arrays.asList(
                f.literal("M1_exo", false), f.literal("M2_exo", true), f.literal("M3_exo", false)
        )));
        possibleContexts.add(new HashSet<>(Arrays.asList(
                f.literal("M1_exo", false), f.literal("M2_exo", false), f.literal("M3_exo", true)
        )));

        List<Set<Literal>> causes = new ArrayList<>();
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M1"))));
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M2"))));
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M3"))));
        Formula phi = f.variable("D");

        double sharedBlame = 1.0/3.0;

        for(Set<Literal> cause : causes){
            BlameSolver BS = new BlameSolver(firingSquad, phi, cause, SolvingStrategy.ORIGINAL_HP);
            assertEquals(sharedBlame, BS.getBlameSameProbabilities(possibleContexts), 1e-7);
        }
    }

    @Test
    public void firing_squad_M2_more_likely_to_have_the_bullet() throws Exception {
        CausalModel firingSquad = DeterministicExampleProvider.firingSquad();
        FormulaFactory f = firingSquad.getFormulaFactory();

        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("M1_exo", true), f.literal("M2_exo", false), f.literal("M3_exo", false)
        ));
        Set<Literal> context2 = new HashSet<>(Arrays.asList(
                f.literal("M1_exo", false), f.literal("M2_exo", true), f.literal("M3_exo", false)
        ));
        Set<Literal> context3 = new HashSet<>(Arrays.asList(
                f.literal("M1_exo", false), f.literal("M2_exo", false), f.literal("M3_exo", true)
        ));

        HashMap<Set<Literal>, Double> contextsMap = new HashMap<>();
        contextsMap.put(context1, 0.25);
        contextsMap.put(context2, 0.5);
        contextsMap.put(context3, 0.25);

        List<Set<Literal>> causes = new ArrayList<>();
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M1"))));
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M2"))));
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M3"))));
        Formula phi = f.variable("D");

        double sharedBlame = 1.0/3.0;

         BlameSolver BS1 = new BlameSolver(firingSquad, phi, causes.get(0), SolvingStrategy.ORIGINAL_HP);
         assertEquals(0.25, BS1.getBlameDifferentProbabilities(contextsMap), 1e-7);

        BlameSolver BS2 = new BlameSolver(firingSquad, phi, causes.get(1), SolvingStrategy.ORIGINAL_HP);
        assertEquals(0.5, BS2.getBlameDifferentProbabilities(contextsMap), 1e-7);

        BlameSolver BS3 = new BlameSolver(firingSquad, phi, causes.get(2), SolvingStrategy.ORIGINAL_HP);
        assertEquals(0.25, BS3.getBlameDifferentProbabilities(contextsMap), 1e-7);
    }

    @Test
    public void firing_squad_do_not_consider_M3() throws Exception {
        CausalModel firingSquad = DeterministicExampleProvider.firingSquad();
        FormulaFactory f = firingSquad.getFormulaFactory();

        List<Set<Literal>> possibleContexts = new ArrayList<>();
        possibleContexts.add(new HashSet<>(Arrays.asList(
                f.literal("M1_exo", true), f.literal("M2_exo", false), f.literal("M3_exo", false)
        )));
        possibleContexts.add(new HashSet<>(Arrays.asList(
                f.literal("M1_exo", false), f.literal("M2_exo", true), f.literal("M3_exo", false)
        )));

        List<Set<Literal>> causes = new ArrayList<>();
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M1"))));
        causes.add(new HashSet<>(Collections.singletonList(f.variable("M2"))));
        Formula phi = f.variable("D");

        for(Set<Literal> cause : causes){
            BlameSolver BS = new BlameSolver(firingSquad, phi, cause, SolvingStrategy.ORIGINAL_HP);
            assertEquals(0.5, BS.getBlameSameProbabilities(possibleContexts), 1e-7);
        }

        Set<Literal> cause3 = new HashSet<>(Collections.singletonList(f.variable("M3")));
        BlameSolver BS = new BlameSolver(firingSquad, phi, cause3, SolvingStrategy.ORIGINAL_HP);
        assertEquals(0.0, BS.getBlameSameProbabilities(possibleContexts), 1e-7);
    }
}
