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

package it.uniroma2.sel.simlab.jeqn.sources;

import it.uniroma2.sel.simlab.jeqn.errors.JEQNConfigurationError;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;

import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskImplicitPolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.users.UserGenerator;

import it.uniroma2.sel.simlab.jrand.objectStreams.numericStreams.NumericStream;

import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;

import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;

import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

/** 
 * Implements an EQN Source element. It can be parameterize according to the 2 followin parameters:
 * 1. interarrival time
 * 2. jobclass of users that will be generated.
 *
 * @author Daniele Gianni
 */
public class Source extends JEQNElement {

    // enables the printing and collection of statistics
    private static final Boolean STATS = true;

    // output port name for generated users
    private static final String OUT_PORT_NAME = "out";

    //MaskStateOnlyDependentPolicy<S, D> extends
    /*
     * the policy that determinates the stop of the user generative process
     */
    protected MaskImplicitPolicy<?, ?, Boolean> terminationPolicy;

    /*
     * the numerical stream that specifies the inter-generation times between two users
     */
    protected NumericStream interarrivalTime;

    /*
     * the output port to be connected to the cascade entity
     */
    protected OutPort outPort;

    /*
     * the user generator, which instantiates the users according to the generator internal
     * characteristics
     */
    protected UserGenerator usersGenerator;
    
    // statistics info
    /*
     * user intearrival times - as they are observed in the user generative process
     */
    protected DiscretePopulationMean interarrivalMean;    
    //private DiscretePopulationVariance interarrivalVariance;    
    
    
    
    /** Creates a new Source
     * 
     * @param name Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.
     * @param layer2factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param ug	User generator
     * @param n		Numeric stream that implements the interarrival time.
     * @param terminationPolicy		policy that specifies the condition under which the user generation terminates.
     */    
    public Source(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final UserGenerator ug, final NumericStream n, final MaskImplicitPolicy<?, ?, Boolean> terminationPolicy ) throws InvalidNameException {                      
        super(name, timeFactory, layer2factory);
        
        setUsersGenerator(ug);
        setInterarrivalTime(n);
        setOutPort(new OutPort(new JEQNName(OUT_PORT_NAME), this));   
        setTerminationPolicy(terminationPolicy);
        
        interarrivalMean = new DiscretePopulationMean();
        //interarrivalVariance = new DiscretePopulationVariance();
    }
    
    /**
     * Contains the simulation logic of the element.
     */   
    public void body() throws JEQNException {                        
        Time nextUserBornTime;                               
        
        while (terminationPolicy.getDecision().booleanValue()) {                
            try {
                nextUserBornTime = timeFactory.makeFrom(interarrivalTime.getNext());

                User u = usersGenerator.getNextUser();
                u.setBornTime(getClock());
                send(outPort, nextUserBornTime, Events.NEW_INCOMING_USER, u);                              

                hold(nextUserBornTime);
                                
                interarrivalMean.insertNewSample(nextUserBornTime.getValue());
            } catch (TimeAlreadyPassedException ex) {
                ex.printStackTrace();
                throw new JEQNTimeException(ex);
            } catch (UnlinkedPortException ex) {
                ex.printStackTrace();
                throw new JEQNConfigurationError(ex);
            }
                //interarrivalVariance.insertNewSample(nextUserBornTime);                
        }
    }
    
    public OutPort getOutPort() {
        return outPort;
    }
     
    /**
     * Gets the interarrival time
     * @return	A Numeric stream that implements the interarrival time.
     */
    public NumericStream getInterarrivalTime() {
        return interarrivalTime;
    }
    
    /**
     * Sets the interarrival time
     * @param r	A Numeric stream that implements the interarrival time.
     */
    public void setInterarrivalTime(final NumericStream r) {
        interarrivalTime = r;
    }
          
   
    public void setOutPort(final OutPort p) {
        outPort = p;
    }
    
    /**
     * Sets the user generator
     * @param ug	User generator
     */
    public void setUsersGenerator(final UserGenerator ug) {
        usersGenerator = ug;
    }
    
    private void setTerminationPolicy(MaskImplicitPolicy<?, ?, Boolean> p) {
        terminationPolicy = p;
    }
    
    
    public void printStatistics() {

    	if (STATS) {
    		if (interarrivalMean.sampleSize() > 0) {
    			System.out.println("### Source " + getEntityName() + "\n");
    			System.out.println("Users generated                                     : " + interarrivalMean.sampleSize());
    			System.out.println("Sampling Mean interarrivalTime                      : " + interarrivalMean.meanValue());
    			System.out.println("Variance of Sampling Mean interarrivalTime          : " + interarrivalMean.variance());
    			//System.out.println("Confidence interval a = 0.9                         : " + interarrivalMean.confidenceInterval(0.9));

    			//System.out.println("Sampling Variance of interarrivalTime               : " + interarrivalVariance.meanValue());
    			//System.out.println("Variance of Sampling Variance of interarrivalTime   : " + interarrivalVariance.variance());
    			//System.out.println("Confidence interval a = 0.9                         : " + interarrivalVariance.confidenceInterval(0.9));        
    			System.out.println("=====================================\n\n\n");
    		}
    	}
    }    
}

