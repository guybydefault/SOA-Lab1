import ru.guybydefault.domain.Flat;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.web.PageRequest;
import ru.guybydefault.web.Pageable;
import ru.guybydefault.web.filtering.FlatSpecification;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Stateless(name = "flatRepository")
public class FlatRepository implements RemoteFlatRepositoryInterface, LocalFlatRepositoryInterface {

    @PersistenceContext(unitName = "persistenceUnit")
    protected EntityManager entityManager;

    public FlatRepository() {
    }

    //    @Query("SELECT AVG(f.numberOfRooms) from Flat f")
    public long getAverageNumberOfRooms() {
        //TODO
        return 0;
    }

    //    @Query(nativeQuery = true, value = "SELECT * from Flat f where f.transport = (SELECT MAX(fi.transport) FROM Flat fi) LIMIT 1")
    public Flat findAnyFlatWithMaxTransport() {
        //TODO
        return new Flat();
    }

    public long countByTransportGreaterThan(Transport transport) {
        //TODO
        return 0;
    }

    public Flat save(Flat f) {
        if (f.isNew()) {
            entityManager.persist(f);
            return f;
        } else {
            return entityManager.merge(f);
        }
    }

    public Flat findById(Integer id) {
        return entityManager.find(Flat.class, id);
    }

    @Transactional
    public boolean delete(Integer flatId) {
        Flat flat = findById(flatId);
        if (flat != null) {
            entityManager.remove(flat);
            return true;
        }
        return false;
    }

    public long getFlatsNum(FlatSpecification spec) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Flat> root = query.from(Flat.class);

        query = query.select(criteriaBuilder.count(root)).where();
        Predicate pred = spec.toPredicate(root, query, criteriaBuilder);
        query = query.where(pred);
        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional
    public Pageable<Flat> findAll(FlatSpecification spec, PageRequest pageRequest) {
        TypedQuery<Flat> typedQuery = buildQueryBySpec(spec, pageRequest);
        long offset = pageRequest.getPage() * pageRequest.getSize();

        typedQuery.setFirstResult((int) offset);
        typedQuery.setMaxResults((int) offset + pageRequest.getSize());

        List<Flat> flats = typedQuery.getResultList();

        long flatsNum = getFlatsNum(spec);
        long pages = (long) Math.ceil(((float) flatsNum) / pageRequest.getSize());

        Pageable<Flat> pageable = new Pageable<Flat>(flats, pageRequest.getPage(), flats.size(), pageRequest.getSize(), flatsNum, pages);
        return pageable;
    }


    public Iterable<Flat> findAll(FlatSpecification spec) {
        return buildQueryBySpec(spec, null).getResultList();
    }

    private TypedQuery<Flat> buildQueryBySpec(FlatSpecification spec, PageRequest pageRequest) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flat> query = criteriaBuilder.createQuery(Flat.class);
        Root<Flat> root = query.from(Flat.class);

        Predicate pred = spec.toPredicate(root, query, criteriaBuilder);

        query = query.select(root).where(pred);

        if (pageRequest != null) {
            query = query.orderBy(pageRequest.getSort().buildOrder(criteriaBuilder, root));
        }

        TypedQuery<Flat> typedQuery = entityManager.createQuery(query);
        return typedQuery;
    }
}
