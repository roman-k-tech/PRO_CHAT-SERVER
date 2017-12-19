import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageList
{
    private static final MessageList msgList = new MessageList();
    private static final int LIMIT = 100;
    private final Gson gson;
    private final List<Message> list = new LinkedList<>();
    private static ArrayList<User> users = AuthorizeServlet.getUsers();

    public static MessageList getInstance() {
        return msgList;
    }

    private MessageList() {
        gson = new GsonBuilder().create();
    }

    public synchronized boolean add(Message message)
    {
        String to = message.getTo();

        for (User users : users)
        {
            if (to.equals("All") || to.equals(users.getLogin())) {
                if (list.size() + 1 == LIMIT) {
                    list.remove(0);
                }
                list.add(message);
                return true;
            }
        }
        return false;
    }

    public synchronized String toJSON(int n) {
        if (n == list.size())
            return null;
        return gson.toJson(new JsonMessages(list, n));
    }
}