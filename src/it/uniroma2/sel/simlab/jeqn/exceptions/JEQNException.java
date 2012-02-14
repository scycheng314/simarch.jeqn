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

package it.uniroma2.sel.simlab.jeqn.exceptions;

import it.uniroma2.sel.simlab.simarch.exceptions.layer3.Layer3Exception;

/** 
 * Indicates that a general issue at layer 3 has been detected
*
* @author Daniele Gianni
*/
public class JEQNException extends Layer3Exception {
    
    /** Creates a new JEQNException with {@code null} as its detail message */
    public JEQNException() {
        super();
    }
    
    /**
     * Create a new JEQNException wrapping an existing exception.
     * The existing exception will be embedded in the new one, and its message will become the default message.
     * 
     * @param	e	The exception to be wrapped in a JEQNQueueOverflowException
     * */
    public JEQNException(final Exception e) {
        super(e);
    }
    
    /**
     * Creates a new JEQNError with the specified  detail message
     * 
     * @param	s	error message
     * */
    public JEQNException(final String s) {
        super(s);
    }
}
