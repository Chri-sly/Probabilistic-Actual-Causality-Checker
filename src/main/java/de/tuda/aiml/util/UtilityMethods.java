package de.tuda.aiml.util;

import org.logicng.formulas.Literal;

import java.util.Set;

/**
 * Class that contains utility methods that are used in multiple approaches
 */
public class UtilityMethods {

    /**
     * Whether the set of literals contains both phases of a literal or not
     * @param literalSet Set of literals that are either positive or negative
     * @return true if the set of literals contains not both values (true and negative) of one literal else false
     */
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
