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

package it.uniroma2.sel.simlab.jeqn.waiting.policies.preemptions;

import it.uniroma2.sel.simlab.jeqn.policies.ExplicitInputPolicy;
import it.uniroma2.sel.simlab.jeqn.policies.ImplicitAndExplicitInputDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;

import java.util.List;

/** Implements a generic User Priority Preemption Policy. The preemption decision is made upon
 * the {@code compareTo} method of decision data extracted from the given users. 
 *
 * @author Daniele Gianni
 */
public class UserPriorityPreemptionPolicy extends ImplicitAndExplicitInputDependentPolicy<User, User, Boolean> {
    
    /** Creates a new instance of UserPriorityPreemptionPolicy */
    public UserPriorityPreemptionPolicy() {
        
    }

    public Boolean getDecisionFor(User u) {
                        
        Comparable underProcessing = (Comparable )getImplicitInput();
        Comparable newEntry = (Comparable )u;
        
        int comparisonResult = underProcessing.compareTo(newEntry);
        
        return (comparisonResult < 0);
    }    
}