package ru.guybydefault.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.guybydefault.domain.Flat;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.repository.FlatRepository;
import ru.guybydefault.web.filtering.FlatSpecification;
import ru.guybydefault.web.filtering.FlatSpecificationParser;
import ru.guybydefault.web.filtering.SortParseOrderException;
import ru.guybydefault.web.filtering.SpecificationParserException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name = "FlatServlet", urlPatterns = "/api/flats/*")
@Component
@Transactional
public class FlatServlet extends HttpServlet {

    private FlatRepository flatRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public FlatServlet() {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    public void setFlatRepository(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Flat flat = parseFlat(request, response);
        if (flat == null || !validate(flat, response)) {
            return;
        } else if (flat.getId() != 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id should not be sent in POST request");
            return;
        }

        flat = flatRepository.save(flat);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setHeader("Location", flat.getId() + "/");
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), flat);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String relativeServletPath = getRelativeServletPath(req);
        if (relativeServletPath.trim().isEmpty() || relativeServletPath.trim().equals("/")) {
            findAll(req, resp);
        } else if (relativeServletPath.equals("/number-of-rooms/average")) {
            returnOK(flatRepository.getAverageNumberOfRooms(), resp);
        } else if (relativeServletPath.equals("/transport/max")) {
            returnOK(flatRepository.findAnyFlatWithMaxTransport(), resp);
        } else if (relativeServletPath.equals("/transport/greater-than")) {
            findFlatsTransportGreaterThan(req, resp);
        } else {
            findOne(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getIntPathVariable("/{id}", getRelativeServletPath(req)).orElse(null);
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getIntPathVariable("/{id}", getRelativeServletPath(req)).orElse(null);
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

    private void findAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String[] criteriaValues = req.getParameterValues("filter");
            FlatSpecification flatSpecification = FlatSpecificationParser.parse(getCriteria(criteriaValues));
            PageRequest pageRequest = parsePageRequest(req);
            returnOK(flatRepository.findAll(flatSpecification, pageRequest), resp);
        } catch (SpecificationParserException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Filter parameters parsing failed");
        } catch (PropertyReferenceException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sort parameters are not correct");
        } catch (SortParseOrderException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sort order is not correct");
        }
    }

    private void findOne(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getIntPathVariable("/{id}", getRelativeServletPath(req)).orElse(null);
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Flat flat = flatRepository.findById(id).orElse(null);
        if (flat == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        returnOK(flat, resp);
    }

    private void findFlatsTransportGreaterThan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String transportParam = req.getParameter("value");
            if (transportParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Value not provided.");
                return;
            }
            long count = flatRepository.countByTransportGreaterThan(Transport.valueOf(transportParam));
            returnOK(count, resp);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Transport value is unknown. Available values: " + Arrays.toString(Transport.values()));
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
        String contextServletPath = request.getServletPath() + request.getContextPath();
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
