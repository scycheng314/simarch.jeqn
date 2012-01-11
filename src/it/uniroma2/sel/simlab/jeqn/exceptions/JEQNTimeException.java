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

/** Indicates that an exception concerning the simulation time has been raised.
 * Examples of these exceptions are: causality violation, negative time value, etc.
 *
 * @author Daniel Gianni
 */
public class JEQNTimeException extends JEQNException {
    
    /** Creates a new instance of JEQNTimeException */
    public JEQNTimeException() {
    }
    
    public JEQNTimeException(final Exception e) {
        super(e);
    }
    
    public JEQNTimeException(final String s) {
        super(s);
    }
}
