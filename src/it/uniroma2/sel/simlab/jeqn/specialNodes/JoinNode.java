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
import it.uniroma2.sel.simlab.jeqn.users.DadUser;
import it.uniroma2.sel.simlab.jeqn.users.SonUser;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import java.util.HashMap;
import java.util.Map;

/** Implements the Join Node simulation entity. This entity synchronizes the incoming dad and son users.
 * Specifically, it reduces the dad user to a normal user upon reception of the dad user and of all the
 * son users.
 *
 * @author Daniele Gianni
 */
public class JoinNode extends SpecialNode {

    // counter of son users received for each received dad user
    private Map<DadUser, Integer> sonCounter;

    // record of the dad users received - the map value is true if a dad user is received (indipendently
    // from the son users received. The map value is false is son users are received an no dad is received
    private Map<DadUser, Boolean> dadsReceived;

    // temp field!
    private int deliveredUsers = 0;
    
    /** Creates a new instance of JoinNode */
    public JoinNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);
        
        setSonCounter(new HashMap<DadUser, Integer>());
        setDadsReceived(new HashMap<DadUser, Boolean>());
    }
    
    public void body() throws JEQNException {
        
        Event event;
        
        try {
            while (true) {
                
                event = nextEvent();
                
                if (event.getTag().equals(Events.NEW_INCOMING_USER)) {
                    // incoming user

                    User user = (User) event.getData();                    
                    //System.out.println("User name : " + user.getName()); // + "      System clock : " + getClock().getValue());
                    
                    if (isADadUser(user)) { // dad user received
                        assert dadsReceived.containsKey(user) : "Inconsistent state in Join Node: just-got-in DadUser already in DadsReceived list";
                        
                        DadUser dad = (DadUser) user;
                        dadsReceived.put(dad, new Boolean(true));                        
                                                
                        if (dad.getNumberOfSons() == 0) {                            
                            //This dad has no sons
                            send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, dad.unwrap());                              
                            dadsReceived.remove(dad);
                            deliveredUsers++;                            
                        } else {                            
                            if (!sonCounter.containsKey(dad)) { // no sons arrived yet
                                sonCounter.put(dad, 0);
                                //Dad received! But no sons yet                                                              
                                
                            } else { // at least one son arrived
                                int sonsAlreadyArrived = sonCounter.get(dad);
                                
                                assert (sonsAlreadyArrived <= dad.getNumberOfSons()) : "Inconsistent State in JoinNode : number of received sons exceeds the one expected from dad's number of sons field";
                                                                
                                if (sonsAlreadyArrived == dad.getNumberOfSons()) {
                                    dadsReceived.remove(dad);
                                    sonCounter.remove(dad);
                                    
                                    send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, dad.unwrap());                                    
                                    
                                    deliveredUsers++;
                                }
                            }
                        }
                    } else { // son user received                        
                        assert isASonUser(user) : "Unexpected User Class received at JoinNode: neither DadUser nor SonUser";
                        
                        DadUser dad = ((SonUser) user).getDad();
                        
                        if (sonCounter.containsKey(dad)) { // at least on sibling arrived
                            //One sibling arrived earlier that dad user
                            int counter = sonCounter.remove(dad).intValue() + 1;                            
                            sonCounter.put(dad, counter);
                            
                            assert (counter <= dad.getNumberOfSons()) : "Inconsisten State in JoinNode: unarrived dad has more sons than expected";
                                                        
                            if (dadsReceived.containsKey(dad)) { // dad arrived                                                                                                
                                if (dad.getNumberOfSons() == counter) { // all sibling arrived
                                    dadsReceived.remove(dad);
                                    sonCounter.remove(dad);                                                                        
                                    //Son received!!!! All siblings received too!!!! ---> Sending it ahead!!!");
                                    
                                    deliveredUsers++;
                                    send(outPort, getUserForwardDelay(), Events.NEW_INCOMING_USER, dad.unwrap());
                                    
                                } else { // missing at least one sibling AND dad arrived
                                    //System.out.println("Son received!!!! Dad arrived but some sibling are still missing");                                    
                                    //sonCounter.put(dad, counter);
                                }
                            } else { // dad not arrived yet
                                //sonCounter.put(dad, counter);                                
                                //System.out.println("Son received but not yet its dad!!!");
                            }
                        } else { // first son of the family                            
                            //System.out.println("First son of the family received!!!");                            
                            sonCounter.put(dad, 1);
                        }
                    }
                } else throw new JEQNUnexpectedEventReceivedException(event);
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationError(ex);
        }
    }
    
    // VERY SPECIFIC IMPLEMENTATION DOES NOT WORK WITH DadUser SUBCLASSES!!!!!
    protected boolean isADadUser(final User u) {
        return u.getClass().getName().contains("DadUser");
    }
    
    protected boolean isASonUser(final User u) {
        return u.getClass().getName().contains("SonUser");
    }
    
    public void printStatistics() {
        //System.out.println("No stats available for Join Node");
        
        System.out.println("Delivered : " + this.deliveredUsers);
    }
    
    private void setSonCounter(Map<DadUser, Integer> m) {
        sonCounter = m;
    }
    
    private void setDadsReceived(Map<DadUser, Boolean> m) {
        dadsReceived = m;
    }
}

