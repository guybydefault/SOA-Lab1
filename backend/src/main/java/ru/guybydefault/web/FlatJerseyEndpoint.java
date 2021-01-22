package ru.guybydefault.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import ru.guybydefault.domain.Flat;
import ru.guybydefault.repository.RemoteFlatRepositoryInterface;
import ru.guybydefault.web.filtering.FlatSpecification;
import ru.guybydefault.web.filtering.FlatSpecificationParser;
import ru.guybydefault.web.filtering.SpecificationParserException;
import ru.guybydefault.web.sort.Sort;
import ru.guybydefault.web.sort.SortDirection;
import ru.guybydefault.web.sort.SortOrder;
import ru.guybydefault.web.sort.SortParseOrderException;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/flats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces({"application/json"})
public class FlatJerseyEndpoint {

    private static final String ENDPOINT_PATH = "/flats";

    @Context
    HttpServletRequest req;
    @Context
    HttpServletResponse resp;

    @EJB(name = "flatRepository")
    @Getter
    @Setter
    private RemoteFlatRepositoryInterface flatRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet() throws IOException {
        return findAll(req, resp);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPost() throws IOException {
        Flat flat = parseFlat(req, resp);
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
        Flat flat = flatRepository.findById(id);
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

    private Flat parseFlat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), Flat.class);
        } catch (JsonParseException | JsonMappingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return null;
        }
    }

    private boolean validate(Flat flat, HttpServletResponse resp) throws IOException {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Flat>> violationSet = validator.validate(flat);
        if (!violationSet.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Entity validation failed");
            objectMapper.writeValue(resp.getWriter(), violationSet);
            return false;
        }
        return true;
    }


}
