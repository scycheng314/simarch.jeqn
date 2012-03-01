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

import it.uniroma2.sel.simlab.jeqn.tokens.Token;
import java.util.ArrayList;

/** Wraps a user allowing it to carry some tokens.
 *
 * @see it.uniroma2.sel.simlab.jeqn.specialNodes.PassiveQueueInNode
 * @see it.uniroma2.sel.simlab.jeqn.specialNodes.PassiveQueueOutNode
 * @author Daniele Gianni
 */
public final class UserWithTokens <T extends Token> extends User {

    // list of the tokens associated to this user
    private ArrayList<T> tokenList;

    // the wrapping of the original user
    private User wrapped;
    
    /**
     * Creates a new instance of UserWithTokens
     * @param toWrap The user to wrap
     */
    public UserWithTokens(final User toWrap) {
        super(toWrap);
        
        initTokenList(); 
        setWrapped(toWrap);
    }   
    
    /**
     * Creates a new instance of UserWithTokens
     * @param token The token to carry
     * @param toWrap The user to wrap
     */
    public UserWithTokens(final User toWrap, final T token) {
        super(toWrap);
        
        initTokenList();
        tokenList.add(token);
        setWrapped(toWrap);
    }        
    
    /**
     * Wraps the given user and attached the given token
     * @return The wrapped user with carries the token
     * @param token To be carried by the user
     * @param toWrap The user that has to carry the token
     */
    public static <T extends Token> UserWithTokens wrap(final User toWrap, final T token) {        
        return new UserWithTokens<T>(toWrap, token);
    }
    
    /**
     * Adds a the given token to the token list
     * @param token To be added
     */
    public <S extends T> void addToken(final S token) {
        tokenList.add(token);
    }
    
    private void initTokenList() {
        tokenList = new ArrayList<T>();
    }
    
    /**
     * Removes the first token from the token list
     * @return The token
     */
    public <S extends T> T removeToken() {
        T token = tokenList.get(0);
        tokenList.remove(0);
        return  token;
    }        
    
    /**
     * Removes the token from the token list
     * @param token To be removed
     * @return The token removed
     */
    public <S extends T> T removeToken(final S token) {
        int i = tokenList.indexOf(token); 
        tokenList.remove(i);
        return  token;
    }       
    
    private void setWrapped(final User u) {
        wrapped = u;                
    }    
    
    /**
     * Unwraps the original user
     * @return The wrapped user that was carrying the tokens
     */
    public User unWrap() {
        return wrapped;
    }
}
