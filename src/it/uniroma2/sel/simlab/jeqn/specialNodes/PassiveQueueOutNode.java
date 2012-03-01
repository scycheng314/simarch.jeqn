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
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNConfigurationException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Defines the common structure for nodes (Release and Destroy) that delimits
 * a passive queue zone
 *
 * @author Daniele Gianni
 */
public abstract class PassiveQueueOutNode extends SpecialNode {

    // name of the port for the token acknowledgements
    protected static final String TOKEN_ACKNOWLEDGE_PORT_NAME = "tokenAckPort";

    // name of the port for the token dismission
    protected static final String TOKEN_DISMISS_PORT_NAME = "tokenDisPort";       

    /*
     * the input port for the acknowledgements
     */
    protected InPort tokenAcknowledgePort;

    /*
     * the output port for the token dismissions
     */
    protected OutPort tokenDismissPort;    

    /*
     * delay time introduced in the dismission process
     */
    protected Time tokenDismissDelay;   
    
    // stats
    // user interarrival time
    private DiscretePopulationMean meanInterarrivalTime;

    // dismission time
    private DiscretePopulationMean meanDismissingTime;
    
    /** Creates a new instance of PassiveQueueInNode
     * 
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param userForwardDelay The delay introduced to send a processed users to the next entity.
     * @param tokenDismissDelay The delay time introduced to dismiss a token
     * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public PassiveQueueOutNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final double tokenDismissDelay) throws InvalidNameException {        
        super(name, timeFactory, layer2factory, userForwardDelay); 
        
        setTokenDismissDelay(timeFactory.makeFrom(tokenDismissDelay));
        
        init();        
    }
    
    private void init() throws InvalidNameException {
        setInPort(new InPort(new JEQNName(USER_IN_PORT_NAME), this));
        setOutPort(new OutPort(new JEQNName(USER_OUT_PORT_NAME), this));                
        
        setTokenDismissPort(new OutPort(new JEQNName(TOKEN_DISMISS_PORT_NAME), this));
        setTokenAcknowledgePort(new InPort(new JEQNName(TOKEN_ACKNOWLEDGE_PORT_NAME), this));
        
        meanInterarrivalTime = new DiscretePopulationMean();
        meanDismissingTime = new DiscretePopulationMean();
    }
    
    /**
     * Contains the simulation logic of the element.
     */ 
    public void body() throws JEQNException {
        
        Event event;
        
        double lastUserTime = 0.0;
        double lastTokenTime = 0.0;
        
        try {
            while (true) {
                event = nextEvent();
                
                if (event.getTag().equals(Events.NEW_INCOMING_USER)) {
                    meanInterarrivalTime.insertNewSample(event.getTime().getValue() - lastUserTime);
                    lastUserTime = event.getTime().getValue();

                    // request a token dismission
                    send(tokenDismissPort, tokenDismissDelay, getServiceRequestCode(), event.getData());
                } else if (event.getTag().equals(getServiceAcknowledgeCode())) {
                    // token dismission acknowledgement
                    meanDismissingTime.insertNewSample(event.getTime().getValue() - lastTokenTime);
                    lastTokenTime = event.getTime().getValue();

                    // forward user to the cascade entity
                    send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, event.getData());
                } else {
                    throw new JEQNUnexpectedEventReceivedException(event);
                }
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationException(ex);
        }
    }
    
    protected abstract Events getServiceRequestCode();
    
    protected abstract Events getServiceAcknowledgeCode();
        
    public void printStatistics() {
        System.out.println("Mean user interarrival time           : " + meanInterarrivalTime.meanValue());
        System.out.println("Number of users passed through        : " + meanInterarrivalTime.sampleSize());
        System.out.println("Mean token dismissing time            : " + meanDismissingTime.meanValue());
        System.out.println("Number of dismissed token             : " + meanDismissingTime.sampleSize());
    }
    
    /**
     * Gets the port used to receive the token acknowledgments
     * @return The port to receive the  token acknowledgments 
     */
    public InPort getTokenAcknowledgePort() {
        return tokenAcknowledgePort;
    }
    
    /**
     * Gets the delay time introduced to dismiss a token
     * @return The dismissal delay.
     */
    public Time getTokenDismissDelay() {
        return tokenDismissDelay;
    }
    
    /**
     * Gets the port used to send the token dismissal requests
     * @return The port for token dismissal requests 
     */
    public OutPort getTokenDismissPort() {
        return tokenDismissPort;
    }
    
    /**
     *  Gets the delay introduced to send a processed users to the next entity.
     *  @return The introduced delay.
     */
    public Time getUserForwardDelay() {
        return userForwardDelay;
    }   
    
    /**
     * Sets the port used to receive token acknowledgments
     * @param p The port for receiving token acknowledgments
     */
    public void setTokenAcknowledgePort(final InPort p) {
        tokenAcknowledgePort = p;
    }
    
    /**
     * Sets the delay introduced to send a token dismiss request.
     * @param t The delay to send a token dismiss request.
     */
    public void setTokenDismissDelay(final Time t) {
        tokenDismissDelay = t;
    }
    
    /**
     * Sets the port used to send the token dismissal requests
     * @param p The port for token dismissal requests
     */
    public void setTokenDismissPort(final OutPort p) {
        tokenDismissPort = p;
    }    
}
