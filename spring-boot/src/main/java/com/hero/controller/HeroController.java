package com.hero.controller;

import com.hero.model.Hero;
import com.hero.repository.HeroRepository;
import io.swagger.annotations.ApiParam;
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
     * Look up all heroes, and transform them into a REST collection resource. Then return them through Spring Web's
     * {@link ResponseEntity} fluent API.
     * @param name is an optional query that is used by the search function in the front-end
     * @return
     */

    @GetMapping("/heroes")
    ResponseEntity<CollectionModel<EntityModel<Hero>>> all(@ApiParam(name="name", value = "Search for Hero's name")
            @RequestParam(value="name", required = false) String name) {

        List<Hero> heroesList = name != null ?
                repository.findByNameContainingIgnoreCase(name)
                : repository.findAll();

        List<EntityModel<Hero>> heroes = StreamSupport.stream(heroesList.spliterator(), false)
                .map(hero -> new EntityModel<>(hero,
                        linkTo(methodOn(HeroController.class).one(hero.getId())).withSelfRel().expand(),
                        linkTo(methodOn(HeroController.class).all(name)).withRel("heroes").expand()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new CollectionModel<>(heroes,
                linkTo(methodOn(HeroController.class).all(name)).withSelfRel()));
    }

    /**
     * Add a new hero with an object, only name is required in the Hero object
     * @param newHero
     * @return
     */
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
     * @param id
     */

    @GetMapping("/heroes/{id}")
    ResponseEntity<EntityModel<Hero>> one(@PathVariable long id) {
        return repository.findById(id)
                .map(hero -> new EntityModel<>(hero,
                        linkTo(methodOn(HeroController.class).one(hero.getId())).withSelfRel(),
                        linkTo(methodOn(HeroController.class).all("")).withRel("heroes")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update existing hero then return a Location header.
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

    /**
     * Delete a hero
     * @param id
     * @return
     */
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