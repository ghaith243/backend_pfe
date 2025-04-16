package Model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String motif;

    private boolean justifiee = false;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Utilisateur employe;

    @ManyToOne
    @JoinColumn(name = "chef_id", nullable = false)
    private Utilisateur chef;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public Utilisateur getEmploye() {
		return employe;
	}

	public void setEmploye(Utilisateur employe) {
		this.employe = employe;
	}

	public Utilisateur getChef() {
		return chef;
	}

	public void setChef(Utilisateur chef) {
		this.chef = chef;
	}

   
}
