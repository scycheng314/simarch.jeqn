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

import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Time;

import it.uniroma2.sel.simlab.jeqn.requests.ServiceRequest;

/** Implements the basic user in jEQN simulator
 *
 * @author  Daniele Gianni
 */
public class User implements Comparable<User> {

    /*
     * default name for unknonw user
     */
    public static final String UNKNOWN_USER = "noName";

    // user instantiation time
    private Time      bornTime;

    // user class
    private Category  category;

    // user arrival time at the entering entity
    private double    inComingTime = 0.0;

    // user name
    private String name;

    // time request for the next service center
    private ServiceRequest serviceRequest;
    
    /** 
     * Creates a new instance of User
     */
    public User() {
        setName(UNKNOWN_USER);
        setBornTime(JEQNTimeFactory.makeFrom(Time.ZERO));
        //setInComingTime(0.0);
    }
    
    /**
     * Creates a new instance of User. 
     * User attributes {@code name}, {@code bornTime} and {@code category} are valued according to the attributes of the 
     * specified User passed as parameter.   
     * @param u The user used to determine the attributes value of the new user. 
     */
    public User(final User u) {
        setName(u.getName());
        setBornTime(u.getBornTime());
        //setInComingTime(0.0); //u.getInComingTime());
        setCategory(u.getCategory());
    }
    
    /**
     * Creates a new instance of User with the specified name.
     * @param s The user name
     */
    public User(final String s) {
        setName(s);
        setBornTime(JEQNTimeFactory.makeFrom(Time.ZERO));
        //setInComingTime(0.0);
    }
    
    /**
     * Creates a new instance of User with the specified name and category.
     * @param s The user name
     * @param c The category
     */
    public User(final String s, final Category c) {
        setName(s);
        setCategory(c);
        setBornTime(JEQNTimeFactory.makeFrom(Time.ZERO));
        //setInComingTime(0.0);
    }
    
    /**
     * Check whether or not this user is carrying tokens
     * @return {@code false} always for this basic user
     */
    public boolean carriesTokens() {
        return false;
    }
    
    /**
     * Compare this user with the specified user.
     * @param u User to be compared with this user.
     */
    public int compareTo(final User u) { //, final Comparable<? extends User> u2) {
        return name.compareTo(u.getName());
    }
    
    /** Gets the user category.
     * @return User category.
     *
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Sets the user category.
     * @param c the new user category
     */
    public void setCategory(final Category c) {
        category = c;
    }    
    
    /** Gets the user name.
     * @return User name.
     *
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the user name.
     * @param s The new user name
     */
    public void setName(final String s) {
        name = s;
    }
        
    /** Gets the time request for the next service center.
     * @return Service request value.
     *
     */
    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
    
    /**
     * Sets the time request for the next service center.
     * @param sr The new service request
     */
    public void setServiceRequest(final ServiceRequest sr) {
        serviceRequest = sr;
    }    
    
    /**
     * Returns a string constituted by the user properties value.
     * 
     * @return the concatenation of user properties value.
     */
    public String toString() {
        return getName() + " " + getCategory() + " " + getServiceRequest() + " Born time : " + getBornTime().getValue() +"#";
    }
    
    /** Gets the user born time.
     * @return The user born time.
     *
     */
    public Time getBornTime() {
        return bornTime;
    }
    
    /**
     * Set the value for the user born time.
     * @param t user born time.
     */
    public void setBornTime(final Time t) {
        bornTime = t;
    }
    
    /**
     * Gets the user incoming time value.
     * 
     * @return The user arrival time at the current EQN entity
     */
    public double getInComingTime() {
        return inComingTime;
    }
    
    /**
     * Sets the user incoming time value.
     * 
     * @param d The new user arrival time at the current EQN entity
     */
    public void setInComingTime(final double d) {
        inComingTime = d;
    }    
    /*
    public String toString() {
        return " Name : " + name + "  Born time : " + bornTime;
    }
     */
}
