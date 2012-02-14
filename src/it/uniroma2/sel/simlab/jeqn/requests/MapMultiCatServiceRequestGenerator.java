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

package it.uniroma2.sel.simlab.jeqn.requests;

import it.uniroma2.sel.simlab.jeqn.users.Category;

import java.util.HashMap;

/** 
 * Creates a multiple category service request generator by specifying a mapping between service request generators and categories.
 * 
 * @see it.uniroma2.sel.simlab.jeqn.requests.MultiCatServiceRequestGenerator
 * @see it.uniroma2.sel.simlab.jeqn.requests.SingleCatServiceRequestGenerator
 *
 * @author Daniele Gianni
 */
public class MapMultiCatServiceRequestGenerator extends MultiCatServiceRequestGenerator {
    
    protected HashMap <Category, SingleCatServiceRequestGenerator> categoryToServiceRequestGeneratorMap;
    
    /** 
     * Creates a new MapMultiCatServiceRequestGenerator that allows to associate a single category service request generator for each different category.
     * */
    public MapMultiCatServiceRequestGenerator() {
        categoryToServiceRequestGeneratorMap = new HashMap<Category, SingleCatServiceRequestGenerator>(); 
    }

    /**
     * Associates the specified single category service request generator to the specified category.
     * @param c	Category for which the service request generator is specified.
     * @param g Single category service request generator to be associated to the category.
     */
    public void put(final Category c, final SingleCatServiceRequestGenerator g) {
        categoryToServiceRequestGeneratorMap.put(c, g);
    }
    
    public ServiceRequest getNext(final Category c) {
        return categoryToServiceRequestGeneratorMap.get(c).getNext(null);
    }
    
}
