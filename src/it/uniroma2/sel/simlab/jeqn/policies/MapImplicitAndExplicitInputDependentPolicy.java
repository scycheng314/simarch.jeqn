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

package it.uniroma2.sel.simlab.jeqn.policies;


import java.util.HashMap;
import java.util.Map;

/** Reverse the hierarchical tree of the policy classification to enable the transparent
 * deployment of all types of policies. The direct tree is defined with Policy as root interface
 * and with the types of policies adding methods and attributes to Policy. However, this creates
 * a problem when a jEQN simulation component does not require a specific type of policy as the
 * component would not be able to invokate the decision method of some types of policies.
 *
 * Masking the policies within this class eliminates this problem. Please see use in examples.
 * 
 * @author Daniele Gianni
 */
public class MapImplicitAndExplicitInputDependentPolicy<E, D> extends ImplicitAndExplicitInputDependentPolicy<Map<E, D>, E, D> {
        
    /** Creates a new instance of MapPolicy */
    public MapImplicitAndExplicitInputDependentPolicy() {
        setImplicitInput(new HashMap<E, D>());
    }
    
    public MapImplicitAndExplicitInputDependentPolicy(final Map<E, D> m) {
        super(m);
    }
    
    public void addMapping(final E key, final D value) {
        getImplicitInput().put(key, value);
    }
        
    public D getDecisionFor(final E k) {
        return getImplicitInput().get(k);
    }    
}
