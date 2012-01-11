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

package it.uniroma2.sel.simlab.jeqn.waiting.storages;

import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNQueueOverflowException;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;

/**
 * Provides a bounded data structure to store the users
 *
 * @author Daniele Gianni
 */

public final class FiniteUserQueue implements UserQueue {    

    // number of users that could not be admitted on queue and have been lost
    private int dischargedUsers;

    // max size of the queue
    private int size;

    // current size of the queue
    private int enqueuedUsers;
    
    private UserQueue userQueue;

    // interface to SimArch service, needed to retrieve time for statistics computations
    private Layer3ToLayer2 layer3ToLayer2;
        
    /**
     * Create a new instance of FiniteUserQueue
     * @param userQueue An actual infinite user queue to store the users
     * @param size The max number of user that this queue can keep at the same time
     * @param l The underlying layer implementation
     */
    public FiniteUserQueue(final UserQueue userQueue, final int size, final Layer3ToLayer2 layer2) {        
        setUserQueue(userQueue);                
        setSize(size);                
        
        setLayer3ToLayer2(layer2);
        
        init();
    }
    
    
    /**
     * Create a new instance of FiniteUserQueue
     * @param userQueue An actual infinite user queue to store the users
     * @param size The max number of user that this queue can keep at the same time
     * @param l The underlying layer implementation
     */
    public FiniteUserQueue(final UserQueue userQueue, final int size) {        
        setUserQueue(userQueue);                
        setSize(size);                
                
        init();
    }
    
    // init methods
    
    private void init() {
        initStats();
    }
    
    private void initStats() {
        setDischargedUsers(0);        
        setEnqueuedUsers(0);
    }
    
    // core methods
    
    public User extract() {
        enqueuedUsers--;
        return userQueue.extract();
    }        
    
    public String getStatInfo() {        
        return userQueue.getStatInfo() + "\n" + "Discharged users : " + dischargedUsers;
    }
    
    public void insert(final User u) throws JEQNQueueOverflowException {
        if (isFull()) {
            dischargedUsers++;
            throw new JEQNQueueOverflowException(u);
        } else {
            enqueuedUsers++;
            userQueue.insert(u);
        }
    }   
    
    public boolean isEmpty() {
        return userQueue.isEmpty();
    }
    
    public boolean isFull() {
        return (size == userQueue.getEnqueuedUsers());
    }
    
    public Time nextUserExtractingTime() {
        return userQueue.nextUserExtractingTime();
    }
    
    // accessor methods
    
    /**
     * Accessor method for the property
     * @return The number of users that have been discharged
     */
    public int getDischargedUsers() {
        return dischargedUsers;
    }
    
    /**
     * Accessor method for the property
     * @return The number of users currently enqueued
     */
    public int getEnqueuedUsers() {
        return userQueue.getEnqueuedUsers();
    }
    
    /**
     * Accessor method for the property
     * @return The number of users that have successfully passed through this component
     */
    public int getNumberOfUsersPassedThrough() {
        return userQueue.getNumberOfUsersPassedThrough();
    }    
    
    private void setDischargedUsers(final int i) {
        dischargedUsers = i;
    }

    private void setEnqueuedUsers(final int i) {
        enqueuedUsers = i;
    }
    
    /**
     * Accessor method for the property
     * @param i The size of this queue
     */
    protected void setSize(final int i) {
        size = i;
    }
    
    /**
     * Accessor method for the property
     * @param q The actual UserQueue implementation it is built on
     */
    protected void setUserQueue(final UserQueue q) {
        userQueue = q;
    }

    /**
     * Accessor method for the property
     * @param l The underlying layer
     */
    public void setLayer3ToLayer2(final Layer3ToLayer2 l) {
        layer3ToLayer2 = l;        
        userQueue.setLayer3ToLayer2(l);
    }
    
    public int size() {
        return size;
    }
}
