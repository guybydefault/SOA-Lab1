package ru.guybydefault.web;

import ru.guybydefault.domain.Transport;
import ru.guybydefault.dto.FlatDto;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface IFlatEndpoint {

    @WebMethod
    FlatDto findById(Integer id);

    @WebMethod
    FlatDto[] findAll();

    @WebMethod
    Pageable<FlatDto> findAllByParams(@WebParam Integer page, @WebParam Integer pageSize, @WebParam String filter, @WebParam String sort);

    @WebMethod
    long numberOfRoomsAverage();

    @WebMethod
    FlatDto anyFlatWithMaxTransport();

    @WebMethod
    long transportGreaterThan(@WebParam Transport transport);

    @WebMethod
    FlatDto saveFlat(@WebParam FlatDto flatDto);

    @WebMethod
    boolean deleteFlat(int id);

}
