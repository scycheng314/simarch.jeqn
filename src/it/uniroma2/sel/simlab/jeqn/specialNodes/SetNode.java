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

package it.uniroma2.sel.simlab.jeqn.specialNodes;

import it.uniroma2.sel.simlab.jeqn.errors.JEQNError;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jrand.objectStreams.ObjectStream;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Sets a specified value into users passing through this node
 *
 * @author Daniele Gianni
 */
public class SetNode<T> extends SpecialNode {

    // the sequences of values to be set into the users
    private ObjectStream<? extends T> objectStream;

    // the name of the field in class User, to be set
    private String fieldName;
    
    // stats
    // user interarrival time
    private DiscretePopulationMean meanInterarrivalTime;
    
    /** Creates a new instance of SetNode */
    public SetNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final ObjectStream<? extends T> valueSequence, final String fieldName) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);
        
        setObjectStream(valueSequence);
        setFieldName(fieldName);
        
        meanInterarrivalTime = new DiscretePopulationMean();
    }
    
    public void body() throws JEQNException {
        
        Event event;
        
        double currentTime = 0.0;
        
        try {
            while (true) {
                event = nextEvent();
                
                if (event.getTag().equals(Events.NEW_INCOMING_USER)) {
                    User user = (User) event.getData();
                    
                    try {
                        // sets the value into the specified field
                        user.getClass().getField(fieldName).set(user, objectStream.getNext());
                    } catch (SecurityException ex) {
                        ex.printStackTrace();
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (NoSuchFieldException ex) {
                        ex.printStackTrace();
                    }
                    
                    try {
                        // forwards the user to the cascade entity
                        send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, user);
                    } catch (TimeAlreadyPassedException ex) {
                        ex.printStackTrace();
                        throw new JEQNTimeException(ex);
                    }
                    
                    meanInterarrivalTime.insertNewSample(event.getTime().getValue() - currentTime);
                    currentTime = event.getTime().getValue();
                } else {
                    throw new JEQNUnexpectedEventReceivedException(event);
                }
            }
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        }
    }

    public void printStatistics() {
        System.out.println("### Set  Node " + getEntityName() + "\n");
        System.out.println("Mean Interarrival Time                   : " + meanInterarrivalTime.meanValue());                
        //System.out.println("Variance of Mean Waiting Time             : " + meanInterarrivalTime.variance());
        System.out.println("Number of users passed through           : " + meanInterarrivalTime.sampleSize());
        System.out.println("\n\n");
    }
    
    private void setFieldName(final String s) {
        fieldName = s;
    }
    
    private void setObjectStream(final ObjectStream<? extends T> s) {
        objectStream = s;
    }        
}
