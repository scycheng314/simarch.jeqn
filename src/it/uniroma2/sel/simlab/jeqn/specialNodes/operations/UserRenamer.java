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

package it.uniroma2.sel.simlab.jeqn.specialNodes.operations;

import it.uniroma2.sel.simlab.jeqn.errors.JEQNConfigurationError;
import it.uniroma2.sel.simlab.jeqn.policies.masks.MaskBasePolicy;
import it.uniroma2.sel.simlab.jeqn.users.User;
import java.lang.reflect.Method;

/** Renames a user using a specified renaming policy - currently not used
 *
 * @author Daniele Gianni
 */
public class UserRenamer implements Operator {
    
    protected MaskBasePolicy<?, User, ?, String> renamingPolicy;
    
    /** Creates a new instance of UserRenamer */
    public UserRenamer(MaskBasePolicy<?, User, ?, String> renamingPolicy) {
        setRenamingPolicy(renamingPolicy);
    }
    
    
    public void rename(final User u) {
        u.setName(renamingPolicy.getDecisionFor(u));
    }

    public Method getOperation() {
        try {            
            return getClass().getMethod("rename");
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            throw new JEQNConfigurationError(ex);
        }
        
        return null;
    }

    private void setRenamingPolicy(MaskBasePolicy<?, User, ?, String> p) {
        renamingPolicy = p;
    }
}