package homebound.hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class PersistentCategories {

	@ManyToOne
	@JoinColumn(name = "item_id",insertable=false, updatable=false)
	private PersistentItem persistentItem;
	
	@Column(name = "category",insertable=false, updatable=false)
	private String category;
	
	@EmbeddedId
	public CategoryId categoryId;

	public CategoryId getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(CategoryId categoryId) {
		this.categoryId = categoryId;
	}

	public PersistentItem getPersistentItem() {
		return persistentItem;
	}

	public void setPersistentItem(PersistentItem persistentItem) {
		this.persistentItem = persistentItem;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}

