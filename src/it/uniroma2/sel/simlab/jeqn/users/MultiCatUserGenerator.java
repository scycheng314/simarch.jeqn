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

package it.uniroma2.sel.simlab.jeqn.users;

import it.uniroma2.sel.simlab.jrand.objectStreams.numericStreams.NumericStream;
import java.util.Vector;


/** Provides a multi category generator. Interleaves a set of {@code UserGenerator}
 * by means of a number sequence
 *
 * @author  Daniele Gianni
 */
public final class MultiCatUserGenerator implements UserGenerator {

    // the set of user generators
    private Vector usersGenerators;

    // the interleaving numeric stream for the selection of a user generator
    private NumericStream numericStream;
    
    /** Creates a new instance of MultiCatUserGenerator 
     * @param s The sequence of number to mix the single {@code UserGenerator}
     */
    public MultiCatUserGenerator(final NumericStream s) {
        numericStream = s;
        usersGenerators = new Vector();
    }
    
    /** Adds another {@code UserGenerator}
     * @param g 
     */
    public void addUsersGenerator(final UserGenerator g) {
        usersGenerators.addElement(g);
    }
        
    public User getNextUser() {
        return ((UserGenerator)(usersGenerators.elementAt(numericStream.getNext().intValue()))).getNextUser();
    }    
}
