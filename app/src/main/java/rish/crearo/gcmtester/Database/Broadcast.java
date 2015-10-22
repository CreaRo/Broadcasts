package rish.crearo.gcmtester.Database;

import com.orm.SugarRecord;

/**
 * Created by rish on 10/10/15.
 */
public class Broadcast extends SugarRecord<Broadcast> {

    public String title, content, sender, forGroup, datePost, dateEvent, location;

    public Broadcast() {

    }

    public Broadcast(String title, String content, String sender, String forGroup, String datePost, String dateEvent, String location) {
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.forGroup = forGroup;
        this.datePost = datePost;
        this.dateEvent = dateEvent;
        this.location = location;
    }
}
