package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.model.ItemCategory;
import ru.job4j.todo.model.Role;
import ru.job4j.todo.model.User;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class HbnStore implements Store, AutoCloseable {

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    private static final class Lazy {
        private static final Store INST = new HbnStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Item add(Item item) {
        return this.tx(
                session -> {
                    session.save(item);
                    return item;
                }
        );
    }

    @Override
    public boolean replace(Integer id, Item item) {
        return this.tx(
                session -> {
                    boolean result = false;
                    Item itemold = this.findById(id);
                    if (itemold != null) {
                        itemold.setDesc(item.getDesc());
                        itemold.setCreated(item.getCreated());
                        itemold.setDone(item.getDone());
                        itemold.setUser(item.getUser());
                        itemold.setItemCategories(item.getItemCategories());

                        session.update(itemold);
                        result = true;
                    }
                    return result;
                }
        );
    }

    @Override
    public boolean delete(Integer id) {
        return this.tx(
                session -> {
                    boolean result = false;
                    Item item = this.findById(id);
                    if (item != null) {
                        session.delete(item);
                        result = true;
                    }
                    return result;
                }
        );
    }

    @Override
    public List<Item> findAll() {
        return this.tx(
                session -> session.createQuery("select item from ru.job4j.todo.model.Item item join fetch item.itemCategories").list()
        );
    }

    @Override
    public Item findById(Integer id) {
        return this.tx(
                session -> session.get(Item.class, id)
        );
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    public static void main(String[] args) throws Exception {
        Store hbmTracker = HbnStore.instOf();
        System.out.println(hbmTracker.findAllItemCategory());
        User user = hbmTracker.findUserByName("user1");
        Item item = new Item("new Item", new Timestamp(System.currentTimeMillis()), false, user);
        List<ItemCategory> itemCategories = new ArrayList<>();
        itemCategories.add(hbmTracker.findItemCategoryById(1));
        itemCategories.add(hbmTracker.findItemCategoryById(2));
        item.setItemCategories(itemCategories);

        hbmTracker.add(item);
        System.out.println(item);
        //hbmTracker.delete(item.getId());
        System.out.println(item);
        hbmTracker.close();
    }

    @Override
    public Role findRoleById(Integer id) {
        return this.tx(
                session -> session.get(Role.class, id)
        );
    }

    @Override
    public User findUserByName(String name) {
        return this.tx(
                session -> {
                    User user = null;
                    final Query query = session.createQuery("FROM ru.job4j.todo.model.User where name = :name");
                    query.setParameter("name", name);
                    if (!query.getResultList().isEmpty()) {
                        user = (User) query.getResultList().get(0);
                    }
                    return user;
                }
        );
    }

    @Override
    public Role findRoleByName(String name) {
        return this.tx(
                session -> {
                    Role role = null;
                    final Query query = session.createQuery("FROM ru.job4j.todo.model.Role where name = :name");
                    query.setParameter("name", name);
                    if (!query.getResultList().isEmpty()) {
                        role = (Role) query.getResultList().get(0);
                    }
                    return role;
                }
        );
    }

    @Override
    public User save(User user) {
        return this.tx(
                session -> {
                    User userold = this.findUserByName(user.getName());
                    if (userold != null) {
                        //userold.setRole(user.getRole());
                        session.save(userold);
                    } else {
                        session.save(user);
                    }
                    return userold != null ? userold : user;
                }
        );

    }

    @Override
    public Role save(Role role) {
        return this.tx(
                session -> {
                    Role roleold = this.findRoleByName(role.getName());
                    if (roleold == null) {
                        session.save(role);
                    }
                    return roleold != null ? roleold : role;
                }
        );
    }

    @Override
    public ItemCategory findItemCategoryById(Integer id) {
        return this.tx(
                session -> session.get(ItemCategory.class, id)
        );
    }

    @Override
    public List<ItemCategory> findAllItemCategory() {
        return this.tx(
                session -> session.createQuery("from ru.job4j.todo.model.ItemCategory order by name desc ").list()
        );
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
