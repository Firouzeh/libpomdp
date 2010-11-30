/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: blindFlat.java
 * Description: blind policy value function approximation with a flat
 *              representation
 *              do not try on large problems as this will run out of mem
 * Copyright (c) 2009, 2010 Diego Maniloff 
 * W3: http://www.cs.uic.edu/~dmanilof
 --------------------------------------------------------------------------- */

package libpomdp.solve.vi.heuristic;

import libpomdp.common.AlphaVector;
import libpomdp.common.CustomVector;
import libpomdp.common.std.PomdpStd;
import libpomdp.common.std.ValueFunctionStd;
import libpomdp.solve.IterationStats;
import libpomdp.solve.vi.ValueIterationStd;


public class BpviStd extends ValueIterationStd {
	
	public BpviStd(PomdpStd pomdp){
		startTimer();
		initValueIteration(pomdp);
		// Blind is |A| x |S| - initialize each \alpha^a_{0} to \min_s {R(s,a)/(1-\gamma)}
		current = new ValueFunctionStd(pomdp.nrStates());
		for(int a=0; a<pomdp.nrActions(); a++) {
			double factor=1.0/(1.0-pomdp.getGamma());
			double val=pomdp.getRewardMin();
			val*=factor;
		    current.push(CustomVector.getHomogene(pomdp.nrStates(),val),a);
		}
		registerInitTime();
	}
	

	@Override
	public IterationStats iterate() {
		startTimer();
		old=current.copy();
    	for(int a=0; a<pomdp.nrActions(); a++) {
    	    AlphaVector vec=current.getAlpha(a);
    	    AlphaVector res=pomdp.mdpValueUpdate(vec,a);
    	    vec.set(res);
    	}
    	registerValueIterationStats();
    	return iterationStats;
	}

    
    
}