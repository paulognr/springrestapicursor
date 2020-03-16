package com.paulognr.cursor.controller;

import java.util.List;
import com.paulognr.cursor.api.CursorDTO;
import com.paulognr.cursor.api.CursorRequest;
import com.paulognr.cursor.api.Cursorable;
import com.paulognr.cursor.api.Sort;
import com.paulognr.cursor.api.SortUtils;
import com.paulognr.cursor.service.PersonDTO;
import com.paulognr.cursor.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/people",
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE })
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(value="/search", method = RequestMethod.GET)
    public ResponseEntity<CursorDTO<PersonDTO>> search(@RequestParam(value="size", required = false) Integer size,
                                                       @RequestParam(value="sort", required = false) List<String> sort,
                                                       @RequestParam(value="before", required = false) String before,
                                                       @RequestParam(value="after", required = false) String after) {

        Cursorable cursorable;
        if (after != null) {
            cursorable = CursorRequest.after(after);
        } else if (before != null) {
            cursorable = CursorRequest.before(before);
        } else {
            Sort sortBy = SortUtils.fromQueryParam(sort);
            if (sortBy == null) {
                sortBy = Sort.by(Sort.Direction.ASC, "id");
            }
            cursorable = CursorRequest.of(size, sortBy);
        }

        return ResponseEntity.ok(new CursorDTO(personService.search(cursorable)));
    }

}
