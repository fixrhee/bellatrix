package org.bellatrix.process;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import org.bellatrix.data.Brokers;
import org.bellatrix.data.FeeByMember;
import org.bellatrix.data.Fees;
import org.bellatrix.data.Groups;
import org.bellatrix.data.TransferNotifications;
import org.bellatrix.data.TransferTypePermission;
import org.bellatrix.data.TransferTypes;
import org.bellatrix.services.BrokerRequest;
import org.bellatrix.services.FeeRequest;
import org.bellatrix.services.TransferTypeNotificationRequest;
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
			TransferTypes transferType = this.jdbcTemplate.queryForObject(
					"select b.id, b.from_account_id,  b.to_account_id, a.name as fromaccount, (select c.name from accounts c where id = b.to_account_id) as toaccount, b.name, b.description, b.min_amount, b.max_amount, b.max_count, b.otp_threshold from transfer_types b inner join accounts a on b.from_account_id = a.id where b.id = ?",
					new Object[] { id }, new RowMapper<TransferTypes>() {
						public TransferTypes mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypes transferType = new TransferTypes();
							transferType.setId(rs.getInt("id"));
							transferType.setName(rs.getString("name"));
							transferType.setDescription(rs.getString("description"));
							transferType.setFromAccounts(rs.getInt("from_account_id"));
							transferType.setFromAccountName(rs.getString("fromaccount"));
							transferType.setToAccounts(rs.getInt("to_account_id"));
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

	public List<TransferTypes> loadAllTransferType(Integer currentPage, Integer pageSize) {
		try {
			List<TransferTypes> transferType = this.jdbcTemplate.query(
					"select b.id, b.from_account_id,  b.to_account_id, a.name as fromaccount, (select c.name from accounts c where id = b.to_account_id) as toaccount, b.name, b.description, b.min_amount, b.max_amount, b.max_count, b.otp_threshold from transfer_types b inner join accounts a on b.from_account_id = a.id  order by id DESC LIMIT ?,?",
					new Object[] { currentPage, pageSize }, new RowMapper<TransferTypes>() {
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

	public List<TransferTypePermission> loadTransferTypePermissionsByID(Integer id) {
		try {
			List<TransferTypePermission> permission = this.jdbcTemplate.query(
					"select t.id as permission_id, g.id, g.name from transfer_type_permissions t inner join groups g on t.`group_id` = g.`id` where t.`transfer_type_id`= ?;",
					new Object[] { id }, new RowMapper<TransferTypePermission>() {
						public TransferTypePermission mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypePermission permission = new TransferTypePermission();
							Groups group = new Groups();
							group.setId(rs.getInt("id"));
							group.setName(rs.getString("name"));
							permission.setGroup(group);
							permission.setPermissionID(rs.getInt("permission_id"));
							return permission;
						}
					});
			return permission;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<TransferTypePermission> loadTransferTypePermissionsByGroup(Integer groupID, Integer transferTypeID) {
		try {
			List<TransferTypePermission> permission = this.jdbcTemplate.query(
					"select t.id as permission_id, g.id, g.name from transfer_type_permissions t inner join groups g on t.`group_id` = g.`id` where t.`transfer_type_id`= ? and t.group_id = ?;",
					new Object[] { transferTypeID, groupID }, new RowMapper<TransferTypePermission>() {
						public TransferTypePermission mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferTypePermission permission = new TransferTypePermission();
							Groups group = new Groups();
							group.setId(rs.getInt("id"));
							group.setName(rs.getString("name"));
							permission.setGroup(group);
							permission.setPermissionID(rs.getInt("permission_id"));
							return permission;
						}
					});
			return permission;
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

	public Integer countTotalTransferTypes() {
		int count = this.jdbcTemplate.queryForObject("select  count(id) as total from transfer_types", Integer.class);
		return count;
	}

	public Integer countMaxUsageTransferTypes(Integer id, Integer memberID) {
		int count = this.jdbcTemplate.queryForObject(
				"select count(id) from transfers where from_member_id = ? and transfer_type_id = ? and transaction_date >= CURDATE() AND transaction_date < CURDATE() + INTERVAL 1 DAY;",
				Integer.class, new Object[] { memberID, id });
		return count;
	}

	public Fees getFeeFromID(Integer id) {
		try {
			Fees fees = this.jdbcTemplate.queryForObject("select * from fees where id = ?", new Object[] { id },
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
							fees.setPriority(rs.getBoolean("priority"));
							fees.setPercentageValue(rs.getBigDecimal("percentage_value"));
							fees.setInitialRangeAmount(rs.getBigDecimal("initial_range_amount"));
							fees.setMaximumRangeAmount(rs.getBigDecimal("maximum_range_amount"));
							fees.setFromAllMember(rs.getBoolean("from_all_member"));
							fees.setToAllMember(rs.getBoolean("to_all_member"));
							fees.setStartDate(rs.getDate("start_date"));
							fees.setEndDate(rs.getDate("end_date"));
							fees.setEnabled(rs.getBoolean("enabled"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
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
							fees.setPriority(rs.getBoolean("priority"));
							fees.setPercentageValue(rs.getBigDecimal("percentage_value"));
							fees.setInitialRangeAmount(rs.getBigDecimal("initial_range_amount"));
							fees.setMaximumRangeAmount(rs.getBigDecimal("maximum_range_amount"));
							fees.setFromAllMember(rs.getBoolean("from_all_member"));
							fees.setToAllMember(rs.getBoolean("to_all_member"));
							fees.setStartDate(rs.getDate("start_date"));
							fees.setEndDate(rs.getDate("end_date"));
							fees.setEnabled(rs.getBoolean("enabled"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Fees> getAllFeeFromTransferTypeID(Integer transferTypeID) {
		try {
			List<Fees> fees = this.jdbcTemplate.query("select * from fees where transfer_type_id = ?",
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
							fees.setEnabled(rs.getBoolean("enabled"));
							fees.setFixedAmount(rs.getBigDecimal("fixed_amount"));
							fees.setPriority(rs.getBoolean("priority"));
							fees.setPercentageValue(rs.getBigDecimal("percentage_value"));
							fees.setInitialRangeAmount(rs.getBigDecimal("initial_range_amount"));
							fees.setMaximumRangeAmount(rs.getBigDecimal("maximum_range_amount"));
							fees.setFromAllMember(rs.getBoolean("from_all_member"));
							fees.setToAllMember(rs.getBoolean("to_all_member"));
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
							fees.setFromAllMember(rs.getBoolean("from_all_member"));
							fees.setToAllMember(rs.getBoolean("to_all_member"));
							fees.setStartDate(rs.getDate("start_date"));
							fees.setEndDate(rs.getDate("end_date"));
							fees.setEnabled(rs.getBoolean("enabled"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public boolean validateFeeByMember(Integer feeID, Integer memberID, boolean target) {
		try {
			this.jdbcTemplate.queryForObject(
					"select id from fee_by_members where fee_id = ? and member_id = ? and destination = ?",
					Integer.class, feeID, memberID, target);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public List<FeeByMember> loadFeeByMember(Integer feeID) {
		try {
			List<FeeByMember> fees = this.jdbcTemplate.query(
					"select a.id, a.fee_id, a.member_id, a.destination, b.name  from fee_by_members a inner join members b on a.member_id = b.id where a.fee_id = ?",
					new Object[] { feeID }, new RowMapper<FeeByMember>() {
						public FeeByMember mapRow(ResultSet rs, int rowNum) throws SQLException {
							FeeByMember fees = new FeeByMember();
							fees.setDestination(rs.getBoolean("destination"));
							fees.setFeeID(rs.getInt("fee_id"));
							fees.setId(rs.getInt("id"));
							fees.setName(rs.getString("name"));
							fees.setMemberID(rs.getInt("member_id"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<FeeByMember> loadFeeByMemberByID(Integer id) {
		try {
			List<FeeByMember> fees = this.jdbcTemplate.query(
					"select a.id, a.fee_id, a.member_id, a.destination, b.name from fee_by_members a inner join members b on a.member_id = b.id where a.id = ?",
					new Object[] { id }, new RowMapper<FeeByMember>() {
						public FeeByMember mapRow(ResultSet rs, int rowNum) throws SQLException {
							FeeByMember fees = new FeeByMember();
							fees.setDestination(rs.getBoolean("destination"));
							fees.setFeeID(rs.getInt("fee_id"));
							fees.setId(rs.getInt("id"));
							fees.setName(rs.getString("name"));
							fees.setMemberID(rs.getInt("member_id"));
							return fees;
						}
					});
			return fees;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void createFeeByMember(Integer feeID, Integer memberID, boolean destination) {
		jdbcTemplate.update("insert into fee_by_members (fee_id, member_id, destination) values (?, ?, ?)", feeID,
				memberID, destination);
	}

	public void deleteFeeByMember(Integer id) {
		jdbcTemplate.update("delete from fee_by_members where id = ?", id);
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
				"insert into transfer_types (from_account_id, to_account_id, name, description, min_amount, max_amount, max_count) values (?, ?, ?, ?, ?, ?, ?)",
				fromAccountID, toAccountID, name, description, min, max, maxCount);
	}

	public void updateTransferType(Integer id, Integer fromAccountID, Integer toAccountID, String name,
			String description, BigDecimal min, BigDecimal max, Integer maxCount, BigDecimal otp) {
		jdbcTemplate.update(
				"update transfer_types set from_account_id = ?, to_account_id = ?, name = ?, description = ?, min_amount = ?, max_amount = ?, max_count = ? where id = ?",
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

	public void deleteTransferTypePermission(Integer id) {
		jdbcTemplate.update("delete from transfer_type_permissions where id = ?", id);
	}

	public void createFee(FeeRequest req) {
		jdbcTemplate.update(
				"insert into fees (transfer_type_id, from_member_id, from_account_id, to_member_id, to_account_id, name, description, enabled, deduct_amount, from_all_member, to_all_member, fixed_amount, percentage_value, initial_range_amount, maximum_range_amount, start_date, end_date, priority) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				req.getTransferTypeID(), req.getFromMemberID(), req.getFromAccountID(), req.getToMemberID(),
				req.getToAccountID(), req.getName(), req.getDescription(), req.isEnabled(), req.isDeductAmount(),
				req.isFromAllMember(), req.isToAllMember(), req.getFixedAmount(), req.getPercentage(),
				req.getInitialRangeAmount(), req.getMaxRangeAmount(), req.getStartDate(), req.getEndDate(),
				req.isPriority());
	}

	public void updateFee(FeeRequest req) {
		jdbcTemplate.update(
				"update fees set from_member_id = ?, from_account_id = ?, to_member_id = ?, to_account_id = ?, name = ?, description = ?, enabled = ?, deduct_amount = ?, from_all_member = ?, to_all_member = ?, fixed_amount = ?, percentage_value = ?, initial_range_amount = ?, maximum_range_amount = ?, start_date = ? , end_date = ? , priority = ? where transfer_type_id = ?",
				req.getFromMemberID(), req.getFromAccountID(), req.getToMemberID(), req.getToAccountID(), req.getName(),
				req.getDescription(), req.isEnabled(), req.isDeductAmount(), req.isFromAllMember(), req.isToAllMember(),
				req.getFixedAmount(), req.getPercentage(), req.getInitialRangeAmount(), req.getMaxRangeAmount(),
				req.getStartDate(), req.getEndDate(), req.isPriority(), req.getTransferTypeID());
	}

	public void deleteFee(Integer transferTypeID) {
		jdbcTemplate.update("delete from fees where transfer_type_id = ?", transferTypeID);
	}

	public List<Brokers> getBrokersByID(Integer id) {
		try {
			List<Brokers> brokers = this.jdbcTemplate.query("select * from brokers where id = ?", new Object[] { id },
					new RowMapper<Brokers>() {
						public Brokers mapRow(ResultSet rs, int rowNum) throws SQLException {
							Brokers brokers = new Brokers();
							brokers.setId(rs.getInt("id"));
							brokers.setFeeID(rs.getInt("fee_id"));
							brokers.setFromMemberID(rs.getInt("from_member_id"));
							brokers.setToMemberID(rs.getInt("to_member_id"));
							brokers.setFromAccountID(rs.getInt("from_account_id"));
							brokers.setToAccountID(rs.getInt("to_account_id"));
							brokers.setName(rs.getString("name"));
							brokers.setEnabled(rs.getBoolean("enabled"));
							brokers.setDeductAllFee(rs.getBoolean("deductAllFee"));
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

	public List<Brokers> getBrokersFromFee(Integer feeID) {
		try {
			List<Brokers> brokers = this.jdbcTemplate.query("select * from brokers where fee_id = ?",
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
							brokers.setEnabled(rs.getBoolean("enabled"));
							brokers.setDeductAllFee(rs.getBoolean("deductAllFee"));
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
					"select * from brokers where fee_id = ? and from_member_id = ?",
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
							brokers.setEnabled(rs.getBoolean("enabled"));
							brokers.setDeductAllFee(rs.getBoolean("deductAllFee"));
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
					"select * from brokers where fee_id = ? and to_member_id = ?",
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
							brokers.setEnabled(rs.getBoolean("enabled"));
							brokers.setDeductAllFee(rs.getBoolean("deductAllFee"));
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
					brokers.setDeductAllFee(rs.getBoolean("deductAllFee"));
					brokers.setDescription(rs.getString("description"));
					brokers.setFixedAmount(rs.getBigDecimal("fixed_amount"));
					brokers.setEnabled(rs.getBoolean("enabled"));
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
				"insert into brokers (fee_id, from_member_id, from_account_id, to_member_id, to_account_id, name, description, enabled, fixed_amount, percentage_value, deductAllFee) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				req.getFeeID(), fromMemberID, req.getFromAccountID(), toMemberID, req.getToAccountID(), req.getName(),
				req.getDescription(), req.isEnabled(), req.getFixedAmount(), req.getPercentage(), req.isDeductAllFee());
	}

	public void updateBrokers(BrokerRequest req, Integer fromMemberID, Integer toMemberID) {
		jdbcTemplate.update(
				"update brokers set fee_id = ?, from_member_id = ?, from_account_id = ?, to_member_id = ?, to_account_id = ?, name = ?, description = ?, enabled = ?, fixed_amount = ?, percentage_value = ?, deductAllFee = ? where id = ?",
				req.getFeeID(), fromMemberID, req.getFromAccountID(), toMemberID, req.getToAccountID(), req.getName(),
				req.getDescription(), req.isEnabled(), req.getFixedAmount(), req.getPercentage(), req.isDeductAllFee(),
				req.getId());
	}

	public void deleteBrokers(BrokerRequest req) {
		jdbcTemplate.update("delete from brokers where id = ?", req.getId());
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<TransferNotifications> loadTransferNotificationByTransferType(Integer id) {
		try {
			List<TransferNotifications> notif = this.jdbcTemplate.query(
					"select t.id, t.transfer_type_id, t.notification_type, n.module_url, t.enabled, t.notification_id, n.name, n.module_url from transfer_notifications t inner join notifications n on t.notification_id = n.id where transfer_type_id = ?",
					new Object[] { id }, new RowMapper<TransferNotifications>() {
						public TransferNotifications mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferNotifications notif = new TransferNotifications();
							notif.setId(rs.getInt("id"));
							notif.setNotificationName(rs.getString("name"));
							notif.setEnabled(rs.getBoolean("enabled"));
							notif.setNotificationID(rs.getInt("notification_id"));
							notif.setNotificationType(rs.getString("notification_type"));
							notif.setDestinationModule(rs.getString("module_url"));
							notif.setTransferTypeID(rs.getInt("transfer_type_id"));
							return notif;
						}
					});
			return notif;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public TransferNotifications loadTransferNotificationByID(Integer id) {
		try {
			TransferNotifications notif = this.jdbcTemplate.queryForObject(
					"select t.id, t.transfer_type_id, t.notification_type, n.module_url, t.enabled, t.notification_id, n.name, n.module_url from transfer_notifications t inner join notifications n on t.notification_id = n.id where t.id = ?",
					new Object[] { id }, new RowMapper<TransferNotifications>() {
						public TransferNotifications mapRow(ResultSet rs, int rowNum) throws SQLException {
							TransferNotifications notif = new TransferNotifications();
							notif.setId(rs.getInt("id"));
							notif.setNotificationName(rs.getString("name"));
							notif.setEnabled(rs.getBoolean("enabled"));
							notif.setNotificationID(rs.getInt("notification_id"));
							notif.setNotificationType(rs.getString("notification_type"));
							notif.setDestinationModule(rs.getString("module_url"));
							notif.setTransferTypeID(rs.getInt("transfer_type_id"));
							return notif;
						}
					});
			return notif;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void createTransferNotification(TransferTypeNotificationRequest req) {
		jdbcTemplate.update(
				"insert into transfer_notifications (transfer_type_id, notification_id, notification_type, enabled) values (?, ?, ?, ?)",
				req.getTransferTypeID(), req.getNotificationID(), req.getNotificationType(), req.isEnabled());
	}

	public void updateTransferNotification(TransferTypeNotificationRequest req) {
		jdbcTemplate.update(
				"update transfer_notifications set transfer_type_id = ?, notification_id = ?, notification_type = ?, enabled = ? where id = ?",
				req.getTransferTypeID(), req.getNotificationID(), req.getNotificationType(), req.isEnabled(),
				req.getId());
	}

	public void deleteTransferNotification(Integer id) {
		jdbcTemplate.update("delete from transfer_notifications where id = ?", id);
	}
}
