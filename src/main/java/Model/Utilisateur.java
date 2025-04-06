package Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Entity


@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_utulisateur;
    private String nom;
    private String email;
    private String motDePasse;
    private int enfantCount;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Department service;
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Conge> listeConges;
    @Lob // Indique qu'on stocke un gros objet
    @Column(columnDefinition = "LONGBLOB") // Stocker l'image en BLOB
    private byte[] profilePicture;
	public Long getId() {
		return id_utulisateur;
	}
	public void setId(Long id_utulisateur) {
		this.id_utulisateur = id_utulisateur;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMotDePasse() {
		return motDePasse;
	}
	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}
	public int getEnfantCount() {
		return enfantCount;
	}
	public void setEnfantCount(int enfantCount) {
		this.enfantCount = enfantCount;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Department getService() {
		return service;
	}
	public void setService(Department service) {
		this.service = service;
	}
	public List<Conge> getListeConges() {
		return listeConges;
	}
	public void setListeConges(List<Conge> listeConges) {
		this.listeConges = listeConges;
	}
	public byte[] getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}
	
    
    
}