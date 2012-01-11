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

/** Defines a mapping service request generator for multiple categories
 *
 * @author Daniele Gianni
 */
public class MapMultiCatServiceRequestGenerator extends MultiCatServiceRequestGenerator {
    
    protected HashMap <Category, SingleCatServiceRequestGenerator> categoryToServiceRequestGeneratorMap;
    
    /** Creates a new instance of MapMultiCatServiceRequestGenerator */
    public MapMultiCatServiceRequestGenerator() {
        categoryToServiceRequestGeneratorMap = new HashMap<Category, SingleCatServiceRequestGenerator>(); 
    }

    /**
     * Assignes the individual category service generator to the category
     * @param c
     * @param g
     */
    public void put(final Category c, final SingleCatServiceRequestGenerator g) {
        categoryToServiceRequestGeneratorMap.put(c, g);
    }
    
    public ServiceRequest getNext(final Category c) {
        return categoryToServiceRequestGeneratorMap.get(c).getNext(null);
    }
    
}
