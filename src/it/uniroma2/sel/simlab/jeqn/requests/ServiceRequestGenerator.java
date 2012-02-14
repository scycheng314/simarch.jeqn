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

import it.uniroma2.sel.simlab.jeqn.users.User;
        
/** Defines the abstract class which specifies the interface of the service requests generator
 *
 * @author  Daniele Gianni
 */
public abstract class ServiceRequestGenerator {
	
	/**
	 * Creates a new ServiceRequestGenerator
	 */
    public ServiceRequestGenerator() {
    }

    /**
     * Gets the next service request for the specified user.
     * @param u	user for which the service request is to be retrieved.
     * @return	@{ServiceRequest} object that wraps the service request.
     */
    public abstract ServiceRequest getNext(final User u);

    /**
     * Assignes the internally generated request to User u
     * @param u user for which the service request is to be assigned
     */
    public void assignResourceRequest(User u) {
        u.setServiceRequest(getNext(u));
    }
}
