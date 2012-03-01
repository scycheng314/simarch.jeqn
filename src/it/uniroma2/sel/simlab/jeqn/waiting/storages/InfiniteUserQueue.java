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

import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.interfaces.Layer3ToLayer2;

import it.uniroma2.sel.simlab.statistics.estimators.ContinuousPopulationMean;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

import java.util.ArrayList;
import java.util.List;

/** Provides an unbounded UserQueue
 *
 * @author  Daniele Gianni
 */
public final class InfiniteUserQueue implements UserQueue {

    // list of the user currently stored in the queue
    private List<User> users;

    // list of the arrival times, for each of the user in the above list
    private List<Time> arrivalTimes;

    // the policy that specify in which position within the queue an incoming user has to be
    // inserted
    private MaskBasePolicy<?, User, ?, Integer> enqueuingPolicy;

    // time since last activity - for the determination of busy/idle cycles
    private Time timeSinceLastComingInOrOut;

    // statistics
    private ContinuousPopulationMean queueLengthMean;
    private DiscretePopulationMean queueWaitingTimeMean;
    
    private Layer3ToLayer2 layer3ToLayer2;

    // number of users enqueued since the start
    private int enqueuedUsers;

    // max length of the queue
    private int maxUsers;

    private int numberOfUsersPassedThrough;
    
    /**
     * Creates a new instance of InfiniteUserQueue
     * @param insertionPolicy Policy that specify the position within the queue an incoming user has to be inserted
     * @param userListImplementation list of the user currently stored in the queue
     */
     
    public InfiniteUserQueue( final MaskBasePolicy<?, User, ?, Integer> insertionPolicy, final List<User> userListImplementation) {
        
        setEnqueuingPolicy(insertionPolicy); 
        setUsers(userListImplementation);
        
        init();
    }
    
    // init methods
    
    private void init() {
        
        enqueuedUsers = 0;
        
        initStats();        
    }
    
    private void initStats() {
        initQueueLengthMean(); 
        initQueueWaitingTimeMean(); 
        
        initArrivalTimes(); 
        
        setMaxUsers(0);   
        setNumberOfUsersPassedThrough(0);
        setTimeSinceLastComingInOrOut(JEQNTimeFactory.makeFrom(0));        
    }
    
    private void initArrivalTimes() {
        arrivalTimes = new ArrayList<Time>();
    }
    
    private void initQueueLengthMean() {
        queueLengthMean = new ContinuousPopulationMean();
    }

    private void initQueueWaitingTimeMean() {
        queueWaitingTimeMean = new DiscretePopulationMean();
    }
        
    // core methods
    public User extract() {
    	
    	if (layer3ToLayer2.getClock().decreasedBy(timeSinceLastComingInOrOut).getValue()!=0) {
    		queueLengthMean.insertNewSample(users.size(), layer3ToLayer2.getClock().decreasedBy(timeSinceLastComingInOrOut).getValue());
    	}
    	
        timeSinceLastComingInOrOut = layer3ToLayer2.getClock();                   
        
        User user = users.remove(0);        
        
        double arrivalTime = arrivalTimes.remove(0).getValue();
        
        queueWaitingTimeMean.insertNewSample(layer3ToLayer2.getClock().getValue() - arrivalTime);//user.getComingInTime());
        
        enqueuedUsers--;
        
        return user;
    }            
    
    public void insert(User u) {         
        queueLengthMean.insertNewSample(users.size(), layer3ToLayer2.getClock().decreasedBy(timeSinceLastComingInOrOut).getValue());
        
        timeSinceLastComingInOrOut = layer3ToLayer2.getClock();
                     
        int decision = enqueuingPolicy.getDecisionFor(u);
                                
        users.add(decision, u); 
        arrivalTimes.add(decision, layer3ToLayer2.getClock());                        
        
        enqueuedUsers++;
        
        if (enqueuedUsers > maxUsers) maxUsers = enqueuedUsers;      
        
        numberOfUsersPassedThrough++;                     
    }
                
    public String getStatInfo() {
        return "\nQueue Length Mean Value : " + queueLengthMean.meanValue() + "\n" +
                /*"\nQueue Length Mean Variance : " + queueLengthMean.variance() + "\n" + */
                "Waiting Time Mean Value : " + queueWaitingTimeMean.meanValue() + "\n" +
                /*"\nWaiting Time Mean Variance : " + queueWaitingTimeMean.variance() + "\n" +*/
                "Max users in queue      : " + maxUsers + "\n" +
                "Number of users         : " + numberOfUsersPassedThrough + "\n" +
                "Users still in queue    : " + enqueuedUsers + "\n" +
                "Sample size             : " + queueWaitingTimeMean.sampleSize();
    }     

    // accessor method                
    public int getEnqueuedUsers() {
        return enqueuedUsers;
    }
    
    public int getNumberOfUsersPassedThrough() {
        return numberOfUsersPassedThrough;
    }
    
    public List<User> getUsers() {
        return users;
    }  
    
    public boolean isEmpty() {
        //System.out.println("Users : " + users.isEmpty());
        return users.isEmpty();
    }
    
    public boolean isFull() {
        return !isEmpty();
    }
    
    public Time nextUserExtractingTime() {
        return JEQNTimeFactory.makeFrom(Time.ZERO);
    }
      
    private void setEnqueuingPolicy(final MaskBasePolicy<?, User, ?, Integer> p) {
        enqueuingPolicy = p;
    }
    
    public void setLayer3ToLayer2(final Layer3ToLayer2 l) {
        layer3ToLayer2 = l;
    }
    
    private void setMaxUsers(final int i) {
        maxUsers = i;
    }

    private void setNumberOfUsersPassedThrough(final int i) {
        numberOfUsersPassedThrough = i;
    }   

    private void setTimeSinceLastComingInOrOut(final Time t) {
        timeSinceLastComingInOrOut = t;
    }    
    
    private void setUsers(final List<User> l) {
        users = l;
    } 
    
    public int size() {
        return users.size();
    }
}
