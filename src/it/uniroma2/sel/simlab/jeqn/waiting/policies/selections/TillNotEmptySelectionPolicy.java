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

package it.uniroma2.sel.simlab.jeqn.waiting.policies.selections;

import it.uniroma2.sel.simlab.jeqn.policies.ConditionBasedPolicy;
import it.uniroma2.sel.simlab.jeqn.policies.FixedDecisionPolicy;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskStateDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;

import java.util.List;

/** Implements a selection policy that keeps selecting from a UserQueue until it
 * gets empty. Then passes to another queue according to the given ChangeQueue policy.
 *
 * @author Daniele Gianni
 */
public class TillNotEmptySelectionPolicy extends ConditionBasedPolicy<List</*? extends */UserQueue>, User, UserQueue, UserQueue> {
    
    /**
     * Creates a new instance of TillNotEmptySelectionPolicy
     * @param multiQueue The queue system upon which the decision is made
     * @param initialQueue The queue it starts extracting from
     * 
     * @param changingQueuePolicy The next queue selection policy
     */
    public TillNotEmptySelectionPolicy(final List</*? extends*/ UserQueue> multiQueue, final UserQueue initialQueue, final MaskBasePolicy<?, User, ?, UserQueue> changingQueuePolicy) {
        super(multiQueue, initialQueue, new MaskStateDependentPolicy<User, UserQueue, UserQueue>(new FixedDecisionPolicy(initialQueue)), changingQueuePolicy);
    }

    protected boolean condition(final User u) {
        //System.out.println("TillNotEmptySelectionPolicy : STATE == " + state.isEmpty());
        return !state.isEmpty();
    }
    
    protected UserQueue doOpTrue(final UserQueue q) {
        assert state.isEmpty();
        //System.out.println("Condition TRUE!!");
        //if (state.isEmpty()) System.out.println("AHHHHHH CODA VUOTA CON CONDIZIONE TRUE!!!");
        return state;       
    }        
    
    protected UserQueue doOpFalse(final UserQueue q) {
        assert (!q.isEmpty());                
        setState(q);
        //System.out.println("Condition FALSE!!");
        //if (q.isEmpty()) System.out.println("AHHHHHH CODA VUOTA CON CONDIZIONE FALSE!!!");
        return q;
    }    
}
