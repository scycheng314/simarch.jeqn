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

package it.uniroma2.sel.simlab.jeqn.policies.masks;

import it.uniroma2.sel.simlab.jeqn.policies.ImplicitAndExplicitInputDependentPolicy;

/** Enables the mapping of policies that depend on implicit and explicit input
 *
 * @author Daniele Gianni
 */
public class MaskImplicitAndExplicitInputDependentPolicy<I, E, D> extends MaskBasePolicy<I, E, Void, D> {
    
    protected ImplicitAndExplicitInputDependentPolicy<I, E, D> policy;
    
    /** Creates a new instance of MaskImplicitAndExplicitInputPolicy */
    public MaskImplicitAndExplicitInputDependentPolicy(final ImplicitAndExplicitInputDependentPolicy<I, E, D> policy) {
        super(policy.getImplicitInput(), null);
        setPolicy(policy);
    }
    
    protected void setPolicy(final ImplicitAndExplicitInputDependentPolicy<I, E, D> p) {
        policy = p;
    }

    public D getDecisionFor(final E e) {
        return policy.getDecisionFor(e);
    }
}
