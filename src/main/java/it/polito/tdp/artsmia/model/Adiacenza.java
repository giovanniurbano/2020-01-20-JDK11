package it.polito.tdp.artsmia.model;

public class Adiacenza implements Comparable<Adiacenza>{
	private Artist a1;
	private Artist a2;
	private Double peso;
	public Adiacenza(Artist a1, Artist a2, Double peso) {
		this.a1 = a1;
		this.a2 = a2;
		this.peso = peso;
	}
	public Artist getA1() {
		return a1;
	}
	public void setA1(Artist a1) {
		this.a1 = a1;
	}
	public Artist getA2() {
		return a2;
	}
	public void setA2(Artist a2) {
		this.a2 = a2;
	}
	public Double getPeso() {
		return peso;
	}
	public void setPeso(Double peso) {
		this.peso = peso;
	}
	
	public int compareTo(Adiacenza o) {
		return -this.peso.compareTo(o.peso);
	}
	@Override
	public String toString() {
		return this.a1.getId() + " - " + this.a2.getId() + " - " + this.peso;
	}
	
}
