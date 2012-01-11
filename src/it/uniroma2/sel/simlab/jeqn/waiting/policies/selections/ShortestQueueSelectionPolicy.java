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

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitButNotExplicitInputOnlyDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserMultiQueue;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;

/** Implements a selection policy that pick up the short queue
 *
 * @author Daniele Gianni
 */
public class ShortestQueueSelectionPolicy extends ImplicitButNotExplicitInputOnlyDependentPolicy<UserMultiQueue, UserQueue>  {
    
    /**
     * Creates a new instance of ShortestQueueDispatchingPolicy
     * @param userMultiQueue The multi queue system upon which the decision is made
     */
    public ShortestQueueSelectionPolicy(final UserMultiQueue userMultiQueue) {
        super(userMultiQueue);
    }

    public UserQueue getDecision() {
        
        if (implicitInput.isEmpty()) {
           // System.out.println("Multi QUEUE EMPTY!!!!!!!!");
        }
        UserQueue shortestQueue = implicitInput.getUserQueue(0);
        
        for (UserQueue uq : implicitInput.getUserQueues()) {            
            if (uq.isFull()) {
                if (uq.getEnqueuedUsers() < shortestQueue.getEnqueuedUsers()) {
                    shortestQueue = uq;
                } else if (shortestQueue.isEmpty()) {
                    shortestQueue = uq;
                }                              
            }
        }
        
        return shortestQueue;
    }
    
}
