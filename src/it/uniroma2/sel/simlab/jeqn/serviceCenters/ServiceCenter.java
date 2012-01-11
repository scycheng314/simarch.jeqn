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

package it.uniroma2.sel.simlab.jeqn.serviceCenters;

import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.users.User; //ResourceRequest;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Defines the common interface, method, and the general behaviour of single service centers.
 *
 * @author  Daniele Gianni
 */
public abstract class ServiceCenter extends JEQNElement {

    // self-explanatory of names of ports
    public static final String INCOMING_USERS_PORT_NAME = "incoming";
    public static final String NEXT_ENTITY_PORT_NAME = "next";
    public static final String REQUEST_USERS_PORT_NAME = "request";

    /*
     * delay introduced when sending fully processed users to the cascade entity
     */
    protected Time sendingAheadDelay;

    /*
     * delay introduce when sending back to the previous entity users partially processed
     */
    protected Time requestUserDelay;

    /*
     * port for the reception of incoming users
     */
    protected InPort incomingUsersPort;

    /*
     * port for requesting the next user to be processed
     */
    protected OutPort requestUsersPort;

    /*
     * port to forward the user to the cascade entity
     */
    protected OutPort nextEntityPort;
    
    // statistics
    /*
     * users entirely processed
     */
    protected int usersProcessed;

    /*
     * users interarrival times to the center
     */
    protected DiscretePopulationMean interarrivalTime;

    /*
     * user service times
     */
    protected DiscretePopulationMean serviceTimeMean;
    
    
    public ServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final Time sendingAheadDelay, final Time requestUserDelay) throws InvalidNameException {        
        super(name, timeFactory, factory);   
        
        setSendingAheadDelay(sendingAheadDelay);
        setRequestUserDelay(requestUserDelay);
        
        setIncomingUsersPort(new InPort(new JEQNName(INCOMING_USERS_PORT_NAME), this));
        setNextEntityPort(new OutPort(new JEQNName(NEXT_ENTITY_PORT_NAME), this));
        setRequestUsersPort(new OutPort(new JEQNName(REQUEST_USERS_PORT_NAME), this));
        
        setUsersProcessed(0);
    }        

    /*
     * defines the entity behaviour for the processing of a user
     */
    protected abstract void process(final User u) throws JEQNException;
    
    public InPort getIncomingUsersPort() {
        return incomingUsersPort;
    }
    
    public OutPort getNextEntityPort() {
        return nextEntityPort;
    }
    
    public OutPort getRequestUsersPort() {
        return requestUsersPort;
    }
    
    protected void setIncomingUsersPort(final InPort p) {
        incomingUsersPort = p;
    }
    
    protected void setNextEntityPort(final OutPort p) {
        nextEntityPort = p;
    }
    
    protected void setRequestUserDelay(final Time t) {
        requestUserDelay = t;
    }
    
    protected void setRequestUsersPort(final OutPort p) {
        requestUsersPort = p;
    }      
    
    protected void setSendingAheadDelay(final Time t) {
        sendingAheadDelay = t;
    }
    
    protected void setUsersProcessed(final int i) {
        usersProcessed = i;
    }
}
