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

package it.uniroma2.sel.simlab.jeqn.routers.policies.routing;

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitButNotExplicitInputOnlyDependentPolicy;
import it.uniroma2.sel.simlab.jrand.objectStreams.numericStreams.IntegerStream;

/** Implements a routing policy that relies on an internal sequence of number to determine the output port.
 * This class might be useful in testing environments, in which the routing might be constrained to reproduce
 * particular scenarios.
 *
 * @author Daniele Gianni
 */
public class NumericSequenceBasedRoutingPolicy extends ImplicitButNotExplicitInputOnlyDependentPolicy<IntegerStream, Integer> {
    
    /** 
     * Creates a new NumericSequenceBasedRoutingPolicy
     * @param implicitInput	numeric stream used to determine the policy decision and, ultimately, the output port.
     */
    public NumericSequenceBasedRoutingPolicy(final IntegerStream implicitInput) {
        super(implicitInput);
    }
    
    /**
     * Returns the {@code Integer} object that constitutes the policy decision, which wraps the output port index.
     * @return The {@code Integer} object that wraps the output port index.
     */
    public Integer getDecision() {
        return implicitInput.getNext();
    }    
}
