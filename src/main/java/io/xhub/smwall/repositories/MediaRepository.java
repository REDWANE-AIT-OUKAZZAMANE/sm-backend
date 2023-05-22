package io.xhub.smwall.repositories;

import io.xhub.smwall.domains.Media;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends MongoRepository<Media, String> {
    Optional<Media> findByPinned(boolean pinned);
}
