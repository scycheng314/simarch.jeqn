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

/**
 * Defines a user with path tracking capabilities. The user is able to remember all the centers and
 * branches traversed during its life cycle.
 *
 * The actual functionality of this class must be checked
 *
 * @author Daniele Gianni
 */
public class TrackingPathUser extends User {

    // the nodes (jEQN entities) traversed
    private ArrayList<String> nodes;

    // the links between the entities
    private ArrayList<Link> links;
    
    /**
     * Creates a new TrackingPathUser
     */
    public TrackingPathUser() {
        super();

        nodes = new ArrayList<String>();
    }
    
    /**
     * Add the specified node name to the traversed node list.
     * @param s Node name to be added to the traversed node list.
     */
    public void addNode(String s) {
        nodes.add(s);
        links.add(new Link(nodes.get(nodes.size() - 1), s));
    }
    
    /**
     * Gets the name of the specified node in the ordered traversed node list.
     * @param i	position in the traversed node list.
     * @return The node name at the i-th position in the traversed node list.
     */
    public String getNode(int i) {
        return nodes.get(i);
    }
    
    /**
     * Gets the traversed node list
     * @return The traversed node as an {@code ArrayList} of {@code String}
     */
    public ArrayList<String> getNodes() {
        return nodes;
    }
    
    /**
     * Gets the list of traversed link
     * @return The list of traversed link
     */
    public ArrayList<Link> getListOfLinks() {

        return links;
        /*
        ArrayList<Link> al = new ArrayList<Link>();

        Link l = new Link(nodes.get(0));
        boolean b = true;

        for (String s : nodes) {

        l.setToNode(s);
        al.add(l);
        old = s:

        if (b) {

        } else {
        l = new Link(old, s);
        al.add(l);
        old = s;
        }
        }
         */
    }
    
    /**
     * Returns a string that contains the name of all traversed node.
     * @return A string that concatenates the name of each node contained in the list of traversed nodes
     */
    public String generateStringPath() {
        return nodes.toString();
    }
}
