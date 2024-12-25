package com.example.demo.service;

import com.example.demo.model.Produit;
import com.example.demo.repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    @Qualifier("journalChannel")
    private MessageChannel journalChannel;

    public void logMessage(String message) {
        boolean envoyé = journalChannel.send(MessageBuilder.withPayload(message).build());
        if (!envoyé) {
            throw new RuntimeException("Impossible d'envoyer le message via journalChannel");
        }
    }

    public Produit ajouterProduit(Produit produit) {
        Produit sauvegardé = produitRepository.save(produit);
        logMessage("Produit ajouté : " + sauvegardé.getNom());
        return sauvegardé;
    }

    public List<Produit> obtenirTousLesProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> obtenirProduitParId(Long id) {
        return produitRepository.findById(id);
    }

    public Produit mettreAJourProduit(Long id, Produit produitDetails) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        produit.setNom(produitDetails.getNom());
        produit.setQuantite(produitDetails.getQuantite());
        produit.setPrix(produitDetails.getPrix());
        Produit sauvegardé = produitRepository.save(produit);
        logMessage("Produit mis à jour : " + sauvegardé.getNom());
        return sauvegardé;
    }

    public void supprimerProduit(Long id) {
        produitRepository.deleteById(id);
        logMessage("Produit supprimé : ID " + id);
    }
}
