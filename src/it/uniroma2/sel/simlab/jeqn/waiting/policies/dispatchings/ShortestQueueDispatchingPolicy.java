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

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitButNotExplicitInputOnlyDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;
import java.util.List;

/** Implements the dispatching policy for a user reaching a multi-queue structure.
 * The policy considers the shortest queue (in terms of number of enqueued users).
 *
 * @author Daniele Ganni
 */
public class ShortestQueueDispatchingPolicy extends ImplicitButNotExplicitInputOnlyDependentPolicy<List<UserQueue>, UserQueue>  {
        
    /**
     * Creates a new instance of ShortestQueueDispatchingPolicy
     * @param userMultiQueue The multi queue upon which the decision is taken
     */
    public ShortestQueueDispatchingPolicy(final List<UserQueue> userMultiQueue) {
        super(userMultiQueue);
    }
    
    public UserQueue getDecision() {        
        UserQueue shortestQueue = implicitInput.get(0);

        for (UserQueue uq : implicitInput) {
            if (uq.getEnqueuedUsers() < shortestQueue.getEnqueuedUsers()) {
                shortestQueue = uq;
            }
        }
        return shortestQueue;
    }    
}
