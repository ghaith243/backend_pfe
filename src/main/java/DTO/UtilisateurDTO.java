package DTO;

import Model.Department;


public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String email;
    private String role;
    private Long serviceId;  // Ajouter l'ID du service

    public UtilisateurDTO(Long id, String nom, String email, String role, Department service) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
        this.serviceId = service != null ? service.getId() : null; // Récupérer l'ID du service
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

    
    
}
