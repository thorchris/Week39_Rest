package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone) throws MissingInputException {
        if((fName.length() == 0) || (lName.length() == 0)){
            throw new MissingInputException("Either first name or last name missing in trying to add a new person");
        }
        EntityManager em = getEntityManager();
        Person person = new Person(fName, lName, phone);
        try{
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        PersonDTO personDTO = new PersonDTO(person);
        return personDTO;
    }

    @Override
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null){
            throw new PersonNotFoundException(String.format("Person with id: (%d) not found, try something else", id));
        } else {
        try{
            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        }
    }
    
    @Override
    public PersonDTO getPerson(int id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, id);
            if (person == null) {
                throw new PersonNotFoundException(String.format("Person with id: (%d) not found, try something else", id));
            } else {
                PersonDTO personDTO = new PersonDTO(person);
                return personDTO;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query2 = em.createNamedQuery("Person.getAllRows");

            List<Person> personList = query2.getResultList();
            PersonsDTO  personDTO = new PersonsDTO(personList);

            return personDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException {
        if ((p.getfName().length() == 0) || (p.getlName().length() == 0)) {
            throw new MissingInputException("Either first name or last name missing in trying to edit person");
        }
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();

            Person person = em.find(Person.class, p.getId());
            if (person == null) {
                throw new PersonNotFoundException(String.format("Person with id: (%d) not found, try something else", p.getId()));
            } else {

                person.setFirstName(p.getfName());
                person.setLastName(p.getlName());
                person.setPhone(p.getPhone());
                person.setLastEdited();
            }

            em.merge(person);
            em.getTransaction().commit();
            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
            
        } finally {
            em.close();
        }
    }

    public void populateDB() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            Person p1 = new Person("Thor", "Christensen", "45454545");
            Person p2 = new Person("Frederik", "Dahl", "30303030");
            em.persist(p1);
            em.persist(p2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}
