package ru.guybydefault.lab2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.guybydefault.lab2.domain.Coordinates;
import ru.guybydefault.lab2.domain.Flat;
import ru.guybydefault.lab2.exception.FlatNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlatService {

    @Value("${api.url}")
    private String SERVICE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private RestTemplateBuilder builder = new RestTemplateBuilder();
    private ObjectMapper objectMapper = new ObjectMapper();


    FlatService() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() != HttpStatus.NOT_FOUND && (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())) {
                    return true;
                }
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        });
    }


    public Flat getCheapestFlat(int id1, int id2) {
        return getCheapestFlat(getFlat(id1), getFlat(id2));
    }

    public Flat getCheapestFlat(Flat flat1, Flat flat2) {
        return getFlatPrice(flat1) < getFlatPrice(flat2) ? flat1 : flat2;
    }

    public List<Flat> getFlatsSortedByFootDistance(Sort.Direction sortDirection) {
        Flat[] flats = getFlats();
        return Arrays.stream(flats).sorted(
                (flat1, flat2) -> sortDirection == Sort.Direction.ASC ? flatsCompareTo(flat1, flat2) : flatsCompareTo(flat2, flat1)
        ).collect(Collectors.toList());
    }

    public List<Flat> getFlatsSortedByTransportDistance(Sort.Direction sortDirection) {
        Flat[] flats = getFlats();
        return Arrays.stream(flats).sorted(
                (f1, f2) ->
                        sortDirection == Sort.Direction.ASC ? f1.getTransport().compareTo(f2.getTransport()) : f2.getTransport().compareTo(f1.getTransport()))
                .collect(Collectors.toList());
    }

    public Flat[] getFlats() {
        ResponseEntity<Flat[]> responseEntity = builder.build().getForEntity(SERVICE_URL, Flat[].class);
        return responseEntity.getBody();
    }

    private int flatsCompareTo(Flat f1, Flat f2) {
        if (getDistance(f1) < getDistance(f2)) {
            return -1;
        } else if (getDistance(f1) > getDistance(f2)) {
            return 1;
        } else {
            return 0;
        }
    }

    private double getDistance(Flat flat) {
        Coordinates coord = flat.getCoordinates();
        if (coord.getDistance() == null) {
            flat.getCoordinates().setDistance(Math.sqrt(Math.pow(coord.getX(), 2) + Math.pow(coord.getY(), 2)));
        }
        return coord.getDistance();
    }

    private Integer getFlatPrice(Flat flat) {
        return flat.getTransport().ordinal() * 30 + flat.getView().ordinal() * 15 + Math.round(flat.getArea());
    }

    private Flat getFlat(int id) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(SERVICE_URL + "/" + id, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new FlatNotFoundException();
            }

            return objectMapper.readValue(responseEntity.getBody(), Flat.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
