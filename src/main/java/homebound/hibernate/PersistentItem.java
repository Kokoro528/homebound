package homebound.hibernate;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "items")
public class PersistentItem {
	
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private String imageUrl;
	private String url;
	private double distance;
	
	private Set<PersistentCategories> categories;

	@Id
	@Column(name= "item_id")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@Column(name= "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name= "rating")
	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Column(name= "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name= "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name= "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name= "distance")
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@OneToMany(targetEntity = PersistentCategories.class ,cascade = CascadeType.ALL, mappedBy = "persistentItem", fetch=FetchType.EAGER)
	public Set<PersistentCategories> getCategories() {
		return categories;
	}

	public void setCategories(Set<PersistentCategories> categories) {
		this.categories = categories;
	}
}

