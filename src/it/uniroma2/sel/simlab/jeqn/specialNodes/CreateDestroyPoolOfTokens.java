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
import java.util.Map;

/** Implements a pool of tokens for couples of Create - Release Nodes
 *
 * @author Daniele Gianni
 */
public class CreateDestroyPoolOfTokens extends PoolOfTokens {

    /*
     * the record of the tokens assigned to the users
     */
    protected Map<Token, User> tokenAssignation;           
    
    /** 
     * Creates a new AllocateReleasePoolOfTokens
     * 
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param tokenFactory Factory that generate the tokens
     * @param tokenCreateEventAckDelay The delay time introduced to create a token
     * @param tokenDestroyEventAckDelay The delay time introduced to destroy a token
     * @throws InvalidNameException An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public CreateDestroyPoolOfTokens(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final TokenFactory tokenFactory, final double tokenCreateEventAckDelay, final double tokenDestroyEventAckDelay) throws InvalidNameException {            
        super(name, timeFactory, tokenFactory, layer2factory);        
        
        setTokenCreateEventAckDelay(timeFactory.makeFrom(tokenCreateEventAckDelay));
        setTokenDestroyEventAckDelay(timeFactory.makeFrom(tokenDestroyEventAckDelay));
    }    

    /*
     * handle the event requesting the creation of a token
     */
    protected void handleTokenCreateEvent(final User u) throws JEQNException {        
        try {
            // create enhanced user
            UserWithTokens ut = UserWithTokens.wrap(u, tokenFactory.build((JEQNName) getEntityName()));
            // acknowledge the token creation
            send(outTokenRequestPort, tokenCreateEventAckDelay, Events.TOKEN_CREATED, ut);
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationException(ex);
        }
    }  

    /*
     * handles the event requesting the destruction of a token
     */
    protected void handleTokenDestroyEvent(final UserWithTokens ut) throws JEQNException {        
        try {
            // acknowledge the destruction of a token
            send(outTokenReleasePort, tokenDestroyEventAckDelay, Events.TOKEN_DESTROYED, ut.unWrap());
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationException(ex);
        }
    }    
}