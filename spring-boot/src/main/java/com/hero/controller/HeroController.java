package com.hero.controller;

import com.hero.model.Hero;
import com.hero.repository.HeroRepository;
import com.hero.exception.HeroNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class HeroController {

    private final HeroRepository repository;

    HeroController(HeroRepository repository) {
        this.repository = repository;
    }

    /**
     * Look up all employees, and transform them into a REST collection resource. Then return them through Spring Web's
     * {@link ResponseEntity} fluent API.
     */

    @GetMapping("/heroes")
    ResponseEntity<CollectionModel<EntityModel<Hero>>> all() {

        List<EntityModel<Hero>> heroes = StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(hero -> new EntityModel<>(hero,
                        linkTo(methodOn(HeroController.class).one(hero.getId())).withSelfRel(),
                        linkTo(methodOn(HeroController.class).all()).withRel("heroes")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new CollectionModel<>(heroes,
                linkTo(methodOn(HeroController.class).all()).withSelfRel()));
    }

    @PostMapping("/heroes")
    ResponseEntity newHero(@RequestBody Hero newHero) {
        try {
            Hero savedHero = repository.save(newHero);

            EntityModel<Hero> heroResource = new EntityModel<>(savedHero,
                    linkTo(methodOn(HeroController.class).one(savedHero.getId())).withSelfRel());
            return  ResponseEntity.created(new URI(heroResource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                    .body(heroResource);
        } catch (URISyntaxException e) {
            return  ResponseEntity.badRequest().body("Unable to create " + newHero);
        }
    }

    /**
     * Look up a single {@link Hero} and transform it into a REST resource. Then return it through Spring Web's
     * {@link ResponseEntity} fluent API.
     *
     * @param id
     */

    @GetMapping("/heroes/{id}")
    ResponseEntity<EntityModel<Hero>> one(@PathVariable long id) {

        return repository.findById(id)
                .map(hero -> new EntityModel<>(hero,
                        linkTo(methodOn(HeroController.class).one(hero.getId())).withSelfRel(),
                        linkTo(methodOn(HeroController.class).all()).withRel("heroes")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update existing employee then return a Location header.
     *
     * @param newHero
     * @param id
     * @return
     */
    @PutMapping("/heroes/{id}")
    ResponseEntity<?> updateHero(@RequestBody Hero newHero, @PathVariable long id) {

        Hero heroToUpdate = newHero;
        heroToUpdate.setId(id);
        repository.save(heroToUpdate);

        Link newlyCreatedLink = linkTo(methodOn(HeroController.class).one(id)).withSelfRel();

        try {
            return ResponseEntity.noContent().location(new URI(newlyCreatedLink.getHref())).build();
        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body("Unable to update " + heroToUpdate);
        }
    }

    @DeleteMapping("/heroes/{id}")
    ResponseEntity<?> deleteHero(@PathVariable long id) {

        return repository.findById(id)
                .map(hero -> {
                    repository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}