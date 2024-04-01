package devarea.fr.db.data;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface DBItem {

    Document toDocument();
    Bson toUpdates();
}
