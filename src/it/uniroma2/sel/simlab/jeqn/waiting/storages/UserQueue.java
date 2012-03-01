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

import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNQueueOverflowException;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;

/** Defines an abstract data strucure representing a UserQueue
 *
 * @author  Daniele Gianni
 */
public interface UserQueue {
        
    /**
     * To extract one user from the queue
     * @return the user extracted
     */
    public User extract();   
    
    /**
     * says how much it will take to extract the next user. To be called before extracting
     * a user
     * @return the time
     */
    public Time nextUserExtractingTime();
    
    /**
     * Insert the given user into the user queue
     * @param u the user
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNQueueOverflowException If the user queue cannot store the user
     */
    public void insert(User u) throws JEQNQueueOverflowException;    
    
    /**
     * Says whether the queue is empty or not, that is if it contains at least one user
     * @return {@code true} if queue length is greater than 0
     * {@code false} if queue length is 0
     */
    public boolean isEmpty();
    
    /**
     * Says whether the queue is full or not. The semantic of this is defined into the 
     * implementing classes.
     * @return {@code true} if full or {@code false} if not.
     * @see FiniteUserQueue
     * @see InfiniteUserQueue
     */
    public boolean isFull();
    
    /**
     * Returns the number of user currently in queue
     * @return the number of users
     */
    public int getEnqueuedUsers();
    
    /**
     * Returns the number of users that have passed through this user queue
     * @return the number of users
     */
    public int getNumberOfUsersPassedThrough();
    
    /**
     * Returns a string of stats info collected at user queue level
     * @return The stats string
     */
    public String getStatInfo();
    
    /**
     * The size of the user queue.
     * @return the size
     */
    public int size();    
    
    /**
     * Sets the underlying layer implementation for stats collection purposes
     * @param l the implementation of the lower layer
     */
    public void setLayer3ToLayer2(final Layer3ToLayer2 l);
}
