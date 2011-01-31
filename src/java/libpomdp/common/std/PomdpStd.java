/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: 
 * Description: Represent a POMDP model using a flat representation and
 *              sparse matrices and vectors. This class can be constructed
 *              from a pomdpSpecSparseMTJ object after parsing a .pomdp file.
 *              Sparse matrices by matrix-toolkits-java, 
 *              every matrix will be CustomMatrix:
 *              
 * S =
 *  (3,1)        1
 *  (2,2)        2
 *  (3,2)        3
 *  (4,3)        4
 *  (1,4)        5
 * A =
 *   0     0     0     5
 *   0     2     0     0
 *   1     3     0     0
 *   0     0     4     0
 * Copyright (c) 2009, 2010, 2011 Diego Maniloff
 * Copyright (c) 2010, 2011 Mauricio Araya
 --------------------------------------------------------------------------- */

package libpomdp.common.std;

// imports
import java.io.Serializable;

import libpomdp.common.AlphaVector;
import libpomdp.common.BeliefMdp;
import libpomdp.common.BeliefState;
import libpomdp.common.CustomMatrix;
import libpomdp.common.CustomVector;
import libpomdp.common.Pomdp;
import libpomdp.common.Utils;

public class PomdpStd implements Pomdp, Serializable {

    /**
     * Generated by Eclipse.
     */
    private static final long serialVersionUID = -5511401938934887929L;

    // ------------------------------------------------------------------------
    // properties
    // ------------------------------------------------------------------------

    // number of states
    private int nrSta;

    // private nrAct
    private int nrAct;

    // private nrObs
    private int nrObs;

    // transition model: a x s x s'
    private CustomMatrix T[];

    // observation model: a x s' x o
    private CustomMatrix O[];

    // reward model: a x s'
    private CustomVector R[];

    // discount factor
    private double gamma;

    // action names
    private String actStr[];

    // observation names
    private String obsStr[];

    // state names
    private String staStr[];

    // starting belief
    private BeliefStateStd initBelief;

    // ------------------------------------------------------------------------
    // methods
    // ------------------------------------------------------------------------

    // / constructor
    public PomdpStd(CustomMatrix[] O, CustomMatrix[] T, CustomVector[] R,
	    int nrSta, int nrAct, int nrObs, double gamma, String staStr[],
	    String actStr[], String obsStr[], CustomVector init) {

	// allocate space for the pomdp models
	this.nrSta = nrSta;
	this.nrAct = nrAct;
	this.nrObs = nrObs;
	this.T = new CustomMatrix[nrAct];
	this.O = new CustomMatrix[nrAct];
	this.R = new CustomVector[nrAct];
	this.gamma = gamma;
	this.actStr = actStr;
	this.obsStr = obsStr;

	// set initial belief state
	this.initBelief = new BeliefStateStd(init, 0.0);

	// copy the model matrices - transform from dense to comprow
	// do we really need this? dense is in sparse form already...
	for (int a = 0; a < nrAct; a++) {
	    this.O[a] = new CustomMatrix(T[a]);
	    this.T[a] = new CustomMatrix(O[a]);
	    this.R[a] = new CustomVector(R[a]);
	}
    } // constructor

    public PomdpStd(PomdpStd pomdp) {
	this.nrSta = pomdp.nrSta;
	this.nrAct = pomdp.nrAct;
	this.nrObs = pomdp.nrObs;
	this.T = pomdp.T;
	this.O = pomdp.O;
	this.R = pomdp.R;
	this.gamma = pomdp.gamma;
	this.staStr = pomdp.staStr;
	this.actStr = pomdp.actStr;
	this.obsStr = pomdp.obsStr;
	this.initBelief = pomdp.initBelief;
    }

    // / tao(b,a,o)
    
    public BeliefState nextBeliefState(BeliefState b, int a, int o) {
	// long start = System.currentTimeMillis();
	// System.out.println("made it to tao");
	BeliefState bPrime;
	// compute T[a]' * b1
	CustomVector b1 = b.getPoint();
	CustomVector b2 = new CustomVector(nrSta);
	b2 = T[a].transMult(b1);
	// System.out.println("Elapsed in tao - T[a] * b1" +
	// (System.currentTimeMillis() - start));

	// element-wise product with O[a](:,o)
	b2.elementMult(O[a].getColumn(o));
	// System.out.println("Elapsed in tao - O[a] .* b2" +
	// (System.currentTimeMillis() - start));

	// compute P(o|b,a) - norm1 is the sum of the absolute values
	double poba = b2.norm(1.0);
	// make sure we can normalize
	if (poba < 0.00001) {
	    // System.err.println("Zero prob observation - resetting to init");
	    // this branch will have poba = 0.0
	    bPrime = initBelief;
	} else {
	    // safe to normalize now
	    b2 = b2.scale(1.0 / poba);
	    bPrime = new BeliefStateStd(b2, poba);
	}
	// System.out.println("Elapsed in tao" + (System.currentTimeMillis() -
	// start));
	// return
	return bPrime;
    }

