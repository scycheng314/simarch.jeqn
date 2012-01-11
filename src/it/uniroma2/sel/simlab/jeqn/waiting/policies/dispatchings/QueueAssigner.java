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

import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;

/** Provides all the mechanisms necessary for the queue assignment of a user reaching a multi-queue structure.
 * It basically consists of a dispatching policy and a decision data factory to build up
 * decision data from the incoming user.
 *
 * @author Daniele Gianni
 */
public class QueueAssigner {

    /*
     * The policy that determinates on which queue the incoming users will be enqueued
     */
    protected MaskBasePolicy<?, User, ?, UserQueue> dispatchingPolicy;
   
    /** Creates a new instance of QueueAssigner */
    public QueueAssigner(MaskBasePolicy<?, User, ?, UserQueue> dispatchingPolicy) {
        setDispatchingPolicy(dispatchingPolicy);
    }
    
    public UserQueue getUserQueue(final User u) {        
        return dispatchingPolicy.getDecisionFor(u);
    }

    private void setDispatchingPolicy(MaskBasePolicy<?, User, ?, UserQueue> p) {
        dispatchingPolicy = p;
    }
}
