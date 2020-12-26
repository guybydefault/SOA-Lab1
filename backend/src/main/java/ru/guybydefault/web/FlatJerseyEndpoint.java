package ru.guybydefault.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Component;
import ru.guybydefault.domain.Flat;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.repository.FlatRepository;
import ru.guybydefault.web.filtering.FlatSpecification;
import ru.guybydefault.web.filtering.FlatSpecificationParser;
import ru.guybydefault.web.filtering.SortParseOrderException;
import ru.guybydefault.web.filtering.SpecificationParserException;

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
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/flats")
@Component
public class FlatJerseyEndpoint {

    private static final String ENDPOINT_PATH = "/flats";

    @Context
    HttpServletRequest req;
    @Context
    HttpServletResponse resp;
    private FlatRepository flatRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public void setFlatRepository(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet() throws IOException {
        return findAll(req, resp);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Integer id) throws IOException {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "Id is not correct").build();
        }
        return findOne(id);
    }


    @GET
    @Path("/number-of-rooms/average")
    @Produces(MediaType.APPLICATION_JSON)
    public Response numberOfRoomsAverage() throws IOException {
        return buildOkResponse(flatRepository.getAverageNumberOfRooms());
    }

    @GET
    @Path("/transport/max")
    @Produces(MediaType.APPLICATION_JSON)
    public Response anyFlatWithMaxTransport() throws IOException {
        return buildOkResponse(flatRepository.findAnyFlatWithMaxTransport());
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
    public void doPost() throws IOException {
        Flat flat = parseFlat(req, resp);
        if (flat == null || !validate(flat, resp)) {
            return;
        } else if (flat.getId() != 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id should not be sent in POST request");
            return;
        }

        flat = flatRepository.save(flat);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setHeader("Location", flat.getId() + "/");
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), flat);
    }


    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void doPut(@PathParam("id") Integer id) throws IOException {
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Correct id has not been provided.");
            return;
        }
        Flat flat = flatRepository.findById(id).orElse(null);
        if (flat == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        LocalDateTime creationDate = flat.getCreationDate();
        flat = parseFlat(req, resp);
        if (flat == null || !validate(flat, resp)) {
            return;
        }
        flat.setCreationDate(creationDate);

        flat = flatRepository.save(flat);
        returnOK(flat, resp);
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void doDelete(@PathParam("id") Integer id) throws IOException {
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Correct id has not been provided.");
            return;
        }
        Flat flat = flatRepository.findById(id).orElse(null);
        if (flat == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        flatRepository.delete(flat);
        returnOK(resp);
    }

    private void returnOK(HttpServletResponse resp) throws IOException {
        returnOK(null, resp);
    }

    private void returnOK(Object responseEntity, HttpServletResponse resp) throws IOException {
        if (responseEntity == null) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), responseEntity);
    }

    private Response findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String[] criteriaValues = req.getParameterValues("filter");
            FlatSpecification flatSpecification = FlatSpecificationParser.parse(getCriteria(criteriaValues));
            if (!req.getParameterMap().containsKey("size") && !req.getParameterMap().containsKey("page")) {
                return buildOkResponse(flatRepository.findAll(flatSpecification));
            } else {
                PageRequest pageRequest = parsePageRequest(req);
                return buildOkResponse(flatRepository.findAll(flatSpecification, pageRequest));
            }
        } catch (SpecificationParserException e) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST, "Filter parameters parsing failed").build();
        } catch (PropertyReferenceException e) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST, "Sort parameters are not correct").build();
        } catch (SortParseOrderException e) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST, "Sort order is not correct").build();
        }
    }

    private Response findOne(Integer id) throws IOException {
        Flat flat = flatRepository.findById(id).orElse(null);
        if (flat == null) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), "FLat by given id not found").build();
        }

        return buildOkResponse(flat);
    }

    private Response findFlatsTransportGreaterThan(HttpServletRequest req) throws IOException {
        try {
            String transportParam = req.getParameter("value");
            if (transportParam == null) {
                return Response.status(HttpServletResponse.SC_BAD_REQUEST, "Value not provided.").build();
            }
            long count = flatRepository.countByTransportGreaterThan(Transport.valueOf(transportParam));
            return buildOkResponse(count);
        } catch (IllegalArgumentException e) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST, "Transport value is unknown. Available values: " + Arrays.toString(Transport.values())).build();
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

        return PageRequest.of(page, size, sort);
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

    private String getRelativeServletPath(HttpServletRequest request) {
        String contextServletPath = request.getServletPath() + request.getContextPath() + ENDPOINT_PATH;
        return request.getRequestURI().substring(contextServletPath.length());
    }

    private Optional<String> getPathVariable(String template, String path) {
        Matcher variableTemplateMatcher = Pattern.compile("\\{(.*)}").matcher(template);
        Matcher pathMatcher = Pattern.compile(variableTemplateMatcher.replaceFirst("(.*)")).matcher(path);
        if (pathMatcher.matches()) {
            return Optional.of(pathMatcher.group(1));
        } else {
            return Optional.empty();
        }
    }

    private Optional<Integer> getIntPathVariable(String template, String path) {
        try {
            Optional<String> optional = getPathVariable(template, path);
            return optional.map(Integer::parseInt);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Response buildBadRequestResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), message).build();
    }

    private Response buildOkResponse(Object responseEntity) throws IOException {
        if (responseEntity == null) {
            return Response.noContent().build();
        }
        //TODO ContentType application/json? do we need to set?
        return Response.ok().entity(responseEntity).build();
    }

    private Sort parseSort(String[] values) {
        if (values == null) {
            return Sort.unsorted();
        }

        try {
            List<Sort.Order> orders = Arrays.stream(values)
                    .map(x -> x.split(","))
                    .map(x -> new Sort.Order(Sort.Direction.fromString(x[1]), x[0]))
                    .collect(Collectors.toList());
            return Sort.by(orders);
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


}
