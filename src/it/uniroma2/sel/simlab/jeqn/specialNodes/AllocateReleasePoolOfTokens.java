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
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.tokens.Token;
import it.uniroma2.sel.simlab.jeqn.tokens.TokenFactory;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.users.UserWithTokens;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/** Implements a Pool of Token for couples of Allocate - Release Nodes
 *
 * @author Daniele Gianni
 */
public class AllocateReleasePoolOfTokens extends PoolOfTokens {

    /*
     * the list of users that have requested a token, and that have not been assigned one yet
     */
    protected List<User> tokenRequests;

    /*
     * the list of available tokens
     */
    protected List<Token> tokenList;

    /*
     * the record of the tokens assigned to the users
     */
    protected Map<Token, User> tokenAssignation;       
    
    /** 
     * Creates a new instance of AllocateReleasePoolOfTokens
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param tokenFactory Factory that generate the tokens
     * @param tokenAllocateEventAckDelay The delay time introduced to send a token. It constitutes the response to the token request submitted by the Allocate node.
     * @param tokenReleaseEventAckDelay The delay time introduced to get back a token. It constitutes the response to the token release request submitted by the Release node.
     * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public AllocateReleasePoolOfTokens(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final TokenFactory tokenFactory, final double tokenAllocateEventAckDelay, final double tokenReleaseEventAckDelay) throws InvalidNameException {            
        super(name, timeFactory, tokenFactory, layer2factory);
        
        tokenRequests = new ArrayList<User>();
        tokenAssignation = new Hashtable<Token, User>();
        
        setTokenAllocateEventAckDelay(timeFactory.makeFrom(tokenAllocateEventAckDelay));
        setTokenReleaseEventAckDelay(timeFactory.makeFrom(tokenReleaseEventAckDelay));

        initTokenList();
    }    
    
    protected void initTokenList() {        
        tokenList = new ArrayList<Token>();
        
        while (tokenFactory.stillHasAToken()) {         
            tokenList.add(tokenFactory.build());
        }          
    }

    /*
     * Handle the event requesting the allocation of a token for User u
     */
    protected void handleTokenAllocateEvent(final User u) throws JEQNException {
        if (tokenList.isEmpty()) {
            // no tokens available
            tokenRequests.add(u);
        } else {
            // at least one token available
            try {
                // assigning token and enhancing user type
                UserWithTokens ut = UserWithTokens.wrap(u, tokenList.remove(0));
                // sending user with token back to the requesting entity
                send(outTokenRequestPort, tokenAllocateEventAckDelay, Events.TOKEN_ALLOCATED, ut);
            } catch (TimeAlreadyPassedException ex) {
                ex.printStackTrace();
                throw new JEQNTimeException(ex);
            } catch (UnlinkedPortException ex) {
                ex.printStackTrace();
                throw new JEQNConfigurationException(ex);
            }
        } 
    }    

    /*
     * handle the event requesting a release of a token
     */
    protected void handleTokenReleaseEvent(final UserWithTokens ut) throws JEQNException {
        if (tokenRequests.isEmpty()) {
            // no user waiting for a token
            tokenList.add(ut.removeToken());
            try {
                // acknowledge token release
                send(outTokenReleasePort, tokenReleaseEventAckDelay, Events.TOKEN_RELEASED, ut.unWrap());
            } catch (TimeAlreadyPassedException ex) {
                ex.printStackTrace();
                throw new JEQNTimeException(ex);
            } catch (UnlinkedPortException ex) {
                ex.printStackTrace();
                throw new JEQNConfigurationException(ex);
            }
        } else {
            // at least one user waiting for a token
            // allocate token for user
            User u = tokenRequests.get(0);
            UserWithTokens uut = UserWithTokens.wrap(u, ut.removeToken());
            try {
                // acknowledge token allocation
                send(outTokenRequestPort, tokenAllocateEventAckDelay, Events.TOKEN_ALLOCATED, uut);
                // acknowledge token release
                send(outTokenReleasePort, tokenReleaseEventAckDelay, Events.TOKEN_RELEASED, ut.unWrap());
            } catch (TimeAlreadyPassedException ex) {
                ex.printStackTrace();
                throw new JEQNTimeException(ex);
            } catch (UnlinkedPortException ex) {
                ex.printStackTrace();
                throw new JEQNConfigurationException(ex);
            }
        }
    }
}
