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
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.users.DadUser;
import it.uniroma2.sel.simlab.jeqn.users.SonUser;
import it.uniroma2.sel.simlab.jeqn.users.SonUserGenerator;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.errors.ConfigurationError;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import java.util.ArrayList;

/** Implements the EQN ForkNode, that is the node that generates a set of child users that are to be synchronized
 * at a specified {@code Join} node. A specified policy it is used to detemine the number of child users generate, but it should be noted that
 * for each incoming user the following users are routed to the outport:
 * - a {@code dadUser}, that wraps the incoming user
 * - n {@code childUser}, where n accords to the policy decision.
 * 
 * Finally, it should be noted that the Fork node does not implement any routing policy: outgoing users are forwarded to the same output port. 
 * If needed, a Router node must be placed as cascade entity. 
 * 
 * @see it.uniroma2.sel.simlab.jeqn.specialNodes.JoinNode
 * @see it.uniroma2.sel.simlab.jeqn.users.DadUser
 * @see it.uniroma2.sel.simlab.jeqn.users.SonUser
 *
 * @author Daniele Gianni
 */
public class ForkNode extends SpecialNode {

    /*
     * the object determining on which data the fork policy must be applied
     */
//    protected DecisionDataFactory decisionDataFactory;

    /*
     * the policy determining how many child users have to be generated for each incoming user
     */
    protected MaskBasePolicy<?, User, ?, Integer> forkPolicy;
    //protected MaskBasePolicy<?, ?, DecisionData, Integer> forkPolicy;

    /*
     * the child user generator
     */
    protected SonUserGenerator sonUserGenerator;

    /*
     * the delay time in the forwarding of the child users to the cascade entity
     */
    private Time interUserDelay;
    
    /** 
     * Creates a new ForkNode
     * 
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param userForwardDelay The delay introduced to send a processed users to the next entity.
     * @param forkPolicy policy Policy that determines how many child users have to be generated for each incoming user
     * @param sonUserGenerator Generator of child users.
     * @param interUserDelay The delay time introduced to forward outgoing users to the cascade entity
     * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public ForkNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final MaskBasePolicy<?, User, ?, Integer> forkPolicy /* final MaskBasePolicy<?, ?, DecisionData, Integer> forkPolicy, final DecisionDataFactory decisionDataFactory*/,  final SonUserGenerator sonUserGenerator, final Time interUserDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);
        
        setForkPolicy(forkPolicy);
        setSonUserGenerator(sonUserGenerator);
        setInterUserDelay(interUserDelay);
    }
    
    /** Creates a new instance of ForkNode */
    public ForkNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final MaskBasePolicy<?, User, ?, Integer> forkPolicy /*final MaskBasePolicy<?, ?, DecisionData, Integer> forkPolicy, final DecisionDataFactory decisionDataFactory */, final SonUserGenerator sonUserGenerator, final double interUserDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);
        
        setForkPolicy(forkPolicy);
        setSonUserGenerator(sonUserGenerator);
        setInterUserDelay(timeFactory.makeFrom(interUserDelay));
    }
    
    /**
     * Contains the simulation logic of the element.
     */ 
    public void body() throws JEQNException {
     
        Event event;
        try {
            
            while (true) {            
                event = nextEvent();
                
                if (event.getTag().equals(Events.NEW_INCOMING_USER)) {                    
                                    
                    int numberOfSons = forkPolicy.getDecisionFor((User) event.getData()).intValue();

                    // wrap incoming user in a dad user
                    DadUser dadUser = DadUser.wrap((User) event.getData());

                    // generate son users
                    ArrayList<SonUser> sonList = sonUserGenerator.generateSonsFor(dadUser, numberOfSons);

                    // forward both dad and son users to the cascade entity
                    send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, dadUser);                                    
                    for (SonUser s : sonList) {
                        send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, s);
                    }                                                  
                } else throw new JEQNUnexpectedEventReceivedException(event);
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new ConfigurationError(ex);
        }        
    }        
    
    public void printStatistics() {
        
    }
        
    private void setForkPolicy(final MaskBasePolicy<?, User, ?, Integer> p) {
        forkPolicy = p;
    }
    
    private void setSonUserGenerator(final SonUserGenerator g) {
        sonUserGenerator = g;
    }
    /**
     * Gets the delay time introduced to forward outgoing users to the cascade entity
     * @return The delay time of forwarded users.
     */
    public Time getInterUserDelay() {
        return interUserDelay;
    }
    /**
     * Sets the delay time introduced to forward outgoing users to the cascade entity
     * @param interUserDelay a {@code Time} object that wraps the specified delay
     */
    public void setInterUserDelay(Time interUserDelay) {
        this.interUserDelay = interUserDelay;
    }
}
