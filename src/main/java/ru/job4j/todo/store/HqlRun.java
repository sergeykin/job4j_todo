package ru.job4j.todo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import ru.job4j.todo.model.Candidate;
import ru.job4j.todo.model.Student;


public class HqlRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

//            Candidate one = Candidate.of("Alex", "Java", 50001);
//            Candidate two = Candidate.of("Nikolay", "Python", 50002);
//            Candidate three = Candidate.of("Nikita", "C#", 50003);
//
//            session.save(one);
//            session.save(two);
//            session.save(three);

            Query query = session.createQuery("from Candidate");
            for (Object st : ( query).list()) {
                System.out.println(st);
            }


            query = session.createQuery("from Candidate s where s.id = :fId");
            query.setParameter("fId", 4);
            System.out.println(query.uniqueResult());

            query = session.createQuery("from Candidate s where s.name = :fName");
            query.setParameter("fName", "Nikolay");
            System.out.println(query.uniqueResult());

            query = session.createQuery(
                    "update Candidate s set s.name = :name, s.experience = :experience where s.id = :fId"
            );
            query.setParameter("name", "alex2");
            query.setParameter("experience", "Java2");
            query.setParameter("fId", 4);
            query.executeUpdate();
            session.getTransaction().commit();
            session.close();

            session = sf.openSession();
            session.beginTransaction();

            query = session.createQuery("from Candidate s where s.id = :fId");
            query.setParameter("fId", 4);
            System.out.println(query.uniqueResult());

            session.createQuery("delete from Candidate where id = :fId")
                    .setParameter("fId", 4)
                    .executeUpdate();

            session.getTransaction().commit();
            session.close();

            session = sf.openSession();
            session.beginTransaction();

            query = session.createQuery("from Candidate");
            for (Object st : ( query).list()) {
                System.out.println(st);
            }

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
