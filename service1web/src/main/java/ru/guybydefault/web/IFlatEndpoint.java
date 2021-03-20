package ru.guybydefault.web;

import ru.guybydefault.domain.Transport;
import ru.guybydefault.dto.FlatDto;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface IFlatEndpoint {

    @WebMethod
    FlatDto findById(@WebParam(name = "id") Integer id);

    @WebMethod
    FlatDto[] findAll();

    @WebMethod
    PageableFlats findAllByParams(@WebParam(name = "page") Integer page,
                                  @WebParam(name = "size") Integer pageSize,
                                  @WebParam(name = "filter") String filter,
                                  @WebParam(name = "sort") String sort);

    @WebMethod
    long numberOfRoomsAverage();

    @WebMethod
    FlatDto anyFlatWithMaxTransport();

    @WebMethod
    long transportGreaterThan(@WebParam(name = "value") Transport transport);

    @WebMethod
    FlatDto saveFlat(@WebParam(name = "flat") FlatDto flatDto);

    @WebMethod
    boolean deleteFlat(int id);

}
