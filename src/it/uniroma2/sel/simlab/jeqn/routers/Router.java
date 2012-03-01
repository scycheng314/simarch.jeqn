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

package it.uniroma2.sel.simlab.jeqn.routers;

import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNUnexpectedEventReceivedException;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.simarch.data.Time;

import it.uniroma2.sel.simlab.jeqn.users.User;

import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;

import it.uniroma2.sel.simlab.statistics.estimators.DiscretePopulationMean;

import it.uniroma2.sel.simlab.generalLibrary.dataStructures.SortedList;
import it.uniroma2.sel.simlab.generalLibrary.dataStructures.UnableToInsertException;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNConfigurationException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import java.util.ArrayList;
import java.util.List;

/** Implements the simulation logic of a EQN router component.
 *
 * @author  Daniele Gianni
 */
public class Router extends JEQNElement {

    // enables the printing of statistics
    private static final Boolean STATS = true;

    /* incoming user port
     *
     */
    protected InPort inPort;

    /* the list of output ports
     *
     */
    protected List<OutPort> outPorts;

    /*
     * the policy determining how the output port is chosen for each incoming user
     */
    protected MaskBasePolicy<?, User, ?, Integer> routingPolicy;

    /*
     * the element extracting the decision data for the routing policy, for each incoming user
     */
    //protected DecisionDataFactory<?, ?> decisionDataFactory;

    /*
     * delay time introduced in the propagation of a user through a router
     */
    protected Time delay;
    // statistics data
    /*
     * routing delay sampling
     */
    protected DiscretePopulationMean routingDelay;

    /*
     * collection of the end transmission times - to determine whether a user is in routing state (ie routed but still to be delivered to the recipient entity
     */
    protected SortedList outgoingTimes;

    /*
     * total number of users that have been routed
     */
    protected int usersRouted;

    /*
     * number of users that have been routed through each port
     */
    protected int usersRoutedToPorts[];

    /**
     * Creates a new Router element in the EQN network 
     * @param name	Element name. The name is used to identify entities within the simulation model.
     * @param timeFactory	Instances the jEQN time object that contains the value for the simulation time.	
     * @param factory	According to the Factory pattern, factory is used to instantiates the implementation of Layer3ToLayer2 interface, which provides level 3 services to level 2.
     * @param tdelay	delay introduced by the router to propagate an user to the output port. 
     * @param numberOfOutPorts	{@code Integer} object that specifies the number of output ports.
     * @param routingPolicy	{@code MaskBasePolicy} object that implements the policy used to determine the the output port to which incoming users are to be routed. 
     * @throws InvalidNameException	An InvalidNameException is raised when an issue concerning the element name occurs.
     * 
     */
     
    public Router(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory factory, final Time tdelay, final Integer numberOfOutPorts, final MaskBasePolicy<?, User, ?, Integer> routingPolicy /*, final DecisionDataFactory<?, ?> ddFactory */) throws InvalidNameException {
        super(name, timeFactory, factory);

        setDelay(tdelay);

        init(numberOfOutPorts);
        this.routingPolicy = routingPolicy;

        //decisionDataFactory = ddFactory;
    }

    private void init(final Integer i) throws InvalidNameException {

        // input port initialization
        inPort = new InPort(new JEQNName("inPort"), this);

        /*
         *  Output ports initialization.
         *  Output ports are implemented as an array, so that ports are identified by the related index.
         */
        
        outPorts = new ArrayList<OutPort>();

        for (int j = 0; j < i; j++) {
            outPorts.add(j, new OutPort(new JEQNName("outPort" + j), this));
        }

        initStats();
    }

    private void initStats() {
        usersRouted = 0;
        usersRoutedToPorts = new int[outPorts.size()];
        routingDelay = new DiscretePopulationMean();
        outgoingTimes = new SortedList();
    }
    
