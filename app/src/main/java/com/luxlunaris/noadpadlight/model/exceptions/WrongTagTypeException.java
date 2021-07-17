package com.luxlunaris.noadpadlight.model.exceptions;

/**
 * Thrown when someone tries to access a Metadata tag
 * but gets its type wrong. (Eg: accessing a float
 * assuming it's an int).
 *
 */
public class WrongTagTypeException extends Exception{

    public WrongTagTypeException(String message){
        super(message);
    }



}
