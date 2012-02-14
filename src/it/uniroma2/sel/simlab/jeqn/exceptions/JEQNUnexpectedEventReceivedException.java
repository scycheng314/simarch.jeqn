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

import it.uniroma2.sel.simlab.simarch.data.Event;

/** Indicates that a jEQN simulation entity has received an unexpected event.
 *
 * @author Daniele Gianni
 */
public class JEQNUnexpectedEventReceivedException extends JEQNException {
    
    protected Event event;
    
    /**
     * Creates a new JEQNUnexpectedEventReceivedException with {@code null} as its detail message
     */
    public JEQNUnexpectedEventReceivedException() {
    }
    
    /**
     * Creates a new of JEQNException raised by an unexpected event.
     * 
     * @param	e	Unexpected event that causes the exception
     * */
    public JEQNUnexpectedEventReceivedException(final Event e) {
        event = e;
    }
    
    /**
     * Return a short description of the occurred exception.
     * 
     * @return	error description
     * */
    public String getMessage() {
        return toString();        
    }
    
    /**
     * Generates a short description of the occurred exception.
     * 
     * @return	error description
     * */
    public String toString() {
        String msg = "JEQNUnexpectedEventReceived : \n" +
                     "Event sender      : " + event.getSenderName() + "\n" +
                     "Event recipient   : " + event.getRecipientName() + "\n" +
                     "Event time        : " + event.getTime().getValue() + "\n" +
                     "Event tag         : " + event.getTag() + "\n";        
        return msg;
    }    
}
