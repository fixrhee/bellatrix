package org.prime.batch.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.prime.batch.data.Accounts;
import org.prime.batch.data.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class BatchRepository {

	private JdbcTemplate jdbcTemplate;

	public Integer countMembers() {
		try {
			return this.jdbcTemplate.queryForObject("select count(id) from members;", Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	public List<Members> findMembers(Integer offset, Integer limit) {
		try {
			List<Members> members = this.jdbcTemplate.query("select *  from members limit ?,?",
					new Object[] { offset, limit }, new RowMapper<Members>() {
						public Members mapRow(ResultSet rs, int rowNum) throws SQLException {
							Members members = new Members();
							members.setId(rs.getInt("id"));
							members.setGroupID(rs.getInt("group_id"));
							members.setUsername(rs.getString("username"));
							members.setName(rs.getString("name"));
							members.setMsisdn(rs.getString("msisdn"));
							members.setEmail(rs.getString("email"));
							members.setAddress(rs.getString("address"));
							members.setDateOfBirth(rs.getDate("date_of_birth"));
							members.setPlaceOfBirth(rs.getString("place_of_birth"));
							members.setIdCardNo(rs.getString("id_card_no"));
							members.setMotherMaidenName(rs.getString("mother_maiden_name"));
							members.setWork(rs.getString("work"));
							members.setSex(rs.getString("sex"));
							members.setNationality(rs.getString("nationality"));
							members.setCreatedDate(rs.getTimestamp("created_date"));
							return members;
						}
					});
			return members;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Accounts> loadAccountsByGroups(Members member) {
		final Members members = member;
		try {
			List<Accounts> accounts = this.jdbcTemplate.query(
					"select c.id, c.name, c.description, c.system_account, c.currency_id from account_permissions a inner join groups b on a.group_id = b.id inner join accounts c on a.account_id = c.id where b.id = ?",
					new Object[] { members.getGroupID() }, new RowMapper<Accounts>() {
						public Accounts mapRow(ResultSet rs, int rowNum) throws SQLException {
							Accounts accounts = new Accounts();
							accounts.setId(rs.getInt("id"));
							accounts.setCurrencyID(rs.getInt("currency_id"));
							accounts.setAccountName(rs.getString("name"));
							accounts.setSystemAccount(rs.getBoolean("system_account"));
							accounts.setMember(members);
							return accounts;
						}
					});
			return accounts;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public BigDecimal loadBalanceInquiry(String username, Integer accountID, Integer transferID) {
		try {
			BigDecimal balance = jdbcTemplate.queryForObject(
					"select sum(journal) as balance from (select sum(-amount) as journal from transfers join members on members.id = transfers.from_member_id where members.username = ? and transfers.from_account_id = ? and transfers.transaction_state = 'PROCESSED' and transfers.charged_back = false and transfers.id <= ? union all select sum(amount) as journal from transfers join members on members.id = transfers.to_member_id where members.username = ? and transfers.to_account_id = ? and transfers.transaction_state = 'PROCESSED'  and transfers.charged_back = false and transfers.id <= ?) t1",
					new Object[] { username, accountID, transferID, username, accountID, transferID },
					BigDecimal.class);
			if (balance == null) {
				return BigDecimal.ZERO;
			}
			return balance;
		} catch (EmptyResultDataAccessException e) {
			return BigDecimal.ZERO;
		}
	}

	public Integer getLastTransactionID(Integer memberID, Integer accountID) {
		try {
			return this.jdbcTemplate.queryForObject("select id from transfers where from_member_id = " + memberID
					+ " and from_account_id = " + accountID + " or to_member_id = " + memberID + " and to_account_id = "
					+ accountID + " order by id desc limit 1;", Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	public void insertBalance(Accounts req) {
		String id = String.valueOf(req.getMember().getId()) + String.valueOf(req.getId());
		jdbcTemplate.update(
				"insert into closed_account_balance (id, member_id, account_id, last_transfer_id, balance) values (?,?,?,?,?) on duplicate key update balance = ?, last_transfer_id = ? ",
				id, req.getMember().getId(), req.getId(), req.getLastTransferID(), req.getBalance(), req.getBalance(),
				req.getLastTransferID());
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
