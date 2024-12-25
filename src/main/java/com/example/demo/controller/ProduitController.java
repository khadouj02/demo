package com.example.demo.controller;

import com.example.demo.model.Produit;
import com.example.demo.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    @PostMapping("/log")
    public ResponseEntity<String> envoyerMessage(@RequestParam String message) {
        try {
            produitService.logMessage(message);
            return ResponseEntity.ok("Message logué avec succès : " + message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi du message : " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Produit> ajouterProduit(@RequestBody Produit produit) {
        Produit sauvegardé = produitService.ajouterProduit(produit);
        return ResponseEntity.ok(sauvegardé);
    }

    @GetMapping
    public ResponseEntity<List<Produit>> obtenirTousLesProduits() {
        return ResponseEntity.ok(produitService.obtenirTousLesProduits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> obtenirProduitParId(@PathVariable Long id) {
        return produitService.obtenirProduitParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produit> mettreAJourProduit(@PathVariable Long id, @RequestBody Produit produitDetails) {
        Produit sauvegardé = produitService.mettreAJourProduit(id, produitDetails);
        return ResponseEntity.ok(sauvegardé);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerProduit(@PathVariable Long id) {
        produitService.supprimerProduit(id);
        return ResponseEntity.ok("Produit supprimé avec succès !");
    }
}
