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

import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNConfigurationException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.users.CloneUser;
import it.uniroma2.sel.simlab.jeqn.users.CloneUser_UserTracker;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;
import java.util.ArrayList;

/** An implementation of the Split Node with User Tracking capabilities.
 * Currently used only by Daniele in NETBES
 *
 * @author Daniele Gianni
 */
public class SplitNode_UserTracker extends SpecialNode {
    
    private Time interUserDelay;
    
    private MaskBasePolicy<?, User, ?, Integer> numberOfUsers; 
    
    // stats    
    private DiscretePopulationMean meanInterarrivalTime;
    private DiscretePopulationMean meanNumberOfClones;
    
    /** Creates a new instance of SplitNode */
    public SplitNode_UserTracker(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final MaskBasePolicy<?, User, ?, Integer> numberOfUsers) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);    
        
        setInterUserDelay(timeFactory.makeFrom(Time.ZERO));
        setNumberOfUsers(numberOfUsers);
        
        meanInterarrivalTime = new DiscretePopulationMean();
        meanNumberOfClones = new DiscretePopulationMean();
    }
    
    public SplitNode_UserTracker(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay, final double interUserDelay, final MaskBasePolicy<?, User, ?, Integer> numberOfUsers) throws InvalidNameException {
        super(name, timeFactory, layer2factory, userForwardDelay);    
        
        setInterUserDelay(timeFactory.makeFrom(interUserDelay));
        setNumberOfUsers(numberOfUsers);
        
        meanInterarrivalTime = new DiscretePopulationMean();
        meanNumberOfClones = new DiscretePopulationMean();
    }
    
    public void body() throws JEQNException {        
        Event event;
        
        double currentTime = 0.0;
        
        while (true) {            
            event = nextEvent();                        
            
            if (event.getTag().equals(Events.NEW_INCOMING_USER)) {
                
                User u = (User) event.getData();
                
                Integer numberOfNewUsers = numberOfUsers.getDecisionFor(u); 
                ArrayList<CloneUser> clones = CloneUser_UserTracker.clone(u, numberOfNewUsers);
               
                System.out.println("Numero di cloni : " + numberOfNewUsers);
                
                Time cumulativeDelay = timeFactory.makeFrom(Time.ZERO);
                for (CloneUser cu : clones) {                                                            
                    try {                    	
                        send(outPort, userForwardDelay.increasedBy(cumulativeDelay), Events.NEW_INCOMING_USER, cu);                    
                        cumulativeDelay.increaseBy(cumulativeDelay);                                                
                    } catch (TimeAlreadyPassedException ex) {
                        ex.printStackTrace();
                        throw new JEQNTimeException(ex);
                    } catch (UnlinkedPortException ex) {
                        ex.printStackTrace();
                        throw new JEQNConfigurationException(ex);
                    }                    
                }                                                
                meanNumberOfClones.insertNewSample(clones.size());
                meanInterarrivalTime.insertNewSample(event.getTime().getValue() - currentTime);
                currentTime = event.getTime().getValue();
            } else throw new JEQNUnexpectedEventReceivedException(event);                                   
        }
    }
    
    protected User getNewUser(final User u) {
        return new User(u);
    }
    
    public Time getInterUserDelay() {
        return interUserDelay;
    }

    public void setInterUserDelay(Time t) {
        interUserDelay = t;
    }

    private void setNumberOfUsers(final MaskBasePolicy<?, User, ?, Integer> p) {
        numberOfUsers = p;
    }

    public void printStatistics() {
        
        System.out.println("### Split Node " + getEntityName() + "\n");
        System.out.println("Mean Interarrival Time                   : " + meanInterarrivalTime.meanValue());                
        //System.out.println("Variance of Mean Waiting Time             : " + meanInterarrivalTime.variance());
        System.out.println("Mean number of clones for user           : " + meanNumberOfClones.meanValue());
        System.out.println("Number of users received                 : " + meanNumberOfClones.sampleSize());
        System.out.println("\n\n");
    }
}