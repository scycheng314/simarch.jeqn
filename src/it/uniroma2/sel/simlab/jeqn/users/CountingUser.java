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

import it.uniroma2.sel.simlab.simarch.data.Time;

/** Represents a user that incorporates counting capabilities, ie the user is provided
 * with an internal counter. The counter can be incremented, tested, and reset using the
 * class methods
 *
 * We need to investigate the differences with UserWithCounter
 *
 * @author Daniele Gianni
 */
public final class CountingUser extends User {   

    // name prefix indentifying the type CountingUser
    public final static String COUNT_PREFIX = "Count";

    // the internal counter
    private int counter;

    // the user being wrapped in the CountingUser class
    private User wrapped;
    
    /** 
     * Creates a new instance of UserWithCounter wrapping an existing user
     * @param toWrap User to be wrapped
     */
    public CountingUser(final User toWrap) {
        super(toWrap);
        setWrapped(toWrap);
        setCounter(0);        
    }   
    
    /**
     * Static method that returns a {@code CountingUser} wrapping an existing user.
     * @param toWrap the user to be wrapped.
     * @return A new istance of UserWithCounter
     */
    public static CountingUser wrap(final User toWrap) {        
        return new CountingUser(toWrap);
    }
    
    /**
     * Increments the internal counter and returns the updated value
     * @return The value of the internal counter. 
     */
    public int count() {
        counter++;        
        return getCounter();
    }
    
    /**
     * Gets the internal counter value.
     * @return Counter value.
     */
    public int getCounter() {
        return counter;
    }
    
    public String getName() {
        return COUNT_PREFIX + wrapped.getName();
    }
    
    /**
     * Gets the user wrapped into this UserWithCounter.
     * @return the wrapped user.
     */
    public User getWrapped() {
        return wrapped;
    }
        
    /**
     * Sets the internal counter to the specified value.
     * @param i value used to update the internal counter.
     */
    private void setCounter(final int i) {
        counter = i;
    }    
    
    /**
     * Sets the {@code User} object wrapped into this UserWithCounter.
     * @param u User to be wrapped.
     */
    private void setWrapped(final User u) {
        wrapped = u;                
    }      
    
    /**
     * Unwraps and returns the user wrapped into this UserWithCounter. 
     * @return The wrapped user.
     */
    public User unWrap() {
        return getWrapped();
    }
           
    /** Setter for property request.
     * @param request New value of property request.
     *
     *//*
    public void setServiceRequest(final ServiceRequest serviceRequest) {
        //super.setServiceRequest(serviceRequest);        
        wrapped.setServiceRequest(serviceRequest);
    }        */
    
    /** Getter for property bornTime.
     * @return Value of property bornTime.
     *
     */
    public Time getBornTime() {
        return wrapped.getBornTime();
    }
    
    /** Setter for property bornTime.
     * @param bornTime New value of property bornTime.
     *
     */
    public void setBornTime(final Time bornTime) {
        super.setBornTime(bornTime);        
        //wrapped.setBornTime(bornTime);
    }    
    
    /** Setter for property comingInTime.
     * @param comingInTime New value of property comingInTime.
     *
     *//*
    public void setComingInTime(final double comingInTime) {
        //super.setComingInTime(comingInTime);
        wrapped.setComingInTime(comingInTime);
    }    */
}

