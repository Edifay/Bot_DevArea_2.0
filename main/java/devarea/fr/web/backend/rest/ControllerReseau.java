package devarea.fr.web.backend.rest;

import devarea.fr.db.DBManager;
import devarea.fr.web.backend.entities.WebReseau;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static devarea.fr.web.SpringBackend.checkStatus;


@CrossOrigin()
@RestController
@RequestMapping("reseaux")
public class ControllerReseau {

    @GetMapping(value = "links_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebReseau[] links_list() {
        checkStatus();

        return DBManager.getReseaux().toArray(WebReseau[]::new);
    }

}
