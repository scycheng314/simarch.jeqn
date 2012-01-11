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

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitButNotEsplicitMaskingStateOnlyDependentPolicy;

/** Implements the policy that determines the termination of a source entity execution after a specified number
 * of users has been generated
 *
 * @author Daniele Gianni
 */
public class FiniteSourceTerminationPolicy extends ImplicitButNotEsplicitMaskingStateOnlyDependentPolicy<Integer, Boolean> {    
    
    /** Creates a new instance of FiniteSourceTerminationPolicy */
    public FiniteSourceTerminationPolicy(final Integer counter) {        
        super(counter);
    }
    
    public Boolean getDecision() {
    	boolean decision = (getState().compareTo(0) > 0);
        setState(getState().intValue() - 1);        
        return decision;
    }

    public Void getImplicitInput() {
        return null;
    }

    public void setImplicitInput(Void v) {
    }
}