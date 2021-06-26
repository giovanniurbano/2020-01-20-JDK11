package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	private ArtsmiaDAO dao;
	private Graph<Artist, DefaultWeightedEdge> grafo;
	private List<Artist> vertici;
	private Map<Integer, Artist> idMap;
	private List<Adiacenza> archi;
	
	private List<Artist> migliore;
	private double esposizioni;
	private int nEsp;
	
	public Model() {
		this.dao = new ArtsmiaDAO();
	}
	
	public List<String> listRoles() {
		return this.dao.listRoles();
	}

	public String creaGrafo(String ruolo) {
		this.grafo = new SimpleWeightedGraph<Artist, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//vertici
		this.vertici = this.dao.getVertici(ruolo);
		this.idMap = new HashMap<Integer, Artist>();
		for(Artist a : vertici) {
			this.idMap.put(a.getId(), a);
		}
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//archi
		archi = this.dao.getAdiacenze(ruolo, idMap);
		for(Adiacenza a : archi) {
			Graphs.addEdge(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}

	public Graph<Artist, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<Adiacenza> getArtistiConnessi() {
		List<Adiacenza> connessi = new ArrayList<Adiacenza>(archi);
		Collections.sort(connessi);
		return connessi;
	}

	public Map<Integer, Artist> getIdMap() {
		return idMap;
	}

	public List<Artist> getPercorso(int idA) {
		this.migliore = new ArrayList<Artist>();
		List<Artist> parziale = new ArrayList<Artist>();
		parziale.add(this.idMap.get(idA));
		this.cerca(parziale, 1);
		
		return migliore;
	}

	private void cerca(List<Artist> parziale, int L) {
		Set<DefaultWeightedEdge> edges = this.grafo.outgoingEdgesOf(parziale.get(parziale.size()-1));
		int ck = 0;
		for(DefaultWeightedEdge e : edges) {
			//controllo se c'è almeno un arco che abbia il peso che mi interessa
			double esp = this.grafo.getEdgeWeight(e);
			if(L > 1 && esp == esposizioni)
				ck++;
		}
		
		//casi terminali
		if(ck > 0 && parziale.size() > migliore.size()) {
			this.migliore = new ArrayList<Artist>(parziale);
			this.nEsp = (int) esposizioni;
		}
		else if(ck == 0 && parziale.size() > 1)
			return;
		
		if(L == this.grafo.vertexSet().size()) {
			//artisti finiti
			return;
		}
		
		for(DefaultWeightedEdge e : edges) {
			double esp = this.grafo.getEdgeWeight(e);
			if(parziale.size() == 1) {
				this.esposizioni = esp;
				Artist a = this.grafo.getEdgeTarget(e);
				parziale.add(a);
				cerca(parziale, L+1);
				parziale.remove(parziale.size()-1);
			}
			else {
				Artist a = this.grafo.getEdgeTarget(e);
				if(esp == esposizioni && !parziale.contains(a)) {
					this.esposizioni = esp;
					parziale.add(a);
					cerca(parziale, L+1);
					parziale.remove(parziale.size()-1);
				}
			}
		}
	}

	public int getEspPercorso() {
		return this.nEsp;
	}
	
	
}
