package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todo.model.Item;
import java.sql.Timestamp;
import java.util.List;

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
        List result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.todo.model.Item order by created desc ").list();
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public Item findById(Integer id) {
        Item result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.get(Item.class, id);
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    public static void main(String[] args) throws Exception {
        Store hbmTracker = HbnStore.instOf();
        Item item = new Item("name1"
                , new Timestamp(System.currentTimeMillis())
                , false);
        System.out.println(item.toString());
        Item item2 = new Item("name2",
                new Timestamp(System.currentTimeMillis()),
                false
        );
        hbmTracker.add(item);
        hbmTracker.add(item2);
        System.out.println(item.toString());
        System.out.println(hbmTracker.findAll());
        hbmTracker.replace(item.getId(), item2);
        System.out.println(hbmTracker.findAll());
        //hbmTracker.delete(item.getId());
        //hbmTracker.delete(item2.getId());
        //System.out.println(hbmTracker.delete(333333333));

        hbmTracker.close();
    }
}
