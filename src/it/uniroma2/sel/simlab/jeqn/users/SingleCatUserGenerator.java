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

/** Provides a user generator for a single given user category
 *
 * @author  Daniele Gianni
 */
public final class SingleCatUserGenerator implements UserGenerator {

    // default prefix for user names
    public final static String DEFAULT_NAME = "User";
    
    private Category category;
    private String prefixName;

    // counter of number of users generated
    private int userCounter = 0;
    
    /** Creates a new instance of SingleCategoryUserGenerator 
     *  @param c Category the generated user will belong to
     */    
    public SingleCatUserGenerator(final Category c) {
        setCategory(c);
        setPrefixName(DEFAULT_NAME);
    }    
    
    /** Creates a new instance of SingleCategoryUserGenerator 
     *  @param c Category the generated user will belong to
     *  @param s The user name prefix 
     */    
    public SingleCatUserGenerator(final Category c, final String s) {
        setCategory(c);
        setPrefixName(s);
    }
    
    public User getNextUser() {
        ++userCounter;
        return new User(prefixName + Integer.toString(userCounter), category);
    }
    
    /** Getter for property category.
     * @return Value of property category
     */
    public Category getCategory() {
        return category;
    }
    
    /** Setter for property category.
     * @param c New value of property category.
     *
     */
    public void setCategory(Category c) {
        category = c;
    }
    
    /** Getter for property prefixName.
     * @return Value of property prefixName.
     *
     */
    public String getPrefixName() {
        return prefixName;
    }
    
    /** Setter for property prefixName.
     * @param s New value of property prefixName.
     */
    public void setPrefixName(String s) {
        prefixName = s;
    }    
}
