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

import it.uniroma2.sel.simlab.simarch.data.Name;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;

/** 
 * Creates names used in the jEQN model. Names can be used to identify local and remote entities and ports.
 *
 * @author Daniele Gianni
 */
public class JEQNName extends Name {

    // string separator in jEQN names
    public static final String TOKEN = ".";
    
    /** 
     * Creates a new JEQNName with the specified name
     *
     * @param	s	name to be wrapped in a JEQNName object.
     * @throws	InvalidNameException	An InvalidNameException is raised when an issue concerning the element name occurs.
     * */
    public JEQNName(final String s) throws InvalidNameException {
        super(s);
    }
    
    /** 
     * Sets the name of this object to be equal to the argument name.
     *
     * @param	s	the new name to be wrapped in a JEQNName object.
     * @throws	InvalidNameException	An InvalidNameException is raised if the name format is not allowed. This version throws an exception only if the name parameter contains a '.' character.
     * */
    public void setValue(final String s) throws InvalidNameException {
        
        if (s.contains(TOKEN)) throw new InvalidNameException("TOKEN " + TOKEN + " is not allowed in jEQN Names");
        
        value = s;
    }    
    
    /**
     * Returns a string constituted by the wrapped name.
     * 
     * @return the name wrapped by the JEQNName object.
     */
    
    public String toString() {
        return value;
    }
}
