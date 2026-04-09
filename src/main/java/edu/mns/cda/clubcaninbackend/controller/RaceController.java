package edu.mns.cda.clubcaninbackend.controller;


import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.dao.RaceDao;
import edu.mns.cda.clubcaninbackend.model.Race;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Race", description = "API pour manipuler les races")
@RequiredArgsConstructor
@RequestMapping("/race")
@CrossOrigin
public class RaceController {

    protected final RaceDao raceDao;


    @GetMapping("/list")
    public List<Race> getAllRaces() {
        return raceDao.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Fetch race par id",
            description = "Fetch de la race par l'id dans l'URL'"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation Reussie"),
            @ApiResponse(responseCode = "404", description = "Cet ID de race n'existe pas")
    })
    public ResponseEntity<Race> get(@PathVariable int id) {

        Optional<Race> optionalRace = raceDao.findById(id);

        if(optionalRace.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalRace.get(), HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<Race> createRace(
            @RequestBody
            @Valid() // vérif à effectuer ici
            Race raceToInsert) {

        raceToInsert.setId(null);

        raceDao.save(raceToInsert);

        return new ResponseEntity<>(raceToInsert, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Race> deleteRace(@PathVariable int id) {

        Optional<Race> optionalRace = raceDao.findById(id);

        if(optionalRace.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        raceDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRace(
            @PathVariable int id,
            @RequestBody
            @Valid
            Race raceToUpdate) {

        Optional<Race> optionalRace = raceDao.findById(id);

        if(optionalRace.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // On écrase l'id du json par celui en paramètre
        raceToUpdate.setId(id);

        // On réaffecte les anciennes valeurs qui ne doivent pas être changé
        raceToUpdate.setNom(raceToUpdate.getNom());

        raceDao.save(raceToUpdate);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
