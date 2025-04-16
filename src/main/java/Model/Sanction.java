package Model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class Sanction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Utilisateur employe;
    
    private LocalDate dateSanction;
    
    private String motif;
    
    private String typeSanction;
    
    // Getters et Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getEmploye() {
        return employe;
    }

    public void setEmploye(Utilisateur employe) {
        this.employe = employe;
    }

    public LocalDate getDateSanction() {
        return dateSanction;
    }

    public void setDateSanction(LocalDate dateSanction) {
        this.dateSanction = dateSanction;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getTypeSanction() {
        return typeSanction;
    }

    public void setTypeSanction(String typeSanction) {
        this.typeSanction = typeSanction;
    }
}