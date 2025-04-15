package com.pfe.sytemedeconge.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import Model.Utilisateur;
import Model.Sanction;
import Repository.SanctionRepository;

@Service
public class SanctionService {

    @Autowired
    private SanctionRepository sanctionRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Génère un avis de sanction pour un employé et l'envoie par email.
     */
    public void generateAndSendSanctionNotice(Utilisateur employe) {
        System.out.println("🛠️ Génération de la sanction pour : " + employe.getNom() + " " );
        
        Sanction sanction = createSanction(employe);
        byte[] pdfBytes = generateSanctionPdf(employe, sanction);
        sendSanctionEmail(employe, pdfBytes);
    }

    
    /**
     * Crée un enregistrement de sanction dans la base de données.
     */
    private Sanction createSanction(Utilisateur employe) {
        Sanction sanction = new Sanction();
        sanction.setEmploye(employe);
        sanction.setDateSanction(LocalDate.now());
        sanction.setMotif("Dépassement du seuil de 15 jours d'absence dans l'année");
        sanction.setTypeSanction("Convocation disciplinaire");
        
        return sanctionRepository.save(sanction);
    }
    
    /**
     * Génère un PDF de convocation disciplinaire.
     */
    private byte[] generateSanctionPdf(Utilisateur employe, Sanction sanction) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // En-tête
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("CONVOCATION À UN ENTRETIEN DISCIPLINAIRE", titleFont));
            document.add(new Paragraph("\n"));
            
            // Date
            document.add(new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("\n"));
            
            // Informations de l'employé
            document.add(new Paragraph("À l'attention de: " + employe.getNom() + " " ));
            document.add(new Paragraph("\n"));
            
            // Corps du document
            document.add(new Paragraph("Objet: Convocation à un entretien disciplinaire"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Madame, Monsieur,"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(
                "Nous vous informons que vous êtes convoqué(e) à un entretien disciplinaire qui se tiendra le " + 
                LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                " à 10h00 dans les locaux de l'entreprise, bureau de la Direction des Ressources Humaines."
            ));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(
                "Motif: Vous avez dépassé le seuil autorisé de 15 jours d'absence dans l'année courante, " +
                "ce qui constitue un manquement aux obligations professionnelles définies dans le règlement intérieur."
            ));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(
                "Lors de cet entretien, vous pourrez fournir des explications et présenter votre défense. " +
                "Vous avez la possibilité de vous faire assister par une personne de votre choix appartenant au personnel de l'entreprise."
            ));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Veuillez agréer, Madame, Monsieur, l'expression de nos salutations distinguées."));
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("La Direction des Ressources Humaines"));
            
            document.close();
            return out.toByteArray();
            
        } catch (DocumentException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    
    /**
     * Envoie un email avec le PDF de sanction en pièce jointe.
     */
    private void sendSanctionEmail(Utilisateur employe, byte[] pdfBytes) {
        String subject = "IMPORTANT: Convocation à un entretien disciplinaire";
        String body = "Bonjour " +  " " + employe.getNom() + ",\n\n" +
                "Vous trouverez en pièce jointe une convocation à un entretien disciplinaire suite au dépassement " +
                "du seuil autorisé de jours d'absence.\n\n" +
                "Cordialement,\n" +
                "Le Service des Ressources Humaines";
        
        emailService.sendEmailWithAttachment(employe.getEmail(), subject, body, pdfBytes, "convocation_disciplinaire.pdf");
    }
}