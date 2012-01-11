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

import it.uniroma2.sel.simlab.generalLibrary.dataStructures.SortedList;
import it.uniroma2.sel.simlab.generalLibrary.dataStructures.UnableToInsertException;
import it.uniroma2.sel.simlab.jeqn.errors.JEQNError;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.requests.ServiceRequestGenerator;
import it.uniroma2.sel.simlab.jeqn.users.User;

import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Defines Infinite Server simulation entity
 *
 * @author Daniele Gianni
 */
public class InfiniteServer extends ServiceCenter {

    // enables the printing and collection of statistics
    private static final Boolean STATS = true;

    // generates the time request for each incoming user
    private ServiceRequestGenerator serviceRequestGenerator;
    
    //statistics
    // the processing end times, for the determination of the number of user in processing state
    private SortedList endProcessings;

    // number of users received by this center
    private int usersReceived;
    
    /** Creates a new instance of InfiniteServer */
    public InfiniteServer(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final ServiceRequestGenerator generator, final Time sendingAheadDelay) throws InvalidNameException {        
        super(name, timeFactory, factory, sendingAheadDelay, timeFactory.makeFrom(0.0));      
        setServiceRequestGenerator(generator);
        endProcessings = new SortedList();
        usersReceived = 0;
        
        serviceTimeMean = new DiscretePopulationMean();
        interarrivalTime = new DiscretePopulationMean();
    }
    
    public void body() throws JEQNException {        
        Event event;
        
        double lastUserTime = 0.0;
        
        while (true) {            
            event = nextEvent();
            
            if (event.getTag().equals(Events.NEW_INCOMING_USER)) {
                interarrivalTime.insertNewSample(event.getTime().getValue() - lastUserTime);
                lastUserTime = event.getTime().getValue();
    
                usersProcessed = 0;

                // counts how many users have been processed since the last incoming user
                for (Object o : endProcessings) {
                    Time t = (Time) o;
                    
                    if (t.isLesserOrEqualThan(event.getTime())) {
                        usersProcessed++;
                        t = null;
                        //endProcessings.remove(o);
                    }
                }                
                
                usersReceived++;
                process((User) event.getData());
            } else {
                throw new JEQNUnexpectedEventReceivedException(event);
            }           
        }        
    }

    /**
     * Simulate the user processing by forwarding the use to the cascade entity
     * with the request time delay
     * @param u user to process
     * @throws JEQNException
     */
    protected void process(final User u) throws JEQNException {        
        serviceRequestGenerator.assignResourceRequest(u);
        
        serviceTimeMean.insertNewSample(u.getServiceRequest().getValue().getValue());
        Time sendingDelay = u.getServiceRequest().getValue().increasedBy(sendingAheadDelay);

        // keeps track of the end processing times
        try {            
            endProcessings.add(sendingDelay);
        } catch (UnableToInsertException ex) {
            ex.printStackTrace();
        }
        
        try {
            send(nextEntityPort, sendingDelay, Events.NEW_INCOMING_USER, u);
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (NullPointerException ex) {
        	ex.printStackTrace();
        	System.err.println(getFullName() + " " + nextEntityPort.getName());
        	System.exit(-1);
        }
    }

    public ServiceRequestGenerator getServiceRequestGenerator() {
        return serviceRequestGenerator;
    }

    public void printStatistics() {        

    	if (STATS) {

    		if (interarrivalTime.sampleSize() > 0) {
    			System.out.println("Infinite Server " + getEntityName() + " : ");        
    			System.out.println("\n\n");

    			System.out.println("User interarrival time      : " + interarrivalTime.meanValue());
    			System.out.println("Mean service request time   : " + serviceTimeMean.meanValue());
    			System.out.println("Users received              : " + usersReceived);
    			System.out.println("Processed users             : " + usersProcessed);
    			System.out.println("User under processing       : " + (usersReceived - usersProcessed));
    		}
    	}
    }
    
    public void setServiceRequestGenerator(final ServiceRequestGenerator g) {
        serviceRequestGenerator = g;
    }
}
