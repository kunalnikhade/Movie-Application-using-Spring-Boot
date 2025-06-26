package com.movie.movieApp.exceptions;

public class EmptyFileException extends RuntimeException
{
    public EmptyFileException(String resourceName)
    {
        super(String.format("%s exists", resourceName));
    }
}
