package lv.bootcamp.shelter.service;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;



import java.util.*;

@Slf4j
public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = new TreeSet<>();
        Map<String, List<Animal>> animalsBySpecies = new HashMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();


         //Step 2 - filling collections
        for(Animal animal :allAnimals){
            String species=animal.getSpecies();

            uniqueSpecies.add(species);

            if(!animalsBySpecies.containsKey(species)){
                animalsBySpecies.put(species,new ArrayList<>());
            }
            animalsBySpecies.get(species).add(animal);

            if (animal.getAgeOptional().isEmpty()){
                animalsNeedingVetInput.add(animal.getName()+ "("+species+")");
            }
        }
        log.info("allAnimals={}, uniqueSpecies={}, animalsNeedingVetInput={}",
        allAnimals.size(),uniqueSpecies,animalsNeedingVetInput);

        for (String species:uniqueSpecies){
            log.info("{}: {} animals", species, animalsBySpecies.get(species).size());

        }

        // Step 3:
           Map<String,Long>totalPerSpecies=allAnimals.stream()
                   .collect(Collectors.groupingBy(Animal::getSpecies,Collectors.counting()));

        Map<String,Long>vaccinatedPerSpecies=allAnimals.stream()
                .filter(Animal::isVaccinated)
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        Map<String,Long>unvaccinatedPerSpecies=allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));
        // oldest animal per species, animals with unknown age are excluded

        Map<String,Animal>oldestPerSpecies=new TreeMap<>();
        for (String species : uniqueSpecies) {
            animalsBySpecies.get(species).stream()
                    .filter(animal -> animal.getAgeOptional().isPresent())
                    .max(Comparator.comparing(Animal::getAge))
                    .ifPresent(oldest -> oldestPerSpecies.put(species, oldest));
        }

        log.info("total = {}: vacinated={}, unvacinated={}", totalPerSpecies,vaccinatedPerSpecies,unvaccinatedPerSpecies);

        oldestPerSpecies.forEach((species, animal) ->
                log.info("Oldest {}: {} (age {})", species, animal.getName(), animal.getAge()));

        return new ShelterReportData(importResult, uniqueSpecies, animalsBySpecies, animalsNeedingVetInput,
                totalPerSpecies, vaccinatedPerSpecies, unvaccinatedPerSpecies, oldestPerSpecies);

    }
}
