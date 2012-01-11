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

 package it.uniroma2.sel.simlab.jeqn.specialNodes.policies.triggering;

import it.uniroma2.sel.simlab.jeqn.policies.StateOnlyDependentPolicy;

/* Defines a triggering policy based on the number of users arrived at the center
 *
 * @author Daniele Gianni
 */
public class CountNTriggeringPolicy extends StateOnlyDependentPolicy<Integer, Boolean>{

	private static final Integer COUNTED_INITIAL_VALUE = 0; 
	private Integer counted;
	private Integer n;
	
	public CountNTriggeringPolicy(Integer n) {
		resetCounted();
		setN(n);
	}
	
	@Override
	public Boolean getDecision() {		
		counted++;
		
		if (counted >= n) {
			resetCounted();
			return true;
		} else {
			return false;
		}
	}

	protected void resetCounted() {
		setCounted(COUNTED_INITIAL_VALUE);		
	}
	
	protected Integer getCounted() {
		return counted;
	}

	protected void setCounted(Integer i) {
		counted = i;
	}

	protected Integer getN() {
		return n;
	}

	protected void setN(Integer i) {
		n = i;
	}
}
