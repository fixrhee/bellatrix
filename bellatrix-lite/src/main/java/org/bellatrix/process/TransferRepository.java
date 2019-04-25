package org.bellatrix.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.bellatrix.data.Accounts;
import org.bellatrix.data.BrokeringResult;
import org.bellatrix.data.Brokers;
import org.bellatrix.data.Fees;
import org.bellatrix.data.Members;
import org.bellatrix.data.PaymentCustomFields;
import org.bellatrix.data.PaymentFields;
import org.bellatrix.data.TransferTypes;
import org.bellatrix.data.Transfers;
import org.bellatrix.services.CreatePaymentCustomFieldsRequest;
import org.bellatrix.services.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import com.mysql.jdbc.Statement;

@Component
@Repository
public class TransferRepository {

	private JdbcTemplate jdbcTemplate;
	private Logger logger = Logger.getLogger(TransferRepository.class);

	public List<Transfers> getTransfersFromField(String fieldName, Object fieldID) {
		try {
			List<Transfers> transfers = this.jdbcTemplate.query("select * from transfers where " + fieldName + " = ?",
					new Object[] { fieldID }, new RowMapper<Transfers>() {
						public Transfers mapRow(ResultSet rs, int rowNum) throws SQLException {
							Transfers transfers = new Transfers();
							transfers.setAmount(rs.getBigDecimal("amount"));
							transfers.setChargedBack(rs.getBoolean("charged_back"));
							transfers.setCustomField(rs.getBoolean("custom_field"));
							transfers.setDescription(rs.getString("description"));
							transfers.setFromAccountID(rs.getInt("from_account_id"));
							transfers.setFromMemberID(rs.getInt("from_member_id"));
							transfers.setId(rs.getInt("id"));
							transfers.setModifiedDate(rs.getTimestamp("modified_date"));
							transfers.setParentID(rs.getString("parent_id"));
							transfers.setToAccountID(rs.getInt("to_account_id"));
							transfers.setToMemberID(rs.getInt("to_member_id"));
							transfers.setTraceNumber(rs.getString("trace_number"));
							transfers.setTransactionDate(rs.getTimestamp("transaction_date"));
							transfers.setTransactionNumber(rs.getString("transaction_number"));
							transfers.setTransactionState(rs.getString("transaction_state"));
							transfers.setTransferTypeID(rs.getInt("transfer_type_id"));
							return transfers;
						}
					});
			return transfers;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Integer createTransfers(PaymentRequest paymentReq, BigDecimal amount, TransferTypes trxType,
			Accounts fromAccount, Accounts toAccount, Members from, Members to, String trxNo, String parentID,
			String trxState, Integer webServiceID) {
		final PaymentRequest req = paymentReq;

		boolean useCustomField = req.getPaymentFields() != null ? true : false;

		GeneratedKeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement statement = con.prepareStatement(
						"insert into transfers (transfer_type_id, from_account_id, to_account_id, from_member_id, to_member_id, trace_number, transaction_number, parent_id, transaction_state, description, amount, custom_field, reference_number, originator) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				statement.setInt(1, trxType.getId());
				statement.setInt(2, fromAccount.getId());
				statement.setInt(3, toAccount.getId());
				statement.setInt(4, from.getId());
				statement.setInt(5, to.getId());
				statement.setString(6, webServiceID + req.getTraceNumber());
				statement.setString(7, trxNo);
				statement.setString(8, parentID);
				statement.setString(9, trxState);
				statement.setString(10, req.getDescription());
				statement.setBigDecimal(11, amount);
				statement.setBoolean(12, useCustomField);
				statement.setString(13, req.getReferenceNumber());
				statement.setString(14, req.getOriginator());
				return statement;
			}
		}, holder);

		Integer primaryKey = holder.getKey().intValue();

		if (useCustomField) {

			jdbcTemplate.batchUpdate(
					"insert into payment_custom_field_values (transfer_id, payment_custom_field_id, value) values (?, ?, ?)",
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setLong(1, primaryKey);
							ps.setLong(2, req.getPaymentFields().get(i).getPaymentCustomFieldID());
							ps.setString(3, req.getPaymentFields().get(i).getValue());
						}

						@Override
						public int getBatchSize() {
							return req.getPaymentFields().size();
						}
					});
		}

		return primaryKey;

	}

	public void insertFees(PaymentRequest req, List<Fees> lfees, TransferTypes type, String parent,
			String transactionState, Integer webServiceID) {
		final TransferTypes trxType = type;
		final String parentID = parent;
		final List<Fees> fees = lfees;
		final String trxState = transactionState;
		final Integer wsID = webServiceID;

		jdbcTemplate.batchUpdate(
				"insert into transfers (transfer_type_id, from_account_id, to_account_id, from_member_id, to_member_id, trace_number, transaction_number, parent_id, transaction_state, description, amount) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, trxType.getId());
						ps.setLong(2, fees.get(i).getFromAccountID());
						ps.setLong(3, fees.get(i).getToAccountID());
						ps.setLong(4, fees.get(i).getFromMemberID());
						ps.setLong(5, fees.get(i).getToMemberID());
						ps.setString(6, wsID + Utils.GenerateRandomNumber() + fees.get(i).getId());
						ps.setString(7, fees.get(i).getTransactionNumber());
						ps.setString(8, parentID);
						ps.setString(9, trxState);
						ps.setString(10, fees.get(i).getName());
						ps.setBigDecimal(11, fees.get(i).getFeeAmount());
					}

					@Override
					public int getBatchSize() {
						return fees.size();
					}
				});
	}

	public void insertBrokers(List<Brokers> lbrokers, TransferTypes type, String transactionState,
			Integer webServiceID) {
		final TransferTypes trxType = type;
		final List<Brokers> brokers = lbrokers;
		final String trxState = transactionState;
		final Integer wsID = webServiceID;

		jdbcTemplate.batchUpdate(
				"insert into transfers (transfer_type_id, from_account_id, to_account_id, from_member_id, to_member_id, trace_number, transaction_number, parent_id, transaction_state, description, amount, reference_number) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, trxType.getId());
						ps.setLong(2, brokers.get(i).getFromAccountID());
						ps.setLong(3, brokers.get(i).getToAccountID());
						ps.setLong(4, brokers.get(i).getFromMemberID());
						ps.setLong(5, brokers.get(i).getToMemberID());
						ps.setString(6, wsID + Utils.GenerateRandomNumber() + brokers.get(i).getId());
						ps.setString(7, brokers.get(i).getTransactionNumber());
						ps.setString(8, brokers.get(i).getFeeTransactionNumber());
						ps.setString(9, trxState);
						ps.setString(10, brokers.get(i).getName());
						ps.setBigDecimal(11, brokers.get(i).getFeeAmount());
						ps.setString(12, brokers.get(i).getRequestTransactionAmount());
					}

					@Override
					public int getBatchSize() {
						return brokers.size();
					}
				});
	}

	public void reverseTransfers(List<Transfers> transfers) {

		jdbcTemplate.batchUpdate(
				"insert into transfers (transfer_type_id, from_account_id, to_account_id, from_member_id, to_member_id, trace_number, transaction_number, parent_id, transaction_state, description, amount) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, transfers.get(i).getTransferTypeID());
						ps.setLong(2, transfers.get(i).getToAccountID());
						ps.setLong(3, transfers.get(i).getFromAccountID());
						ps.setLong(4, transfers.get(i).getToMemberID());
						ps.setLong(5, transfers.get(i).getFromMemberID());
						ps.setString(6,
								transfers.get(i).getTraceNumber().substring(0, 1) + Utils.GenerateRandomNumber());
						ps.setString(7, transfers.get(i).getReversedTransactionNumber());
						ps.setString(8, transfers.get(i).getTransactionNumber());
						ps.setString(9, "PROCESSED");
						ps.setString(10,
								"Reversed from transaction number : " + transfers.get(i).getTransactionNumber());
						ps.setBigDecimal(11, transfers.get(i).getAmount());
					}

					@Override
					public int getBatchSize() {
						return transfers.size();
					}
				});
	}

	public void reverseTransaction(String trxNo) {
		this.jdbcTemplate.update(
				"update transfers set charged_back = true, transaction_state = 'REVERSED' where transaction_number = ? or parent_id = ? or reference_number = ?",
				new Object[] { trxNo, trxNo, trxNo });

	}

	public boolean validateTransfersByTraceNumber(Integer wsID, String traceNumber) {
		String traceNo = String.valueOf(wsID) + traceNumber;
		try {
			Integer id = this.jdbcTemplate.queryForObject("select id from transfers where trace_number = ?",
					new Object[] { traceNo }, Integer.class);
			logger.info("[EXIST TraceNumber " + traceNo + " with ID : " + id + "]");
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public void deletePendingTransfers(String transactionNumber) {
		this.jdbcTemplate.update(
				"delete from transfers where transaction_number = ? or parent_id = ? or reference_number = ? and transaction_state = 'PENDING'",
				new Object[] { transactionNumber, transactionNumber, transactionNumber });
	}

	public void confirmPendingTransfers(String transactionNumber, String traceNumber, Integer wsID) {
		String trNo = String.valueOf(wsID) + traceNumber;
		this.jdbcTemplate.update(
				"update  transfers set transaction_state = 'PROCESSED' where transaction_number = ? and trace_number = ? and transaction_state = 'PENDING'",
				new Object[] { transactionNumber, trNo });
	}

	public void confirmPendingTransfers(Integer id, String refNo) {
		this.jdbcTemplate.update(
				"update  transfers set transaction_state = 'PROCESSED', reference_number = ? where id = ? and transaction_state = 'PENDING'",
				new Object[] { refNo, id });
	}

	public void confirmPendingTransfers(Integer id, String parentID, String refNo) {
		this.jdbcTemplate.update(
				"update  transfers set transaction_state = 'PROCESSED', reference_number = ? where id = ? or parent_id = ? and transaction_state = 'PENDING'",
				new Object[] { refNo, id, parentID });
	}

	public void confirmPendingTransfers(String transactionNumber, String traceNumber, Integer wsID, String refNo) {
		String trNo = String.valueOf(wsID) + traceNumber;
		this.jdbcTemplate.update(
				"update  transfers set transaction_state = 'PROCESSED', reference_number = ? where transaction_number = ? and trace_number = ? and transaction_state = 'PENDING'",
				new Object[] { refNo, transactionNumber, trNo });
	}

	public List<Transfers> loadPendingTransfersFromMemberID(Integer memberID, Integer accountID, Integer currentPage,
			Integer pageSize) {
		try {
			List<Transfers> transfers = this.jdbcTemplate.query(
					"select * from transfers where transaction_state = 'PENDING' and from_member_id = ? and from_account_id = ? limit ?,? desc",
					new Object[] { memberID, accountID, currentPage, pageSize }, new RowMapper<Transfers>() {
						public Transfers mapRow(ResultSet rs, int rowNum) throws SQLException {
							Transfers transfers = new Transfers();
							transfers.setAmount(rs.getBigDecimal("amount"));
							transfers.setChargedBack(rs.getBoolean("charged_back"));
							transfers.setCustomField(rs.getBoolean("custom_field"));
							transfers.setDescription(rs.getString("description"));
							transfers.setFromAccountID(rs.getInt("from_account_id"));
							transfers.setFromMemberID(rs.getInt("from_member_id"));
							transfers.setId(rs.getInt("id"));
							transfers.setModifiedDate(rs.getTimestamp("modified_date"));
							transfers.setParentID(rs.getString("parent_id"));
							transfers.setToAccountID(rs.getInt("to_account_id"));
							transfers.setToMemberID(rs.getInt("to_member_id"));
							transfers.setTraceNumber(rs.getString("trace_number"));
							transfers.setTransactionDate(rs.getTimestamp("transaction_date"));
							transfers.setTransactionNumber(rs.getString("transaction_number"));
							transfers.setTransactionState(rs.getString("transaction_state"));
							transfers.setTransferTypeID(rs.getInt("transfer_type_id"));
							return transfers;
						}
					});
			return transfers;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Integer selectSourceMemberFromID(Integer transactionID) {
		try {
			return this.jdbcTemplate.queryForObject("select from_member_id from transfers where id = ? ", Integer.class,
					transactionID);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	public void createPaymentCustomField(CreatePaymentCustomFieldsRequest req) {
		this.jdbcTemplate.update("insert into payment_custom_fields (account_id, internal_name, name) values (?, ?, ?)",
				req.getAccountID(), req.getInternalName(), req.getName());
	}

	public List<PaymentCustomFields> loadPaymentCustomFieldByTransferType(Integer req) {
		List<PaymentCustomFields> paymentfield = this.jdbcTemplate.query(
				"select * from payment_custom_fields where transfer_type_id = ?", new Object[] { req },
				new RowMapper<PaymentCustomFields>() {
					public PaymentCustomFields mapRow(ResultSet rs, int rowNum) throws SQLException {
						PaymentCustomFields paymentfield = new PaymentCustomFields();
						paymentfield.setId(rs.getInt("id"));
						paymentfield.setTransferTypeID(rs.getInt("transfer_type_id"));
						paymentfield.setInternalName(rs.getString("internal_name"));
						paymentfield.setName(rs.getString("name"));
						paymentfield.setCreatedDate(rs.getDate("created_date"));
						return paymentfield;
					}
				});
		return paymentfield;
	}

	public PaymentCustomFields loadPaymentCustomFieldByID(Integer req) {
		PaymentCustomFields paymentfield = this.jdbcTemplate.queryForObject(
				"select * from payment_custom_fields where id = ?", new Object[] { req },
				new RowMapper<PaymentCustomFields>() {
					public PaymentCustomFields mapRow(ResultSet rs, int rowNum) throws SQLException {
						PaymentCustomFields paymentfield = new PaymentCustomFields();
						paymentfield.setId(rs.getInt("id"));
						paymentfield.setTransferTypeID(rs.getInt("transfer_type_id"));
						paymentfield.setInternalName(rs.getString("internal_name"));
						paymentfield.setName(rs.getString("name"));
						paymentfield.setCreatedDate(rs.getDate("created_date"));
						return paymentfield;
					}
				});
		return paymentfield;
	}

	public List<PaymentFields> loadPaymentFieldValuesByTransferID(Integer req) {
		List<PaymentFields> paymentfield = this.jdbcTemplate.query(
				"select * from payment_custom_field_values where transfer_id = ?", new Object[] { req },
				new RowMapper<PaymentFields>() {
					public PaymentFields mapRow(ResultSet rs, int rowNum) throws SQLException {
						PaymentFields paymentfield = new PaymentFields();
						paymentfield.setId(rs.getInt("id"));
						paymentfield.setTransferID(rs.getInt("transfer_id"));
						paymentfield.setPaymentCustomFieldID(rs.getInt("payment_custom_field_id"));
						paymentfield.setValue(rs.getString("value"));
						return paymentfield;
					}
				});
		return paymentfield;
	}

	public List<PaymentFields> loadMultiPaymentFieldValuesByTransferID(List<Integer> req) {
		String sql = "select * from payment_custom_field_values where transfer_id in (:transferID)";
		Map<String, List<Integer>> paramMap = Collections.singletonMap("transferID", req);
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
		List<PaymentFields> paymentfield = template.query(sql, paramMap, new RowMapper<PaymentFields>() {
			public PaymentFields mapRow(ResultSet rs, int rowNum) throws SQLException {
				PaymentFields paymentfield = new PaymentFields();
				paymentfield.setId(rs.getInt("id"));
				paymentfield.setTransferID(rs.getInt("transfer_id"));
				paymentfield.setPaymentCustomFieldID(rs.getInt("payment_custom_field_id"));
				paymentfield.setValue(rs.getString("value"));
				return paymentfield;
			}
		});
		return paymentfield;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}