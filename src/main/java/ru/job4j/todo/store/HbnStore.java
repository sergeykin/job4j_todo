package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.model.Role;
import ru.job4j.todo.model.User;

import javax.persistence.Query;
import java.sql.Timestamp;
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
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
        return item;
    }

    @Override
    public boolean replace(Integer id, Item item) {
        boolean result = false;
        Item itemold = this.findById(id);
        if (itemold != null) {
            try (Session session = sf.openSession()) {
                session.beginTransaction();
                itemold.setDesc(item.getDesc());
                itemold.setCreated(item.getCreated());
                itemold.setDone(item.getDone());
                itemold.setUser(item.getUser());
                session.update(itemold);
                result = true;
                session.getTransaction().commit();
            }
        }
        return result;
    }

    @Override
    public boolean delete(Integer id) {
        boolean result = false;
        Item item = this.findById(id);
        if (item != null) {
            try (Session session = sf.openSession()) {
                session.beginTransaction();
                session.delete(item);
                result = true;
                session.getTransaction().commit();
            }
        }
        return result;
    }

    @Override
    public List<Item> findAll() {
        return this.tx(
                session -> session.createQuery("from ru.job4j.todo.model.Item order by created desc ").list()
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
        Role role = Role.of("ADMIN");
        role = hbmTracker.save(role);
        User user1 = User.of("Sega",role);
        user1 = hbmTracker.save(user1);
        Item item = new Item("name1"
                , new Timestamp(System.currentTimeMillis())
                , false
                , user1
        );
        User user2 = User.of("Sega",role);
        user2 = hbmTracker.save(user2);
        System.out.println(item.toString());
        Item item2 = new Item("name2",
                new Timestamp(System.currentTimeMillis()),
                false
                , user2
        );
        hbmTracker.add(item);
        hbmTracker.add(item2);
        System.out.println(item.toString());
        System.out.println(hbmTracker.findAll());
        hbmTracker.replace(item.getId(), item2);
        System.out.println(hbmTracker.findAll());
        hbmTracker.delete(item.getId());
        hbmTracker.delete(item2.getId());
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
        User user = null;
            try (Session session = sf.openSession()) {
                session.beginTransaction();
                Query query = session.createQuery("FROM ru.job4j.todo.model.User where name = :name");
                query.setParameter("name", name);
                if (!query.getResultList().isEmpty())  {
                    user = (User)query.getResultList().get(0);
                }
                session.getTransaction().commit();
            }
        return user;
    }

    @Override
    public Role findRoleByName(String name) {
        Role role = null;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Query query = session.createQuery("FROM ru.job4j.todo.model.Role where name = :name");
            query.setParameter("name", name);
            if (!query.getResultList().isEmpty())  {
                role = (Role)query.getResultList().get(0);
            }

            session.getTransaction().commit();
        }
        return role;
    }

    @Override
    public User save(User user) {
        User userold = this.findUserByName(user.getName());
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            if (userold != null) {
                userold.setRole(user.getRole());
                session.save(userold);
            } else {
                session.save(user);
            }

            session.getTransaction().commit();
        }
        return userold!=null?userold:user;
    }

    @Override
    public Role save(Role role) {
        Role roleold = this.findRoleByName(role.getName());
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            if (roleold == null) {
                session.save(role);
            }
            session.getTransaction().commit();
        }
        return roleold!=null?roleold:role;
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
