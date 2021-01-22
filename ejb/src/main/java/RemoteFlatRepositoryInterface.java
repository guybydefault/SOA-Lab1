import ru.guybydefault.domain.Flat;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.web.PageRequest;
import ru.guybydefault.web.Pageable;
import ru.guybydefault.web.filtering.FlatSpecification;

import javax.ejb.Remote;

@Remote
public interface RemoteFlatRepositoryInterface {

    //    @Query("SELECT AVG(f.numberOfRooms) from Flat f")
    public long getAverageNumberOfRooms();

    //    @Query(nativeQuery = true, value = "SELECT * from Flat f where f.transport = (SELECT MAX(fi.transport) FROM Flat fi) LIMIT 1")
    public Flat findAnyFlatWithMaxTransport();

    public long countByTransportGreaterThan(Transport transport);

    public Flat save(Flat f);

    public Flat findById(Integer id);

    public boolean delete(Integer flatId);

    public Iterable<Flat> findAll(FlatSpecification spec);

    public Pageable<Flat> findAll(FlatSpecification spec, PageRequest pageRequest);
}
