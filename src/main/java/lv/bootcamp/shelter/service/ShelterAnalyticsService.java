package lv.bootcamp.shelter.service;
import lombok.extern.slf4j.Slf4j;

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

            if(animal.getAge()==null){
                animalsNeedingVetInput.add(animal.getName()+ "("+species+")");
            }
        }
        log.info("allAnimals={}, uniqueSpecies={}, animalsNeedingVetInput={}",
        allAnimals.size(),uniqueSpecies,animalsNeedingVetInput);

        for (String species:uniqueSpecies){
            log.info("{}: {} animals", species, animalsBySpecies.get(species).size());

        }

        // TODO Step 3:
        // Add necessary fields to ShelterReportData
        // Use stream pipelines for:
        // - vaccinated vs unvaccinated counts per species
        // - oldest animal per species (excluding unknown ages)

        return new ShelterReportData(importResult);
    }
}
