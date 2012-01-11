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


import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;

import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** Defines the EQN Sink entity. The Sink is classified as Service Center as it can
 * be emulated by a Service Center with infinite service time
 *
 * @author  Daniele Gianni
 */
public final class Sink extends JEQNElement {

    // enables the printing and collection of statistics
    private static final Boolean STATS = true;

    // the name of the port for incoming users
    public static final String IN_PORT_NAME ="inPort";

    // the port for incoming users
    private InPort inPort;

    // statistics
    /*
     * the time of last incoming user
     */
    private Time lastIncomingUserTime;

    /*
     * user interarrival time at the sink
     */
    private DiscretePopulationMean interarrivalMean;
    //private DiscretePopulationVariance interarrivalVariance;
        
    /** Creates a new instance of Sink */
    public Sink(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory) throws InvalidNameException {        
        super(name, timeFactory, factory);   
        
        setInPort(new InPort(new JEQNName(IN_PORT_NAME), this));        
        lastIncomingUserTime = timeFactory.makeFrom(Time.ZERO);        
        interarrivalMean = new DiscretePopulationMean();
        //interarrivalVariance = new DiscretePopulationVariance();
    }

    public void body() throws JEQNException {
        Event event;        
        
        while (true) {
            event = nextEvent();                        
            interarrivalMean.insertNewSample(event.getTime().decreasedBy(lastIncomingUserTime).getValue());
            //interarrivalVariance.insertNewSample(event.getTime().doubleValue() - lastIncomingUserTime);            
            lastIncomingUserTime = event.getTime();            
        }
    }
    
    public InPort getInPort() {
        return inPort;
    }
    
    private void setInPort(InPort p) {
        inPort = p;
    }
    
    public void printStatistics() {

    	if (STATS) {       
            
    		if (interarrivalMean.sampleSize() > 0) {

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
    }
}
