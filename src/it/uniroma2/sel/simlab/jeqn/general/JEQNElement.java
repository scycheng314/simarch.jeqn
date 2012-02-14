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

package it.uniroma2.sel.simlab.jeqn.general;

import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.entities.BasicComponentLevelEntity;

/** Defines the basic jEQN element by extending the BasicCOmponentLevelEntity (simcomps)
 * and by introducing the factory for jEQN time.
 *
 * @author Daniele Gianni
 */
public abstract class JEQNElement extends BasicComponentLevelEntity<User, Events> { //<User, Events> {
    
    protected JEQNTimeFactory timeFactory;
    
    /** Create a new JEQNElement.
     *
     * @param	name	Element name. The name is used to identify entities within the simulation model.
     * @param	timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param	factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @throws	InvalidNameException	An InvalidNameException is raised when an issue concerning the element name occurs. 
     */
    
    public JEQNElement(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory) throws InvalidNameException {        
        super(name, factory);
        
        setTimeFactory(timeFactory);
    }   
    
    protected void setTimeFactory(final JEQNTimeFactory f) {
        timeFactory = f;
    }
}
