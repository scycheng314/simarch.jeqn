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

import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;

/** Concatenates two decision policies. The combined policy is the result of the application
 * of the external policy to the decision resulting from the internal policy
 *
 * @author Daniele Gianni
 */
public class CombinedPolicy<E, D1, D2> extends MaskBasePolicy<Void, E, Void, D2> {
    
    private MaskBasePolicy<?, E, ?, D1> internal;
    private MaskBasePolicy<?, D1, ?, D2> external;
    
    /** Creates a new instance of CombinedPolicy */
    public CombinedPolicy(final MaskBasePolicy<?, E, ?, D1> internal, final MaskBasePolicy<?, D1, ?, D2> external) {
        super(null, null);
        
        setExternal(external);
        setInternal(internal);
    }
   
    public D2 getDecisionFor(final E e) {
        return external.getDecisionFor(internal.getDecisionFor(e));
    }

    protected void setExternal(final MaskBasePolicy<?, D1, ?, D2> p) {
        external = p;
    }

    protected void setInternal(final MaskBasePolicy<?, E, ?, D1> p) {
        internal = p;
    }    
}
