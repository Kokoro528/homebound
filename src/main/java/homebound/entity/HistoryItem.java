package homebound.entity;
import java.util.List;
public class HistoryItem {
	private String user_id;
	private List<String> favorite;
	
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public List<String> getFavorite() {
		return favorite;
	}
	public void setFavorite(List<String> favorite) {
		this.favorite = favorite;
	}
	

}
