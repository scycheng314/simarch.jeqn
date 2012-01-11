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

import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Implements the switching times in the changing of the queue (in a multi-queue
 * structure) to be served. The switching times are constant.
 *
 * @author  Daniele Gianni
 */
public class ConstantSwitchingTimes implements QueuesSwitchingTimes {

    // the switching times
    protected double times[][];
    
    /** Creates a new instance of ConstantSwitchingTimes */
    public ConstantSwitchingTimes() {
    }
    
    public ConstantSwitchingTimes(final int i, final int j) {
        times = new double[i][j];        
    }

    /*
     * Returns the switching between queue i and queue j
     */
    public double fromTo(int i, int j) {
        return times[i][j];
    }

    /*
     * Return the queue j for which the switching time from queue i is max
     */
    public int maxFor(int i) {
        double max = times[i][0];
        int maxJ = 0;
        
        for (int j = 0; j < times[i].length; j++) {
            if (times[i][j] > max) {
                max = times[i][j];
                maxJ = j;
            }
        }
         
        return maxJ;
    }

    /*
     * Return max switching time that the system can experience from queue i
     */
    public double maxFrom(int i) {
        double max = times[i][0];
        
        for (int j = 0; j < times[i].length; j++) {
            if (times[i][j] > max) {
                max = times[i][j];
            }
        } 
        
        return max;
    }

    /*
     * Return the queue j for which the switching time from queue i is min
     */
    public int minFor(int i) {
        double min = times[i][0];
        int minJ = 0;
        
        for (int j = 0; j < times[i].length; j++) {
            if (times[i][j] < min) {
                min = times[i][j];
                minJ = j;
            }
        }
         
        return minJ;
    }

    /*
     * Return min switching time that the system can experience from queue i
     */
    public double minFrom(int i) {
        double min = times[i][0];
        
        for (int j = 0; j < times[i].length; j++) {
            if (times[i][j] < min) {
                min = times[i][j];
            }
        }
         
        return min;
    }

    /*
     * Loads switching times from file - needs to be double checked
     */
    public static ConstantSwitchingTimes buildFromFile(final String s) {
        
        ConstantSwitchingTimes t = null;
        
        try {
            FileInputStream istream = new FileInputStream(s);
            ObjectInputStream ostream = new ObjectInputStream(istream);       

            int n = ostream.readInt();
            int m = ostream.readInt();

            t = new ConstantSwitchingTimes(n, m);
        
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    t.setTime(i, j, ostream.readDouble());
                }            
            }
        
            return t;
        } catch (IOException e) {
            System.err.println("Unable to build up Constant Switching Times Matrix from file " + s + " " + e);
        } finally {
            return t;
        }
    }
    
    public void setTime(final int i, final int j, final double d) {
        times[i][j] = d;
    }
    
    public double getTime(final int i, final int j) {
        return times[i][j];
    }
    
    /*
    public int hSize() {
        return times[0].length;
    }
    
    public int lSize() {
        return times.length;
    }
    */
}
