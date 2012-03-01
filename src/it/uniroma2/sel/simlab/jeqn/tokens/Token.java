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

package it.uniroma2.sel.simlab.jeqn.tokens;

import it.uniroma2.sel.simlab.jeqn.general.JEQNName;

/** Defines the general skeleton for a class that implements a Token
 *
 * @author Daniele Gianni
 */
public class Token<T> {

    // the name of the resource the token is associated with
    private JEQNName resourceName;

    // the value identifying the token
    private T value;
    
    /**
     * Create a new Token instance.
     * @param t The value associated to the token.
     * @param resourceName The name of the resource the token is associated with
     */
    public Token(final T t, final JEQNName resourceName) {
        setResourceName(resourceName);
        setValue(t);        
    }
    
    /**
     * Gets the resource the token is associated with
     * @return Resource name
     */
    public JEQNName getResourceName() {
        return resourceName;        
    }
    
    /**
     * Gets the token value.
     * @return Token value.
     */
    public T getValue() {
        return value;
    }
    
    /**
     * Sets the name of the resource the token is associated with
     * @param s	Resource name.
     */
    public void setResourceName(final JEQNName s) {
        resourceName = s;
    }
    
    /**
     * Sets the token value.
     * @param t Token value.
     */
    public void setValue(final T t) {
        value = t;
    }
}
