package edu.mns.cda.clubcaninbackend.controller;


import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.dao.RaceDao;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Race", description = "API pour manipuler les races")
@RequiredArgsConstructor
@RequestMapping("/race")
public class RaceController {

    protected final RaceDao raceDao;


    @GetMapping("/list")
    @JsonView("")
}
