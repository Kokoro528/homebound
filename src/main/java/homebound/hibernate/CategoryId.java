package homebound.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CategoryId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2538746252354816715L;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "category")
	private String category;

	public CategoryId() {

	}

	public CategoryId(String itemId, String category) {
		this.itemId = itemId;
		this.category = category;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CategoryId that = (CategoryId) o;

		if (!itemId.equals(that.itemId))
			return false;
		return category.equals(that.category);
	}

	@Override
	public int hashCode() {
		int result = itemId.hashCode();
		result = 31 * result + category.hashCode();
		return result;
	}

}
