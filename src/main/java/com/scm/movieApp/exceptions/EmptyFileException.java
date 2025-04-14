package com.scm.movieApp.exceptions;

public class EmptyFileException extends RuntimeException
{
    public EmptyFileException(String resourceName)
    {
        super(String.format("%s exists", resourceName));
    }
}
