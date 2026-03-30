package models;

import java.util.Objects;

public class Alumno {
	
	private Long id;
	private String CIF;
	private String nombre;
	private String email;
	
	public Alumno(Long id, String cIF, String nombre, String email) {
		super();
		this.id = id;
		CIF = cIF;
		this.nombre = nombre;
		this.email = email;
	}
	
	public Alumno() {}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCIF() {
		return CIF;
	}
	public void setCIF(String cIF) {
		CIF = cIF;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alumno other = (Alumno) obj;
		return Objects.equals(id, other.id);
	}

	
	

}
