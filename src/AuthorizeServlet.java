import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorizeServlet extends HttpServlet
{
    private static Map<String, String> credentials = new HashMap<>();
    private static ArrayList<User> users = new ArrayList<>();
    private HttpSession httpSession = null;

    public AuthorizeServlet() {
        credentials.put("admin", "admin");
        credentials.put("user1", "user1");
        credentials.put("user2", "user2");
        credentials.put("user3", "user3");
        credentials.put("user4", "user4");
        credentials.put("user5", "user5");
        credentials.put("user6", "user6");
        credentials.put("user7", "user7");
    }

    public static ArrayList<User> getUsers() {
        return users;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse responce) throws IOException {

        String obtainedLogin = request.getParameter("login");
        if (obtainedLogin != null) {

            String obtainedPassword = request.getParameter("password");
            String password = credentials.get(obtainedLogin);
            if (password != null && password.equals(obtainedPassword)) {
                User user = new User(obtainedLogin);
                users.remove(user);
                users.add(user);

                httpSession = request.getSession(true);
//                if (httpSession == null) {
//                    httpSession = request.getSession(true);
//                }
//                httpSession.removeAttribute("login");
                httpSession.setAttribute("login", obtainedLogin);

                MessageList.getInstance().add(new Message("SYSTEM", "User " + obtainedLogin + " has joined chat"));

                responce.setStatus(HttpServletResponse.SC_ACCEPTED);
            }
            else {
                responce.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }

        String userList = request.getParameter("list");
        if (userList != null)
        {
            Gson gson = new GsonBuilder().create();
            ArrayList<String> list = new ArrayList<>();
            String jsonString;
            OutputStream outputStream = responce.getOutputStream();
            for (User user : users)
            {
                list.add(user.getLogin() + " - " + user.getStatus());
            }
            jsonString = gson.toJson(list);
            outputStream.write(jsonString.getBytes((StandardCharsets.UTF_8)));
            return;
        }

        String status = request.getParameter("status");
        if (status != null)
        {
            if (httpSession != null)
            {
                String login = (String) httpSession.getAttribute("login");
                int index = users.indexOf(new User(login));
                User user = users.get(index);
                user.setStatus(status);

                MessageList.getInstance().add(new Message("SYSTEM", "User " + login + " gone " + status));
            }
            return;
        }

        String exit = request.getParameter("exit");
        if (exit != null) {
            int index;
            if (httpSession != null) {
                String login = (String) httpSession.getAttribute("login");
                index = users.indexOf(new User(login));
                users.remove(index);
                httpSession.removeAttribute(login);

                MessageList.getInstance().add(new Message("SYSTEM", "User " + login + " left Chat."));

            }
            return;
        }
    }

    private byte[] requestBodyToArray(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = inputStream.read(buf);
            if (r > 0) byteArrayOutputStream.write(buf, 0, r);
        }
        while (r != -1);

        return byteArrayOutputStream.toByteArray();
    }

}

