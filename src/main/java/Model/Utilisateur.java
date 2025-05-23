package Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
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
    private String resetCode;
    private LocalDateTime resetCodeExpiration;

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
    @OneToMany(mappedBy = "employe")
    @JsonIgnore
    private List<Absence> absencesEnTantQuEmploye;

    @OneToMany(mappedBy = "chef")
    @JsonIgnore
    private List<Absence> absencesValideesEnTantQueChef;
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
	
	public String getResetCode() {
		return resetCode;
	}
	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}
	public LocalDateTime getResetCodeExpiration() {
		return resetCodeExpiration;
	}
	public void setResetCodeExpiration(LocalDateTime resetCodeExpiration) {
		this.resetCodeExpiration = resetCodeExpiration;
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
	public Long getId_utulisateur() {
		return id_utulisateur;
	}
	public void setId_utulisateur(Long id_utulisateur) {
		this.id_utulisateur = id_utulisateur;
	}
	public List<Absence> getAbsencesEnTantQuEmploye() {
		return absencesEnTantQuEmploye;
	}
	public void setAbsencesEnTantQuEmploye(List<Absence> absencesEnTantQuEmploye) {
		this.absencesEnTantQuEmploye = absencesEnTantQuEmploye;
	}
	public List<Absence> getAbsencesValideesEnTantQueChef() {
		return absencesValideesEnTantQueChef;
	}
	public void setAbsencesValideesEnTantQueChef(List<Absence> absencesValideesEnTantQueChef) {
		this.absencesValideesEnTantQueChef = absencesValideesEnTantQueChef;
	}
	
	
    
    
}