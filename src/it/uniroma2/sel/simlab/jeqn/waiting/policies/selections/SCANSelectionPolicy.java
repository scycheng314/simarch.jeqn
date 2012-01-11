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

package it.uniroma2.sel.simlab.jeqn.waiting.policies.selections;

import it.uniroma2.sel.simlab.jeqn.policies.StateOnlyDependentPolicy;
import it.uniroma2.sel.simlab.jeqn.waiting.storages.UserQueue;

import java.util.List;

enum Direction {UP, DOWN};

/** Implements a selection policy that scans all the user queues
 *
 * @author Daniele Gianni
 */
public class SCANSelectionPolicy extends StateOnlyDependentPolicy<Integer, UserQueue> {
    
    private Direction direction;
    private List<UserQueue> userMultiQueue;    
    
    /**
     * Creates a new instance of SCANSelectionPolicy
     * @param initialQueue The queue it starts with
     * @param userMultiQueue The queue system upon which the decision is made
     */
    public SCANSelectionPolicy(final Integer initialQueue, final List<UserQueue> userMultiQueue) {
        super(initialQueue);
        
        setUserMultiQueue(userMultiQueue);        
        setDirection(Direction.UP);
    }
    
    public UserQueue getDecision() {
                
        do {
            if (getDirection().equals(Direction.UP)) {
                if (state < (userMultiQueue.size() - 1)) {
                    state = state + 1;
                } else {
                    setDirection(Direction.DOWN);
                    state = state - 1;
                }
            } else { // Direction.DOWN
                if (state > 0) {
                    //???????????
                    state = state - 1;
                } else {
                    setDirection(Direction.UP);
                    state = state + 1;
                }
            }
        } while (userMultiQueue.get(state).isEmpty());
        
        return userMultiQueue.get(state); // getUserQueue(state);
    }
    
    /**
     * Accessor method for the property
     * @return The multi queue systems
     */
    public List<UserQueue> getUserMultiQueue() {
        return userMultiQueue;
    }
    
    /**
     * Accessor method for the property
     * @param q The multi queue system
     */
    public void setUserMultiQueue(List<UserQueue> q) {
        userMultiQueue = q;
    }

    /**
     * Accessor method for the property
     * @return The current scanning direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Accessor method for the property
     * @param direction The new scanning direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
