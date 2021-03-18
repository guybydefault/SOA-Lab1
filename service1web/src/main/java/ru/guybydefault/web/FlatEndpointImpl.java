package ru.guybydefault.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.dto.FlatDto;
import ru.guybydefault.repository.RemoteFlatRepositoryInterface;
import ru.guybydefault.web.filtering.FlatSpecification;
import ru.guybydefault.web.filtering.FlatSpecificationParser;
import ru.guybydefault.web.sort.Sort;
import ru.guybydefault.web.sort.SortDirection;
import ru.guybydefault.web.sort.SortOrder;
import ru.guybydefault.web.sort.SortParseOrderException;

import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebService(name = "FlatEndpoint", endpointInterface = "ru.guybydefault.web.IFlatEndpoint")
public class FlatEndpointImpl implements IFlatEndpoint {

    private static final String REPOSITORY_JNDI = "java:global/ejb-1.0/flatRepository!ru.guybydefault.repository.RemoteFlatRepositoryInterface";

    @Getter
    @Setter
    private RemoteFlatRepositoryInterface flatRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = Logger.getLogger(getClass().getName());

    public FlatEndpointImpl() {
        initFlatRepository();
    }

    @SneakyThrows
    private static Context createInitialContext() {
        Properties jndiProperties = new Properties();
        // TODO -> https-remoting
//        jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        return new InitialContext(jndiProperties);
    }

    @Override
    public FlatDto[] findAll() {
        return flatRepository.findAll(FlatSpecificationParser.parse(new ArrayList<>(0)));
    }

    @Override
    public Pageable<FlatDto> findAllByParams(Integer pageParam, Integer pageSizeParam, String filter, String sort) {
        Integer page = Optional.of(pageParam).orElse(0);
        Integer size = Optional.of(pageSizeParam).orElse(20);
        String[] filterParams = filter.split("&");
        String[] sortParams = sort.split("&");
        FlatSpecification flatSpecification = FlatSpecificationParser.parse(getCriteria(filterParams));
        PageRequest pageRequest = parsePageRequest(size, page, sortParams);
        return flatRepository.findAll(flatSpecification, pageRequest);
    }

    @Override
    public FlatDto findById(Integer id) {
        return flatRepository.findById(id);
    }

    @Override
    public long numberOfRoomsAverage() {
        return flatRepository.getAverageNumberOfRooms();
    }

    @Override
    public FlatDto anyFlatWithMaxTransport() {
        return flatRepository.findAnyFlatWithMaxTransport();
    }


    @Override
    public FlatDto saveFlat(FlatDto flatDto) {
        return flatRepository.save(flatDto);
    }

    @Override
    public boolean deleteFlat(int id) {
        return flatRepository.delete(id);
    }

    @Override
    public long transportGreaterThan(Transport transport) {
        return flatRepository.countByTransportGreaterThan(transport);
    }

    @SneakyThrows
    private void initFlatRepository() {
        flatRepository = (RemoteFlatRepositoryInterface) createInitialContext().lookup(REPOSITORY_JNDI);
    }

    private PageRequest parsePageRequest(Integer pageParam, Integer sizeParam, String[] sortValues) {
        int page = Optional.of(pageParam).orElse(0);
        int size = Optional.of(sizeParam).orElse(20);
        Sort sort = parseSort(sortValues);

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
