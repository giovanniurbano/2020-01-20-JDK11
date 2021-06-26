package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<String> listRoles() {
		String sql = "SELECT DISTINCT role "
				+ "FROM authorship";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {			
				result.add(res.getString("role"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Artist> getVertici(String ruolo) {
		String sql = "SELECT ar.artist_id AS id, ar.name AS nome "
				+ "FROM authorship a, artists ar "
				+ "WHERE a.artist_id = ar.artist_id "
				+ "AND a.role = ? "
				+ "GROUP BY ar.artist_id, ar.name";
		List<Artist> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, ruolo);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Artist a = new Artist(res.getInt("id"), res.getString("nome"));
				
				result.add(a);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String ruolo, Map<Integer, Artist> idMap) {
		String sql = "SELECT a1.artist_id AS id1, a2.artist_id AS id2, COUNT(*) AS peso "
				+ "FROM authorship a1, artists ar1, authorship a2, artists ar2, exhibition_objects e1, exhibition_objects e2 "
				+ "WHERE a1.artist_id = ar1.artist_id AND a2.artist_id = ar2.artist_id AND a1.artist_id > a2.artist_id "
				+ "AND a1.role = ? AND a1.role = a2.role "
				+ "AND e1.object_id = a1.object_id AND e2.object_id = a2.object_id AND e1.object_id > e2.object_id "
				+ "AND e1.exhibition_id = e2.exhibition_id "
				+ "GROUP BY a1.artist_id, a2.artist_id "
				+ "HAVING peso > 0";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, ruolo);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Artist a1 = idMap.get(res.getInt("id1"));
				Artist a2 = idMap.get(res.getInt("id2"));
				if(a1 != null && a2 != null && !a1.equals(a2)) {
					result.add(new Adiacenza(a1, a2, res.getDouble("peso")));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
