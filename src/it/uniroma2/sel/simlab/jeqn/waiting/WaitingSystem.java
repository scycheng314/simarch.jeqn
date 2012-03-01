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

import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNConfigurationException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
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
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;

import it.uniroma2.sel.simlab.statistics.estimators.ContinuousPopulationMean;
import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

import static it.uniroma2.sel.simlab.jeqn.waiting.Names.IN_PORT;
import static it.uniroma2.sel.simlab.jeqn.waiting.Names.OUT_PORT;

import static it.uniroma2.sel.simlab.jeqn.waiting.WaitingSystemStates.SEND_ON_REQUEST;

/** Defines an abstract user Waiting System simulation component
 *
 * @author  Daniele Gianni
 */
public abstract class WaitingSystem extends JEQNElement {
        
    public static final String INPORT_NAME = "inPort";
    public static final String OUTPORT_NAME = "outPort";
        
    /**
     * The port through which new incoming user are received
     */
    protected InPort inPort;
    /**
     * The port through which the user are sent to the next entity
     */
    protected OutPort outPort;
    
    /**
     * The service request generator
     */
    protected ServiceRequestGenerator resourceRequestGenerator;
    
    /**
     * The delay in the sending to the next entity
     */
    protected Time sendingAheadDelay;
    
    /**
     * The current state
     */
    protected WaitingSystemStates state;    
    
    /**
     * The data structure that store the users
     */
    protected UserQueue userQueue;            
    
    
    // Statistics 
    
    /**
     * The current user queue length
     */
    protected ContinuousPopulationMean meanQueueLength;
    //protected ContinuousPopulationVariance queueLengthVariance;
    
    /**
     * The current user waiting time
     */
    protected DiscretePopulationMean meanWaitingTime;
    
    /**
     * The interarrival time experienced by the WaitingSystem
     */
    protected DiscretePopulationMean interarrivalTime;
    //protected DiscretePopulationVariance waitingTimeVariance;
    
    /**
     * The longest queue length experienced
     */
    protected long maxQueueLength;
    /**
     * The time it received the last user
     */
    protected double lastUserComingInTime;
    
    /**
     * The time it had a user coming up or going out
     */
    protected double lastUserMovementTime;
            
    /**
     * Creates a new instance of WaitingSystem
     * @param name The jEQN name of this entity
     * @param timeFactory The time factory to build internal time values
     * @param layer2factory The underlying layer implementation
     * 
     * @param userQueue The actual user queue data structure
     * @param requestGenerator The specified service request generator
     * 
     * @throws it.uniroma2.info.sel.simlab.simarch.exceptions.InvalidNameException If the specified name does not conform the underlying layer name conventions
     */
    public WaitingSystem(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, UserQueue userQueue, ServiceRequestGenerator requestGenerator, Time sendingAheadDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory);               
        
        setInPort(new InPort(new JEQNName(IN_PORT), this));
        setOutPort(new OutPort(new JEQNName(OUT_PORT), this));
        
        setState(SEND_ON_REQUEST);
        setUsersQueue(userQueue);                
        setResourceRequestGenerator(requestGenerator);
        setSendingAheadDelay(sendingAheadDelay);
                
