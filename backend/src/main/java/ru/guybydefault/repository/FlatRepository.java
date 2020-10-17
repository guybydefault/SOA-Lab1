package ru.guybydefault.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.guybydefault.domain.Flat;
import ru.guybydefault.domain.Transport;

@Repository
public interface FlatRepository extends CrudRepository<Flat, Integer>, JpaSpecificationExecutor<Flat> {

    @Query("SELECT AVG(f.numberOfRooms) from Flat f")
    long getAverageNumberOfRooms();

    @Query(nativeQuery = true, value = "SELECT * from Flat f where f.transport = (SELECT MAX(fi.transport) FROM Flat fi) LIMIT 1")
    Flat findAnyFlatWithMaxTransport();

    long countByTransportGreaterThan(Transport transport);
}
