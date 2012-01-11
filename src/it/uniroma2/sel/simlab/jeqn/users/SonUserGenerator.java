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

import java.util.ArrayList;

/** Provides a generator of son users from a given dad user
 *
 * @author Daniele Gianni
 */
public final class SonUserGenerator implements UserGenerator {

    // dad user to which the son users will be referring
    private DadUser dadUser;

    // number of son users to be generated
    private Integer numberOfSons;    

    //
    private UserGenerator userGenerator;

    // number of son uses generated
    private int sonsGenerated = 0;    
    
    /**
     * Creates a new instance of SonUserGenerator
     */
    public SonUserGenerator() {
    }    
    
    public SonUser getNextUser() {            
        //if (sonsGenerated < numberOfSons) {
            return new SonUser(/*userGenerator.getNextUser(),*/ dadUser);
        /*} else {
            return null;
        }*/
    }
    
    /**
     * Generates a number of sons from the dad
     * @return A list of sons
     * @param numberOfSons Number of sons to be generated
     * @param dad Dad
     */
    public ArrayList<SonUser> generateSonsFor(final DadUser dad, final int numberOfSons) {        
        ArrayList<SonUser> sonList = new ArrayList<SonUser>();
        
        setDadUser(dad);
        setNumberOfSons(numberOfSons);               
        
        for (int i = 0; i < numberOfSons; i++) {
            SonUser son = getNextUser();
            sonList.add(son);
            dadUser.addSon(son);
        }
    
        return sonList;
    }
    
    private void setDadUser(final DadUser u) {
        dadUser = u;
    }
    
    private void setNumberOfSons(final Integer i) {
        numberOfSons = i;
    }  
}
