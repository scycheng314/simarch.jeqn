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

import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

/** Implements a EQN Destroy node, that is the entity requesting the destruction of a token to a CreateDestroyPoolOfTokens
 *
 * @author Daniele Gianni
 */
public class DestroyNode extends PassiveQueueOutNode {
    
    /** 
     * Creates a new instance of DestroyNode
     *     
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param userForwardDelay The delay introduced to send a processed users to the next entity. 
     * @param tokenDismissDelay The delay introduced to dismiss a token.
     * @throws InvalidNameException InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public DestroyNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final double tokenDismissDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay, tokenDismissDelay);
    }
    
    /**
     * Gets the code that represents the event "token destroy request"
     */
    protected Events getServiceRequestCode() {
        return Events.TOKEN_DESTROY;
    }
    
    /**
     * Gets the code that represents the event "token destory acknowledge"
     */
    protected Events getServiceAcknowledgeCode() {
        return Events.TOKEN_DESTROYED;
    }        
}
