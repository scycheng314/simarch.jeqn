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
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.users.User;

import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Implements Service Center that operates with Non-Preemptive policy, i.e. the processing of a user is
 * not interrupted by incoming user with higher priorities
 *
 * @author  Daniele Gianni
 */
public class NonPreemptiveServiceCenter extends ServiceCenter {    
    
    //enables the printing and collecting of statistics
    private static final Boolean STATS = true;
	
    // statistics
    /* the time of last incoming user
     *
     */
    protected double lastIncomingUserTime;

    /* average length of mean busy cycles - ie a cycle delimited by two time in which the center is idle
     *
     */
    protected DiscretePopulationMean busyCycleTimeMean;

    /* number of users processed within a busy cycle
     *
     */
    protected DiscretePopulationMean usersPerBusyCycleMean;

    /* average length of idle cycles - ie a cycle delimited by two time in which the center is busy
     *
     */
    protected DiscretePopulationMean idleCycleTimeMean;           
    
    //protected DiscretePopulationVariance busyCycleTimeVariance;
    //protected DiscretePopulationVariance idleCycleTimeVariance;

    // internal variable to determine the previous state (busy or idle) or the center
    private int branch;

    /*
     * total busy time
     */
    protected double busyTime;

    /*
     * total idle time
     */
    protected double idleTime;

    /*
     * start time of the last busy cycle, which coincides with the end time of last idle cycle
     */
    protected double busyCycleStartTime;

    /* start time of the last idle cycle, which coincides with the end time of last busy cycle
     *
     */
    protected double idleCycleStartTime;

    /*
     * end time of last busy cycle
     */
    protected double busyCycleEndTime;

    /*
     * end time of last idle cycle
     */
    protected double idleCycleEndTime;

    /*
     * number of users currently being processe
     */
    protected int usersInCurrentBusyCycle;

    /*
     * number of users in the total busy time
     */
    protected int usersInSample;

    /*
     * number of users currently being processed
     */
    protected boolean userInProcessing;
    
