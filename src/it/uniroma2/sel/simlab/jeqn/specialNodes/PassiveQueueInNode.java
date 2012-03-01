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

import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Defines the common structure for nodes (Allocate and Create) that regulate the access to
 * a passive queue zone
 *
 * @author Daniele Gianni
 */
public abstract class PassiveQueueInNode extends SpecialNode {

    // the name of the port for the token acknowledgements
    protected static final String TOKEN_ACKNOWLEDGE_PORT_NAME = "tokenAckPort";  
    
    // the name of the port for the token requests
    protected static final String TOKEN_REQUEST_PORT_NAME = "tokenReqPort";

    /*
     * the port for the token acknowledgements
     */
    protected InPort tokenAcknowledgePort;

    /*
     * the port for the token requests
     */
    protected OutPort tokenRequestPort;    

    /*
     * the delay introduced in a token request
     */
    protected Time tokenRequestDelay;    
    
    // statistics
    /*
     * user interarrival time
     */
    protected DiscretePopulationMean meanInterarrivalTime;

    /*
     *
     */
    protected DiscretePopulationMean meanRequestTime;
    
    /** 
     * Creates a new instance of PassiveQueueInNode   
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param userForwardDelay The delay introduced to send a processed users to the next entity.
     * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public PassiveQueueInNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay) throws InvalidNameException {        
        super(name, timeFactory, layer2factory, userForwardDelay);            
        
        setTokenRequestDelay(timeFactory.makeFrom(Time.ZERO));
        
        init();
    }
     /**
      * Creates a new instance of PassiveQueueInNode   
      * @param name Element name. The name is used to identify entities within the simulation model.
      * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
      * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
      * @param userForwardDelay The delay introduced to send a processed users to the next entity.
      * @param tokenRequestDelay The delay time introduced to process the token request.
      * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
      */
    public PassiveQueueInNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final double tokenRequestDelay) throws InvalidNameException {        
        super(name, timeFactory, layer2factory, userForwardDelay);            
        
        setTokenRequestDelay(timeFactory.makeFrom(tokenRequestDelay));
        
        init();
    }
    
    private void init() throws InvalidNameException {
        setInPort(new InPort(new JEQNName(USER_IN_PORT_NAME), this));
        setOutPort(new OutPort(new JEQNName(USER_OUT_PORT_NAME), this));                
        
        setTokenRequestPort(new OutPort(new JEQNName(TOKEN_REQUEST_PORT_NAME), this));
        setTokenAcknowledgePort(new InPort(new JEQNName(TOKEN_ACKNOWLEDGE_PORT_NAME), this)); 
        
        meanInterarrivalTime = new DiscretePopulationMean();
        meanRequestTime = new DiscretePopulationMean();
    }
    
    /**
     * Gets the port used to receive the token acknowledgements
     * @return The port to receive the  token acknowledgements 
     */
    public InPort getTokenAcknowledgePort() {
        return tokenAcknowledgePort;
    }
    
    /**
     * Gets the delay time introduced to send the token request.
     * @return The introduced delay
     */
    public Time getTokenRequestDelay() {
        return tokenRequestDelay;
    }
    
    /**
     * Gets the port to request a token to the token pool.
     * @return The port to request a token.
     */
    public OutPort getTokenRequestPort() {
        return tokenRequestPort;
    }
    
    /**
     *  Gets the delay introduced to send a processed users to the next entity.
     *  @return The introduced delay.
     */
    public Time getUserForwardDelay() {
        return userForwardDelay;
    }   
    
    /**
     * Sets the port used to receive token acknowledgements
     * @param p The port for receiving token acknowledgements
     */
    public void setTokenAcknowledgePort(InPort p) {
        tokenAcknowledgePort = p;
    }
    /**
     * Sets the delay time introduced to send the token request.
     * @param t Delay introduced for a token request.
     */
    public void setTokenRequestDelay(Time t) {
        tokenRequestDelay = t;
    }
    /**
     * Sets the delay introduced to send a processed users to the next entity.
     * @param p Delay introduced for forwarding an user
     */
    public void setTokenRequestPort(final OutPort p) {
        tokenRequestPort = p;
    }    
}
