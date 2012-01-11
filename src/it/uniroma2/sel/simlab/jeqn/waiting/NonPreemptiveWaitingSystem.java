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
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNQueueOverflowException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.requests.ServiceRequestGenerator;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;

import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;

/** Implements a Non-Preemptive Waiting System, ie a waiting system that does not
 * operate service interruption on the cascade Service Center in the case of reception of
 * high priority users
 *
 * @author  Daniele Gianni
 */
public class NonPreemptiveWaitingSystem extends WaitingSystem {

    // enables the collection and printing out of statistics
    private static final Boolean STATS = true;

    /*
     * name of the port through which the waiting system receives requests for the next user to process
     */
    public static final String NEXT_REQUEST_USER_PORT_NAME = "nextRequestUser";

    /**
     * The port to be connected to the next entity
     */
    protected InPort nextRequestUserPort;

    /**
     * Creates a new instance of WaitingSystem
     * @param name The entity name
     * @param timeFactory The time Factory
     * @param layer2factory 
     * @param userQueue 
     * @param requestGenerator 
     * @throws it.uniroma2.info.sel.simlab.simarch.exceptions.InvalidNameException 
     */
    public NonPreemptiveWaitingSystem(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final UserQueue userQueue, final ServiceRequestGenerator requestGenerator, final double sendingAheadDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userQueue, requestGenerator, sendingAheadDelay);
        setNextRequestUserPort(new InPort(name, this));
    }

    /**
     * Accessor method for property {@code nextRequestUserPort}
     * @return the port
     */
    public InPort getNextRequestUserPort() {
        return nextRequestUserPort;
    }

    /*
     * Handles the event of a user arriving from another center
     */
    protected void newIncomingUserEventHandler(Event event) { //throws JEQNQueueOverflowException {

        //System.out.println("Users currently in the system : " + this.userQueue.getEnqueuedUsers());
        try {
            User toInsert = (User) event.getData();

            toInsert.setInComingTime(event.getTime().getValue());
            resourceRequestGenerator.assignResourceRequest(toInsert);
            
            // enqueueing
            try {
                userQueue.insert(toInsert);
            } catch (JEQNQueueOverflowException ex) {
                overflownUserHandler(toInsert);
            }

            // if a next user request has already been received and not yet satisfied
            if (isInSendOnComingInState()) {
                // send the user directly to the cascade center
                Time delay = userQueue.nextUserExtractingTime();
                send(outPort, delay, Events.NEW_INCOMING_USER, userQueue.extract());

                setState(WaitingSystemStates.SEND_ON_REQUEST);

                meanQueueLength.insertNewSample(0, event.getTime().getValue() - lastUserMovementTime);
                meanWaitingTime.insertNewSample(delay.getValue());
            } else {
                // simply enqueue the user
                meanQueueLength.insertNewSample((userQueue.getEnqueuedUsers() - 1), (event.getTime().getValue() - lastUserMovementTime));

                if (maxQueueLength < userQueue.getEnqueuedUsers()) {
                    maxQueueLength = userQueue.getEnqueuedUsers();
                }
            }
            
            lastUserMovementTime = event.getTime().getValue();

        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationError(ex);
        }
    }

    /**
     * Accessor method for property {@code nextRequestUserPort}
     * @param p the port
     */
    public void setNextRequestUserPort(InPort p) {
        nextRequestUserPort = p;
    }

    protected void otherEventHandler(Event event) throws JEQNUnexpectedEventReceivedException {
        throw new JEQNUnexpectedEventReceivedException(event);
    }

    protected void overflownUserHandler(User toInsert) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void printStatistics() {
        if (STATS) {
            super.printStatistics();
        }
    }
}
