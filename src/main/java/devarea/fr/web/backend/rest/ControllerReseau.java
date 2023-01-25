package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.web.backend.entities.WebReseau;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin()
@RestController
public class ControllerReseau {

    @GetMapping(value = "reseaux/links_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebReseau[] links_list() {
        return DBManager.getReseaux().toArray(WebReseau[]::new);
    }

}
