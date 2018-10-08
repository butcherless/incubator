package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

//@SpringBootApplication
//@EnableNeo4jRepositories
public class Application {

    private final static Logger log = LoggerFactory.getLogger(Application.class);
/*
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
*/
/*
    @Bean
    CommandLineRunner demo(PersonRepository personRepository) {
        return args -> {

            personRepository.deleteAll();

            Person greg = new Person("Greg", "Pérez");
            personRepository.save(greg);
            log.info("create person: " + greg);
*/
    //PersonManager manager;// = new PersonManager(personRepository);
    //final Person greg1 = manager.getPerson("greg");
    //log.info("get person: " + greg1);


            /*
            Person greg = new Person("Greg");
            Person roy = new Person("Roy");
            Person craig = new Person("Craig");
            Person pinky = new Person("Pumuky");

            List<Person> team = Arrays.asList(greg, roy, craig, pinky);

            log.info("Before linking up with Neo4j...");

            team.stream().forEach(person -> log.info("\t" + person.toString()));

            personRepository.save(greg);
            personRepository.save(roy);
            personRepository.save(craig);
            personRepository.save(pinky);

            greg = personRepository.findByName(greg.getName());
            greg.worksWith(roy);
            greg.worksWith(craig);
            personRepository.save(greg);

            roy = personRepository.findByName(roy.getName());
            roy.worksWith(craig);
            // We already know that roy works with greg
            personRepository.save(roy);

            pinky = personRepository.findByName(pinky.getName());
            pinky.worksWith(roy);
            personRepository.save(pinky);

            // We already know craig works with roy and greg

            log.info("Lookup each person by name...");
            team.stream().forEach(person -> log.info(
                    "\t" + personRepository.findByName(person.getName()).toString()));
            */
    //       };
//    }

}
