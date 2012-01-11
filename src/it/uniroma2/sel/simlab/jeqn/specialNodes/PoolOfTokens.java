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
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.tokens.TokenFactory;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.users.UserWithTokens;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Defines the general structure and interface for PoolOfTokens (e.g. AllocateReleasePoolOfTokens)
 *
 * @author Daniele Gianni
 */
public class PoolOfTokens extends JEQNElement {

    /*
     * the name of the input port for token requests
     */
    protected final static String INPUT_REQUEST_PORT = "inRequestPort";

    /*
     * the name of the output port for acknowledging token requests
     */
    protected final static String OUTPUT_REQUEST_PORT = "outRequestPort";

    /*
     * the name of the input port for dismissing tokens
     */
    protected final static String INPUT_RELEASE_PORT = "inReleasePort";

    /*
     * the name of the output for acknowledging token dismission
     */
    protected final static String OUTPUT_RELEASE_PORT = "outReleasePort";

    /*
     * delay time introduced in the acknowledgement of a token allocate request
     */
    protected Time tokenAllocateEventAckDelay;

    /*
     * delay time introduced in the acknowledgement of a token release request
     */
    protected Time tokenReleaseEventAckDelay;

    /*
     * delay time introduced in the acknowledgement of a token create request
     */
    protected Time tokenCreateEventAckDelay;

    /*
     * delay time introduced in the acknowledgement of a token destroy request
     */
    protected Time tokenDestroyEventAckDelay;

    /*
     * the port for requesting allocation of tokens
     */
    protected InPort inTokenRequestPort;

    /*
     * the port for requesting release of tokens
     */
    protected InPort inTokenReleasePort;

    /*
     * the port for sending the acknowledgements to token allocate requests
     */
    protected OutPort outTokenRequestPort;

    /*
     * the port for sending the acknowledgements to token dismiss requests
     */
    protected OutPort outTokenReleasePort;

    /*
     * the object to generate tokens
     */
    protected TokenFactory tokenFactory;
    
    // stats
    /*
     * token request interarrival time
     */
    protected DiscretePopulationMean interarrivalTime[];

    /*
     * the array of the arrival times of the last requests
     */
    protected double lastReqTime[];
    
    /** Creates a new instance of PoolOfTokens */
    protected PoolOfTokens(final JEQNName name, final JEQNTimeFactory timeFactory, final TokenFactory tokenFactory, final Layer3ToLayer2Factory layer2factory) throws InvalidNameException {            
        super(name, timeFactory, layer2factory);
          
        setInTokenReleasePort(new InPort(new JEQNName(INPUT_RELEASE_PORT), this));
        setOutTokenReleasePort(new OutPort(new JEQNName(OUTPUT_RELEASE_PORT), this));
        
        setInTokenRequestPort(new InPort(new JEQNName(INPUT_REQUEST_PORT), this));
        setOutTokenRequestPort(new OutPort(new JEQNName(OUTPUT_REQUEST_PORT), this));
        
        setTokenFactory(tokenFactory);
        
        interarrivalTime = new DiscretePopulationMean[4];
        
        interarrivalTime[0] = new DiscretePopulationMean(); 
        interarrivalTime[1] = new DiscretePopulationMean();
        interarrivalTime[2] = new DiscretePopulationMean();
        interarrivalTime[3] = new DiscretePopulationMean();
        
        lastReqTime = new double[4];
    }
    
