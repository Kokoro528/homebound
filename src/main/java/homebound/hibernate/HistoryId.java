package homebound.hibernate;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@Embeddable
public class HistoryId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2895183747894955106L;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "user_id")
	private String userId;

	public HistoryId() {
	}

	public HistoryId(String itemId, String userId) {
		this.userId = userId;
		this.itemId = itemId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		HistoryId that = (HistoryId) o;

		if (!itemId.equals(that.itemId))
			return false;
		return userId.equals(that.userId);
	}

	@Override
	public int hashCode() {
		int result = itemId.hashCode();
		result = 31 * result + userId.hashCode();
		return result;
	}

}
