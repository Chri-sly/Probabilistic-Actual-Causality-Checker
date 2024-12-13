package de.tuda.aiml.util;

import org.logicng.formulas.Literal;

import java.util.Set;

/**
 * Class that contains utility methods that are used in multiple approaches
 */
public class UtilityMethods {

    public static boolean noDuplicates(Set<Literal> literalSet){
        boolean flag = true;
        for(Literal literal : literalSet){
            if(literalSet.contains(literal.negate())){
                flag = false;
                break;
            }
        }
        return flag;
    }
}
