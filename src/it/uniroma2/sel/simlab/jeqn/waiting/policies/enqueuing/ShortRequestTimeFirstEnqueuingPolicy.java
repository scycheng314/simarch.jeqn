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

package it.uniroma2.sel.simlab.jeqn.waiting.policies.enqueuing;

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitAndExplicitInputDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;

import java.util.List;

/** Implements the enqueueing policy ShortRequestTimeFirst aka Short Job First
 *
 * @author Daniele Gianni
 */
public class ShortRequestTimeFirstEnqueuingPolicy extends ImplicitAndExplicitInputDependentPolicy<List<User>, User, Integer> {
    
    /**
     * Creates a new instance of ShortRequestTimeFirstInsertionPolicy
     * @param implicitInput The list of users it has to make a decision upon to
     */
    public ShortRequestTimeFirstEnqueuingPolicy(final List<User> implicitInput) {
        super(implicitInput);
    }
    
    public Integer getDecisionFor(final User u) {
             
        User a;
        
        for (int i = 0; i < getImplicitInput().size(); i++) {
            a = getImplicitInput().get(i);
            if (a.getServiceRequest().getValue().isLesserOrEqualThan(u.getServiceRequest().getValue())) {
                return i;
            }
        } 
        
        return getImplicitInput().size();
    }    
}