        init();
    }
    
    /**
     * Creates a new instance of WaitingSystem
     * @param name The jEQN name of this entity
     * @param timeFactory The time factory to build internal time values
     * @param layer2factory The underlying layer implementation
     * 
     * @param userQueue The actual user queue data structure
     * @param requestGenerator The specified service request generator
     * @param sendingAheadDelay The delay introduced when forwarding a user to the next entity
     * @throws it.uniroma2.info.sel.simlab.simarch.exceptions.InvalidNameException 
     */
    public WaitingSystem(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, UserQueue userQueue, ServiceRequestGenerator requestGenerator, final double sendingAheadDelay) throws InvalidNameException { 
        super(name, timeFactory, layer2factory);           
        
        setInPort(new InPort(new JEQNName(IN_PORT), this));
        setOutPort(new OutPort(new JEQNName(OUT_PORT), this));
        
        setState(SEND_ON_REQUEST);
        setUsersQueue(userQueue);                
        setResourceRequestGenerator(requestGenerator);
        setSendingAheadDelay(timeFactory.makeFrom(sendingAheadDelay));
        
        init();
    }
        
    private void init() {                        
        userQueue.setLayer3ToLayer2(executionContainer);
        
        initStats();
    }
    
    private void initStats() {
        setLastUserComingInTime(0);
        setLastUserMovementTime(0);        
        setMaxQueueLength(0);        
        initMeanQueueLength(); 
        initMeanWaitingTime();         
        initInterarrivalTime();        
    }
    
    private void initMeanQueueLength() {
        setMeanQueueLength(new ContinuousPopulationMean());
    }

    private void initMeanWaitingTime() {
        setMeanWaitingTime(new DiscretePopulationMean());
    }
      
    private void initInterarrivalTime() {
        setInterarrivalTime(new DiscretePopulationMean());
    }
    
    /**
     * Contains the simulation logic of the element.
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNException If an exception occurs within this method
     */
    public void body() throws JEQNException {
        
        Event event;
        
        while (true) {            
            
            event = nextEvent();
            
            if (event.getTag().equals(Events.NEW_INCOMING_USER)) {                
                interarrivalTime.insertNewSample(event.getTime().getValue() - lastUserComingInTime);                
                // insert here Resource Request assignement to User
                newIncomingUserEventHandler(event);                
                lastUserMovementTime = lastUserComingInTime = event.getTime().getValue();
            } else {
                if (event.getTag().equals(Events.REQUESTED_NEXT_USER)) {                    
                    requestNextUserEventHandler(event);
                    lastUserMovementTime = event.getTime().getValue();
                } else {
                    otherEventHandler(event);                    
                }
            }
        }
    }
          
    /**
     * Handles incoming events different from {@code} Events.NEW_INCOMING_USER and Events.REQUESTED_NEXT_USER
     * @param e The event
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNException 
     */
    protected abstract void otherEventHandler(final Event e) throws JEQNException ;
    
    /**
     * Handles the event {@code Events.INCOMING_USER}
     * @param e The incoming event to handle
     */
    protected abstract void newIncomingUserEventHandler(Event e);
    
    /**
     * Handles the event {@code Events.REQUEST_NEXT_USER}
     * @param e The event
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNException 
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNTimeException If a TimeException is thrown during the underlying layer sendEvent method
     * @throws it.uniroma2.info.sel.simlab.jeqn.exceptions.JEQNConfigurationException If the proper link is floating
     */
    protected void requestNextUserEventHandler(final Event e) throws JEQNException {       
        
        if (userQueue.getEnqueuedUsers() > 0) { //!userQueue.isEmpty()) {             
            //System.out.println("La coda non e' vuota!!! " + userQueue.getEnqueuedUsers());
            Time extractingTime = userQueue.nextUserExtractingTime();            
            meanQueueLength.insertNewSample(userQueue.getEnqueuedUsers(), e.getTime().getValue() - lastUserMovementTime);//u.getInComingTime());
            //queueLengthVariance.insertNewSample(usersQueue.size(), e.getTime().doubleValue() - u.getBornTime());
            
            User u = userQueue.extract();                                                                       
            
            lastUserMovementTime = e.getTime().getValue();            
            meanWaitingTime.insertNewSample(getClock().getValue() - u.getInComingTime());               
            
            try {                
                send(outPort, extractingTime.increasedBy(sendingAheadDelay), Events.NEW_INCOMING_USER, u);
            } catch (TimeAlreadyPassedException ex) {
                ex.printStackTrace();
                throw new JEQNTimeException(ex);
            } catch (UnlinkedPortException ex) {
                ex.printStackTrace();
                throw new JEQNConfigurationException(ex);
            }
        } else {
            setState(WaitingSystemStates.SEND_ON_COMING_IN);
        }
    }            
    
    public void printStatistics() {

    	if (interarrivalTime.sampleSize() > 0) {
    		System.out.println("### Waiting System " + getEntityName() + "\n");

    		System.out.println("Max Users in the waiting system     : " + maxQueueLength);
    		System.out.println("Mean Users in the waiting system    : " + meanQueueLength.meanValue());

    		//System.out.println("Variance of Mean Queue Length       : " + meanQueueLength.variance());
    		//System.out.println("Queue Length Variance               : " + queueLengthVariance.meanValue());
    		//System.out.println("Variance of Queue Length Variance   : " + queueLengthVariance.variance());
    		System.out.println("Mean Waiting Time                   : " + meanWaitingTime.meanValue());
    		System.out.println("Variance of Mean Waiting Time       : " + meanWaitingTime.variance());

    		System.out.println("Mean Interarrival Time              : " + interarrivalTime.meanValue());
    		System.out.println("Variance of Interarrival Time       : " + interarrivalTime.variance());

    		//System.out.println("Waiting Time Variance               : " + waitingTimeVariance.meanValue());
    		//System.out.println("Variance of Waiting Time Variance   : " + waitingTimeVariance.variance());
    		System.out.println("Sample size                         : " + meanWaitingTime.sampleSize());       

    		System.out.println("Number of users still in the system : " + userQueue.getEnqueuedUsers());

    		System.out.println("\nUsersQueue Stat info :\n" + userQueue.getStatInfo());

    		System.out.println("=====================================\n\n\n");
    	}
    } 
    
    /**
     * Returns the input port through which the users come in. Must be connected to the 
     * previous entity's user output port.
     * @return The port
     */
    public InPort getInPort() {
        return inPort;
    }
    
    /**
     * Returns the input port through which the users come in. Must be connected to the 
     * previous entity's user output port.
     * @return The port
     */
    public OutPort getOutPort() {
        return outPort;
    }        
    
    /**
     * Says if the system is in {@code SEND_ON_COMING_IN} state that is if it is empty 
     * and being asked for a new user to forward to the next entity.
     * @return {@code true} if is empty and received event {@ NEXT_USER_REQUEST}
     * {@code false} otherwise
     */
    protected boolean isInSendOnComingInState() {
        return (state.equals(WaitingSystemStates.SEND_ON_COMING_IN));
    }        
        
    /**
     * Accessor method for property Interarrival Time
     * @param m The stat variable
     */
    protected void setInterarrivalTime(final DiscretePopulationMean m) {
        interarrivalTime = m;
    } 
        
    /**
     * Accessor method for the incoming user port.
     * @param p The port
     */
    protected void setInPort(InPort p) {
        inPort = p;
    }
    
    /**
     * Accessor method for the property lastUserComingInTime
     * @param d The time
     */
    protected void setLastUserComingInTime(final double d) {
        lastUserComingInTime = d;
    }
        
    /**
     * Accessor method for the property lastUserComingInTime
     * @param d The time
     */
    protected void setLastUserMovementTime(final double d) {
        lastUserMovementTime = d;
    }
        
    /**
     * Accessor method for the property MaxQueueLength
     * @param i The length
     */
    protected void setMaxQueueLength(final int i) {
        maxQueueLength = i;
    }
    
    /**
     * Accessor method for the property MeanQueueLength
     * @param m The stat variable
     */
    protected void setMeanQueueLength(final ContinuousPopulationMean m) {
        meanQueueLength = m;
    }

    /**
     * Accessor method for the property MeanWaitingTime
     * @param m The stat variable
     */
    protected void setMeanWaitingTime(final DiscretePopulationMean m) {
        meanWaitingTime = m;
    }
    
    /**
     * Accessor method for the outgoing user port
     * @param p The port
     */
    protected void setOutPort(OutPort p) {
        outPort = p;
    }
    
    /**
     * Sets the request generator that will generate the service request for each 
     * single user passing through this entity and getting the next entity
     * @param r The generator
     */
    protected void setResourceRequestGenerator(final ServiceRequestGenerator r) {
        resourceRequestGenerator = r;
    }
    
    /**
     * Keeps track of the Waiting System internal state
     * @param s The state value
     */
    protected void setState(final WaitingSystemStates s) {
        state = s;
    }
    
    /**
     * Sets the actual user queue implementation to be used to store the user during 
     * their wait
     * @param u The queue implementation
     */
    protected void setUsersQueue(UserQueue u) {
        userQueue = u;
    }           

    /**
     * Gets the delay time introduced in the sending of the users to the next entity
     * @return The current delay
     */
    public Time getSendingAheadDelay() {
        return sendingAheadDelay;
    }

    /**
     * Sets the delay time in the sending ahead of the user to the next entity
     * @param t The delay value
     */
    public void setSendingAheadDelay(final Time t) {
        sendingAheadDelay = t;
    }
}