    /// R(b,a)
    
    public double expectedImmediateReward(BeliefState bel, int a) {
	CustomVector b = ((BeliefStateStd) bel).bSparse;
	return b.dot(R[a]);
    }

    // P(o|b,a) in vector form for all o's

    public CustomVector observationProbabilities(BeliefState b, int a) {
	CustomVector b1 = b.getPoint();
	CustomVector Tb = new CustomVector(nrSta);
	Tb = T[a].mult(b1);
	CustomVector Poba = new CustomVector(nrObs);
	Poba = O[a].transMult(Tb);
	return Poba;
    }

    
    public CustomMatrix getTransitionTable(int a) {
	return T[a].copy();
    }

    
    public CustomMatrix getObservationTable(int a) {
	return O[a].copy();
    }

    
    public CustomVector getImmediateRewards(int a) {
	return R[a].copy();
    }

    
    public BeliefState getInitialBeliefState() {
	return initBelief.copy();
    }

    
    public int nrStates() {
	return nrSta;
    }

    
    public int nrActions() {
	return nrAct;
    }

    
    public int nrObservations() {
	return nrObs;
    }

    
    public double getGamma() {
	return gamma;
    }

    
    public String getActionString(int a) {
	return actStr[a];
    }

    
    public String getObservationString(int o) {
	return obsStr[o];
    }

    
    public String getStateString(int s) {
	return staStr[s];
    }

    public int getRandomAction() {
        return (Utils.gen.nextInt(Integer.MAX_VALUE) % nrActions());
    }

    /// ???
    public int sampleObservation(BeliefState bel, int a) {
	double roulette = Utils.gen.nextDouble();
	CustomVector vect = O[a].mult(bel.getPoint());
	double sum = 0.0;
	for (int o = 0; o < nrObs; o++) {
	    sum += vect.get(o);
	    if (roulette < sum)
		return o;
	}
	return (-1);
    }

    public AlphaVectorStd mdpValueUpdate(AlphaVector alpha, int a) {
	CustomVector vec = getTransitionTable(a).mult(getGamma(),
		(CustomVector)alpha.getInternalRef());
	vec.add(getRewardValueFunction(a).getAlpha(0).getInternalRef());
	return (new AlphaVectorStd(vec, a));
    }

    public ValueFunctionStd getRewardValueFunction(int a) {
	ValueFunctionStd vf = new ValueFunctionStd();
	vf.push(R[a].copy(), a);
	return vf;
    }

    public double getRewardMax() {
	double max_val = Double.NEGATIVE_INFINITY;
	for (int a = 0; a < nrActions(); a++) {
	    double test_val = getRewardMax(a);
	    if (test_val > max_val)
		max_val = test_val;
	}
	return max_val;
    }

    public double getRewardMin() {
	double min_val = Double.POSITIVE_INFINITY;
	for (int a = 0; a < nrActions(); a++) {
	    double test_val = getRewardMin(a);
	    if (test_val < min_val)
		min_val = test_val;
	}
	return min_val;
    }

    public double getRewardMaxMin() {
	double max_val = Double.NEGATIVE_INFINITY;
	for (int a = 0; a < nrActions(); a++) {
	    double test_val = getRewardMin(a);
	    if (test_val > max_val)
		max_val = test_val;
	}
	return max_val;
    }

    public double getRewardMin(int a) {
	return (R[a].min());
    }

    public double getRewardMax(int a) {
	return (R[a].max());
    }

    public AlphaVector getRewardVec(int a, BeliefState bel) {
	return (new AlphaVectorStd(R[a].copy(), a));
    }

	public AlphaVector getEmptyAlpha() {
		return new AlphaVectorStd(nrSta);
	}

	public BeliefMdp getBeliefMdp() {
		return new BeliefMdpStd(this);
	}
	
	public AlphaVector getHomogeneAlpha(double bestVal) {
		return (new AlphaVectorStd(CustomVector.getHomogene(nrStates(),
				bestVal), -1));
	}

	public AlphaVector getEmptyAlpha(int a) {
		return (new AlphaVectorStd(nrStates(), a));
	}



} // PomdpStd.java

