package org.bellatrix.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.bellatrix.data.CategoryMenuData;
import org.bellatrix.data.ChildMenu;
import org.bellatrix.data.ParentMenu;
import org.bellatrix.data.ParentMenuData;
import org.bellatrix.data.WelcomeMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class MenuRepository {

	private JdbcTemplate jdbcTemplate;

	public List<ParentMenuData> loadParentMenuByGroupID(Integer groupID) {
		try {
			List<ParentMenuData> parentMenu = this.jdbcTemplate.query(
					"select b.menu_parent_id as id, c.id as main_menu_id, c.name as main_menu_name, a.name, a.icon, a.sequence from menu_parent a join menu_permission b join menu_main c on a.id = b.menu_parent_id and a.menu_main_id = c.id where b.group_id = ? order by a.sequence;",
					new Object[] { groupID }, new RowMapper<ParentMenuData>() {
						public ParentMenuData mapRow(ResultSet rs, int rowNum) throws SQLException {
							ParentMenuData parentMenu = new ParentMenuData();
							parentMenu.setId(rs.getInt("id"));
							parentMenu.setIcon(rs.getString("icon"));
							parentMenu.setParentMenuName(rs.getString("name"));
							parentMenu.setSequenceNo(rs.getInt("sequence"));
							parentMenu.setMainMenuName(rs.getString("main_menu_name"));
							return parentMenu;
						}
					});
			return parentMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ParentMenuData> loadParentMenuByID(Integer id) {
		try {
			List<ParentMenuData> parentMenu = this.jdbcTemplate.query(
					"select * from menu_parent where id = ? order by sequence;", new Object[] { id },
					new RowMapper<ParentMenuData>() {
						public ParentMenuData mapRow(ResultSet rs, int rowNum) throws SQLException {
							ParentMenuData parentMenu = new ParentMenuData();
							parentMenu.setId(rs.getInt("id"));
							parentMenu.setIcon(rs.getString("icon"));
							parentMenu.setParentMenuName(rs.getString("name"));
							parentMenu.setSequenceNo(rs.getInt("sequence"));
							parentMenu.setDescription(rs.getString("description"));
							return parentMenu;
						}
					});
			return parentMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ParentMenuData> loadParentMenuByCategoryID(Integer categoryID) {
		try {
			List<ParentMenuData> parentMenu = this.jdbcTemplate.query(
					"select * from menu_parent where menu_main_id = ? order by sequence;", new Object[] { categoryID },
					new RowMapper<ParentMenuData>() {
						public ParentMenuData mapRow(ResultSet rs, int rowNum) throws SQLException {
							ParentMenuData parentMenu = new ParentMenuData();
							parentMenu.setId(rs.getInt("id"));
							parentMenu.setIcon(rs.getString("icon"));
							parentMenu.setParentMenuName(rs.getString("name"));
							parentMenu.setSequenceNo(rs.getInt("sequence"));
							parentMenu.setDescription(rs.getString("description"));
							return parentMenu;
						}
					});
			return parentMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ParentMenuData> loadParentMenu() {
		try {
			List<ParentMenuData> parentMenu = this.jdbcTemplate.query("select * from menu_parent order by sequence;",
					new RowMapper<ParentMenuData>() {
						public ParentMenuData mapRow(ResultSet rs, int rowNum) throws SQLException {
							ParentMenuData parentMenu = new ParentMenuData();
							parentMenu.setId(rs.getInt("id"));
							parentMenu.setIcon(rs.getString("icon"));
							parentMenu.setParentMenuName(rs.getString("name"));
							parentMenu.setSequenceNo(rs.getInt("sequence"));
							parentMenu.setDescription(rs.getString("description"));
							return parentMenu;
						}
					});
			return parentMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<CategoryMenuData> loadCategoryMenu() {
		try {
			List<CategoryMenuData> catMenu = this.jdbcTemplate.query("select * from menu_main;",
					new RowMapper<CategoryMenuData>() {
						public CategoryMenuData mapRow(ResultSet rs, int rowNum) throws SQLException {
							CategoryMenuData catMenu = new CategoryMenuData();
							catMenu.setId(rs.getInt("id"));
							catMenu.setName(rs.getString("name"));
							return catMenu;
						}
					});
			return catMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<CategoryMenuData> loadCategoryMenuByID(Integer id) {
		try {
			List<CategoryMenuData> catMenu = this.jdbcTemplate.query("select * from menu_main where id = ?;",
					new Object[] { id }, new RowMapper<CategoryMenuData>() {
						public CategoryMenuData mapRow(ResultSet rs, int rowNum) throws SQLException {
							CategoryMenuData catMenu = new CategoryMenuData();
							catMenu.setId(rs.getInt("id"));
							catMenu.setName(rs.getString("name"));
							return catMenu;
						}
					});
			return catMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public WelcomeMenu loadWelcomeMenuLinkByGroup(Integer groupID) {
		try {
			WelcomeMenu welcomeMenu = this.jdbcTemplate.queryForObject(
					"select * from menu_welcome where group_id = ?;", new Object[] { groupID },
					new RowMapper<WelcomeMenu>() {
						public WelcomeMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
							WelcomeMenu welcomeMenu = new WelcomeMenu();
							welcomeMenu.setId(rs.getInt("id"));
							welcomeMenu.setLink(rs.getString("link"));
							return welcomeMenu;
						}
					});
			return welcomeMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public WelcomeMenu loadWelcomeMenuLinkByID(Integer id) {
		try {
			WelcomeMenu welcomeMenu = this.jdbcTemplate.queryForObject("select * from menu_welcome where id = ? ",
					new Object[] { id }, new RowMapper<WelcomeMenu>() {
						public WelcomeMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
							WelcomeMenu welcomeMenu = new WelcomeMenu();
							welcomeMenu.setId(rs.getInt("id"));
							welcomeMenu.setLink(rs.getString("link"));
							return welcomeMenu;
						}
					});
			return welcomeMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<WelcomeMenu> loadWelcomeMenuLink() {
		try {
			List<WelcomeMenu> welcomeMenu = this.jdbcTemplate.query("select * from menu_welcome",
					new RowMapper<WelcomeMenu>() {
						public WelcomeMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
							WelcomeMenu welcomeMenu = new WelcomeMenu();
							welcomeMenu.setId(rs.getInt("id"));
							welcomeMenu.setLink(rs.getString("link"));
							return welcomeMenu;
						}
					});
			return welcomeMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ChildMenu> loadChildMenuByParentID(List<Integer> parentID) {
		String sql = "select * from menu_child where menu_parent_id in (:parentID) order by sequence;";
		Map<String, List<Integer>> paramMap = Collections.singletonMap("parentID", parentID);
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
		List<ChildMenu> childMenu = template.query(sql, paramMap, new RowMapper<ChildMenu>() {
			public ChildMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
				ChildMenu childMenu = new ChildMenu();
				childMenu.setId(rs.getInt("id"));
				childMenu.setParentMenuID(rs.getInt("menu_parent_id"));
				childMenu.setChildMenuName(rs.getString("name"));
				childMenu.setLink(rs.getString("link"));
				childMenu.setSequenceNo(rs.getInt("sequence"));
				return childMenu;
			}
		});
		return childMenu;
	}

	public List<ChildMenu> loadChildMenuByID(Integer id) {
		try {
			List<ChildMenu> catMenu = this.jdbcTemplate.query("select * from menu_child where id = ?;",
					new Object[] { id }, new RowMapper<ChildMenu>() {
						public ChildMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
							ChildMenu catMenu = new ChildMenu();
							catMenu.setId(rs.getInt("id"));
							catMenu.setChildMenuName(rs.getString("name"));
							catMenu.setLink(rs.getString("link"));
							catMenu.setParentMenuID(rs.getInt("menu_parent_id"));
							catMenu.setSequenceNo(rs.getInt("sequence"));
							return catMenu;
						}
					});
			return catMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ChildMenu> loadChildMenuByParentID(Integer id) {
		try {
			List<ChildMenu> catMenu = this.jdbcTemplate.query("select * from menu_child where menu_parent_id = ?;",
					new Object[] { id }, new RowMapper<ChildMenu>() {
						public ChildMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
							ChildMenu catMenu = new ChildMenu();
							catMenu.setId(rs.getInt("id"));
							catMenu.setChildMenuName(rs.getString("name"));
							catMenu.setLink(rs.getString("link"));
							catMenu.setParentMenuID(rs.getInt("menu_parent_id"));
							catMenu.setSequenceNo(rs.getInt("sequence"));
							return catMenu;
						}
					});
			return catMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<ChildMenu> loadChildMenu() {
		try {
			List<ChildMenu> catMenu = this.jdbcTemplate.query("select * from menu_child;", new RowMapper<ChildMenu>() {
				public ChildMenu mapRow(ResultSet rs, int rowNum) throws SQLException {
					ChildMenu catMenu = new ChildMenu();
					catMenu.setId(rs.getInt("id"));
					catMenu.setChildMenuName(rs.getString("name"));
					catMenu.setLink(rs.getString("link"));
					catMenu.setParentMenuID(rs.getInt("menu_parent_id"));
					catMenu.setSequenceNo(rs.getInt("sequence"));
					return catMenu;
				}
			});
			return catMenu;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
