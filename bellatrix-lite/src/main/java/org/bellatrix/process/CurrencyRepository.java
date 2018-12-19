package org.bellatrix.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.bellatrix.data.Accounts;
import org.bellatrix.data.Currencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class CurrencyRepository {

	private JdbcTemplate jdbcTemplate;

	public List<Currencies> loadAllCurrencies() {
		try {
			List<Currencies> currency = this.jdbcTemplate.query("select * from currency", new Object[] {},
					new RowMapper<Currencies>() {
						public Currencies mapRow(ResultSet rs, int rowNum) throws SQLException {
							Currencies currency = new Currencies();
							currency.setId(rs.getInt("id"));
							currency.setName(rs.getString("name"));
							currency.setCode(rs.getString("code"));
							currency.setDecimal(rs.getString("decimal_separator").charAt(0));
							currency.setGrouping(rs.getString("grouping_separator").charAt(0));
							currency.setFormat(rs.getString("format"));
							currency.setPrefix(rs.getString("prefix"));
							currency.setTrailer(rs.getString("trailer"));
							return currency;
						}
					});
			return currency;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Currencies loadCurrencyByID(Integer currencyID) {
		try {
			Currencies currency = this.jdbcTemplate.queryForObject("select * from currency where id = ?",
					new Object[] { currencyID }, new RowMapper<Currencies>() {
						public Currencies mapRow(ResultSet rs, int rowNum) throws SQLException {
							Currencies currency = new Currencies();
							currency.setId(rs.getInt("id"));
							currency.setName(rs.getString("name"));
							currency.setCode(rs.getString("code"));
							currency.setDecimal(rs.getString("decimal_separator").charAt(0));
							currency.setGrouping(rs.getString("grouping_separator").charAt(0));
							currency.setFormat(rs.getString("format"));
							currency.setPrefix(rs.getString("prefix"));
							currency.setTrailer(rs.getString("trailer"));
							return currency;
						}
					});
			return currency;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void createCurrency(String name, String code, String prefix, String trailer, String format, char grouping,
			char decimal) {
		jdbcTemplate.update(
				"insert into currency (name, code, prefix, trailer, format, grouping_separator, decimal_separator) values (?, ?, ?, ?, ?, ?, ?)",
				name, code, prefix, trailer, format, grouping, decimal);
	}

	public void updateCurrency(String name, String code, String prefix, String trailer, String format, char grouping,
			char decimal) {
		this.jdbcTemplate.update(
				"update  currency set name = ?, code = ?, prefix = ?, trailer = ?, format = ?, grouping_separator = ?, decimal_separator = ?",
				new Object[] { name, code, prefix, trailer, format, grouping, decimal });
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
