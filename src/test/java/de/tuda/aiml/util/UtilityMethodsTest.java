package de.tuda.aiml.util;

import org.junit.Test;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for the UtilityMethods class
 */
public class UtilityMethodsTest {

    @Test
    public void noDuplicatesTestTrueWhenNoDuplicates(){
        FormulaFactory f = new FormulaFactory();
        Set<Literal> literalSet = new HashSet<>(Arrays.asList(f.literal("A", true),
                f.literal("B", false), f.literal("C", true)));

        assertTrue(UtilityMethods.noDuplicates(literalSet));
    }

    @Test
    public void noDuplicatesTestFalseWhenDuplicates(){
        FormulaFactory f = new FormulaFactory();
        Set<Literal> literalSet = new HashSet<>(Arrays.asList(f.literal("A", true),
                f.literal("A", false), f.literal("C", true)));

        assertFalse(UtilityMethods.noDuplicates(literalSet));
    }

    @Test
    public void noDuplicatesTestTrueWhenEmptySet(){
        FormulaFactory f = new FormulaFactory();
        Set<Literal> literalSet = new HashSet<>();

        assertTrue(UtilityMethods.noDuplicates(literalSet));
    }
}
