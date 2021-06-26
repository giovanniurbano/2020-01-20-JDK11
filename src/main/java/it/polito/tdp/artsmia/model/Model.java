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
	private int nEsp;
	private List<Artist> best;
	
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
		this.cerca(parziale, 0);
		
		return migliore;
	}

	private void cerca(List<Artist> parziale, double esposizioni) {
		Set<DefaultWeightedEdge> edges = this.grafo.outgoingEdgesOf(parziale.get(parziale.size()-1));
		
		for(DefaultWeightedEdge e : edges) {
			double esp = this.grafo.getEdgeWeight(e);
			Artist a = this.grafo.getEdgeTarget(e);
			if(esposizioni == 0 && !parziale.contains(a)) {
				parziale.add(a);
				cerca(parziale, esp);
				parziale.remove(parziale.size()-1);
			}
			else {
				if(esp == esposizioni && !parziale.contains(a)) {
					parziale.add(a);
					cerca(parziale, esposizioni);
					parziale.remove(parziale.size()-1);
				}
			}
		}
		
		//casi terminali
		if(parziale.size() > migliore.size()) {
			this.migliore = new ArrayList<Artist>(parziale);
			this.nEsp = (int) esposizioni;
		}
	}

	public int getEspPercorso() {
		return this.nEsp;
	}
	
	
	
	
	
	//soluzione prof
	public List<Artist> trovaPercorso(Integer sorgente){
		this.best = new ArrayList<Artist>();
		List<Artist> parziale = new ArrayList<>();
		parziale.add(this.idMap.get(sorgente));
		//lancio la ricorsione
		ricorsione(parziale, -1);
		
		return best;
	}
	
	//soluzione prof
	private void ricorsione(List<Artist> parziale, int peso) {
		Artist ultimo = parziale.get(parziale.size() - 1);
		//ottengo i vicini
		List<Artist> vicini = Graphs.neighborListOf(this.grafo, ultimo);
		for(Artist vicino : vicini) {
			if(!parziale.contains(vicino) && peso == -1) {
				parziale.add(vicino);
				ricorsione(parziale, (int) this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)));
				parziale.remove(vicino);
			} else {
				if(!parziale.contains(vicino) && this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)) == peso) {
					parziale.add(vicino);
					ricorsione(parziale, peso);
					parziale.remove(vicino);
				}
			}
		}
		
		if(parziale.size() > best.size()) {
			this.best = new ArrayList<>(parziale);
		}
		
	}
	
}
