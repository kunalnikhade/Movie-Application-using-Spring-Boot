package com.movie.movieApp.exceptions;

public class FileAlreadyExistsException extends RuntimeException
{
    public FileAlreadyExistsException(final String resourceName, final String fieldName, final Integer fieldValue)
    {
        super(String.format("%s exists with %s : %s", resourceName, fieldName, fieldValue));
    }
}
