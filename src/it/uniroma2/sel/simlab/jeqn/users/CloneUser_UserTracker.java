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

package it.uniroma2.sel.simlab.jeqn.users;

import java.util.ArrayList;

/** Represents a CloneUser with tracking capabilities. Currently this class is used only
 * in jEQN - Building, by Daniele
 *
 * @author Daniele Gianni
 */
public class CloneUser_UserTracker extends CloneUser {

    // path followed from the cloned user
    private TrackingPathUser cloned;

    // number of the copy
    private Integer copyNumber;
    
    /**
     * Creates a new instance of CloneUser
     * @param toClone User to be coned.
     */
    public CloneUser_UserTracker(final TrackingPathUser toClone) {
        super(toClone);
        
        setCloned(toClone);
        setCopyNumber(0);
    }
    
    /**
     * Creates a new instance of CloneUser
     * @param toClone User to be coned.
     * @param copyNumber the number of clones that have to be generated
     */
    public CloneUser_UserTracker(final TrackingPathUser toClone, final Integer copyNumber) {
        super(toClone);
        
        setCloned(toClone);
        setCopyNumber(copyNumber);
    }
    
    /**
     * Static method that returns an instance of CloneUser by wrapping the specified user.
     * @param u The user to be cloned.
     * @return The new instance of CloneUser.
     */
    public static CloneUser_UserTracker wrap(final TrackingPathUser u) {
        return new CloneUser_UserTracker(u);
    }
    
    /**
     * Static method that returns an instance of CloneUser with the specified number of clones. It wraps the specified user.
     * 
     * @param u The TrackingPathUser to be cloned.
     * @param i the number of clones that have to be generated
     * @return The new instance of CloneUser.
     */
    public static CloneUser_UserTracker wrap(final TrackingPathUser u, final Integer i) {
        return new CloneUser_UserTracker(u, i);
    }        
    
    /**
     * Static method that returns a list of cloned user, generated from an original user.
     * @param toClone user to be cloned 
     * @param numberOfClones the number of clones that have to be generated
     * @return the list of clones
     */
    public static ArrayList<CloneUser_UserTracker> clone(final TrackingPathUser toClone, final Integer numberOfClones) {
    
        ArrayList<CloneUser_UserTracker> clones = new ArrayList<CloneUser_UserTracker>(numberOfClones);
        
        for (int i = 0; i < numberOfClones; i++) {
            clones.add(new CloneUser_UserTracker(toClone, i));
        }
        
        return clones;        
    }
    
    /**
     * 
     */
    public User getClone() {
        return cloned;
    }
    
    public Integer getCopyNumber() {
        return copyNumber;
    }
    
    public String getName() {
        return "Clone" + getCopyNumber() + "-" + cloned.getName();
    }
    
    protected void setCloned(final TrackingPathUser u) {
        cloned = u;
    }
    
    public void setCopyNumber(final Integer i) {
        copyNumber = i;
    }
}
