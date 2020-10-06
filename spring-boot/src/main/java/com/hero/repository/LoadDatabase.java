package com.hero.repository;

import com.hero.model.Hero;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(HeroRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Hero("Dr Nice")));
            log.info("Preloading " + repository.save(new Hero("Narco")));
            log.info("Preloading " + repository.save(new Hero("Bombasto")));
            log.info("Preloading " + repository.save(new Hero("Celeritas")));
            log.info("Preloading " + repository.save(new Hero("Magneta")));
            log.info("Preloading " + repository.save(new Hero("Rubberman")));
            log.info("Preloading " + repository.save(new Hero("Dynama")));
            log.info("Preloading " + repository.save(new Hero("Dr IQ")));
            log.info("Preloading " + repository.save(new Hero("Magma")));
            log.info("Preloading " + repository.save(new Hero("Tornado")));
        };
    }
}
