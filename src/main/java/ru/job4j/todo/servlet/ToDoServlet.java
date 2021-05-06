package ru.job4j.todo.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.HbnStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class ToDoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");

        if (user != null) {
            boolean showalltodo = Boolean.valueOf(req.getParameter("showalltodo"));
            boolean showalluser = Boolean.valueOf(req.getParameter("showalluser"));

            List<Item> list = HbnStore.instOf().findAll();
            List<Item> listresult = new LinkedList<>();
            for (Item item:list){
                if ((showalltodo || !item.getDone()) && (showalluser || item.getUser().equals(user))) {
                    listresult.add(item);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            String string = mapper.writeValueAsString(listresult);
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("json");
            resp.getWriter().write(string);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Item item;
        String ids = req.getParameter("id");
        String desc = req.getParameter("desc");

        if (ids != null) {
            int id = Integer.valueOf(ids);
            boolean done = Boolean.valueOf(req.getParameter("done"));
            item = HbnStore.instOf().findById(id);
            item.setDone(done);
            HbnStore.instOf().replace(id, item);
        } else {
            User user = (User) req.getSession().getAttribute("user");
            if (user != null) {
                item = new Item(desc, new Timestamp(System.currentTimeMillis()), false, user);
                HbnStore.instOf().add(item);
            }
        }
        req.setCharacterEncoding("UTF-8");
    }
}
