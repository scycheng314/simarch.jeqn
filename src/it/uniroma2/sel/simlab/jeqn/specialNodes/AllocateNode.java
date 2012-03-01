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

package it.uniroma2.sel.simlab.jeqn.specialNodes;

import it.uniroma2.sel.simlab.jeqn.errors.JEQNConfigurationError;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Event;

import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;

/** Implements an EQN Allocate node. It is used to model resource contention. Incoming users are enqueued in a
 * passive queue untile a token is available in the associated pool of tokens.
 *
 * @author Daniele Gianni
 */
public class AllocateNode extends PassiveQueueInNode {    

    /*
     * the name of the port to which
     */
    protected static final String USER_REQUEST_PORT_NAME = "userReqPort";

    /*
     * the port to request the next user - when this is available on the passive queue
     */
    protected OutPort userRequestPort;

    /*
     * the delay time introduced in the user request process
     */
    protected Time userRequestDelay;
        
    /**
     * Creates a new  AllocateNode
     * 
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param userForwardDelay The delay introduced to send a processed users to the next entity.
     * @param tokenRequestDelay The delay time introduced to process the token request
     * @param userRequestDelay The delay time introduced to process the user request
     * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public AllocateNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final double tokenRequestDelay, final double userRequestDelay) throws InvalidNameException {        
        super(name, timeFactory, layer2factory, userForwardDelay, tokenRequestDelay);  
        
        setUserRequestDelay(timeFactory.makeFrom(userRequestDelay));
        setUserRequestPort(new OutPort(new JEQNName(USER_REQUEST_PORT_NAME), this));
    }
    
    /**
     * Contains the simulation logic of the element.
     */ 
    public void body() throws JEQNException {
        
        Event event;
        Event nextIncomingUser;
        
        double arrivalTime = 0.0;
        double requestTime = 0.0;
        
        try {            
            while (true) {

                // request the next user
                send(userRequestPort, userRequestDelay, Events.REQUESTED_NEXT_USER, new Integer(0));
                event = nextEvent();                           
                meanInterarrivalTime.insertNewSample(event.getTime().getValue() - arrivalTime);
                requestTime = arrivalTime = event.getTime().getValue();

                // requests a token
                send(tokenRequestPort, tokenRequestDelay, Events.TOKEN_ALLOCATE, event.getData());
                
                // wait for a token to be available/released in the pool of tokens
                event = nextEvent();
                meanRequestTime.insertNewSample(event.getTime().getValue() - requestTime);                                

                // forward the user to the cascade entity
                send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, event.getData());
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationError(ex);
        }
    }
    
    /**
     * Gets the user request delay.
     * @return User request delay
     */
    public Time getUserRequestDelay() {
        return userRequestDelay;
    }
    
    /**
     * Gets the port to request the next user. Must be available on the associated passive queue used to enqueue waiting users.
     * @return The user request port.
     */
    public OutPort getUserRequestPort() {
        return userRequestPort;
    }
    
    /**
     * 
     */
    public void printStatistics() {     
        System.out.println("### Allocate Node " + getEntityName() + " : ");
        System.out.println("\n\n");
        
        System.out.println("Mean user interarrival time             : " + meanInterarrivalTime.meanValue());
        System.out.println("Numer of user arrived                   : " + meanInterarrivalTime.sampleSize());
        System.out.println("Mean request time                       : " + meanRequestTime.meanValue());
        System.out.println("Number of token request satisfied       : " + meanRequestTime.sampleSize());
    }
    
    public void setUserRequestDelay(final Time t) {
        userRequestDelay = t;
    }
    
    public void setUserRequestPort(final OutPort p) {
        userRequestPort = p;
    }
}
