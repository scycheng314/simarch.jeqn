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

package it.uniroma2.sel.simlab.jeqn.printers;


import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;

import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Implements a Sink jEQN simulation component that also prints the details of the
 * user being dismissed.
 *
 *   @see  it.uniroma2.sel.simlab.jeqn.general.JEQNElement
 *   @see  it.uniroma2.sel.simlab.simcomp.basic.entities.BasicComponentLevelEntity 
 * 	 @see  it.uniroma2.sel.simlab.simarch.data.ComponentLevelEntity 
 *	 @see  it.uniroma2.sel.simlab.simarch.interfaces.Layer2ToLayer3
 *
 * @author  Daniele Gianni
 */
public final class PrinterSink extends JEQNElement {
  
    private static final String IN_PORT_NAME ="inPort";
    
    private InPort inPort;
        
    private Time lastIncomingUserTime;
    private DiscretePopulationMean interarrivalMean;
    //private DiscretePopulationVariance interarrivalVariance;
        
    /**
     * Creates a new PrinterSink element.
     * 
     * @param	name	Element name. The name is used to identify entities within the simulation model.
     * @param	timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param	factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @throws	InvalidNameException	An InvalidNameException is raised when an issue concerning the element name occurs.
     */
    public PrinterSink(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory) throws InvalidNameException {        
        super(name, timeFactory, factory);   
        
        setInPort(new InPort(new JEQNName(IN_PORT_NAME), this));        
        lastIncomingUserTime = timeFactory.makeFrom(Time.ZERO);        
        interarrivalMean = new DiscretePopulationMean();
        //interarrivalVariance = new DiscretePopulationVariance();
    }
    
    /**
     * Contains the simulation logic of the element
     */
    public void body() throws JEQNException {
        Event event;        
        
        while (true) {
            event = nextEvent();                        
            interarrivalMean.insertNewSample(event.getTime().decreasedBy(lastIncomingUserTime).getValue());
            //interarrivalVariance.insertNewSample(event.getTime().doubleValue() - lastIncomingUserTime);            
            lastIncomingUserTime = event.getTime();  
            
            User u = (User) event.getData();
            System.out.println("Received user : " + u.toString() + " at Time : " + getClock().getValue());
        }
    }
    
    /**
     * Returns the input port of the element.
     * @return	input port.
     */
    public InPort getInPort() {
        return inPort;
    }
    
    /**
     * Sets the input port of the element.
     * @param	p	input port.
     */
    private void setInPort(InPort p) {
        inPort = p;
    }
    
    /**
     * Prints the statistics data gathered by the component during the simulation.
     */
    public void printStatistics() {
        System.out.println("#### Sink " + getEntityName() + "\n");
        System.out.println("Users destroyed                                     : " + interarrivalMean.sampleSize());
        System.out.println("Sampling Mean interarrivalTime                      : " + interarrivalMean.meanValue());
        System.out.println("Variance of Sampling Mean interarrivalTime          : " + interarrivalMean.variance());
        System.out.println("Confidence interval a = 0.9                         : " + interarrivalMean.confidenceInterval(0.9));
        
        //System.out.println("Sampling Variance of interarrivalTime               : " + interarrivalVariance.meanValue());
        //System.out.println("Variance of Sampling Variance of interarrivalTime   : " + interarrivalVariance.variance());
        //System.out.println("Confidence interval a = 0.9                         : " + interarrivalVariance.confidenceInterval(0.9));  
        System.out.println("=====================================\n");
    }    
}
