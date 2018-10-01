package homebound.hibernate;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "history")
public class PersistentHistory {
	@EmbeddedId
	private HistoryId historyId;
	
	@ManyToOne
	@JoinColumn(name = "item_id",insertable=false, updatable=false)
	private PersistentItem itemId;
	
	@ManyToOne
	@JoinColumn(name = "user_id",insertable=false, updatable=false)
	private PersistentUser userId;
	
	@Column(name = "last_favor_time")
	private Timestamp lastFavorTime;
	
	public HistoryId getHistoryId() {
		return historyId;
	}
	public void setHistoryId(HistoryId historyId) {
		this.historyId = historyId;
	}
	
	public PersistentItem getItemId() {
		return itemId;
	}
	public void setItemId(PersistentItem itemId) {
		this.itemId = itemId;
	}
	public PersistentUser getUserId() {
		return userId;
	}
	public void setUserId(PersistentUser userId) {
		this.userId = userId;
	}
	public Timestamp getLastFavorTime() {
		return lastFavorTime;
	}
	public void setLastFavorTime(Timestamp lastFavorTime) {
		this.lastFavorTime = lastFavorTime;
	}

}
