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
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.users.TrackingPathUser;

import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;
import java.lang.reflect.Method;

/** Performs a specified operation upon the reception of a user. The operation is specified
 * as reference to a Java method having no parameters.
 *
 * This class is currently semplified and the method is coded internally for the NET-BES scenario.
 * @author Daniele Gianni
 */
public class OperationNode extends SpecialNode {

    // method to be invokated upon receiving a user
    private Method method;

    // statistics
    private DiscretePopulationMean interarrivalTime;

    // updater of the Quality of Service metrics - field specific for the NETBES scenario
    private QoSUpdater qosUpdater;
    
    /** Creates a new instance of SetNode */
    public OperationNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, QoSUpdater qosu) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);
        
        setMethod(method);        
        interarrivalTime = new DiscretePopulationMean();        
        qosUpdater = qosu;
    }    
    
    public void body() throws JEQNException {
        
        Event event;
        double lastUserTime = 0.0;
        
        try {
            while (true) {                
                event = nextEvent();                
                TrackingPathUser user = (TrackingPathUser) event.getData();                
                
                // method invokation currently disabled

                //qosUpdater.updateQoS(user, event.getTime().getValue());
                //method.invoke(user); 
                
                interarrivalTime.insertNewSample(event.getTime().getValue() - lastUserTime);
                lastUserTime = event.getTime().getValue();
                
                send(outPort, userForwardDelay, Events.NEW_INCOMING_USER, user);
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            throw new JEQNError(ex);
        } 
//        catch (IllegalAccessException ex) {
//            ex.printStackTrace();
//            throw new JEQNError(ex);
//        } catch (InvocationTargetException ex) {
//            ex.printStackTrace();
//            throw new JEQNError(ex);
//        }                
    }
    
    public void printStatistics() {
        System.out.println("### Operation Node " + getEntityName() + " : ");
        System.out.println("\n\n");
        
        System.out.println("User interarrival time : " + interarrivalTime.meanValue());
        System.out.println("User passed through    : " + interarrivalTime.sampleSize());        
    }
    
    private void setMethod(final Method m) {
        method = m;
    }           
}
