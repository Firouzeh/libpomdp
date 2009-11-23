/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: valueFunction.java
 * Description: representation of a set of alpha vectors and their
 *              associated actions for direct control (if possible)
 * Copyright (c) 2009, Diego Maniloff 
 * W3: http://www.cs.uic.edu/~dmanilof
 --------------------------------------------------------------------------- */

interface valueFunction {
    
    // simple list of actions associated with each alpha
    public int[] getActions();

    // value of a belief according to this value function
    public double V(belState b);

    // flat representation
    public double[][] getvFlat();

} // valueFunction