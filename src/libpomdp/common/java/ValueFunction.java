/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: ValueFunction.java
 * Description: representation of a set of alpha vectors and their
 *              associated actions for direct control (if possible)
 * Copyright (c) 2009, 2010 Diego Maniloff 
 --------------------------------------------------------------------------- */

package libpomdp.common.java;


/** Representation of a set of alpha vectors and their associated actions for direct control (if possible)
    @author Diego Maniloff 
    @author Mauricio Araya*/
public interface ValueFunction {
    
    public double V(BeliefState b);

    public CustomVector getVector(int idx);

    public int[] getActions();

    public int size();
    
    public AlphaVector getAlpha(int idx);
    public CustomVector getAlphaValues(int idx);
    
} // ValueFunction
