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

import it.uniroma2.sel.simlab.jeqn.general.JEQNElement;
import it.uniroma2.sel.simlab.jeqn.general.JEQNName;
import it.uniroma2.sel.simlab.jeqn.general.JEQNTimeFactory;
import it.uniroma2.sel.simlab.simarch.data.Time;
import it.uniroma2.sel.simlab.simarch.exceptions.InvalidNameException;
import it.uniroma2.sel.simlab.simarch.factories.Layer3ToLayer2Factory;
import it.uniroma2.sel.simlab.simcomp.basic.ports.InPort;
import it.uniroma2.sel.simlab.simcomp.basic.ports.OutPort;

/** Defines the structure and the interface of entities that are introduced in the
 * Extended Queueing Network domain, with respect to the Queueing Network domain.
 * Examples of these entities are Allocate Node, Release Node, etc.
 *
 * @author Daniele Gianni
 */
public abstract class SpecialNode extends JEQNElement {

    // the name of the input port for the reception of users
    protected static final String USER_IN_PORT_NAME = "inPort";

    // the name of the output port for the forwarding of users
    protected static final String USER_OUT_PORT_NAME = "outPort";

    /*
     * input port through which the node receives users
     */
    protected InPort inPort;

    /*
     * output port through which the node can forward users to the cascade entity
     */
    protected OutPort outPort;

    /*
     * delay time introduced at each user forward
     */
    protected Time userForwardDelay;
    
    /** Creates a new instance of SpecialNode */
    public SpecialNode(final JEQNName name, final JEQNTimeFactory timeFactory, final Layer3ToLayer2Factory layer2factory, final double userForwardDelay) throws InvalidNameException {
        super(name, timeFactory, layer2factory);
        
        setUserForwardDelay(timeFactory.makeFrom(userForwardDelay));
        
        init();
    }
    
    private void init() throws InvalidNameException {
        setInPort(new InPort(new JEQNName(USER_IN_PORT_NAME), this));
        setOutPort(new OutPort(new JEQNName(USER_OUT_PORT_NAME), this));
    }
    
    public InPort getInPort() {
        return inPort;
    }
    
    public OutPort getOutPort() {
        return outPort;
    }
    
    public Time getUserForwardDelay() {
        return userForwardDelay;
    }
    
    public void setInPort(final InPort p) {
        inPort = p;
    }
    
    public void setOutPort(final OutPort p) {
        outPort = p;
    }
    
    public void setUserForwardDelay(Time t) {
        userForwardDelay = t;
    }    
}
