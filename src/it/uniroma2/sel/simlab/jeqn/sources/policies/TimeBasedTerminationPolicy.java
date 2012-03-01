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

package it.uniroma2.sel.simlab.jeqn.sources.policies;

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitButNotExplicitInputAndStateDependentPolicy;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;

/** Implements the policy that stops the source execution after a simulation time is reached
 *
 * @author Daniele Gianni
 */
public class TimeBasedTerminationPolicy extends ImplicitButNotExplicitInputAndStateDependentPolicy<Time, Layer3ToLayer2, Boolean> {    
    
    /** Creates a new instance of TimeBasedTerminationPolicy */
    public TimeBasedTerminationPolicy(Time stopTime, Layer3ToLayer2 layer3ToLayer2) {
        super(stopTime, layer3ToLayer2);
    }
    
    /**
     * Returns the decision of this policy. 
     * @return The {@code Boolean} object that indicates whether or not the user generation has to be terminated.
     */
    public Boolean getDecision() {
        return state.getClock().isGreaterOrEqualThan(implicitInput);
    }        
}
