package Model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "conge")
public class Conge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_conge;

    private String type; // Annuelle, Maladie, Maternité, Exceptionnel, etc.
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String status; 
    private String motif;

    @ManyToOne
    @JoinColumn(name = "utulisateur_id", nullable = false)
    private Utilisateur utilisateur;
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    
    @JsonIgnore 
    private Service service;

    public Long getId() {
        return id_conge;
    }

    public void setId(Long id) {
        this.id_conge = id;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

	public Long getId_conge() {
		return id_conge;
	}

	public void setId_conge(Long id_conge) {
		this.id_conge = id_conge;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

   

   
}
