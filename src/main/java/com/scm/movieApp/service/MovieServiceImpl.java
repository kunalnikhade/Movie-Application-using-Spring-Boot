package com.scm.movieApp.service;

import com.scm.movieApp.dto.MovieDto;
import com.scm.movieApp.dto.MoviePageResponse;
import com.scm.movieApp.entities.MovieEntity;
import com.scm.movieApp.exceptions.ResourceNotFoundException;
import com.scm.movieApp.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService
{
    @Value("${file.upload-dir}")
    private String path;

    @Value("${file.base-url}")
    private String baseUrl;

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Autowired
    public MovieServiceImpl(final MovieRepository movieRepository, final FileService fileService)
    {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    @Transactional
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException
    {
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename())))
        {
            throw new FileAlreadyExistsException("File is already exists ! Please add another one.");
        }
        // 1. upload the file
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. set the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);

        // 3. Build the full URL for the poster and set it.
        String posterUrl = baseUrl + "/" + uploadedFileName;
        movieDto.setPosterUrl(posterUrl);

        MovieEntity movieEntity = convertToEntity(movieDto);
        return convertToDto(movieRepository.save(movieEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDto getMovieById(Integer movieId)
    {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie Not Found", "Id", movieId));

        return convertToDto(movieEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies()
    {
        List<MovieEntity> movieEntities = movieRepository.findAll();
        return movieEntities.stream().map(movieEntity -> convertToDto(movieEntity)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieDto updateMovie(MovieDto movieDto, MultipartFile file, Integer movieId) throws IOException
    {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie Not Found", "Id", movieId));

        String fileName = movieEntity.getPoster();

        // If a new file is uploaded, delete the old one and update the name
        if (file != null && !file.isEmpty()) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        // Update entity fields
        movieEntity.setTitle(movieDto.getTitle());
        movieEntity.setDirector(movieDto.getDirector());
        movieEntity.setStudio(movieDto.getStudio());
        movieEntity.setReleaseYear(movieDto.getReleaseYear());
        movieEntity.setMovieCast(movieDto.getMovieCast());
        movieEntity.setPoster(fileName);

        // Update the poster URL
        String posterUrl = baseUrl + "/" + fileName;
        movieDto.setPosterUrl(posterUrl);

        return convertToDto(movieRepository.save(movieEntity));
    }


    @Override
    @Transactional
    public void deleteMovie(Integer movieId)
    {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie Not Found", "Id", movieId));
        movieRepository.delete(movieEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MovieEntity> moviePages = movieRepository.findAll(pageable);
        return buildMoviePageResponse(moviePages);
    }

    @Override
    @Transactional(readOnly = true)
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direction)
    {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                    : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<MovieEntity> moviePages = movieRepository.findAll(pageable);
        return buildMoviePageResponse(moviePages);
    }

    // method to remove duplication
    private MoviePageResponse buildMoviePageResponse(Page<MovieEntity> moviePages)
    {
        List<MovieDto> movieDto = moviePages.getContent().stream()
                                                         .map(movieEntity -> convertToDto(movieEntity))
                                                         .collect(Collectors.toList());

        return new MoviePageResponse(movieDto,
                                     moviePages.getNumber(),
                                     moviePages.getSize(),
                                     moviePages.getTotalElements(),
                                     moviePages.getTotalPages(),
                                     moviePages.isLast());
    }

    public MovieDto convertToDto(final MovieEntity movieEntity)
    {
        final MovieDto movieDto = new MovieDto();
        movieDto.setTitle(movieEntity.getTitle());
        movieDto.setDirector(movieEntity.getDirector());
        movieDto.setStudio(movieEntity.getStudio());
        movieDto.setReleaseYear(movieEntity.getReleaseYear());
        movieDto.setMovieCast(movieEntity.getMovieCast());
        movieDto.setPoster(movieEntity.getPoster());
        movieDto.setPosterUrl(baseUrl + "/" + movieEntity.getPoster());
        return movieDto;
    }

    public MovieEntity convertToEntity(final MovieDto movieDto)
    {
        final MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(movieDto.getTitle());
        movieEntity.setDirector(movieDto.getDirector());
        movieEntity.setStudio(movieDto.getStudio());
        movieEntity.setReleaseYear(movieDto.getReleaseYear());
        movieEntity.setMovieCast(movieDto.getMovieCast());
        movieEntity.setPoster(movieDto.getPoster());
        return movieEntity;
    }
}
