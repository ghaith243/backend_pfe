package DTO;

public class AbsenceRequest {
    private Long employeId;
    private String motif;
    private boolean justifiee;
	public Long getEmployeId() {
		return employeId;
	}
	public void setEmployeId(Long employeId) {
		this.employeId = employeId;
	}
	public String getMotif() {
		return motif;
	}
	public void setMotif(String motif) {
		this.motif = motif;
	}
	public boolean isJustifiee() {
		return justifiee;
	}
	public void setJustifiee(boolean justifiee) {
		this.justifiee = justifiee;
	}

    
    
}

