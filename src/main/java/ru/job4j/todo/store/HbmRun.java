package ru.job4j.todo.store;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todo.model.*;

import java.util.List;

public class HbmRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Book book1 = Book.of("book1");
            Book book2 = Book.of("book2");

            Author author1 = Author.of("author1");
            Author author2 = Author.of("author2");

            author1.getBooks().add(book1);
            author1.getBooks().add(book2);

            author2.getBooks().add(book1);
            session.persist(author1);
            session.persist(author2);
//            Person person = session.get(Person.class, 1);
//            session.remove(person);
//            Address one = Address.of("Kazanskaya", "1");
//            Address two = Address.of("Piterskaya", "10");
//
//            Person first = Person.of("Nikolay");
//            first.getAddresses().add(one);
//            first.getAddresses().add(two);
//
//            Person second = Person.of("Anatoliy");
//            second.getAddresses().add(two);
//
//            session.save(first);
//            session.save(second);

//            CarModel carModel1 = CarModel.of("carModel1");
//            CarModel carModel2 = CarModel.of("carModel2");
//
//            CarBrand carBrand1 = CarBrand.of("carBrand");
//            carBrand1.getCarModels().add(carModel1);
//            carBrand1.getCarModels().add(carModel2);
//            session.save(carBrand1);
//            User one = User.of("Petr");
//            User two = User.of("Andrei");
//
//            Role admin = Role.of("ADMIN");
//            admin.getUsers().add(one);
//            admin.getUsers().add(two);
//
//            session.save(admin);

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static <T> T create(T model, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(model);
        session.getTransaction().commit();
        session.close();
        return model;
    }

    public static <T> List<T> findAll(Class<T> cl, SessionFactory sf) {
        Session session = sf.openSession();
        session.beginTransaction();
        List<T> list = session.createQuery("from " + cl.getName(), cl).list();
        session.getTransaction().commit();
        session.close();
        return list;
    }
}
