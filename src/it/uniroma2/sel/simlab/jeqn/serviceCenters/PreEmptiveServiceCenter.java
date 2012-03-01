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

package it.uniroma2.sel.simlab.jeqn.serviceCenters;

import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNConfigurationException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.users.CountingUser;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Implements an EQN Service Center that allows to interrupt the processing of a user as an higher priority user incomes.
 *
 * @author Daniele Gianni
 */
public class PreEmptiveServiceCenter extends ServiceCenter {
    
    // enables the printing and collaction of statistics
    private static final Boolean STATS = true;
    
    // the time the user is received
    private Time userReceivedTime;

    // the output port for the cascade entity
    private OutPort queuePort;

    // the delay of re-enqueuing for a user interrupted
    private Time sendingBackDelay;

    // statistics
    /*
     * the number of users entirely processed
     */
    protected int fullyProcessedUsers;

    /*
     * all the users received
     */
    protected int allUsers;

    /*
     * number of preemptive interruptions
     */
    protected int preemptions;

    /*
     * re-enqueuings stochastic variable
     */
    protected DiscretePopulationMean reenqueueings;
    
    /** 
     * 
     *
     * Creates a new PreEmptiveServiceCenter with the specified delays
     * 
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param sendingAheadDelay	{@code Time} object that contains the delay introduced when sending a processed users to the next entity.
     * @param requestDelay	{@code Time} object that contains the delay introduced when sending a partially processed user back to the previous entity. 
     * @param sendingBackDelay {@code Time} object that contains the delay introduced to re-enqueuing an interrupted user.
     * @throws InvalidNameException	An InvalidNameException is raised when an issue concerning the element name occurs.
     *
     */
    public PreEmptiveServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final Time requestDelay, final Time sendingAheadDelay, final Time sendingBackDelay) throws InvalidNameException {        
        super(name, timeFactory, factory, sendingAheadDelay, requestDelay);
        
        setSendingBackDelay(sendingBackDelay);            
        
        queuePort = new OutPort(new JEQNName(INCOMING_USERS_PORT_NAME), this);
        
        fullyProcessedUsers = 0;
        allUsers = 0;
        preemptions = 0;
        reenqueueings = new DiscretePopulationMean();
        
    }
    
    public void body() throws JEQNException {        
        
        Event event;
        
        try {                 
            while (true) {                
                //System.out.println("[PSC] Ask for a new user to serve at time " + getClock().getValue());
                send(requestUsersPort, requestUserDelay, Events.REQUESTED_NEXT_USER, new Integer(0));
                
                do {
                    event = nextEvent();                   
                } while (!event.getTag().equals(Events.NEW_INCOMING_USER));
                                
                CountingUser user = (CountingUser) event.getData();
                userReceivedTime = event.getTime();
                Time serviceRequest = user.getServiceRequest().getValue();
                Time serviceEndTime = userReceivedTime.increasedBy(serviceRequest);
                //System.out.println("[PSC] Received a new user to serve at time " + getClock().getValue() + " Service Request " + serviceRequest.getValue());
                //System.out.println("[PSC]       End Time " + serviceEndTime.getValue() + " User : + NAME : " + user.getName());//" + user.toString()); 
                
                allUsers++;               
                unsetEventReceived();
                
                //System.out.println("[PSC] Starting to serve the user");
                while (holdUnlessIncomingEvent(serviceRequest)) {                    
                    //System.out.print("[PSC]   Sono interrotto a t == " + + getClock().getValue());

                    // the event that break the processing of the current user
                    Event breakingEvent = (Event) getReceivedEvent();
                    
                    if (breakingEvent.getTag().equals(Events.NEW_INCOMING_USER)) {                                                
                        //System.out.println("[PSC] Interrupted by a new higher priority user at time " + getClock().getValue() + " Service End Time " + serviceEndTime.getValue() + " NAME : " + user.getName());
                        
                        allUsers++;
                        if (serviceEndTime.isGreaterThan(getClock())) {                            
                            //update service request and send back to the queue                           
                            
                            preemptions++;
                            user.getServiceRequest().setValue(serviceEndTime.decreasedBy(getClock()));
                            send(queuePort, sendingBackDelay, Events.REENQUEUE_USER, user);
                            
                            //System.out.println("[PSC] Current user still needs service time (send it back) " + user.getServiceRequest().getValue() + " NAME : " + user.getName());                            
                            //System.out.println("[PSC] NEW USER : " + ((CountingUser) getReceivedEvent().getData()).getName());
                            
                            user = ((CountingUser) getReceivedEvent().getData());
                            serviceRequest = ((CountingUser) getReceivedEvent().getData()).getServiceRequest().getValue();
                            userReceivedTime.setValue(getClock().getValue());
                            serviceEndTime = userReceivedTime.increasedBy(serviceRequest);
                            //System.out.println("==> PSC nuova richiesta : " + serviceRequest.getValue());                            
                        } else {
                            //System.out.println(" finito processamento, mando avanti!");
                            fullyProcessedUsers++;
                            send(nextEntityPort, sendingAheadDelay, Events.NEW_INCOMING_USER, user);
                            
                            reenqueueings.insertNewSample(user.getCounter());
                            //System.out.println("[PSC] Current user service request has been completed. Send it ahead.");
                            //System.out.println("[PSC] User Request ending time == " + serviceEndTime.getValue() + " CK " + getClock().getValue() + " NAME : " + user.getName());
                            //System.out.println("[PSC] NEW USER : " + ((CountingUser) getReceivedEvent().getData()).getName());
                            
                            serviceRequest = ((CountingUser) getReceivedEvent().getData()).getServiceRequest().getValue();
                            userReceivedTime.setValue(getClock().getValue());
                            serviceEndTime = userReceivedTime.increasedBy(serviceRequest);
                            
                            user = ((CountingUser) getReceivedEvent().getData());
                        }                        
                    } else {                        
                        serviceRequest.setValue(serviceRequest.getValue() - (getClock().getValue() - userReceivedTime.getValue()));
                        //System.out.println("[PSC] Unexpected event. Keep working on the current user ( " + serviceRequest.getValue() + " )");
                    }
                }
                                
                //System.out.println("[PSC] User service request completely satisfied with NO INTERRUPTIONS " + " NAME : " + user.getName());
                fullyProcessedUsers++;
                reenqueueings.insertNewSample(user.getCounter());
                send(nextEntityPort, sendingAheadDelay, Events.NEW_INCOMING_USER, user.unWrap());                
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationException(ex);
        }
    }
    
    public OutPort getQueuePort() {
        return queuePort;
    }
    
    public void printStatistics() {

    	if (STATS) {
    		System.out.println("Preemptive Service Center " + getEntityName() + "\n");

    		System.out.println("Number of users received        : " + allUsers);
    		System.out.println("Fully processed Users           : " + fullyProcessedUsers);
    		System.out.println("Preemptions                     : " + preemptions);  
    		System.out.println("Reenqueueings mean              : " + reenqueueings.meanValue());
    		System.out.println("Reenqueueings sample size       : " + reenqueueings.sampleSize());
    	}
    }

    /*
     * this method is currently empty and left only for consistency with interface defined by the superclass
     */
    protected void process(final User u) {
        /*
        Time processingStart = getClock();
        
        holdUnlessIncomingEvent(u.getServiceRequest().getValue());
        
        Time processingEnd = getClock();
        
        Time actualProcessingTime = processingEnd.decreasedBy(processingStart);
        
        try {
        if (actualProcessingTime.isLesserThan(u.getServiceRequest().getValue())) {            
            u.getServiceRequest().getValue().decreaseBy(actualProcessingTime);
            send(queuePort, sendingBackDelay, Events.NEW_INCOMING_USER, u);
            
            preemptiveUser = true;
            
            //this.h
        } else {
            send(nextEntityPort, sendingAheadDelay, Events.NEW_INCOMING_USER, u);
            
            preemptiveUser = false;
        }    
        } catch (final UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (final TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        }*/
    }
    
    protected void setSendingBackDelay(final Time t) {
        sendingBackDelay = t;
    }
}
