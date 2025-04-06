package DTO;

public class AuthResponse {
    private String token;
    private String role;
    private Long Id;
    

    public AuthResponse(String token, String role,long Id) {
        this.token = token;
        this.role = role;
        this.Id=Id;
        
    }
    

	public Long getId() {
		return Id;
	}


	public void setId(Long id) {
		Id = id;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
