package com.photoapp.api.users.data;

import com.photoapp.api.users.ui.model.AlbumResponseModel;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "albums-ws", fallbackFactory = AlbumsFallbackFactory.class)
public interface AlbumsServiceClient {
    @GetMapping("/users/{id}/albums")
    List<AlbumResponseModel> getAlbums(@PathVariable String id);
}

@Component
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
}
