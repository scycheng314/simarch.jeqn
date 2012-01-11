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

package it.uniroma2.sel.simlab.jeqn.waiting.times;

/** Implements a switching time class, in which all the times are the same
 *
 * @author Daniele Gianni
 */
public class UniformSwitchingTimes implements QueuesSwitchingTimes {
    
    protected double time;
    
    /** Creates a new instance of UniformSwitchingTimes */
    public UniformSwitchingTimes(final double d) {
       setTime(d); 
    }

    public double fromTo(final int i, final int j) {
        return getTime();
    }

    public double getTime() {
        return time;
    }
    
    public int maxFor(final int i) {
        System.err.println("Method maxFor has not been defined yet!!!!");
        return 0;
        
        //return getTime();
    }

    public double maxFrom(final int i) {
        return getTime();
    }

    public int minFor(final int i) {
        System.err.println("Method minFor has not been defined yet!!!!");
        return 0;

        //return getTime();
    }

    public double minFrom(final int i) {
        return getTime();
    }
    
    public void setTime(final double d) {
        time = d;
    }    
}
