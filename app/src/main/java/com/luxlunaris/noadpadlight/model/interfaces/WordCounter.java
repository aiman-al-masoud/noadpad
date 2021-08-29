package com.luxlunaris.noadpadlight.model.interfaces;

import java.io.Serializable;

/**
 * Manages jumping back and forth between instances of a
 * word in a piece of text.
 * Provides statistics about said piece of text.
 */
public interface WordCounter extends Serializable {

    /**
     * Set the token to be found in this Page
     *
     * @param token
     */
    void setTokenToBeFound(String token);

    /**
     * Get the next position of the currently sought-after token
     *
     * @return
     */
    int nextPosition();

    /**
     * Get the previous position of the currently sought-after token
     *
     * @return
     */

    int previousPosition();

    /**
     * How many times does a token appear in this Page's text?
     *
     * @param token
     * @return
     */
    int numOfTokens(String token);
}
