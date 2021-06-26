package it.polito.tdp.artsmia;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ArtsmiaController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnArtistiConnessi;

    @FXML
    private Button btnCalcolaPercorso;

    @FXML
    private ComboBox<String> boxRuolo;

    @FXML
    private TextField txtArtista;

    @FXML
    private TextArea txtResult;

    @FXML
    void doArtistiConnessi(ActionEvent event) {
    	txtResult.clear();
    	txtResult.appendText("Calcola artisti connessi\n");
    	
    	if(this.model.getGrafo() == null) {
    		this.txtResult.appendText("Creare prima il grafo!");
    		return;
    	}
    	
    	List<Adiacenza> connessi = this.model.getArtistiConnessi();
    	for(Adiacenza c : connessi)
    		this.txtResult.appendText(c.toString() + "\n");
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	txtResult.appendText("Calcola percorso\n");
    	
    	String aS = this.txtArtista.getText();
    	try {
    		int idA = Integer.parseInt(aS);
    		if(!this.model.getIdMap().containsKey(idA)) {
    			this.txtResult.appendText("Artista non presente!");
        		return;
    		}
    		
    		List<Artist> percorso = this.model.getPercorso(idA);
    		for(Artist a : percorso)
    			this.txtResult.appendText(a.toString() + "\n");
    		
    		this.txtResult.appendText("Numero di esposizioni: " + this.model.getEspPercorso());
    	}
    	catch(NumberFormatException nfe) {
    		this.txtResult.appendText("Inserire un numero!");
    		return;
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	
    	String ruolo = this.boxRuolo.getValue();
    	if(ruolo == null) {
    		this.txtResult.appendText("Scegliere un ruolo!");
    		return;
    	}
    	String msg = this.model.creaGrafo(ruolo);
    	this.txtResult.appendText(msg);
    }

    public void setModel(Model model) {
    	this.model = model;
    	
    	this.boxRuolo.getItems().addAll(this.model.listRoles());
    }

    
    @FXML
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnArtistiConnessi != null : "fx:id=\"btnArtistiConnessi\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnCalcolaPercorso != null : "fx:id=\"btnCalcolaPercorso\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert boxRuolo != null : "fx:id=\"boxRuolo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtArtista != null : "fx:id=\"txtArtista\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Artsmia.fxml'.";

    }
}
