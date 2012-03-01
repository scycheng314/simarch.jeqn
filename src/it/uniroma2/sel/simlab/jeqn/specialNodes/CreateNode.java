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
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

/** Implements a EQN Create Node, that is the entity that requests to a CreateDestroyPoolOfTokens
 * the creation of a token
 *
 * @author Daniele Gianni
 */
public class CreateNode extends PassiveQueueInNode {        
        
    /** 
     * Creates a new instance of CreateNode  
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2. 
     * @param tokenRequestDelay The delay time introduced to process the token request
     * @param userForwardDelay The delay introduced to send a processed users to the next entity.
     * @throws InvalidNameException InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public CreateNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double tokenRequestDelay, final double userForwardDelay) throws InvalidNameException {        
        super(name, timeFactory, layer2factory, userForwardDelay, tokenRequestDelay);         
    }
    
    /**
     * Contains the simulation logic of the element.
     */
    public void body() throws JEQNException {
        
        Event event;
        
        double lastUserTime = 0.0;
        double lastRequestTime = 0.0;
        
        while (true) {            
            event = nextEvent();
            
            switch ((Events) event.getTag()) {

                // incoming user
                case NEW_INCOMING_USER:
                    try {
                        meanInterarrivalTime.insertNewSample(event.getTime().getValue() - lastUserTime);
                        lastUserTime = event.getTime().getValue();
                        // request a token creation
                        send(tokenRequestPort, tokenRequestDelay, Events.TOKEN_CREATE, event.getData());                    
                    } catch (TimeAlreadyPassedException ex) {
                        ex.printStackTrace();
                        throw new JEQNTimeException(ex);
                    } catch (UnlinkedPortException ex) {
                        ex.printStackTrace();
                        throw new JEQNConfigurationError(ex);
                    }
                    break;

                    // token created
                case TOKEN_CREATED:
                    try {
                        meanRequestTime.insertNewSample(event.getTime().getValue() - lastRequestTime);
                        lastRequestTime = event.getTime().getValue();
                        // forward the user to the cascade entity
                        send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, event.getData());
                    } catch (TimeAlreadyPassedException ex) {
                        ex.printStackTrace();
                        throw new JEQNTimeException(ex);
                    } catch (UnlinkedPortException ex) {
                        ex.printStackTrace();
                        throw new JEQNConfigurationError(ex);
                    }
                    break;
                    
                default: throw new JEQNUnexpectedEventReceivedException();
            }            
        }
    }
    
    public void printStatistics() {     
        System.out.println("### Create Node " + getEntityName() + " : ");
        System.out.println("\n\n");
                
        System.out.println("Mean user interarrival time         : " + meanInterarrivalTime.meanValue());
        System.out.println("Number of user arrived              : " + meanInterarrivalTime.sampleSize());
        System.out.println("Mean time between token             : " + meanRequestTime.meanValue());
        System.out.println("Number of token created (users passed through : " + meanRequestTime.sampleSize());
        
        System.out.println("\n\n");
    }
}
