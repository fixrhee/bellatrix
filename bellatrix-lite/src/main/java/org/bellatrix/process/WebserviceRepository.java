package org.bellatrix.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.bellatrix.data.WebServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class WebserviceRepository {

	private JdbcTemplate jdbcTemplate;

	public WebServices validateWebService(Integer id) {
		try {
			WebServices ws = this.jdbcTemplate.queryForObject("select * from webservices where id = ?",
					new Object[] { id }, new RowMapper<WebServices>() {
						public WebServices mapRow(ResultSet rs, int rowNum) throws SQLException {
							WebServices ws = new WebServices();
							ws.setActive(rs.getBoolean("active"));
							ws.setHash(rs.getString("hash"));
							ws.setId(rs.getInt("id"));
							ws.setName(rs.getString("name"));
							ws.setUsername(rs.getString("username"));
							ws.setSecureTransaction(rs.getBoolean("credentials"));
							return ws;
						}
					});
			return ws;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public WebServices validateWebService(String username, String password) {
		try {
			WebServices ws = this.jdbcTemplate.queryForObject(
					"select * from webservices where username = ? and password = ? and active = true;",
					new Object[] { username, password }, new RowMapper<WebServices>() {
						public WebServices mapRow(ResultSet rs, int rowNum) throws SQLException {
							WebServices ws = new WebServices();
							ws.setActive(rs.getBoolean("active"));
							ws.setHash(rs.getString("hash"));
							ws.setId(rs.getInt("id"));
							ws.setName(rs.getString("name"));
							ws.setUsername(rs.getString("username"));
							ws.setSecureTransaction(rs.getBoolean("credentials"));
							return ws;
						}
					});
			return ws;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public WebServices loadSecretByUsername(String username) {
		try {
			WebServices ws = this.jdbcTemplate.queryForObject(
					"select * from webservices where username = ? and active = true", new Object[] { username },
					new RowMapper<WebServices>() {
						public WebServices mapRow(ResultSet rs, int rowNum) throws SQLException {
							WebServices ws = new WebServices();
							ws.setActive(rs.getBoolean("active"));
							ws.setId(rs.getInt("id"));
							ws.setName(rs.getString("name"));
							ws.setHash(rs.getString("hash"));
							ws.setSecureTransaction(rs.getBoolean("credentials"));
							return ws;
						}
					});
			return ws;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<WebServices> loadWebservices(Integer currentPage, Integer pageSize) {
		try {
			List<WebServices> ws = this.jdbcTemplate.query("select * from webservices order by id desc limit ?,?;",
					new Object[] { currentPage, pageSize }, new RowMapper<WebServices>() {
						public WebServices mapRow(ResultSet rs, int rowNum) throws SQLException {
							WebServices ws = new WebServices();
							ws.setActive(rs.getBoolean("active"));
							ws.setId(rs.getInt("id"));
							ws.setName(rs.getString("name"));
							ws.setHash(rs.getString("hash"));
							ws.setSecureTransaction(rs.getBoolean("credentials"));
							return ws;
						}
					});
			return ws;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<WebServices> loadWebservicesByID(Integer id) {
		try {
			List<WebServices> ws = this.jdbcTemplate.query("SELECT * from webservices where id = ? ",
					new Object[] { id }, new RowMapper<WebServices>() {
						public WebServices mapRow(ResultSet rs, int rowNum) throws SQLException {
							WebServices ws = new WebServices();
							ws.setActive(rs.getBoolean("active"));
							ws.setId(rs.getInt("id"));
							ws.setName(rs.getString("name"));
							ws.setHash(rs.getString("hash"));
							ws.setSecureTransaction(rs.getBoolean("credentials"));
							return ws;
						}
					});
			return ws;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<WebServices> loadWebservicesByGroup(Integer id, Integer currentPage, Integer pageSize) {
		try {
			List<WebServices> ws = this.jdbcTemplate.query(
					"SELECT a.active, a.id, a.name, a.hash, a.credentials from webservices a inner join webservice_permissions b on a.id = b.webservice_id where b.group_id = ? order by a.id desc limit ?,?;",
					new Object[] { id, currentPage, pageSize }, new RowMapper<WebServices>() {
						public WebServices mapRow(ResultSet rs, int rowNum) throws SQLException {
							WebServices ws = new WebServices();
							ws.setActive(rs.getBoolean("active"));
							ws.setId(rs.getInt("id"));
							ws.setName(rs.getString("name"));
							ws.setHash(rs.getString("hash"));
							ws.setSecureTransaction(rs.getBoolean("credentials"));
							return ws;
						}
					});
			return ws;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public boolean validateGroupAccessToWebService(Integer wsID, Integer groupID) {
		try {
			jdbcTemplate.queryForObject(
					"SELECT id from webservice_permissions where webservice_id = ? and group_id = ?",
					new Object[] { wsID, groupID }, Integer.class);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public Integer countWebservices() {
		Integer records = jdbcTemplate.queryForObject("SELECT  count(id) from webservices", Integer.class);
		return records;
	}

	public void insertWebservice(String username, String password, String name, String hash, boolean active,
			boolean credential) {
		this.jdbcTemplate.update(
				"insert into webservices (username, password, name, hash, active, credentials) values(?,?,?,?,?,?);",
				username, password, name, hash, active, credential);
	}

	public void updateWebservice(Integer id, String username, String password, String name, String hash, boolean active,
			boolean credential) {
		this.jdbcTemplate.update(
				"update webservices set username  ?, password = ?, name = ?, hash = ?, active = ?, credentials = ? where id = ? ;",
				id, username, password, name, hash, active, credential);
	}

	public void deleteWebservice(Integer id) {
		this.jdbcTemplate.update("delete from webservices where id = ?;", id);
	}

	public void addWebservicePermission(Integer wsID, Integer groupID) {
		this.jdbcTemplate.update("insert into webservice_permissions (webservice_id, group_id) values(?,?);", wsID,
				groupID);
	}

	public void deleteWebservicePermission(Integer wsID, Integer groupID) {
		this.jdbcTemplate.update("delete from webservice_permissions where webservice_id = ? and group_id = ?;", wsID,
				groupID);
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
