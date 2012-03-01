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

/** Implements Category, that represents a class of users within EQN network.
 *
 * @author  Daniele Gianni
 */
public class Category implements Comparable<Category> {

    // category name, which also is the category unique identifier
    private String name;
    
    // other info needed to characterize the category
    
    /** 
     * Creates a new Category
     * 
     * @param s The category name.
     */
     
    public Category(final String s) {       
        setName(s);
    }
    
    /**
     * Gets the category name
     * @return The user category name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the category name
     * @param s The user category name
     */
    public void setName(final String s) {
        name = s;
    }
    
    /**
     * Return a string that contains the category name
     * @return A string that contains the category name
     */
    public String toString() {
        return "cat: " + name;
    }    

    /**
     * Compares this category to the one specified as parameter by comparing the category names
     * @return the value {code 0} if the argument category is equal to this category; 
     * a value less than {@code 0} if this category is lexicographically less than the category argument; 
     * and a value greater than {@code 0} othewise.
     */
    public int compareTo(final Category c) {
        return name.compareTo(c.getName());
    }
}
