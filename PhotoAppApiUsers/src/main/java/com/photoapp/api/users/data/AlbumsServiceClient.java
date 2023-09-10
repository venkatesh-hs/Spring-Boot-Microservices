package com.photoapp.api.users.data;

import com.photoapp.api.users.ui.model.AlbumResponseModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

//@FeignClient(name = "albums-ws", fallbackFactory = AlbumsFallbackFactory.class)
@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {
    @GetMapping("/users/{id}/albums")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
    List<AlbumResponseModel> getAlbums(@PathVariable String id);

    default List<AlbumResponseModel> getAlbumsFallback(String id, Throwable exception) {
        System.out.println("userId : " + id);
        System.out.println("Exception from getAlbumsFallback : " + exception.getMessage());
        return Collections.emptyList();
    }
}

/*@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient> {

    @Override
    public AlbumsServiceClient create(Throwable cause) {
        return new AlbumsServiceClientFallBack(cause);
    }
}

@Slf4j
class AlbumsServiceClientFallBack implements AlbumsServiceClient {

    private final Throwable cause;

    public AlbumsServiceClientFallBack(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public List<AlbumResponseModel> getAlbums(String id) {
        if (cause instanceof FeignException && ((FeignException) cause).status() == HttpStatus.NOT_FOUND.value()) {
            log.error("404 error took place while fetching getAlbums from the service with userId : {}. Error Message : {}", id, cause.getLocalizedMessage());
        } else {
            log.error("Other error took place : {}", cause.getLocalizedMessage());
        }
        return Collections.emptyList();
    }
}*/
