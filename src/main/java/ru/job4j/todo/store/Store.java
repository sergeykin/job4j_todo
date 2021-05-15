package ru.job4j.todo.store;

import ru.job4j.todo.model.*;

import java.util.List;

public interface Store extends AutoCloseable {
    Item add(Item item);
    boolean replace(Integer id, Item item);
    boolean delete(Integer id);
    List<Item> findAll();
    List<ItemCategory> findAllItemCategory();
    Item findById(Integer id);
    Role findRoleById(Integer id);
    ItemCategory findItemCategoryById(Integer id);
    Role findRoleByName(String name);

    User findUserByName(String name);
    User save(User user);
    Role save(Role role);
}
