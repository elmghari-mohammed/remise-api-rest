package org.example.tpremise.controllers;

import org.example.tpremise.models.Remise;
import org.example.tpremise.service.RemiseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/remises")
public class RemiseController {

    private final RemiseService remiseService;

    public RemiseController(RemiseService remiseService) {
        this.remiseService = remiseService;
    }

    @PostMapping
    public ResponseEntity<Remise> create(@RequestBody Remise remise) {
        Remise created = remiseService.create(remise);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/montant/{montant}")
    public ResponseEntity<Remise> getByMontant(@PathVariable Double montant) {
        return remiseService.findByMontant(montant)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Remise> update(@PathVariable Long id, @RequestBody Remise remise) {
        Remise updated = remiseService.update(id, remise);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        remiseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
