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
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.waiting.policies.dispatchings.QueueAssigner;

import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;

import java.util.List;

/**
 * Implements the structure for a Multi Queue
 * @author Daniele Gianni
 */
public final class UserMultiQueue implements UserQueue {

    // the object that controls the assigments of users to the queues
    private QueueAssigner queueAssigner;

    // the policy that governs the retrieval of the next user upon the request of a
    // cascade service center
    private MaskBasePolicy<?, ?, ?, UserQueue> userWithdrawer;

    // the list of all the queues forming the multi-queue structure
    private List<? extends UserQueue> userQueues;

    // interface of SimArch services, needed to collect statistics
    private Layer3ToLayer2 layer3ToLayer2;
     
    // statistics
    private int usersEnqueued;    
    private int usersPassedThrough;
    
    /**
     * Creates a new instance of UserMultiQueue
     * @param queueAssigner The component in charge of enqueueing an incoming user to a queue
     * @param userWithdrawer The component in charge of withdrawing a user from the system
     * @param userQueues The set of queues making up the multi queue
     */
    public UserMultiQueue(final QueueAssigner queueAssigner,
                          final MaskBasePolicy<?, ?, ?, UserQueue> userWithdrawer,     
                          final List<? extends UserQueue> userQueues) {
        
        setQueueAssigner(queueAssigner);
        setUserWithdrawer(userWithdrawer);

        setUserQueues(userQueues);
        
        init();
    }
    
    // init methods
    
    private void init() {
        initStats();
    }
    
    private void initStats() {
        setUsersEnqueued(0);        
        setUsersPassedThrough(0);
    }
    
    // core methods
    
    public User extract() {           
        UserQueue userQueueToWithdraw = userWithdrawer.getDecisionFor(null); //For() getDecision(lastServed);
        
        usersEnqueued--;                
        
        return userQueueToWithdraw.extract();         
    }
    
    public Time nextUserExtractingTime() {               
        // IMPORTANT NOTE:
        // substitute the following return value with
        // .fromTo(previousQueue, currentQueue) and add a pre cond at the function header
        // (Switch Queue method must be invoke before the use of this
        
        //return queuesSwitchingTimes.fromTo(previousQueue, currentQueue);
        return JEQNTimeFactory.makeFrom(Time.ZERO); //userWithdrawer.getSwitchingTime();
    }            
            
    public void insert(User u) throws JEQNQueueOverflowException {                
        usersEnqueued++;        
        usersPassedThrough++;
                
        System.out.flush();
        queueAssigner.getUserQueue(u); //.insert(u);
        UserQueue uq = queueAssigner.getUserQueue(u);
        
        uq.insert(u);       
    }
    
    public boolean isEmpty() {
        
        return (usersEnqueued == 0);
        /*
        for (UserQueue queue : userQueues) {
            if (!queue.isEmpty()) {
                return false;
            }
        }        
        return true;
         */
    }
    
    public boolean isFull() {
        
        return (usersEnqueued > 0);
        
        /*
        for (UserQueue queue : userQueues) {
            if (queue.isFull()) {
                return true;
            }
        }        
        return false;
         */
    }
    
    // accessor methods
    
    /**
     * Accessor method for the property
     * @param q Queue number
     * @return The queue
     */
    public int getQueueNumber(final UserQueue q) {
        return userQueues.indexOf(q);
    }                

    /**
     * Accessor method for the property
     * @return The number of users currently enqueued in the multi queue
     */
    public int getEnqueuedUsers() {
        return usersEnqueued;
    }
    
    /**
     * Accessor method for the property
     * @return The number of queues making up the multi queue
     */
    public Integer getNumberOfQueues() {
        return userQueues.size();
    }
    
    /**
     * Accessor method for the property
     * @return The number of users that have gone throught this component since the beginning
     */
    public int getNumberOfUsersPassedThrough() {
        return usersPassedThrough;
    }
    
    /**
     * Gets some stats info
     * @return A string summarizing the stats collected
     */
    public String getStatInfo() {
        String s = new String();
        
        for (int i = 0; i < userQueues.size(); i++) {
            s += "Queue" + i + " : " + userQueues.get(i).getStatInfo() + "\n";
            s += "Percentage of users in this queue : " + (userQueues.get(i).getNumberOfUsersPassedThrough() * 1.0 / usersPassedThrough) + "\n";
        }
        
        //s += "\n" + queuesAssigner.getStatInfo();
        
        return s;
    }    
    
    /**
     * Accessor method for the property
     * @param i The queue number
     * @return The queue
     */
    public UserQueue getUserQueue(final int i) {
        return userQueues.get(i);
    }
    
    /**
     * Accessor method for the property
     * @return The list of queues
     */
    public List<? extends UserQueue> getUserQueues() {
        return userQueues;
    }
    
    public void setLayer3ToLayer2(final Layer3ToLayer2 l) {        
        layer3ToLayer2 = l;
        
        for (UserQueue uq : userQueues) {
            uq.setLayer3ToLayer2(l);
        } 
    }

    private void setQueueAssigner(final QueueAssigner qa) {
        queueAssigner = qa;
    }
    
    private void setUserQueues(final List<? extends UserQueue> l) {
        userQueues = l;
    }  

    private void setUserWithdrawer(final MaskBasePolicy<?, ?, ?, UserQueue> uw) {
        userWithdrawer = uw;
    }
    
    /*
    protected void setQueuesSwitchingTimes(final QueuesSwitchingTimes t) {
        queuesSwitchingTimes = t;
    }
    */
    
    private void setUsersEnqueued(final int i) {
        usersEnqueued = i;
    }

    private void setUsersPassedThrough(final int i) {
        usersPassedThrough = i;
    }    
    
    public int size() {        
        return usersEnqueued;
    }    
}
