package com.luxlunaris.noadpadlight.model.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Contains methods to analyze a string of text.
 */
public class WordCounter implements Serializable {

    String originalText;
    Integer[] positionsOfToken;
    String currentToken;
    int posIndex;


    public WordCounter(String originalText){
        this.originalText = originalText;
    }


    /**
     * Set the token to be found in this Page
     * @param token
     */
    public void setTokenToBeFound(String token){
        positionsOfToken = getTokensPositions(token);
        currentToken = token;
        posIndex = 0;
    }



    /**
     *  Get the next position of the currently sought-after token
     * @return
     */
    public int nextPosition() {

        //if no token, or no positions, return index = 0
        if(currentToken ==null || positionsOfToken.length==0){
            return 0;
        }

        if(posIndex+1 > positionsOfToken.length-1){
            return positionsOfToken[posIndex];
        }

        //return the due position, THEN increment the index
        return positionsOfToken[posIndex++];
    }

    /**
     *  Get the previous position of the currently sought-after token
     * @return
     */

    public int previousPosition() {

        //if no token, or no positions, return index = 0
        if(currentToken ==null || positionsOfToken.length==0){
            return 0;
        }

        if(posIndex-1 < 0){
            return positionsOfToken[posIndex];
        }

        //return the due position, THEN increment the index
        return positionsOfToken[posIndex--];
    }

    /**
     * How many times does a token appear in this Page's text?
     * @param token
     * @return
     */
    public int numOfTokens(String token) {
        //escape the token's special chars (just in case)
        token = escapeRegex(token);
        return originalText.toUpperCase().split(token.toUpperCase()).length-1;
    }



    /**
     * Find all of the positions of a token in a piece of text.
     * @param token
     * @return
     */
    private Integer[] getTokensPositions(String token) {

        //initialize list of positions
        ArrayList<Integer> positions = new ArrayList<Integer>();

        //convert token and text to upper case
        token = token.toUpperCase();

        //escape the token's special chars (just in case)
        token = escapeRegex(token);

        //get the text (w/out tags, as displayed on screen) and convert it to upper case
        String text = originalText.toUpperCase();

        //split the text by the token
        String[] parts = text.split(token);

        //first position
        positions.add(parts[0].length());

        //get the other positions
        for(int i =1; i<parts.length-1; i++){
            int lastPos = positions.get(positions.size()-1);
            int nextPos = lastPos+ token.length() +parts[i].length();
            positions.add(nextPos);
        }

        return positions.toArray(new Integer[0]);
    }


    /**
     * Returns the escaped version of string.
     * @param string
     * @return
     */
    private String escapeRegex(String string){
        Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
        return SPECIAL_REGEX_CHARS.matcher(string).replaceAll("\\\\$0");
    }







}
