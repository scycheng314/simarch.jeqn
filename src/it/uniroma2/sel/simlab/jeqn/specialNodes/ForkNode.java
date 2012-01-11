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

/** Implements the ForkNode, ie the node that generates a set of child users that are to be synchronized
 * at a specified Join node
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
    
    /** Creates a new instance of ForkNode */
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

    public Time getInterUserDelay() {
        return interUserDelay;
    }

    public void setInterUserDelay(Time interUserDelay) {
        this.interUserDelay = interUserDelay;
    }
}
