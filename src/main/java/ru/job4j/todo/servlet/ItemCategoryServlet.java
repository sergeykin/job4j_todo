package ru.job4j.todo.servlet;

import ru.job4j.todo.model.City;
import ru.job4j.todo.model.ItemCategory;
import ru.job4j.todo.store.HbmStore;
import ru.job4j.todo.store.HbnStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ItemCategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ItemCategory> itemCategories = HbnStore.instOf().findAllItemCategory();
        req.setAttribute("categories", itemCategories);
        req.getRequestDispatcher("additem.jsp").forward(req, resp);
    }
}
