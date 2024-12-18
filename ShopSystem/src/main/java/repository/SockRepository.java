package repository;

import entity.SockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SockRepository extends JpaRepository<SockEntity, Long> {
    @Query("SELECT s FROM SockEntity s WHERE s.color = :color AND s.cottonPercentage = :cottonPercentage")
    Optional<SockEntity> findByColorAndCottonPercentage(@Param("color") String color, @Param("cottonPercentage") int cottonPercentage);
    @Query("SELECT s FROM SockEntity s WHERE "
            + "(:color = 'all' OR s.color = :color) "
            + "AND (:operator = 'equal' AND s.cottonPercentage = :cottonPercentage "
            + "     OR :operator = 'moreThan' AND s.cottonPercentage > :cottonPercentage "
            + "     OR :operator = 'lessThan' AND s.cottonPercentage < :cottonPercentage)")
    List<SockEntity> findByFilters(
            @Param("color") String color,
            @Param("operator") String operator,
            @Param("cottonPercentage") int cottonPercentage
    );

}
