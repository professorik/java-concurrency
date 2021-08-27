package bsa.java.concurrency.image;

import bsa.java.concurrency.image.dto.SearchResultDTO;
import bsa.java.concurrency.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * @author professorik
 * @created 28/06/2021 - 11:26
 * @project concurrency
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    Image findOneById(UUID id);

    @Query(value = "SELECT cast(id AS VARCHAR(255)) AS imageId, " +
            "hemming(hash,:hash) * 100 AS matchPercent, " +
            "url AS imageUrl " +
            "FROM images " +
            "WHERE hemming(hash,:hash) >= :threshold", nativeQuery = true)
    List<SearchResultDTO> getSearchResult(@Param("hash") long hash, @Param("threshold") double threshold);
}
