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

/** Defines a SonUser, ie a user created by a Fork Node upon the arrival of any jEQN
 * user. The SonUser also maintain a reference to the DadUser
 *
 * @author Daniele Gianni
 */
public final class SonUser extends User {

    // the object representing the son user
    private User me;

    // the reference to the dad user
    private DadUser dad;
    
    /**
     * Creates a new instance of SonUser
     * @param dad The new user's dad
     */
    public SonUser(final DadUser dad) {
        super();
        
        setDad(dad);
        
        setMe(new User("SonOf-" + dad.getName(), dad.getCategory()));        
    }
    
    /**
     * Creates a new instance of SonUser
     * @param son The new son
     * @param dad The new user's (son's) dad
     */
    public SonUser(final User son, final DadUser dad) {
        super(son);
        
        setMe(son);        
        setDad(dad);
    }
    
    /**
     * Unwraps the son user
     * @param u The user to wrap
     * @return The wrapped user
     */
    public static User unwrap(final SonUser u) {
        return u.getMe();
    }        
    
    /**
     * Wraps the original user
     * @param me The user to wrap
     * @param dad The user's dad
     * @return The son user (wrapped)
     */
    public static SonUser wrap(final User me, final DadUser dad) {
        return new SonUser(me, dad);
    }    
    
    /**
     * Accessor method for property
     * @return The dad
     */
    public DadUser getDad() {
        return dad;
    }
    
    /**
     * Accessor method for property
     * @return The son original user
     */
    public User getMe() {
        return me;
    }       
    
    /**
     * Accessor method for property
     * @return The user name
     */
    public String getName() {
        return me.getName();
    }
    
    /**
     * Accessor method for property
     * @return The dad
     */
    public ArrayList<SonUser> getDadSons() {
        return dad.getSons();
    }
    
    /**
     * Returns the number of siblings this son user has
     * @return Number of siblings
     */
    public Integer getNumberOfSiblings() {
        return dad.getNumberOfSons();
    }
    
    /**
     * Says whether it has the specified dad or not
     * @param d The dad to check the sonhood
     * @return {@code true} if d is this user's dad
     * {@code false} no otherwise
     */
    public boolean isSonOf(final DadUser d) {
        return (dad.compareTo(d) == 0);
    }
    
    private void setMe(final User u) {
        me = u;
    }
    
    private void setDad(final DadUser u) {
        dad = u;
    }    
}
