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

/** Provides a User generator for a specified category of users. The category
 * is specified as "the same as" the one of an example User object
 *
 * To be finalized?
 *
 * @author Daniele Gianni
 */
public class SameCatUserGenerator implements UserGenerator {
    
    private Category category;
    
    /** Creates a new instance of SameCatUserGenerator */
    public SameCatUserGenerator(final User u) {
        setCategory(u.getCategory());
    }

    public User getNextUser() {
        return new User("User_of_category_" + category.getName(), category);
    }

    /*
     * Sets the category to be associated to each user generated
     */
    protected void setCategory(final Category c) {
        category = c;
    }
}
