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

package it.uniroma2.sel.simlab.jeqn.requests;

import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Time;

/** 
 * A @code{ServiceRequest} object wraps a {@code Time} object, which represents the amount of time that an user requests to the next service center
 * 
 * @see it.uniroma2.sel.simlab.simarch.data.Time	
 * @author  Daniele Gianni
 */
public class ServiceRequest {
    
    private Time value;
    
    /**
     * Creates a new ServiceRequest with {@code 0} as its service request.
     */
    public ServiceRequest() {
        setValue(JEQNTimeFactory.makeFrom(Time.ZERO));
    } 
    
    /**
     * Creates a new ServiceRequest with the specified service request value.
     * 
     * @param	t	amount of time that an user requests to the next service center
     */
    public ServiceRequest(final Time t) {
        setValue(t);
    }
    
    /**
     *	Gets the service request value.
     *	 
     * @return	{@code Time} object that contains the service request value.
     */
    public Time getValue() {
        return value;
    }
    
    /**
     * Sets the service request value.
     * @param t	{@code Time} object that contains the service request value.
     */
    public void setValue(final Time t) {
        value = t;
    }
    
    /**
     * Returns a string that shows the service request value.
     */
    public String toString() {
        return "ServiceRequest " + value;
    }
}
