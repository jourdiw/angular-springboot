package com.hero.repository;

import com.hero.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {

    public List<Hero> findByNameContainingIgnoreCase(String name);

}
