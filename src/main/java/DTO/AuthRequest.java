package DTO;



public class AuthRequest {
    private String nom; // Optionnel pour login, requis pour signup
    private String email;
    private String motDePasse;
    private String role; // ADMIN, CHEF, EMPLOYE - Optionnel pour login
    
    private Long serviceId; 
    private int enfantCount;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public int getEnfantCount() {
		return enfantCount;
	}
	public void setEnfantCount(int enfantCount) {
		this.enfantCount = enfantCount;
	}





}