    /**
     * Creates a new instance of NonPreemptiveServiceCenter
     */
    public NonPreemptiveServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory) throws InvalidNameException {        
        super(name, timeFactory, factory, timeFactory.makeFrom(Time.ZERO), timeFactory.makeFrom(Time.ZERO));
        
        init();        
    }

    /**
     * Creates a new instance of NonPreemptiveServiceCenter
     */
    public NonPreemptiveServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final Time sendingAheadDelay, final Time requestDelay) throws InvalidNameException {
        super(name, timeFactory, factory, sendingAheadDelay, requestDelay);

        init();
    }

    /**
     * Creates a new instance of NonPreemptiveServiceCenter
     */
    public NonPreemptiveServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final double sendingAheadDelay, final double requestDelay) throws InvalidNameException {        
        super(name, timeFactory, factory, timeFactory.makeFrom(sendingAheadDelay), timeFactory.makeFrom(requestDelay));
        
        init();
    }

    /**
     * Creates a new instance of NonPreemptiveServiceCenter
     */
    public NonPreemptiveServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final double sendingAheadDelay) throws InvalidNameException {
        super(name, timeFactory, factory, timeFactory.makeFrom(sendingAheadDelay), timeFactory.makeFrom(Time.ZERO));

        init();
    }

    /**
     * Creates a new instance of NonPreemptiveServiceCenter
     */
    public NonPreemptiveServiceCenter(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final Time sendingAheadDelay) throws InvalidNameException {        
        super(name, timeFactory, factory, sendingAheadDelay, timeFactory.makeFrom(Time.ZERO));
        
        init();
    }

    // initialization of this component
    private void init() {
        lastIncomingUserTime = 0;
        
        busyCycleTimeMean = new DiscretePopulationMean();
        usersPerBusyCycleMean = new DiscretePopulationMean();
        
        idleCycleTimeMean = new DiscretePopulationMean();
        
        interarrivalTime = new DiscretePopulationMean();
        
        serviceTimeMean = new DiscretePopulationMean();
        
        //busyCycleTimeVariance = new DiscretePopulationVariance();
        //idleCycleTimeVariance = new DiscretePopulationVariance();

        // branch == 1, mean initial state is idle
        branch = 1;
        busyTime = 0.0;
        idleTime = 0.0;           
                
        busyCycleStartTime = 0.0;        
        idleCycleStartTime = 0.0;
        
        busyCycleEndTime = 0.0;        
        idleCycleEndTime = 0.0;
        
        usersInCurrentBusyCycle = 0;
        usersInSample = 0;
        
        userInProcessing = false;
    }
    
    public void body() throws JEQNException {
        
        double idleCycle = 0.0;
        double busyCycle = 0.0;                               
        
        Event event;
        User user;
        
        Time lastUserIncomingTime = timeFactory.makeFrom(Time.ZERO);
        try {
            
            while (true) {
                idleCycleStartTime = getClock().getValue();
                
                send(requestUsersPort, requestUserDelay, Events.REQUESTED_NEXT_USER, new Integer(0));
                event = nextEvent();
                
                if (event.getTag().equals(Events.NEW_INCOMING_USER)) {

                    idleCycleEndTime = getClock().getValue();
                    
                    interarrivalTime.insertNewSample(getClock().decreasedBy(lastUserIncomingTime).getValue());
                    
                    lastUserIncomingTime = getClock();
                    
                    if (idleCycleEndTime == idleCycleStartTime) {
                        // condition met when the busy cycle is not broken by an idle interval
                        branch = 1;
                        
                        userInProcessing = true;
                        process((User) event.getData());
                        userInProcessing = false;
                        
                        usersInCurrentBusyCycle++;
                        busyCycleEndTime = getClock().getValue();
                    } else {
                        // condition met when the idle cycle is broken and a busy cycle starts
                        branch = 2;                                                
                        
                        idleCycle = idleCycleEndTime - idleCycleStartTime;
                        idleTime += idleCycle;
                        idleCycleTimeMean.insertNewSample(idleCycle);
                        //idleCycleTimeVariance.insertNewSample(idleCycle);
                        
                        busyCycle = busyCycleEndTime - busyCycleStartTime;
                        
                        busyTime += busyCycle;
                        busyCycleTimeMean.insertNewSample(busyCycle);
                        
                        usersInSample += usersInCurrentBusyCycle;
                        usersPerBusyCycleMean.insertNewSample((double ) usersInCurrentBusyCycle);
                        
                        busyCycleStartTime = getClock().getValue();
                        
                        usersInCurrentBusyCycle = 0;
                        
                        userInProcessing = true;
                        process((User) event.getData());
                        userInProcessing = false;
                        
                        usersInCurrentBusyCycle = 1;
                        
                        busyCycleEndTime = getClock().getValue();
                    }
                } else {
                    throw new JEQNUnexpectedEventReceivedException(event);
                }
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationException(ex);
        }
    }    
    
    public void printStatistics() {

    	if (STATS) {

    		if (usersProcessed > 0) {

    			double exIdleTime = idleTime;
    			double exBusyTime = busyTime;

    			if (branch == 1) {
    				if (userInProcessing) {
    					// stats are not computed as the current busy cycle is not complete                
    					busyTime += getClock().getValue() - busyCycleStartTime;
    				} else {
    					busyTime += getClock().getValue() - busyCycleStartTime; 

    					busyCycleEndTime = getClock().getValue();
    					double busyCycle = busyCycleEndTime - busyCycleStartTime;                 
    					busyCycleTimeMean.insertNewSample(busyCycle);  

    					usersInSample += usersInCurrentBusyCycle;
    					usersPerBusyCycleMean.insertNewSample((double ) usersInCurrentBusyCycle);

    					idleTime += getClock().getValue() - idleCycleStartTime;  
    				}
    			} else {

    				if (userInProcessing) {
    					//System.out.println("Branch 2 USER IN PROCESSING");
    					busyTime += getClock().getValue() - busyCycleStartTime;                
    				} else {
    					//System.out.println("Branch 2 USER NOT IN PROCESSING");
    					double busyCycle = busyCycleEndTime - busyCycleStartTime;   
    					busyTime += busyCycle;                 

    					busyCycleTimeMean.insertNewSample(busyCycle);  

    					usersInSample += usersInCurrentBusyCycle;
    					usersPerBusyCycleMean.insertNewSample((double ) usersInCurrentBusyCycle);

    					idleTime += getClock().getValue() - idleCycleStartTime; 
    				}            
    			}                          

    			/*********************
    			 *
    			 *
    			 * Review statistics
    			 *
    			 *
    			 *********************/        

    			System.out.println("### NoPreemptiveServiceCenter " + getEntityName() + "\n");
    			System.out.println("Observation interval                : [ 0 ; " + (exIdleTime + exBusyTime) + " ] ");
    			System.out.println("Users in the center                 : " + (userInProcessing? 1: 0));
    			System.out.println("Users processed                     : " + usersProcessed);        
    			System.out.println("Mean interarrival time              : " + interarrivalTime.meanValue());
    			System.out.println("Variance of mean interarrival time  : " + interarrivalTime.variance());        
    			System.out.println("Center utilization                  : " + (busyTime / (busyTime + idleTime)));
    			System.out.println("Mean service time                   : " + serviceTimeMean.meanValue());

    			System.out.println("");        

    			System.out.println("Total Busy Time                                 : " + busyTime);
    			System.out.println("Busy Time in Busy Cycles                        : " + exBusyTime);
    			System.out.println("Sampling Busy Cycle Mean Time                   : " + busyCycleTimeMean.meanValue());
    			System.out.println("Variance of Sampling Busy Cycle Mean Time       : " + busyCycleTimeMean.variance());
    			System.out.println("Confidence interval a = 0.9                     : " + busyCycleTimeMean.confidenceInterval(0.9));
    			System.out.println("Number of Busy Cycle                            : " + busyCycleTimeMean.sampleSize());
    			System.out.println("User in the sample                              : " + usersInSample);
    			System.out.println("Average users per busy cycle                    : " + usersPerBusyCycleMean.meanValue());

    			//System.out.println("Sampling Busy Cycle Variance Time               : " + busyCycleTimeVariance.meanValue());
    			//System.out.println("Variance of Sampling Busy Cycle Variance Time   : " + busyCycleTimeVariance.variance());
    			//System.out.println("Confidence interval a = 0.9                     : " + busyCycleTimeVariance.confidenceInterval(0.9));        

    			System.out.println("");
    			System.out.println("Total Idle Time                                 : " + idleTime);
    			System.out.println("Idle Time in Idle Cycles                        : " + exIdleTime);
    			System.out.println("Sampling Idle Cycle Mean Time                   : " + idleCycleTimeMean.meanValue());
    			System.out.println("Variance of Sampling Idle Cycle Mean Time       : " + idleCycleTimeMean.variance());
    			System.out.println("Confidence interval a = 0.9                     : " + idleCycleTimeMean.confidenceInterval(0.9));
    			System.out.println("Number of Idle Cycle                            : " + idleCycleTimeMean.sampleSize());
    			//System.out.println("Sampling Idle Cycle Variance Time               : " + idleCycleTimeVariance.meanValue());
    			//System.out.println("Variance of Sampling Idle Cycle Variance Time   : " + idleCycleTimeVariance.variance());
    			//System.out.println("Confidence interval a = 0.9                     : " + idleCycleTimeVariance.confidenceInterval(0.9)); 
    			System.out.println("=====================================\n\n\n");
    		}
    	}
    }

    protected void process(final User u) throws JEQNException {
                        
        Time sendingDelay = sendingAheadDelay.increasedBy(u.getServiceRequest().getValue());
        
        if (u.getServiceRequest().getValue().getValue() <= 0) {
            System.err.println("User negative time request : " + u.getServiceRequest().getValue().getValue());
            System.err.println("User name " + u.getName());
            System.err.println("Current time " + getClock());            
        }
        try {
            /*
            System.out.println("User service time " + u.getServiceRequest().getValue().getValue());
            System.out.println("sending Delay " + sendingDelay.getValue());
             */
            send(nextEntityPort, sendingDelay, Events.NEW_INCOMING_USER, u);        
            hold(u.getServiceRequest().getValue());
            usersProcessed++;            
            serviceTimeMean.insertNewSample(u.getServiceRequest().getValue().getValue());
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationException(ex);
        } catch (NullPointerException ex) {
        	ex.printStackTrace();
        	System.err.println(getFullName() + " " + nextEntityPort.getName());
        	System.exit(-1);
        }
    }
           
}
