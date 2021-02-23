package ru.guybydefault.repository;

import org.modelmapper.ModelMapper;
import ru.guybydefault.domain.Flat;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.dto.FlatDto;
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
import java.util.ArrayList;
import java.util.List;

@Stateless(name = "flatRepository")
public class FlatRepository implements RemoteFlatRepositoryInterface {

    private static ModelMapper modelMapper = new ModelMapper();

    @PersistenceContext(unitName = "persistenceUnit")
    protected EntityManager entityManager;

    public FlatRepository() {
    }

    public long getAverageNumberOfRooms() {
        return ((Double) entityManager.createQuery("SELECT AVG(f.numberOfRooms) from Flat f").getSingleResult()).longValue();
    }

    public FlatDto findAnyFlatWithMaxTransport() {
        List<Flat> flatList = (List<Flat>) entityManager.createQuery("SELECT f from Flat f where f.transport = (SELECT MAX(fi.transport) FROM Flat fi)").getResultList();
        if (flatList.size() > 0) {
            return convert(flatList.get(0));
        } else {
            return null;
        }
    }

    public long countByTransportGreaterThan(Transport transport) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Flat> root = query.from(Flat.class);

        query = query.select(criteriaBuilder.count(root)).where(criteriaBuilder.lessThanOrEqualTo(root.get("transport"), transport));
        return entityManager.createQuery(query).getSingleResult();
    }

    public FlatDto save(FlatDto flatDto) {
        Flat f = convert(flatDto);
        if (f.isNew()) {
            entityManager.persist(f);
            return convert(f);
        } else {
            return convert(entityManager.merge(f));
        }
    }

    public FlatDto findById(Integer id) {
        return convert(findFlatById(id));
    }

    private Flat findFlatById(Integer id) {
        return entityManager.find(Flat.class, id);
    }

    @Transactional
    public boolean delete(Integer flatId) {
        Flat flat = findFlatById(flatId);
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
    public Pageable<FlatDto> findAll(FlatSpecification spec, PageRequest pageRequest) {
        TypedQuery<Flat> typedQuery = buildQueryBySpec(spec, pageRequest);
        long offset = pageRequest.getPage() * pageRequest.getSize();

        typedQuery.setFirstResult((int) offset);
        typedQuery.setMaxResults((int) offset + pageRequest.getSize());

        List<Flat> flats = typedQuery.getResultList();

        long flatsNum = getFlatsNum(spec);
        long pages = (long) Math.ceil(((float) flatsNum) / pageRequest.getSize());

        Pageable<FlatDto> pageable = new Pageable<FlatDto>(convert(flats), pageRequest.getPage(), flats.size(), pageRequest.getSize(), flatsNum, pages);
        return pageable;
    }


    public Iterable<FlatDto> findAll(FlatSpecification spec) {
        return convert(buildQueryBySpec(spec, null).getResultList());
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

    private List<FlatDto> convert(List<Flat> flats) {
        List<FlatDto> flatDtos = new ArrayList<>(flats.size());
        for (Flat flat : flats) {
            FlatDto flatDto = new FlatDto();
            modelMapper.map(flat, flatDto);
            flatDtos.add(flatDto);
        }
        return flatDtos;
    }

    private FlatDto convert(Flat flat) {
        if (flat == null) {
            return null;
        }
        FlatDto flatDto = new FlatDto();
        modelMapper.map(flat, flatDto);
        return flatDto;
    }

    private Flat convert(FlatDto flatDto) {
        if (flatDto == null) {
            return null;
        }
        Flat flat = new Flat();
        modelMapper.map(flatDto, flat);
        return flat;
    }
}
