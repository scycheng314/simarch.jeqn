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

import it.uniroma2.sel.simlab.jeqn.errors.JEQNConfigurationError;
import it.uniroma2.sel.simlab.jeqn.events.Events;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNException;
import it.uniroma2.sel.simlab.jeqn.exceptions.JEQNTimeException;
import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.stats.Population;
import it.uniroma2.sel.simlab.jeqn.stats.SetOfPopulations;
import it.uniroma2.sel.simlab.jeqn.users.User;
import it.uniroma2.sel.simlab.jeqn.users.UserGenerator;
import it.uniroma2.sel.simlab.simarch.data.Event;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.TimeAlreadyPassedException;
import it.uniroma2.sel.simlab.simarch.exceptions.layer2.UnlinkedPortException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;

/* Triggers a new users (of a specified type) upon reception of an incoming user and
 * depending on a specified triggering policy.
 *
 * It's currently used only by Daniele, within NET-BES, to emulate the Join node
 *
 * @author Daniele Gianni
 *
 */
public class TriggerNode extends JEQNElement {

    protected static final String IN_PORT = "inPort";
    protected static final String NEXT_ENTITY = "nextEntity";
    protected InPort inPort;
    protected OutPort nextEntityPort;
    protected Time reactionDelay;
    protected MaskBasePolicy<?, User, ?, Boolean> triggeringPolicy;
    protected UserGenerator triggerUserGenerator;
    protected SetOfPopulations samples;

    /** Creates a new instance of AllocateNode */
    public TriggerNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, MaskBasePolicy<?, User, ?, Boolean> triggeringPolicy, final UserGenerator triggerUserGenerator, final double reactionDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory);

        setReactionDelay(timeFactory.makeFrom(reactionDelay));
        setNextEntityPort(new OutPort(new JEQNName(NEXT_ENTITY), this));
        setInPort(new InPort(new JEQNName(IN_PORT), this));
        setTriggerUserGenerator(triggerUserGenerator);

        samples = new SetOfPopulations();
    }

    public void body() throws JEQNException {

        Event event;
        Event nextIncomingUser;

        double arrivalTime = 0.0;
        double requestTime = 0.0;

        int userCounter = 0;

        double timeOfLastTrigger = 0.0;
        Population currentPopulation = new Population();

        try {
            while (true) {

                event = nextEvent();

                User toCheck = (User) event.getData();
                userCounter++;

                //System.out.println("Contati utenti : " + userCounter + " " + toCheck.getName() + " " + getClock().getValue());

                currentPopulation.insertSample(getClock().getValue() - timeOfLastTrigger);

                if (triggeringPolicy.getDecisionFor(toCheck)) {
                    User triggerUser = triggerUserGenerator.getNextUser();
                    triggerUser.setBornTime(getClock());
                    send(nextEntityPort, reactionDelay, Events.NEW_INCOMING_USER, triggerUser);
                    //System.out.println("\n\nTriggered a new user at time " + getClock().getValue() + "\n\n\n");
                    // collect statistics here!

                    samples.addPopulation(currentPopulation);
                    currentPopulation = new Population();
                    timeOfLastTrigger = getClock().getValue();
                } else {
                }
            }
        } catch (TimeAlreadyPassedException ex) {
            ex.printStackTrace();
            throw new JEQNTimeException(ex);
        } catch (UnlinkedPortException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationError(ex);
        }
    }

    public Time getReactionDelay() {
        return reactionDelay;
    }

    public OutPort getNextEntityPort() {
        return nextEntityPort;
    }

    public void printStatistics() {
        System.out.println("### Trigger Node " + getEntityName() + " : ");
        System.out.println("\n\n");

        samples.print();
        //System.out.println("No statistics available yet.");
        /*
        System.out.println("Mean user interarrival time             : " + meanInterarrivalTime.meanValue());
        System.out.println("Numer of user arrived                   : " + meanInterarrivalTime.sampleSize());
        System.out.println("Mean request time                       : " + meanRequestTime.meanValue());
        System.out.println("Number of token request satisfied       : " + meanRequestTime.sampleSize());
         */
    }

    public void setReactionDelay(final Time t) {
        reactionDelay = t;
    }

    public void setNextEntityPort(final OutPort p) {
        nextEntityPort = p;
    }

    public UserGenerator getTriggerUserGenerator() {
        return triggerUserGenerator;
    }

    public void setTriggerUserGenerator(UserGenerator ug) {
        triggerUserGenerator = ug;
    }

    public InPort getInPort() {
        return inPort;
    }

    protected void setInPort(InPort p) {
        inPort = p;
    }
}
