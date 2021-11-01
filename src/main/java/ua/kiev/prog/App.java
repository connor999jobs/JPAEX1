package ua.kiev.prog;



import javax.persistence.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add client");
                    System.out.println("2: add random clients");
                    System.out.println("3: delete client");
                    System.out.println("4: change client");
                    System.out.println("5: view clients");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addClient(sc);
                            break;
                        case "2":
                            insertRandomClients(sc);
                            break;
                        case "3":
                            deleteClient(sc);
                            break;
                        case "4":
                            changeClient(sc);
                            break;
                        case "5":
                            viewClients();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static void addClient(Scanner sc){
        System.out.println("Enter client name");
        String name =sc.nextLine();
        System.out.println("Enter client age");
        String sAge = sc.nextLine();
        int age = Integer.parseInt(sAge);
        em.getTransaction().begin();
        try {
            SimleClient c = new SimleClient( name,age);
            em.persist(c);
            em.getTransaction().commit();
        }
        catch (Exception ex){
            em.getTransaction().rollback();
        }
    }

    public static void deleteClient(Scanner sc){
        System.out.println("Enter client id: ");
        String sId = sc.nextLine();
        long id = Long.parseLong(sId);
        SimleClient c = em.find(SimleClient.class, id);
        if (c == null){
            System.out.println("Client not found");
            return;
        }
        em.getTransaction().begin();
        try {
            em.remove(c);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
        }
    }


    public static void changeClient (Scanner sc){
        System.out.println("Enter client name: ");
        String name = sc.nextLine();
        System.out.println("Enter new age");
        String sAge = sc.nextLine();
        int age = Integer.parseInt(sAge);
        SimleClient c = null;
        try {
            Query query = em.createQuery("SELECT c from SimleClient c WHERE c.name = :name", SimleClient.class);
            query.setParameter("name", name);
            c = (SimleClient)query.getSingleResult();
        } catch (NoResultException ex){
            System.out.println("Client not found ");
            return;
        } catch (NonUniqueResultException ex){
            System.out.println("No Unique result");
            return;
        }
        em.getTransaction().begin();
        try {
            c.setAge(age);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
        }
    }



    public static void insertRandomClients(Scanner sc){
        System.out.println("Enter Client count");
        String sCount =sc.nextLine();
        int count = Integer.parseInt(sCount);

        em.getTransaction().begin();
        try {
            for (int i = 0; i < count; i++) {
                SimleClient c = new SimleClient(randomName(), RND.nextInt(100));
                em.persist(c);
            }
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
        }
    }

    public static void viewClients(){
        Query query = em.createQuery("select c from SimleClient c", SimleClient.class);
        List<SimleClient> list =(List<SimleClient>) query.getResultList();
        for (SimleClient c : list)
            System.out.println(c);
    }

    static final String[] NAMES = {"Vadym ","Andrey", "Vlad", "Dmitriy", "Valentin" };
    static final Random RND = new Random();

    static String randomName(){
        return NAMES[RND.nextInt(NAMES.length)];
    }
}
