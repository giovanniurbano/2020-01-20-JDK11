package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List<Adiacenza> archi = this.dao.getAdiacenze(ruolo, idMap);
		for(Adiacenza a : archi) {
			Graphs.addEdge(this.grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
}
