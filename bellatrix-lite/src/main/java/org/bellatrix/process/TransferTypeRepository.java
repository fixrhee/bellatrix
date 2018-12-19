package org.bellatrix.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import org.bellatrix.data.Brokers;
import org.bellatrix.data.Fees;
import org.bellatrix.data.TransferTypes;
import org.bellatrix.services.BrokerRequest;
import org.bellatrix.services.FeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public class TransferTypeRepository {

	private JdbcTemplate jdbcTemplate;

	public TransferTypes findTransferTypeByGroupID(Integer id, Integer groupID) {
		try {
			TransferTypes transferType = this.jdbcTemplate.queryForObject(
					"select a.id, a.name, a.description, a.from_account_id, a.to_account_id, a.min_amount, a.max_amount, a.max_count, b.group_id, a.otp_threshold from transfer_types a inner join transfer_type_permissions b on b.transfer_type_id = a.id where a.id = ? and b.group_id = ?",
					new Object[] { id, groupID }, new RowMapper<TransferTypes>() {
						public TransferTypes mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypes transferType = new TransferTypes();
							transferType.setId(rs.getInt("id"));
							transferType.setName(rs.getString("name"));
							transferType.setDescription(rs.getString("description"));
							transferType.setFromAccounts(rs.getInt("from_account_id"));
							transferType.setToAccounts(rs.getInt("to_account_id"));
							transferType.setMinAmount(rs.getBigDecimal("min_amount"));
							transferType.setMaxAmount(rs.getBigDecimal("max_amount"));
							transferType.setOtpThreshold(rs.getBigDecimal("otp_threshold"));
							transferType.setMaxCount(rs.getInt("max_count"));
							return transferType;
						}
					});
			return transferType;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public TransferTypes findTransferTypeByID(Integer id) {
		try {
			TransferTypes transferType = this.jdbcTemplate.queryForObject("select * from transfer_types where id = ?",
					new Object[] { id }, new RowMapper<TransferTypes>() {
						public TransferTypes mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypes transferType = new TransferTypes();
							transferType.setId(rs.getInt("id"));
							transferType.setName(rs.getString("name"));
							transferType.setDescription(rs.getString("description"));
							transferType.setFromAccounts(rs.getInt("from_account_id"));
							transferType.setToAccounts(rs.getInt("to_account_id"));
							transferType.setMinAmount(rs.getBigDecimal("min_amount"));
							transferType.setMaxAmount(rs.getBigDecimal("max_amount"));
							transferType.setOtpThreshold(rs.getBigDecimal("otp_threshold"));
							transferType.setMaxCount(rs.getInt("max_count"));
							return transferType;
						}
					});
			return transferType;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<TransferTypes> listTransferTypeByAccountID(Integer id) {
		try {
			List<TransferTypes> transferType = this.jdbcTemplate.query(
					"select b.id, b.from_account_id,  b.to_account_id, a.name as fromaccount, (select c.name from accounts c where id = b.to_account_id) as toaccount, b.name, b.description, b.min_amount, b.max_amount, b.max_count, b.otp_threshold from transfer_types b inner join accounts a on b.from_account_id = a.id and a.`id` = ?",
					new Object[] { id }, new RowMapper<TransferTypes>() {
						public TransferTypes mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypes transferType = new TransferTypes();
							transferType.setId(rs.getInt("id"));
							transferType.setName(rs.getString("name"));
							transferType.setDescription(rs.getString("description"));
							transferType.setFromAccounts(rs.getInt("from_account_id"));
							transferType.setToAccounts(rs.getInt("to_account_id"));
							transferType.setFromAccountName(rs.getString("fromaccount"));
							transferType.setToAccountName(rs.getString("toaccount"));
							transferType.setMinAmount(rs.getBigDecimal("min_amount"));
							transferType.setMaxAmount(rs.getBigDecimal("max_amount"));
							transferType.setOtpThreshold(rs.getBigDecimal("otp_threshold"));
							transferType.setMaxCount(rs.getInt("max_count"));
							return transferType;
						}
					});
			return transferType;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<TransferTypes> listTransferTypeByGroupID(Integer groupID) {
		try {
			List<TransferTypes> transferType = this.jdbcTemplate.query(
					"select b.transfer_type_id, a.name, a.description from transfer_types a join transfer_type_permissions b on a.id = b.transfer_type_id where b.group_id = ?",
					new Object[] { groupID }, new RowMapper<TransferTypes>() {
						public TransferTypes mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypes transferType = new TransferTypes();
							transferType.setId(rs.getInt("transfer_type_id"));
							transferType.setName(rs.getString("name"));
							transferType.setDescription(rs.getString("description"));
							return transferType;
						}
					});
			return transferType;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Integer countTotalTransferTypes(Integer accountID) {
		int count = this.jdbcTemplate.queryForObject(
				"select  count(*) as total from transfer_types where from_account_id = ?", Integer.class,
				new Object[] { accountID });
		return count;
	}

	public Integer countMaxUsageTransferTypes(Integer id, Integer memberID) {
		int count = this.jdbcTemplate.queryForObject(
				"select count(id) from transfers where from_member_id = ? and transfer_type_id = ? and transaction_date >= CURDATE() AND transaction_date < CURDATE() + INTERVAL 1 DAY;",
				Integer.class, new Object[] { memberID, id });
		return count;
	}

	public List<Fees> getFeeFromTransferTypeID(Integer transferTypeID) {
		try {
			List<Fees> fees = this.jdbcTemplate.query(
					"select * from fees where transfer_type_id = ? and enabled = true", new Object[] { transferTypeID },
					new RowMapper<Fees>() {
						public Fees mapRow(ResultSet rs, int rowNum) throws SQLException {
							Fees fees = new Fees();
							fees.setId(rs.getInt("id"));
							fees.setTransferTypeID(rs.getInt("transfer_type_id"));
							fees.setFromMemberID(rs.getInt("from_member_id"));
							fees.setToMemberID(rs.getInt("to_member_id"));
							fees.setFromAccountID(rs.getInt("from_account_id"));
							fees.setToAccountID(rs.getInt("to_account_id"));
							fees.setName(rs.getString("name"));
							fees.setDescription(rs.getString("description"));
							fees.setDeductAmount(rs.getBoolean("deduct_amount"));
							fees.setFixedAmount(rs.getBigDecimal("fixed_amount"));
							fees.setPercentageValue(rs.getBigDecimal("percentage_value"));
							fees.setInitialRangeAmount(rs.getBigDecimal("initial_range_amount"));
							fees.setMaximumRangeAmount(rs.getBigDecimal("maximum_range_amount"));
							fees.setFromAllGroup(rs.getBoolean("from_all_group"));
							fees.setToAllGroup(rs.getBoolean("to_all_group"));
							fees.setStartDate(rs.getDate("start_date"));
							fees.setEndDate(rs.getDate("end_date"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Fees> getPriorityFeeFromTransferTypeID(Integer transferTypeID) {
		try {
			List<Fees> fees = this.jdbcTemplate.query(
					"select * from fees where transfer_type_id = ? and priority = true and enabled = true",
					new Object[] { transferTypeID }, new RowMapper<Fees>() {
						public Fees mapRow(ResultSet rs, int rowNum) throws SQLException {
							Fees fees = new Fees();
							fees.setId(rs.getInt("id"));
							fees.setTransferTypeID(rs.getInt("transfer_type_id"));
							fees.setFromMemberID(rs.getInt("from_member_id"));
							fees.setToMemberID(rs.getInt("to_member_id"));
							fees.setFromAccountID(rs.getInt("from_account_id"));
							fees.setToAccountID(rs.getInt("to_account_id"));
							fees.setName(rs.getString("name"));
							fees.setDescription(rs.getString("description"));
							fees.setDeductAmount(rs.getBoolean("deduct_amount"));
							fees.setFixedAmount(rs.getBigDecimal("fixed_amount"));
							fees.setPercentageValue(rs.getBigDecimal("percentage_value"));
							fees.setInitialRangeAmount(rs.getBigDecimal("initial_range_amount"));
							fees.setMaximumRangeAmount(rs.getBigDecimal("maximum_range_amount"));
							fees.setFromAllGroup(rs.getBoolean("from_all_group"));
							fees.setToAllGroup(rs.getBoolean("to_all_group"));
							fees.setStartDate(rs.getDate("start_date"));
							fees.setEndDate(rs.getDate("end_date"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public boolean validateFeeByGroup(Integer feeID, Integer groupID, boolean target) {
		try {
			this.jdbcTemplate.queryForObject(
					"select id from fee_by_groups where fee_id = ? and group_id = ? and destination = ?", Integer.class,
					feeID, groupID, target);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public void blockedTransferType(Integer transferTypeID, Integer memberID) {
		jdbcTemplate.update("insert into transfer_type_blocked (transfer_type_id, member_id) values (?, ?)",
				transferTypeID, memberID);
	}

	public void deleteBlockedTransferType(Integer memberID, Integer transferTypeID) {
		this.jdbcTemplate.update("delete from transfer_type_blocked where member_id = ? and transfer_type_id = ?",
				new Object[] { memberID, transferTypeID });
	}

	public boolean validateBlockedTransferType(Integer memberID, Integer transferTypeID) {
		try {
			this.jdbcTemplate.queryForObject("select id from transfer_type_blocked where member_id = " + memberID
					+ " and transfer_type_id = " + transferTypeID, Integer.class);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public void updatePendingFees(String parentID) {
		this.jdbcTemplate.update(
				"update  transfers set transaction_state = 'PROCESSED' where parent_id = ? or reference_number = ? and transaction_state = 'PENDING'",
				new Object[] { parentID, parentID });
	}

	public void createTransferType(Integer fromAccountID, Integer toAccountID, String name, String description,
			BigDecimal min, BigDecimal max, Integer maxCount, BigDecimal otp) {
		jdbcTemplate.update(
				"insert into transfer_type (from_account_id, to_account_id, name, description, min_amount, max_amount, max_count) values (?, ?, ?, ?, ?, ?, ?)",
				fromAccountID, toAccountID, name, description, min, max, maxCount);
	}

	public void updateTransferType(Integer id, Integer fromAccountID, Integer toAccountID, String name,
			String description, BigDecimal min, BigDecimal max, Integer maxCount, BigDecimal otp) {
		jdbcTemplate.update(
				"update transfer_type set from_account_id = ?, to_account_id = ?, name = ?, description = ?, min_amount = ?, max_amount = ?, max_count = ? where id = ?",
				fromAccountID, toAccountID, name, description, min, max, maxCount, id);
	}

	public void createTransferTypePermission(Integer transferTypeID, Integer groupID) {
		jdbcTemplate.update("insert into transfer_type_permissions (transfer_type_id, group_id) values (?, ?)",
				transferTypeID, groupID);
	}

	public void deleteTransferTypePermission(Integer transferTypeID, Integer groupID) {
		jdbcTemplate.update("delete from transfer_type_permissions where transfer_type_id = ? and group_id = ?",
				transferTypeID, groupID);
	}

	public void createFee(FeeRequest req) {
		jdbcTemplate.update(
				"insert into fees (transfer_type_id, from_member_id, from_account_id, to_member_id, to_account_id, name, description, enabled, deduct_amount, from_all_group, to_all_group, fixed_amount, percentage_value, initial_range_amount, maximum_range_amount, start_date, end_date) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				req.getTransferTypeID(), req.getFromMemberID(), req.getFromAccountID(), req.getToMemberID(),
				req.getToAccountID(), req.getName(), req.getDescription(), req.isEnabled(), req.isDeductAmount(),
				req.isFromAllGroup(), req.isToAllGroup(), req.getFixedAmount(), req.getPercentage(),
				req.getInitialRangeAmount(), req.getMaxRangeAmount(), req.getStartDate(), req.getEndDate());
	}

	public void updateFee(FeeRequest req) {
		jdbcTemplate.update(
				"update fees set from_member_id = ?, from_account_id = ?, to_member_id = ?, to_account_id = ?, name = ?, description = ?, enabled = ?, deduct_amount = ?, from_all_group = ?, to_all_group = ?, fixed_amount = ?, percentage_value = ?, initial_range_amount = ?, maximum_range_amount = ?, start_date = ? , end_date = ? where transfer_type_id = ?",
				req.getFromMemberID(), req.getFromAccountID(), req.getToMemberID(), req.getToAccountID(), req.getName(),
				req.getDescription(), req.isEnabled(), req.isDeductAmount(), req.isFromAllGroup(), req.isToAllGroup(),
				req.getFixedAmount(), req.getPercentage(), req.getInitialRangeAmount(), req.getMaxRangeAmount(),
				req.getStartDate(), req.getEndDate(), req.getTransferTypeID());
	}

	public void deleteFee(Integer transferTypeID) {
		jdbcTemplate.update("delete from fees where transfer_type_id = ?", transferTypeID);
	}

	public List<Brokers> getBrokersFromFee(Integer feeID) {
		try {
			List<Brokers> brokers = this.jdbcTemplate.query("select * from brokers where fee_id = ? and enabled = true",
					new Object[] { feeID }, new RowMapper<Brokers>() {
						public Brokers mapRow(ResultSet rs, int rowNum) throws SQLException {
							Brokers brokers = new Brokers();
							brokers.setId(rs.getInt("id"));
							brokers.setFeeID(rs.getInt("fee_id"));
							brokers.setFromMemberID(rs.getInt("from_member_id"));
							brokers.setToMemberID(rs.getInt("to_member_id"));
							brokers.setFromAccountID(rs.getInt("from_account_id"));
							brokers.setToAccountID(rs.getInt("to_account_id"));
							brokers.setName(rs.getString("name"));
							brokers.setDescription(rs.getString("description"));
							brokers.setFixedAmount(rs.getBigDecimal("fixed_amount"));
							brokers.setPercentageValue(rs.getBigDecimal("percentage_value"));
							return brokers;
						}
					});
			return brokers;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Brokers> getBrokersFromMember(Integer feeID, Integer fromMemberID) {
		try {
			List<Brokers> brokers = this.jdbcTemplate.query(
					"select * from brokers where fee_id = ? and from_member_id = ?  and enabled = true",
					new Object[] { feeID, fromMemberID }, new RowMapper<Brokers>() {
						public Brokers mapRow(ResultSet rs, int rowNum) throws SQLException {
							Brokers brokers = new Brokers();
							brokers.setId(rs.getInt("id"));
							brokers.setFeeID(rs.getInt("fee_id"));
							brokers.setFromMemberID(rs.getInt("from_member_id"));
							brokers.setToMemberID(rs.getInt("to_member_id"));
							brokers.setFromAccountID(rs.getInt("from_account_id"));
							brokers.setToAccountID(rs.getInt("to_account_id"));
							brokers.setName(rs.getString("name"));
							brokers.setDescription(rs.getString("description"));
							brokers.setFixedAmount(rs.getBigDecimal("fixed_amount"));
							brokers.setPercentageValue(rs.getBigDecimal("percentage_value"));
							return brokers;
						}
					});
			return brokers;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Brokers> getBrokersToMember(Integer feeID, Integer toMemberID) {
		try {
			List<Brokers> brokers = this.jdbcTemplate.query(
					"select * from brokers where fee_id = ? and to_member_id = ?  and enabled = true",
					new Object[] { feeID, toMemberID }, new RowMapper<Brokers>() {
						public Brokers mapRow(ResultSet rs, int rowNum) throws SQLException {
							Brokers brokers = new Brokers();
							brokers.setId(rs.getInt("id"));
							brokers.setFeeID(rs.getInt("fee_id"));
							brokers.setFromMemberID(rs.getInt("from_member_id"));
							brokers.setToMemberID(rs.getInt("to_member_id"));
							brokers.setFromAccountID(rs.getInt("from_account_id"));
							brokers.setToAccountID(rs.getInt("to_account_id"));
							brokers.setName(rs.getString("name"));
							brokers.setDescription(rs.getString("description"));
							brokers.setFixedAmount(rs.getBigDecimal("fixed_amount"));
							brokers.setPercentageValue(rs.getBigDecimal("percentage_value"));
							return brokers;
						}
					});
			return brokers;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Brokers> loadAllBrokers() {
		try {
			List<Brokers> brokers = this.jdbcTemplate.query("select * from brokers;", new RowMapper<Brokers>() {
				public Brokers mapRow(ResultSet rs, int rowNum) throws SQLException {
					Brokers brokers = new Brokers();
					brokers.setId(rs.getInt("id"));
					brokers.setFeeID(rs.getInt("fee_id"));
					brokers.setFromMemberID(rs.getInt("from_member_id"));
					brokers.setToMemberID(rs.getInt("to_member_id"));
					brokers.setFromAccountID(rs.getInt("from_account_id"));
					brokers.setToAccountID(rs.getInt("to_account_id"));
					brokers.setName(rs.getString("name"));
					brokers.setDescription(rs.getString("description"));
					brokers.setFixedAmount(rs.getBigDecimal("fixed_amount"));
					brokers.setPercentageValue(rs.getBigDecimal("percentage_value"));
					return brokers;
				}
			});
			return brokers;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void createBrokers(BrokerRequest req, Integer fromMemberID, Integer toMemberID) {
		jdbcTemplate.update(
				"insert into brokers (fee_id, from_member_id, from_account_id, to_member_id, to_account_id, name, description, enabled, fixed_amount, percentage_value) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				req.getFeeID(), fromMemberID, req.getFromAccountID(), toMemberID, req.getToAccountID(), req.getName(),
				req.getDescription(), req.isEnabled(), req.getFixedAmount(), req.getPercentage());
	}

	public void updateBrokers(BrokerRequest req, Integer fromMemberID, Integer toMemberID) {
		jdbcTemplate.update(
				"update brokers set fee_id = ?, from_member_id = ?, from_account_id = ?, to_member_id = ?, to_account_id = ?, name = ?, description = ?, enabled = ?, fixed_amount = ?, percentage_value = ? where id = ?",
				req.getFeeID(), fromMemberID, req.getFromAccountID(), toMemberID, req.getToAccountID(), req.getName(),
				req.getDescription(), req.isEnabled(), req.getFixedAmount(), req.getPercentage(), req.getId());
	}

	public void deleteBrokers(BrokerRequest req) {
		jdbcTemplate.update("delete from brokers where id = ?", req.getId());
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
