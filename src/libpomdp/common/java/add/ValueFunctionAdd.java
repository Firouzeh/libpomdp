/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: ValueFunctionAdd.java
 * Description: implementation of a value function via ADDs
 *              makes use of Poupart's OP class to manipulate ADDs
 *              see README reference [5]
 *              implements Serializable so we can use the save command
 *              in Matlab
 * Copyright (c) 2009, 2010 Diego Maniloff 
 * W3: http://www.cs.uic.edu/~dmanilof
 --------------------------------------------------------------------------- */

package libpomdp.common.java.add;

// imports
import java.io.Serializable;

import libpomdp.common.java.BeliefState;
import libpomdp.common.java.CustomVector;
import libpomdp.common.java.Util;
import libpomdp.common.java.ValueFunction;
import symPerseusJava.DD;
import symPerseusJava.OP;

public class ValueFunctionAdd implements ValueFunction, Serializable {

    // ------------------------------------------------------------------------
    // properties
    // ------------------------------------------------------------------------

    // serial id
    static final long serialVersionUID = 5L;

    // represent a value function via an array of Adds
    private DD vAdd[];

    // staIds of the problem
    private int staIds[];

    // actions associated to each alpha vector
    private int a[];

    // constructor
    public ValueFunctionAdd(DD vAdd[], int staIds[], int a[]) {
	this.vAdd   = vAdd; 
	this.a      = a;
	this.staIds = staIds;
    }

    // ------------------------------------------------------------------------
    // interface methods
    // ------------------------------------------------------------------------

    // return value of a belief state
    @Override
    public double V(BeliefState bel) {
        // declarations
        DD     b;
        DD     m[];
        double dotProds[];
        // compute dot products
        if (bel instanceof BeliefStateAdd) {
            b = ((BeliefStateAdd)bel).bAdd; 
            dotProds = OP.dotProductNoMem(b, vAdd, staIds);
        } else {
            m = ((BelStateFactoredAdd)bel).marginals;
            dotProds = OP.factoredExpectationSparseNoMem(m, vAdd);
        }
        // find best vector
        int argmax = Util.argmax(dotProds);
        // save the index of the alpha that supports this belief point
        bel.setAlpha(argmax);
        return dotProds[argmax];
    }

    @Override
    public CustomVector getVector(int idx) {
        double[][] val=OP.convert2array(vAdd, staIds);
        return new CustomVector(val[idx]);
    }

    // list of actions associated with each alpha
    @Override
    public int[] getActions() {
	return a;
    }

    @Override
    public int size() {
	return a.length;	
    }

    // return flat value function
    public double[][] getvFlat() {
	return OP.convert2array(vAdd, staIds);
    }    

    // return Add representation of this value function
    public DD[] getvAdd() {
	return vAdd;
    }

} // valueFunctionAdd
