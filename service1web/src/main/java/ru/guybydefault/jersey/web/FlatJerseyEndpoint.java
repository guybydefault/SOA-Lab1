package ru.guybydefault.jersey.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.dto.FlatDto;
import ru.guybydefault.repository.RemoteFlatRepositoryInterface;
import ru.guybydefault.web.PageRequest;
import ru.guybydefault.web.filtering.FlatSpecification;
import ru.guybydefault.web.filtering.FlatSpecificationParser;
import ru.guybydefault.web.filtering.SpecificationParserException;
import ru.guybydefault.web.sort.Sort;
import ru.guybydefault.web.sort.SortDirection;
import ru.guybydefault.web.sort.SortOrder;
import ru.guybydefault.web.sort.SortParseOrderException;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped()
@Path("/flats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces({"application/json"})
public class FlatJerseyEndpoint {

    private static final String REPOSITORY_JNDI = "java:global/ejb-1.0/flatRepository!ru.guybydefault.repository.RemoteFlatRepositoryInterface";

    @javax.ws.rs.core.Context
    HttpServletRequest req;
    @javax.ws.rs.core.Context
    HttpServletResponse resp;

    @Getter
    @Setter
    private RemoteFlatRepositoryInterface flatRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = Logger.getLogger(getClass().getName());

    public FlatJerseyEndpoint() {
        initFlatRepository();
    }

    @SneakyThrows
    private static Context createInitialContext() {
        Properties jndiProperties = new Properties();
        // TODO -> https-remoting
//        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        return new InitialContext(jndiProperties);
    }

    @SneakyThrows
    private void initFlatRepository() {
        flatRepository = (RemoteFlatRepositoryInterface) createInitialContext().lookup(REPOSITORY_JNDI);
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet() throws IOException {
        return findAll(req, resp);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Integer id) throws IOException {
        if (id == null) {
            // "Id is not correct"
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
        return findOne(id);
    }


    @GET
    @Path("/number-of-rooms/average")
    @Produces(MediaType.APPLICATION_JSON)
    public Response numberOfRoomsAverage() throws IOException {
        return Response.ok(flatRepository.getAverageNumberOfRooms()).build();
    }

    @GET
    @Path("/transport/max")
    @Produces(MediaType.APPLICATION_JSON)
    public Response anyFlatWithMaxTransport() throws IOException {
        return Response.ok(flatRepository.findAnyFlatWithMaxTransport()).build();
    }

    @GET
    @Path("/transport/greater-than")
    @Produces(MediaType.APPLICATION_JSON)
    public Response transportGreaterThan() throws IOException {
        return findFlatsTransportGreaterThan(req);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPost() throws IOException {
        FlatDto flat = parseFlat(req, resp);
        if (flat == null || !validate(flat, resp)) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        } else if (flat.getId() != 0) {
            // ID should not be sent in post request
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        }

        flat = flatRepository.save(flat);
        return Response
                .created(URI.create(flat.getId() + "/"))
                .type(MediaType.APPLICATION_JSON)
                .entity(flat)
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPut(@PathParam("id") Integer id) throws IOException {
        if (id == null) {
            // Correct id has not been provided
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        }
        FlatDto flat = flatRepository.findById(id);
        if (flat == null) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
        LocalDateTime creationDate = flat.getCreationDate();
        flat = parseFlat(req, resp);
        if (flat == null || !validate(flat, resp)) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        }
        flat.setCreationDate(creationDate);

        flat = flatRepository.save(flat);
        return Response.ok(flat).build();
    }

    @DELETE
    @Path("{id}")
    public Response doDelete(@PathParam("id") Integer id) throws IOException {
        if (id == null) {
            // Correct id has not been provided
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (flatRepository.delete(id)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private Response findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String[] criteriaValues = req.getParameterValues("filter");
            FlatSpecification flatSpecification = FlatSpecificationParser.parse(getCriteria(criteriaValues));
            if (!req.getParameterMap().containsKey("size") && !req.getParameterMap().containsKey("page")) {
                return Response.ok(flatRepository.findAll(flatSpecification)).build();
            } else {
                PageRequest pageRequest = parsePageRequest(req);
                System.out.println(flatRepository);
                return Response.ok(flatRepository.findAll(flatSpecification, pageRequest)).build();
            }
        } catch (SpecificationParserException e) {
//            Filter parameters parsing failed
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (SortParseOrderException e) {
//            Sort order is not correct
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        }
    }

    private Optional<Integer> parseInt(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }


    private PageRequest parsePageRequest(HttpServletRequest request) {
        int page = parseInt(request.getParameter("page")).orElse(0);
        int size = parseInt(request.getParameter("size")).orElse(20);
        Sort sort = parseSort(request.getParameterValues("sort"));

        return new PageRequest(page, size, sort);
    }

    private Sort parseSort(String[] values) {
        if (values == null) {
            return new Sort(new ArrayList<>());
        }

        try {
            List<SortOrder> orders = Arrays.stream(values)
                    .map(x -> x.split(","))
                    .map(x -> {
                        //TODO check that x[0] is fieldName
                        return new SortOrder(x[0].split("\\."), SortDirection.valueOf(x[1]));
                    })
                    .collect(Collectors.toList());
            return new Sort(orders);
        } catch (IllegalArgumentException e) {
            throw new SortParseOrderException(e);
        }
    }

    private List<List<String>> getCriteria(String[] values) {
        if (values == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(values)
                .map(x -> Arrays.stream(x.split(",")).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private FlatDto parseFlat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), FlatDto.class);
        } catch (JsonParseException | JsonMappingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private Response findFlatsTransportGreaterThan(HttpServletRequest req) throws IOException {
        try {
            String transportParam = req.getParameter("value");
            if (transportParam == null) {
                // value not provided
                return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
            }
            long count = flatRepository.countByTransportGreaterThan(Transport.valueOf(transportParam));
            return Response.ok(count).build();
        } catch (IllegalArgumentException e) {
            // "Transport value is unknown. Available values: " + Arrays.toString(Transport.values())
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        }
    }

    private Response findOne(Integer id) throws IOException {
        FlatDto flat = flatRepository.findById(id);
        if (flat == null) {
            //"FLat by given id not found"
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }

        return Response.ok(flat).build();
    }

    private boolean validate(FlatDto flat, HttpServletResponse resp) throws IOException {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<FlatDto>> violationSet = validator.validate(flat);
        if (!violationSet.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Entity validation failed");
            objectMapper.writeValue(resp.getWriter(), violationSet);
            return false;
        }
        return true;
    }


}
