package ru.guybydefault.lab2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.guybydefault.lab2.domain.Flat;
import ru.guybydefault.lab2.exception.FlatNotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("agency")
public class MainController {

    @Autowired
    private FlatService flatService;

    @GetMapping("/get-cheapest/{id1}/{id2}")
    public ResponseEntity getCheapestFlat(@PathVariable("id1") Integer id1, @PathVariable("id2") Integer id2) {
        try {
            return ResponseEntity.of(Optional.of(flatService.getCheapestFlat(id1, id2)));
        } catch (FlatNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-ordered-by-time-to-metro/{by-transport}/{desc}")
    public ResponseEntity<List<Flat>> getFlatsOrderedByTimeToMetro(@PathVariable("by-transport") String byTransport, @PathVariable("desc") String desc) {
        try {
            Sort.Direction sortDirection = Sort.Direction.fromString(desc);
            if (byTransport.equals("transport")) {
                return ResponseEntity.of(Optional.of(flatService.getFlatsSortedByTransportDistance(sortDirection)));
            } else if (byTransport.equals("foot")) {
                return ResponseEntity.of(Optional.of(flatService.getFlatsSortedByFootDistance(sortDirection)));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }


    }
}
