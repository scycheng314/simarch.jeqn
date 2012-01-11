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

/** The basic user in jEQN simulators
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
    
    /** Creates a new instance of User */
    public User() {
        setName(UNKNOWN_USER);
        setBornTime(JEQNTimeFactory.makeFrom(Time.ZERO));
        //setInComingTime(0.0);
    }
    
    /**
     * Creates a new instance of User
     * @param u The user which it can copy all data
     */
    public User(final User u) {
        setName(u.getName());
        setBornTime(u.getBornTime());
        //setInComingTime(0.0); //u.getInComingTime());
        setCategory(u.getCategory());
    }
    
    /**
     * Creates a new instance of User
     * @param s The user name
     */
    public User(final String s) {
        setName(s);
        setBornTime(JEQNTimeFactory.makeFrom(Time.ZERO));
        //setInComingTime(0.0);
    }
    
    /**
     * Creates a new instance of User
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
     * Says whether is carrying token
     * @return {@code false} always for this basic user
     */
    public boolean carriesTokens() {
        return false;
    }
    
    /**
     * 
     */
    public int compareTo(final User u) { //, final Comparable<? extends User> u2) {
        return name.compareTo(u.getName());
    }
    
    /** Getter for property category.
     * @return Value of property category.
     *
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Setter for property category.
     * @param c the new category
     */
    public void setCategory(final Category c) {
        category = c;
    }    
    
    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setter for property name.
     * @param s The new name
     */
    public void setName(final String s) {
        name = s;
    }
        
    /** Getter for property request.
     * @return Value of property request.
     *
     */
    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
    
    /**
     * Setter for property request.
     * @param sr The new service request
     */
    public void setServiceRequest(final ServiceRequest sr) {
        serviceRequest = sr;
    }    
    
    public String toString() {
        return getName() + " " + getCategory() + " " + getServiceRequest() + " Born time : " + getBornTime().getValue() +"#";
    }
    
    /** Getter for property bornTime.
     * @return Value of property bornTime.
     *
     */
    public Time getBornTime() {
        return bornTime;
    }
    
    /**
     * Setter for property bornTime.
     * @param t The born time
     */
    public void setBornTime(final Time t) {
        bornTime = t;
    }
    
    /**
     * Getter for property inComingTime.
     * 
     * @return Value of property inComingTime.
     */
    public double getInComingTime() {
        return inComingTime;
    }
    
    /**
     * Setter for property inComingTime.
     * 
     * @param d The new time
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
