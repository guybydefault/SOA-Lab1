package ru.guybydefault.repository;

import ru.guybydefault.domain.Transport;
import ru.guybydefault.dto.FlatDto;
import ru.guybydefault.web.PageRequest;
import ru.guybydefault.web.Pageable;
import ru.guybydefault.web.filtering.FlatSpecification;

import javax.ejb.Remote;

@Remote
public interface RemoteFlatRepositoryInterface {

    public long getAverageNumberOfRooms();

    public FlatDto findAnyFlatWithMaxTransport();

    public long countByTransportGreaterThan(Transport transport);

    public FlatDto save(FlatDto f);

    public FlatDto findById(Integer id);

    public boolean delete(Integer flatId);

    public Iterable<FlatDto> findAll(FlatSpecification spec);

    public Pageable<FlatDto> findAll(FlatSpecification spec, PageRequest pageRequest);
}
