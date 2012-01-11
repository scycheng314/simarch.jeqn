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

/*
 * This class needs to be clarified - how it ended up here and what it represents
 */
public class Link {
	
	private String fromNode;
	private String toNode;
	
	public Link() {
		
	}
	
        static public Link buildLinkFromString(String s) {
            String fromNode = s.substring(1, s.indexOf(";"));
            String toNode = s.substring(s.indexOf(";") + 1, s.length());
            Link l = new Link(fromNode, toNode);
            
            return l;
        }
        
	public Link(String from) {
		fromNode = from;
	}
	
	public Link(String from, String to) {
		fromNode = from;
		toNode = to;
	}
	
	public boolean connects(String s) {
		return (fromNode.equals(s) || toNode.equals(s));
	}

	public String getFromNode() {
		return fromNode;
	}

	public void setFromNode(String fromNode) {
		this.fromNode = fromNode;
	}

	public String getToNode() {
		return toNode;
	}

	public void setToNode(String toNode) {
		this.toNode = toNode;
	}
	
        @Override
        public boolean equals(Object o) {
            System.out.println("Casting to equals Link ");
            return equals((Link )o);
        }
        
	public boolean equals(Link l) {
//            System.out.println("Checking Link l equal");
//            System.out.println("Link current " + this); 
//            System.out.println("Link l " + l);
            boolean result = fromNode.equalsIgnoreCase(l.fromNode) && toNode.equalsIgnoreCase(l.toNode);
            System.out.println("equals(" + this + " --- " + l + ") == " + result);
//            System.out.println("Result == " + result);
            
            return result;
	}
	
	public String toString() {
		return "[" + fromNode + ";" + toNode + "]";
	}
}
