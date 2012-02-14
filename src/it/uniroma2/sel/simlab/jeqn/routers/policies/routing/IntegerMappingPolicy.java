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

import it.uniroma2.sel.simlab.jeqn.policies.MapImplicitAndExplicitInputDependentPolicy;

import java.util.Map;

/** Defines a numeric-based routing policy, according to which the output port depends on an integer parameter. Specifically, this class is defined as specialization of a mapping policy
 *
 * @author Daniele Gianni
 */
public class IntegerMappingPolicy extends MapImplicitAndExplicitInputDependentPolicy<Integer, Integer>{
    
    /**
     * Creates a new IntegerMappingPolicy with the specified mapping between integer parameters and output port indexes.
     * @param m {@code Map} object that associate an {@code Integer} object, which wraps the output port index, to an {@code Integer} object which represents the policy parameter.
     */
    public IntegerMappingPolicy(final Map<Integer, Integer> m) {
        super(m);
    }    
}