    public void body() throws JEQNException {        
        Event event;
        
        
        while (true) {        
            event = nextEvent();
            
            switch ((Events) event.getTag()) {
                
                case TOKEN_ALLOCATE:                        
                        interarrivalTime[0].insertNewSample(event.getTime().getValue() - lastReqTime[0]);
                        lastReqTime[0] = event.getTime().getValue();
                        
                        handleTokenAllocateEvent((User ) event.getData());
                        break;
                        
                case TOKEN_CREATE:
                        interarrivalTime[1].insertNewSample(event.getTime().getValue() - lastReqTime[1]);
                        lastReqTime[1] = event.getTime().getValue();
                        
                        handleTokenCreateEvent((User ) event.getData());
                        break;
                        
                case TOKEN_DESTROY:
                        interarrivalTime[2].insertNewSample(event.getTime().getValue() - lastReqTime[2]);
                        lastReqTime[2] = event.getTime().getValue();
                        
                        handleTokenDestroyEvent((UserWithTokens ) event.getData());
                        break;
                        
                case TOKEN_RELEASE:
                        interarrivalTime[3].insertNewSample(event.getTime().getValue() - lastReqTime[3]);
                        lastReqTime[3] = event.getTime().getValue();
                        
                        handleTokenReleaseEvent((UserWithTokens ) event.getData());
                        break;
                        
                default: throw new JEQNUnexpectedEventReceivedException(event);
            }
        }        
    }
    
    protected void handleTokenAllocateEvent(final User u) throws JEQNException {
        
    }
    
    protected void handleTokenCreateEvent(final User u) throws JEQNException {
        
    }
    
    protected void handleTokenReleaseEvent(final UserWithTokens u) throws JEQNException {
        
    }
    
    protected void handleTokenDestroyEvent(final UserWithTokens u) throws JEQNException {
        
    }    
    
    public void printStatistics() {        
        //System.out.println("### PoolOfTokens : ");
        //System.out.println("\n\n");
        
        System.out.println("Token allocate request mean interarrival time   : " + interarrivalTime[0].meanValue());
        System.out.println("Number of token allocate request                : " + interarrivalTime[0].sampleSize());
        System.out.println("Token create request mean interarrival time     : " + interarrivalTime[1].meanValue());
        System.out.println("Number of token create request                  : " + interarrivalTime[1].sampleSize());
        System.out.println("Token destroy request mean interarrival time    : " + interarrivalTime[2].meanValue());
        System.out.println("Number of token destroy request                 : " + interarrivalTime[2].sampleSize());
        System.out.println("Token release request mean interarrival time    : " + interarrivalTime[3].meanValue());
        System.out.println("Number of token release request                 : " + interarrivalTime[3].sampleSize());
    }

    public Time getTokenAllocateEventAckDelay() {
        return tokenAllocateEventAckDelay;
    }

    public void setTokenAllocateEventAckDelay(final Time t) {
        tokenAllocateEventAckDelay = t;
    }

    public Time getTokenReleaseEventAckDelay() {
        return tokenReleaseEventAckDelay;
    }

    public void setTokenReleaseEventAckDelay(final Time t) {
        tokenReleaseEventAckDelay = t;
    }

    public Time getTokenCreateEventAckDelay() {
        return tokenCreateEventAckDelay;
    }

    public void setTokenCreateEventAckDelay(final Time t) {
        tokenCreateEventAckDelay = t;
    }

    public Time getTokenDestroyEventAckDelay() {
        return tokenDestroyEventAckDelay;
    }

    public void setTokenDestroyEventAckDelay(final Time t) {
        tokenDestroyEventAckDelay = t;
    }
    
    private void setTokenFactory(final TokenFactory f) {
        tokenFactory = f;
    }

    public InPort getInTokenRequestPort() {
        return inTokenRequestPort;
    }

    public void setInTokenRequestPort(final InPort p) {
        inTokenRequestPort = p;
    }

    public InPort getInTokenReleasePort() {
        return inTokenReleasePort;
    }

    public void setInTokenReleasePort(final InPort p) {
        inTokenReleasePort = p;
    }

    public OutPort getOutTokenRequestPort() {
        return outTokenRequestPort;
    }

    public void setOutTokenRequestPort(final OutPort p) {
        outTokenRequestPort = p;
    }

    public OutPort getOutTokenReleasePort() {
        return outTokenReleasePort;
    }

    public void setOutTokenReleasePort(final OutPort p) {
        outTokenReleasePort = p;
    }
}
