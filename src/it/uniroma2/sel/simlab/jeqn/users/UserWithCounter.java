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

/** Wraps a user allowing it to set up a counter
 *
 * @author Daniele Gianni
 */
public final class UserWithCounter extends User {
    
    private static final int DEFAULT_THRESHOLD = 0;
    
    private int counter;
    private int threshold;
    private User wrapped;
    
    /**
     * Creates a new instance of UserWithCounter
     * @param toWrap The user to be wrapped
     */
    public UserWithCounter(final User toWrap) {
        super(toWrap);
        
        setCounter(0);
        setThreshold(DEFAULT_THRESHOLD);
        setWrapped(toWrap);
    }   
        
    /**
     * Creates a new instance of UserWithCounter
     * @param toWrap The user to be wrapped
     * @param counter The initial counter value
     */
    public UserWithCounter(final User toWrap, final Integer counter) {
        super(toWrap);
        
        setCounter(counter);
        setThreshold(DEFAULT_THRESHOLD);
        setWrapped(toWrap);
    }  
    
    /**
     * Creates a new instance of UserWithCounter
     * @param toWrap The user to wrap
     * @param counter The initial counter value
     * @param threshold The threshold level
     */
    public UserWithCounter(final User toWrap, final Integer counter, final Integer threshold) {
        super(toWrap);
        
        setCounter(counter);
        setThreshold(threshold);
        setWrapped(toWrap);
    }   
    
    /**
     * Wraps the given user with a {@code UserWithCounter}
     * @param toWrap The user to wrap
     * @param counter The initial counter value
     * @return The wrapped user
     */
    public static UserWithCounter wrap(final User toWrap, final Integer counter) {        
        return new UserWithCounter(toWrap, counter);
    }
    
    /**
     * Decrements the counter
     * @return the new counter value
     */
    public boolean decCounter() {
        counter--;        
        return isAtThreshold();
    }
    
    /**
     * Accessor method for the property
     * @return The counter value
     */
    public Integer getCounter() {
        return counter;
    }
    
    /**
     * Accessor method for the property
     * @return The wrapped user
     */
    public User getWrapped() {
        return wrapped;
    }
    
    /**
     * Check whether the user is expired (the threshold has gone)
     * @return {@code true} if counter is greater or equal than threshold
     * {@code false} otherwise
     */
    public boolean hasGone() {
        return isAtThreshold();
    }
    
    /*
    public void incCounter() {
        counter++;
    }
    */
    
    /**
     * Says whether the user is at threshold level or not
     * @return {@code true} if counter is equal to threshold
     * {@code false} otherwise
     */
    public boolean isAtThreshold() {
        return (counter == threshold);
    }
    
    private void setCounter(final int i) {
        counter = i;
    }
    
    private void setThreshold(final int i) {
        threshold = i;
    }
    
    private void setWrapped(final User u) {
        wrapped = u;                
    }      
    
    /**
     * Unwraps the user
     * @return The unwrapped user
     */
    public User unWrap() {
        return getWrapped();
    }
}
