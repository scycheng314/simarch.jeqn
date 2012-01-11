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

package it.uniroma2.sel.simlab.jeqn.waiting;

import it.uniroma2.sel.simlab.jeqn.errors.JEQNConfigurationError;
import it.uniroma2.sel.simlab.jeqn.errors.JEQNError;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNQueueOverflowException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;

import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.requests.ServiceRequestGenerator;
import it.uniroma2.sel.simlab.jeqn.users.CountingUser;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;
import it.uniroma2.sel.simlab.simarch.data.Event;

import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;

/**
 * Defines a Waiting System that support resource preemption upon receiving a users with higher
 * priority
 *
 * @author Daniele Gianni
 */
public class PreemptiveWaitingSystem extends WaitingSystem {  

    /*
     * the name of the port through which the waiting system receives requests for next users to be processed
     */
    public static final String NEXT_REQUEST_USER_PORT_NAME = "nextRequestUser";

    /**
     * The port through which it is asked for a new user to be sent
     */
    protected InPort nextRequestUserPort;       
            
    /**
     * A statistic variable for the number of user under processing by the following 
     * entity
     */
    protected User underProcessing;
    
    /**
     * A statistic counter for the number of preemptions
     */
    protected int preemptions;
    /**
     * A statistic counter for the number of reenqueueing request received
     */
    protected int reenqueueings;
    
    /**
     * The policy used to evaluate if a incoming user has enough right, priority, etc. 
     * to interrupt the current service and being served with no delay.
     */
    protected MaskBasePolicy<User, User, ?, Boolean> preemptionPolicy;
    
    /**
     * Creates a new instance of WaitingSystem
     * @param name The entity name
     * @param timeFactory The time factory to build specific time values
     * @param layer2factory An implementation of the underlying layer
     * 
     * @param userQueue An implementation of the actual user queue data structure to store the users
     * @param requestGenerator The service request generator for the following service center
     * @param preemptionPolicy The policy for the preemption
     * @param decisionDataFactory The data factory to build the data upon which the policy will take the decision
     * (decision data)
     * @throws it.uniroma2.info.sel.simlab.simarch.exceptions.InvalidNameException Thrown if the provided name does not conform to the underlying name conventions
     */
    public PreemptiveWaitingSystem(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final UserQueue userQueue, final ServiceRequestGenerator requestGenerator, final MaskBasePolicy<User, User, ?, Boolean> preemptionPolicy, final Time sendingAheadDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userQueue, requestGenerator, sendingAheadDelay);
        
        setNextRequestUserPort(new InPort(name, this));
        setPreemptionPolicy(preemptionPolicy);
        
