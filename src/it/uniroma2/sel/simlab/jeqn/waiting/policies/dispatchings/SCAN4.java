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

package it.uniroma2.sel.simlab.jeqn.waiting.policies.dispatchings;

import it.uniroma2.sel.simlab.jeqn.policies.ObjectSequencePolicy;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserMultiQueue;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;

/** Implements a SCAN dispatching policy for a multi-queue structure consisting of
 * four queues.
 *
 * @author Daniele Gianni
 */
public class SCAN4 extends ObjectSequencePolicy<UserQueue> {
        
    private UserMultiQueue q;
    private int currentQueue = 0;
    
    /** Creates a new instance of SCAN4 */
    public SCAN4() {
    }
    
    /**
     * Sets the internal multi-queue
     * @param umq multi-queue structure
     */
    public void setQ(final UserMultiQueue umq) {
        q = umq;
    }
    
    /**
     * Gets the policy decision
     * 
	 * @return Returns one of the queue contained in the internal multi-queue structure according to a round-robin policy (SCAN among the queues).
     */
    public UserQueue getDecision() {
        currentQueue++;
        return q.getUserQueue(currentQueue % 4);
    }
}
