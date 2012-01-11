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

/** Defines the common structure for nodes (Allocate and Create) that regulare the access to
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
    
    /** Creates a new instance of PassiveQueueInNode */   
    public PassiveQueueInNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay) throws InvalidNameException {        
        super(name, timeFactory, layer2factory, userForwardDelay);            
        
        setTokenRequestDelay(timeFactory.makeFrom(Time.ZERO));
        
        init();
    }
    
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
    
    public InPort getTokenAcknowledgePort() {
        return tokenAcknowledgePort;
    }
    
    public Time getTokenRequestDelay() {
        return tokenRequestDelay;
    }
    
    public OutPort getTokenRequestPort() {
        return tokenRequestPort;
    }
    
    public Time getUserForwardDelay() {
        return userForwardDelay;
    }   
    
    public void setTokenAcknowledgePort(InPort p) {
        tokenAcknowledgePort = p;
    }
    
    public void setTokenRequestDelay(Time t) {
        tokenRequestDelay = t;
    }
    
    public void setTokenRequestPort(final OutPort p) {
        tokenRequestPort = p;
    }    
}
