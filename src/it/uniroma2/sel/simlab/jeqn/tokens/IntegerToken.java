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

/** Defines a token identified by an integer value
 *
 * @author Daniele Gianni
 */
public class IntegerToken extends Token<Integer> {
    
    public static final Integer DEFAULT = new Integer(0);
    
    private Integer value;

    /** Creates a new instance of IntegerToken */
    public IntegerToken(final JEQNName resourceName) {        
        super(DEFAULT, resourceName);        
    }
    
    public IntegerToken(final Integer i, final JEQNName resourceName) {
        super(DEFAULT, resourceName);   
    }   
}
