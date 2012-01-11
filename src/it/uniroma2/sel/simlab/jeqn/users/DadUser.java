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

/** Defines the Dad User, ie any type of jEQN user that passing through a Fork Node
 * is transformed in a Dad User - and associated to a set of Son Users
 *
 * @author Daniele Gianni
 */
public class DadUser extends User{

    // the wrapped (original) user passing through a Fork Node
    private User me;

    // the set of Son users
    private ArrayList<SonUser> sons;

    private Integer numberOfSons; // probably redundant but ...
    
    /** Creates a new instance of DadUser
     * @param dadUser the dad
     */
    public DadUser(final User dadUser) {
        super(dadUser);
        
        setMe(dadUser);        
        setSons(new ArrayList<SonUser>());
        
        setNumberOfSons(0);
    }
    
    /** Creates a new instance of DadUser 
     * @param dadUser the dad
     * @param numberOfSons the number of sons
     */
    public DadUser(final User dadUser, final int numberOfSons) {
        super(dadUser);
        
        setMe(dadUser);        
        setSons(new ArrayList<SonUser>());
        
        setNumberOfSons(numberOfSons);        
    }
    
    /**
     * Unwraps the original user
     * @param u The user to unwrap
     * @return The original user
     */
    public static User unwrap(final DadUser u) {
        return u.getMe();
    }
    
    /**
     * Wraps the user with a respective DadUser
     * @param u The user to wrap
     * @return The wrapped user
     */
    public static DadUser wrap(final User u) {
        return new DadUser(u);
    }
    
    /**
     * Adds a son to this DadUser
     * @param u The user to add
     */
    public void addSon(final SonUser u) {        
        sons.add(u);        
        numberOfSons++;
    }
    
    /**
     * Says whether it is the dad of the given user
     * @param su The son user to be checked
     * @return {@code true} if this is its dad
     * {@code false} otherwise
     */
    public boolean isDadOf(final SonUser su) {
        
        for (SonUser s : sons) {
            if (s.compareTo(su) == 0) { 
                return true;
            } 
        }        
        return false;
    }
    
    /**
     * Accessor method for property
     * @return The original user
     */
    public User getMe() {
        return me;
    }

    /**
     * Accessor method for property
     * @return This user name
     */
    public String getName() {
        return "Dad-" + me.getName();
    }
    
    /**
     * Accessor method for property
     * @return This user's number of sons
     */
    public Integer getNumberOfSons() {
        return numberOfSons;
    }
    
    /**
     * Accessor method for property
     * @return This user's sons
     */
    public ArrayList<SonUser> getSons() {
        return sons;
    }
    
    /**
     * Says whether this user has sons or not
     * @return {@code true} if this user has not sons
     * {@code false} otherwise
     */
    public boolean hasNoSons() {
        return sons.isEmpty();
    }
        
    /**
     * Says if this user is dad of the given user
     * @param u The son user to be checked
     * @return {@code true} if this has user u
     * {@code false} otherwise
     */
    public boolean hasSon(final User u) {
        return sons.contains(u);
    }
    
    /**
     * Says if this has sons or not
     * @return the opposite of {@code hasNoSons}
     */
    public boolean hasSons() {
        return !hasNoSons();
    }
    
    /**
     * Remove the given user from son user list
     * @param u The user to be removed
     */
    public void remove(final User u) {
        sons.remove(u);
    }
    
    private void setMe(final User u) {
        me = u;
    }
    
    private void setNumberOfSons(final int i) {
        numberOfSons = i;
    }
    
    private void setSons(final ArrayList<SonUser> l) {
        sons = l;
    }       
    
    /**
     * Unwrap the original user
     * @return the original user
     */
    public User unwrap() {
        return getMe();
    }    
}