    /**
     * Contains the simulation logic of the element.
     */
    public void body() throws JEQNException {

        Event event;

        try {
            while (true) {
                event = nextEvent();

                // computation of the average inter arrival time
                while (!outgoingTimes.isEmpty()) {
                    if (getClock().isGreaterOrEqualThan((Time) outgoingTimes.seeFirst())) {
                        outgoingTimes.removeFirst();
                    } else {
                        break;
                    }
                }

                // router body - actual simulation logic
                if (event.getTag().equals(Events.NEW_INCOMING_USER)) {
                    usersRouted++;

                    outgoingTimes.add(getClock().increasedBy(delay));

                    //routingDelay.insertNewSample(delay.getValue());

                    // updating routing statistics
                    int outPortIndex = routingPolicy.getDecisionFor((User) event.getData());

//                    getDecisionFor(capture#808 of ? extends DecisionData) in ExplicitInputPolicy<capture#808 of ? extends DecisionData,java.lang.Integer> 
//                            cannot be applied to (DecisionData<capture of ? extends DecisionData>)
//                            
//                    int outPortIndex = routingPolicy.getDecisionFor(decisionDataFactory.buildDecisionDataFrom(null));//(User) event.getData()));
//
//                            routingPolicy.getDecisionFor(
//                               decisionDataFactory.buildDecisionDataFrom((User) event.getData()));
                    OutPort outPort = outPorts.get(outPortIndex);
                    usersRoutedToPorts[outPortIndex]++;

                    // user routing
                    try {
                        send(outPort, delay, Events.NEW_INCOMING_USER, event.getData());
                    } catch (NullPointerException ex) {
                        System.err.print(getFullName());
                        System.err.print(" " + outPort.getName() + "nnnn \n");
                        System.exit(-1);
                    }
                } else {
                    throw new JEQNUnexpectedEventReceivedException(event);
                }
            }
        } catch (UnableToInsertException ex) {
            throw new JEQNException(ex);
        } catch (UnlinkedPortException exL) {
            throw new JEQNConfigurationException(exL);
        } catch (TimeAlreadyPassedException exT) {
            throw new JEQNTimeException(exT);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.err.println(getFullName());
        }
    }
    
    /**
     * Returns the router input port
     * @return Router input port
     */
    public InPort getInPort() {
        return inPort;
    }
    
    /**
     * Returns the router output port identified by the specified numeric index
     * @param i	index that identifies an {@code OutPort} whithin the {@code outPorts} array.
     * @return the router output port identified by the specified index
     */
    public OutPort getOutPort(final int i) {
        return outPorts.get(i);
    }
    
    /**
     * Returns the {@code outPorts} object that implements the array of output ports.
     * @return array of output ports.
     */
    public List<OutPort> getOutPorts() {
        return outPorts;
    }
    
    /**
     * Returns the number of user still in the router (ie routed but still to be delivered to the recipient entity)
     * 
     * @return number of users still to be delivered to the recipient entity.
     */
    public int getUsersInRouting() {
        /*if (outgoingTimes.isEmpty()) return 0;
        else */
        return outgoingTimes.size();
    }
    
    /**
     * Prints the statistics data gathered by the component during the simulation.
     */
    public void printStatistics() {

        if (STATS) {

            if (usersRouted > 0) {
                System.out.println("### Router " + getEntityName() + "\n");
                System.out.println("\n\n");
                System.out.println("Users Routed                : " + usersRouted);

                while (!outgoingTimes.isEmpty()) {
                    if (getClock().isGreaterOrEqualThan(((Time) outgoingTimes.seeFirst()))) {
                        outgoingTimes.removeFirst();
                    } else {
                        break;
                    }
                }

                System.out.println("Number of Users in routing  : " + getUsersInRouting());

                System.out.println("");
                for (int i = 0; i < usersRoutedToPorts.length; i++) {
                    System.out.println("Users routed to port " + i + " connected to ### : " + usersRoutedToPorts[i]);
                }

                System.out.println("");
                for (int i = 0; i < usersRoutedToPorts.length; i++) {
                    System.out.println("Percentage of users routed to port " + i + " connected to ### : " + ((usersRoutedToPorts[i] * 1.0) / (usersRouted * 1.0)));
                }
            }
        }
    }

    protected void setDelay(final Time t) {
        delay = t;
    }
    
    /**
     * Sets the list of output ports
     * @param ps	list of output ports
     */
    public void setOutPorts(final List<OutPort> ps) {
        outPorts = ps;
    }
}
