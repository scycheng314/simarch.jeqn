/*
 * 	Copyright (C) 2005-2011 Department of Enteprise Engineering, University of Rome "Tor Vergata"
 *                              ( http://www.dii.uniroma2.it )
 *
 *      This file is part of jEQN and was developed at the Software Engineering Laboratory
 *      ( http://www.sel.uniroma2.it )
 *
 *      jEQN is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      jEQN is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with jEQN.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package it.uniroma2.sel.simlab.jeqn.policies;

import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;

/** Provides the basic schema for policies that are defined depending on a condition.
 * More specifically, this type of policy consists of two policy: one for a positive condition
 * and one for a negative condition. The condition value (true or false) is directly evaluated
 * on the explicit input to be evaluated for the decision
 *
 * @author Daniele Gianni
 */
public abstract class ConditionBasedPolicy<I, E, S, D> extends MaskBasePolicy<I, E, S, D> {
        
    public MaskBasePolicy<?, E, ?, D> truePolicy;
    protected MaskBasePolicy<?, E, ?, D> falsePolicy;
        
    /** Creates a new instance of ConditionBasedPolicy */
    public ConditionBasedPolicy(final I implicitInput, final S state) {
        super(implicitInput, state);
    }
    
    public ConditionBasedPolicy(final I implicitInput, final S state, final MaskBasePolicy<?, E, ?, D> truePolicy, final MaskBasePolicy<?, E, ?, D> falsePolicy) {
        super(implicitInput, state);
                
        setTruePolicy(truePolicy);
        setFalsePolicy(falsePolicy);
    }

    /**
     * Defines the condition use to choose which policy (true or false) must be applied
     * @param t
     * @return
     */
    protected abstract boolean condition(final E e);

    /**
     * The operation to be carried out on the decision, when the condition is true
     * @param d
     * @return
     */
    protected abstract D doOpTrue(final D d);

    /**
     * The operation to be carried out on the decision, when the condition is false
     * @param d
     * @return
     */
    protected abstract D doOpFalse(final D d);
    
    public D getDecisionFor(final E e) {
        if (condition(e)) {
            return doOpTrue(truePolicy.getDecisionFor(e));
        } else {
            return doOpFalse(falsePolicy.getDecisionFor(e));
        }
    }
    
    /*protected*/ public void setTruePolicy(MaskBasePolicy<?, E, ?, D> p) {
        truePolicy = p;
    }
    
    /*protected*/ public void setFalsePolicy(final MaskBasePolicy<?, E, ?, D> p) {
        falsePolicy = p;
    }
}