        init();        
    }    
    
    /**
     * Creates a new instance of WaitingSystem
     * @param name The entity name
     * @param timeFactory The time factory to build specific time values
     * @param layer2factory An implementation of the underlying layer
     * 
     * @param userQueue An implementation of the actual user queue data structure to store the users
     * @param requestGenerator The service request generator for the following service center
     * @param preemptionPolicy The policy for the preemption
     * @param decisionDataFactory The data factory to build the data upon which the policy will take the decision
     * (decision data)
     * @param sendingAheadDelay The time delay introduced when the user is forwarded to the next entity
     * @throws it.uniroma2.info.sel.simlab.simarch.exceptions.InvalidNameException 
     */
    public PreemptiveWaitingSystem(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final UserQueue userQueue, final ServiceRequestGenerator requestGenerator, final MaskBasePolicy<User, User, ?, Boolean> preemptionPolicy, final double sendingAheadDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userQueue, requestGenerator, sendingAheadDelay);
        
        setNextRequestUserPort(new InPort(name, this));      
        setPreemptionPolicy(preemptionPolicy);
        
        init();
    }
    
    private void init() {        
        initStats();
    }
    
    private void initStats() {
        setLastUserMovementTime(0.0);
        setPreemptions(0);
        setReenqueueings(0);        
    }
    
    // core methods
    
    /**
     * Says if the given user can interrupt the user currently under processing
     * @param u The user to be evaluated
     * @return {@code true} if u can preempt {@code false} otherwise
     */
    protected boolean isHigherPriorityThanCurrentlyUnderProcessing(final User u) {        
                
        if (underProcessing != null) {
            
            preemptionPolicy.setImplicitInput(underProcessing);
            return preemptionPolicy.getDecisionFor(u);

        } else return true;
    }            
    
    /**
     * Handles the incoming event {@code NEW_INCOMING_USER}. Check whether the incoming 
     * user can be sent to the service center right soon or not, and then enqueued.
     * @param event The event received
     */
    protected void newIncomingUserEventHandler(final Event event) { //throws JEQNQueueOverflowException {        
        try {            
            User nUser = (User) event.getData();
            nUser.setInComingTime(getClock().getValue()); /*event.getTime().getValue());*/
            
            CountingUser newUser = CountingUser.wrap(nUser);
            
            newUser.setInComingTime(getClock().getValue());//event.getTime().getValue());
            resourceRequestGenerator.assignResourceRequest(newUser);            

            // if a request for the next user was received but not successfully processed
            //(no users at that request time)
            if (isInSendOnComingInState()) {                
                try {
                    userQueue.insert(newUser);
                } catch (JEQNQueueOverflowException ex) {
                    overflownUserHandler(newUser);
                }
                
                Time delay = userQueue.nextUserExtractingTime();
                send(outPort, sendingAheadDelay.increasedBy(delay), Events.NEW_INCOMING_USER, userQueue.extract());
                
                setState(WaitingSystemStates.SEND_ON_REQUEST);
                
                meanQueueLength.insertNewSample(0, getClock().getValue() - lastUserMovementTime);
                meanWaitingTime.insertNewSample(delay.getValue());                                                                              
            } else {
                // chech user properties to see whether the user is higher priority than user currently under processing
                if (isHigherPriorityThanCurrentlyUnderProcessing(newUser)) {
                    // user is higher priority
                    preemptions++;                    
                    
                    try {
                        userQueue.insert(newUser);
                    } catch (JEQNQueueOverflowException ex) {
                        overflownUserHandler(newUser);
                    }
                    
                    meanQueueLength.insertNewSample((userQueue.getEnqueuedUsers() - 1), (getClock().getValue()/*event.getTime().getValue()*/ - lastUserMovementTime));                  
                    
                    if (maxQueueLength < userQueue.getEnqueuedUsers()) maxQueueLength = userQueue.getEnqueuedUsers();
                                        
                    Time delay = userQueue.nextUserExtractingTime();                                        
                    
                    meanWaitingTime.insertNewSample(delay.getValue());

                    send(outPort, sendingAheadDelay, Events.NEW_INCOMING_USER, userQueue.extract());
                } else {
                    // user is lower priority, just enqueue it
                    try {
                        userQueue.insert(newUser);
                    } catch (JEQNQueueOverflowException ex) {
                        overflownUserHandler(newUser);
                    }                    
                    meanQueueLength.insertNewSample((userQueue.getEnqueuedUsers() - 1), (getClock().getValue() /*(event.getTime().getValue()*/ - lastUserMovementTime));                                       
                    if (userQueue.getEnqueuedUsers() > maxQueueLength) maxQueueLength = userQueue.getEnqueuedUsers();
                }
            }            
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationError(ex);
        }
    }

    /**
     * Handles unexpected events according to {@code WaitingSystem}: if {@code REENQUEUE_USER} 
     * it reenqueues else the {@code UnexpectedEventReceivedException} is thrown.
     *
     * In this class implementation, this method handles the re-enqueueing of users that have
     * been interrupted while being serviced.
     * 
     * @param e The event
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNException Not actually thrown
     */
    protected void otherEventHandler(final Event e) throws JEQNException {              
        if (e.getTag().equals(Events.REENQUEUE_USER)) {            
            try {
                CountingUser user = (CountingUser) e.getData();            
                user.count();
                user.setInComingTime(getClock().getValue()); //e.getTime().getValue());
            
                userQueue.insert(user);
                
                meanQueueLength.insertNewSample((userQueue.getEnqueuedUsers() - 1), (getClock().getValue()/*e.getTime().getValue()*/ - lastUserMovementTime));                
            } catch (JEQNQueueOverflowException ex) {
                overflownUserHandler(null);
            }
     
            if (userQueue.getEnqueuedUsers() > maxQueueLength) maxQueueLength = userQueue.getEnqueuedUsers();
            
            reenqueueings++;     
            
            lastUserMovementTime = lastUserComingInTime = e.getTime().getValue();
        } else {
            throw new JEQNUnexpectedEventReceivedException(e);
        }        
    }

    /**
     * Handles the overflowing users in the case of user queue overflow
     * @param newUser The user overflown
     */
    protected void overflownUserHandler(CountingUser newUser) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }   
    
    public void printStatistics() {        
        super.printStatistics();
        System.out.println("Preemptions   :  " + preemptions);
        System.out.println("Reenqueueings :  " + reenqueueings);
    }
    
    // Accessor methods
    
    /**
     * Accessor method for the {@code nextRequestUserPort}, the port used to request by
     * the following entity to request the next user to be served
     * @return The port
     */
    public InPort getNextRequestUserPort() {
        return nextRequestUserPort;
    }   
        
    /**
     * Accessor method for the {@code nextRequestUserPort}, the port used to request by
     * the following entity to request the next user to be served
     * @param p The port
     */
    public void setNextRequestUserPort(InPort p) {
        nextRequestUserPort = p;
    }

    private void setPreemptionPolicy(MaskBasePolicy<User, User, ?, Boolean> p) {
        preemptionPolicy = p;
    }
    
    private void setPreemptions(final int i) {
        preemptions = i;
    }
    
    private void setReenqueueings(final int i) {
        reenqueueings = i;
    }
}
