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

import it.uniroma2.sel.simlab.jeqn.policies.MapImplicitAndExplicitInputDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.users.Category;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;
import it.uniroma2.sel.simlab.jrand.objectStreams.streamTrasformations.MappedStream;

/** Defines a dispatching policy for a multi-queue structure. The policy is based on a mapping
 * between the set of user categories and the queues in the multi-queue structure
 *
 * @author  Daniele Gianni
 */
public class CategoryBasedMapDispatchingPolicy extends MapImplicitAndExplicitInputDependentPolicy<Category, UserQueue> {
   
    /** Creates a new instance of GeneralQueuesAssigner */
    public CategoryBasedMapDispatchingPolicy(final MappedStream<UserQueue> s) {
        super();
    }
}
